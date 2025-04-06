package support

import ru.sbertech.dataspace.sql.dialect.Dialect
import ru.sbertech.dataspace.sql.dialect.postgres.PostgresDialect
import ru.sbertech.dataspace.test.sql.dialect.support.dataSource
import javax.sql.DataSource

val postgresDataSource: DataSource? =
    dataSource(
        "org.postgresql.Driver",
        System.getProperty("db.postgres.url"),
        System.getProperty("db.postgres.username"),
        System.getProperty("db.postgres.password"),
    )

val postgresDialect: Dialect = PostgresDialect()
