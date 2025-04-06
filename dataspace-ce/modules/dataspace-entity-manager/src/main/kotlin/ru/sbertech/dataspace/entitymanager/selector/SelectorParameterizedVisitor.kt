package ru.sbertech.dataspace.entitymanager.selector

interface SelectorParameterizedVisitor<in P, out R> {
    fun visit(
        entityCollectionBasedSelector: Selector.EntityCollectionBased,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        propertyBasedSelector: Selector.PropertyBased,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        expr: Selector.Expr,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        group: Selector.Group,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        approximateType: Selector.ApproximateType,
        param: P,
    ): R = throw UnsupportedOperationException()
}
