package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.model.property.Property

internal inline fun <V> validateUniqueValue(
    errors: MutableCollection<ModelError>,
    value: V,
    noinline attributePath: () -> String,
    crossinline getAnotherAttributePath: () -> (() -> String)?,
    crossinline setAnotherAttributePath: (attributePath: () -> String) -> Unit,
) {
    when (val anotherAttributePath = getAnotherAttributePath()) {
        null -> setAnotherAttributePath(attributePath)
        else -> errors += ModelError.N3(attributePath(), value, anotherAttributePath())
    }
}

internal fun <V> validateUniqueValue(
    errors: MutableCollection<ModelError>,
    value: V,
    attributePath: () -> String,
    attributePathByValue: MutableMap<V, () -> String>,
) {
    validateUniqueValue(errors, value, attributePath, { attributePathByValue[value] }, { attributePathByValue[value] = it })
}

internal fun Collection<Property.Override.Builder>.applyTo(embeddedType: EmbeddedType.Builder) {
    forEach { propertyOverride ->
        propertyOverride.propertyName?.let { embeddedType.internal.property(it) }?.also { propertyOverride.internal.applyTo(it) }
    }
}
