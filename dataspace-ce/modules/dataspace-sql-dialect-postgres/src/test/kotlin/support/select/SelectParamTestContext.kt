package support.select

import ru.sbertech.dataspace.test.sql.dialect.support.context.SelectQueryTestContext
import support.postgresDataSource
import support.postgresDialect

class SelectParamTestContext : SelectQueryTestContext() {
    override val dataSource = postgresDataSource

    override val dialect = postgresDialect

    override val expectedQueryString =
        """
        select ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?,
               ?
        """.trimIndent()
}
