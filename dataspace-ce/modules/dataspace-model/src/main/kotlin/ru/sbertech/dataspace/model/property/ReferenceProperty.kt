package ru.sbertech.dataspace.model.property

import ru.sbertech.dataspace.common.LazyNull
import ru.sbertech.dataspace.model.AbstractBuilder
import ru.sbertech.dataspace.model.Goal
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.ModelError
import ru.sbertech.dataspace.model.Relation
import ru.sbertech.dataspace.model.type.EntityType

class ReferenceProperty private constructor() : Property() {
    lateinit var type: EntityType
        private set

    var isOptional: Boolean = false
        private set

    var isSettableOnCreate: Boolean = false
        private set

    var isSettableOnUpdate: Boolean = false
        private set

    // доступно, если isPersistable
    lateinit var idProperty: Property
        private set

    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : Property.Builder() {
        var typeName: String? = null

        // default true
        var isOptional: Boolean? = null

        // default true
        var isSettableOnCreate: Boolean? = null

        // default true
        var isSettableOnUpdate: Boolean? = null

        var idPropertyOverride: Property.Override.Builder? = null

        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : Property.Builder.Internal() {
            var type: EntityType.Builder? = null
                private set

            var isOptional: Boolean = false
                private set

            var isSettableOnCreate: Boolean = false
                private set

            var isSettableOnUpdate: Boolean = false
                private set

            val idProperty: Property.Builder? get() = lazyIdProperty.value

            private lateinit var lazyIdProperty: Lazy<Property.Builder?>

            override val isSuitableForId get() = true

            override lateinit var result: ReferenceProperty
                private set

            override val meta get() = Meta

            override val about get() = "Reference property '$name'"

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.typeName = typeName
                clone.isOptional = this@Builder.isOptional
                clone.isSettableOnCreate = this@Builder.isSettableOnCreate
                clone.isSettableOnUpdate = this@Builder.isSettableOnUpdate
            }

            override fun prepare(
                goal: Goal,
                model: Model.Builder,
                parent: AbstractBuilder?,
                parentRelation: Relation<*>?,
            ) {
                lazyIdProperty = LazyNull
                super.prepare(goal, model, parent, parentRelation)
                type = typeName?.let { model.internal.type(it) as? EntityType.Builder }
                isOptional = this@Builder.isOptional ?: true
                isSettableOnCreate = this@Builder.isSettableOnCreate ?: true
                isSettableOnUpdate = this@Builder.isSettableOnUpdate ?: true
                lazyIdProperty =
                    lazy(LazyThreadSafetyMode.NONE) {
                        type?.internal?.inheritedIdProperty?.clone()?.also {
                            it.name = name
                            idPropertyOverride?.internal?.applyTo(it)
                            it.internal.prepare(goal, model, this@Builder, meta.idProperty)
                        }
                    }
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val typeName = typeName
                when {
                    typeName == null -> errors += ModelError.N1(path(TYPE_NAME_ATTRIBUTE))
                    type == null ->
                        when (val type2 = model.internal.type(typeName)) {
                            null -> errors += ModelError.N6(path(TYPE_NAME_ATTRIBUTE), typeName, model.internal.path())
                            !is EntityType.Builder -> errors += ModelError.N14(path(TYPE_NAME_ATTRIBUTE), type2.internal.path())
                            else -> {}
                        }
                }
            }

            override fun createResult() {
                super.createResult()
                result = ReferenceProperty()
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.type = type!!.internal.result
                result.isOptional = isOptional
                result.isSettableOnCreate = isSettableOnCreate
                result.isSettableOnUpdate = isSettableOnUpdate
                if (isPersistable) result.idProperty = idProperty!!.internal.result
            }
        }

        internal object Meta : Property.Builder.Meta<Builder>() {
            val idPropertyOverride: Relation<Builder> = builderRelation(ID_PROPERTY_OVERRIDE_ATTRIBUTE) { it.idPropertyOverride }

            val idProperty: Relation<Builder> = builderRelation(ID_PROPERTY_ATTRIBUTE) { it.internal.idProperty }
        }

        companion object {
            internal const val TYPE_NAME_ATTRIBUTE: String = "type name"

            internal const val IS_OPTIONAL_ATTRIBUTE: String = "is optional"

            internal const val IS_SETTABLE_ON_CREATE_ATTRIBUTE: String = "is settable on create"

            internal const val IS_SETTABLE_ON_UPDATE_ATTRIBUTE: String = "is settable on update"

            internal const val ID_PROPERTY_OVERRIDE_ATTRIBUTE: String = "id property override"

            internal const val ID_PROPERTY_ATTRIBUTE: String = "[generated] id property"
        }
    }

    object Override {
        class Builder : Property.Override.Builder() {
            val idPropertyOverride: Property.Override.Builder? = null

            override val internal = Internal()

            override fun clone() = Builder().also { internal.setCloneProperties(it) }

            internal inner class Internal : Property.Override.Builder.Internal() {
                override val meta get() = Meta

                override val about get() = "Reference property '$propertyName' override"

                override fun validate(errors: MutableCollection<ModelError>) {
                    super.validate(errors)
                    correspondingProperty
                        ?.also { if (it !is ReferenceProperty.Builder) errors += ModelError.N13(path(), it.internal.path()) }
                }
            }

            internal object Meta : Property.Override.Builder.Meta<Builder>() {
                val idPropertyOverride: Relation<Builder> = builderRelation(ID_PROPERTY_OVERRIDE_ATTRIBUTE) { it.idPropertyOverride }
            }

            companion object {
                internal const val ID_PROPERTY_OVERRIDE_ATTRIBUTE: String = "id property override"
            }
        }
    }
}
