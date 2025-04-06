package ru.sbertech.dataspace.test.sql.dialect.support.context

import java.sql.Connection

abstract class InsertQueryTestContext : QueryTestContext() {
    abstract fun assertInsert(connection: Connection)
}
