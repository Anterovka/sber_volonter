package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.common.generateSequence
import ru.sbertech.dataspace.common.uncheckedCast
import java.lang.ref.WeakReference
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * This class represents builder for [ru.sbertech.dataspace.model.Model] and its components.
 *
 * TODO
 *
 * It is not allowed to use same builder in different parts of hierarchy.
 * Thread safety
 */
abstract class AbstractBuilder internal constructor() {
    internal abstract val internal: Internal

    abstract fun clone(): AbstractBuilder

    internal abstract inner class Internal {
        val cloneSource: AbstractBuilder get() = cloneSourceReference.get()!!

        var parent: AbstractBuilder? = null
            private set

        open val result: Any get() = throw IllegalStateException()

        protected abstract val meta: Meta<AbstractBuilder>

        protected abstract val about: String

        protected lateinit var model: Model.Builder
            private set

        protected open val doCreateResult: Boolean get() = true

        private lateinit var cloneSourceReference: WeakReference<AbstractBuilder>

        private var parentRelation: Relation<*>? = null

        open fun setCloneProperties(clone: AbstractBuilder) {
            clone.internal.cloneSourceReference = WeakReference(this@AbstractBuilder)
        }

        inline fun <reified B : AbstractBuilder> belongsTo(relation: Relation<B>): Boolean = parent is B && parentRelation == relation

        fun path(attribute: String? = null): String =
            StringBuilder()
                .apply {
                    append('[')
                    generateSequence(this@AbstractBuilder) { it.internal.parent }
                        .toCollection(arrayListOf())
                        .apply { reverse() }
                        .forEach { builder ->
                            builder.internal.parentRelation?.also { append('/').append(it.attribute) }
                            append('{').append(builder.internal.about).append('}')
                        }
                    attribute?.also { append('/').append(it) }
                    append(']')
                }.toString()

        open fun prepare(
            goal: Goal,
            model: Model.Builder,
            parent: AbstractBuilder?,
            parentRelation: Relation<*>?,
        ) {
            this.model = model
            this.parent = parent
            this.parentRelation = parentRelation
            forEachChild { relation, child -> child.internal.prepare(goal, model, this@AbstractBuilder, relation) }
        }

        protected open fun validate(errors: MutableCollection<ModelError>) {
            forEachChild { _, child -> child.internal.validate(errors) }
        }

        protected open fun createResult() {
            forEachChild { _, child -> if (child.internal.doCreateResult) child.internal.createResult() }
        }

        protected open fun setResultProperties() {
            forEachChild { _, child -> if (child.internal.doCreateResult) child.internal.setResultProperties() }
        }

        private inline fun forEachChild(crossinline action: (relation: Relation<*>, child: AbstractBuilder) -> Unit) {
            meta.relations.forEach { relation -> relation.builders(this@AbstractBuilder).forEach { action(relation, it) } }
        }
    }

    internal abstract class Meta<out B : AbstractBuilder> {
        val relations: Collection<Relation<*>> get() = mutableRelations

        private val mutableRelations: MutableCollection<Relation<*>> = arrayListOf()

        protected inline fun builderRelation(
            attribute: String,
            crossinline propertyValue: (builder: B) -> AbstractBuilder?,
        ): Relation<B> = relation(attribute) { generateSequence(propertyValue(it)) }

        protected inline fun buildersRelation(
            attribute: String,
            crossinline propertyValue: (builder: B) -> Collection<AbstractBuilder>?,
        ): Relation<B> = relation(attribute) { propertyValue(it)?.asSequence().orEmpty() }

        private inline fun relation(
            attribute: String,
            crossinline builders: (builder: B) -> Sequence<AbstractBuilder>,
        ) = object : Relation<B>(attribute) {
            override fun builders(builder: AbstractBuilder) = builders(builder.uncheckedCast())
        }.also { mutableRelations += it }
    }
}

@OptIn(ExperimentalContracts::class)
internal inline fun <reified B : AbstractBuilder> AbstractBuilder.Internal.belongsTo(
    relation: Relation<B>,
    parentForSmartCast: AbstractBuilder?,
): Boolean {
    contract {
        returns(true) implies (parentForSmartCast is B)
    }
    return parent === parentForSmartCast && belongsTo(relation)
}
