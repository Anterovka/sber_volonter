package ru.sbertech.dataspace.model.property

class MappedReferenceProperty private constructor() : MappedProperty() {
    override fun <P, R> accept(
        visitor: PropertyParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : MappedProperty.Builder() {
        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : MappedProperty.Builder.Internal() {
            override lateinit var result: MappedReferenceProperty
                private set

            override val meta get() = Meta

            override val about get() = "Mapped reference property '$name'"

            override fun createResult() {
                super.createResult()
                result = MappedReferenceProperty()
            }
        }

        internal object Meta : MappedProperty.Builder.Meta<Builder>()
    }
}
