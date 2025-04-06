package ru.sbertech.dataspace.sql.dialect.postgres

import ru.sbertech.dataspace.primitive.Primitive
import ru.sbertech.dataspace.primitive.PrimitiveVisitor
import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.accept
import ru.sbertech.dataspace.sql.dialect.PreparedQuery
import ru.sbertech.dataspace.sql.dialect.ResultReader
import ru.sbertech.dataspace.sql.expr.Expr
import java.math.BigDecimal
import java.sql.Connection
import java.sql.PreparedStatement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

// TODO single thread only
internal class PostgresPreparedQuery(
    connection: Connection,
    query: String,
    private val params: Collection<Expr.Param>,
) : PreparedQuery,
    PrimitiveVisitor<Unit> {
    private val preparedStatement: PreparedStatement = connection.prepareStatement(query)

    private var paramIndex: Int = 0

    override var fetchSize
        get() = preparedStatement.fetchSize
        set(value) {
            preparedStatement.fetchSize = value
        }

    private fun setNull() {
        preparedStatement.setObject(paramIndex, null)
    }

    override fun setParamValues(paramValueByName: Map<String, Primitive?>) {
        paramIndex = 1
        params.forEach {
            when (val value = paramValueByName[it.name]) {
                null -> setNull()
                else -> value.accept(this)
            }
            ++paramIndex
        }
    }

    override fun executeQuery(): ResultReader = PostgresResultReader(preparedStatement.executeQuery())

    override fun executeUpdate(): Int = preparedStatement.executeUpdate()

    override fun addBatch() {
        preparedStatement.addBatch()
    }

    override fun executeBatch(): IntArray = preparedStatement.executeBatch()

    override fun close() {
        preparedStatement.close()
    }

    override fun visit(
        char: Char,
        param: Unit,
    ) {
        preparedStatement.setString(paramIndex, char.toString())
    }

    override fun visit(
        string: String,
        param: Unit,
    ) {
        preparedStatement.setString(paramIndex, string)
    }

    override fun visit(
        text: Text,
        param: Unit,
    ) {
        preparedStatement.setString(paramIndex, text.content)
    }

    override fun visit(
        byte: Byte,
        param: Unit,
    ) {
        preparedStatement.setByte(paramIndex, byte)
    }

    override fun visit(
        short: Short,
        param: Unit,
    ) {
        preparedStatement.setShort(paramIndex, short)
    }

    override fun visit(
        int: Int,
        param: Unit,
    ) {
        preparedStatement.setInt(paramIndex, int)
    }

    override fun visit(
        long: Long,
        param: Unit,
    ) {
        preparedStatement.setLong(paramIndex, long)
    }

    override fun visit(
        float: Float,
        param: Unit,
    ) {
        preparedStatement.setFloat(paramIndex, float)
    }

    override fun visit(
        double: Double,
        param: Unit,
    ) {
        preparedStatement.setDouble(paramIndex, double)
    }

    override fun visit(
        bigDecimal: BigDecimal,
        param: Unit,
    ) {
        preparedStatement.setBigDecimal(paramIndex, bigDecimal)
    }

    override fun visit(
        localDate: LocalDate,
        param: Unit,
    ) {
        preparedStatement.setObject(paramIndex, localDate)
    }

    override fun visit(
        localTime: LocalTime,
        param: Unit,
    ) {
        preparedStatement.setObject(paramIndex, localTime)
    }

    override fun visit(
        localDateTime: LocalDateTime,
        param: Unit,
    ) {
        preparedStatement.setObject(paramIndex, localDateTime)
    }

    override fun visit(
        offsetDateTime: OffsetDateTime,
        param: Unit,
    ) {
        preparedStatement.setObject(paramIndex, offsetDateTime)
    }

    override fun visit(
        boolean: Boolean,
        param: Unit,
    ) {
        preparedStatement.setBoolean(paramIndex, boolean)
    }

    override fun visit(
        byteArray: ByteArray,
        param: Unit,
    ) {
        preparedStatement.setBytes(paramIndex, byteArray)
    }
}
