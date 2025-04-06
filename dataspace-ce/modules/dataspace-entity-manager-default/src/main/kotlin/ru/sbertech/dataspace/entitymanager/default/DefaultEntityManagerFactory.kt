package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.entitymanager.EntityManager
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.sql.dialect.Dialect
import java.sql.Connection

class DefaultEntityManagerFactory(
    private val model: Model,
    private val dialect: Dialect,
) : EntityManagerFactory {
    private val metaByTable: Map<String, TableMeta> =
        run {
            val metaBuilderByTable = linkedMapOf<String, TableMeta.Builder>()
            val tablesMetaPreparingVisitor = TablesMetaPreparingVisitor(metaBuilderByTable)
            model.types.asSequence().filterIsInstance<EntityType>().forEach { entityType ->
                val tableMetaBuilder =
                    metaBuilderByTable.getOrPut(entityType.table) {
                        TableMeta.Builder().also {
                            it.table = entityType.table
                            it.idProperty = entityType.tableIdProperty
                            entityType.tableIdProperty.accept(tablesMetaPreparingVisitor, it)
                        }
                    }
                entityType.discriminatorColumn?.also { tableMetaBuilder.addParam(it, entityType.discriminatorType, true) }
                entityType.persistableProperties.forEach { if (!it.isId) it.accept(tablesMetaPreparingVisitor, tableMetaBuilder) }
            }
            metaBuilderByTable.mapValuesTo(linkedMapOf()) { (_, metaBuilder) -> metaBuilder.build() }
        }

    override fun create(connection: Connection): EntityManager = DefaultEntityManager(model, dialect, metaByTable, connection)
}
