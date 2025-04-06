package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.MappedReferenceCollectionProperty
import ru.sbertech.dataspace.model.property.MappedReferenceProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.primitive.type.PrimitiveType

internal class TablesMetaPreparingVisitor(
    private val metaBuilderByTable: MutableMap<String, TableMeta.Builder>,
) : PropertyParameterizedVisitor<TableMeta.Builder, Unit> {
    override fun visit(
        primitiveProperty: PrimitiveProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
        tableMetaBuilder.addParam(primitiveProperty.column, primitiveProperty.type, primitiveProperty.isSettableOnCreate)
    }

    override fun visit(
        enumProperty: EnumProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
        tableMetaBuilder.addParam(enumProperty.column, PrimitiveType.String, enumProperty.isSettableOnCreate)
    }

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
        metaBuilderByTable[primitiveCollectionProperty.table] =
            TableMeta.Builder().also {
                it.table = primitiveCollectionProperty.table
                it.idProperty = primitiveCollectionProperty.ownerIdProperty
                primitiveCollectionProperty.ownerIdProperty.accept(this, it)
                it.addParam(primitiveCollectionProperty.elementColumn, primitiveCollectionProperty.type, true)
            }
    }

    override fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
        metaBuilderByTable[enumCollectionProperty.table] =
            TableMeta.Builder().also {
                it.table = enumCollectionProperty.table
                it.idProperty = enumCollectionProperty.ownerIdProperty
                enumCollectionProperty.ownerIdProperty.accept(this, it)
                it.addParam(enumCollectionProperty.elementColumn, PrimitiveType.String, true)
            }
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
        embeddedProperty.embeddedType.properties.forEach { it.accept(this, tableMetaBuilder) }
    }

    override fun visit(
        referenceProperty: ReferenceProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
        referenceProperty.idProperty.accept(this, tableMetaBuilder)
    }

    override fun visit(
        mappedReferenceProperty: MappedReferenceProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
    }

    override fun visit(
        mappedReferenceCollectionProperty: MappedReferenceCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") tableMetaBuilder: TableMeta.Builder,
    ) {
    }
}
