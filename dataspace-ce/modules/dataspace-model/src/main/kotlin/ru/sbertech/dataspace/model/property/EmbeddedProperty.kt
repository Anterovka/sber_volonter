package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.common.LazyNull
import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.EmbeddedType
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.applyTo
import ru.sbertech.dataspace.model.belongsTo
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.IdentifiableType
import ru.sbertech.dataspace.model.type.StructuredType

class EmbeddedProperty private constructor() : Property() {
    lateinit var type: EmbeddableType
        private set

    // доступно, если isPersistable
    lateinit var embeddedType: EmbeddedType
        private set

    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : Property.Builder() {
        var typeName: String? = null

        var propertyOverrides: MutableCollection<Property.Override.Builder>? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : Property.Builder.Internal() {
            var type: EmbeddableType.Builder? = null
                private set

            val embeddedType: EmbeddedType.Builder? get() = lazyEmbeddedType.value

            private lateinit var lazyEmbeddedType: Lazy<EmbeddedType.Builder?>

            override val isSuitableForId
                get() =
                    type
                        ?.properties
                        .orEmpty()
                        .let { properties -> properties.isNotEmpty() && properties.all { it.internal.isSuitableForId } }

            override lateinit var result: EmbeddedProperty
                private set

            override val meta get() = Meta

            override val about get() = "Embedded property '$name'"

            override fun property(name: String) = embeddedType?.internal?.property(name)

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.typeName = typeName
                clone.propertyOverrides = propertyOverrides?.mapTo(arrayListOf()) { it.clone() }
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                lazyEmbeddedType = LazyNull
                super.prepare(goal, model, parent, parentRelation)
                type = typeName?.let { model.internal.type(it) as? EmbeddableType.Builder }
                type?.also { embeddableType ->
                    lazyEmbeddedType =
                        lazy(LazyThreadSafetyMode.NONE) {
                            EmbeddedType.Builder().also { embeddedType ->
                                embeddedType.embeddableType = embeddableType
                                embeddedType.isPersistable = isPersistable
                                embeddedType.owningEntityType = owningEntityType
                                embeddedType.isId = isId
                                embeddedType.properties =
                                    when {
                                        belongsTo(StructuredType.Builder.Meta.properties) -> embeddableType.properties
                                        else -> (cloneSource as Builder).internal.embeddedType?.properties
                                    }?.mapTo(arrayListOf()) { it.clone() }
                                embeddedType.table = table
                                embeddedType.idProperty =
                                    when {
                                        belongsTo(IdentifiableType.Builder.Meta.properties, parent) -> parent.internal.inheritedIdProperty
                                        belongsTo(EmbeddedType.Builder.Meta.properties, parent) -> parent.idProperty
                                        else -> null
                                    }
                                propertyOverrides?.applyTo(embeddedType)
                                embeddedType.internal.prepare(goal, model, this@Builder, meta.embeddedType)
                            }
                        }
                }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val typeName = typeName
                when (typeName) {
                    null -> errors += ModelError.N1(path(TYPE_NAME_ATTRIBUTE))
                    else ->
                        when (val type2 = model.internal.type(typeName)) {
                            null -> errors += ModelError.N6(path(TYPE_NAME_ATTRIBUTE), typeName, model.internal.path())
                            !is EmbeddableType.Builder -> errors += ModelError.N8(path(TYPE_NAME_ATTRIBUTE), type2.internal.path())
                            else -> {}
                        }
                }
            }

            override fun createResult() {
                super.createResult()
                result = EmbeddedProperty()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type!!.internal.result
                if (isPersistable) result.embeddedType = embeddedType!!.internal.result
            }
        }

        internal object Meta : Property.Builder.Meta<Builder>() {
            val propertyOverrides: Relation<Builder> = buildersRelation(PROPERTY_OVERRIDES_ATTRIBUTE) { it.propertyOverrides }

            val embeddedType: Relation<Builder> = builderRelation(EMBEDDED_TYPE_ATTRIBUTE) { it.internal.embeddedType }
        }

        companion object {
            internal const val TYPE_NAME_ATTRIBUTE: String = "type name"

            internal const val PROPERTY_OVERRIDES_ATTRIBUTE: String = "property overrides"

            internal const val EMBEDDED_TYPE_ATTRIBUTE: String = "[generated] embedded type"
        }
    }

    object Override {
        class Builder : Property.Override.Builder() {
            var propertyOverrides: MutableCollection<Property.Override.Builder>? = null

            override val internal = Internal()

            override fun clone() = Builder().also { internal.setCloneProperties(it) }

            internal inner class Internal : Property.Override.Builder.Internal() {
                override val meta get() = Meta

                override val about get() = "Embedded property '$propertyName' override"

                override fun applyTo(property: Property.Builder) {
                    super.applyTo(property)
                    if (property is EmbeddedProperty.Builder) {
                        propertyOverrides?.also { propertyOverrides ->
                            property.propertyOverrides = propertyOverrides.mapTo(arrayListOf()) { it.clone() }
                        }
                    }
                }

                override fun setCloneProperties(clone: AbstractBuilder) {
                    super.setCloneProperties(clone as Builder)
                    clone.propertyOverrides = propertyOverrides?.mapTo(arrayListOf()) { it.clone() }
                }

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    correspondingProperty
                        ?.also { if (it !is EmbeddedProperty.Builder) errors += ModelError.N13(path(), it.internal.path()) }
                }
            }

            internal object Meta : Property.Override.Builder.Meta<Builder>() {
                val propertyOverrides: Relation<Builder> = buildersRelation(PROPERTY_OVERRIDES_ATTRIBUTE) { it.propertyOverrides }
            }

            companion object {
                internal const val PROPERTY_OVERRIDES_ATTRIBUTE: String = "property overrides"
            }
        }
    }
}
