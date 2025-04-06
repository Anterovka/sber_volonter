package ru.sbertech.dataspace.model

internal abstract class Relation<out B : AbstractBuilder>(
    val attribute: String,
) {
    abstract fun builders(builder: AbstractBuilder): Sequence<AbstractBuilder>

    override fun equals(other: Any?) = this === other || (other is Relation<*> && attribute == other.attribute)

    override fun hashCode() = attribute.hashCode()
}
