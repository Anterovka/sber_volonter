package ru.sbertech.dataspace.entitymanager.default.select.valuereader

import ru.sbertech.dataspace.common.getOrSet
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.universalvalue.UniversalValue

internal class RootCollectionReader(
    private val cacheId: Int,
) : ValueReader() {
    override fun read(
        cacheById: MutableList<Any?>,
        resultReader: ResultReader,
    ) = cacheById.uncheckedCast<MutableList<MutableCollection<UniversalValue>>>().getOrSet(cacheId) { arrayListOf() }
}
