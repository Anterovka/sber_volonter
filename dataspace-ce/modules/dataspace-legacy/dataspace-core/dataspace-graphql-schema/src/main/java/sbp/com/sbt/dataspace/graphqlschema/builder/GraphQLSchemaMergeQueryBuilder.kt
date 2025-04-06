package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference.typeRef
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ELEMENTS_FIELD_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.MERGED_ENTITIES_COLLECTION_OBJECT_TYPE_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.MERGE_FIELD_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.COUNT_FIELD_DEFINITION
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.MergeDataFetcher
import javax.annotation.Nullable

@Component
class GraphQLSchemaMergeQueryBuilder(
    private val graphQLDataFetcherHelpers: Map<String, GraphQLDataFetcherHelper>,
    @Nullable private val securityRulesFetcher: SecurityRulesFetcher?,
) : GraphQLSchemaBaseQueryBuilder() {
    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        val mergedEntitiesCollectionObjectType =
            GraphQLObjectType
                .newObject()
                .name(MERGED_ENTITIES_COLLECTION_OBJECT_TYPE_NAME)
                .field(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(typeRef(ENTITY_INTERFACE_TYPE_NAME))))),
                ).field(COUNT_FIELD_DEFINITION)
                .build()

        queryTypeBuilder.field(
            GraphQLFieldDefinition
                .newFieldDefinition()
                .name(MERGE_FIELD_NAME)
                .argument(Helper.LIMIT_ARGUMENT)
                .argument(Helper.OFFSET_ARGUMENT)
                .argument(Helper.SORT_ARGUMENT)
                .type(nonNull(mergedEntitiesCollectionObjectType)),
        )
        codeRegistryBuilder
            .dataFetcher(
                FieldCoordinates.coordinates(QUERY_OBJECT_TYPE_NAME, MERGE_FIELD_NAME),
                MergeDataFetcher(securityRulesFetcher, graphQLDataFetcherHelpers.getValue("searchGraphQLDataFetcherHelper")),
            )

        additionalDirectives.add(Helper.MERGE_REQUEST_SPECIFICATION_DIRECTIVE)
    }
}
