package ru.sbertech.dataspace.entitymanager.default.select.exprselector

import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class CondSelector(
    val sqlExpr: SqlExpr,
) : ExprSelector()
