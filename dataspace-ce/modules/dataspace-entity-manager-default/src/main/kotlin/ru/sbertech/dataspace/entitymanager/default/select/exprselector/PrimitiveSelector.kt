package ru.sbertech.dataspace.entitymanager.default.select.exprselector

import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.PrimitiveReader
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class PrimitiveSelector(
    val type: PrimitiveType,
    val sqlExpr: SqlExpr,
) : ExprSelector() {
    override fun reader(subQuery: SimpleSubQueryBuilder) = PrimitiveReader(type, subQuery.select(this).columnIndex)

    override fun eq(expr: ExprSelector) =
        when {
            expr is PrimitiveSelector && type == expr.type -> CondSelector(SqlExpr.Eq(sqlExpr, expr.sqlExpr))
            else -> throw IllegalArgumentException()
        }
}
