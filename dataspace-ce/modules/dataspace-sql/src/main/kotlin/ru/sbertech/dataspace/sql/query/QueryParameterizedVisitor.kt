package ru.sbertech.dataspace.sql.query

interface QueryParameterizedVisitor<in P, out R> {
    fun visit(
        insertQuery: Query.Insert,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        updateQuery: Query.Update,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        deleteQuery: Query.Delete,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        selectQuery: Query.Select,
        param: P,
    ): R = throw UnsupportedOperationException()
}
