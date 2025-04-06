package ru.sbertech.dataspace.test.sql.dialect

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.sql.dialect.prepareQuery
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.test.sql.dialect.support.context.QueryTestContext
import java.sql.Connection
import com.github.vertical_blank.sqlformatter.SqlFormatter.format as formatSql

abstract class QueryTest {
    protected open val context: QueryTestContext =
        Class
            .forName("support${javaClass.packageName.removePrefix(QueryTest::class.java.packageName)}.${javaClass.simpleName}Context")
            .getDeclaredConstructor()
            .newInstance() as QueryTestContext

    protected abstract val query: Query

    protected abstract val paramValueByName: Map<String, Primitive?>

    @Test
    fun `Query string`() {
        val sqlSlot = slot<String>()
        val connection =
            mockk<Connection>().apply {
                every { prepareStatement(capture(sqlSlot)) } returns mockk()
            }

        connection.prepareQuery(query, context.dialect)

        assertThat(formatSql(sqlSlot.captured)).describedAs("Formatted query string").isEqualTo(formatSql(context.expectedQueryString))
    }

    private fun dataSourceInitialized(): Boolean = context.dataSource != null
}
