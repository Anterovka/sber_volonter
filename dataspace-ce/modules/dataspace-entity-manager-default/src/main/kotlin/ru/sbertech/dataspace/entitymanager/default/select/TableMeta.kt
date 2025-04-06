package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.entitymanager.default.select.exprselector.ExprSelector
import ru.sbertech.dataspace.sql.table.Table

internal class TableMeta(
    val table: Table.Simple,
    val localId: ExprSelector,
)
