package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Component
import ru.sbertech.dataspace.model.EmbeddedType
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.belongsTo
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.model.type.IdentifiableType
import ru.sbertech.dataspace.model.type.StructuredType

sealed class Property : Component() {
    lateinit var owningEntityType: EntityType
        private set

    var isId: Boolean = false
        private set

    abstract fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ): R

    fun <R> accept(visitor: PropertyVisitor<R>): R = accept(visitor, Unit)

    sealed class Builder : Component.Builder() {
        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : Component.Builder.Internal() {
            abstract val isSuitableForId: Boolean

            val isPersistable: Boolean get() = lazyPersistableFlag.value

            var owningEntityType: EntityType.Builder? = null
                private set

            val isId: Boolean get() = lazyIdFlag.value

            val table: String? get() = lazyTable.value

            private lateinit var lazyPersistableFlag: Lazy<Boolean>

            private lateinit var lazyIdFlag: Lazy<Boolean>

            private lateinit var lazyTable: Lazy<String?>

            abstract override val result: Property

            abstract override val meta: Meta<Builder>

            override val doCreateResult get() = super.doCreateResult && (isPersistable || belongsTo(EmbeddableType.Builder.Meta.properties))

            open fun property(name: String): Builder? = null

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                lazyPersistableFlag =
                    lazy(LazyThreadSafetyMode.NONE) {
                        belongsTo(EntityType.Builder.Meta.properties) ||
                            belongsTo(EntityType.Builder.Meta.tableIdProperty, parent) ||
                            (belongsTo(CollectionProperty.Builder.Meta.ownerIdProperty, parent) && parent.internal.isPersistable) ||
                            (belongsTo(ReferenceProperty.Builder.Meta.idProperty, parent) && parent.internal.isPersistable) ||
                            (belongsTo(EmbeddedType.Builder.Meta.properties, parent) && parent.isPersistable)
                    }
                owningEntityType =
                    when {
                        belongsTo(EntityType.Builder.Meta.properties, parent) -> parent
                        belongsTo(EntityType.Builder.Meta.tableIdProperty, parent) -> parent
                        belongsTo(ReferenceProperty.Builder.Meta.idProperty, parent) -> parent.internal.owningEntityType
                        belongsTo(EmbeddedType.Builder.Meta.properties, parent) -> parent.owningEntityType
                        else -> null
                    }
                lazyIdFlag =
                    lazy(LazyThreadSafetyMode.NONE) {
                        (belongsTo(IdentifiableType.Builder.Meta.properties, parent) && parent.internal.idProperty == this@Builder) ||
                            belongsTo(EntityType.Builder.Meta.tableIdProperty) ||
                            belongsTo(CollectionProperty.Builder.Meta.ownerIdProperty) ||
                            belongsTo(ReferenceProperty.Builder.Meta.idProperty) ||
                            (belongsTo(EmbeddedType.Builder.Meta.properties, parent) && parent.isId)
                    }
                lazyTable =
                    lazy(LazyThreadSafetyMode.NONE) {
                        when {
                            this@Builder is CollectionProperty.Builder -> null
                            belongsTo(EntityType.Builder.Meta.properties, parent) -> parent.internal.table
                            belongsTo(EntityType.Builder.Meta.tableIdProperty, parent) -> parent.internal.table
                            belongsTo(CollectionProperty.Builder.Meta.ownerIdProperty, parent) -> parent.table
                            belongsTo(ReferenceProperty.Builder.Meta.idProperty, parent) -> parent.internal.table
                            belongsTo(EmbeddedType.Builder.Meta.properties, parent) -> parent.table
                            else -> null
                        }
                    }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val parent = parent
                name?.also { name ->
                    if (belongsTo(StructuredType.Builder.Meta.properties, parent)) {
                        parent.internal.validateUniquePropertyName(errors, name) { path(NAME_ATTRIBUTE) }
                    }
                    if (belongsTo(IdentifiableType.Builder.Meta.properties, parent)) {
                        parent.internal.parentType?.internal?.inheritedProperty(name)?.also {
                            errors += ModelError.N3(path(NAME_ATTRIBUTE), name, it.internal.path(NAME_ATTRIBUTE))
                        }
                    }
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                owningEntityType?.also { result.owningEntityType = it.internal.result }
                result.isId = isId
            }
        }

        internal abstract class Meta<out B : Builder> : Component.Builder.Meta<B>() {
            companion object : Meta<Builder>()
        }
    }

    object Override {
        sealed class Builder : AbstractBuilder() {
            var propertyName: String? = null

            abstract override val internal: Internal

            abstract override fun clone(): Builder

            internal abstract inner class Internal : AbstractBuilder.Internal() {
                val correspondingProperty: Property.Builder? get() = lazyCorrespondingProperty.value

                private lateinit var lazyCorrespondingProperty: Lazy<Property.Builder?>

                abstract override val meta: Meta<Builder>

                override val doCreateResult get() = false

                open fun applyTo(property: Property.Builder) {}

                override fun setCloneProperties(clone: AbstractBuilder) {
                    super.setCloneProperties(clone as Builder)
                    clone.propertyName = propertyName
                }

                override fun prepare(
                    goal: Goal,
                    model: Model.Builder,
                    parent: AbstractBuilder?,
                    parentRelation: Relation<*>?,
                ) {
                    super.prepare(goal, model, parent, parentRelation)
                    lazyCorrespondingProperty =
                        lazy(LazyThreadSafetyMode.NONE) {
                            when {
                                belongsTo(EntityType.Builder.Meta.tableIdPropertyOverride, parent) -> parent.internal.tableIdProperty
                                belongsTo(EmbeddedProperty.Builder.Meta.propertyOverrides) ||
                                    belongsTo(EmbeddedProperty.Override.Builder.Meta.propertyOverrides) ->
                                    propertyName?.let {
                                        val embeddedProperty =
                                            when {
                                                belongsTo(EmbeddedProperty.Builder.Meta.propertyOverrides) -> parent
                                                belongsTo(EmbeddedProperty.Override.Builder.Meta.propertyOverrides, parent) ->
                                                    parent.internal.correspondingProperty

                                                else -> throw IllegalStateException()
                                            } as? EmbeddedProperty.Builder
                                        embeddedProperty
                                            ?.internal
                                            ?.embeddedType
                                            ?.internal
                                            ?.property(it)
                                    }

                                belongsTo(CollectionProperty.Builder.Meta.ownerIdPropertyOverride) ||
                                    belongsTo(CollectionProperty.Override.Builder.Meta.ownerIdPropertyOverride) -> {
                                    val collectionProperty =
                                        when {
                                            belongsTo(CollectionProperty.Builder.Meta.ownerIdPropertyOverride) -> parent
                                            belongsTo(CollectionProperty.Override.Builder.Meta.ownerIdPropertyOverride, parent) ->
                                                parent.internal.correspondingProperty

                                            else -> throw IllegalStateException()
                                        } as? CollectionProperty.Builder
                                    collectionProperty?.internal?.ownerIdProperty
                                }

                                belongsTo(ReferenceProperty.Builder.Meta.idPropertyOverride) ||
                                    belongsTo(ReferenceProperty.Override.Builder.Meta.idPropertyOverride) -> {
                                    val referenceProperty =
                                        when {
                                            belongsTo(ReferenceProperty.Builder.Meta.idPropertyOverride) -> parent
                                            belongsTo(ReferenceProperty.Override.Builder.Meta.idPropertyOverride, parent) ->
                                                parent.internal.correspondingProperty

                                            else -> throw IllegalStateException()
                                        } as? ReferenceProperty.Builder
                                    referenceProperty?.internal?.idProperty
                                }

                                else -> throw IllegalStateException()
                            }
                        }
                }

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    if (correspondingProperty == null) errors += ModelError.N12(path())
                    when {
                        belongsTo(EmbeddedProperty.Builder.Meta.propertyOverrides) ||
                            belongsTo(EmbeddedProperty.Override.Builder.Meta.propertyOverrides) ->
                            if (propertyName == null) errors += ModelError.N1(path(PROPERTY_NAME_ATTRIBUTE))

                        belongsTo(EntityType.Builder.Meta.tableIdPropertyOverride) ||
                            belongsTo(CollectionProperty.Builder.Meta.ownerIdPropertyOverride) ||
                            belongsTo(CollectionProperty.Override.Builder.Meta.ownerIdPropertyOverride) ||
                            belongsTo(ReferenceProperty.Builder.Meta.idPropertyOverride) ||
                            belongsTo(ReferenceProperty.Override.Builder.Meta.idPropertyOverride) ->
                            propertyName?.also { errors += ModelError.N2(path(PROPERTY_NAME_ATTRIBUTE)) }
                    }
                }
            }

            internal abstract class Meta<out B : Builder> : AbstractBuilder.Meta<B>() {
                companion object : Meta<Builder>()
            }

            companion object {
                internal const val PROPERTY_NAME_ATTRIBUTE: String = "property name"
            }
        }
    }
}
