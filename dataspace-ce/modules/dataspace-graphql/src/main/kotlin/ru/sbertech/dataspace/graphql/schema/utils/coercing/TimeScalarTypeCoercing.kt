package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes.Companion.TIME_SCALAR_TYPE_NAME
import java.time.DateTimeException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

object TimeScalarTypeCoercing : BaseCoercing<LocalTime, String>() {
    override fun serialize(
        dataFetcherResult: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): String =
        try {
            val temporalAccessor: TemporalAccessor =
                when (dataFetcherResult) {
                    is TemporalAccessor -> {
                        dataFetcherResult
                    }

                    is String -> {
                        DateTimeFormatter.ISO_LOCAL_TIME.parse(dataFetcherResult)
                    }

                    else -> {
                        throw getCoercingSerializeValueException(TIME_SCALAR_TYPE_NAME, dataFetcherResult)
                    }
                }
            DateTimeFormatter.ISO_LOCAL_TIME.format(temporalAccessor)
        } catch (e: DateTimeException) {
            throw getCoercingSerializeValueException(TIME_SCALAR_TYPE_NAME, dataFetcherResult)
        }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): LocalTime {
        when (input) {
            is LocalTime -> {
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
        throw getCoercingParseValueException(TIME_SCALAR_TYPE_NAME, input)
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): LocalTime {
        when (input) {
            is StringValue -> {
                try {
                    return parse(input.value)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseLiteralException(TIME_SCALAR_TYPE_NAME, input)
    }

    fun parse(string: String): LocalTime = LocalTime.parse(string, DateTimeFormatter.ISO_LOCAL_TIME)
}
