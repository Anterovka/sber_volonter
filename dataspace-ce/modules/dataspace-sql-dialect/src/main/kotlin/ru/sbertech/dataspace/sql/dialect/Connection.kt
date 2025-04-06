package ru.sbertech.dataspace.sql.dialect

import ru.sbertech.dataspace.sql.query.Query
import java.sql.Connection

fun Connection.prepareQuery(
    query: Query,
    dialect: Dialect,
): PreparedQuery = dialect.prepareQuery(this, query)
