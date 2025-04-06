package ru.sbertech.dataspace.sql.table

import ru.sbertech.dataspace.sql.JoinType
import ru.sbertech.dataspace.sql.expr.Expr

sealed class Table {
    abstract fun <P, R> accept(
        visitor: TableParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: TableVisitor<R>): R = accept(visitor, Unit)

    data class Simple(
        val name: String,
        val alias: String? = null,
        val schema: String? = null,
    ) : Table() {
        override fun <P, R> accept(
            visitor: TableParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Join(
        val table1: Table,
        val table2: Table,
        val cond: Expr,
        val type: JoinType = JoinType.INNER,
    ) : Table() {
        override fun <P, R> accept(
            visitor: TableParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class SubQuery(
        val subQuery: ru.sbertech.dataspace.sql.subquery.SubQuery,
        val alias: String,
    ) : Table() {
        override fun <P, R> accept(
            visitor: TableParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
