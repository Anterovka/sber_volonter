package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes.Companion.OFFSET_DATETIME_SCALAR_TYPE_NAME
import java.time.DateTimeException
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

object OffsetDateTimeScalarTypeCoercing : BaseCoercing<OffsetDateTime, String>() {
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
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(dataFetcherResult)
                    }

                    else -> {
                        throw getCoercingSerializeValueException(OFFSET_DATETIME_SCALAR_TYPE_NAME, dataFetcherResult)
                    }
                }
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(temporalAccessor)
        } catch (e: DateTimeException) {
            throw getCoercingSerializeValueException(OFFSET_DATETIME_SCALAR_TYPE_NAME, dataFetcherResult)
        }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): OffsetDateTime {
        when (input) {
            is OffsetDateTime -> {
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
        throw getCoercingParseValueException(OFFSET_DATETIME_SCALAR_TYPE_NAME, input)
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): OffsetDateTime {
        when (input) {
            is StringValue -> {
                try {
                    return parse(input.value)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseLiteralException(OFFSET_DATETIME_SCALAR_TYPE_NAME, input)
    }

    fun parse(string: String): OffsetDateTime = OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
