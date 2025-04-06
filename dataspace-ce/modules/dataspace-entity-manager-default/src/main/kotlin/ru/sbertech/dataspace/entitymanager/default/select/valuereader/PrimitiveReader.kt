package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.dialect.ResultReader

internal class PrimitiveReader(
    private val type: PrimitiveType,
    private val columnIndex: Int,
) : ValueReader() {
    override fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ) = resultReader[type, columnIndex]
}
