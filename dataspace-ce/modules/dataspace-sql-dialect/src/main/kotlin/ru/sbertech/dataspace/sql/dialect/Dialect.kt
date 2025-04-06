package ru.sbertech.dataspace.sql.dialect

import ru.sbertech.dataspace.sql.query.Query
import java.sql.Connection

interface Dialect {
    fun prepareQuery(
        connection: Connection,
        query: Query,
    ): PreparedQuery
}
