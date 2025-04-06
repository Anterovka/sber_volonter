package ru.sbertech.dataspace.uow.packet.aggregate

import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.dsl.expr
import ru.sbertech.dataspace.model.aggregates.Aggregate
import ru.sbertech.dataspace.model.aggregates.AggregatesModel
import ru.sbertech.dataspace.model.dictionaries.isDictionary
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandVisitor

class OptimisticLockContext private constructor(
    private val aggregateVersion: AggregateVersion,
    private val aggregateContext: AggregateContext,
    private val entityManager: EntityManager,
    private val aggregatesModel: AggregatesModel,
) : CommandVisitor<Unit> {
    var currentAggregateVersion: Long? = null

    companion object {
        fun create(
            aggregateVersion: AggregateVersion,
            aggregateContext: AggregateContext?,
            entityManager: EntityManager,
        ): OptimisticLockContext? {
            aggregateContext ?: return null

            return OptimisticLockContext(
                aggregateVersion,
                aggregateContext,
                entityManager,
                aggregateContext.aggregatesModel,
            )
        }
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

    private fun checkAggregateVersion() {
        if (!aggregateVersion.isNeedCheck()) {
            return
        }

        val expectedAggregateVersion = aggregateVersion.expectedAggregateVersion
        if (currentAggregateVersion != expectedAggregateVersion) {
            throw IllegalStateException(
                "Optimistic lock exception: the expected aggregateVersion value is $expectedAggregateVersion" +
                    " but the actual aggregateVersion value is $currentAggregateVersion",
            )
        }
    }

    private fun readAggregateVersion(
        aggregateType: EntityType,
        aggregateVersionFieldName: String,
        aggregateIdentifier: UniversalValue,
        isChanged: Boolean,
    ): Long {
        val findAggregateByIdSelector =
            Selector.EntityCollectionBased(
                aggregateType.name,
                linkedMapOf(
                    aggregateVersionFieldName to
                        Selector.PropertyBased(aggregateVersionFieldName),
                ),
                expr { cur[aggregateType.tableIdProperty.name] eq value(aggregateIdentifier) },
            )

        val aggregateAsMap =
            if (isChanged) {
                findEntityByIdentifier(findAggregateByIdSelector, aggregateType, aggregateIdentifier, true)
            } else {
                findEntityByIdentifier(findAggregateByIdSelector, aggregateType, aggregateIdentifier, false)
            }

        return aggregateAsMap[aggregateVersionFieldName] as Long? ?: 0L
    }

    private fun updateAggregateVersions() {
        aggregateContext.aggregateStates.filter { it.value.isChanged && !it.value.isAggregateRootDeleted }.forEach {
            val propertyValueByName = linkedMapOf<String, UniversalValue?>()
            val aggregateType = it.key.type
            val aggregateVersionFieldName =
                (
                    aggregatesModel.aggregateOrLeaf(
                        aggregateType.name,
                    ) as Aggregate
                ).aggregateVersionPropertyName
            val aggregateIdentifier = it.key.identifier

            val currentAggregateVersion = readAggregateVersion(aggregateType, aggregateVersionFieldName, aggregateIdentifier, true)
            propertyValueByName[aggregateVersionFieldName] = currentAggregateVersion.plus(1)
            entityManager.update(aggregateType.name, aggregateIdentifier, propertyValueByName)
        }
    }

    fun flush() {
        if (aggregateContext.aggregateStates.all { !it.value.isChanged } && !aggregateVersion.isRequested()) {
            return
        }

        if (aggregateContext.aggregateStates.all { !it.value.isChanged } && aggregateVersion.isNeedCheck()) {
            throw IllegalArgumentException("Only request of the aggregate version is allowed for this packet")
        }

        if (aggregateContext.aggregateStates.size > 1 &&
            (
                aggregateContext.aggregateStates.count { it.value.isChanged } > 1 ||
                    aggregateContext.aggregateStates.all { !it.value.isChanged }
            ) &&
            aggregateVersion.isRequested()
        ) {
            throw IllegalStateException(
                "Aggregate version usage is not allowed for packet with many aggregates",
            )
        }

        val aggregateIdentifier =
            aggregateContext.aggregateIdentifier
                ?: throw IllegalStateException("Aggregate root is not defined: aggregate version usage is not allowed")

        val aggregate = aggregateContext.aggregate
        val aggregateType = aggregate.type
        val aggregateVersionPropertyName = aggregate.aggregateVersionPropertyName

        if (aggregateType.isDictionary) {
            return
        }

        if (aggregateContext.aggregateStates.count { it.value.isChanged } == 1 || aggregateContext.aggregateStates.size == 1) {
            val aggregateState =
                aggregateContext.aggregateStates.values.firstOrNull { it.isChanged } ?: aggregateContext.aggregateStates.values.first()

            if (currentAggregateVersion == null) {
                currentAggregateVersion =
                    readAggregateVersion(
                        aggregateType,
                        aggregateVersionPropertyName,
                        aggregateIdentifier,
                        aggregateState.isChanged,
                    )
            }

            checkAggregateVersion()

            if (aggregateState.isChanged) {
                val propertyValueByName = linkedMapOf<String, UniversalValue?>()
                currentAggregateVersion = currentAggregateVersion?.plus(1)
                propertyValueByName[aggregateVersionPropertyName] = currentAggregateVersion
                if (!aggregateState.isAggregateRootDeleted) {
                    entityManager.update(aggregateType.name, aggregateIdentifier, propertyValueByName)
                }
            }
        } else {
            updateAggregateVersions()
        }
    }

    override fun visit(
        createCommand: Command.Create,
        param: Unit,
    ) {
        // TODO оптимизация в случае вставки корня?
        // do nothing
    }

    override fun visit(
        updateCommand: Command.Update,
        param: Unit,
    ) {
        // do nothing
    }

    override fun visit(
        deleteCommand: Command.Delete,
        param: Unit,
    ) {
        if (aggregateContext.aggregateStates.size > 1) {
            return
        }

        val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(deleteCommand.entityType.name)
        if (aggregateOrLeaf is Aggregate) {
            currentAggregateVersion =
                readAggregateVersion(
                    aggregateContext.aggregate.type,
                    aggregateContext.aggregate.aggregateVersionPropertyName,
                    deleteCommand.identifier,
                    true,
                )
        }
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
}
