package ru.sbertech.dataspace.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.io.File

class LinuxContextManager(
    parentContext: ApplicationContext,
    modelMetaInfo: ModelMetaInfo,
    environment: Environment,
    properties: AppProperties,
) : AbstractContextManager(parentContext, modelMetaInfo, environment, properties) {
    override fun createContextIfAllExist(allConfigsDirectoryFile: File) {
        var isFoundDirectoryWithModels = false
        for (fileOrDirectoryWithConfig in allConfigsDirectoryFile.listFiles()) {
            if (fileOrDirectoryWithConfig.isDirectory) {
                LOGGER.info(
                    "In the folder {} with configurations has been found directory {}",
                    allConfigsDirectoryFile.absolutePath,
                    fileOrDirectoryWithConfig.absolutePath,
                )
                var contextId: String?
                try {
                    contextId = createContext(fileOrDirectoryWithConfig.name, fileOrDirectoryWithConfig.toPath())
                    LOGGER.info(
                        "For folder {} with configuration has been created context {}",
                        fileOrDirectoryWithConfig.name,
                        contextId,
                    )
                    isFoundDirectoryWithModels = true
                } catch (e: ContextOperationException) {
                    LOGGER.error("The context could not be created: ", e)
                }
            }
        }
        if (!isFoundDirectoryWithModels) {
            LOGGER.info(
                "In the folder {} with configurations hasn't been found directory with models",
                allConfigsDirectoryFile.absolutePath,
            )
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(ContextManager::class.java)
    }
}
