package ru.sbertech.dataspace.sql.dialect

import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

interface ResultReader : AutoCloseable {
    fun next(): Boolean

    fun char(columnIndex: Int): Char

    fun string(columnIndex: Int): String

    fun text(columnIndex: Int): Text

    fun byte(columnIndex: Int): Byte

    fun short(columnIndex: Int): Short

    fun int(columnIndex: Int): Int

    fun long(columnIndex: Int): Long

    fun float(columnIndex: Int): Float

    fun double(columnIndex: Int): Double

    fun bigDecimal(columnIndex: Int): BigDecimal

    fun localDate(columnIndex: Int): LocalDate

    fun localTime(columnIndex: Int): LocalTime

    fun localDateTime(columnIndex: Int): LocalDateTime

    fun offsetDateTime(columnIndex: Int): OffsetDateTime

    fun boolean(columnIndex: Int): Boolean

    fun byteArray(columnIndex: Int): ByteArray

    fun wasNull(): Boolean

    operator fun get(
        type: PrimitiveType,
        columnIndex: Int,
    ): Primitive?
}
