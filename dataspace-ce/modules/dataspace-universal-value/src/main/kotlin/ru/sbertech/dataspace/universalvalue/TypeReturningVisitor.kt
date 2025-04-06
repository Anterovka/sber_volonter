package ru.sbertech.dataspace.universalvalue

import ru.sbertech.dataspace.universalvalue.type.UniversalValueType

internal object TypeReturningVisitor : UniversalValueVisitor<UniversalValueType> {
    override fun visit(
        type: UniversalValueType,
        value: UniversalValue,
        param: Unit,
    ) = type
}
