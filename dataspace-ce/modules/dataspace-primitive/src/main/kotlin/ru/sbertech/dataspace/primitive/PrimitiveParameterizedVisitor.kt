package ru.sbertech.dataspace.primitive

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

interface PrimitiveParameterizedVisitor<in P, out R> {
    fun visit(
        type: PrimitiveType,
        value: Primitive,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        char: Char,
        param: P,
    ): R = visit(PrimitiveType.Char, char, param)

    fun visit(
        string: String,
        param: P,
    ): R = visit(PrimitiveType.String, string, param)

    fun visit(
        text: Text,
        param: P,
    ): R = visit(PrimitiveType.Text, text, param)

    fun visit(
        byte: Byte,
        param: P,
    ): R = visit(PrimitiveType.Byte, byte, param)

    fun visit(
        short: Short,
        param: P,
    ): R = visit(PrimitiveType.Short, short, param)

    fun visit(
        int: Int,
        param: P,
    ): R = visit(PrimitiveType.Int, int, param)

    fun visit(
        long: Long,
        param: P,
    ): R = visit(PrimitiveType.Long, long, param)

    fun visit(
        float: Float,
        param: P,
    ): R = visit(PrimitiveType.Float, float, param)

    fun visit(
        double: Double,
        param: P,
    ): R = visit(PrimitiveType.Double, double, param)

    fun visit(
        bigDecimal: BigDecimal,
        param: P,
    ): R = visit(PrimitiveType.BigDecimal, bigDecimal, param)

    fun visit(
        localDate: LocalDate,
        param: P,
    ): R = visit(PrimitiveType.LocalDate, localDate, param)

    fun visit(
        localTime: LocalTime,
        param: P,
    ): R = visit(PrimitiveType.LocalTime, localTime, param)

    fun visit(
        localDateTime: LocalDateTime,
        param: P,
    ): R = visit(PrimitiveType.LocalDateTime, localDateTime, param)

    fun visit(
        offsetDateTime: OffsetDateTime,
        param: P,
    ): R = visit(PrimitiveType.OffsetDateTime, offsetDateTime, param)

    fun visit(
        boolean: Boolean,
        param: P,
    ): R = visit(PrimitiveType.Boolean, boolean, param)

    fun visit(
        byteArray: ByteArray,
        param: P,
    ): R = visit(PrimitiveType.ByteArray, byteArray, param)
}
