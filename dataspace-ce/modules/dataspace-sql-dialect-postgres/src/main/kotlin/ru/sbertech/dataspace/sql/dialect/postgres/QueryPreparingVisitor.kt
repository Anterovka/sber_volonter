package ru.sbertech.dataspace.sql.dialect.postgres

import ru.sbertech.dataspace.common.forEachSeparated
import ru.sbertech.dataspace.common.replaceTo
import ru.sbertech.dataspace.primitive.PrimitiveVisitor
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.accept
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.sql.CombinationType
import ru.sbertech.dataspace.sql.JoinType
import ru.sbertech.dataspace.sql.LockMode
import ru.sbertech.dataspace.sql.SortCriterion
import ru.sbertech.dataspace.sql.SortNullsOrder
import ru.sbertech.dataspace.sql.SortOrder
import ru.sbertech.dataspace.sql.UnitOfTime
import ru.sbertech.dataspace.sql.expr.Expr
import ru.sbertech.dataspace.sql.expr.ExprVisitor
import ru.sbertech.dataspace.sql.query.Query
import ru.sbertech.dataspace.sql.query.QueryVisitor
import ru.sbertech.dataspace.sql.subquery.SubQuery
import ru.sbertech.dataspace.sql.subquery.SubQueryVisitor
import ru.sbertech.dataspace.sql.table.Table
import ru.sbertech.dataspace.sql.table.TableVisitor
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

internal class QueryPreparingVisitor(
    private val defaultSchema: String?,
    private val stringBuilder: StringBuilder,
    private val params: MutableCollection<Expr.Param>,
) : QueryVisitor<Unit>,
    SubQueryVisitor<Unit>,
    TableVisitor<Unit>,
    ExprVisitor<Unit>,
    PrimitiveVisitor<Unit> {
    private fun visitSchema(schema: String?) {
        (schema ?: defaultSchema)?.also { stringBuilder.append(it).append('.') }
    }

    private fun visitSortCriteria(sortCriteria: Collection<SortCriterion>) {
        if (sortCriteria.isNotEmpty()) {
            stringBuilder.append(" order by ")
            sortCriteria.forEachSeparated(stringBuilder, ", ") {
                it.expr.accept(this)
                if (it.order == SortOrder.DESC) stringBuilder.append(" desc")
                when (it.nullsOrder) {
                    SortNullsOrder.DEFAULT -> {}
                    SortNullsOrder.FIRST -> stringBuilder.append(" nulls first")
                    SortNullsOrder.LAST -> stringBuilder.append(" nulls last")
                }
            }
        }
    }

    private fun visitOffset(offset: Expr?) {
        offset?.also {
            stringBuilder.append(" offset ")
            it.accept(this)
        }
    }

    private fun visitLimit(limit: Expr?) {
        limit?.also {
            stringBuilder.append(" limit ")
            it.accept(this)
        }
    }

    private fun visitCombinedSubQuery(combinedSubQuery: SubQuery) {
        if (combinedSubQuery.accept(DoWrapCombinedSubQueryVisitor)) {
            stringBuilder.append('(')
            combinedSubQuery.accept(this)
            stringBuilder.append(')')
        } else {
            combinedSubQuery.accept(this)
        }
    }

    private fun visitExprBasedOnPriority(
        expr: Expr,
        parentExprPriority: ExprPriority,
        doWrapOnEqualPriorities: Boolean,
    ) {
        val exprPriority = expr.accept(PriorityReturningVisitor)
        if (parentExprPriority < exprPriority || (doWrapOnEqualPriorities && parentExprPriority == exprPriority)) {
            stringBuilder.append('(')
            expr.accept(this)
            stringBuilder.append(')')
        } else {
            expr.accept(this)
        }
    }

    private fun visitUnitOfTime(unitOfTime: UnitOfTime) {
        stringBuilder.append(
            when (unitOfTime) {
                UnitOfTime.MILLISECOND -> "millisecond"
                UnitOfTime.SECOND -> "second"
                UnitOfTime.MINUTE -> "minute"
                UnitOfTime.HOUR -> "hour"
                UnitOfTime.DAY -> "day"
                UnitOfTime.MONTH -> "month"
                UnitOfTime.YEAR -> "year"
            },
        )
    }

    override fun visit(
        insertQuery: Query.Insert,
        param: Unit,
    ) {
        stringBuilder.append("insert into ")
        visitSchema(insertQuery.schema)
        stringBuilder.append(insertQuery.table)
        stringBuilder.append('(')
        insertQuery.columns.forEachSeparated(stringBuilder, ", ") { stringBuilder.append(it) }
        stringBuilder.append(") values ")
        insertQuery.values.forEachSeparated(stringBuilder, ", ") { values ->
            stringBuilder.append('(')
            values.forEachSeparated(stringBuilder, ", ") { it.accept(this) }
            stringBuilder.append(')')
        }
    }

    override fun visit(
        updateQuery: Query.Update,
        param: Unit,
    ) {
        stringBuilder.append("update ")
        visitSchema(updateQuery.schema)
        stringBuilder.append(updateQuery.table)
        stringBuilder.append(" set ")
        updateQuery.valueByColumn.entries.forEachSeparated(stringBuilder, ", ") { (column, value) ->
            stringBuilder.append(column)
            stringBuilder.append(" = ")
            value.accept(this)
        }
        updateQuery.cond?.also {
            stringBuilder.append(" where ")
            it.accept(this)
        }
    }

    override fun visit(
        deleteQuery: Query.Delete,
        param: Unit,
    ) {
        stringBuilder.append("delete from ")
        visitSchema(deleteQuery.schema)
        stringBuilder.append(deleteQuery.table)
        deleteQuery.cond?.also {
            stringBuilder.append(" where ")
            it.accept(this)
        }
    }

    override fun visit(
        selectQuery: Query.Select,
        param: Unit,
    ) {
        if (selectQuery.commonTables.isNotEmpty()) {
            stringBuilder.append("with ")
            selectQuery.commonTables.forEachSeparated(stringBuilder, ", ") { commonTable ->
                stringBuilder.append(commonTable.name).append(" as (")
                commonTable.subQuery.accept(this)
                stringBuilder.append(')')
            }
            stringBuilder.append(' ')
        }
        selectQuery.subQuery.accept(this)
        selectQuery.lock?.let {
            stringBuilder.append(" for ")
            stringBuilder.append(
                when (it.mode) {
                    LockMode.FOR_UPDATE -> "update"
                    LockMode.FOR_SHARE -> "share"
                },
            )
        }
    }

    override fun visit(
        simpleSubQuery: SubQuery.Simple,
        param: Unit,
    ) {
        stringBuilder.append("select ")
        simpleSubQuery.selectedExprs.forEachSeparated(stringBuilder, ", ") { selExpr ->
            selExpr.expr.accept(this)
            selExpr.alias?.also { stringBuilder.append(' ').append(it) }
        }
        simpleSubQuery.table?.also {
            stringBuilder.append(" from ")
            it.accept(this)
        }
        simpleSubQuery.cond?.also {
            stringBuilder.append(" where ")
            it.accept(this)
        }
        if (simpleSubQuery.groupingExprs.isNotEmpty()) {
            stringBuilder.append(" group by ")
            simpleSubQuery.groupingExprs.forEachSeparated(stringBuilder, ", ") { it.accept(this) }
        }
        simpleSubQuery.groupingCond?.also {
            stringBuilder.append(" having ")
            it.accept(this)
        }
        visitSortCriteria(simpleSubQuery.sortCriteria)
        visitOffset(simpleSubQuery.offset)
        visitLimit(simpleSubQuery.limit)
    }

    override fun visit(
        subQueriesCombination: SubQuery.Combination,
        param: Unit,
    ) {
        visitCombinedSubQuery(subQueriesCombination.subQuery1)
        stringBuilder.append(
            when (subQueriesCombination.type) {
                CombinationType.UNION -> " union "
                CombinationType.INTERCEPT -> " intercept "
                CombinationType.MINUS -> " minus "
                CombinationType.UNION_ALL -> " union all "
            },
        )
        visitCombinedSubQuery(subQueriesCombination.subQuery2)
        visitSortCriteria(subQueriesCombination.sortCriteria)
        visitOffset(subQueriesCombination.offset)
        visitLimit(subQueriesCombination.limit)
    }

    override fun visit(
        simpleTable: Table.Simple,
        param: Unit,
    ) {
        visitSchema(simpleTable.schema)
        stringBuilder.append(simpleTable.name)
        simpleTable.alias?.also { stringBuilder.append(' ').append(it) }
    }

    // TODO 1. cross join; 2. join wrapping!!
    //  select * from f_entity t1 join (f_product t2 cross join f_product_aliases t3) on t1.id = t3.owner
    override fun visit(
        tablesJoin: Table.Join,
        param: Unit,
    ) {
        tablesJoin.table1.accept(this)
        stringBuilder.append(
            when (tablesJoin.type) {
                JoinType.INNER -> " join "
                JoinType.LEFT -> " left join "
                JoinType.RIGHT -> " right join "
            },
        )
        tablesJoin.table2.accept(this)
        stringBuilder.append(" on ")
        tablesJoin.cond.accept(this)
    }

    override fun visit(
        subQuery: Table.SubQuery,
        param: Unit,
    ) {
        stringBuilder.append('(')
        subQuery.subQuery.accept(this)
        stringBuilder.append(") ").append(subQuery.alias)
    }

    override fun visit(
        null0: Expr.Null,
        param: Unit,
    ) {
        stringBuilder.append("null")
    }

    override fun visit(
        value: Expr.Value,
        param: Unit,
    ) {
        value.value.accept(this)
    }

    override fun visit(
        param0: Expr.Param,
        param: Unit,
    ) {
        stringBuilder.append('?')
        params += param0
    }

    override fun visit(
        column: Expr.Column,
        param: Unit,
    ) {
        visitSchema(column.schema)
        column.table?.also { stringBuilder.append(it).append('.') }
        stringBuilder.append(column.name)
    }

    override fun visit(
        subQuery: Expr.SubQuery,
        param: Unit,
    ) {
        stringBuilder.append('(')
        subQuery.subQuery.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        neg: Expr.Neg,
        param: Unit,
    ) {
        stringBuilder.append('-')
        visitExprBasedOnPriority(neg.expr, ExprPriority.NEG, true)
    }

    override fun visit(
        abs: Expr.Abs,
        param: Unit,
    ) {
        stringBuilder.append("abs(")
        abs.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        upper: Expr.Upper,
        param: Unit,
    ) {
        stringBuilder.append("upper(")
        upper.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        lower: Expr.Lower,
        param: Unit,
    ) {
        stringBuilder.append("lower(")
        lower.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        length: Expr.Length,
        param: Unit,
    ) {
        stringBuilder.append("length(")
        length.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        trim: Expr.Trim,
        param: Unit,
    ) {
        stringBuilder.append("trim(")
        trim.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        lTrim: Expr.LTrim,
        param: Unit,
    ) {
        stringBuilder.append("ltrim(")
        lTrim.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        rTrim: Expr.RTrim,
        param: Unit,
    ) {
        stringBuilder.append("rtrim(")
        rTrim.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        round: Expr.Round,
        param: Unit,
    ) {
        stringBuilder.append("round(")
        round.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        ceil: Expr.Ceil,
        param: Unit,
    ) {
        stringBuilder.append("ceil(")
        ceil.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        floor: Expr.Floor,
        param: Unit,
    ) {
        stringBuilder.append("floor(")
        floor.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        hash: Expr.Hash,
        param: Unit,
    ) {
        stringBuilder.append("hashtext(")
        visitExprBasedOnPriority(hash.expr, ExprPriority.CAST, true)
        stringBuilder.append("::text)")
    }

    override fun visit(
        cast: Expr.Cast,
        param: Unit,
    ) {
        visitExprBasedOnPriority(cast.expr, ExprPriority.CAST, true)
        stringBuilder.append(
            when (cast.type) {
                PrimitiveType.String -> "::varchar"
                PrimitiveType.BigDecimal -> "::decimal"
                // TODO?
                else -> throw UnsupportedOperationException("Cast to type '${cast.type}' is not supported")
            },
        )
    }

    override fun visit(
        switch: Expr.Switch,
        param: Unit,
    ) {
        stringBuilder.append("case ")
        switch.expr.accept(this)
        switch.cases.forEach {
            stringBuilder.append(" when ")
            it.value.accept(this)
            stringBuilder.append(" then ")
            it.result.accept(this)
        }
        switch.elseResult?.also {
            stringBuilder.append(" else ")
            it.accept(this)
        }
        stringBuilder.append(" end")
    }

    override fun visit(
        isNull: Expr.IsNull,
        param: Unit,
    ) {
        isNull.expr.accept(this)
        stringBuilder.append(" is null")
    }

    override fun visit(
        isNotNull: Expr.IsNotNull,
        param: Unit,
    ) {
        isNotNull.expr.accept(this)
        stringBuilder.append(" is not null")
    }

    override fun visit(
        exists: Expr.Exists,
        param: Unit,
    ) {
        stringBuilder.append("exists(")
        exists.subQuery.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        min: Expr.Min,
        param: Unit,
    ) {
        stringBuilder.append("min(")
        min.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        max: Expr.Max,
        param: Unit,
    ) {
        stringBuilder.append("max(")
        max.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        sum: Expr.Sum,
        param: Unit,
    ) {
        stringBuilder.append("sum(")
        sum.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        avg: Expr.Avg,
        param: Unit,
    ) {
        stringBuilder.append("avg(")
        avg.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        count: Expr.Count,
        param: Unit,
    ) {
        stringBuilder.append("count(")
        count.expr.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        not: Expr.Not,
        param: Unit,
    ) {
        stringBuilder.append("not ")
        visitExprBasedOnPriority(not.expr, ExprPriority.NOT, true)
    }

    override fun visit(
        and: Expr.And,
        param: Unit,
    ) {
        visitExprBasedOnPriority(and.expr1, ExprPriority.AND, false)
        stringBuilder.append(" and ")
        visitExprBasedOnPriority(and.expr2, ExprPriority.AND, false)
    }

    override fun visit(
        or: Expr.Or,
        param: Unit,
    ) {
        visitExprBasedOnPriority(or.expr1, ExprPriority.OR, false)
        stringBuilder.append(" or ")
        visitExprBasedOnPriority(or.expr2, ExprPriority.OR, false)
    }

    override fun visit(
        add: Expr.Add,
        param: Unit,
    ) {
        visitExprBasedOnPriority(add.expr1, ExprPriority.ADD, false)
        stringBuilder.append(" + ")
        visitExprBasedOnPriority(add.expr2, ExprPriority.ADD, false)
    }

    override fun visit(
        sub: Expr.Sub,
        param: Unit,
    ) {
        visitExprBasedOnPriority(sub.expr1, ExprPriority.ADD, false)
        stringBuilder.append(" - ")
        visitExprBasedOnPriority(sub.expr2, ExprPriority.ADD, true)
    }

    override fun visit(
        mul: Expr.Mul,
        param: Unit,
    ) {
        visitExprBasedOnPriority(mul.expr1, ExprPriority.MUL, false)
        stringBuilder.append(" * ")
        visitExprBasedOnPriority(mul.expr2, ExprPriority.MUL, false)
    }

    override fun visit(
        div: Expr.Div,
        param: Unit,
    ) {
        visitExprBasedOnPriority(div.expr1, ExprPriority.CAST, true)
        stringBuilder.append("::decimal / ")
        visitExprBasedOnPriority(div.expr2, ExprPriority.MUL, true)
    }

    override fun visit(
        mod: Expr.Mod,
        param: Unit,
    ) {
        stringBuilder.append("mod(")
        mod.expr1.accept(this)
        stringBuilder.append(", ")
        mod.expr2.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        temporalAdd: Expr.TemporalAdd,
        param: Unit,
    ) {
        visitExprBasedOnPriority(temporalAdd.temporal, ExprPriority.ADD, false)
        stringBuilder.append(" + ")
        visitExprBasedOnPriority(temporalAdd.value, ExprPriority.MUL, false)
        stringBuilder.append(" * interval '1 ")
        visitUnitOfTime(temporalAdd.unitOfTime)
        stringBuilder.append('\'')
    }

    override fun visit(
        temporalSub: Expr.TemporalSub,
        param: Unit,
    ) {
        visitExprBasedOnPriority(temporalSub.temporal, ExprPriority.ADD, false)
        stringBuilder.append(" - ")
        visitExprBasedOnPriority(temporalSub.value, ExprPriority.MUL, false)
        stringBuilder.append(" * interval '1 ")
        visitUnitOfTime(temporalSub.unitOfTime)
        stringBuilder.append('\'')
    }

    override fun visit(
        eq: Expr.Eq,
        param: Unit,
    ) {
        eq.expr1.accept(this)
        stringBuilder.append(" = ")
        eq.expr2.accept(this)
    }

    override fun visit(
        inSubQuery: Expr.InSubQuery,
        param: Unit,
    ) {
        inSubQuery.expr.accept(this)
        stringBuilder.append(" in (")
        inSubQuery.subQuery.accept(this)
        stringBuilder.append(')')
    }

    override fun visit(
        inList: Expr.InList,
        param: Unit,
    ) {
        inList.expr.accept(this)
        stringBuilder.append(" in (")
        inList.exprs.forEachSeparated(stringBuilder, ", ") { it.accept(this) }
        stringBuilder.append(')')
    }

    override fun visit(
        char: Char,
        param: Unit,
    ) {
        when (char) {
            '\'' -> stringBuilder.append("''''")
            else -> stringBuilder.append('\'').append(char).append('\'')
        }
    }

    override fun visit(
        string: String,
        param: Unit,
    ) {
        stringBuilder.append('\'')
        string.replaceTo(stringBuilder, "'", "''")
        stringBuilder.append('\'')
    }

    override fun visit(
        text: Text,
        param: Unit,
    ): Unit = throw IllegalArgumentException()

    override fun visit(
        byte: Byte,
        param: Unit,
    ) {
        stringBuilder.append(byte)
    }

    override fun visit(
        short: Short,
        param: Unit,
    ) {
        stringBuilder.append(short)
    }

    override fun visit(
        int: Int,
        param: Unit,
    ) {
        stringBuilder.append(int)
    }

    override fun visit(
        long: Long,
        param: Unit,
    ) {
        stringBuilder.append(long)
    }

    override fun visit(
        float: Float,
        param: Unit,
    ) {
        stringBuilder.append(float)
    }

    override fun visit(
        double: Double,
        param: Unit,
    ) {
        stringBuilder.append(double)
    }

    override fun visit(
        bigDecimal: BigDecimal,
        param: Unit,
    ) {
        stringBuilder.append(bigDecimal)
    }

    // TODO поддержка дат
    override fun visit(
        localDate: LocalDate,
        param: Unit,
    ): Unit = throw IllegalArgumentException()

    override fun visit(
        localTime: LocalTime,
        param: Unit,
    ): Unit = throw IllegalArgumentException()

    override fun visit(
        localDateTime: LocalDateTime,
        param: Unit,
    ): Unit = throw IllegalArgumentException()

    override fun visit(
        offsetDateTime: OffsetDateTime,
        param: Unit,
    ): Unit = throw IllegalArgumentException()

    override fun visit(
        boolean: Boolean,
        param: Unit,
    ) {
        stringBuilder.append(boolean)
    }

    override fun visit(
        byteArray: ByteArray,
        param: Unit,
    ): Unit = throw IllegalArgumentException()
}
