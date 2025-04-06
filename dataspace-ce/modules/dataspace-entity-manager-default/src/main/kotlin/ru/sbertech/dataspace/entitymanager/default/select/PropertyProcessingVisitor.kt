package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.EmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.embedded.SimpleEmbeddedSelector
import ru.sbertech.dataspace.entitymanager.default.select.exprselector.entity.EntitySelector
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr

internal object PropertyProcessingVisitor {
    object ForId : PropertyParameterizedVisitor<String, ExprSelector> {
        override fun visit(
            primitiveProperty: PrimitiveProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") table: String,
        ) = PrimitiveSelector(primitiveProperty.type, Expr.Column(primitiveProperty.column, table))

        override fun visit(
            enumProperty: EnumProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") table: String,
        ) = PrimitiveSelector(PrimitiveType.String, Expr.Column(enumProperty.column, table))

        override fun visit(
            embeddedProperty: EmbeddedProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") table: String,
        ) = SimpleEmbeddedSelector(embeddedProperty.embeddedType, table, null)
    }

    object ForEntity : PropertyParameterizedVisitor<EntitySelector, ExprSelector> {
        override fun visit(
            primitiveProperty: PrimitiveProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entity: EntitySelector,
        ) = entity.property(primitiveProperty)

        override fun visit(
            enumProperty: EnumProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entity: EntitySelector,
        ) = entity.property(enumProperty)

        override fun visit(
            primitiveCollectionProperty: PrimitiveCollectionProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entity: EntitySelector,
        ) = TODO()

        override fun visit(
            embeddedProperty: EmbeddedProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entity: EntitySelector,
        ) = entity.property(embeddedProperty)
    }

    object ForEmbedded : PropertyParameterizedVisitor<EmbeddedSelector, ExprSelector> {
        override fun visit(
            primitiveProperty: PrimitiveProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embedded: EmbeddedSelector,
        ) = embedded.property(primitiveProperty)

        override fun visit(
            enumProperty: EnumProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embedded: EmbeddedSelector,
        ) = embedded.property(enumProperty)

        override fun visit(
            primitiveCollectionProperty: PrimitiveCollectionProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embedded: EmbeddedSelector,
        ) = TODO()

        override fun visit(
            embeddedProperty: EmbeddedProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embedded: EmbeddedSelector,
        ) = embedded.property(embeddedProperty)
    }
}
