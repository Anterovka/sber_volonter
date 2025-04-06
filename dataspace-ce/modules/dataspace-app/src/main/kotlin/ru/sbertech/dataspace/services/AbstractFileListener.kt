package ru.sbertech.dataspace.services

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent

abstract class AbstractFileListener(
    private val listenersHolder: FileListenersHolder,
    protected val contextManager: ContextManager,
) : FileWatchListener {
    @PostConstruct
    protected fun init() {
        listenersHolder.addListener(this)
    }

    override fun onEvent(
        path: Path,
        event: WatchEvent<*>,
    ) {
        LOGGER.info("An event {} has been received for path {} ", event.kind(), path.toFile().absolutePath)
        try {
            if (event.kind() === StandardWatchEventKinds.ENTRY_CREATE) {
                createOrUpdateContext(path)
                LOGGER.info("Model {} has been created or refreshed ", path.toFile())
            } else if (event.kind() === StandardWatchEventKinds.ENTRY_DELETE) {
                deleteContext(path)
                LOGGER.info("Model {} has been deleted ", path.toFile())
            }
        } catch (e: ContextOperationException) {
            LOGGER.error("Model {} hasn't been changed ", path.toFile())
        }
    }

    protected abstract fun deleteContext(path: Path?)

    protected abstract fun createOrUpdateContext(inPath: Path)

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(AbstractFileListener::class.java)
    }
}
