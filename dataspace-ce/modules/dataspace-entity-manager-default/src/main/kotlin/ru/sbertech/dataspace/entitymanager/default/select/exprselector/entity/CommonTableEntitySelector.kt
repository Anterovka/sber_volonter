package ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity

import ru.sbertech.dataspace.entitymanager.default.select.PropertyProcessingVisitor
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.CommonEmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.EmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SequentialSimpleSubQueryBuilder
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class CommonTableEntitySelector(
    private val baseEntity: EntitySelector,
    private val commonTable: String,
    private val commonTableSubQuery: SequentialSimpleSubQueryBuilder,
) : EntitySelector() {
    private val lazyId: Lazy<ExprSelector> =
        lazy(LazyThreadSafetyMode.NONE) {
            type.rootEntityType.idProperty!!.accept(PropertyProcessingVisitor.ForEntity, this)
        }

    private val selectorByProperty: MutableMap<Property, ExprSelector> = linkedMapOf()

    override val type get() = baseEntity.type

    override val id by lazyId

    override fun property(primitiveProperty: PrimitiveProperty) =
        selectorByProperty.getOrPut(primitiveProperty) {
            PrimitiveSelector(
                primitiveProperty.type,
                SqlExpr.Column(commonTableSubQuery.select(baseEntity.property(primitiveProperty)).alias!!, commonTable),
            )
        } as PrimitiveSelector

    override fun property(enumProperty: EnumProperty) =
        selectorByProperty.getOrPut(enumProperty) {
            PrimitiveSelector(
                PrimitiveType.String,
                SqlExpr.Column(commonTableSubQuery.select(baseEntity.property(enumProperty)).alias!!, commonTable),
            )
        } as PrimitiveSelector

    override fun property(embeddedProperty: EmbeddedProperty) =
        selectorByProperty.getOrPut(embeddedProperty) {
            CommonEmbeddedSelector(baseEntity.property(embeddedProperty), commonTable, commonTableSubQuery, lazyId, selectorByProperty)
        } as EmbeddedSelector
}
