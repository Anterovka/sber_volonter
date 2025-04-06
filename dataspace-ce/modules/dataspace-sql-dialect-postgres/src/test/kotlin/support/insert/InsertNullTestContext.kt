package support.insert

import org.assertj.core.api.Assertions.assertThat
import ru.sbertech.dataspace.test.sql.dialect.support.context.InsertQueryTestContext
import support.postgresDataSource
import support.postgresDialect
import java.sql.Connection

class InsertNullTestContext : InsertQueryTestContext() {
    override val dataSource = postgresDataSource

    override val dialect = postgresDialect

    override val expectedQueryString =
        """
        insert into T_TABLE_A(C_ID,
                      C_CHAR,
                      C_STRING,
                      C_TEXT,
                      C_BYTE,
                      C_SHORT,
                      C_INT,
                      C_LONG,
                      C_FLOAT,
                      C_DOUBLE,
                      C_BIG_DECIMAL,
                      C_LOCAL_DATE,
                      C_LOCAL_TIME,
                      C_LOCAL_DATE_TIME,
                      C_OFFSET_DATE_TIME,
                      C_BOOLEAN,
                      C_BYTE_ARRAY)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

    override fun assertInsert(connection: Connection) {
        connection
            .prepareStatement(
                """
                select C_CHAR,
                       C_STRING,
                       C_TEXT,
                       C_BYTE,
                       C_SHORT,
                       C_INT,
                       C_LONG,
                       C_FLOAT,
                       C_DOUBLE,
                       C_BIG_DECIMAL,
                       C_LOCAL_DATE,
                       C_LOCAL_TIME,
                       C_LOCAL_DATE_TIME,
                       C_OFFSET_DATE_TIME,
                       C_BOOLEAN,
                       C_BYTE_ARRAY
                from T_TABLE_A
                where C_ID = ?
                """.trimIndent(),
            ).use { preparedStatement ->
                preparedStatement.setString(1, randomString("id"))
                preparedStatement.executeQuery().use {
                    assertThat(it.next()).isTrue
                    it.getString(1)
                    assertThat(it.wasNull()).isTrue
                    it.getString(2)
                    assertThat(it.wasNull()).isTrue
                    it.getString(3)
                    assertThat(it.wasNull()).isTrue
                    it.getByte(4)
                    assertThat(it.wasNull()).isTrue
                    it.getShort(5)
                    assertThat(it.wasNull()).isTrue
                    it.getInt(6)
                    assertThat(it.wasNull()).isTrue
                    it.getLong(7)
                    assertThat(it.wasNull()).isTrue
                    it.getFloat(8)
                    assertThat(it.wasNull()).isTrue
                    it.getDouble(9)
                    assertThat(it.wasNull()).isTrue
                    it.getBigDecimal(10)
                    assertThat(it.wasNull()).isTrue
                    it.getDate(11)
                    assertThat(it.wasNull()).isTrue
                    it.getTime(12)
                    assertThat(it.wasNull()).isTrue
                    it.getTimestamp(13)
                    assertThat(it.wasNull()).isTrue
                    it.getTimestamp(14)
                    assertThat(it.wasNull()).isTrue
                    it.getBoolean(15)
                    assertThat(it.wasNull()).isTrue
                    it.getBytes(16)
                    assertThat(it.wasNull()).isTrue
                    assertThat(it.next()).isFalse
                }
            }
    }
}
