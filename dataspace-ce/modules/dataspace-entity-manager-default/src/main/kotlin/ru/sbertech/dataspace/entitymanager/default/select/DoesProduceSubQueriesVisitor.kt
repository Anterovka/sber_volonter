package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.entitymanager.selector.SelectorParameterizedVisitor
import ru.sbertech.dataspace.model.EmbeddedType
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.type.EntityType

internal sealed class DoesProduceSubQueriesVisitor {
    object ForEntityType : DoesProduceSubQueriesVisitor(), SelectorParameterizedVisitor<EntityType, Boolean> {
        override fun visit(
            entityCollectionBasedSelector: Selector.EntityCollectionBased,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entityType: EntityType,
        ) = true

        override fun visit(
            propertyBasedSelector: Selector.PropertyBased,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entityType: EntityType,
        ) = entityType.inheritedPersistableProperty(propertyBasedSelector.name).accept(ForProperty, propertyBasedSelector)

        override fun visit(
            expr: Selector.Expr,
            param: EntityType,
        ) = false

        override fun visit(
            group: Selector.Group,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entityType: EntityType,
        ) = group.selectorByName.values.any { it.accept(this, entityType) }

        override fun visit(
            approximateType: Selector.ApproximateType,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") entityType: EntityType,
        ) = false
    }

    object ForEmbeddedType : DoesProduceSubQueriesVisitor(), SelectorParameterizedVisitor<EmbeddedType, Boolean> {
        override fun visit(
            entityCollectionBasedSelector: Selector.EntityCollectionBased,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embeddedType: EmbeddedType,
        ) = true

        override fun visit(
            propertyBasedSelector: Selector.PropertyBased,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embeddedType: EmbeddedType,
        ) = embeddedType.property(propertyBasedSelector.name).accept(ForProperty, propertyBasedSelector)

        override fun visit(
            expr: Selector.Expr,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embeddedType: EmbeddedType,
        ) = false

        override fun visit(
            group: Selector.Group,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embeddedType: EmbeddedType,
        ) = group.selectorByName.values.any { it.accept(this, embeddedType) }

        override fun visit(
            approximateType: Selector.ApproximateType,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") embeddedType: EmbeddedType,
        ) = false
    }

    object ForProperty : DoesProduceSubQueriesVisitor(), PropertyParameterizedVisitor<Selector.PropertyBased, Boolean> {
        override fun visit(
            primitiveProperty: PrimitiveProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") selector: Selector.PropertyBased,
        ) = false

        override fun visit(
            enumProperty: EnumProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") selector: Selector.PropertyBased,
        ) = false

        override fun visit(
            primitiveCollectionProperty: PrimitiveCollectionProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") selector: Selector.PropertyBased,
        ) = true

        override fun visit(
            embeddedProperty: EmbeddedProperty,
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") selector: Selector.PropertyBased,
        ) = selector.selectorByName.values.any { it.accept(ForEmbeddedType, embeddedProperty.embeddedType) }
    }
}
