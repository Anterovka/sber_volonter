package ru.sbertech.dataspace.primitive

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

typealias Primitive = Any

val Primitive.type: PrimitiveType get() = accept(TypeReturningVisitor)

fun <P, R> Primitive.accept(
    visitor: PrimitiveParameterizedVisitor<P, R>,
    param: P,
    doIfNotPrimitive: (thisRef: Any) -> R = { throw IllegalArgumentException() },
): R =
    when (this) {
        is Char -> visitor.visit(this, param)
        is String -> visitor.visit(this, param)
        is Text -> visitor.visit(this, param)
        is Byte -> visitor.visit(this, param)
        is Short -> visitor.visit(this, param)
        is Int -> visitor.visit(this, param)
        is Long -> visitor.visit(this, param)
        is Float -> visitor.visit(this, param)
        is Double -> visitor.visit(this, param)
        is BigDecimal -> visitor.visit(this, param)
        is LocalDate -> visitor.visit(this, param)
        is LocalTime -> visitor.visit(this, param)
        is LocalDateTime -> visitor.visit(this, param)
        is OffsetDateTime -> visitor.visit(this, param)
        is Boolean -> visitor.visit(this, param)
        is ByteArray -> visitor.visit(this, param)
        else -> doIfNotPrimitive(this)
    }

fun <R> Primitive.accept(visitor: PrimitiveVisitor<R>): R = accept(visitor, Unit)
