package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType

interface GraphQLSchemaQueryBuilder {
    fun build(queryTypeBuilder: GraphQLObjectType.Builder,
              additionalTypes: MutableSet<GraphQLType>,
              additionalDirectives: MutableSet<GraphQLDirective>,
              codeRegistryBuilder: GraphQLCodeRegistry.Builder)
}