package ru.sbertech.dataspace.uow.packet.idempotence

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.uow.packet.FeatherUtils
import ru.sbertech.dataspace.uow.packet.aggregate.AggregateContext
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import java.sql.Connection
import java.time.LocalDateTime

private const val DATA = "data"
private const val API_CALL_ID = "apiCallId"
private const val FIRST_CALL_DATE = "firstCallDate"

object IdempotenceDataDao {
    private fun getIdempotenceEntityType(aggregateContext: AggregateContext) =
        aggregateContext.aggregate.idempotenceDataEntityType
            ?: throw IllegalStateException(
                "Idempotence entity not specified for aggregate ${aggregateContext.aggregate.type}, but idempotenceId is not null",
            )

    fun read(
        entityManager: EntityManager,
        idempotenceId: String,
        aggregateContext: AggregateContext,
        entitiesReadAccessJson: EntitiesReadAccessJson,
        connection: Connection,
    ): ArrayList<CommandIdempotenceData> {
        val idempotenceDataEntityType = getIdempotenceEntityType(aggregateContext)
        val idempotenceDataTypeLeaf = aggregateContext.aggregatesModel.aggregateOrLeaf(idempotenceDataEntityType.name) as Leaf

        // TODO LEGACY
//        val selector =
//            Selector.EntityCollectionBased(
//                idempotenceDataEntityType.name,
//                mapOf(
//                    DATA to Selector.PropertyBased(DATA),
//                    idempotenceDataTypeLeaf.aggregateRootProperty.name to
//                        Selector.PropertyBased(idempotenceDataTypeLeaf.aggregateRootProperty.name),
//                ),
//                expr { cur[idempotenceDataEntityType.tableIdProperty.name] eq value(idempotenceId) },
//            )
//
//        val resultAsMap =
//            (entityManager.select(selector) as Collection<*>)
//                .takeIf { !it.isEmpty() }
//                ?.let { it.iterator().next() as Map<*, *> }
//
//        if (resultAsMap == null) {
//            return arrayListOf()
//        }

        val queryNode = JsonNodeFactory.instance.objectNode()
        queryNode.put("type", idempotenceDataEntityType.name)
        queryNode.put("cond", "root.${'$'}id=='$idempotenceId'")
        val props = JsonNodeFactory.instance.arrayNode()
        props.add(DATA)
        props.add(idempotenceDataTypeLeaf.aggregateRootProperty.name)
        queryNode.set<ArrayNode>("props", props)

        val entities = entitiesReadAccessJson.searchEntities(queryNode, connection)
        val result = (entities as ObjectNode).get("elems").get(0) ?: return arrayListOf()

        val resultAsMap = FeatherUtils.jsonToMap(result as ObjectNode)["props"] as Map<*, *>

        val aggregateIdentifier = (resultAsMap[idempotenceDataTypeLeaf.aggregateRootProperty.name] as Map<*, *>)["id"]

        aggregateContext.findEntityByIdentifier(
            aggregateIdentifier,
            aggregateContext.aggregate.type,
            // TODO LEGACY
            JsonNodeFactory.instance.arrayNode(),
        )

        // TODO нужно ли тут записывать aggregateIdentifier?
        aggregateContext.setAggregateIdentifier(aggregateIdentifier, aggregateContext.aggregate.type, true)

        return IdempotenceDataSerializer.deserialize(resultAsMap[DATA] as String)
    }

    fun write(
        entityManager: EntityManager,
        idempotenceId: String,
        aggregateContext: AggregateContext,
        idempotenceData: Collection<CommandIdempotenceData>,
    ) {
        val idempotenceDataEntityType = getIdempotenceEntityType(aggregateContext)
        val idempotenceDataTypeLeaf = aggregateContext.aggregatesModel.aggregateOrLeaf(idempotenceDataEntityType.name) as Leaf

        entityManager.create(
            idempotenceDataEntityType.name,
            mapOf(
                idempotenceDataEntityType.tableIdProperty.name to idempotenceId,
                DATA to IdempotenceDataSerializer.serialize(idempotenceData),
                API_CALL_ID to idempotenceId,
                // TODO
                FIRST_CALL_DATE to LocalDateTime.now(),
                idempotenceDataTypeLeaf.parentProperty.name to aggregateContext.aggregateIdentifier!!,
                idempotenceDataTypeLeaf.aggregateRootProperty.name to aggregateContext.aggregateIdentifier!!,
            ),
        )
    }
}
