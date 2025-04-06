package ru.sbertech.dataspace.expr

interface ExprParameterizedVisitor<in P, out R> {
    fun visit(
        value: Expr.Value,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        cur: Expr.Cur,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        root: Expr.Root,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        property: Expr.Property,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        eq: Expr.Eq,
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
}
