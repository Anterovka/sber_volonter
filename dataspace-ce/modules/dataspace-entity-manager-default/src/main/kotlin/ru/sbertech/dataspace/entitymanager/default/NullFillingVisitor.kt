package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.model.property.BasicProperty
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.primitive.Primitive

internal object NullFillingVisitor : PropertyParameterizedVisitor<MutableMap<String, Primitive?>, Unit> {
    private fun visitBasicProperty(
        basicProperty: BasicProperty,
        valueByColumn: MutableMap<String, Primitive?>,
    ) {
        valueByColumn[basicProperty.column] = null
    }

    override fun visit(
        primitiveProperty: PrimitiveProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        visitBasicProperty(primitiveProperty, valueByColumn)
    }

    override fun visit(
        enumProperty: EnumProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        visitBasicProperty(enumProperty, valueByColumn)
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        embeddedProperty.embeddedType.properties.forEach { it.accept(this, valueByColumn) }
    }

    override fun visit(
        referenceProperty: ReferenceProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        referenceProperty.idProperty.accept(this, valueByColumn)
    }
}
