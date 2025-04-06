package ru.sbertech.dataspace.model

internal class TableMeta {
    var tableAttributePath: (() -> String)? = null

    val attributePathByColumn: MutableMap<String, () -> String> = linkedMapOf()
}
