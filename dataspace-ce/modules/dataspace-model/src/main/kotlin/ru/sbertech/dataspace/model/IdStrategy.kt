package ru.sbertech.dataspace.model

import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import java.sql.Connection

interface IdStrategy {
    fun validate(
        entityType: EntityType,
        id: UniversalValue,
    )

    fun generate(
        entityType: EntityType,
        connection: Connection,
    ): UniversalValue
}
