package ru.sbertech.dataspace.graphql.command

import ru.sbertech.dataspace.primitive.Text
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import ru.sbertech.dataspace.primitive.type.PrimitiveTypeParameterizedVisitor
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.REFERENCE

open class GraphQLValueToPrimitiveConvertingVisitor(
    private val commandQualifier: String,
    private val commandRefContext: CommandRefContext,
) : PrimitiveTypeParameterizedVisitor<Any?, UniversalValue?> {
    private fun handleStringValue(propertyValue: Any?): UniversalValue? {
        if (propertyValue == null) {
            return null
        }
        if ((propertyValue as String).contains(REFERENCE)) {
            commandRefContext.registerReference(commandQualifier)
        }
        return propertyValue
    }

    override fun visit(
        stringType: PrimitiveType.String,
        param: Any?,
    ): UniversalValue? = handleStringValue(param)

    override fun visit(
        charType: PrimitiveType.Char,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        textType: PrimitiveType.Text,
        param: Any?,
    ): UniversalValue? = if (param == null) null else Text(param as String)

    override fun visit(
        byteType: PrimitiveType.Byte,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        shortType: PrimitiveType.Short,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        intType: PrimitiveType.Int,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        longType: PrimitiveType.Long,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        floatType: PrimitiveType.Float,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        doubleType: PrimitiveType.Double,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        bigDecimalType: PrimitiveType.BigDecimal,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        localDateType: PrimitiveType.LocalDate,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        localTimeType: PrimitiveType.LocalTime,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        localDateTimeType: PrimitiveType.LocalDateTime,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        offsetDateTimeType: PrimitiveType.OffsetDateTime,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        booleanType: PrimitiveType.Boolean,
        param: Any?,
    ): UniversalValue? = param

    override fun visit(
        byteArrayType: PrimitiveType.ByteArray,
        param: Any?,
    ): UniversalValue? = param
}
