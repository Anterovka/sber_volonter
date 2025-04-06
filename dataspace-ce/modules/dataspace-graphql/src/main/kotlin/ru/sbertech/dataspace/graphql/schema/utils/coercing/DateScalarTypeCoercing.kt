package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes.Companion.DATE_SCALAR_TYPE_NAME
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

object DateScalarTypeCoercing : BaseCoercing<LocalDate, String>() {
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
                        DateTimeFormatter.ISO_LOCAL_DATE.parse(dataFetcherResult)
                    }

                    else -> {
                        throw getCoercingSerializeValueException(DATE_SCALAR_TYPE_NAME, dataFetcherResult)
                    }
                }
            DateTimeFormatter.ISO_LOCAL_DATE.format(temporalAccessor)
        } catch (e: DateTimeException) {
            throw getCoercingSerializeValueException(DATE_SCALAR_TYPE_NAME, dataFetcherResult)
        }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): LocalDate {
        when (input) {
            is LocalDate -> {
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
        throw getCoercingParseValueException(DATE_SCALAR_TYPE_NAME, input)
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale,
    ): LocalDate {
        when (input) {
            is StringValue -> {
                try {
                    return parse(input.value)
                } catch (e: Exception) {
                    // do nothing
                }
            }
        }
        throw getCoercingParseLiteralException(DATE_SCALAR_TYPE_NAME, input)
    }

    fun parse(string: String): LocalDate = LocalDate.parse(string, DateTimeFormatter.ISO_LOCAL_DATE)
}
