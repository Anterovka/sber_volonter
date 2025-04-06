package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.common.LazyNull
import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.EmbeddedType
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.belongsTo
import ru.sbertech.dataspace.model.type.IdentifiableType

sealed class CollectionProperty : Property() {
    // доступно, если isPersistable
    lateinit var table: String
        private set

    // доступно, если isPersistable
    lateinit var ownerIdProperty: Property
        private set

    abstract class Builder : Property.Builder() {
        var table: String? = null

        var ownerIdPropertyOverride: Property.Override.Builder? = null

        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : Property.Builder.Internal() {
            val ownerIdProperty: Property.Builder? get() = lazyOwnerIdProperty.value

            private lateinit var lazyOwnerIdProperty: Lazy<Property.Builder?>

            override val isSuitableForId get() = false

            abstract override val result: CollectionProperty

            abstract override val meta: Meta<Builder>

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.table = this@Builder.table
                clone.ownerIdPropertyOverride = ownerIdPropertyOverride?.clone()
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                lazyOwnerIdProperty = LazyNull
                super.prepare(goal, model, parent, parentRelation)
                lazyOwnerIdProperty =
                    lazy(LazyThreadSafetyMode.NONE) {
                        when {
                            belongsTo(IdentifiableType.Builder.Meta.properties, parent) -> parent.internal.inheritedIdProperty
                            belongsTo(EmbeddedType.Builder.Meta.properties, parent) -> parent.idProperty
                            else -> null
                        }?.clone()?.also {
                            ownerIdPropertyOverride?.internal?.applyTo(it)
                            it.internal.prepare(goal, model, this@Builder, meta.ownerIdProperty)
                        }
                    }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val table = this@Builder.table
                if (isPersistable) {
                    when {
                        table == null -> errors += ModelError.N1(path(TABLE_ATTRIBUTE))
                        table.isBlank() -> errors += ModelError.N4(path(TABLE_ATTRIBUTE))
                        else -> model.internal.validateUniqueTable(errors, table) { path(TABLE_ATTRIBUTE) }
                    }
                    if (ownerIdProperty == null) errors += ModelError.N12(path(OWNER_ID_PROPERTY_OVERRIDE_ATTRIBUTE))
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                if (isPersistable) {
                    result.table = this@Builder.table!!
                    result.ownerIdProperty = ownerIdProperty!!.internal.result
                }
            }
        }

        internal abstract class Meta<out B : Builder> : Property.Builder.Meta<B>() {
            val ownerIdPropertyOverride: Relation<B> = builderRelation(OWNER_ID_PROPERTY_OVERRIDE_ATTRIBUTE) { it.ownerIdPropertyOverride }

            val ownerIdProperty: Relation<B> = builderRelation(OWNER_ID_PROPERTY_ATTRIBUTE) { it.internal.ownerIdProperty }

            companion object : Meta<Builder>()
        }

        companion object {
            internal const val TABLE_ATTRIBUTE: String = "table"

            internal const val OWNER_ID_PROPERTY_OVERRIDE_ATTRIBUTE: String = "owner id property override"

            internal const val OWNER_ID_PROPERTY_ATTRIBUTE: String = "[generated] owner id property"
        }
    }

    object Override {
        sealed class Builder : Property.Override.Builder() {
            var table: String? = null

            var ownerIdPropertyOverride: Property.Override.Builder? = null

            abstract override val internal: Internal

            abstract override fun clone(): Builder

            internal abstract inner class Internal : Property.Override.Builder.Internal() {
                abstract override val meta: Meta<Builder>

                override fun applyTo(property: Property.Builder) {
                    super.applyTo(property)
                    if (property is CollectionProperty.Builder) {
                        table?.also { property.table = it }
                        ownerIdPropertyOverride?.also { property.ownerIdPropertyOverride = it.clone() }
                    }
                }

                override fun setCloneProperties(clone: AbstractBuilder) {
                    super.setCloneProperties(clone as Builder)
                    clone.table = table
                    clone.ownerIdPropertyOverride = ownerIdPropertyOverride?.clone()
                }
            }

            internal abstract class Meta<out B : Builder> : Property.Override.Builder.Meta<B>() {
                val ownerIdPropertyOverride: Relation<B> =
                    builderRelation(OWNER_ID_PROPERTY_OVERRIDE_ATTRIBUTE) { it.ownerIdPropertyOverride }

                companion object : Meta<Builder>()
            }

            companion object {
                internal const val TABLE_ATTRIBUTE: String = "table"

                internal const val OWNER_ID_PROPERTY_OVERRIDE_ATTRIBUTE: String = "owner id property override"
            }
        }
    }
}
