package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.EnumValue
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.property.ReferenceProperty.Builder
import ru.sbertech.dataspace.model.type.EnumType

class EnumProperty private constructor() : BasicProperty() {
    lateinit var type: EnumType
        private set

    var defaultValue: EnumValue? = null
        private set

    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : BasicProperty.Builder() {
        var typeName: String? = null

        var defaultValueName: String? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : BasicProperty.Builder.Internal() {
            var type: EnumType.Builder? = null
                private set

            var defaultValue: EnumValue.Builder? = null
                private set

            override lateinit var result: EnumProperty
                private set

            override val meta get() = Meta

            override val about get() = "Enum property '$name'"

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.typeName = typeName
                clone.defaultValueName = defaultValueName
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                type = typeName?.let { model.internal.type(it) as? EnumType.Builder }
                defaultValue = defaultValueName?.let { type?.internal?.value(it) }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val typeName = typeName
                val defaultValueName = defaultValueName
                val type = type
                when {
                    typeName == null -> errors += ModelError.N1(path(TYPE_NAME_ATTRIBUTE))
                    type == null ->
                        when (val type2 = model.internal.type(typeName)) {
                            null -> errors += ModelError.N6(path(TYPE_NAME_ATTRIBUTE), typeName, model.internal.path())
                            !is EnumType.Builder -> errors += ModelError.N15(path(TYPE_NAME_ATTRIBUTE), type2.internal.path())
                            else -> {}
                        }

                    defaultValueName != null && defaultValue == null ->
                        errors += ModelError.N16(path(DEFAULT_VALUE_NAME_ATTRIBUTE), defaultValueName, type.internal.path())
                }
            }

            override fun createResult() {
                super.createResult()
                result = EnumProperty()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type!!.internal.result
                result.defaultValue = defaultValue?.internal?.result
            }
        }

        internal object Meta : BasicProperty.Builder.Meta<Builder>()

        companion object {
            internal const val TYPE_NAME_ATTRIBUTE: String = "type name"

            internal const val DEFAULT_VALUE_NAME_ATTRIBUTE: String = "default value name"
        }
    }

    object Override {
        class Builder : BasicProperty.Override.Builder() {
            override val internal = Internal()

            override fun clone() = Builder().also { internal.setCloneProperties(it) }

            internal inner class Internal : BasicProperty.Override.Builder.Internal() {
                override val meta get() = Meta

                override val about get() = "Enum property '$propertyName' override"

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    correspondingProperty
                        ?.also { if (it !is EnumProperty.Builder) errors += ModelError.N13(path(), it.internal.path()) }
                }
            }

            internal object Meta : BasicProperty.Override.Builder.Meta<Builder>()
        }
    }
}
