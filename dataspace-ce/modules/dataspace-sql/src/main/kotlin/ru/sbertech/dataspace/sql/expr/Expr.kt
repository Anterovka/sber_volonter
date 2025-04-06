package ru.sbertech.dataspace.sql.expr

import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.SwitchCase
import ru.sbertech.dataspace.sql.UnitOfTime

sealed class Expr {
    abstract fun <P, R> accept(
        visitor: ExprParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: ExprVisitor<R>): R = accept(visitor, Unit)

    data class Null(
        val type: PrimitiveType,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Value(
        val value: Primitive,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Param(
        val name: String,
        val type: PrimitiveType,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Column(
        val name: String,
        val table: String? = null,
        val schema: String? = null,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class SubQuery(
        val subQuery: ru.sbertech.dataspace.sql.subquery.SubQuery,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Neg(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Abs(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Upper(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Lower(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Length(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Trim(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class LTrim(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class RTrim(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Round(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Ceil(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Floor(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Hash(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    // TODO или лучше отдельные методы, т.к. могут быть доп параметры?
    data class Cast(
        val expr: Expr,
        val type: PrimitiveType,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Switch(
        val expr: Expr,
        val cases: Collection<SwitchCase>,
        val elseResult: Expr? = null,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class IsNull(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class IsNotNull(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Exists(
        val subQuery: ru.sbertech.dataspace.sql.subquery.SubQuery,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Min(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Max(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Sum(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Avg(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Count(
        val expr: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Not(
        val expr: Expr,
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

    data class Add(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Sub(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Mul(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Div(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Mod(
        val expr1: Expr,
        val expr2: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class TemporalAdd(
        val temporal: Expr,
        val unitOfTime: UnitOfTime,
        val value: Expr,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class TemporalSub(
        val temporal: Expr,
        val unitOfTime: UnitOfTime,
        val value: Expr,
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

    data class InSubQuery(
        val expr: Expr,
        val subQuery: ru.sbertech.dataspace.sql.subquery.SubQuery,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class InList(
        val expr: Expr,
        val exprs: Collection<Expr>,
    ) : Expr() {
        override fun <P, R> accept(
            visitor: ExprParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
