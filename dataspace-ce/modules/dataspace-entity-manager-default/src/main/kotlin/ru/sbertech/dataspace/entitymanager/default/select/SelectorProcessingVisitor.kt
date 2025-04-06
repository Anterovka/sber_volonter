package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.EmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity.CommonTableEntitySelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity.EntitySelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity.SimpleEntitySelector
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.PositionedSimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SequentialSimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.tablebuilder.SimpleTableBuilder
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.CollectionElementReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.CollectionReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ConstPrimitiveReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ObjectReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.PrimitiveReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.RootCollectionElementReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.RootCollectionReader
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ValueReader
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.entitymanager.selector.SelectorVisitor
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.sql.table.Table
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class SelectorProcessingVisitor(
    private val context: SelectContext,
    private val subQuery: SimpleSubQueryBuilder?,
    private val cur: ExprSelector?,
    private val root: ExprSelector?,
    private val collectionKey: Lazy<Collection<ExprSelector>>,
) : SelectorVisitor<ValueReader>,
    PropertyParameterizedVisitor<Selector.PropertyBased, ValueReader> {
    override fun visit(
        entityCollectionBasedSelector: Selector.EntityCollectionBased,
        param: Unit,
    ) = when (subQuery) {
        null -> {
            val entityType = context.model.type(entityCollectionBasedSelector.typeName).cast<EntityType>()
            var entity: EntitySelector = SimpleEntitySelector(entityType, context)
            val hasDependantSubQueries =
                entityCollectionBasedSelector.selectorByName.values.any {
                    it.accept(DoesProduceSubQueriesVisitor.ForEntityType, entityType)
                }
            var subQuery: SimpleSubQueryBuilder =
                if (hasDependantSubQueries) {
                    SequentialSimpleSubQueryBuilder(context)
                } else {
                    PositionedSimpleSubQueryBuilder(context)
                }
            subQuery.table = (entity as SimpleEntitySelector).table
            val exprProcessingVisitor = ExprProcessingVisitor(context, entity, entity)
            entityCollectionBasedSelector.cond?.also { subQuery.cond = it.accept(exprProcessingVisitor).cast() }
            // TODO
//            if (entityType.parentEntityType != null && entityType.rootEntityType.inheritanceStrategy == InheritanceStrategy.SINGLE_TABLE) {
//                val additionalCond = SqlExpr.InList(SqlExpr.Column())
//                subQuery.cond =
//            }
            if (hasDependantSubQueries) {
                val commonTable = context.addCommonTable(subQuery)
                entity = CommonTableEntitySelector(entity, commonTable, subQuery as SequentialSimpleSubQueryBuilder)
                subQuery = PositionedSimpleSubQueryBuilder(context)
                subQuery.table = SimpleTableBuilder(Table.Simple(commonTable))
            }
            val subQueryId = context.addSubQuery(subQuery as PositionedSimpleSubQueryBuilder)
            val collectionKey = lazy(LazyThreadSafetyMode.NONE) { arrayListOf(entity.id) }
            val selectorProcessingVisitor = SelectorProcessingVisitor(context, subQuery, entity, entity, collectionKey)
            val cacheId = context.nextCacheId()
            context.addValueReader(
                subQueryId,
                RootCollectionElementReader(
                    cacheId,
                    ObjectReader(
                        entityCollectionBasedSelector.selectorByName.mapValuesTo(linkedMapOf()) { (_, selector) ->
                            selector.accept(selectorProcessingVisitor)
                        },
                    ),
                ),
            )
            RootCollectionReader(cacheId).also { context.setResultReader(it) }
        }

        else -> TODO()
    }

    override fun visit(
        propertyBasedSelector: Selector.PropertyBased,
        param: Unit,
    ) = when (val cur = cur) {
        is EntitySelector -> cur.type.inheritedPersistableProperty(propertyBasedSelector.name).accept(this, propertyBasedSelector)
        is EmbeddedSelector -> cur.type.property(propertyBasedSelector.name).accept(this, propertyBasedSelector)
        else -> throw IllegalArgumentException()
    }

    override fun visit(
        expr: Selector.Expr,
        param: Unit,
    ): ValueReader {
        val subQuery = subQuery ?: throw IllegalArgumentException("TODO")
        val expr2 = expr.expr.accept(ExprProcessingVisitor(context, cur, root))
        if (expr2 !is PrimitiveSelector) throw IllegalArgumentException("TODO")
        return expr2.reader(subQuery)
    }

    override fun visit(
        group: Selector.Group,
        param: Unit,
    ) = ObjectReader(group.selectorByName.mapValuesTo(linkedMapOf()) { (_, selector) -> selector.accept(this, param) })

    override fun visit(
        approximateType: Selector.ApproximateType,
        param: Unit,
    ) = when (val curSelector = cur) {
        is EntitySelector -> ConstPrimitiveReader(curSelector.type.name)
        else -> throw IllegalArgumentException()
    }

    override fun visit(
        primitiveProperty: PrimitiveProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") propertyBasedSelector: Selector.PropertyBased,
    ): ValueReader {
        val expr =
            when (val cur = cur) {
                is EntitySelector -> cur.property(primitiveProperty)
                is EmbeddedSelector -> cur.property(primitiveProperty)
                else -> throw IllegalArgumentException()
            }
        return expr.reader(subQuery!!)
    }

    override fun visit(
        enumProperty: EnumProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") propertyBasedSelector: Selector.PropertyBased,
    ): ValueReader {
        val expr =
            when (val cur = cur) {
                is EntitySelector -> cur.property(enumProperty)
                is EmbeddedSelector -> cur.property(enumProperty)
                else -> throw IllegalArgumentException()
            }
        return expr.reader(subQuery!!)
    }

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") propertyBasedSelector: Selector.PropertyBased,
    ): ValueReader {
        val tableAlias = context.nextTableAlias()
        val collectionTable = Table.Simple(primitiveCollectionProperty.table, tableAlias)
        val subQuery = PositionedSimpleSubQueryBuilder(context)
        val id =
            when (val cur = cur) {
                is EntitySelector -> cur.id
                is EmbeddedSelector -> cur.id!!
                else -> throw IllegalArgumentException()
            }
        subQuery.table =
            SimpleTableBuilder(
                Table.Join(
                    (this.subQuery!!.table as SimpleTableBuilder).table,
                    collectionTable,
                    id.eq(primitiveCollectionProperty.ownerIdProperty.accept(PropertyProcessingVisitor.ForId, tableAlias)).sqlExpr,
                ),
            )
        val elementSelector =
            PrimitiveSelector(primitiveCollectionProperty.type, SqlExpr.Column(primitiveCollectionProperty.elementColumn, tableAlias))
        val exprProcessingVisitor = ExprProcessingVisitor(context, elementSelector, root)
        propertyBasedSelector.cond?.also { subQuery.cond = it.accept(exprProcessingVisitor).cast() }
        val cacheId = context.nextCacheId()
        val subQueryId = context.addSubQuery(subQuery)
        context.addValueReader(
            subQueryId,
            CollectionElementReader(
                cacheId,
                collectionKey.value.map { it.reader(subQuery) },
                PrimitiveReader(primitiveCollectionProperty.type, subQuery.select(elementSelector).columnIndex),
            ),
        )
        return CollectionReader(cacheId, collectionKey.value.map { it.reader(this.subQuery) })
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") propertyBasedSelector: Selector.PropertyBased,
    ): ValueReader {
        val expr =
            when (val cur = cur) {
                is EntitySelector -> cur.property(embeddedProperty)
                is EmbeddedSelector -> cur.property(embeddedProperty)
                else -> throw IllegalArgumentException()
            }
        val selectorProcessingVisitor =
            SelectorProcessingVisitor(
                context,
                subQuery,
                expr,
                root,
                collectionKey,
            )
        return ObjectReader(
            propertyBasedSelector.selectorByName.mapValuesTo(linkedMapOf()) { (_, selector) -> selector.accept(selectorProcessingVisitor) },
        )
    }
}
