package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.entitymanager.default.select.SelectContext
import ru.sbertech.dataspace.entitymanager.default.select.SelectorProcessingVisitor
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.model.InheritanceStrategy
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.CommonTable
import ru.sbertech.dataspace.sql.dialect.Dialect
import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.sql.dialect.prepareQuery
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.sql.subquery.SubQuery
import ru.sbertech.dataspace.universalvalue.UniversalValue
import java.sql.Connection
import java.util.Collections
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class DefaultEntityManager(
    private val model: Model,
    private val dialect: Dialect,
    private val metaByTable: Map<String, TableMeta>,
    private val connection: Connection,
) : EntityManager {
    override fun select(selector: Selector): UniversalValue? {
        val context = SelectContext(model)
        selector.accept(SelectorProcessingVisitor(context, null, null, null, lazyOf(emptyList())))
        val getSubQueryId: (resultReader: ResultReader) -> Int =
            if (context.subQueryMetaById.size > 1) {
                val subQueryIdColumnIndex = context.freeColumnIndex(PrimitiveType.Int)
                context.subQueryMetaById.forEachIndexed { index, subQueryMeta ->
                    subQueryMeta.subQuery.select(PrimitiveSelector(PrimitiveType.Int, SqlExpr.Value(index)), subQueryIdColumnIndex)
                }
                ({ it.int(subQueryIdColumnIndex) })
            } else {
                { 0 }
            }
        val query =
            Query.Select(
                context.subQueryMetaById
                    .asSequence()
                    .map { it.subQuery.build() as SubQuery }
                    .reduce { subQuery1, subQuery2 -> SubQuery.Combination(subQuery1, subQuery2) },
                context.commonTableSubQueryByName.map { (name, subQuery) -> CommonTable(name, subQuery.build()) },
            )
        return connection.prepareQuery(query, dialect).use { preparedQuery ->
            preparedQuery.setParamValues(context.paramValueByName)
            preparedQuery.executeQuery().use {
                val cacheById = context.createCacheById()
                while (it.next()) {
                    val subQueryId = getSubQueryId(it)
                    context.subQueryMetaById[subQueryId].valueReader.read(cacheById, it)
                }
                context.resultReader.read(cacheById, it)
            }
        }
    }

    override fun lock(
        entityTypeName: String,
        id: UniversalValue,
    ) {
        val entityType = model.type(entityTypeName).cast<EntityType>()
        val idFillingVisitor =
            ValueFillingVisitor(
                dialect,
                metaByTable,
                connection,
                Goal.CREATE, // TODO ?
                Collections.singletonMap(entityType.tableIdProperty.name, id),
                Collections.emptyList(),
            )
        val tableMeta = metaByTable.getValue(entityType.rootEntityType.table)
        val valueByColumn = linkedMapOf<String, Primitive?>()
        entityType.rootEntityType.tableIdProperty.accept(idFillingVisitor, valueByColumn)
        connection.prepareQuery(tableMeta.lockQuery, dialect).use { preparedQuery ->
            preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
            preparedQuery.executeQuery()
        }
    }

    override fun create(
        entityTypeName: String,
        propertyValueByName: Map<String, UniversalValue?>,
    ): UniversalValue {
        val entityType = model.type(entityTypeName).cast<EntityType>()
        val modifiedPropertyValueByName = propertyValueByName.toMutableMap()
        val idStrategy = entityType.rootEntityType.idStrategy
        when (val id = modifiedPropertyValueByName[entityType.tableIdProperty.name]) {
            null -> modifiedPropertyValueByName[entityType.tableIdProperty.name] = idStrategy.generate(entityType, connection)
            else -> idStrategy.validate(entityType, id)
        }
        val delayedActions = arrayListOf<() -> Unit>()
        val valueFillingVisitor =
            ValueFillingVisitor(dialect, metaByTable, connection, Goal.CREATE, modifiedPropertyValueByName, delayedActions)
        var valueByColumn: MutableMap<String, Primitive?> = linkedMapOf()
        generateSequence(entityType) { it.parentEntityType }.toCollection(arrayListOf()).apply { reverse() }.forEach { entityType2 ->
            if (entityType2.parentEntityType == null || entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED) {
                valueByColumn.clear()
                entityType2.tableIdProperty.accept(valueFillingVisitor, valueByColumn)
            }
            entityType2.discriminatorColumn?.also { valueByColumn[it] = entityType.discriminatorValue }
            entityType2.persistableProperties.forEach { if (!it.isId) it.accept(valueFillingVisitor, valueByColumn) }
            if (entityType2 === entityType || entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED) {
                val tableMeta = metaByTable.getValue(entityType2.table)
                connection.prepareQuery(tableMeta.insertQuery, dialect).use { preparedQuery ->
                    preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
                    preparedQuery.executeUpdate()
                }
            }
        }
        delayedActions.forEach { delayedAction -> delayedAction() }
        // TODO обработка "лишних" значений
        // TODO кеширование запросов для batching'а
        return modifiedPropertyValueByName.getValue(entityType.tableIdProperty.name)!!
    }

    override fun update(
        entityTypeName: String,
        id: UniversalValue,
        propertyValueByName: Map<String, UniversalValue?>,
    ) {
        val entityType = model.type(entityTypeName).cast<EntityType>()
        if (entityType.tableIdProperty.name in propertyValueByName) {
            throw IllegalArgumentException("Id property update is forbidden")
        }
        val idFillingVisitor =
            ValueFillingVisitor(
                dialect,
                metaByTable,
                connection,
                Goal.CREATE,
                Collections.singletonMap(entityType.tableIdProperty.name, id),
                Collections.emptyList(),
            )
        val valueFillingVisitor =
            ValueFillingVisitor(
                dialect,
                metaByTable,
                connection,
                Goal.UPDATE,
                propertyValueByName,
                Collections.emptyList(),
                idFillingVisitor,
            )
        var valueByColumn: MutableMap<String, Primitive?> = linkedMapOf()
        generateSequence(entityType) { it.parentEntityType }.toCollection(arrayListOf()).apply { reverse() }.forEach { entityType2 ->
            entityType2.persistableProperties.forEach { if (!it.isId) it.accept(valueFillingVisitor, valueByColumn) }
            if (entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED &&
                entityType2.parentEntityType == null &&
                valueByColumn.isEmpty()
            ) {
                entityType2.tableIdProperty.accept(valueFillingVisitor, valueByColumn)
            }

            if ((entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED || entityType2 === entityType) &&
                valueByColumn.isNotEmpty()
            ) {
                val tableMeta = metaByTable.getValue(entityType2.table)
                val updateQuery = tableMeta.updateQuery(valueByColumn.keys)
                entityType2.tableIdProperty.accept(idFillingVisitor, valueByColumn)
                connection.prepareQuery(updateQuery, dialect).use { preparedQuery ->
                    preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
                    preparedQuery.executeUpdate()
                }
            }
            if (entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED) valueByColumn.clear()
        }

        generateSequence(entityType) { it.parentEntityType }.forEach { entityType2 ->
            if (entityType2 === entityType || entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED) {
                valueByColumn.clear()
            }
            entityType2.persistableProperties.forEach { if (!it.isId) it.accept(valueFillingVisitor, valueByColumn) }
            if (valueByColumn.isNotEmpty() &&
                (entityType2.parentEntityType == null || entityType2.rootEntityType.inheritanceStrategy == InheritanceStrategy.JOINED)
            ) {
                val tableMeta = metaByTable.getValue(entityType2.table)
                val updateQuery = tableMeta.updateQuery(valueByColumn.keys)
                entityType2.tableIdProperty.accept(idFillingVisitor, valueByColumn)
                connection.prepareQuery(updateQuery, dialect).use { preparedQuery ->
                    preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
                    preparedQuery.executeUpdate()
                }
            }
        }
        // TODO обработка "лишних" значений
        // TODO кеширование запросов для batching'а
    }

    override fun delete(
        entityTypeName: String,
        id: UniversalValue,
    ) {
        val entityType = model.type(entityTypeName).cast<EntityType>()
        val idFillingVisitor =
            ValueFillingVisitor(
                dialect,
                metaByTable,
                connection,
                Goal.CREATE, // TODO ?
                Collections.singletonMap(entityType.tableIdProperty.name, id),
                Collections.emptyList(),
            )
        val tableMeta = metaByTable.getValue(entityType.rootEntityType.table)
        val valueByColumn = linkedMapOf<String, Primitive?>()
        entityType.rootEntityType.tableIdProperty.accept(idFillingVisitor, valueByColumn)
        connection.prepareQuery(tableMeta.deleteQuery, dialect).use { preparedQuery ->
            preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
            preparedQuery.executeUpdate()
        }
        // TODO кеширование запросов для batching'а
    }

    override fun flush() {
        // TODO сохранить preparedQuery для последующего переиспользования (повторный flush)?
        // TODO можно ли долго держать открытым preparedQuery?
        // TODO
    }
}
