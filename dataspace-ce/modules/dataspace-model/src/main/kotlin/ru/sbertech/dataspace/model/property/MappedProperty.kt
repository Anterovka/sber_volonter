package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.type.EntityType

sealed class MappedProperty : Property() {
    lateinit var type: EntityType
        private set

    lateinit var mappingProperty: ReferenceProperty
        private set

    sealed class Builder : Property.Builder() {
        var typeName: String? = null

        var mappingPropertyPath: MutableCollection<String>? = null

        abstract override val internal: Internal

        abstract override fun clone(): Property.Builder

        internal abstract inner class Internal : Property.Builder.Internal() {
            var type: EntityType.Builder? = null
                private set

            val mappingProperty: ReferenceProperty.Builder? get() = lazyMappingProperty.value

            private lateinit var lazyMappingProperty: Lazy<ReferenceProperty.Builder?>

            override val isSuitableForId get() = false

            abstract override val result: MappedProperty

            abstract override val meta: Meta<Builder>

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.typeName = typeName
                clone.mappingPropertyPath = mappingPropertyPath?.toCollection(arrayListOf())
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                super.prepare(goal, model, parent, parentRelation)
                type = typeName?.let { model.internal.type(it) as? EntityType.Builder }
                lazyMappingProperty =
                    lazy(LazyThreadSafetyMode.NONE) {
                        mappingPropertyPath?.let { type?.internal?.property(it) as? ReferenceProperty.Builder }
                    }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val typeName = typeName
                val mappingPropertyPath = mappingPropertyPath
                val type = type
                val mappingProperty = mappingProperty
                if (!belongsTo(EntityType.Builder.Meta.properties)) errors += ModelError.N20(path())
                when {
                    typeName == null -> errors += ModelError.N1(path(TYPE_NAME_ATTRIBUTE))
                    type == null ->
                        when (val type2 = model.internal.type(typeName)) {
                            null -> errors += ModelError.N6(path(TYPE_NAME_ATTRIBUTE), typeName, model.internal.path())
                            !is EntityType.Builder -> errors += ModelError.N14(path(TYPE_NAME_ATTRIBUTE), type2.internal.path())
                            else -> {}
                        }
                }
                when {
                    mappingPropertyPath == null -> errors += ModelError.N1(path(MAPPING_PROPERTY_PATH_ATTRIBUTE))
                    mappingProperty == null ->
                        if (type != null) {
                            when (val mappingProperty2 = type.internal.property(mappingPropertyPath)) {
                                null ->
                                    errors +=
                                        ModelError.N17(path(MAPPING_PROPERTY_PATH_ATTRIBUTE), mappingPropertyPath, type.internal.path())

                                !is ReferenceProperty.Builder ->
                                    errors += ModelError.N18(path(MAPPING_PROPERTY_PATH_ATTRIBUTE), mappingProperty2.internal.path())

                                else -> {}
                            }
                        }

                    else -> {
                        val mappingPropertyType = mappingProperty.internal.type
                        val owningEntityType = owningEntityType
                        if (mappingPropertyType != null && owningEntityType != null && mappingPropertyType != owningEntityType) {
                            errors +=
                                ModelError.N19(
                                    path(MAPPING_PROPERTY_PATH_ATTRIBUTE),
                                    mappingPropertyType.internal.path(),
                                    owningEntityType.internal.path(),
                                )
                        }
                    }
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type!!.internal.result
                result.mappingProperty = mappingProperty!!.internal.result
            }
        }

        internal abstract class Meta<out B : Builder> : Property.Builder.Meta<B>() {
            companion object : Meta<Builder>()
        }

        companion object {
            internal const val TYPE_NAME_ATTRIBUTE: String = "type name"

            internal const val MAPPING_PROPERTY_PATH_ATTRIBUTE: String = "mapping property path"
        }
    }
}
