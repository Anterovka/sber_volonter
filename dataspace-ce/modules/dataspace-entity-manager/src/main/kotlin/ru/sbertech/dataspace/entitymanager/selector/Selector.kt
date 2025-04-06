package ru.sbertech.dataspace.entitymanager.selector

sealed class Selector {
    abstract fun <P, R> accept(
        visitor: SelectorParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: SelectorVisitor<R>): R = accept(visitor, Unit)

    data class EntityCollectionBased(
        val typeName: String,
        val selectorByName: Map<String, Selector> = emptyMap(),
        val cond: ru.sbertech.dataspace.expr.Expr? = null,
    ) : Selector() {
        override fun <P, R> accept(
            visitor: SelectorParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class PropertyBased(
        val name: String,
        val selectorByName: Map<String, Selector> = emptyMap(),
        val cond: ru.sbertech.dataspace.expr.Expr? = null,
    ) : Selector() {
        override fun <P, R> accept(
            visitor: SelectorParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Expr(
        val expr: ru.sbertech.dataspace.expr.Expr,
    ) : Selector() {
        override fun <P, R> accept(
            visitor: SelectorParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data class Group(
        val selectorByName: Map<String, Selector> = emptyMap(),
    ) : Selector() {
        override fun <P, R> accept(
            visitor: SelectorParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }

    data object ApproximateType : Selector() {
        override fun <P, R> accept(
            visitor: SelectorParameterizedVisitor<P, R>,
            param: P,
        ) = visitor.visit(this, param)
    }
}
