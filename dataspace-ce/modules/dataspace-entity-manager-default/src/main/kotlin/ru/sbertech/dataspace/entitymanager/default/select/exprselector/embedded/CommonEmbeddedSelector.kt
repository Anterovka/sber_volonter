package ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SequentialSimpleSubQueryBuilder
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr

internal class CommonEmbeddedSelector(
    private val baseEmbedded: EmbeddedSelector,
    private val commonTable: String,
    private val commonTableSubQuery: SequentialSimpleSubQueryBuilder,
    private val lazyId: Lazy<ExprSelector?>,
    private val selectorByProperty: MutableMap<Property, ExprSelector> = linkedMapOf(),
) : EmbeddedSelector() {
    override val type get() = baseEmbedded.type

    override val id by lazyId

    override fun property(primitiveProperty: PrimitiveProperty) =
        selectorByProperty.getOrPut(primitiveProperty) {
            PrimitiveSelector(
                primitiveProperty.type,
                Expr.Column(commonTableSubQuery.select(baseEmbedded.property(primitiveProperty)).alias!!, commonTable),
            )
        } as PrimitiveSelector

    override fun property(enumProperty: EnumProperty) =
        selectorByProperty.getOrPut(enumProperty) {
            PrimitiveSelector(
                PrimitiveType.String,
                Expr.Column(commonTableSubQuery.select(baseEmbedded.property(enumProperty)).alias!!, commonTable),
            )
        } as PrimitiveSelector

    override fun property(embeddedProperty: EmbeddedProperty) =
        selectorByProperty.getOrPut(embeddedProperty) {
            CommonEmbeddedSelector(baseEmbedded.property(embeddedProperty), commonTable, commonTableSubQuery, lazyId, selectorByProperty)
        } as EmbeddedSelector
}
