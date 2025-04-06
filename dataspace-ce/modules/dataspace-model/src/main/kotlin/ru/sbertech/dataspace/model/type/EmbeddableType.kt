package ru.sbertech.dataspace.model.type

class EmbeddableType private constructor() : StructuredType() {
    override fun <P, R> accept(
        visitor: TypeParameterizedVisitor<P, R>,
        param: P,
    ) = visitor.visit(this, param)

    class Builder : StructuredType.Builder() {
        override val internal = Internal()

        override fun clone() = Builder().also { internal.setCloneProperties(it) }

        internal inner class Internal : StructuredType.Builder.Internal() {
            override lateinit var result: EmbeddableType
                private set

            override val meta get() = Meta

            override val about get() = "Embeddable type '$name'"

            override fun createResult() {
                super.createResult()
                result = EmbeddableType()
            }
        }

        internal object Meta : StructuredType.Builder.Meta<Builder>()
    }
}
