package ru.sbertech.dataspace.entitymanager.default.select.tablebuilder

import ru.sbertech.dataspace.sql.table.Table

internal class SimpleTableBuilder(
    var table: Table,
) : TableBuilder() {
    override fun build() = table
}
