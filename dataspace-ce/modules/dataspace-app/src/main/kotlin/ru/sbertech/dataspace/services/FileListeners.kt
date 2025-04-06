package ru.sbertech.dataspace.services

import java.nio.file.Path
import java.nio.file.WatchEvent
import java.util.function.Consumer

class FileListeners : FileListenersHolder {
    private val listeners: MutableList<FileWatchListener> = ArrayList()

    override fun addListener(listener: FileWatchListener) {
        listeners.add(listener)
    }

    override fun getListeners(): List<FileWatchListener> = listeners

    override fun notifyListeners(
        path: Path,
        event: WatchEvent<*>,
    ) {
        listeners.forEach(Consumer { listener: FileWatchListener -> listener.onEvent(path, event) })
    }
}
