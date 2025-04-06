package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.MappedReferenceCollectionProperty
import ru.sbertech.dataspace.model.property.MappedReferenceProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.dialect.Dialect
import ru.sbertech.dataspace.sql.dialect.prepareQuery
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.universalvalue.type
import ru.sbertech.dataspace.universalvalue.type.UniversalValueType
import java.sql.Connection

internal class ValueFillingVisitor(
    private val dialect: Dialect,
    private val metaByTable: Map<String, TableMeta>,
    private val connection: Connection,
    private val goal: Goal,
    private val propertyValueByName: Map<String, UniversalValue?>,
    private val delayedActions: MutableCollection<() -> Unit>,
    idFillingVisitor: ValueFillingVisitor? = null,
) : PropertyParameterizedVisitor<MutableMap<String, Primitive?>, Unit> {
    private val idFillingVisitor: ValueFillingVisitor = idFillingVisitor ?: this

    private fun insertCollectionElements(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        tableMeta: TableMeta,
        elements: Collection<UniversalValue?>,
    ) {
        connection.prepareQuery(tableMeta.insertQuery, dialect).use { preparedQuery ->
            elements.forEach { element ->
                when {
                    element == null -> throw IllegalArgumentException("TODO null element is unacceptable")
                    !element.type.let {
                        it is UniversalValueType.Primitive && it.type == primitiveCollectionProperty.type
                    } -> throw IllegalArgumentException("Element type doesn't suit property '${primitiveCollectionProperty.name}' type")

                    else ->
                        linkedMapOf<String, Primitive?>().also { valueByColumn ->
                            primitiveCollectionProperty.ownerIdProperty.accept(idFillingVisitor, valueByColumn)
                            valueByColumn[primitiveCollectionProperty.elementColumn] = element
                            preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
                            if (elements.size != 1) preparedQuery.addBatch() else preparedQuery.executeUpdate()
                        }
                }
            }
            if (elements.size != 1) preparedQuery.executeBatch()
        }
    }

    private fun insertCollectionElements(
        enumCollectionProperty: EnumCollectionProperty,
        tableMeta: TableMeta,
        elements: Collection<UniversalValue?>,
    ) {
        connection.prepareQuery(tableMeta.insertQuery, dialect).use { preparedQuery ->
            elements.forEach { element ->
                when {
                    element == null -> throw IllegalArgumentException("TODO null element is unacceptable")
                    !element.type.let {
                        it is UniversalValueType.Primitive && it.type == PrimitiveType.String
                    } -> throw IllegalArgumentException("Element type doesn't suit property '${enumCollectionProperty.name}' type")

                    else ->
                        linkedMapOf<String, Primitive?>().also { valueByColumn ->
                            enumCollectionProperty.ownerIdProperty.accept(idFillingVisitor, valueByColumn)
                            valueByColumn[enumCollectionProperty.elementColumn] = element
                            preparedQuery.setParamValues(tableMeta.paramValueByName(valueByColumn))
                            if (elements.size != 1) preparedQuery.addBatch() else preparedQuery.executeUpdate()
                        }
                }
            }
            if (elements.size != 1) preparedQuery.executeBatch()
        }
    }

    private fun validateChangeability(
        propertyName: String,
        isSettableOnCreate: Boolean,
        isSettableOnUpdate: Boolean,
    ) {
        if ((goal == Goal.CREATE && !isSettableOnCreate || goal == Goal.UPDATE && !isSettableOnUpdate) &&
            propertyName in propertyValueByName
        ) {
            throw IllegalArgumentException("Property '$propertyName' is not expected to be set")
        }
    }

    override fun visit(
        primitiveProperty: PrimitiveProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        validateChangeability(primitiveProperty.name, primitiveProperty.isSettableOnCreate, primitiveProperty.isSettableOnUpdate)
        val value =
            when (goal) {
                Goal.CREATE -> propertyValueByName.getOrDefault(primitiveProperty.name, primitiveProperty.defaultValue)
                Goal.UPDATE -> propertyValueByName[primitiveProperty.name]
            }
        when {
            value == null ->
                when {
                    goal == Goal.UPDATE && primitiveProperty.name !in propertyValueByName -> return
                    !primitiveProperty.isOptional -> throw IllegalArgumentException("Property '${primitiveProperty.name}' is not optional")
                }

            !value.type.let { it is UniversalValueType.Primitive && it.type == primitiveProperty.type } ->
                throw IllegalArgumentException("Value type doesn't suit property '${primitiveProperty.name}' type")
        }
        valueByColumn[primitiveProperty.column] = value
    }

    override fun visit(
        enumProperty: EnumProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        validateChangeability(enumProperty.name, enumProperty.isSettableOnCreate, enumProperty.isSettableOnUpdate)
        val value =
            when (goal) {
                Goal.CREATE -> propertyValueByName.getOrDefault(enumProperty.name, enumProperty.defaultValue?.name)
                Goal.UPDATE -> propertyValueByName[enumProperty.name]
            }
        when {
            value == null ->
                when {
                    goal == Goal.UPDATE && enumProperty.name !in propertyValueByName -> return
                    !enumProperty.isOptional -> throw IllegalArgumentException("Property '${enumProperty.name}' is not optional")
                }

            !value.type.let { it is UniversalValueType.Primitive && it.type == PrimitiveType.String } ->
                throw IllegalArgumentException("Value type doesn't suit property '${enumProperty.name}' type")
        }
        valueByColumn[enumProperty.column] = value?.let { enumProperty.type.value(it as String).name }
    }

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        val value = propertyValueByName[primitiveCollectionProperty.name]
        when {
            value == null ->
                when {
                    goal == Goal.CREATE -> return
                    goal == Goal.UPDATE && primitiveCollectionProperty.name !in propertyValueByName -> return
                }

            value.type !is UniversalValueType.Collection ->
                throw IllegalArgumentException("Value type doesn't suit property '${primitiveCollectionProperty.name}' type")
        }
        val tableMeta = metaByTable.getValue(primitiveCollectionProperty.table)
        if (goal == Goal.UPDATE) {
            connection.prepareQuery(tableMeta.deleteQuery, dialect).use { preparedQuery ->
                preparedQuery.setParamValues(
                    tableMeta.paramValueByName(
                        linkedMapOf<String, Primitive?>().also {
                            primitiveCollectionProperty.ownerIdProperty.accept(idFillingVisitor, it)
                        },
                    ),
                )
                preparedQuery.executeUpdate()
            }
        }
        value?.also {
            val elements = value.uncheckedCast<Collection<UniversalValue?>>()
            if (elements.isNotEmpty()) {
                when (goal) {
                    Goal.CREATE -> delayedActions += { insertCollectionElements(primitiveCollectionProperty, tableMeta, elements) }
                    Goal.UPDATE -> insertCollectionElements(primitiveCollectionProperty, tableMeta, elements)
                }
            }
        }
    }

    override fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        val value = propertyValueByName[enumCollectionProperty.name]
        when {
            value == null ->
                when {
                    goal == Goal.CREATE -> return
                    goal == Goal.UPDATE && enumCollectionProperty.name !in propertyValueByName -> return
                }

            value.type !is UniversalValueType.Collection ->
                throw IllegalArgumentException("Value type doesn't suit property '${enumCollectionProperty.name}' type")
        }
        val tableMeta = metaByTable.getValue(enumCollectionProperty.table)
        if (goal == Goal.UPDATE) {
            connection.prepareQuery(tableMeta.deleteQuery, dialect).use { preparedQuery ->
                preparedQuery.setParamValues(
                    tableMeta.paramValueByName(
                        linkedMapOf<String, Primitive?>().also {
                            enumCollectionProperty.ownerIdProperty.accept(idFillingVisitor, it)
                        },
                    ),
                )
                preparedQuery.executeUpdate()
            }
        }
        value?.also {
            val elements = value.uncheckedCast<Collection<UniversalValue?>>()
            if (elements.isNotEmpty()) {
                when (goal) {
                    Goal.CREATE -> delayedActions += { insertCollectionElements(enumCollectionProperty, tableMeta, elements) }
                    Goal.UPDATE -> insertCollectionElements(enumCollectionProperty, tableMeta, elements)
                }
            }
        }
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        val value =
            when (goal) {
                Goal.CREATE -> propertyValueByName.getOrDefault(embeddedProperty.name, emptyMap<String, UniversalValue?>())
                Goal.UPDATE -> propertyValueByName[embeddedProperty.name]
            }
        when {
            value == null -> if (goal == Goal.UPDATE && embeddedProperty.name !in propertyValueByName) return

            value.type !is UniversalValueType.Object ->
                throw IllegalArgumentException("Value type doesn't suit property '${embeddedProperty.name}' type")
        }
        val visitor =
            ValueFillingVisitor(
                dialect,
                metaByTable,
                connection,
                goal,
                value?.uncheckedCast() ?: embeddedProperty.embeddedType.properties.associateByTo(linkedMapOf(), { it.name }, { null }),
                delayedActions,
                idFillingVisitor,
            )
        embeddedProperty.embeddedType.properties.forEach { it.accept(visitor, valueByColumn) }
    }

    override fun visit(
        referenceProperty: ReferenceProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        validateChangeability(referenceProperty.name, referenceProperty.isSettableOnCreate, referenceProperty.isSettableOnUpdate)
        val visitor =
            when (propertyValueByName[referenceProperty.name]) {
                null -> {
                    if (goal == Goal.UPDATE && referenceProperty.name !in propertyValueByName) return
                    NullFillingVisitor
                }

                else ->
                    when (goal) {
                        Goal.CREATE -> this
                        else ->
                            ValueFillingVisitor(
                                dialect,
                                metaByTable,
                                connection,
                                Goal.CREATE,
                                propertyValueByName,
                                delayedActions,
                                idFillingVisitor,
                            )
                    }
            }
        referenceProperty.idProperty.accept(visitor, valueByColumn)
    }

    override fun visit(
        mappedReferenceProperty: MappedReferenceProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        // TODO
    }

    override fun visit(
        mappedReferenceCollectionProperty: MappedReferenceCollectionProperty,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") valueByColumn: MutableMap<String, Primitive?>,
    ) {
        // TODO
    }
}
