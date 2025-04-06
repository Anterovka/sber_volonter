package ru.sbertech.dataspace.services

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationContextFactory
import org.springframework.boot.WebApplicationType
import org.springframework.context.ApplicationContext
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.support.ResourcePropertySource
import ru.sbertech.dataspace.entity.ContainerInfo
import ru.sbertech.dataspace.entity.ERROR_NAME
import ru.sbertech.dataspace.entity.ModelInfo
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.entity.REQUEST_NAME
import ru.sbertech.dataspace.entity.RESPONSE_NAME
import ru.sbertech.dataspace.modelcontext.configs.ChildCtxConfig
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.Properties
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.isDirectory

abstract class AbstractContextManager(
    private var parentContext: ApplicationContext,
    private var modelMetaInfo: ModelMetaInfo,
    private var environment: Environment,
    protected var properties: AppProperties,
) : ContextManager {
    private val switchLock: ReentrantLock = ReentrantLock()

    @PostConstruct
    fun initContext() {
        val allConfigsDirectory = properties.pathConfigDirectory
        try {
            createContextIfConfigDirectoriesExist(allConfigsDirectory)
        } catch (exception: ContextOperationException) {
            LOGGER.error("Context exception within context manager init", exception)
        }
    }

    @Throws(ContextOperationException::class)
    protected fun createContext(
        name: String,
        path: Path,
    ): String = createContext(name, true, path)

    @Throws(ContextOperationException::class)
    protected fun createContext(
        inName: String,
        isActiveContext: Boolean,
        changedPath: Path,
    ): String {
        var name = inName

        if (properties.singleMode) name = properties.defaultModelId

        LOGGER.info("For directory {} with configuration will be create context", name)

        val childContext = AnnotationConfigApplicationContext()

        val childEnvironment =
            ApplicationContextFactory.DEFAULT
                .createEnvironment(WebApplicationType.SERVLET)
        childContext.environment = childEnvironment

        var modelPath: Path = changedPath

        if (!changedPath.isDirectory()) {
            modelPath = changedPath.parent
        }

        val modelInfo = createOrUpdateModelInfo(name, childContext, isActiveContext, modelPath)

        try {
            LOGGER.info("Updated metaInfo for model {}", modelInfo!!.modelId)
            if (parentContext.environment is ConfigurableEnvironment) {
                childEnvironment.conversionService = (parentContext.environment as ConfigurableEnvironment).conversionService

                try {
                    initContextProperties(name, modelInfo, childEnvironment)
                } catch (e: ContextOperationException) {
                    modelMetaInfo.removeModelByModelId(name)
                    throw ContextOperationException(e)
                }
            }

            childContext.setClassLoader(parentContext.classLoader!!)

            val contextSuffix = if (isActiveContext) ACTIVE else INACTIVE

            childContext.setId(modelInfo.modelId + "_" + contextSuffix)

            childContext.register(ChildCtxConfig::class.java)

            LOGGER.info("Context for model {} will be refresh", name)
            try {
                childContext.refresh()
            } catch (e: Exception) {
                modelMetaInfo.removeModelByModelId(name)
                if (properties.singleMode) (modelMetaInfo.allModels as ArrayList).clear()
                throw ContextOperationException(e)
            }
            LOGGER.info("Context for model {} refreshing completed", name)

            return childContext.id
        } catch (e: ContextOperationException) {
            LOGGER.error("The context could not be created: ", e)
            throw e
        }
    }

    protected fun createOrUpdateModelInfo(
        name: String,
        childContext: ConfigurableApplicationContext,
        isActiveContext: Boolean,
        modelPath: Path,
    ): ModelInfo? {
        val containerInfo = ContainerInfo(isActiveContext, childContext)
        val modelPathWithoutFileName =
            if (modelPath.toFile().isFile) {
                modelPath
                    .toFile()
                    .parentFile.parentFile
                    .toString()
            } else {
                modelPath
                    .toFile()
                    .toString()
            }

        val deletedDirNameIfExists = deleteDirNameIfExists(modelPathWithoutFileName, properties.removeTemplateForModelPath)

        if (isActiveContext) {
            val modelInfo = ModelInfo(name, deletedDirNameIfExists)
            modelInfo.addContainerInfo(containerInfo)
            if (properties.singleMode) (modelMetaInfo.allModels as ArrayList).clear()
            modelMetaInfo.addModelInfo(modelInfo)
            return modelInfo
        }
        val modelByModelId = modelMetaInfo.getModelByModelId(name)
        if (modelByModelId != null) modelByModelId.addContainerInfo(containerInfo)

        return modelByModelId
    }

    /**
     * Delete string template from the end of string
     */
    private fun deleteDirNameIfExists(
        dirName: String,
        template: String,
    ): String {
        val dirNameWithoutData = dirName.replace(template, "")
        return dirNameWithoutData
    }

    @Throws(ContextOperationException::class)
    override fun createOrUpdateContext(
        modelId: String,
        inPath: Path,
    ) {
        val existModel =
            modelMetaInfo.allModels
                .stream()
                .filter { modelInfo: ModelInfo -> modelInfo.modelId == modelId }
                .findFirst()

        try {
            if (existModel.isPresent) {
                LOGGER.info("The model with ID {} already exists, this model will be updated", modelId)
                val activeContainerOptional =
                    existModel
                        .get()
                        .containersInfo!!
                        .stream()
                        .filter(ContainerInfo::isActive)
                        .findFirst()
                if (activeContainerOptional.isPresent) {
                    val inActiveContextId = createContext(convertDirNameToUri(modelId), false, inPath)
                    if (!inActiveContextId.isEmpty()) {
                        LOGGER.info("An inactive context has been created")
                    }
                    switchActiveContext(modelId)
                    val modelInfo = modelMetaInfo.getModelByModelId(modelId)
                    if (modelInfo != null) {
                        shutdownInactiveContext(modelInfo.inActiveContainerInfo)
                        modelInfo.removeContainerInfo(modelInfo.inActiveContainerInfo)
                        val changedContextId =
                            modelInfo.activeContainerInfo
                                ?.context!!
                                .id
                                ?.replace(INACTIVE, ACTIVE)
                        modelInfo.activeContainerInfo
                            ?.context!!
                            .setId(
                                changedContextId!!,
                            )
                    }
                }
            } else {
                createContext(convertDirNameToUri(modelId), inPath)
            }
        } catch (e: ContextOperationException) {
            LOGGER.error("The context could not be created: ", e)
            throw e
        }
    }

    protected fun shutdownInactiveContext(containerInfo: ContainerInfo?) {
        if (containerInfo != null) {
            val contextId = containerInfo.context.id
            val pause = properties.pauseSecondBeforeContextOff.toLong()

            LOGGER.info(
                "The inactive context with Id {} will be turned off after {} sec .",
                contextId,
                pause,
            )
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(pause))
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            if (isOldContextHasNotRequests(containerInfo)) {
                containerInfo.context.stop()
                LOGGER.info("The context with the ID {} has been disabled", contextId)
            }
        }
    }

    protected fun isOldContextHasNotRequests(containerInfo: ContainerInfo): Boolean {
        val requestsCount = containerInfo.requestStat[REQUEST_NAME]!!
        val responseCount = containerInfo.requestStat[RESPONSE_NAME]!!
        val errorCount = containerInfo.requestStat[ERROR_NAME]!!
        var step = 0
        while (step <= properties.shutdownRetryCount) {
            if (requestsCount == responseCount || requestsCount == (responseCount + errorCount)) {
                LOGGER.info("The number of requests {} and responses {} the same.", requestsCount, responseCount)
                break
            }
            LOGGER.info(
                "The number of requests {} and responses {} differ. Another attempt will be made after {} seconds",
                requestsCount,
                responseCount,
                properties.shutdownRetryPauseSecond,
            )
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(properties.shutdownRetryPauseSecond.toLong()))
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            ++step
        }
        return true
    }

    protected fun switchActiveContext(modelId: String) {
        switchLock.lock()
        val modelInfo = modelMetaInfo.getModelByModelId(modelId)

        // TODO  тут похоже надо брать не актив не эктив а по другому признаку, ID порту или тп. Т.к. в один момент оба контекста будут активные
        val oldActiveContext = modelInfo.activeContainerInfo
        val newInActiveContext = modelInfo.inActiveContainerInfo
        newInActiveContext?.isActive = true
        oldActiveContext?.isActive = false
        LOGGER.info(
            "The context role has been changed. Now the active context is ",
        )
        switchLock.unlock()
    }

    @Throws(ContextOperationException::class)
    protected fun initContextProperties(
        name: String,
        modelInfo: ModelInfo,
        childEnvironment: ConfigurableEnvironment,
    ) {
        val propPath: String =
            StringBuilder(modelInfo.modelPath)
                .append(FileSystems.getDefault().separator)
                .append(properties.modelContextFullFileNameProperties)
                .toString()
        val fileSystemResource = FileSystemResource(propPath)

        var tryCount = 0
        while (true) {
            try {
                val resourcePropertySource =
                    ResourcePropertySource(properties.modelPropertiesFileName, fileSystemResource)
                LOGGER.info("For model {} has been found property file {}", name, resourcePropertySource.name)
                childEnvironment.propertySources.addLast(resourcePropertySource)
                break
            } catch (e: IOException) {
                if (properties.singleMode) {
                    LOGGER.info("Property file for context {} hasn't been found. Single mode will be continued without it", name)
                    throw ContextOperationException(e)
                }

                LOGGER.info(
                    "Property file for context {} hasn't been found. {} attempts will be made in 1 second ...",
                    name,
                    properties.maxTriesPropertyFileFinding - tryCount,
                )
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(properties.pauseSecondsPropertyFileFinding.toLong()))
                } catch (ex: InterruptedException) {
                    LOGGER.error("Error was occurred: ", e)
                    throw RuntimeException(ex)
                }
                if (++tryCount == properties.maxTriesPropertyFileFinding) {
                    LOGGER.error("During context properties initializing the error was occurred: ", e)
                    throw ContextOperationException(e)
                }
            }
        }

        val prop = Properties()
        prop["child.model.path"] = modelInfo.modelPath
        prop["child.model.pdm.zip"] = properties.pdmZipped
        setPropertiesIfSingleMode(prop)
        childEnvironment.propertySources.addFirst(PropertiesPropertySource("dspcCustomProperties", prop))
    }

    fun setPropertiesIfSingleMode(childProp: Properties) {
        if (properties.singleMode && environment is ConfigurableEnvironment) {
            for (propertySource in (environment as ConfigurableEnvironment).propertySources) {
                if (propertySource is EnumerablePropertySource<*>) {
                    for (key in (propertySource as EnumerablePropertySource<*>).propertyNames) {
                        if (key.startsWith("${properties.singleOptionPrefix}.")) {
                            val optionName: String = key.replaceFirst("${properties.singleOptionPrefix}.", "")
                            childProp[optionName] = propertySource.getProperty(key)
                        }
                    }
                }
            }
        }
    }

    @Throws(ContextOperationException::class)
    override fun removeContext(modelId: String) {
        if (properties.singleMode) {
            modelMetaInfo.allModels.forEach { modelInfo ->
                modelInfo.containersInfo?.forEach { containerInfo ->
                    LOGGER.info("Stopping context ${containerInfo.context.id}")
                    containerInfo.context.stop()
                }
            }
            (modelMetaInfo.allModels as ArrayList).clear()
            LOGGER.info("Deleted all models")
            return
        }
        modelMetaInfo.removeModelByModelId(modelId)
        val contextOptional: ConfigurableApplicationContext? =
            modelMetaInfo.searchActiveContextByModelId(
                modelId,
            )
        if (contextOptional == null) {
            LOGGER.error(
                "Deleting the context is not possible because the context with the {} model does not exist.",
                modelId,
            )
            return
        }
        LOGGER.info("The {} context will be disabled for the {} model", contextOptional.id, modelId)

        contextOptional.stop()
    }

    protected fun convertDirNameToUri(dirName: String): String = dirName

    protected fun createContextIfConfigDirectoriesExist(allConfigsDirectory: String?) {
        LOGGER.info(
            "In the directory {} , a search for folders with models will be performed and further context will be raised",
            allConfigsDirectory,
        )
        val allConfigsDirectoryFile = File(allConfigsDirectory)

        if (!allConfigsDirectoryFile.exists() || !allConfigsDirectoryFile.isDirectory) {
            LOGGER.error(
                "The directory {} isn't exist or it isn't directory.",
                allConfigsDirectoryFile.absolutePath,
            )
            return
        }

        if (allConfigsDirectoryFile.listFiles()?.size == 0) {
            LOGGER.info(
                "In the folder {} with configuration hasn't been found directories",
                allConfigsDirectoryFile.absolutePath,
            )
        }
        if (properties.singleMode) {
            createContext(properties.defaultModelId, allConfigsDirectoryFile.toPath())
        } else {
            createContextIfAllExist(allConfigsDirectoryFile)
        }
    }

    abstract fun createContextIfAllExist(allConfigsDirectoryFile: File)

    companion object {
        protected val LOGGER: Logger = LoggerFactory.getLogger(AbstractContextManager::class.java)

        protected const val ACTIVE: String = "active"
        protected const val INACTIVE: String = "inactive"
    }
}
