package ru.sbertech.dataspace.model.type

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.validateUniqueValue

sealed class StructuredType : Type() {
    val properties: Collection<Property> get() = propertyByName.values

    private lateinit var propertyByName: Map<String, Property>

    fun property(name: String): Property = propertyByName[name] ?: throw IllegalArgumentException("Property '$name' is not found")

    sealed class Builder : Type.Builder() {
        var properties: MutableCollection<Property.Builder>? = null

        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : Type.Builder.Internal() {
            private lateinit var attributePathByPropertyNameForValidate: MutableMap<String, () -> String>

            abstract override val result: StructuredType

            abstract override val meta: Meta<Builder>

            fun property(name: String): Property.Builder? = properties?.find { it.name == name }

            fun property(propertyPath: Collection<String>): Property.Builder? {
                var property: Property.Builder? = null
                if (propertyPath.isNotEmpty()) {
                    property = property(propertyPath.first())
                    propertyPath.asSequence().drop(1).forEach { property = property?.internal?.property(it) }
                }
                return property
            }

            fun validateUniquePropertyName(
                errors: MutableCollection<ModelError>,
                propertyName: String,
                propertyNameAttributePath: () -> String,
            ) {
                validateUniqueValue(errors, propertyName, propertyNameAttributePath, attributePathByPropertyNameForValidate)
            }

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.properties = properties?.mapTo(arrayListOf()) { it.clone() }
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                when (goal) {
                    Goal.VALIDATE -> attributePathByPropertyNameForValidate = linkedMapOf()
                    Goal.BUILD -> {}
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.propertyByName =
                    properties
                        ?.asSequence()
                        .orEmpty()
                        .map { it.internal.result }
                        .associateByTo(linkedMapOf()) { it.name }
            }
        }

        internal abstract class Meta<out B : Builder> : Type.Builder.Meta<B>() {
            val properties: Relation<B> = buildersRelation(PROPERTIES_ATTRIBUTE) { it.properties }

            companion object : Meta<Builder>()
        }

        companion object {
            internal const val PROPERTIES_ATTRIBUTE: String = "properties"
        }
    }
}
