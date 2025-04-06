package ru.sbertech.dataspace.mvc

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import org.openjdk.jol.info.GraphLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.sbertech.dataspace.entity.ContainerInfo
import ru.sbertech.dataspace.entity.ModelInfo
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.util.DsceConstants

@RestController
class ParentCtxController {
    @Autowired
    var modelMetaInfo: ModelMetaInfo? = null

    /**
     * Add JVM's  17 options: java  -Djdk.attach.allowAttachSelf=true -Djol.tryWithSudo=true --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util.function=ALL-UNNAMED -jar ./target/dataspace-app-DEV-SNAPSHOT.jar
     *            21 options: java -XX:+EnableDynamicAgentLoading -Djdk.attach.allowAttachSelf=true -Djol.tryWithSudo=true --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.function=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.security=ALL-UNNAMED --add-opens=java.base/jdk.internal.module=ALL-UNNAMED -jar ./target/dataspace-app-DEV-SNAPSHOT.jar
     * Compute beans or context size (Attention!!!  Long operation):
     *              http://localhost:8080/metainfo?beansize=true
     *              or
     *              http://localhost:8080/metainfo?contextsize=true
     */
    @GetMapping(value = ["/actuator/models"], produces = ["application/json"])
    fun metainfo(
        @RequestParam("beansize", defaultValue = "false") isBeanSizeEnabled: Boolean,
        @RequestParam("contextsize", defaultValue = "false") isContextSizeEnabled: Boolean,
        properties: AppProperties,
    ): String {
        val mapper = ObjectMapper()
        val rootNode: ObjectNode = mapper.createObjectNode()
        val modelsNode = rootNode.withObject("/models")

        val json: String
        try {
            modelMetaInfo!!.allModels.forEach { modelInfo ->
                val modelNode = modelsNode.withObject("/model-" + modelInfo.modelId)
                modelNode.put("modelId", modelInfo.modelId)
                modelNode.put("modelPath", modelInfo.modelPath)

                val endpointsNode = modelNode.withObject("/endpoints")
                if (properties.singleMode) {
                    endpointsNode.put("graphql", "/graphql")
                    endpointsNode.put("graphiql", "/graphiql?path=/graphql")
                } else {
                    endpointsNode.put(
                        "graphql",
                        DsceConstants.MODEL_GRAPHQL_PATH.replace(DsceConstants.MODEL_PATTERN, modelInfo.modelId),
                    )
                    endpointsNode.put(
                        "graphiql",
                        "/graphiql?path=${
                            DsceConstants.MODEL_GRAPHQL_PATH
                                .replace(DsceConstants.MODEL_PATTERN, modelInfo.modelId)
                        }",
                    )
                }
                val contextsNode = modelNode.withObject("/contexts")

                modelInfo.containersInfo!!.forEach { containerInfo ->
                    val contextNode: ObjectNode = contextsNode.withObject("/context-model-" + modelInfo.modelId)
                    if (containerInfo.isActive) {
                        gatherGeneralContextInfo(contextNode, modelInfo, containerInfo, "active")
                        val beansInfoNode: ObjectNode = contextNode.withObject("/context-active-beans-info")
                        beansInfoNode.put("beans-total", containerInfo.context.beanDefinitionCount.toString())

                        try {
                            computeSizeOfBeanOrContext(isContextSizeEnabled, beansInfoNode, containerInfo, isBeanSizeEnabled)
                        } catch (e: RuntimeException) {
                            return " Something was wrong. Probably these options will help you:\n" +
                                " JVM17: java  -Djdk.attach.allowAttachSelf=true -Djol.tryWithSudo=true " +
                                "--add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" +
                                " --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED" +
                                " --add-opens=java.base/java.util.function=ALL-UNNAMED -jar ./target/dataspace-app-DEV-SNAPSHOT.jar\n" +
                                " JVM21: java -XX:+EnableDynamicAgentLoading -Djdk.attach.allowAttachSelf=true -Djol.tryWithSudo=true" +
                                " --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED" +
                                " --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.function=ALL-UNNAMED" +
                                " --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED" +
                                " --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.security=ALL-UNNAMED" +
                                " --add-opens=java.base/jdk.internal.module=ALL-UNNAMED -jar ./target/dataspace-app-DEV-SNAPSHOT.jar"
                        }
                    } else {
                        gatherGeneralContextInfo(contextNode, modelInfo, containerInfo, "inactive")
                    }
                }
            }
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode)
        } catch (e: JsonProcessingException) {
            LOGGER.error("Failed to convert model information to JSON: ", e)
            throw java.lang.RuntimeException(e)
        }
        return json
    }

    @Throws(RuntimeException::class)
    private fun computeSizeOfBeanOrContext(
        isContextSizeEnabled: Boolean,
        beansInfoNode: ObjectNode,
        containerInfo: ContainerInfo,
        isBeanSizeEnabled: Boolean,
    ) {
        if (isContextSizeEnabled) {
            try {
                beansInfoNode.put(
                    "context-size-bytes",
                    GraphLayout
                        .parseInstance(containerInfo.context)
                        .totalSize()
                        .toString(),
                )
            } catch (e: RuntimeException) {
                LOGGER.error("Exception occurred", e)
                throw RuntimeException(e)
            }
        }

        if (isBeanSizeEnabled) {
            val beanNode: JsonNode = beansInfoNode.withObject("/bean-size-bytes")
            containerInfo.context.beanDefinitionNames.forEach { beanName ->
                run {
                    try {
                        (beanNode as ObjectNode).put(
                            beanName,
                            GraphLayout
                                .parseInstance(containerInfo.context.getBean(beanName))
                                .totalSize()
                                .toString(),
                        )
                    } catch (e: RuntimeException) {
                        LOGGER.error("Exception occurred: ", e)
                        throw RuntimeException(e)
                    }
                }
            }
        } else {
            val beanNode: JsonNode = beansInfoNode.withObject("/beans-name")
            var count = 0
            containerInfo.context.beanDefinitionNames.forEach { beanName ->
                run {
                    count++
                    (beanNode as ObjectNode).put(
                        count.toString(),
                        beanName,
                    )
                }
            }
        }
    }

    private fun gatherGeneralContextInfo(
        contextNode: ObjectNode,
        modelInfo: ModelInfo,
        containerInfo: ContainerInfo,
        activeSuffix: String,
    ) {
        contextNode.put("contextId-$activeSuffix", modelInfo.modelId)
        containerInfo.requestStat.forEach { (key: String, value: Long) ->
            contextNode.put("context-$activeSuffix-$key", value.toString())
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ParentCtxController::class.java)
    }
}
