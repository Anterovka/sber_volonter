package ru.sbertech.dataspace.sql.dialect

import ru.sbertech.dataspace.primitive.Primitive

interface PreparedQuery : AutoCloseable {
    var fetchSize: Int

    fun setParamValues(paramValueByName: Map<String, Primitive?>)

    fun executeQuery(): ResultReader

    fun executeUpdate(): Int

    fun addBatch()

    fun executeBatch(): IntArray
}
