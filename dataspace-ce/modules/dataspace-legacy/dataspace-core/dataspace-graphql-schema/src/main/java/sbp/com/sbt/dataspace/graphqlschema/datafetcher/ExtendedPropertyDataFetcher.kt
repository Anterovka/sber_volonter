package sbp.com.sbt.dataspace.graphqlschema.datafetcher

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.PropertyDataFetcherHelper

class ExtendedPropertyDataFetcher : DataFetcher<Any> {

    override fun get(environment: DataFetchingEnvironment): Any? {
        val source = environment.getSource<Any>() ?: return null

        val alias = environment.field.alias
        if (source is Map<*, *> && alias != null) {
            return source[alias]
        }

        return PropertyDataFetcherHelper.getPropertyValue(
            environment.field.name,
            source,
            environment.fieldType
        ) { environment }
    }
}
