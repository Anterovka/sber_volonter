package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType

class EmbeddedType private constructor() {
    lateinit var embeddableType: EmbeddableType
        private set

    val properties: Collection<Property> get() = propertyByName.values

    // может быть недоступно (пример, коллекция embedded)
    lateinit var idProperty: Property
        private set

    private lateinit var propertyByName: Map<String, Property>

    fun property(name: String): Property = propertyByName[name] ?: throw IllegalArgumentException("Property '$name' is not found")

    internal class Builder : AbstractBuilder() {
        var embeddableType: EmbeddableType.Builder? = null

        var isPersistable: Boolean = false

        var owningEntityType: EntityType.Builder? = null

        var isId: Boolean = false

        var properties: MutableCollection<Property.Builder>? = null

        var idProperty: Property.Builder? = null

        var table: String? = null

        override val internal = Internal()

        override fun clone() = throw UnsupportedOperationException()

        internal inner class Internal : AbstractBuilder.Internal() {
            override lateinit var result: EmbeddedType
                private set

            override val meta get() = Meta

            override val about get() = "Embedded type"

            override val doCreateResult get() = super.doCreateResult && isPersistable

            fun property(name: String): Property.Builder? = properties?.find { it.name == name }

            override fun createResult() {
                super.createResult()
                result = EmbeddedType()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.embeddableType = embeddableType!!.internal.result
                result.propertyByName =
                    properties
                        ?.asSequence()
                        .orEmpty()
                        .map { it.internal.result }
                        .associateByTo(linkedMapOf()) { it.name }
                idProperty?.also { result.idProperty = it.internal.result }
            }
        }

        internal object Meta : AbstractBuilder.Meta<Builder>() {
            val properties: Relation<Builder> = buildersRelation(PROPERTIES_ATTRIBUTE) { it.properties }
        }

        companion object {
            internal const val EMBEDDABLE_TYPE_ATTRIBUTE: String = "embeddable type"

            internal const val IS_PERSISTABLE_ATTRIBUTE: String = "is persistable"

            internal const val IS_FOR_ID_ATTRIBUTE: String = "is for id"

            internal const val PROPERTIES_ATTRIBUTE: String = "properties"

            internal const val ID_PROPERTY_ATTRIBUTE: String = "id property"

            internal const val TABLE_ATTRIBUTE: String = "table"
        }
    }
}
