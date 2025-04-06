package ru.sbertech.dataspace

import ru.sbertech.dataspace.data.Status
import ru.sbertech.dataspace.data.StatusCache
import ru.sbertech.dataspace.data.StatusGroup
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.EntityType
import java.util.WeakHashMap

private val statusModelExtension = WeakHashMap<Any, Any>()

val Model.statusModel: StatusModel? get() = statusModelExtension[this] as StatusModel?
val Property.statusProperty get() = statusModelExtension[this] as StatusProperty?
val EntityType.statusInfo get() = statusModelExtension[this] as EntityWithStatus?

class StatusModel {
    lateinit var statusType: StatusType
        private set
    lateinit var statusGroups: List<StatusGroup>
        private set
    lateinit var statusCacheByType: Map<EntityType, StatusCache>
        private set

    class Builder {
        var statusType: StatusType.Builder? = null
        var entitiesWithStatus: MutableCollection<EntityWithStatus.Builder>? = null

        var statusGroups: MutableCollection<StatusGroup.Builder>? = null

        fun build(model: Model): StatusModel {
            val statusModel = StatusModel()

            statusModel.statusType = statusType!!.build(model)
            statusModel.statusGroups = statusGroups?.map { it.build() }.orEmpty()

            entitiesWithStatus?.forEach {
                val entityWithStatus = it.build(model, statusModel.statusGroups, statusModelExtension)
                statusModelExtension[entityWithStatus.type] = entityWithStatus
            }

            statusModel.statusCacheByType = initStatusCache(model)

            statusModelExtension[model] = statusModel

            return statusModel
        }

        private fun initStatusCache(model: Model): Map<EntityType, StatusCache> {
            val statusCacheByType = HashMap<EntityType, StatusCache>()

            model.types.filterIsInstance<EntityType>().forEach { type ->
                var statusesByGroup: Map<String, List<Status>> = HashMap()
                var currentType: EntityType? = type

                while (currentType != null) {
                    val currentTypeStatusesByGroup =
                        currentType.statusInfo
                            ?.statuses
                            ?.groupBy { it.group.code }
                            ?.toMap() ?: emptyMap()

                    statusesByGroup = currentTypeStatusesByGroup.plus(statusesByGroup)
                    currentType = currentType.parentEntityType
                }

                if (statusesByGroup.isNotEmpty()) {
                    statusCacheByType[type] = StatusCache(statusesByGroup)
                }
            }

            return statusCacheByType
        }
    }
}
