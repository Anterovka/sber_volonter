package ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder

import ru.sbertech.dataspace.sql.subquery.SubQuery

internal abstract class SubQueryBuilder {
    abstract fun build(): SubQuery
}
