package ru.sbertech.dataspace.common

data object LazyNull : Lazy<Nothing?> {
    override val value get() = null

    override fun isInitialized() = true
}
