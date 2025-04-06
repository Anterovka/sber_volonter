package ru.sbertech.dataspace.model.property

interface PropertyParameterizedVisitor<in P, out R> {
    fun visit(
        primitiveProperty: PrimitiveProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        enumProperty: EnumProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        embeddedProperty: EmbeddedProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        referenceProperty: ReferenceProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        mappedReferenceProperty: MappedReferenceProperty,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        mappedReferenceCollectionProperty: MappedReferenceCollectionProperty,
        param: P,
    ): R = throw UnsupportedOperationException()
}
