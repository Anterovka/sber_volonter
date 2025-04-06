package ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.EmbeddedSelector
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.type.EntityType

internal abstract class EntitySelector : ExprSelector() {
    abstract val type: EntityType

    abstract val id: ExprSelector

    abstract fun property(primitiveProperty: PrimitiveProperty): PrimitiveSelector

    abstract fun property(enumProperty: EnumProperty): PrimitiveSelector

    abstract fun property(embeddedProperty: EmbeddedProperty): EmbeddedSelector

    override fun eq(expr: ExprSelector) =
        when {
            expr is EntitySelector && type == expr.type -> id.eq(expr.id)
            else -> throw IllegalArgumentException()
        }
}
