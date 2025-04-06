package ru.sbertech.dataspace.model.idstrategy

import ru.sbertech.dataspace.model.IdStrategy
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import java.sql.Connection
import java.util.UUID

object StringUUIDIdStrategy : IdStrategy {
    override fun validate(
        entityType: EntityType,
        id: UniversalValue,
    ) = throw IllegalArgumentException("Id for type '${entityType.name}' is not expected")

    override fun generate(
        entityType: EntityType,
        connection: Connection,
    ) = UUID.randomUUID().toString()
}
