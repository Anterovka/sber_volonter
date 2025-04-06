package ru.sbertech.dataspace.universalvalue

import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.universalvalue.type.UniversalValueType
import ru.sbertech.dataspace.primitive.accept as acceptAsPrimitive

typealias UniversalValue = Any

val UniversalValue.type: UniversalValueType get() = accept(TypeReturningVisitor)

fun <P, R> UniversalValue.accept(
    visitor: UniversalValueParameterizedVisitor<P, R>,
    param: P,
    doIfNotUniversalValue: (thisRef: Any) -> R = { throw IllegalArgumentException() },
): R =
    when (this) {
        is Map<*, *> -> visitor.visit(uncheckedCast<Map<String, UniversalValue?>>(), param)
        is Collection<*> -> visitor.visit(uncheckedCast<Collection<UniversalValue?>>(), param)
        else -> acceptAsPrimitive(visitor, param, doIfNotUniversalValue)
    }

fun <R> UniversalValue.accept(visitor: UniversalValueVisitor<R>): R = accept(visitor, Unit)
