package sbp.com.sbt.dataspace.graphqlschema.builder

import com.sbt.dataspace.pdm.PdmModel
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.UserQueryDataFetcher
import java.util.Locale
import javax.annotation.Nullable

@Component
class GraphQLSchemaUserQueriesQueryBuilder(
    private val pdmModel: PdmModel,
    @Qualifier("searchGraphQLDataFetcherHelper")
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    @Nullable private val securityRulesFetcher: SecurityRulesFetcher?,
) : GraphQLSchemaBaseQueryBuilder() {
    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        val searchDataFetcher =
            UserQueryDataFetcher(
                graphQLDataFetcherHelper,
                pdmModel,
                { GraphQLSchemaHelper.SQL_QUERY_FIELD_NAME_PREFIX },
                securityRulesFetcher,
            )

        pdmModel.model.queriesAsList.forEach { query ->
            val searchFieldName = GraphQLSchemaHelper.SQL_QUERY_FIELD_NAME_PREFIX + query.name

            val arguments = ArrayList<GraphQLArgument>()

            // If the request has parameters, then we add the corresponding argument to set them
            if (query.params.isNotEmpty()) {
                arguments.add(
                    GraphQLArgument
                        .newArgument()
                        .name(GraphQLSchemaHelper.PARAMS_ARGUMENT_NAME)
                        .type(
                            GraphQLTypeReference.typeRef(
                                GraphQLSchemaHelper.SQL_QUERY_PARAMS_TYPE_PREFIX +
                                    query.name.replaceFirstChar { it.titlecase(Locale.getDefault()) } +
                                    GraphQLSchemaHelper.SQL_QUERY_PARAMS_TYPE_SUFFIX,
                            ),
                        ).build(),
                )
            }

            arguments.addAll(DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS)

            queryTypeBuilder.field(
                GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(searchFieldName)
                    .arguments(arguments)
                    // The return type for the search API is by convention the first one in the list
                    .type(
                        GraphQLNonNull.nonNull(
                            GraphQLTypeReference.typeRef(
                                GraphQLSchemaHelper.ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + query.name,
                            ),
                        ),
                    ),
            )

            codeRegistryBuilder
                .typeResolver(query.name, Helper.TYPE_RESOLVER)
                .dataFetcher(FieldCoordinates.coordinates(GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME, searchFieldName), searchDataFetcher)
        }
    }
}
