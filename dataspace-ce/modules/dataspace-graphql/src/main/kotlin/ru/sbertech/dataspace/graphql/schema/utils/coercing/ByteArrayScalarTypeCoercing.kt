package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes.Companion.BYTE_ARRAY_SCALAR_TYPE_NAME
import java.util.Base64
import java.util.Locale

object ByteArrayScalarTypeCoercing : BaseCoercing<ByteArray, String>() {
    override fun serialize(
        dataFetcherResult: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): String =
        when (dataFetcherResult) {
            is String -> dataFetcherResult
            else -> Base64.getEncoder().encodeToString(dataFetcherResult as ByteArray)
        }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): ByteArray {
        when (input) {
            is ByteArray -> {
                return input
            }
            is String -> {
                try {
                    return parse(input)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseValueException(BYTE_ARRAY_SCALAR_TYPE_NAME, input)
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): ByteArray {
        when (input) {
            is StringValue -> {
                try {
                    return parse(input.value)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseLiteralException(BYTE_ARRAY_SCALAR_TYPE_NAME, input)
    }

    fun parse(string: String): ByteArray = Base64.getDecoder().decode(string)
}
