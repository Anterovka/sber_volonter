package ru.sbertech.dataspace.model.type

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.IdStrategy
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.property.Property

sealed class IdentifiableType : StructuredType() {
    var parentType: IdentifiableType? = null
        private set

    // TODO не должно использоваться в EntityManager'е (use tableIdProperty)
    var idProperty: Property? = null
        private set

    lateinit var idStrategy: IdStrategy
        private set

    sealed class Builder : StructuredType.Builder() {
        var parentTypeName: String? = null

        var idPropertyName: String? = null

        var idStrategy: IdStrategy? = null

        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : StructuredType.Builder.Internal() {
            var parentType: Builder? = null
                private set

            var idProperty: Property.Builder? = null
                private set

            val inheritedPropertyByName: Map<String, Property.Builder> get() = lazyInheritedPropertyByName.value

            val inheritedIdProperty: Property.Builder? get() = lazyInheritedIdProperty.value

            private lateinit var lazyInheritedPropertyByName: Lazy<Map<String, Property.Builder>>

            private lateinit var lazyInheritedIdProperty: Lazy<Property.Builder?>

            abstract override val result: IdentifiableType

            abstract override val meta: Meta<Builder>

            fun inheritedProperty(name: String): Property.Builder? = inheritedPropertyByName[name]

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.parentTypeName = parentTypeName
                clone.idPropertyName = idPropertyName
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                parentType = parentTypeName?.let { model.internal.type(it) as? Builder }
                idProperty = idPropertyName?.let { property(it) }
                lazyInheritedPropertyByName =
                    lazy(LazyThreadSafetyMode.NONE) {
                        properties?.asSequence().orEmpty().filter { it.name != null }.associateByTo(linkedMapOf()) { it.name!! }.apply {
                            parentType?.also { putAll(it.internal.inheritedPropertyByName) }
                        }
                    }
                lazyInheritedIdProperty =
                    lazy(LazyThreadSafetyMode.NONE) {
                        if (parentTypeName == null) idProperty else parentType?.internal?.inheritedIdProperty
                    }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val parentTypeName = parentTypeName
                val idPropertyName = idPropertyName
                val idProperty = idProperty
                when (parentTypeName) {
                    null ->
                        when {
                            idPropertyName == null -> errors += ModelError.N1(path(ID_PROPERTY_NAME_ATTRIBUTE))
                            idProperty == null -> errors += ModelError.N9(path(ID_PROPERTY_NAME_ATTRIBUTE), idPropertyName, path())
                            !idProperty.internal.isSuitableForId ->
                                errors += ModelError.N10(path(ID_PROPERTY_NAME_ATTRIBUTE), idProperty.internal.path())
                        }

                    else -> {
                        if (parentType == null) {
                            when (val parentType2 = model.internal.type(parentTypeName)) {
                                null -> errors += ModelError.N6(path(PARENT_TYPE_NAME_ATTRIBUTE), parentTypeName, model.internal.path())
                                !is Builder -> errors += ModelError.N7(path(PARENT_TYPE_NAME_ATTRIBUTE), parentType2.internal.path())
                                else -> {}
                            }
                        }
                        idPropertyName?.also { errors += ModelError.N2(path(ID_PROPERTY_NAME_ATTRIBUTE)) }
                    }
                }
                when {
                    idPropertyName == null && idStrategy != null -> errors += ModelError.N2(path(ID_STRATEGY_ATTRIBUTE))
                    idPropertyName != null && idStrategy == null -> errors += ModelError.N1(path(ID_STRATEGY_ATTRIBUTE))
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.parentType = parentType?.internal?.result
                result.idProperty = idProperty?.internal?.result
                idStrategy?.let { result.idStrategy = it }
            }
        }

        internal abstract class Meta<out B : Builder> : StructuredType.Builder.Meta<B>() {
            companion object : Meta<Builder>()
        }

        companion object {
            internal const val PARENT_TYPE_NAME_ATTRIBUTE: String = "parent type name"

            internal const val ID_PROPERTY_NAME_ATTRIBUTE: String = "id property name"

            internal const val ID_STRATEGY_ATTRIBUTE: String = "id strategy"
        }
    }
}
