import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.dialect.Dialect
import ru.sbertech.dataspace.sql.dialect.PreparedQuery
import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.sql.dialect.prepareQuery
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.sql.subquery.SubQuery
import java.sql.Connection

// TODO нужны ли такие тесты??
class ConnectionTest {
    @Nested
    inner class `prepareQuery` {
        @Test
        fun `executeQuery`() {
            val param = Expr.Param("param", PrimitiveType.Int)
            val query = Query.Select(SubQuery.Simple(listOf(SelectedExpr(param))))

            val connection = mockk<Connection>()
            val resultReader1 =
                mockk<ResultReader>().apply {
                    every { next() } returns true andThen false
                    every { int(any()) } returns 1
                    every { close() } just runs
                }
            val resultReader2 =
                mockk<ResultReader>().apply {
                    every { next() } returns true andThen false
                    every { int(any()) } returns 2
                    every { close() } just runs
                }
            val preparedQuery =
                mockk<PreparedQuery>().apply {
                    every { setParamValues(any()) } just runs
                    every { executeQuery() } returns resultReader1 andThen resultReader2
                    every { close() } just runs
                }
            val dialect =
                mockk<Dialect>().apply {
                    every { prepareQuery(any(), any()) } returns preparedQuery
                }

            connection.prepareQuery(query, dialect).use { preparedQuery2 ->
                preparedQuery2.setParamValues(mapOf("param" to 1))
                preparedQuery2.executeQuery().use {
                    assertThat(it.next()).isTrue
                    assertThat(it.int(1)).isEqualTo(1)
                    assertThat(it.next()).isFalse
                }
                preparedQuery2.setParamValues(mapOf("param" to 2))
                preparedQuery2.executeQuery().use {
                    assertThat(it.next()).isTrue
                    assertThat(it.int(1)).isEqualTo(2)
                    assertThat(it.next()).isFalse
                }
            }

            verifySequence {
                dialect.prepareQuery(connection, query)
                preparedQuery.setParamValues(mapOf("param" to 1))
                preparedQuery.executeQuery()
                resultReader1.next()
                resultReader1.int(1)
                resultReader1.next()
                resultReader1.close()
                preparedQuery.setParamValues(mapOf("param" to 2))
                preparedQuery.executeQuery()
                resultReader2.next()
                resultReader2.int(1)
                resultReader2.next()
                resultReader2.close()
                preparedQuery.close()
            }
        }

        @Test
        fun `executeUpdate`() {
            val param = Expr.Param("param", PrimitiveType.Int)
            val query = Query.Insert("table1", listOf("id"), listOf(listOf(param)))

            val connection = mockk<Connection>()
            val preparedQuery =
                mockk<PreparedQuery>().apply {
                    every { setParamValues(any()) } just runs
                    every { executeUpdate() } returns 1
                    every { close() } just runs
                }
            val dialect =
                mockk<Dialect>().apply {
                    every { prepareQuery(any(), any()) } returns preparedQuery
                }

            connection.prepareQuery(query, dialect).use { preparedQuery2 ->
                preparedQuery2.setParamValues(mapOf("param" to 1))
                assertThat(preparedQuery2.executeUpdate()).isEqualTo(1)
            }

            verifySequence {
                dialect.prepareQuery(connection, query)
                preparedQuery.setParamValues(mapOf("param" to 1))
                preparedQuery.executeUpdate()
                preparedQuery.close()
            }
        }

        @Test
        fun `executeBatch`() {
            val param = Expr.Param("param", PrimitiveType.Int)
            val query = Query.Insert("table1", listOf("id"), listOf(listOf(param)))

            val connection = mockk<Connection>()
            val preparedQuery =
                mockk<PreparedQuery>().apply {
                    every { setParamValues(any()) } just runs
                    every { addBatch() } just runs
                    every { executeBatch() } returns intArrayOf(1, 1)
                    every { close() } just runs
                }
            val dialect =
                mockk<Dialect>().apply {
                    every { prepareQuery(any(), any()) } returns preparedQuery
                }

            connection.prepareQuery(query, dialect).use { preparedQuery2 ->
                preparedQuery2.setParamValues(mapOf("param" to 1))
                preparedQuery2.addBatch()
                preparedQuery2.setParamValues(mapOf("param" to 2))
                preparedQuery2.addBatch()
                assertThat(preparedQuery2.executeBatch()).isEqualTo(intArrayOf(1, 1))
            }

            verifySequence {
                dialect.prepareQuery(connection, query)
                preparedQuery.setParamValues(mapOf("param" to 1))
                preparedQuery.addBatch()
                preparedQuery.setParamValues(mapOf("param" to 2))
                preparedQuery.addBatch()
                preparedQuery.executeBatch()
                preparedQuery.close()
            }
        }
    }
}
