package ru.sbertech.dataspace.entitymanager

import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.universalvalue.UniversalValue

interface EntityManager {
    fun select(selector: Selector): UniversalValue?

    fun lock(
        entityTypeName: String,
        id: UniversalValue,
    )

    fun create(
        entityTypeName: String,
        propertyValueByName: Map<String, UniversalValue?>,
    ): UniversalValue

    fun update(
        entityTypeName: String,
        id: UniversalValue,
        propertyValueByName: Map<String, UniversalValue?>,
    )

    fun delete(
        entityTypeName: String,
        id: UniversalValue,
    )

    fun flush()
}
