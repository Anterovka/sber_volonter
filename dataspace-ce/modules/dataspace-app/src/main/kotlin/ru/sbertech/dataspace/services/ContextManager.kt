package ru.sbertech.dataspace.services

import ru.sbertech.dataspace.services.exception.ContextOperationException
import java.nio.file.Path

interface ContextManager {
    @Throws(ContextOperationException::class)
    fun createOrUpdateContext(
        modelId: String,
        inPath: Path,
    )

    @Throws(ContextOperationException::class)
    fun removeContext(modelId: String)
}
