package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.ModelError

sealed class BasicCollectionProperty : CollectionProperty() {
    // доступно, если isPersistable
    lateinit var elementColumn: String
        private set

    abstract class Builder : CollectionProperty.Builder() {
        var elementColumn: String? = null

        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : CollectionProperty.Builder.Internal() {
            abstract override val result: BasicCollectionProperty

            abstract override val meta: Meta<Builder>

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.elementColumn = elementColumn
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val elementColumn = elementColumn
                if (isPersistable) {
                    when {
                        elementColumn == null -> errors += ModelError.N1(path(ELEMENT_COLUMN_ATTRIBUTE))
                        elementColumn.isBlank() -> errors += ModelError.N4(path(ELEMENT_COLUMN_ATTRIBUTE))
                        else ->
                            table?.also {
                                model.internal.validateUniqueColumn(errors, it, elementColumn) { path(ELEMENT_COLUMN_ATTRIBUTE) }
                            }
                    }
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                if (isPersistable) result.elementColumn = elementColumn!!
            }
        }

        internal abstract class Meta<out B : Builder> : CollectionProperty.Builder.Meta<B>() {
            companion object : Meta<Builder>()
        }

        companion object {
            internal const val ELEMENT_COLUMN_ATTRIBUTE: String = "element column"
        }
    }

    object Override {
        sealed class Builder : CollectionProperty.Override.Builder() {
            var elementColumn: String? = null

            abstract override val internal: Internal

            abstract override fun clone(): Builder

            internal abstract inner class Internal : CollectionProperty.Override.Builder.Internal() {
                abstract override val meta: Meta<Builder>

                override fun applyTo(property: Property.Builder) {
                    super.applyTo(property)
                    if (property is BasicCollectionProperty.Builder) elementColumn?.also { property.elementColumn = it }
                }

                override fun setCloneProperties(clone: AbstractBuilder) {
                    super.setCloneProperties(clone as Builder)
                    clone.elementColumn = elementColumn
                }
            }

            internal abstract class Meta<out B : Builder> : CollectionProperty.Override.Builder.Meta<B>() {
                companion object : Meta<Builder>()
            }

            companion object {
                internal const val ELEMENT_COLUMN_ATTRIBUTE: String = "element column"
            }
        }
    }
}
