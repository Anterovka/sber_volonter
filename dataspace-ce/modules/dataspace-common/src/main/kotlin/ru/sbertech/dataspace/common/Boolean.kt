package ru.sbertech.dataspace.common

inline fun Boolean.onTrue(crossinline action: () -> Unit): Boolean = apply { if (this) action() }

inline fun Boolean.onFalse(crossinline action: () -> Unit): Boolean = apply { if (!this) action() }
