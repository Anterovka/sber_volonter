package ru.sbertech.dataspace.sql

import ru.sbertech.dataspace.sql.expr.Expr

data class SwitchCase(
    val value: Expr,
    val result: Expr,
)
