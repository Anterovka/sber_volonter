package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.PositionedSimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ValueReader

internal class SubQueryMeta(
    val subQuery: PositionedSimpleSubQueryBuilder,
) {
    lateinit var valueReader: ValueReader
}
