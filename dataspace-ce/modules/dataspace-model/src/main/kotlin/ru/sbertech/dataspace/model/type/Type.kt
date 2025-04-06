package ru.sbertech.dataspace.model.type

import ru.sbertech.dataspace.model.Component
import ru.sbertech.dataspace.model.ModelError

sealed class Type : Component() {
    abstract fun <P, R> accept(
        visitor: TypeParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: TypeVisitor<R>): R = accept(visitor, Unit)

    inline fun <reified T : Type> cast(): T = this as? T ?: throw IllegalArgumentException("Type '$name' is not an TODO")

    sealed class Builder : Component.Builder() {
        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : Component.Builder.Internal() {
            abstract override val result: Type

            abstract override val meta: Meta<Builder>

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                name?.also { model.internal.validateUniqueTypeName(errors, it) { path(NAME_ATTRIBUTE) } }
            }
        }

        internal abstract class Meta<out B : Builder> : Component.Builder.Meta<B>() {
            companion object : Meta<Builder>()
        }
    }
}
