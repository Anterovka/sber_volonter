package ru.sbertech.dataspace.test.sql.dialect.insert

import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.test.sql.dialect.support.TableA

class InsertNullTest : InsertQueryTest() {
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
}
