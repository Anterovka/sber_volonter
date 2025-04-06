package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.sql.dialect.ResultReader

internal class ObjectReader(
    private val propertyReaderByName: Map<String, ValueReader>,
) : ValueReader() {
    override fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ) = propertyReaderByName.mapValuesTo(linkedMapOf()) { (_, reader) -> reader.read(cacheById, resultReader) }
}
