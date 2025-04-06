package ru.sbertech.dataspace.common

inline fun <E> arrayListOfSize(
    size: Int,
    crossinline init: (index: Int) -> E,
) = ArrayList<E>(size).apply { repeat(size) { add(init(it)) } }
