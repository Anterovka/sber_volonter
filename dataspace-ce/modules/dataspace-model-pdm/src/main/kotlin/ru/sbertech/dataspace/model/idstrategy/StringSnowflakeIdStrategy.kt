package ru.sbertech.dataspace.model.idstrategy

import ru.sbertech.dataspace.model.IdStrategy
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import sbp.sbt.model.config.snowflake.core.ShuffledUIDGenerator
import java.sql.Connection

object StringSnowflakeIdStrategy : IdStrategy {
    override fun validate(
        entityType: EntityType,
        id: UniversalValue,
    ) = throw IllegalArgumentException("Id for type '${entityType.name}' is not expected")

    override fun generate(
        entityType: EntityType,
        connection: Connection,
    ): UniversalValue = ShuffledUIDGenerator.getInstance().nextValue
}
