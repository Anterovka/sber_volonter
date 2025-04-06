package ru.sbertech.dataspace.grammar

interface Grammar<T> {
    fun appendTo(
        appendable: Appendable,
        value: T,
    )

    fun toString(value: T): String = StringBuilder().also { appendTo(it, value) }.toString()

    fun parse(charSequence: CharSequence): T
}
