package ru.sbertech.dataspace.grammar.expr.tostring

import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.expr.ExprVisitor

internal object PriorityReturningVisitor : ExprVisitor<Priority> {
    override fun visit(
        value: Expr.Value,
        param: Unit,
    ) = Priority.VALUE

    override fun visit(
        cur: Expr.Cur,
        param: Unit,
    ) = Priority.VALUE

    override fun visit(
        root: Expr.Root,
        param: Unit,
    ) = Priority.VALUE

    override fun visit(
        property: Expr.Property,
        param: Unit,
    ) = Priority.VALUE

    override fun visit(
        eq: Expr.Eq,
        param: Unit,
    ) = Priority.COMPARISON

    override fun visit(
        and: Expr.And,
        param: Unit,
    ) = Priority.AND

    override fun visit(
        or: Expr.Or,
        param: Unit,
    ) = Priority.OR
}
