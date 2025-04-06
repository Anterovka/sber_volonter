package ru.sbertech.dataspace.services

import java.nio.file.Path
import java.nio.file.WatchEvent

interface FileListenersHolder {
    fun addListener(listener: FileWatchListener)

    fun getListeners(): List<FileWatchListener>

    fun notifyListeners(
        path: Path,
        event: WatchEvent<*>,
    )
}
