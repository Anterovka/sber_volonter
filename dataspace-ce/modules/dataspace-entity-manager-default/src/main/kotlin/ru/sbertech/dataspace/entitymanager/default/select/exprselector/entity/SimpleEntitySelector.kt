package ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity

import ru.sbertech.dataspace.entitymanager.default.select.PropertyProcessingVisitor
import ru.sbertech.dataspace.entitymanager.default.select.SelectContext
import ru.sbertech.dataspace.entitymanager.default.select.TableMeta
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.EmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.SimpleEmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.tablebuilder.SimpleTableBuilder
import ru.sbertech.dataspace.model.InheritanceStrategy
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.JoinType
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.table.Table

internal class SimpleEntitySelector(
    override val type: EntityType,
    private val context: SelectContext,
) : EntitySelector() {
    val table: SimpleTableBuilder

    private val tableMetaByEntityType: MutableMap<EntityType, TableMeta> = linkedMapOf()

    private val selectorByProperty: MutableMap<Property, ExprSelector> = linkedMapOf()

    override val id: ExprSelector

    init {
        val tableMeta = tableMeta(type)
        table = SimpleTableBuilder(tableMeta.table)
        id = tableMeta.localId
        selectorByProperty[type.rootEntityType.idProperty!!] = id
    }

    private fun tableMeta(
        entityType: EntityType,
        joinType: JoinType? = null,
    ): TableMeta {
        val localEntityType =
            when (entityType.rootEntityType.inheritanceStrategy) {
                InheritanceStrategy.SINGLE_TABLE -> entityType.rootEntityType
                InheritanceStrategy.JOINED -> entityType
            }
        return tableMetaByEntityType.getOrPut(localEntityType) {
            val tableAlias = context.nextTableAlias()
            TableMeta(
                Table.Simple(localEntityType.table, tableAlias),
                localEntityType.tableIdProperty.accept(PropertyProcessingVisitor.ForId, tableAlias),
            ).also { tableMeta ->
                joinType?.also { table.table = Table.Join(table.table, tableMeta.table, id.eq(tableMeta.localId).sqlExpr, joinType) }
            }
        }
    }

    override fun property(primitiveProperty: PrimitiveProperty) =
        selectorByProperty.getOrPut(primitiveProperty) {
            val tableMeta = tableMeta(primitiveProperty.owningEntityType, JoinType.INNER)
            PrimitiveSelector(primitiveProperty.type, Expr.Column(primitiveProperty.column, tableMeta.table.alias!!))
        } as PrimitiveSelector

    override fun property(enumProperty: EnumProperty) =
        selectorByProperty.getOrPut(enumProperty) {
            val tableMeta = tableMeta(enumProperty.owningEntityType, JoinType.INNER)
            PrimitiveSelector(PrimitiveType.String, Expr.Column(enumProperty.column, tableMeta.table.alias!!))
        } as PrimitiveSelector

    override fun property(embeddedProperty: EmbeddedProperty) =
        selectorByProperty.getOrPut(embeddedProperty) {
            val tableMeta = tableMeta(embeddedProperty.owningEntityType, JoinType.INNER)
            SimpleEmbeddedSelector(embeddedProperty.embeddedType, tableMeta.table.alias!!, id)
        } as EmbeddedSelector
}
