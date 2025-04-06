package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.common.getOrSet
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.entitymanager.default.select.CollectionKey
import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.universalvalue.UniversalValue

internal class CollectionReader(
    private val cacheId: Int,
    private val keyReaders: Collection<ValueReader>,
) : ValueReader() {
    override fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ) = cacheById
        .uncheckedCast<MutableList<MutableMap<CollectionKey, MutableCollection<UniversalValue?>>>>()
        .getOrSet(cacheId) { linkedMapOf() }
        .getOrPut(keyReaders.map { it.read(cacheById, resultReader) }) { arrayListOf() }
}
