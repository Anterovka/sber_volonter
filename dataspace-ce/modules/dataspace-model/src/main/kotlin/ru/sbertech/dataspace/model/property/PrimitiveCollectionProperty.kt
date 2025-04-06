package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.primitive.type.PrimitiveType

class PrimitiveCollectionProperty private constructor() : BasicCollectionProperty() {
    lateinit var type: PrimitiveType
        private set

    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : BasicCollectionProperty.Builder() {
        // default String
        var type: PrimitiveType? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : BasicCollectionProperty.Builder.Internal() {
            override lateinit var result: PrimitiveCollectionProperty
                private set

            override val meta get() = Meta

            override val about get() = "Primitive collection property '$name'"

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.type = type
            }

            override fun createResult() {
                super.createResult()
                result = PrimitiveCollectionProperty()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type ?: PrimitiveType.String
            }
        }

        internal object Meta : BasicCollectionProperty.Builder.Meta<Builder>()

        companion object {
            internal const val TYPE_ATTRIBUTE: String = "type"
        }
    }

    object Override {
        class Builder : BasicCollectionProperty.Override.Builder() {
            override val internal = Internal()

            override fun clone() = Builder().also { internal.setCloneProperties(it) }

            internal inner class Internal : BasicCollectionProperty.Override.Builder.Internal() {
                override val meta get() = Meta

                override val about get() = "Primitive collection property '$propertyName' override"

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    correspondingProperty
                        ?.also { if (it !is PrimitiveCollectionProperty.Builder) errors += ModelError.N13(path(), it.internal.path()) }
                }
            }

            internal object Meta : BasicCollectionProperty.Override.Builder.Meta<Builder>()
        }
    }
}
