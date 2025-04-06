package ru.sbertech.dataspace.model.idstrategy

import ru.sbertech.dataspace.model.IdStrategy
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import java.sql.Connection

class AutoOnEmptyIdStrategy(
    private val base: IdStrategy,
) : IdStrategy {
    override fun validate(
        entityType: EntityType,
        id: UniversalValue,
    ) {
    }

    override fun generate(
        entityType: EntityType,
        connection: Connection,
    ) = base.generate(entityType, connection)
}
