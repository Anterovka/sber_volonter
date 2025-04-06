package ru.sbertech.dataspace.sql.expr

interface ExprParameterizedVisitor<in P, out R> {
    fun visit(
        null0: Expr.Null,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        value: Expr.Value,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        param0: Expr.Param,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        column: Expr.Column,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        subQuery: Expr.SubQuery,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        neg: Expr.Neg,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        abs: Expr.Abs,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        upper: Expr.Upper,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        lower: Expr.Lower,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        length: Expr.Length,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        trim: Expr.Trim,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        lTrim: Expr.LTrim,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        rTrim: Expr.RTrim,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        round: Expr.Round,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        ceil: Expr.Ceil,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        floor: Expr.Floor,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        hash: Expr.Hash,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        cast: Expr.Cast,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        switch: Expr.Switch,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        isNull: Expr.IsNull,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        isNotNull: Expr.IsNotNull,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        exists: Expr.Exists,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        min: Expr.Min,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        max: Expr.Max,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        sum: Expr.Sum,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        avg: Expr.Avg,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        count: Expr.Count,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        not: Expr.Not,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        and: Expr.And,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        or: Expr.Or,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        add: Expr.Add,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        sub: Expr.Sub,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        mul: Expr.Mul,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        div: Expr.Div,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        mod: Expr.Mod,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        temporalAdd: Expr.TemporalAdd,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        temporalSub: Expr.TemporalSub,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        eq: Expr.Eq,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        inSubQuery: Expr.InSubQuery,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        inList: Expr.InList,
        param: P,
    ): R = throw UnsupportedOperationException()
}
