package ru.sbertech.dataspace.model.type

import ru.sbertech.dataspace.common.LazyNull
import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.InheritanceStrategy
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type
import ru.sbertech.dataspace.primitive.type.PrimitiveType

class EntityType private constructor() : IdentifiableType() {
    var parentEntityType: EntityType? = null
        private set

    lateinit var rootEntityType: EntityType
        private set

    lateinit var childEntityTypes: Collection<EntityType>
        private set

    val persistableProperties: Collection<Property> get() = persistablePropertyByName.values

    val inheritedPersistableProperties: Collection<Property> get() = inheritedPersistablePropertyByName.values

    lateinit var inheritanceStrategy: InheritanceStrategy
        private set

    lateinit var table: String
        private set

    lateinit var tableIdProperty: Property
        private set

    var discriminatorColumn: String? = null
        private set

    lateinit var discriminatorType: PrimitiveType
        private set

    lateinit var discriminatorValue: Primitive
        private set

    private lateinit var persistablePropertyByName: Map<String, Property>

    private lateinit var inheritedPersistablePropertyByName: Map<String, Property>

    // TODO надо?
    fun persistableProperty(name: String): Property =
        persistablePropertyByName[name] ?: throw IllegalArgumentException("Persistable property '$name' is not found")

    fun inheritedPersistableProperty(name: String): Property =
        inheritedPersistablePropertyByName[name] ?: throw IllegalArgumentException("Inherited persistable property '$name' is not found")

    override fun <P, R> accept(
        visitor: TypeParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : IdentifiableType.Builder() {
        // default for root type = SINGLE_TABLE
        var inheritanceStrategy: InheritanceStrategy? = null

        var table: String? = null

        var tableIdPropertyOverride: Property.Override.Builder? = null

        var discriminatorColumn: String? = null

        // default for root type = String
        var discriminatorType: PrimitiveType? = null

        // default = name
        var discriminatorValue: Primitive? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : IdentifiableType.Builder.Internal() {
            var parentEntityType: Builder? = null
                private set

            val rootEntityType: Builder? get() = lazyRootEntityType.value

            val childEntityTypes: Collection<Builder> get() = lazyChildEntityTypes.value

            val tableIdProperty: Property.Builder? get() = lazyTableIdProperty.value

            lateinit var inheritanceStrategy: InheritanceStrategy
                private set

            val table: String? get() = lazyTable.value

            lateinit var discriminatorType: PrimitiveType
                private set

            private lateinit var lazyRootEntityType: Lazy<Builder?>

            private lateinit var lazyChildEntityTypes: Lazy<Collection<Builder>>

            private lateinit var lazyTableIdProperty: Lazy<Property.Builder?>

            private lateinit var lazyTable: Lazy<String?>

            override lateinit var result: EntityType
                private set

            override val meta get() = Meta

            override val about get() = "Entity type '$name'"

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.inheritanceStrategy = this@Builder.inheritanceStrategy
                clone.table = this@Builder.table
                clone.tableIdPropertyOverride = tableIdPropertyOverride?.clone()
                clone.discriminatorColumn = discriminatorColumn
                clone.discriminatorType = this@Builder.discriminatorType
                clone.discriminatorValue = discriminatorValue
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                lazyTableIdProperty = LazyNull
                super.prepare(goal, model, parent, parentRelation)
                parentEntityType = parentType as? Builder
                lazyRootEntityType =
                    lazy(LazyThreadSafetyMode.NONE) {
                        if (parentTypeName == null) this@Builder else parentEntityType?.internal?.rootEntityType
                    }
                lazyChildEntityTypes =
                    lazy(LazyThreadSafetyMode.NONE) {
                        model.types
                            ?.asSequence()
                            .orEmpty()
                            .filterIsInstance<Builder>()
                            .filter { entityType -> this@Builder in generateSequence(entityType) { it.internal.parentEntityType } }
                            .toCollection(arrayListOf())
                    }
                lazyTableIdProperty =
                    lazy(LazyThreadSafetyMode.NONE) {
                        val rootEntityType = rootEntityType
                        when {
                            parentTypeName != null && rootEntityType?.internal?.inheritanceStrategy == InheritanceStrategy.JOINED -> {
                                inheritedIdProperty?.clone()?.also {
                                    tableIdPropertyOverride?.internal?.applyTo(it)
                                    it.internal.prepare(goal, model, this@Builder, meta.tableIdProperty)
                                }
                            }

                            else -> null
                        }
                    }
                inheritanceStrategy = this@Builder.inheritanceStrategy ?: InheritanceStrategy.SINGLE_TABLE
                lazyTable =
                    lazy(LazyThreadSafetyMode.NONE) {
                        rootEntityType?.let {
                            when (it.internal.inheritanceStrategy) {
                                InheritanceStrategy.SINGLE_TABLE -> it.table
                                InheritanceStrategy.JOINED -> this@Builder.table
                            }
                        }
                    }
                discriminatorType = this@Builder.discriminatorType ?: PrimitiveType.String
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val table = this@Builder.table
                val discriminatorColumn = discriminatorColumn
                val rootEntityType = rootEntityType
                val inheritanceStrategy = rootEntityType?.internal?.inheritanceStrategy
                when (parentTypeName) {
                    null ->
                        when {
                            discriminatorColumn == null ->
                                if (childEntityTypes.size > 1) errors += ModelError.N1(path(DISCRIMINATOR_COLUMN_ATTRIBUTE))

                            discriminatorColumn.isBlank() -> errors += ModelError.N4(path(DISCRIMINATOR_COLUMN_ATTRIBUTE))
                            else ->
                                table?.also {
                                    model.internal.validateUniqueColumn(errors, table, discriminatorColumn) {
                                        path(DISCRIMINATOR_COLUMN_ATTRIBUTE)
                                    }
                                }
                        }

                    else -> {
                        this@Builder.inheritanceStrategy?.also { errors += ModelError.N2(path(INHERITANCE_STRATEGY_ATTRIBUTE)) }
                        discriminatorColumn?.also { errors += ModelError.N2(path(DISCRIMINATOR_COLUMN_ATTRIBUTE)) }
                        this@Builder.discriminatorType?.also { errors += ModelError.N2(path(DISCRIMINATOR_TYPE_ATTRIBUTE)) }
                    }
                }
                when {
                    parentTypeName == null || inheritanceStrategy == InheritanceStrategy.JOINED ->
                        when {
                            table == null -> errors += ModelError.N1(path(TABLE_ATTRIBUTE))
                            table.isBlank() -> errors += ModelError.N4(path(TABLE_ATTRIBUTE))
                            else -> model.internal.validateUniqueTable(errors, table) { path(TABLE_ATTRIBUTE) }
                        }

                    rootEntityType != null -> table?.also { errors += ModelError.N2(path(TABLE_ATTRIBUTE)) }
                }
                if ((parentTypeName == null || inheritanceStrategy?.equals(InheritanceStrategy.JOINED) == false) &&
                    tableIdPropertyOverride != null
                ) {
                    errors += ModelError.N2(path(TABLE_ID_PROPERTY_OVERRIDE_ATTRIBUTE))
                }
                if (rootEntityType != null) {
                    when (rootEntityType.discriminatorColumn) {
                        null -> discriminatorValue?.also { errors += ModelError.N2(path(DISCRIMINATOR_VALUE_ATTRIBUTE)) }
                        else ->
                            when (val discriminatorValue = discriminatorValue) {
                                null ->
                                    if (discriminatorType != PrimitiveType.String) {
                                        errors += ModelError.N1(path(DISCRIMINATOR_VALUE_ATTRIBUTE))
                                    }

                                else -> {
                                    val discriminatorValueType = discriminatorValue.type
                                    if (discriminatorValueType != rootEntityType.internal.discriminatorType) {
                                        errors +=
                                            ModelError.N11(
                                                path(DISCRIMINATOR_VALUE_ATTRIBUTE),
                                                discriminatorValueType,
                                                rootEntityType.internal.path(DISCRIMINATOR_TYPE_ATTRIBUTE),
                                                rootEntityType.internal.discriminatorType,
                                            )
                                    }
                                }
                            }
                    }
                }
            }

            override fun createResult() {
                super.createResult()
                result = EntityType()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.parentEntityType = parentEntityType?.internal?.result
                result.rootEntityType = rootEntityType!!.internal.result
                result.childEntityTypes = childEntityTypes.mapTo(arrayListOf()) { it.internal.result }
                result.persistablePropertyByName =
                    properties
                        ?.asSequence()
                        .orEmpty()
                        .map { it.internal.result }
                        .associateByTo(linkedMapOf()) { it.name }
                result.inheritedPersistablePropertyByName =
                    inheritedPropertyByName.mapValuesTo(linkedMapOf()) { (_, inheritedProperty) -> inheritedProperty.internal.result }
                if (parentTypeName == null) result.inheritanceStrategy = inheritanceStrategy
                result.table = table!!
                result.tableIdProperty = (tableIdProperty ?: inheritedIdProperty!!).internal.result
                discriminatorColumn?.also {
                    result.discriminatorColumn = it
                    result.discriminatorType = discriminatorType
                }
                rootEntityType!!.discriminatorColumn?.also { result.discriminatorValue = discriminatorValue ?: name!! }
            }
        }

        internal object Meta : IdentifiableType.Builder.Meta<Builder>() {
            val tableIdPropertyOverride: Relation<Builder> =
                builderRelation(TABLE_ID_PROPERTY_OVERRIDE_ATTRIBUTE) { it.tableIdPropertyOverride }

            val tableIdProperty: Relation<Builder> = builderRelation(TABLE_ID_PROPERTY_ATTRIBUTE) { it.internal.tableIdProperty }
        }

        companion object {
            internal const val INHERITANCE_STRATEGY_ATTRIBUTE: String = "inheritance strategy"

            internal const val TABLE_ATTRIBUTE: String = "table"

            internal const val TABLE_ID_PROPERTY_OVERRIDE_ATTRIBUTE: String = "table id property override"

            internal const val DISCRIMINATOR_COLUMN_ATTRIBUTE: String = "discriminator column"

            internal const val DISCRIMINATOR_TYPE_ATTRIBUTE: String = "discriminator type"

            internal const val DISCRIMINATOR_VALUE_ATTRIBUTE: String = "discriminator value"

            internal const val TABLE_ID_PROPERTY_ATTRIBUTE: String = "[generated] table id property"
        }
    }
}
