package ru.sbertech.dataspace.parser

import ru.sbertech.dataspace.common.onTrue

abstract class Parser<T>(
    protected val charSequence: CharSequence,
) {
    protected var position: Int = 0

    abstract fun parse(): T

    protected fun createParseException() =
        IllegalArgumentException("TODO ${charSequence.substring(0, position)}[ ]${charSequence.substring(position)}")

    protected inline fun isCurChar(
        offset: Int = 0,
        crossinline predicate: (char: Char) -> Boolean,
    ): Boolean = (position + offset).let { it < charSequence.length && predicate(charSequence[it]) }

    protected fun isCurChar(
        char: Char,
        offset: Int = 0,
    ): Boolean = isCurChar(offset) { it == char }

    protected inline fun skipChars(crossinline predicate: (char: Char) -> Boolean) {
        while (isCurChar(predicate = predicate)) ++position
    }

    protected inline fun tryParseChar(crossinline predicate: (char: Char) -> Boolean): Boolean =
        isCurChar(predicate = predicate).onTrue { ++position }

    protected fun tryParseChar(char: Char): Boolean = tryParseChar { it == char }

    protected fun tryParseChars(chars: String): Boolean =
        tryParseChar(chars[0]).onTrue { for (index in 1..<chars.length) parseChar(chars[index]) }

    protected inline fun successfully(crossinline tryParse: () -> Boolean) {
        if (!tryParse()) throw createParseException()
    }

    protected inline fun parseChar(crossinline predicate: (char: Char) -> Boolean) {
        successfully { tryParseChar(predicate) }
    }

    protected fun parseChar(char: Char) {
        successfully { tryParseChar(char) }
    }

    protected fun parseChars(chars: String) {
        successfully { tryParseChars(chars) }
    }
}
