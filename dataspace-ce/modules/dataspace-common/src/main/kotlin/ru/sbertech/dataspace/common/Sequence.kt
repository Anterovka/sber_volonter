package ru.sbertech.dataspace.common

fun <T> generateSequence(seed: T?): Sequence<T> = generateSequence(seed) { null }
