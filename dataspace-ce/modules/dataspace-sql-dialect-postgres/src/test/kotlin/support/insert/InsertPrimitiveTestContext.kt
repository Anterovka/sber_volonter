package support.insert

import org.assertj.core.api.Assertions.assertThat
import ru.sbertech.dataspace.test.sql.dialect.support.context.InsertQueryTestContext
import support.postgresDataSource
import support.postgresDialect
import java.math.BigDecimal
import java.sql.Connection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class InsertPrimitiveTestContext : InsertQueryTestContext() {
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
                    assertThat(it.getString(1)).isEqualTo("A")
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getString(2)).isEqualTo("Hello")
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getString(3)).isEqualTo(randomText("text").content)
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getByte(4)).isEqualTo(123.toByte())
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getShort(5)).isEqualTo(12345.toShort())
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getInt(6)).isEqualTo(1234567890)
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getLong(7)).isEqualTo(123456789012345L)
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getFloat(8)).isEqualTo(123.4567f)
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getDouble(9)).isEqualTo(1234567890.12345)
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getBigDecimal(10)).isEqualTo(BigDecimal("1234567890123456789.1234567890123456789"))
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getObject(11, LocalDate::class.java)).isEqualTo(LocalDate.of(2021, 9, 9))
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getObject(12, LocalTime::class.java)).isEqualTo(LocalTime.of(19, 53, 10, 123456000))
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getObject(13, LocalDateTime::class.java)).isEqualTo(LocalDateTime.of(2021, 9, 9, 19, 53, 10, 123456000))
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getObject(14, OffsetDateTime::class.java))
                        .isEqualTo(OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.of("+06:00")))
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getBoolean(15)).isEqualTo(true)
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.getBytes(16)).isEqualTo(byteArrayOf(1, 2, 3))
                    assertThat(it.wasNull()).isFalse
                    assertThat(it.next()).isFalse
                }
            }
    }
}
