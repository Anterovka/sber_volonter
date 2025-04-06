package ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder

import ru.sbertech.dataspace.entitymanager.default.select.SelectContext
import ru.sbertech.dataspace.entitymanager.default.select.SelectedExprMeta
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.expr.Expr

internal class PositionedSimpleSubQueryBuilder(
    private val context: SelectContext,
) : SimpleSubQueryBuilder() {
    private val selectedExprMetaByColumnIndex: MutableMap<Int, SelectedExprMeta> = linkedMapOf()

    fun select(
        expr: PrimitiveSelector,
        columnIndex: Int,
    ) = SelectedExprMeta(expr, null, columnIndex).also { selectedExprMeta ->
        metaBySelectedExpr.putIfAbsent(expr, selectedExprMeta)?.also { throw IllegalStateException() }
        selectedExprMetaByColumnIndex.putIfAbsent(columnIndex, selectedExprMeta)?.also { throw IllegalStateException() }
    }

    override fun selectedExprMeta(expr: PrimitiveSelector): SelectedExprMeta {
        val columnIndex = context.freeColumnIndex(expr.type, selectedExprMetaByColumnIndex.keys)
        return SelectedExprMeta(expr, null, columnIndex).also { selectedExprMetaByColumnIndex[columnIndex] = it }
    }

    override fun selectedExprs() =
        context.columnTypes.mapIndexed { index, columnType ->
            SelectedExpr(selectedExprMetaByColumnIndex[index + 1]?.expr?.sqlExpr ?: Expr.Null(columnType))
        }
}
