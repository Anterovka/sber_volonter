package ru.sbertech.dataspace.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.function.Consumer

class K8sConfigMapContextManager(
    parentContext: ApplicationContext,
    modelMetaInfo: ModelMetaInfo,
    environment: Environment,
    properties: AppProperties,
) : AbstractContextManager(parentContext, modelMetaInfo, environment, properties) {
    override fun createContextIfAllExist(allConfigsDirectoryFile: File) {
        for (fileOrDirectoryWithConfig in allConfigsDirectoryFile.listFiles()) {
            if (fileOrDirectoryWithConfig.isDirectory) {
                LOGGER.info(
                    "In the folder {} with configurations has been found directory {}",
                    allConfigsDirectoryFile.absolutePath,
                    fileOrDirectoryWithConfig.absolutePath,
                )

                val pathPattern = File(fileOrDirectoryWithConfig.absolutePath + properties.pathConfigModelDir).toPath()

                if (pathPattern.toFile().exists()) {
                    try {
                        Files
                            .newDirectoryStream(
                                pathPattern,
                                "*" + properties.pathConfigModelFilePattern,
                            ).use { dirStream ->
                                dirStream.forEach(
                                    Consumer { path: Path ->
                                        LOGGER.info("Found files {}", path)
                                        val contextId: String?
                                        try {
                                            val modelId =
                                                path.fileName.toString().substring(0, path.fileName.toString().indexOf("_"))
                                            contextId = createContext(modelId, path)
                                            LOGGER.info(
                                                "For folder {} with configuration has been created context {}",
                                                fileOrDirectoryWithConfig.name,
                                                contextId,
                                            )
                                        } catch (e: ContextOperationException) {
                                            LOGGER.error("The context could not be created: ", e)
                                        } catch (e: IndexOutOfBoundsException) {
                                            LOGGER.error("The context could not be created: ", e)
                                        }
                                    },
                                )
                            }
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            }
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(K8sConfigMapContextManager::class.java)
    }
}
