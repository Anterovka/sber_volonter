package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.PrimitiveSelector

internal class SelectedExprMeta(
    val expr: PrimitiveSelector,
    val alias: String?,
    val columnIndex: Int,
)
