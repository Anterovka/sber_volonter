package ru.sbertech.dataspace.primitive.type

interface PrimitiveTypeParameterizedVisitor<in P, out R> {
    fun visit(
        charType: PrimitiveType.Char,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        stringType: PrimitiveType.String,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        textType: PrimitiveType.Text,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        byteType: PrimitiveType.Byte,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        shortType: PrimitiveType.Short,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        intType: PrimitiveType.Int,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        longType: PrimitiveType.Long,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        floatType: PrimitiveType.Float,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        doubleType: PrimitiveType.Double,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        bigDecimalType: PrimitiveType.BigDecimal,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        localDateType: PrimitiveType.LocalDate,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        localTimeType: PrimitiveType.LocalTime,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        localDateTimeType: PrimitiveType.LocalDateTime,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        offsetDateTimeType: PrimitiveType.OffsetDateTime,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        booleanType: PrimitiveType.Boolean,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        byteArrayType: PrimitiveType.ByteArray,
        param: P,
    ): R = throw UnsupportedOperationException()
}
