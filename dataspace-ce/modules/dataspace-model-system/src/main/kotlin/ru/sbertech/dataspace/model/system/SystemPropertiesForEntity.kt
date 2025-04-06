package ru.sbertech.dataspace.model.system

import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.StructuredType

class SystemPropertiesForEntity {
    lateinit var systemProperties: Collection<Property>
        private set

    class Builder {
        var entity: String? = null
        var systemProperties: MutableCollection<String>? = null

        fun build(model: Model): SystemPropertiesForEntity {
            val systemPropertiesForEntity = SystemPropertiesForEntity()

            val entityType = model.type(entity!!) as StructuredType

            systemPropertiesForEntity.systemProperties =
                systemProperties?.mapTo(arrayListOf()) {
                    entityType.property(it)
                } ?: emptyList()

            return systemPropertiesForEntity
        }
    }
}
