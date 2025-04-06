package ru.sbertech.dataspace.security.token

import ru.sbertech.dataspace.security.utils.SecurityKind
import java.util.LinkedList

interface TokenEntity {
    val headerContent: Map<String, List<String>>
    val kind: SecurityKind

    fun addHeader(
        headerContent: MutableMap<String, MutableList<String>>,
        key: String,
        value: String,
    ) {
        val values = headerContent.computeIfAbsent(key) { LinkedList() }
        values.add(value)
    }
}
