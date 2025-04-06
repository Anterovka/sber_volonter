package ru.sbertech.dataspace.universalvalue.type

interface UniversalValueTypeParameterizedVisitor<in P, out R> {
    fun visit(
        type: UniversalValueType.Primitive,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        objectType: UniversalValueType.Object,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        collectionType: UniversalValueType.Collection,
        param: P,
    ): R = throw UnsupportedOperationException()
}
