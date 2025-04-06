package ru.sbertech.dataspace.sql.dialect.postgres

import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.expr.ExprVisitor

internal object PriorityReturningVisitor : ExprVisitor<ExprPriority> {
    override fun visit(
        value: Expr.Value,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        param0: Expr.Param,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        column: Expr.Column,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        subQuery: Expr.SubQuery,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        neg: Expr.Neg,
        param: Unit,
    ) = ExprPriority.NEG

    override fun visit(
        abs: Expr.Abs,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        upper: Expr.Upper,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        lower: Expr.Lower,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        length: Expr.Length,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        trim: Expr.Trim,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        lTrim: Expr.LTrim,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        rTrim: Expr.RTrim,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        round: Expr.Round,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        ceil: Expr.Ceil,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        floor: Expr.Floor,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        hash: Expr.Hash,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        cast: Expr.Cast,
        param: Unit,
    ) = ExprPriority.CAST

    override fun visit(
        switch: Expr.Switch,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        isNull: Expr.IsNull,
        param: Unit,
    ) = ExprPriority.COMPARISON

    override fun visit(
        isNotNull: Expr.IsNotNull,
        param: Unit,
    ) = ExprPriority.COMPARISON

    override fun visit(
        exists: Expr.Exists,
        param: Unit,
    ) = ExprPriority.COMPARISON

    override fun visit(
        min: Expr.Min,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        max: Expr.Max,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        sum: Expr.Sum,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        avg: Expr.Avg,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        count: Expr.Count,
        param: Unit,
    ) = ExprPriority.VALUE

    override fun visit(
        not: Expr.Not,
        param: Unit,
    ) = ExprPriority.NOT

    override fun visit(
        and: Expr.And,
        param: Unit,
    ) = ExprPriority.AND

    override fun visit(
        or: Expr.Or,
        param: Unit,
    ) = ExprPriority.OR

    override fun visit(
        add: Expr.Add,
        param: Unit,
    ) = ExprPriority.ADD

    override fun visit(
        sub: Expr.Sub,
        param: Unit,
    ) = ExprPriority.ADD

    override fun visit(
        mul: Expr.Mul,
        param: Unit,
    ) = ExprPriority.MUL

    override fun visit(
        div: Expr.Div,
        param: Unit,
    ) = ExprPriority.MUL

    override fun visit(
        mod: Expr.Mod,
        param: Unit,
    ) = ExprPriority.MUL

    override fun visit(
        temporalAdd: Expr.TemporalAdd,
        param: Unit,
    ) = ExprPriority.ADD

    override fun visit(
        temporalSub: Expr.TemporalSub,
        param: Unit,
    ) = ExprPriority.ADD

    override fun visit(
        eq: Expr.Eq,
        param: Unit,
    ) = ExprPriority.COMPARISON

    override fun visit(
        inSubQuery: Expr.InSubQuery,
        param: Unit,
    ) = ExprPriority.COMPARISON

    override fun visit(
        inList: Expr.InList,
        param: Unit,
    ) = ExprPriority.COMPARISON
}
