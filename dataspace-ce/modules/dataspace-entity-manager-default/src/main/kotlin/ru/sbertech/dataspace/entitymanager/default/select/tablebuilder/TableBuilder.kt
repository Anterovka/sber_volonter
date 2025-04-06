package ru.sbertech.dataspace.entitymanager.default.select.tablebuilder

import ru.sbertech.dataspace.sql.table.Table

internal abstract class TableBuilder {
    abstract fun build(): Table
}
