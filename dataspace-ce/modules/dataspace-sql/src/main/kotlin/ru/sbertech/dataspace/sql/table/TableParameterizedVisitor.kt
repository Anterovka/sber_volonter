package ru.sbertech.dataspace.sql.table

interface TableParameterizedVisitor<in P, out R> {
    fun visit(
        simpleTable: Table.Simple,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        tablesJoin: Table.Join,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        subQuery: Table.SubQuery,
        param: P,
    ): R = throw UnsupportedOperationException()
}
