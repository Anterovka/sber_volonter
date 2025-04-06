package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation

sealed class BasicProperty : Property() {
    var isOptional: Boolean = false
        private set

    var isSettableOnCreate: Boolean = false
        private set

    var isSettableOnUpdate: Boolean = false
        private set

    // доступно, если isPersistable
    lateinit var column: String
        private set

    sealed class Builder : Property.Builder() {
        // default true
        var isOptional: Boolean? = null

        // default true
        var isSettableOnCreate: Boolean? = null

        // default true
        var isSettableOnUpdate: Boolean? = null

        var column: String? = null

        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : Property.Builder.Internal() {
            var isOptional: Boolean = false
                private set

            var isSettableOnCreate: Boolean = false
                private set

            var isSettableOnUpdate: Boolean = false
                private set

            override val isSuitableForId get() = true

            abstract override val result: BasicProperty

            abstract override val meta: Meta<Builder>

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.isOptional = this@Builder.isOptional
                clone.isSettableOnCreate = this@Builder.isSettableOnCreate
                clone.isSettableOnUpdate = this@Builder.isSettableOnUpdate
                clone.column = column
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                isOptional = this@Builder.isOptional ?: true
                isSettableOnCreate = this@Builder.isSettableOnCreate ?: true
                isSettableOnUpdate = this@Builder.isSettableOnUpdate ?: true
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val column = column
                if (isPersistable) {
                    when {
                        column == null -> errors += ModelError.N1(path(COLUMN_ATTRIBUTE))
                        column.isBlank() -> errors += ModelError.N4(path(COLUMN_ATTRIBUTE))
                        else -> table?.also { model.internal.validateUniqueColumn(errors, it, column) { path(COLUMN_ATTRIBUTE) } }
                    }
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.isOptional = isOptional
                result.isSettableOnCreate = isSettableOnCreate
                result.isSettableOnUpdate = isSettableOnUpdate
                if (isPersistable) result.column = column!!
            }
        }

        internal abstract class Meta<out B : Builder> : Property.Builder.Meta<B>() {
            companion object : Meta<Builder>()
        }

        companion object {
            internal const val IS_OPTIONAL_ATTRIBUTE: String = "is optional"

            internal const val IS_SETTABLE_ON_CREATE_ATTRIBUTE: String = "is settable on create"

            internal const val IS_SETTABLE_ON_UPDATE_ATTRIBUTE: String = "is settable on update"

            internal const val COLUMN_ATTRIBUTE: String = "column"
        }
    }

    object Override {
        sealed class Builder : Property.Override.Builder() {
            var column: String? = null

            abstract override val internal: Internal

            abstract override fun clone(): Builder

            internal abstract inner class Internal : Property.Override.Builder.Internal() {
                abstract override val meta: Meta<Builder>

                override fun applyTo(property: Property.Builder) {
                    super.applyTo(property)
                    if (property is BasicProperty.Builder) column?.also { property.column = it }
                }

                override fun setCloneProperties(clone: AbstractBuilder) {
                    super.setCloneProperties(clone as Builder)
                    clone.column = column
                }
            }

            internal abstract class Meta<out B : Builder> : Property.Override.Builder.Meta<B>() {
                companion object : Meta<Builder>()
            }

            companion object {
                internal const val COLUMN_ATTRIBUTE: String = "column"
            }
        }
    }
}
