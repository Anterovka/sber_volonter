package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import org.springframework.stereotype.Component
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.Helper.addSqlExprField

@Component
class GraphQLSchemaStrExprQueryBuilder : GraphQLSchemaBaseQueryBuilder() {
    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder
    ) {
        addSqlExprField(queryTypeBuilder)
        codeRegistryBuilder.dataFetcher(
            coordinates(GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME, GraphQLSchemaHelper.STRING_EXPRESSION_NAME),
            DataFetcher { null }
        )
    }
}
