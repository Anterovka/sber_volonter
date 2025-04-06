package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.sql.dialect.ResultReader

internal class ConstPrimitiveReader(
    private val value: Primitive,
) : ValueReader() {
    override fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ) = value
}
