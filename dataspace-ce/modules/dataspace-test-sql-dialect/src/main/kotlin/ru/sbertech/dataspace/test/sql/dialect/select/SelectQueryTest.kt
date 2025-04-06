package ru.sbertech.dataspace.test.sql.dialect.select

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.dialect.prepareQuery
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.test.sql.dialect.QueryTest
import ru.sbertech.dataspace.test.sql.dialect.support.assertThatPrimitive
import ru.sbertech.dataspace.test.sql.dialect.support.context.SelectQueryTestContext

abstract class SelectQueryTest : QueryTest() {
    protected open val preliminaryInsertQueries: Collection<Query.Insert> = emptyList()

    protected open val preliminaryInsertParamValueByName: Map<String, Primitive?> = emptyMap()

    protected abstract val columnTypes: List<PrimitiveType>

    protected abstract val expectedValueByColumnByRow: List<List<Primitive?>>

    override val context: SelectQueryTestContext get() = super.context as SelectQueryTestContext

    @Test
    @EnabledIf("dataSourceInitialized")
    fun `executeQuery`() {
        context.dataSource!!.connection.use { connection ->
            preliminaryInsertQueries.forEach { preliminaryInsertQuery ->
                connection.prepareQuery(preliminaryInsertQuery, context.dialect).use {
                    it.setParamValues(preliminaryInsertParamValueByName)
                    it.executeUpdate()
                }
            }
            connection.prepareQuery(query, context.dialect).use { preparedQuery ->
                preparedQuery.setParamValues(paramValueByName)
                preparedQuery.executeQuery().use {
                    expectedValueByColumnByRow.forEachIndexed { rowIndex, row ->
                        assertThat(it.next()).isTrue
                        row.forEachIndexed { columnIndex, value ->
                            assertThatPrimitive(it[columnTypes[columnIndex], columnIndex + 1])
                                .describedAs("Expected value at ($rowIndex, $columnIndex)")
                                .isEqualTo(value)
                        }
                    }
                    assertThat(it.next()).isFalse
                }
            }
            connection.rollback()
        }
    }
}
