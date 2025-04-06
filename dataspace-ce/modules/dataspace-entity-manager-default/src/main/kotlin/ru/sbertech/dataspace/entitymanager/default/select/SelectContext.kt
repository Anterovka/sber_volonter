package ru.sbertech.dataspace.entitymanager.default.select

import ru.sbertech.dataspace.common.arrayListOfSize
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.PositionedSimpleSubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.subquerybuilder.SubQueryBuilder
import ru.sbertech.dataspace.entitymanager.default.select.valuereader.ValueReader
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.type
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.expr.Expr as SqlExpr

internal class SelectContext(
    val model: Model,
) {
    val columnTypes: List<PrimitiveType> get() = mutableColumnTypes

    val commonTableSubQueryByName: Map<String, SubQueryBuilder> get() = mutableCommonTableSubQueryByName

    // TODO а то SubQueryMeta.valueReader - var!
    val subQueryMetaById: List<SubQueryMeta> get() = mutableSubQueryMetaById

    val paramValueByName: Map<String, Primitive> get() = mutableParamValueByName

    lateinit var resultReader: ValueReader
        private set

    private val mutableColumnTypes: MutableList<PrimitiveType> = arrayListOf()

    private var lastTableAliasIndex: Int = -1

    private var lastSelectedExprAliasIndex: Int = -1

    private var lastCacheId: Int = -1

    private val mutableCommonTableSubQueryByName: MutableMap<String, SubQueryBuilder> = linkedMapOf()

    private val mutableSubQueryMetaById: MutableList<SubQueryMeta> = arrayListOf()

    private val mutableParamValueByName: MutableMap<String, Primitive> = linkedMapOf()

    fun freeColumnIndex(
        type: PrimitiveType,
        occupiedIndexes: Set<Int>? = null,
    ): Int {
        occupiedIndexes?.also {
            mutableColumnTypes.forEachIndexed { index, columnType ->
                val columnIndex = index + 1
                if (type == columnType && columnIndex !in occupiedIndexes) return columnIndex
            }
        }
        mutableColumnTypes += type
        return mutableColumnTypes.size
    }

    fun nextTableAlias(): String = "t${++lastTableAliasIndex}"

    fun nextSelectedExprAlias(): String = "c${++lastSelectedExprAliasIndex}"

    fun nextCacheId(): Int = ++lastCacheId

    // TODO returns name
    fun addCommonTable(subQuery: SubQueryBuilder): String =
        "ct${mutableCommonTableSubQueryByName.size}".also { mutableCommonTableSubQueryByName[it] = subQuery }

    // TODO returns subQueryId
    fun addSubQuery(subQuery: PositionedSimpleSubQueryBuilder): Int {
        mutableSubQueryMetaById += SubQueryMeta(subQuery)
        return mutableSubQueryMetaById.size - 1
    }

    // TODO returns param
    fun addParam(value: Primitive): SqlExpr.Param {
        val paramName = "p${mutableParamValueByName.size}"
        mutableParamValueByName[paramName] = value
        return SqlExpr.Param(paramName, value.type)
    }

    fun addValueReader(
        subQueryId: Int,
        valueReader: ValueReader,
    ) {
        subQueryMetaById[subQueryId].valueReader = valueReader
    }

    fun setResultReader(resultReader: ValueReader) {
        this.resultReader = resultReader
    }

    fun createCacheById(): MutableList<Any?> = arrayListOfSize(lastCacheId + 1) { null }
}
