package ru.sbertech.dataspace.grammar.expr.parse

import ru.sbertech.dataspace.common.SubCharSequence
import ru.sbertech.dataspace.common.onTrue
import ru.sbertech.dataspace.common.replaceTo
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.parser.Parser
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

internal class ExprParser(
    charSequence: CharSequence,
) : Parser<Expr>(charSequence) {
    private var savedPosition: Int = -1

    private var savedPosition2: Int = -1

    private fun skipDigits() {
        skipChars { it.isDigit }
    }

    private fun skipSpaces() {
        skipChars { it.isSpace }
    }

    private inline fun <R> skipSpacesAnd(crossinline action: () -> R): R {
        skipSpaces()
        return action()
    }

    private fun tryParseDigit(): Boolean = tryParseChar { it.isDigit }

    private fun parseDigit() {
        successfully { tryParseDigit() }
    }

    private fun tryParseString(): Boolean =
        tryParseChar('\'').onTrue {
            savedPosition = position
            do {
                val nextIndex = charSequence.indexOf("'", position)
                if (nextIndex == -1) {
                    position = charSequence.length
                    throw createParseException()
                } else {
                    position = nextIndex + 1
                }
            } while (tryParseChar('\''))
        }

    private fun parsedString(): String =
        StringBuilder().apply { charSequence.replaceTo(this, "''", "'", savedPosition, position - 1) }.toString()

    private fun tryParseNumber(): Boolean {
        savedPosition = position
        when {
            isCurChar('-') && isCurChar(1) { it.isDigit } -> position += 2
            !tryParseDigit() -> return false
        }
        skipDigits()
        if (isCurChar('.') && isCurChar(1) { it.isDigit }) {
            position += 2
            skipDigits()
            if (tryParseChar('E')) {
                tryParseChar('-')
                parseDigit()
                skipDigits()
            }
        }
        return true
    }

    private fun parsedNumber(): Number = charSequence.substring(savedPosition, position).let { it.toLongOrNull() ?: BigDecimal(it) }

    private fun tryParseDate(): Boolean =
        tryParseChar('D').onTrue {
            savedPosition = position
            repeat(4) { parseDigit() }
            parseChar('-')
            repeat(2) { parseDigit() }
            parseChar('-')
            repeat(2) { parseDigit() }
        }

    private fun parsedLocalDate(): LocalDate =
        LocalDate.parse(SubCharSequence(charSequence, savedPosition, position), DateTimeFormatter.ISO_LOCAL_DATE)

    private fun tryParseTime(): Boolean =
        tryParseChar('T').onTrue {
            savedPosition2 = position
            repeat(2) { parseDigit() }
            parseChar(':')
            repeat(2) { parseDigit() }
            parseChar(':')
            repeat(2) { parseDigit() }
            if (isCurChar('.') && isCurChar(1) { it.isDigit }) {
                position += 2
                for (index in 2..9) if (!tryParseDigit()) break
            }
        }

    private fun parsedLocalTime(): LocalTime =
        LocalTime.parse(SubCharSequence(charSequence, savedPosition2, position), DateTimeFormatter.ISO_LOCAL_TIME)

    private fun parsedLocalDateTime(): LocalDateTime =
        LocalDateTime.parse(SubCharSequence(charSequence, savedPosition, position), DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    private fun tryParseOffset(): Boolean =
        tryParseChar('Z') ||
            ((isCurChar('+') || isCurChar('-')) && isCurChar(':', 3)).onTrue {
                ++position
                repeat(2) { parseDigit() }
                ++position
                repeat(2) { parseDigit() }
            }

    private fun parsedOffsetDateTime(): OffsetDateTime =
        OffsetDateTime.parse(SubCharSequence(charSequence, savedPosition, position), DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    private fun parseName(): String {
        val startPosition = position
        parseChar { it.isLetter }
        skipChars { it.isPartOfName }
        return charSequence.substring(startPosition, position)
    }

    private fun parseBaseValue(): Expr =
        when {
            tryParseChars("it") -> Expr.Cur
            tryParseChars("root") -> Expr.Root
            tryParseString() -> Expr.Value(parsedString())
            tryParseNumber() -> Expr.Value(parsedNumber())
            tryParseDate() ->
                Expr.Value(
                    when {
                        !tryParseTime() -> parsedLocalDate()
                        !tryParseOffset() -> parsedLocalDateTime()
                        else -> parsedOffsetDateTime()
                    },
                )

            tryParseTime() -> Expr.Value(parsedLocalTime())
            tryParseChars("true") -> Expr.Value(true)
            tryParseChars("false") -> Expr.Value(false)
            tryParseChar('(') -> skipSpacesAnd { parseRootExpr() }.also { skipSpacesAnd { parseChar(')') } }
            tryParseChars("elem") -> Expr.Cur // TODO legacy
            else -> throw createParseException()
        }

    private fun parseValue(): Expr {
        var result = parseBaseValue()
        while (skipSpacesAnd { tryParseChar('.') }) result = Expr.Property(result, skipSpacesAnd { parseName() })
        return result
    }

    private fun parseComparison(): Expr {
        val expr = parseValue()
        skipSpaces()
        return when {
            tryParseChars("==") -> Expr.Eq(expr, skipSpacesAnd { parseValue() })
            else -> expr
        }
    }

    private fun parseAnd(): Expr {
        var result = parseComparison()
        while (skipSpacesAnd { tryParseChars("&&") }) result = Expr.And(result, skipSpacesAnd { parseComparison() })
        return result
    }

    private fun parseRootExpr(): Expr {
        var result = parseAnd()
        while (skipSpacesAnd { tryParseChars("||") }) result = Expr.Or(result, skipSpacesAnd { parseAnd() })
        return result
    }

    override fun parse(): Expr =
        skipSpacesAnd { parseRootExpr() }.also { skipSpacesAnd { if (isCurChar { true }) throw createParseException() } }
}
