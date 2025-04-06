package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.model.type.EnumType

class EnumValue private constructor() : Component() {
    class Builder : Component.Builder() {
        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : Component.Builder.Internal() {
            override lateinit var result: EnumValue
                private set

            override val meta get() = Meta

            override val about get() = "Enum value '$name'"

            override fun validate(errors: MutableCollection<ModelError>) {
                super.validate(errors)
                name?.also { (parent as EnumType.Builder).internal.validateUniqueValueName(errors, it) { path(NAME_ATTRIBUTE) } }
            }

            override fun createResult() {
                super.createResult()
                result = EnumValue()
            }
        }

        internal object Meta : Component.Builder.Meta<Builder>()
    }
}
