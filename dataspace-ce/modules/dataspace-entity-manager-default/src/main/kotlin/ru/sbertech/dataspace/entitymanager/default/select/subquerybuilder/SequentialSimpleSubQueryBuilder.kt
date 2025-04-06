package ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder

import ru.sbertech.dataspace.entitymanager.default.select.SelectContext
import ru.sbertech.dataspace.entitymanager.default.select.SelectedExprMeta
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.sql.SelectedExpr

internal class SequentialSimpleSubQueryBuilder(
    private val context: SelectContext,
) : SimpleSubQueryBuilder() {
    override fun selectedExprMeta(expr: PrimitiveSelector) =
        SelectedExprMeta(expr, context.nextSelectedExprAlias(), metaBySelectedExpr.size + 1)

    override fun selectedExprs() = metaBySelectedExpr.values.map { SelectedExpr(it.expr.sqlExpr, it.alias) }
}
