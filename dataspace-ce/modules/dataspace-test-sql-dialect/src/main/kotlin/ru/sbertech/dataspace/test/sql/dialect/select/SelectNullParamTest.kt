package ru.sbertech.dataspace.test.sql.dialect.select

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.sql.subquery.SubQuery

class SelectNullParamTest : SelectQueryTest() {
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
            "charParam" to null,
            "stringParam" to null,
            "textParam" to null,
            "byteParam" to null,
            "shortParam" to null,
            "intParam" to null,
            "longParam" to null,
            "floatParam" to null,
            "doubleParam" to null,
            "bigDecimalParam" to null,
            "localDateParam" to null,
            "localTimeParam" to null,
            "localDateTimeParam" to null,
            "offsetDateTimeParam" to null,
            "booleanParam" to null,
            "byteArrayParam" to null,
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
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            ),
        )
}
