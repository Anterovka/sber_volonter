package ru.sbertech.dataspace.sql

import ru.sbertech.dataspace.sql.expr.Expr

data class SortCriterion(
    val expr: Expr,
    val order: SortOrder = SortOrder.ASC,
    val nullsOrder: SortNullsOrder = SortNullsOrder.DEFAULT,
)
