package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes.Companion.DATETIME_SCALAR_TYPE_NAME
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

object DateTimeScalarTypeCoercing : BaseCoercing<LocalDateTime, String>() {
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
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(dataFetcherResult)
                    }

                    else -> {
                        throw getCoercingSerializeValueException(DATETIME_SCALAR_TYPE_NAME, dataFetcherResult)
                    }
                }
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(temporalAccessor)
        } catch (e: DateTimeException) {
            throw getCoercingSerializeValueException(DATETIME_SCALAR_TYPE_NAME, dataFetcherResult)
        }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): LocalDateTime {
        when (input) {
            is LocalDateTime -> {
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
        throw getCoercingParseValueException(DATETIME_SCALAR_TYPE_NAME, input)
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): LocalDateTime {
        when (input) {
            is StringValue -> {
                try {
                    return parse(input.value)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseLiteralException(DATETIME_SCALAR_TYPE_NAME, input)
    }

    fun parse(string: String): LocalDateTime = LocalDateTime.parse(string, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}
