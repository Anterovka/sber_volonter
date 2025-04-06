package ru.sbertech.dataspace

import ru.sbertech.dataspace.data.Status
import ru.sbertech.dataspace.data.StatusGroup
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType
import java.util.WeakHashMap

class EntityWithStatus {
    lateinit var type: EntityType
        private set
    lateinit var statusProperties: List<StatusProperty>
        private set
    lateinit var statuses: List<Status>
        private set

    override fun toString(): String = "EntityWithStatus(type=${type.name})"

    class Builder {
        var entityType: String? = null
        var statusProperties: MutableCollection<StatusProperty.Builder>? = null
        var statuses: MutableCollection<Status.Builder>? = null

        fun build(
            model: Model,
            statusGroups: List<StatusGroup>,
            statusModelExtension: WeakHashMap<Any, Any>,
        ): EntityWithStatus {
            val entityWithStatus = EntityWithStatus()

            entityWithStatus.type = model.type(entityType!!) as EntityType

            entityWithStatus.statusProperties =
                statusProperties!!.map {
                    val statusProperty = it.build(entityWithStatus.type, statusGroups)
                    statusModelExtension.getOrPut(statusProperty.property) { statusProperty } as StatusProperty
                }

            val statuses = arrayListOf<Status>()
            statuses.addAll(this.statuses?.map { it.build(entityWithStatus.type, statusGroups, statuses) } ?: emptyList())

            entityWithStatus.statuses = statuses

            return entityWithStatus
        }
    }
}
