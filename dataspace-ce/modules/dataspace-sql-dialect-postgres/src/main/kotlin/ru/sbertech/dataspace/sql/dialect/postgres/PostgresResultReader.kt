package ru.sbertech.dataspace.sql.dialect.postgres

import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.primitive.type.PrimitiveTypeVisitor
import ru.sbertech.dataspace.sql.dialect.ResultReader
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

// TODO single thread only
internal class PostgresResultReader(
    private val resultSet: ResultSet,
) : ResultReader,
    PrimitiveTypeVisitor<Primitive> {
    private var columnIndex: Int = 0

    override fun next() = resultSet.next()

    override fun char(columnIndex: Int) = resultSet.getString(columnIndex)?.get(0) ?: CHAR_STUB

    override fun string(columnIndex: Int) = resultSet.getString(columnIndex) ?: STRING_STUB

    override fun text(columnIndex: Int) = resultSet.getString(columnIndex)?.let { Text(it) } ?: TEXT_STUB

    override fun byte(columnIndex: Int) = resultSet.getByte(columnIndex)

    override fun short(columnIndex: Int) = resultSet.getShort(columnIndex)

    override fun int(columnIndex: Int) = resultSet.getInt(columnIndex)

    override fun long(columnIndex: Int) = resultSet.getLong(columnIndex)

    override fun float(columnIndex: Int) = resultSet.getFloat(columnIndex)

    override fun double(columnIndex: Int) = resultSet.getDouble(columnIndex)

    override fun bigDecimal(columnIndex: Int) = resultSet.getBigDecimal(columnIndex) ?: BIG_DECIMAL_STUB

    override fun localDate(columnIndex: Int) = resultSet.getObject(columnIndex, LocalDate::class.java) ?: LOCAL_DATE_STUB

    override fun localTime(columnIndex: Int) = resultSet.getObject(columnIndex, LocalTime::class.java) ?: LOCAL_TIME_STUB

    override fun localDateTime(columnIndex: Int) = resultSet.getObject(columnIndex, LocalDateTime::class.java) ?: LOCAL_DATE_TIME_STUB

    override fun offsetDateTime(columnIndex: Int) = resultSet.getObject(columnIndex, OffsetDateTime::class.java) ?: OFFSET_DATE_TIME_STUB

    override fun boolean(columnIndex: Int) = resultSet.getBoolean(columnIndex)

    override fun byteArray(columnIndex: Int) = resultSet.getBytes(columnIndex) ?: BYTE_ARRAY_STUB

    override fun wasNull() = resultSet.wasNull()

    override fun get(
        type: PrimitiveType,
        columnIndex: Int,
    ): Primitive? {
        this.columnIndex = columnIndex
        return type.accept(this).takeUnless { wasNull() }
    }

    override fun close() {
        resultSet.close()
    }

    override fun visit(
        charType: PrimitiveType.Char,
        param: Unit,
    ) = char(columnIndex)

    override fun visit(
        stringType: PrimitiveType.String,
        param: Unit,
    ) = string(columnIndex)

    override fun visit(
        textType: PrimitiveType.Text,
        param: Unit,
    ) = text(columnIndex)

    override fun visit(
        byteType: PrimitiveType.Byte,
        param: Unit,
    ) = byte(columnIndex)

    override fun visit(
        shortType: PrimitiveType.Short,
        param: Unit,
    ) = short(columnIndex)

    override fun visit(
        intType: PrimitiveType.Int,
        param: Unit,
    ) = int(columnIndex)

    override fun visit(
        longType: PrimitiveType.Long,
        param: Unit,
    ) = long(columnIndex)

    override fun visit(
        floatType: PrimitiveType.Float,
        param: Unit,
    ) = float(columnIndex)

    override fun visit(
        doubleType: PrimitiveType.Double,
        param: Unit,
    ) = double(columnIndex)

    override fun visit(
        bigDecimalType: PrimitiveType.BigDecimal,
        param: Unit,
    ) = bigDecimal(columnIndex)

    override fun visit(
        localDateType: PrimitiveType.LocalDate,
        param: Unit,
    ) = localDate(columnIndex)

    override fun visit(
        localTimeType: PrimitiveType.LocalTime,
        param: Unit,
    ) = localTime(columnIndex)

    override fun visit(
        localDateTimeType: PrimitiveType.LocalDateTime,
        param: Unit,
    ) = localDateTime(columnIndex)

    override fun visit(
        offsetDateTimeType: PrimitiveType.OffsetDateTime,
        param: Unit,
    ) = offsetDateTime(columnIndex)

    override fun visit(
        booleanType: PrimitiveType.Boolean,
        param: Unit,
    ) = boolean(columnIndex)

    override fun visit(
        byteArrayType: PrimitiveType.ByteArray,
        param: Unit,
    ) = byteArray(columnIndex)
}

private const val CHAR_STUB: Char = '\u0000'

private const val STRING_STUB: String = ""

private val TEXT_STUB = Text("")

private val BIG_DECIMAL_STUB: BigDecimal = BigDecimal.ZERO

private val LOCAL_DATE_STUB: LocalDate = LocalDate.MIN

private val LOCAL_TIME_STUB: LocalTime = LocalTime.MIDNIGHT

private val LOCAL_DATE_TIME_STUB: LocalDateTime = LocalDateTime.MIN

private val OFFSET_DATE_TIME_STUB: OffsetDateTime = OffsetDateTime.MIN

private val BYTE_ARRAY_STUB: ByteArray = byteArrayOf()
