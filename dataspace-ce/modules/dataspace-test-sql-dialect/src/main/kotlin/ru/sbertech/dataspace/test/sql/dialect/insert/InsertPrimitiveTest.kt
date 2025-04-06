package ru.sbertech.dataspace.test.sql.dialect.insert

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.test.sql.dialect.support.TableA
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class InsertPrimitiveTest : InsertQueryTest() {
    override val query =
        Query.Insert(
            TableA.NAME,
            listOf(
                TableA.Columns.ID,
                TableA.Columns.CHAR,
                TableA.Columns.STRING,
                TableA.Columns.TEXT,
                TableA.Columns.BYTE,
                TableA.Columns.SHORT,
                TableA.Columns.INT,
                TableA.Columns.LONG,
                TableA.Columns.FLOAT,
                TableA.Columns.DOUBLE,
                TableA.Columns.BIG_DECIMAL,
                TableA.Columns.LOCAL_DATE,
                TableA.Columns.LOCAL_TIME,
                TableA.Columns.LOCAL_DATE_TIME,
                TableA.Columns.OFFSET_DATE_TIME,
                TableA.Columns.BOOLEAN,
                TableA.Columns.BYTE_ARRAY,
            ),
            listOf(
                listOf(
                    Expr.Param("idParam", PrimitiveType.String),
                    Expr.Param("charParam", PrimitiveType.Char),
                    Expr.Param("stringParam", PrimitiveType.String),
                    Expr.Param("textParam", PrimitiveType.Text),
                    Expr.Param("byteParam", PrimitiveType.Byte),
                    Expr.Param("shortParam", PrimitiveType.Short),
                    Expr.Param("intParam", PrimitiveType.Int),
                    Expr.Param("longParam", PrimitiveType.Long),
                    Expr.Param("floatParam", PrimitiveType.Float),
                    Expr.Param("doubleParam", PrimitiveType.Double),
                    Expr.Param("bigDecimalParam", PrimitiveType.BigDecimal),
                    Expr.Param("localDateParam", PrimitiveType.LocalDate),
                    Expr.Param("localTimeParam", PrimitiveType.LocalTime),
                    Expr.Param("localDateTimeParam", PrimitiveType.LocalDateTime),
                    Expr.Param("offsetDateTimeParam", PrimitiveType.OffsetDateTime),
                    Expr.Param("booleanParam", PrimitiveType.Boolean),
                    Expr.Param("byteArrayParam", PrimitiveType.ByteArray),
                ),
            ),
        )

    override val paramValueByName =
        mapOf(
            "idParam" to context.randomString("id"),
            "charParam" to 'A',
            "stringParam" to "Hello",
            "textParam" to context.randomText("text"),
            "byteParam" to 123.toByte(),
            "shortParam" to 12345.toShort(),
            "intParam" to 1234567890,
            "longParam" to 123456789012345L,
            "floatParam" to 123.4567f,
            "doubleParam" to 1234567890.12345,
            "bigDecimalParam" to BigDecimal("1234567890123456789.1234567890123456789"),
            "localDateParam" to LocalDate.of(2021, 9, 9),
            "localTimeParam" to LocalTime.of(19, 53, 10, 123456000),
            "localDateTimeParam" to LocalDateTime.of(2021, 9, 9, 19, 53, 10, 123456000),
            "offsetDateTimeParam" to OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.of("+06:00")),
            "booleanParam" to true,
            "byteArrayParam" to byteArrayOf(1, 2, 3),
        )
}
