package ru.sbertech.dataspace.services

import jakarta.annotation.PreDestroy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import ru.sbertech.dataspace.properties.AppProperties
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Consumer

class PathWatchService(
    private val appProperties: AppProperties,
    private val fileListenersHolder: FileListenersHolder,
) : ApplicationListener<ContextRefreshedEvent> {
    private var watchService: WatchService? = null
    private var executor: ExecutorService? = null

    private var isEnabled = false

    @EventListener
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        init()
    }

    private fun init() {
        if (!isEnabled) {
            try {
                watchService = FileSystems.getDefault().newWatchService()
                executor = Executors.newSingleThreadExecutor()
                startRecursiveWatcher()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            isEnabled = true
        }
    }

    @Throws(IOException::class)
    private fun startRecursiveWatcher() {
        LOGGER.info("Starting Recursive  File Watcher")

        val keys: MutableMap<WatchKey, Path> = HashMap()

        val register =
            Consumer<Path> { p: Path ->
                try {
                    Files.walkFileTree(
                        p,
                        object : SimpleFileVisitor<Path>() {
                            @Throws(IOException::class)
                            override fun preVisitDirectory(
                                dir: Path,
                                attrs: BasicFileAttributes,
                            ): FileVisitResult {
                                LOGGER.info("Registering $dir in watcher service")
                                val watchKey =
                                    dir.register(
                                        watchService,
                                        arrayOf<WatchEvent.Kind<*>>(
                                            StandardWatchEventKinds.ENTRY_CREATE,
                                            StandardWatchEventKinds.ENTRY_MODIFY,
                                            StandardWatchEventKinds.ENTRY_DELETE,
                                        ),
                                    )
                                keys[watchKey] = dir
                                return FileVisitResult.CONTINUE
                            }
                        },
                    )
                } catch (e: IOException) {
                    throw RuntimeException("Error registering path $p")
                }
            }

        register.accept(Paths.get(appProperties.pathConfigDirectory))

        executor!!.submit {
            while (true) {
                val key: WatchKey
                try {
                    key = watchService!!.take() // wait for a key to be available
                } catch (ex: InterruptedException) {
                    return@submit
                }

                val dir = keys[key]
                if (dir == null) {
                    LOGGER.error("WatchKey {} not recognized!", key)
                    continue
                }

                key
                    .pollEvents()
                    .stream()
                    .filter { e: WatchEvent<*> -> (e.kind() !== StandardWatchEventKinds.OVERFLOW) }
                    .forEach { e: WatchEvent<*> ->
                        val path =
                            (e as WatchEvent<Path?>).context()
                        val absPath = dir.resolve(path)
                        if (appProperties.filesystemK8sEnabled) {
                            if (e.kind() === StandardWatchEventKinds.ENTRY_CREATE &&
                                !absPath
                                    .toString()
                                    .contains("data_tmp") &&
                                absPath.toString().contains("..data")
                            ) {
                                LOGGER.info(
                                    "There have been changes {} with {}, it will be updated in the list of monitored",
                                    e.kind(),
                                    absPath.toFile().absolutePath,
                                )
                                register.accept(absPath)
                                fileListenersHolder.notifyListeners(absPath, e)
                            } else if (e.kind() === StandardWatchEventKinds.ENTRY_DELETE &&
                                !absPath
                                    .toString()
                                    .contains("/..")
                            ) {
                                LOGGER.info(
                                    "There have been changes {} with {}",
                                    e.kind(),
                                    absPath.toFile().absolutePath,
                                )
                                fileListenersHolder.notifyListeners(absPath, e)
                            }
                        } else if (appProperties.filesystemLinuxEnabled) {
                            if (absPath
                                    .toFile()
                                    .isDirectory &&
                                e.kind() === StandardWatchEventKinds.ENTRY_CREATE ||
                                e.kind() === StandardWatchEventKinds.ENTRY_MODIFY &&
                                absPath
                                    .toFile()
                                    .exists()
                            ) {
                                LOGGER.info(
                                    "There have been changes {} with {}, it will be updated in the list of watched",
                                    e.kind(),
                                    absPath.toFile().absolutePath,
                                )
                                register.accept(absPath)
                                fileListenersHolder.notifyListeners(absPath, e)
                            } else if (!absPath
                                    .toFile()
                                    .isDirectory &&
                                e.kind() === StandardWatchEventKinds.ENTRY_DELETE
                            ) {
                                LOGGER.info(
                                    "There have been changes {} with {}",
                                    e.kind(),
                                    absPath.toFile().absolutePath,
                                )
                                fileListenersHolder.notifyListeners(absPath, e)
                            } else if (appProperties.singleMode) {
                                fileListenersHolder.notifyListeners(absPath, e)
                                LOGGER.info(
                                    "There have been changes {} with {} in Single Mode",
                                    e.kind(),
                                    absPath.toFile().absolutePath,
                                )
                            }
                        }
                    }

                val valid = key.reset()
                if (!valid) {
                    LOGGER.info(
                        "The {} directory will be removed from the watch list",
                        key.watchable().toString(),
                    )
                    keys.remove(key)
                    if (keys.isEmpty()) {
                        break
                    }
                }
            }
        }
    }

    @PreDestroy
    fun cleanup() {
        try {
            watchService!!.close()
        } catch (e: IOException) {
            LOGGER.error("Error closing watcher service", e)
        }
        executor!!.shutdown()
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(PathWatchService::class.java)
    }
}
