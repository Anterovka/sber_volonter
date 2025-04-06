package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.model.property.CollectionProperty
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.type.EntityType

abstract class Component internal constructor() {
    lateinit var name: String
        private set

    var description: String? = null
        private set

    abstract class Builder internal constructor() : AbstractBuilder() {
        var name: String? = null

        var description: String? = null

        abstract override val internal: Internal

        abstract override fun clone(): Builder

        internal abstract inner class Internal : AbstractBuilder.Internal() {
            abstract override val result: Component

            abstract override val meta: Meta<Builder>

            override fun setCloneProperties(clone: AbstractBuilder) {
                super.setCloneProperties(clone as Builder)
                clone.name = name
                clone.description = description
            }

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                val name = name
                when {
                    belongsTo(EntityType.Builder.Meta.tableIdProperty) -> {}
                    belongsTo(CollectionProperty.Builder.Meta.ownerIdProperty) -> {}
                    belongsTo(ReferenceProperty.Builder.Meta.idProperty) -> {}
                    belongsTo(EmbeddedType.Builder.Meta.properties) -> {}
                    name == null -> errors += ModelError.N1(path(NAME_ATTRIBUTE))
                    !name.matches(REGEX_FOR_NAME) -> errors += ModelError.N5(path(NAME_ATTRIBUTE), name, REGEX_FOR_NAME)
                }
            }

            override fun setResultProperties() {
                super.setResultProperties()
                result.name = name!!
                result.description = description
            }
        }

        internal abstract class Meta<out B : Builder> : AbstractBuilder.Meta<B>() {
            companion object : Meta<Builder>()
        }

        companion object {
            internal const val NAME_ATTRIBUTE: String = "name"

            internal const val DESCRIPTION_ATTRIBUTE: String = "description"
        }
    }
}

private val REGEX_FOR_NAME: Regex = "[A-Za-z][A-Za-z0-9_]*".toRegex()
