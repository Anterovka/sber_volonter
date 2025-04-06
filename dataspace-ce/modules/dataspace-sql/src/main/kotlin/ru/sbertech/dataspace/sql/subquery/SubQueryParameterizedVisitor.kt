package ru.sbertech.dataspace.sql.subquery

interface SubQueryParameterizedVisitor<in P, out R> {
    fun visit(
        simpleSubQuery: SubQuery.Simple,
        param: P,
    ): R = throw UnsupportedOperationException()

    fun visit(
        subQueriesCombination: SubQuery.Combination,
        param: P,
    ): R = throw UnsupportedOperationException()
}
