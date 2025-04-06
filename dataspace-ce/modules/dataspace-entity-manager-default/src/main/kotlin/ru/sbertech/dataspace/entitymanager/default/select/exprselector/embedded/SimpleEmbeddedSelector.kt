package ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.model.EmbeddedType
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr

internal class SimpleEmbeddedSelector(
    override val type: EmbeddedType,
    private val table: String,
    override val id: ExprSelector?,
    private val selectorByProperty: MutableMap<Property, ExprSelector> = linkedMapOf(),
) : EmbeddedSelector() {
    override fun property(primitiveProperty: PrimitiveProperty) =
        selectorByProperty.getOrPut(primitiveProperty) {
            PrimitiveSelector(primitiveProperty.type, Expr.Column(primitiveProperty.column, table))
        } as PrimitiveSelector

    override fun property(enumProperty: EnumProperty) =
        selectorByProperty.getOrPut(enumProperty) {
            PrimitiveSelector(PrimitiveType.String, Expr.Column(enumProperty.column, table))
        } as PrimitiveSelector

    override fun property(embeddedProperty: EmbeddedProperty) =
        selectorByProperty.getOrPut(embeddedProperty) {
            SimpleEmbeddedSelector(embeddedProperty.embeddedType, table, id, selectorByProperty)
        } as EmbeddedSelector
}
