package ru.sbertech.dataspace.model.type

interface TypeParameterizedVisitor<in P, out R> {
    fun visit(
        enumType: EnumType,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        embeddableType: EmbeddableType,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        entityType: EntityType,
        param: P,
    ): R = throw UnsupportedOperationException()
}
