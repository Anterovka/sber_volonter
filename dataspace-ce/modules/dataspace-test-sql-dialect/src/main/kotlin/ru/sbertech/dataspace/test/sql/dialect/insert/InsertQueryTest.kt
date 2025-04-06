package ru.sbertech.dataspace.test.sql.dialect.insert

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import ru.sbertech.dataspace.sql.dialect.prepareQuery
import ru.sbertech.dataspace.test.sql.dialect.QueryTest
import ru.sbertech.dataspace.test.sql.dialect.support.context.InsertQueryTestContext

abstract class InsertQueryTest : QueryTest() {
    override val context: InsertQueryTestContext get() = super.context as InsertQueryTestContext

    @Test
    @EnabledIf("dataSourceInitialized")
    fun `executeUpdate`() {
        context.dataSource!!.connection.use { connection ->
            connection.prepareQuery(query, context.dialect).use {
                it.setParamValues(paramValueByName)
                assertThat(it.executeUpdate()).describedAs("Changed rows").isEqualTo(1)
            }
            context.assertInsert(connection)
            connection.rollback()
        }
    }
}
