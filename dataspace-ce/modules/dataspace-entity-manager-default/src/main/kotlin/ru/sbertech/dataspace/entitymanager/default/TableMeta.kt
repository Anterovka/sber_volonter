package ru.sbertech.dataspace.entitymanager.default

import ru.sbertech.dataspace.model.property.Property
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.Lock
import ru.sbertech.dataspace.sql.SelectedExpr
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.sql.subquery.SubQuery
import ru.sbertech.dataspace.sql.table.Table
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class TableMeta(
    private val table: String,
    private val paramByColumn: Map<String, SqlExpr.Param>,
    val insertQuery: Query.Insert,
    private val idCond: SqlExpr,
    val deleteQuery: Query.Delete,
    val lockQuery: Query.Select,
) {
    fun paramValueByName(valueByColumn: Map<String, Primitive?>): Map<String, Primitive?> =
        valueByColumn.mapKeysTo(linkedMapOf()) { (column, _) -> paramByColumn.getValue(column).name }

    fun updateQuery(columns: Collection<String>) =
        Query.Update(table, paramByColumn.filterTo(linkedMapOf()) { (column, _) -> column in columns }, idCond)

    class Builder {
        lateinit var table: String

        lateinit var idProperty: Property

        private val paramByColumn: MutableMap<String, SqlExpr.Param> = linkedMapOf()

        private val paramByColumnForInsert: MutableMap<String, SqlExpr.Param> = linkedMapOf()

        fun addParam(
            column: String,
            type: PrimitiveType,
            isForInsert: Boolean,
        ) {
            val param = SqlExpr.Param("p${paramByColumn.size}", type)
            paramByColumn[column] = param
            if (isForInsert) paramByColumnForInsert[column] = param
        }

        fun build(): TableMeta {
            val idCond = idProperty.accept(IdCondReturningVisitor, paramByColumn)
            return TableMeta(
                table,
                paramByColumn,
                Query.Insert(table, paramByColumnForInsert.keys, arrayListOf(paramByColumnForInsert.values)),
                idCond,
                Query.Delete(table, idCond),
                Query.Select(
                    SubQuery.Simple(
                        arrayListOf(SelectedExpr(SqlExpr.Value(1))),
                        Table.Simple(table),
                        idCond,
                    ),
                    lock = Lock(),
                ),
            )
        }
    }
}
