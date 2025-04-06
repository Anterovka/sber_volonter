package ru.sbertech.dataspace

import ru.sbertech.dataspace.data.StatusGroup
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.EntityType

class StatusProperty {
    lateinit var property: Property
        private set

    lateinit var group: StatusGroup
        private set

    override fun toString(): String = "StatusProperty(property=${property.name}, group=${group.code})"

    class Builder {
        var propertyName: String? = null

        var groupCode: String? = null

        fun build(
            entityType: EntityType,
            statusGroups: List<StatusGroup>,
        ): StatusProperty {
            val statusProperty = StatusProperty()

            statusProperty.property = entityType.inheritedPersistableProperty(propertyName!!)
            statusProperty.group = statusGroups.firstOrNull { it.code == groupCode }
                ?: throw IllegalArgumentException(
                    "StatusGroup with code: '$groupCode' not found for StatusProperty '$propertyName'",
                )

            return statusProperty
        }
    }
}
