package ru.sbertech.dataspace.uow.packet.aggregate

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.aggregates.Aggregate
import ru.sbertech.dataspace.model.aggregates.AggregatesModel
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.model.aggregates.aggregatesModel
import ru.sbertech.dataspace.model.dictionaries.isDictionary
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandVisitor
import ru.sbertech.dataspace.uow.packet.FeatherUtils
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.sql.Connection

data class AggregateKey(
    val identifier: UniversalValue,
    val type: EntityType,
)

data class AggregateState(
    val isChanged: Boolean,
    var isAggregateRootDeleted: Boolean = false,
)

private data class ParentData(
    val parentAggregateRootPropertyName: String,
    val parentAsMap: Map<String, Any>,
)

class AggregateContext private constructor(
    private val entityManager: EntityManager,
    val aggregate: Aggregate,
    val aggregatesModel: AggregatesModel,
    private val entitiesReadAccessJson: EntitiesReadAccessJson,
    private val connection: Connection,
    private val isManyAggregatesAllowed: Boolean,
) : CommandVisitor<Unit> {
    var aggregateIdentifier: UniversalValue? = null
    var aggregateStates = hashMapOf<AggregateKey, AggregateState>()
    private var isReadOnlyPacket = true

    companion object {
        fun create(
            commands: Collection<Command>,
            model: Model,
            entityManager: EntityManager,
            entitiesReadAccessJson: EntitiesReadAccessJson,
            connection: Connection,
            isManyAggregatesAllowed: Boolean,
        ): AggregateContext? {
            val aggregatesModel = model.aggregatesModel ?: return null

            return commands
                .firstOrNull { it !is Command.Get }
                ?.let { command ->
                    getAggregateContext(
                        aggregatesModel,
                        command,
                        entityManager,
                        entitiesReadAccessJson,
                        connection,
                        isManyAggregatesAllowed,
                    )
                } ?: commands
                .firstOrNull()
                ?.let { command ->
                    getAggregateContext(
                        aggregatesModel,
                        command,
                        entityManager,
                        entitiesReadAccessJson,
                        connection,
                        isManyAggregatesAllowed,
                    )
                }
        }

        private fun getAggregateContext(
            aggregatesModel: AggregatesModel,
            command: Command,
            entityManager: EntityManager,
            entitiesReadAccessJson: EntitiesReadAccessJson,
            connection: Connection,
            isManyAggregatesAllowed: Boolean,
        ) = when (val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(command.entityType.name)) {
            is Aggregate -> {
                AggregateContext(
                    entityManager,
                    aggregateOrLeaf,
                    aggregatesModel,
                    entitiesReadAccessJson,
                    connection,
                    isManyAggregatesAllowed,
                )
            }

            is Leaf -> {
                AggregateContext(
                    entityManager,
                    aggregateOrLeaf.aggregate,
                    aggregatesModel,
                    entitiesReadAccessJson,
                    connection,
                    isManyAggregatesAllowed,
                )
            }

            else -> {
                throw UnsupportedOperationException("Unsupported type for aggregate model: ${aggregatesModel::class}")
            }
        }
    }

    fun setAggregateIdentifier(
        aggregateIdentifier: UniversalValue?,
        aggregateType: EntityType,
        isReadOnlyCommand: Boolean,
        isAggregateRootDeleted: Boolean = false,
    ) {
        if (aggregateIdentifier == null) {
            throw IllegalStateException("Aggregate identifier must not be null")
        }

        if (isReadOnlyCommand && this.aggregateIdentifier != null) {
            aggregateStates.getOrPut(AggregateKey(aggregateIdentifier, aggregateType)) { AggregateState(false, isAggregateRootDeleted) }
            return
        }

        if (!isReadOnlyPacket &&
            ((this.aggregateIdentifier != null && this.aggregateIdentifier != aggregateIdentifier) || aggregate.type != aggregateType)
        ) {
            if (!isManyAggregatesAllowed) {
                throw IllegalStateException(
                    "Too many aggregates: [${aggregate.type.name}, ${this.aggregateIdentifier}], [${aggregateType.name}, $aggregateIdentifier]",
                )
            } else {
                addAggregateState(aggregateIdentifier, aggregateType, AggregateState(true, isAggregateRootDeleted))
                return
            }
        }

        addAggregateState(aggregateIdentifier, aggregateType, AggregateState(!isReadOnlyCommand, isAggregateRootDeleted))

        isReadOnlyPacket = isReadOnlyCommand

        this.aggregateIdentifier = aggregateIdentifier
    }

    private fun addAggregateState(
        aggregateIdentifier: UniversalValue,
        aggregateType: EntityType,
        newAggregateState: AggregateState,
    ) {
        val aggregateKey = AggregateKey(aggregateIdentifier, aggregateType)
        aggregateStates.compute(aggregateKey) { _, aggregateState ->
            if (aggregateState != null && aggregateState.isAggregateRootDeleted) {
                aggregateState
            } else {
                newAggregateState
            }
        }
    }

    private fun getReferenceIdentifier(
        referencePropertyName: String,
        resultAsMap: Map<String, Any>,
    ): Any? {
        val referenceIdentifier =
            if (referencePropertyName != "id") {
                ((resultAsMap["props"] as Map<*, *>)[referencePropertyName] as Map<*, *>)["id"]
            } else {
                resultAsMap["id"]
            }
        return referenceIdentifier
    }

    private fun getParentData(
        parentIdentifier: UniversalValue?,
        parentType: EntityType,
        parentPropertyForParent: Property? = null,
    ): ParentData {
        val parentAggregateRootPropertyName =
            when (val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(parentType.name)) {
                // TODO LEGACY
                is Aggregate -> "id" // aggregateOrLeaf1.type.tableIdProperty.name
                is Leaf -> aggregateOrLeaf.aggregateRootProperty.name
                else -> {
                    throw UnsupportedOperationException("Unsupported type for aggregate model: ${aggregateOrLeaf::class}")
                }
            }

        val props = JsonNodeFactory.instance.arrayNode()
        if (parentAggregateRootPropertyName != "id") {
            props.add(parentAggregateRootPropertyName)
        }
        if (parentPropertyForParent != null) {
            props.add(parentPropertyForParent.name)
        }

        val parentAsMap =
            findEntityByIdentifier(
                parentIdentifier,
                parentType,
                props,
            )
        return ParentData(parentAggregateRootPropertyName, parentAsMap)
    }

    fun findEntityByIdentifier(
        identifier: UniversalValue?,
        entityType: EntityType,
        props: ArrayNode,
        failOnEmpty: Boolean = true,
    ): Map<String, Any> {
        if (identifier == null) {
            throw IllegalStateException("Parent entity's identifier must not be null")
        }

        // TODO LEGACY
        //        val selectResult =
        //            entityManager.select(
        //                Selector.EntityCollectionBased(
        //                    parentType.name,
        //                    mapOf(parentAggregateRootPropertyName to Selector.PropertyBased(parentAggregateRootPropertyName)),
        //                    expr { cur[parentType.tableIdProperty.name] eq value(parentIdentifier) },
        //                ),
        //            ) as Collection<*>

        //        if (selectResult.isEmpty()) {
        //            throw IllegalStateException("The Entity with type: ${parentType.name} and identifier: $parentIdentifier doesn't exists")
        //        }
        //
        //        val aggregateIdentifier = (selectResult.iterator().next() as Map<*, *>)[parentAggregateRootPropertyName]

        val queryNode = JsonNodeFactory.instance.objectNode()
        queryNode.put("type", entityType.name)
        queryNode.put("cond", "root.${'$'}id=='$identifier'")
        queryNode.set<ArrayNode>("props", props)

        val entities = entitiesReadAccessJson.searchEntities(queryNode, connection)
        val result =
            (entities as ObjectNode).get("elems").get(0) ?: if (failOnEmpty) {
                throw IllegalStateException("The Entity with type: ${entityType.name} and identifier: $identifier doesn't exists")
            } else {
                return emptyMap()
            }
        val resultAsMap = FeatherUtils.jsonToMap(result as ObjectNode)
        return resultAsMap
    }

    override fun visit(
        createCommand: Command.Create,
        param: Unit,
    ) {
        val entityType = createCommand.entityType
        val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(entityType.name)

        if (entityType.isDictionary) {
            // TODO
            createCommand.propertyValueByName[(aggregateOrLeaf as Leaf).aggregateRootProperty.name] = "1"
            setAggregateIdentifier("1", aggregateOrLeaf.aggregate.type, false)
            return
        }

        when (aggregateOrLeaf) {
            is Aggregate -> {
                createCommand.propertyValueByName[entityType.tableIdProperty.name]?.also {
                    setAggregateIdentifier(it, aggregateOrLeaf.type, false)
                }
            }

            is Leaf -> {
                if (createCommand.propertyValueByName[aggregateOrLeaf.aggregateRootProperty.name] != null) {
                    return
                }

                val parentIdentifier = createCommand.propertyValueByName[aggregateOrLeaf.parentProperty.name]
                var aggregateIdentifier: UniversalValue? = null

                val parentData =
                    parentIdentifier?.let {
                        val parentData = getParentData(parentIdentifier, (aggregateOrLeaf.parentProperty as ReferenceProperty).type)
                        aggregateIdentifier = getReferenceIdentifier(parentData.parentAggregateRootPropertyName, parentData.parentAsMap)
                        parentData
                    }

                aggregateOrLeaf.treeParentProperty?.also { treeParentProperty ->
                    val treeParentIdentifier = createCommand.propertyValueByName[treeParentProperty.name]
                    treeParentIdentifier?.also {
                        val treeParentType = (treeParentProperty as ReferenceProperty).type
                        val parentPropertyForParent = (aggregatesModel.aggregateOrLeaf(treeParentType.name) as Leaf).parentProperty
                        val threeParentData = getParentData(it, treeParentType, parentPropertyForParent)

                        val parentForTreeParentIdentifier =
                            getReferenceIdentifier(parentPropertyForParent.name, threeParentData.parentAsMap)

                        if (parentData == null) {
                            aggregateIdentifier =
                                getReferenceIdentifier(threeParentData.parentAggregateRootPropertyName, threeParentData.parentAsMap)
                            createCommand.propertyValueByName[aggregateOrLeaf.parentProperty.name] = parentForTreeParentIdentifier
                        } else if (
                            parentForTreeParentIdentifier != parentIdentifier
                        ) {
                            throw IllegalArgumentException(
                                "The parent property '${aggregateOrLeaf.parentProperty.name}' value '$parentIdentifier'" +
                                    " is not match with the property '${treeParentProperty.name}.${parentPropertyForParent.name}'" +
                                    " value '$parentForTreeParentIdentifier'",
                            )
                        }
                    }
                }

                setAggregateIdentifier(aggregateIdentifier, aggregateOrLeaf.aggregate.type, false)
                createCommand.propertyValueByName[aggregateOrLeaf.aggregateRootProperty.name] = this.aggregateIdentifier!!
            }

            else -> {
                throw UnsupportedOperationException("Unsupported type for aggregate model: ${aggregateOrLeaf::class}")
            }
        }
    }

    override fun visit(
        updateCommand: Command.Update,
        param: Unit,
    ) {
        val entityType = updateCommand.entityType
        val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(entityType.name)

        if (entityType.isDictionary) {
            setAggregateIdentifier("1", (aggregateOrLeaf as Leaf).aggregate.type, false)
            return
        }

        when (aggregateOrLeaf) {
            is Aggregate -> {
                val aggregateIdentifier = updateCommand.propertyValueByName[entityType.tableIdProperty.name]!!

                findEntityByIdentifier(
                    aggregateIdentifier,
                    aggregateOrLeaf.type,
                    // TODO LEGACY
                    JsonNodeFactory.instance.arrayNode(),
                )

                setAggregateIdentifier(aggregateIdentifier, aggregateOrLeaf.type, false)
            }

            is Leaf -> {
                val entityIdentifier = updateCommand.propertyValueByName[entityType.tableIdProperty.name]

                val props =
                    JsonNodeFactory.instance
                        .arrayNode()
                        .add(aggregateOrLeaf.aggregateRootProperty.name)
                        .add(aggregateOrLeaf.parentProperty.name)

                aggregateOrLeaf.treeParentProperty?.also { props.add(it.name) }

                val entityAsMap =
                    findEntityByIdentifier(
                        entityIdentifier,
                        entityType,
                        props,
                    )

                val aggregateIdentifier = getReferenceIdentifier(aggregateOrLeaf.aggregateRootProperty.name, entityAsMap)

                var parentIdentifier = updateCommand.propertyValueByName[aggregateOrLeaf.parentProperty.name]
                parentIdentifier?.also {
                    val parentData = getParentData(parentIdentifier, (aggregateOrLeaf.parentProperty as ReferenceProperty).type)
                    val aggregateIdentifierByNewParent =
                        getReferenceIdentifier(parentData.parentAggregateRootPropertyName, parentData.parentAsMap)

                    if (aggregateIdentifierByNewParent != aggregateIdentifier) {
                        throw IllegalArgumentException(
                            "The aggregate from the parent property" +
                                " '${aggregateOrLeaf.parentProperty.name}' ($aggregateIdentifierByNewParent)" +
                                " not match with the current aggregate from the property" +
                                " '${aggregateOrLeaf.aggregateRootProperty.name}' ($aggregateIdentifier)",
                        )
                    }
                }

                aggregateOrLeaf.treeParentProperty?.also {
                    var treeParentIdentifier = updateCommand.propertyValueByName[it.name]

                    if (parentIdentifier != null || treeParentIdentifier != null) {
                        parentIdentifier = parentIdentifier ?: getReferenceIdentifier(aggregateOrLeaf.parentProperty.name, entityAsMap)
                        treeParentIdentifier = treeParentIdentifier ?: getReferenceIdentifier(it.name, entityAsMap)

                        val treeParentType = (it as ReferenceProperty).type
                        val parentPropertyForTreeParent = (aggregatesModel.aggregateOrLeaf(treeParentType.name) as Leaf).parentProperty

                        val treeParentProps =
                            JsonNodeFactory.instance
                                .arrayNode()
                                .add(parentPropertyForTreeParent.name)

                        val treeParentEntityAsMap =
                            findEntityByIdentifier(
                                treeParentIdentifier,
                                treeParentType,
                                treeParentProps,
                            )

                        val parentForTreeParentIdentifier =
                            getReferenceIdentifier(parentPropertyForTreeParent.name, treeParentEntityAsMap)

                        if (parentForTreeParentIdentifier != parentIdentifier) {
                            throw IllegalArgumentException(
                                "The parent property '${aggregateOrLeaf.parentProperty.name}' value '$parentIdentifier'" +
                                    " is not match with the property '${it.name}.${parentPropertyForTreeParent.name}'" +
                                    " value '$parentForTreeParentIdentifier'",
                            )
                        }
                    }
                }

                setAggregateIdentifier(aggregateIdentifier, aggregateOrLeaf.aggregate.type, false)
            }

            else -> {
                throw UnsupportedOperationException("Unsupported type for aggregate model: ${aggregateOrLeaf::class}")
            }
        }
    }

    override fun visit(
        updateOrCreateCommand: Command.UpdateOrCreate,
        param: Unit,
    ) {
        // do nothing
    }

    override fun visit(
        deleteCommand: Command.Delete,
        param: Unit,
    ) {
        val entityType = deleteCommand.entityType
        val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(entityType.name)

        if (entityType.isDictionary) {
            setAggregateIdentifier("1", (aggregateOrLeaf as Leaf).aggregate.type, false)
            return
        }

        when (aggregateOrLeaf) {
            is Aggregate -> {
                val aggregateIdentifier = deleteCommand.identifier

                // TODO LEGACY
                findEntityByIdentifier(
                    aggregateIdentifier,
                    aggregateOrLeaf.type,
                    // TODO LEGACY
                    JsonNodeFactory.instance.arrayNode(),
                )

                setAggregateIdentifier(aggregateIdentifier, aggregateOrLeaf.type, isReadOnlyCommand = false, isAggregateRootDeleted = true)
            }

            is Leaf -> {
                val entityIdentifier = deleteCommand.identifier

                val parentAsMap =
                    findEntityByIdentifier(
                        entityIdentifier,
                        entityType,
                        JsonNodeFactory.instance.arrayNode().add(aggregateOrLeaf.aggregateRootProperty.name),
                    )

                val aggregateIdentifier = getReferenceIdentifier(aggregateOrLeaf.aggregateRootProperty.name, parentAsMap)

                setAggregateIdentifier(aggregateIdentifier, aggregateOrLeaf.aggregate.type, false)
            }

            else -> {
                throw UnsupportedOperationException("Unsupported type for aggregate model: ${aggregateOrLeaf::class}")
            }
        }
    }

    override fun visit(
        getCommand: Command.Get,
        param: Unit,
    ) {
        val entityType = getCommand.entityType
        val aggregateOrLeaf = aggregatesModel.aggregateOrLeaf(entityType.name)

        if (entityType.isDictionary) {
            setAggregateIdentifier("1", (aggregateOrLeaf as Leaf).aggregate.type, true)
            return
        }

        val identifier = getCommand.identifier ?: return

        when (aggregateOrLeaf) {
            is Aggregate -> {
                setAggregateIdentifier(identifier, entityType, true)
            }

            is Leaf -> {
                val parentAsMap =
                    findEntityByIdentifier(
                        identifier,
                        entityType,
                        JsonNodeFactory.instance.arrayNode().add(aggregateOrLeaf.aggregateRootProperty.name),
                        false,
                    ).takeIf { it.isNotEmpty() } ?: return

                val aggregateIdentifier = getReferenceIdentifier(aggregateOrLeaf.aggregateRootProperty.name, parentAsMap)

                setAggregateIdentifier(aggregateIdentifier, aggregateOrLeaf.aggregate.type, true)
            }

            else -> {
                throw UnsupportedOperationException("Unsupported type for aggregate model: ${aggregateOrLeaf::class}")
            }
        }
    }

    override fun visit(
        manyCommand: Command.Many,
        param: Unit,
    ) {
        // do nothing
    }
}
