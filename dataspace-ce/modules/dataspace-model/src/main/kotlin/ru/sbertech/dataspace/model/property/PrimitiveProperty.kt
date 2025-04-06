package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.primitive.type as primitiveType

class PrimitiveProperty private constructor() : BasicProperty() {
    lateinit var type: PrimitiveType
        private set

    var defaultValue: Primitive? = null
        private set

    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : BasicProperty.Builder() {
        // default String
        var type: PrimitiveType? = null

        var defaultValue: Primitive? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : BasicProperty.Builder.Internal() {
            lateinit var type: PrimitiveType
                private set

            override lateinit var result: PrimitiveProperty
                private set

            override val meta get() = Meta

            override val about get() = "Primitive property '$name'"

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.type = this@Builder.type
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                type = this@Builder.type ?: PrimitiveType.String
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                defaultValue?.let { defaultValue ->
                    val defaultValueType = defaultValue.primitiveType
                    if (defaultValueType != type) {
                        errors += ModelError.N11(path(DEFAULT_VALUE_ATTRIBUTE), defaultValueType, path(TYPE_ATTRIBUTE), type)
                    }
                }
            }

            override fun createResult() {
                super.createResult()
                result = PrimitiveProperty()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type
                result.defaultValue = defaultValue
            }
        }

        internal object Meta : BasicProperty.Builder.Meta<Builder>()

        companion object {
            internal const val TYPE_ATTRIBUTE: String = "type"

            internal const val DEFAULT_VALUE_ATTRIBUTE: String = "default value"
        }
    }

    object Override {
        class Builder : BasicProperty.Override.Builder() {
            override val internal = Internal()

            override fun clone() = Builder().also { internal.setCloneProperties(it) }

            internal inner class Internal : BasicProperty.Override.Builder.Internal() {
                override val meta get() = Meta

                override val about get() = "Primitive property '$propertyName' override"

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    correspondingProperty
                        ?.also { if (it !is PrimitiveProperty.Builder) errors += ModelError.N13(path(), it.internal.path()) }
                }
            }

            internal object Meta : BasicProperty.Override.Builder.Meta<Builder>()
        }
    }
}
