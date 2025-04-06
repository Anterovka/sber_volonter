package ru.sbertech.dataspace.primitive

import ru.sbertech.dataspace.primitive.type.PrimitiveType

internal object TypeReturningVisitor : PrimitiveVisitor<PrimitiveType> {
    override fun visit(
        type: PrimitiveType,
        value: Primitive,
        param: Unit,
    ) = type
}
