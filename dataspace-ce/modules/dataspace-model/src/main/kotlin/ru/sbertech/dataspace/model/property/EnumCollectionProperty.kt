package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.property.EnumProperty.Builder
import ru.sbertech.dataspace.model.type.EnumType

class EnumCollectionProperty private constructor() : BasicCollectionProperty() {
    lateinit var type: EnumType
        private set

    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : BasicCollectionProperty.Builder() {
        var typeName: String? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : BasicCollectionProperty.Builder.Internal() {
            var type: EnumType.Builder? = null
                private set

            override lateinit var result: EnumCollectionProperty
                private set

            override val meta get() = Meta

            override val about get() = "Enum collection property '$name'"

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.typeName = typeName
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                type = typeName?.let { model.internal.type(it) as? EnumType.Builder }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val typeName = typeName
                val type = type
                when {
                    typeName == null -> errors += ModelError.N1(path(TYPE_NAME_ATTRIBUTE))
                    type == null ->
                        when (val type2 = model.internal.type(typeName)) {
                            null -> errors += ModelError.N6(path(TYPE_NAME_ATTRIBUTE), typeName, model.internal.path())
                            !is EnumType.Builder -> errors += ModelError.N15(path(TYPE_NAME_ATTRIBUTE), type2.internal.path())
                            else -> {}
                        }
                }
            }

            override fun createResult() {
                super.createResult()
                result = EnumCollectionProperty()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type!!.internal.result
            }
        }

        internal object Meta : BasicCollectionProperty.Builder.Meta<Builder>()

        companion object {
            internal const val TYPE_NAME_ATTRIBUTE: String = "type name"
        }
    }

    object Override {
        class Builder : BasicCollectionProperty.Override.Builder() {
            override val internal = Internal()

            override fun clone() = Builder().also { internal.setCloneProperties(it) }

            internal inner class Internal : BasicCollectionProperty.Override.Builder.Internal() {
                override val meta get() = Meta

                override val about get() = "Enum collection property '$propertyName' override"

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    correspondingProperty
                        ?.also { if (it !is EnumCollectionProperty.Builder) errors += ModelError.N13(path(), it.internal.path()) }
                }
            }

            internal object Meta : BasicCollectionProperty.Override.Builder.Meta<Builder>()
        }
    }
}
