package ru.sbertech.dataspace.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.entity.ModelInfo
import ru.sbertech.dataspace.entity.ModelMetaInfo
import ru.sbertech.dataspace.properties.AppProperties
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import java.util.Optional
import java.util.function.Consumer

class K8sConfigMapListener(
    listenersHolder: FileListenersHolder,
    private val modelMetaInfo: ModelMetaInfo,
    contextManager: ContextManager,
    private val properties: AppProperties,
) : AbstractFileListener(listenersHolder, contextManager) {
    override fun deleteContext(path: Path?) {
        val dirName = path!!.toFile().name

        val containsPath: Optional<ModelInfo> =
            modelMetaInfo.allModels
                .stream()
                .filter { modelInfo ->
                    modelInfo.modelPath.lowercase().contains(path.toString().lowercase(Locale.getDefault()))
                }.findFirst()
        containsPath.ifPresent { modelInfo: ModelInfo ->
            LOGGER.info(
                "Only the top-level folder {} for the model {} has been deleted",
                path.toFile().absolutePath,
                modelInfo.modelPath,
            )
        }

        if (dirName.lowercase(Locale.getDefault()).contains(properties.pathConfigModelFilePattern) || containsPath.isPresent) {
            try {
                val modelId =
                    if (path.fileName.toString().contains("_")
                    ) {
                        path.fileName.toString().substring(0, path.fileName.toString().indexOf("_"))
                    } else {
                        containsPath.get().modelId
                    }
                contextManager.removeContext(modelId)
            } catch (e: ContextOperationException) {
                LOGGER.error("The context could not be deleted ", e)
            } catch (e: IndexOutOfBoundsException) {
                LOGGER.error("The context could not be deleted ", e)
            }
        }
    }

    override fun createOrUpdateContext(inPath: Path) {
        val absolutePath = inPath.resolve(inPath)
        try {
            Files
                .newDirectoryStream(
                    absolutePath,
                    "*_active.txt",
                ).use { dirStream ->
                    dirStream.forEach(
                        Consumer { path: Path ->
                            LOGGER.info("Found files {}", path)
                            try {
                                val modelId = path.fileName.toString().substring(0, path.fileName.toString().indexOf("_"))
                                contextManager.createOrUpdateContext(modelId, path)
                            } catch (e: ContextOperationException) {
                                LOGGER.error("The context could not be created: ", e)
                                throw e
                            } catch (e: IndexOutOfBoundsException) {
                                LOGGER.error("The context could not be created: ", e)
                                throw ContextOperationException(e)
                            }
                            LOGGER.info(
                                "For folder {} with configuration has been created or updated context",
                                path.fileName,
                            )
                        },
                    )
                }
        } catch (e: IOException) {
            LOGGER.error("The context could not be created: ", e)
            throw RuntimeException(e)
        }
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(K8sConfigMapListener::class.java)
    }
}
