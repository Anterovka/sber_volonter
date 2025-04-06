package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

data class MutationTypeBuilderHolder(
    var builder: GraphQLObjectType.Builder? = null,
)

interface GraphQLSchemaMutationBuilder {
    fun build(
        mutationTypeBuilderHolder: MutationTypeBuilderHolder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
        entitiesReadAccessJson: EntitiesReadAccessJson,
        graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
        securityRulesFetcher: SecurityRulesFetcher?,
    )
}
