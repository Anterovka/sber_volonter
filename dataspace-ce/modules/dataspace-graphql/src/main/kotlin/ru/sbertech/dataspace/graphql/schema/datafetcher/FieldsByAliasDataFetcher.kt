package ru.sbertech.dataspace.graphql.schema.datafetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.PropertyDataFetcherHelper

object FieldsByAliasDataFetcher : DataFetcher<Any> {
    override fun get(environment: DataFetchingEnvironment?): Any? {
        val source = environment?.getSource<Any>()

        val fieldQualifier = environment?.field?.alias ?: environment?.field?.name
        if (source is Map<*, *>) {
            return source[fieldQualifier]
        }

        return PropertyDataFetcherHelper.getPropertyValue(fieldQualifier, source, environment?.fieldType)
    }
}
