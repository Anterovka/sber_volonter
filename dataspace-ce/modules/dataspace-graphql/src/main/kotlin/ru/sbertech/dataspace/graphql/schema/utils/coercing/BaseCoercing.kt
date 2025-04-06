package ru.sbertech.dataspace.graphql.schema.utils.coercing

import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException

abstract class BaseCoercing<I, O> : Coercing<I, O> {
    fun getCoercingParseLiteralException(
        type: String,
        value: Any,
    ): CoercingParseLiteralException =
        CoercingParseLiteralException("Literal parsing error: type=$type, value=$value, value type=${value::class}")

    fun getCoercingParseValueException(
        type: String,
        value: Any,
    ): CoercingParseValueException =
        CoercingParseValueException("Value parsing error: type=$type, value=$value, value type=${value::class}")

    fun getCoercingSerializeValueException(
        type: String,
        value: Any,
    ): CoercingSerializeException =
        CoercingSerializeException("Value serializing error: type=$type, value=$value, value type=${value::class}")
}
