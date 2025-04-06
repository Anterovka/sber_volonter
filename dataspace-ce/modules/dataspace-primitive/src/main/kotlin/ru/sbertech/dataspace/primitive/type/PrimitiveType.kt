package ru.sbertech.dataspace.primitive.type

sealed class PrimitiveType {
    abstract fun <P, R> accept(
        visitor: PrimitiveTypeParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: PrimitiveTypeVisitor<R>): R = accept(visitor, Unit)

    data object Char : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object String : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Text : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Byte : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Short : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Int : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Long : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Float : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Double : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object BigDecimal : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object LocalDate : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object LocalTime : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object LocalDateTime : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object OffsetDateTime : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object Boolean : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object ByteArray : PrimitiveType() {
        override fun <P, R> accept(
            visitor: PrimitiveTypeParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
