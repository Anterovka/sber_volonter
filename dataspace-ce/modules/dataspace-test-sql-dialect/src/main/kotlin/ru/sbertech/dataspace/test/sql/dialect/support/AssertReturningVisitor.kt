package ru.sbertech.dataspace.test.sql.dialect.support

import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import ru.sbertech.dataspace.primitive.PrimitiveVisitor
import ru.sbertech.dataspace.primitive.Text
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

internal object AssertReturningVisitor : PrimitiveVisitor<AbstractAssert<*, *>> {
    override fun visit(
        char: Char,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(char)

    override fun visit(
        string: String,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(string)

    override fun visit(
        text: Text,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(text)

    override fun visit(
        byte: Byte,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(byte)

    override fun visit(
        short: Short,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(short)

    override fun visit(
        int: Int,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(int)

    override fun visit(
        long: Long,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(long)

    override fun visit(
        float: Float,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(float)

    override fun visit(
        double: Double,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(double)

    override fun visit(
        bigDecimal: BigDecimal,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(bigDecimal)

    override fun visit(
        localDate: LocalDate,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(localDate)

    override fun visit(
        localTime: LocalTime,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(localTime)

    override fun visit(
        localDateTime: LocalDateTime,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(localDateTime)

    override fun visit(
        offsetDateTime: OffsetDateTime,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(offsetDateTime)

    override fun visit(
        boolean: Boolean,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(boolean)

    override fun visit(
        byteArray: ByteArray,
        param: Unit,
    ): AbstractAssert<*, *> = assertThat(byteArray)
}
