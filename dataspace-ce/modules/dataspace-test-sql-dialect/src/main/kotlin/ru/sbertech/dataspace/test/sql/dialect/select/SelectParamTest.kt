package ru.sbertech.dataspace.test.sql.dialect.select

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.sql.subquery.SubQuery
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SelectParamTest : SelectQueryTest() {
    override val query =
        Query.Select(
            SubQuery.Simple(
                listOf(
                    SelectedExpr(Expr.Param("charParam", PrimitiveType.Char)),
                    SelectedExpr(Expr.Param("stringParam", PrimitiveType.String)),
                    SelectedExpr(Expr.Param("textParam", PrimitiveType.Text)),
                    SelectedExpr(Expr.Param("byteParam", PrimitiveType.Byte)),
                    SelectedExpr(Expr.Param("shortParam", PrimitiveType.Short)),
                    SelectedExpr(Expr.Param("intParam", PrimitiveType.Int)),
                    SelectedExpr(Expr.Param("longParam", PrimitiveType.Long)),
                    SelectedExpr(Expr.Param("floatParam", PrimitiveType.Float)),
                    SelectedExpr(Expr.Param("doubleParam", PrimitiveType.Double)),
                    SelectedExpr(Expr.Param("bigDecimalParam", PrimitiveType.BigDecimal)),
                    SelectedExpr(Expr.Param("localDateParam", PrimitiveType.LocalDate)),
                    SelectedExpr(Expr.Param("localTimeParam", PrimitiveType.LocalTime)),
                    SelectedExpr(Expr.Param("localDateTimeParam", PrimitiveType.LocalDateTime)),
                    SelectedExpr(Expr.Param("offsetDateTimeParam", PrimitiveType.OffsetDateTime)),
                    SelectedExpr(Expr.Param("booleanParam", PrimitiveType.Boolean)),
                    SelectedExpr(Expr.Param("byteArrayParam", PrimitiveType.ByteArray)),
                ),
            ),
        )

    override val paramValueByName =
        mapOf(
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

    override val columnTypes =
        arrayListOf(
            PrimitiveType.Char,
            PrimitiveType.String,
            PrimitiveType.Text,
            PrimitiveType.Byte,
            PrimitiveType.Short,
            PrimitiveType.Int,
            PrimitiveType.Long,
            PrimitiveType.Float,
            PrimitiveType.Double,
            PrimitiveType.BigDecimal,
            PrimitiveType.LocalDate,
            PrimitiveType.LocalTime,
            PrimitiveType.LocalDateTime,
            PrimitiveType.OffsetDateTime,
            PrimitiveType.Boolean,
            PrimitiveType.ByteArray,
        )

    override val expectedValueByColumnByRow =
        arrayListOf(
            arrayListOf(
                'A',
                "Hello",
                context.randomText("text"),
                123.toByte(),
                12345.toShort(),
                1234567890,
                123456789012345L,
                123.4567f,
                1234567890.12345,
                BigDecimal("1234567890123456789.1234567890123456789"),
                LocalDate.of(2021, 9, 9),
                LocalTime.of(19, 53, 10, 123456000),
                LocalDateTime.of(2021, 9, 9, 19, 53, 10, 123456000),
                OffsetDateTime.of(2021, 9, 9, 19, 53, 10, 123456000, ZoneOffset.of("+06:00")),
                true,
                byteArrayOf(1, 2, 3),
            ),
        )
}
