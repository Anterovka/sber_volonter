import ru.sbertech.dataspace.model.IdStrategy
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import java.sql.Connection

object ManualIdStrategy : IdStrategy {
    override fun validate(
        entityType: EntityType,
        id: UniversalValue,
    ) {
    }

    override fun generate(
        entityType: EntityType,
        connection: Connection,
    ) = throw IllegalStateException("Can't generate")
}
