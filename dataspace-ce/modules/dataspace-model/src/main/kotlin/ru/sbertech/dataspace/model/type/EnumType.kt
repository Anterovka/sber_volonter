package ru.sbertech.dataspace.model.type

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.EnumValue
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.validateUniqueValue

class EnumType private constructor() : Type() {
    val values: Collection<EnumValue> get() = valueByName.values

    private lateinit var valueByName: MutableMap<String, EnumValue>

    fun value(name: String): EnumValue = valueByName[name] ?: throw IllegalArgumentException("Enum value '$name' is not found")

    override fun <P, R> accept(
        visitor: TypeParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : Type.Builder() {
        var values: MutableCollection<EnumValue.Builder>? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : Type.Builder.Internal() {
            private lateinit var attributePathByValueNameForValidate: MutableMap<String, () -> String>

            override lateinit var result: EnumType
                private set

            override val meta get() = Meta

            override val about get() = "Enum type '$name'"

            fun value(name: String): EnumValue.Builder? = values?.find { it.name == name }

            fun validateUniqueValueName(
                errors: MutableCollection<ModelError>,
                valueName: String,
                valueNameAttributePath: () -> String,
            ) {
                validateUniqueValue(errors, valueName, valueNameAttributePath, attributePathByValueNameForValidate)
            }

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.values = values?.mapTo(arrayListOf()) { it.clone() }
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                when (goal) {
                    Goal.VALIDATE -> attributePathByValueNameForValidate = linkedMapOf()
                    Goal.BUILD -> {}
                }
            }

            override fun createResult() {
                super.createResult()
                result = EnumType()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.valueByName =
                    values
                        ?.asSequence()
                        .orEmpty()
                        .map { it.internal.result }
                        .associateByTo(linkedMapOf()) { it.name }
            }
        }

        internal object Meta : Type.Builder.Meta<Builder>() {
            val values: Relation<Builder> = buildersRelation(VALUES_ATTRIBUTE) { it.values }
        }

        companion object {
            internal const val VALUES_ATTRIBUTE: String = "values"
        }
    }
}
