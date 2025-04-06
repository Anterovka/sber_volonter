package ru.sbertech.dataspace.uow.packet.status

import ru.sbertech.dataspace.StatusModel
import ru.sbertech.dataspace.StatusProperty
import ru.sbertech.dataspace.common.onFalse
import ru.sbertech.dataspace.data.Status
import ru.sbertech.dataspace.data.StatusCache
import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.dsl.expr
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.statusInfo
import ru.sbertech.dataspace.statusModel
import ru.sbertech.dataspace.statusProperty
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandVisitor
import kotlin.collections.component1
import kotlin.collections.component2

class StatusContext private constructor(
    private val statusModel: StatusModel,
    private val entityManager: EntityManager,
) : CommandVisitor<Unit> {
    companion object {
        fun create(
            model: Model,
            entityManager: EntityManager,
        ): StatusContext? {
            val statusModel = model.statusModel ?: return null

            return StatusContext(statusModel, entityManager)
        }
    }

    override fun visit(
        createCommand: Command.Create,
        param: Unit,
    ) {
        val entityType = createCommand.entityType
        val statusCache = statusModel.statusCacheByType[entityType] ?: return
        val valueByStatusProperty = getValueByStatusProperty(createCommand.propertyValueByName, entityType) ?: emptyMap()

        setInitialStatusValuesByDefault(entityType, valueByStatusProperty, statusCache, createCommand)

        valueByStatusProperty.forEach { (statusProperty, value) ->
            val statusCode = value[statusModel.statusType.codeProperty.name]
            val status = getStatus(statusCache, statusProperty, entityType, statusCode)

            status.isInitial.onFalse {
                throw IllegalArgumentException(
                    "Status with group '${statusProperty.group.code}' and code '$statusCode' for type '${createCommand.entityType.name}'" +
                        " cannot be used as initial",
                )
            }
        }
    }

    override fun visit(
        updateCommand: Command.Update,
        param: Unit,
    ) {
        entityManager.flush()

        val entityType = updateCommand.entityType
        val statusCache = statusModel.statusCacheByType[entityType] ?: return

        val valueByStatusProperty = getValueByStatusProperty(updateCommand.propertyValueByName, entityType) ?: return

        val entityIdentifier = updateCommand.propertyValueByName[entityType.tableIdProperty.name]!!

        val codePropertyName = statusModel.statusType.codeProperty.name
        val findEntityByIdSelector =
            Selector.EntityCollectionBased(
                entityType.name,
                valueByStatusProperty.keys.associate {
                    it.property.name to
                        Selector.PropertyBased(
                            it.property.name,
                            mapOf(
                                codePropertyName to
                                    Selector.PropertyBased(
                                        codePropertyName,
                                    ),
                            ),
                        )
                },
                expr {
                    cur[entityType.tableIdProperty.name] eq
                        value(
                            entityIdentifier,
                        )
                },
            )

        val entityAsMap = findEntityByIdentifier(findEntityByIdSelector, entityType, entityIdentifier, true)

        valueByStatusProperty.forEach { (statusProperty, value) ->
            val newStatusCode = value[codePropertyName]
            val newStatus = getStatus(statusCache, statusProperty, entityType, newStatusCode)

            val currentStatusCode = (entityAsMap[statusProperty.property.name] as Map<String, String>)[codePropertyName]

            val currentStatus =
                entityType.statusInfo?.statuses?.firstOrNull { it.group == newStatus.group && it.code == currentStatusCode }
                    ?: throw IllegalStateException(
                        "Current status for type '${entityType.name}' with code '$currentStatusCode' " +
                            "and group '${statusProperty.group.code}' is not defined in the status model",
                    )

            if (!currentStatus.isTransitionDefined(newStatus)) {
                throw IllegalArgumentException(
                    "Transition from status code '$currentStatusCode' to status code '$newStatusCode' " +
                        "in the '${statusProperty.group.code}' status group does not defined",
                )
            }

            if (newStatusCode == currentStatusCode) {
                return@forEach
            }
        }
    }

    override fun visit(
        deleteCommand: Command.Delete,
        param: Unit,
    ) {
        // do nothing
    }

    override fun visit(
        updateOrCreateCommand: Command.UpdateOrCreate,
        param: Unit,
    ) {
        // do nothing
    }

    override fun visit(
        getCommand: Command.Get,
        param: Unit,
    ) {
        // do nothing
    }

    override fun visit(
        manyCommand: Command.Many,
        param: Unit,
    ) {
        // do nothing
    }

    // TODO вынести в отдельный слой
    private fun findEntityByIdentifier(
        selector: Selector,
        entityType: EntityType,
        identifier: UniversalValue,
        lock: Boolean,
    ): Map<String, UniversalValue> {
        if (lock) {
            entityManager.lock(entityType.name, identifier)
        }
        val result =
            (entityManager.select(selector) as Collection<*>).takeIf { it.isNotEmpty() }
                ?: throw IllegalStateException(
                    "The Entity with type: ${entityType.name} and identifier: $identifier doesn't exists",
                )

        @Suppress("UNCHECKED_CAST")
        return result.iterator().next() as Map<String, UniversalValue>
    }

    private fun getValueByStatusProperty(
        propertyValueByName: Map<String, UniversalValue?>,
        entityType: EntityType,
    ): Map<StatusProperty, Map<String, String>>? {
        return propertyValueByName
            .mapNotNull {
                val property = entityType.inheritedPersistableProperty(it.key)
                val statusProperty = property.statusProperty ?: return@mapNotNull null
                // TODO проверить формат value
                return@mapNotNull statusProperty to it.value as Map<String, String>
            }.toMap()
            .takeIf { it.isNotEmpty() }
    }

    private fun getStatus(
        statusCache: StatusCache,
        statusProperty: StatusProperty,
        entityType: EntityType,
        statusCode: String?,
    ): Status {
        val statuses =
            statusCache.statusesByGroup[statusProperty.group.code]
                ?: throw IllegalStateException(
                    "Statuses for type '${entityType.name}' and group '${statusProperty.group.code}' is not defined",
                )

        return statuses.firstOrNull { it.code == statusCode }
            ?: throw IllegalStateException(
                "Status for type '${entityType.name}' with code '$statusCode' " +
                    "and group '${statusProperty.group.code}' is not defined",
            )
    }

    private fun setInitialStatusValuesByDefault(
        entityType: EntityType,
        valueByStatusProperty: Map<StatusProperty, Map<String, String>>,
        statusCache: StatusCache,
        createCommand: Command.Create,
    ) {
        entityType.statusInfo
            ?.statusProperties
            ?.forEach { statusProperty ->
                if (!valueByStatusProperty.containsKey(statusProperty)) {
                    val initialStatus = statusCache.statusesByGroup[statusProperty.group.code]!!.first(Status::isInitial)
                    val value = mapOf(Pair(statusModel.statusType.codeProperty.name, initialStatus.code))
                    createCommand.propertyValueByName[statusProperty.property.name] = value
                }
            }
    }
}
