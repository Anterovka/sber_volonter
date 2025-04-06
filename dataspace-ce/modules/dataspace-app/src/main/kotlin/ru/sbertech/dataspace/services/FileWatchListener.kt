package ru.sbertech.dataspace.services

import java.nio.file.Path
import java.nio.file.WatchEvent

interface FileWatchListener {
    fun onEvent(
        path: Path,
        event: WatchEvent<*>,
    )
}
