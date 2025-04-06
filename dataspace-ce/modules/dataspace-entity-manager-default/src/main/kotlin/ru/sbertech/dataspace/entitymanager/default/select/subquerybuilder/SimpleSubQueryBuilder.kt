package ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder

import ru.sbertech.dataspace.entitymanager.default.select.SelectedExprMeta
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.CondSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.tablebuilder.TableBuilder
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.subquery.SubQuery

internal abstract class SimpleSubQueryBuilder : SubQueryBuilder() {
    var table: TableBuilder? = null

    var cond: CondSelector? = null

    protected val metaBySelectedExpr: MutableMap<PrimitiveSelector, SelectedExprMeta> = linkedMapOf()

    fun select(expr: PrimitiveSelector) = metaBySelectedExpr.getOrPut(expr) { selectedExprMeta(expr) }

    abstract fun selectedExprMeta(expr: PrimitiveSelector): SelectedExprMeta

    abstract fun selectedExprs(): Collection<SelectedExpr>

    override fun build() = SubQuery.Simple(selectedExprs(), table?.build(), cond?.sqlExpr)
}
