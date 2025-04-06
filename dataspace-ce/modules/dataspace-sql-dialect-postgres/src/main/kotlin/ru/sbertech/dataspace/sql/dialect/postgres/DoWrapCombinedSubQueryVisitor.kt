package ru.sbertech.dataspace.sql.dialect.postgres

import ru.sbertech.dataspace.sql.subquery.SubQuery
import ru.sbertech.dataspace.sql.subquery.SubQueryVisitor

internal object DoWrapCombinedSubQueryVisitor : SubQueryVisitor<Boolean> {
    override fun visit(
        simpleSubQuery: SubQuery.Simple,
        param: Unit,
    ) = simpleSubQuery.sortCriteria.isNotEmpty() || simpleSubQuery.offset != null || simpleSubQuery.limit != null

    override fun visit(
        subQueriesCombination: SubQuery.Combination,
        param: Unit,
    ) = subQueriesCombination.sortCriteria.isNotEmpty() || subQueriesCombination.offset != null || subQueriesCombination.limit != null
}
