package ru.sbertech.dataspace.common

inline fun <E> Iterable<E>.forEachSeparated(
    appendable: Appendable,
    delimiter: CharSequence,
    crossinline action: (element: E) -> Unit,
) {
    var isFirst = true
    iterator().forEach {
        if (isFirst) isFirst = false else appendable.append(delimiter)
        action(it)
    }
}
