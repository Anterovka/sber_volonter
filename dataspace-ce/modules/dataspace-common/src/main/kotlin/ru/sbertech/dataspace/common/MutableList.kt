package ru.sbertech.dataspace.common

inline fun <E> MutableList<E>.getOrSet(
    index: Int,
    crossinline defaultValue: (index: Int) -> E,
): E = get(index) ?: defaultValue(index).also { set(index, it) }
