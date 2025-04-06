package ru.sbertech.dataspace.sql.dialect.postgres

import ru.sbertech.dataspace.sql.dialect.Dialect
import ru.sbertech.dataspace.sql.dialect.PreparedQuery
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.query.Query
import java.sql.Connection

class PostgresDialect(
    private val defaultSchema: String? = null,
) : Dialect {
    override fun prepareQuery(
        connection: Connection,
        query: Query,
    ): PreparedQuery {
        val stringBuilder = StringBuilder()
        val params = arrayListOf<Expr.Param>()
        query.accept(QueryPreparingVisitor(defaultSchema, stringBuilder, params))
        return PostgresPreparedQuery(connection, stringBuilder.toString(), params)
    }
}
