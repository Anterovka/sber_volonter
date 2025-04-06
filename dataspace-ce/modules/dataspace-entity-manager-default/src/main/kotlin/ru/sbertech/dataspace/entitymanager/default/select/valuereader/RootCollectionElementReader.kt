package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.common.getOrSet
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.universalvalue.UniversalValue

internal class RootCollectionElementReader(
    private val cacheId: Int,
    private val elementReader: ValueReader,
) : ValueReader() {
    override fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ) = elementReader.read(cacheById, resultReader).also {
        cacheById.uncheckedCast<MutableList<MutableCollection<UniversalValue?>>>().getOrSet(cacheId) { arrayListOf() } += it
    }
}
