package ru.sbertech.dataspace.sql.subquery

import ru.sbertech.dataspace.sql.CombinationType
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.SortCriterion
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.table.Table

sealed class SubQuery {
    abstract fun <P, R> accept(
        visitor: SubQueryParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: SubQueryVisitor<R>): R = accept(visitor, Unit)

    data class Simple(
        val selectedExprs: Collection<SelectedExpr>,
        val table: Table? = null,
        val cond: Expr? = null,
        val groupingExprs: Collection<Expr> = emptyList(),
        val groupingCond: Expr? = null,
        val sortCriteria: Collection<SortCriterion> = emptyList(),
        val offset: Expr? = null,
        val limit: Expr? = null,
    ) : SubQuery() {
        override fun <P, R> accept(
            visitor: SubQueryParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Combination(
        val subQuery1: SubQuery,
        val subQuery2: SubQuery,
        val type: CombinationType = CombinationType.UNION_ALL,
        val sortCriteria: Collection<SortCriterion> = emptyList(),
        val offset: Expr? = null,
        val limit: Expr? = null,
    ) : SubQuery() {
        override fun <P, R> accept(
            visitor: SubQueryParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
