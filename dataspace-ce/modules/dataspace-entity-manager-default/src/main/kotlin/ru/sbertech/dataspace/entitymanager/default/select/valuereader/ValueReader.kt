package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.universalvalue.UniversalValue

internal abstract class ValueReader {
    abstract fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ): UniversalValue?
}
