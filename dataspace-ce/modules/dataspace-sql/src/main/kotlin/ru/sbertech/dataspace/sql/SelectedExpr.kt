package ru.sbertech.dataspace.sql

import ru.sbertech.dataspace.sql.expr.Expr

data class SelectedExpr(
    val expr: Expr,
    val alias: String? = null,
)
