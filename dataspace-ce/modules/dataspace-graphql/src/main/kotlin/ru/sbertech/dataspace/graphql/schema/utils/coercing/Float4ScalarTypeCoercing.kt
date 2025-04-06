package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.StringValue
import graphql.language.Value
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes.Companion.FLOAT4_SCALAR_TYPE_NAME
import java.math.BigDecimal
import java.util.Locale

object Float4ScalarTypeCoercing : BaseCoercing<Float, Float>() {
    override fun serialize(
        dataFetcherResult: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): Float =
        when (dataFetcherResult) {
            is String -> dataFetcherResult.toFloat()
            else -> (dataFetcherResult as Float).toFloat()
        }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): Float {
        when (input) {
            is Number, is String -> {
                try {
                    return BigDecimal(input.toString()).toFloat()
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseValueException(FLOAT4_SCALAR_TYPE_NAME, input)
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): Float {
        when (input) {
            is FloatValue -> {
                return input.value.toFloat()
            }

            is IntValue -> {
                return input.value.toFloat()
            }

            is StringValue -> {
                try {
                    return input.value.toFloat()
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseLiteralException(FLOAT4_SCALAR_TYPE_NAME, input)
    }
}
