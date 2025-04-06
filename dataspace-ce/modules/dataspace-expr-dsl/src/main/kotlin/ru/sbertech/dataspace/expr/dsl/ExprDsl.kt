package ru.sbertech.dataspace.expr.dsl

import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.primitive.Primitive

object ExprDsl {
    fun value(value: Primitive) = Expr.Value(value)

    val cur get() = Expr.Cur

    val root get() = Expr.Root

    operator fun Expr.get(propertyName: String) = Expr.Property(this, propertyName)

    infix fun Expr.eq(expr: Expr) = Expr.Eq(this, expr)

    infix fun Expr.and(expr: Expr) = Expr.And(this, expr)

    infix fun Expr.or(expr: Expr) = Expr.Or(this, expr)
}

inline fun expr(crossinline create: ExprDsl.() -> Expr): Expr = ExprDsl.create()
