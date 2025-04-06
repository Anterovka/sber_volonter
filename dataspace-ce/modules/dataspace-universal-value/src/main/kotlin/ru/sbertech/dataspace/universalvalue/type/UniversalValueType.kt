package ru.sbertech.dataspace.universalvalue.type

import ru.sbertech.dataspace.primitive.type.PrimitiveType

sealed class UniversalValueType {
    abstract fun <P, R> accept(
        visitor: UniversalValueTypeParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: UniversalValueTypeVisitor<R>) = accept(visitor, Unit)

    data object Object : UniversalValueType() {
        override fun <P, R> accept(
            visitor: UniversalValueTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Collection : UniversalValueType() {
        override fun <P, R> accept(
            visitor: UniversalValueTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Primitive(
        val type: PrimitiveType,
    ) : UniversalValueType() {
        override fun <P, R> accept(
            visitor: UniversalValueTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
