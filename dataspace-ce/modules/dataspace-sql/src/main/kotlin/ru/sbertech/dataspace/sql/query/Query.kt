package ru.sbertech.dataspace.sql.query

import ru.sbertech.dataspace.sql.CommonTable
import ru.sbertech.dataspace.sql.Lock
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.subquery.SubQuery

sealed class Query {
    abstract fun <P, R> accept(
        visitor: QueryParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: QueryVisitor<R>): R = accept(visitor, Unit)

    data class Insert(
        val table: String,
        val columns: Collection<String>,
        val values: Collection<Collection<Expr>>,
        val schema: String? = null,
    ) : Query() {
        override fun <P, R> accept(
            visitor: QueryParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Update(
        val table: String,
        val valueByColumn: Map<String, Expr>,
        val cond: Expr? = null,
        val schema: String? = null,
    ) : Query() {
        override fun <P, R> accept(
            visitor: QueryParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Delete(
        val table: String,
        val cond: Expr? = null,
        val schema: String? = null,
    ) : Query() {
        override fun <P, R> accept(
            visitor: QueryParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Select(
        val subQuery: SubQuery,
        val commonTables: Collection<CommonTable> = emptyList(),
        val lock: Lock? = null,
    ) : Query() {
        override fun <P, R> accept(
            visitor: QueryParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
