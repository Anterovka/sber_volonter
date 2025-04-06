package ru.sbertech.dataspace.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent

class LinuxFileListener(
    listenersHolder: FileListenersHolder?,
    contextManager: ContextManager?,
) : AbstractFileListener(listenersHolder!!, contextManager!!) {
    override fun onEvent(
        path: Path,
        event: WatchEvent<*>,
    ) {
        var modelPath = path
        try {
            if (event.kind() === StandardWatchEventKinds.ENTRY_CREATE || event.kind() === StandardWatchEventKinds.ENTRY_MODIFY) {
                createOrUpdateContext(path)
                LOGGER.info("Model {} has been created or refreshed ", path.toFile())
            } else if (event.kind() === StandardWatchEventKinds.ENTRY_DELETE) {
//                if (!path.isDirectory()) {
//                    modelPath = path.parent
//                }
                deleteContext(modelPath)
                LOGGER.info("Model {} has been deleted ", path.toFile())
            }
        } catch (e: ContextOperationException) {
            LOGGER.error("Model {} hasn't been changed because something went wrong", path.toFile())
        }
    }

    override fun deleteContext(path: Path?) {
        val dirName = path!!.toFile().name
        try {
            contextManager.removeContext(convertDirNameToUri(dirName))
        } catch (e: ContextOperationException) {
            LOGGER.error("The context could not be deleted ", e)
            throw RuntimeException(e)
        }
    }

    @Throws(ContextOperationException::class)
    override fun createOrUpdateContext(inPath: Path) {
        val dirName: String
        if (inPath.toFile().isDirectory) {
            dirName = inPath.toFile().name
            LOGGER.info("New changes on the file system are a directory {}", dirName)
        } else {
            dirName = inPath.toFile().parentFile.name
            LOGGER.info(
                "New changes on the file system are not a directory. The name {} of the file directory will be taken",
                dirName,
            )
        }
        try {
            contextManager.createOrUpdateContext(convertDirNameToUri(dirName), inPath)
        } catch (e: ContextOperationException) {
            LOGGER.error("The context could not be created: ", e)
            throw e
        }
    }

    private fun convertDirNameToUri(dirName: String): String = dirName

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(LinuxFileListener::class.java)
    }
}
