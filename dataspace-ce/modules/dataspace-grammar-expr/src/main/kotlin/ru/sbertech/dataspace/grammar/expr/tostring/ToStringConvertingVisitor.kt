package ru.sbertech.dataspace.grammar.expr.tostring

import ru.sbertech.dataspace.common.replaceTo
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.expr.ExprVisitor
import ru.sbertech.dataspace.primitive.PrimitiveVisitor
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.accept
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal class ToStringConvertingVisitor(
    private val stringBuilder: StringBuilder,
) : ExprVisitor<Unit>,
    PrimitiveVisitor<Unit> {
    private fun visitExprBasedOnPriority(
        expr: Expr,
        parentExprPriority: Priority,
        doWrapOnEqualPriorities: Boolean,
    ) {
        val exprPriority = expr.accept(PriorityReturningVisitor)
        if (parentExprPriority < exprPriority || (doWrapOnEqualPriorities && parentExprPriority == exprPriority)) {
            stringBuilder.append('(')
            expr.accept(this)
            stringBuilder.append(')')
        } else {
            expr.accept(this)
        }
    }

    override fun visit(
        value: Expr.Value,
        param: Unit,
    ) {
        value.value.accept(this)
    }

    override fun visit(
        cur: Expr.Cur,
        param: Unit,
    ) {
        stringBuilder.append("it")
    }

    override fun visit(
        root: Expr.Root,
        param: Unit,
    ) {
        stringBuilder.append("root")
    }

    override fun visit(
        property: Expr.Property,
        param: Unit,
    ) {
        visitExprBasedOnPriority(property.expr, Priority.VALUE, false)
        stringBuilder.append('.').append(property.name)
    }

    override fun visit(
        eq: Expr.Eq,
        param: Unit,
    ) {
        visitExprBasedOnPriority(eq.expr1, Priority.COMPARISON, false)
        stringBuilder.append("==")
        visitExprBasedOnPriority(eq.expr2, Priority.COMPARISON, false)
    }

    override fun visit(
        and: Expr.And,
        param: Unit,
    ) {
        visitExprBasedOnPriority(and.expr1, Priority.AND, false)
        stringBuilder.append("&&")
        visitExprBasedOnPriority(and.expr2, Priority.AND, false)
    }

    override fun visit(
        or: Expr.Or,
        param: Unit,
    ) {
        visitExprBasedOnPriority(or.expr1, Priority.OR, false)
        stringBuilder.append("||")
        visitExprBasedOnPriority(or.expr2, Priority.OR, false)
    }

    override fun visit(
        char: Char,
        param: Unit,
    ) {
        when (char) {
            '\'' -> stringBuilder.append("''''")
            else -> stringBuilder.append('\'').append(char).append('\'')
        }
    }

    override fun visit(
        string: String,
        param: Unit,
    ) {
        stringBuilder.append('\'')
        string.replaceTo(stringBuilder, "'", "''")
        stringBuilder.append('\'')
    }

    override fun visit(
        text: Text,
        param: Unit,
    ) = throw IllegalArgumentException()

    override fun visit(
        byte: Byte,
        param: Unit,
    ) {
        stringBuilder.append(byte)
    }

    override fun visit(
        short: Short,
        param: Unit,
    ) {
        stringBuilder.append(short)
    }

    override fun visit(
        int: Int,
        param: Unit,
    ) {
        stringBuilder.append(int)
    }

    override fun visit(
        long: Long,
        param: Unit,
    ) {
        stringBuilder.append(long)
    }

    override fun visit(
        float: Float,
        param: Unit,
    ) {
        stringBuilder.append(float)
    }

    override fun visit(
        double: Double,
        param: Unit,
    ) {
        stringBuilder.append(double)
    }

    override fun visit(
        bigDecimal: BigDecimal,
        param: Unit,
    ) {
        stringBuilder.append(bigDecimal)
    }

    override fun visit(
        localDate: LocalDate,
        param: Unit,
    ) {
        stringBuilder.append('D')
        DateTimeFormatter.ISO_LOCAL_DATE.formatTo(localDate, stringBuilder)
    }

    override fun visit(
        localTime: LocalTime,
        param: Unit,
    ) {
        stringBuilder.append('T')
        DateTimeFormatter.ISO_LOCAL_TIME.formatTo(localTime, stringBuilder)
    }

    override fun visit(
        localDateTime: LocalDateTime,
        param: Unit,
    ) {
        stringBuilder.append('D')
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.formatTo(localDateTime, stringBuilder)
    }

    override fun visit(
        offsetDateTime: OffsetDateTime,
        param: Unit,
    ) {
        stringBuilder.append('D')
        DateTimeFormatter.ISO_OFFSET_DATE_TIME.formatTo(offsetDateTime, stringBuilder)
    }

    override fun visit(
        boolean: Boolean,
        param: Unit,
    ) {
        stringBuilder.append(boolean)
    }

    override fun visit(
        byteArray: ByteArray,
        param: Unit,
    ) = throw IllegalArgumentException()
}
