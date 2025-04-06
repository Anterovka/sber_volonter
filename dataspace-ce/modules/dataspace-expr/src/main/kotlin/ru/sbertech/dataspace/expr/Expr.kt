package ru.sbertech.dataspace.expr

import ru.sbertech.dataspace.primitive.Primitive

sealed class Expr {
    abstract fun <P, R> accept(
        visitor: ExprParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: ExprVisitor<R>): R = accept(visitor, Unit)

    data class Value(
        val value: Primitive,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Cur : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Root : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Property(
        val expr: Expr,
        val name: String,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Eq(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class And(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Or(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
