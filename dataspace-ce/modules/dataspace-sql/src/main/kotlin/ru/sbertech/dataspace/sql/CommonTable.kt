package ru.sbertech.dataspace.sql

import ru.sbertech.dataspace.sql.subquery.SubQuery

data class CommonTable(
    val name: String,
    val subQuery: SubQuery,
)
