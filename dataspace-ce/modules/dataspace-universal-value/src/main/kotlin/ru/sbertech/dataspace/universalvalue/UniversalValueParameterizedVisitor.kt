package ru.sbertech.dataspace.universalvalue

import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.PrimitiveParameterizedVisitor
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.universalvalue.type.UniversalValueType

interface UniversalValueParameterizedVisitor<in P, out R> : PrimitiveParameterizedVisitor<P, R> {
    fun visit(
        type: UniversalValueType,
        value: UniversalValue,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        object0: Map<String, UniversalValue?>,
        param: P,
    ): R = visit(UniversalValueType.Object, object0, param)

    fun visit(
        collection: Collection<UniversalValue?>,
        param: P,
    ): R = visit(UniversalValueType.Collection, collection, param)

    override fun visit(
        type: PrimitiveType,
        value: Primitive,
        param: P,
    ) = visit(UniversalValueType.Primitive(type), value, param)
}
