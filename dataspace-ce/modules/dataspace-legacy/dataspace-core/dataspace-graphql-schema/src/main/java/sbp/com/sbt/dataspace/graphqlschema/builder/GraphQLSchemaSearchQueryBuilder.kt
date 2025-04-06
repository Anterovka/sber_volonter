package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.introspection.Introspection
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.model.helper.SecurityClassesHolder
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription
import sbp.com.sbt.dataspace.feather.modeldescription.TableType
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.SEARCH_FIELD_NAME_PREFIX
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.TYPE_RESOLVER
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.IntrospectionSchemaDataFetcher
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.IntrospectionTypeDataFetcher
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.SearchDataFetcher
import javax.annotation.Nullable

@Component
class GraphQLSchemaSearchQueryBuilder(
    modelDescription: ModelDescription,
    @Qualifier("searchGraphQLDataFetcherHelper") private val searchFetcherHelper: GraphQLDataFetcherHelper,
    @Nullable private val securityRulesFetcher: SecurityRulesFetcher?,
    @Value("\${dataspace.security.graphql.introspection.query.check-condition:#{null}}") private val introspectionCheckCondition: String?,
) : GraphQLSchemaModelDescriptionAwareQueryBuilder(modelDescription) {
    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        val searchDataFetcher =
            SearchDataFetcher(
                searchFetcherHelper,
                securityRulesFetcher,
            )

        // Redefine introspection by security wrappers for security work
        introspectionCheckCondition?.run {
            codeRegistryBuilder
                .dataFetcher(
                    FieldCoordinates.systemCoordinates(Introspection.SchemaMetaFieldDef.name),
                    IntrospectionSchemaDataFetcher(searchFetcherHelper, securityRulesFetcher, introspectionCheckCondition),
                ).dataFetcher(
                    FieldCoordinates.systemCoordinates(Introspection.TypeMetaFieldDef.name),
                    IntrospectionTypeDataFetcher(searchFetcherHelper, securityRulesFetcher, introspectionCheckCondition),
                )
        }

        filteredEntityDescriptions {
            it.tableType != TableType.QUERY && !SecurityClassesHolder.isSecurityClass(it.name)
        }.forEach { entityDescription ->
            // creating a search method (field) search<EntityName>(...)
            val searchFieldName = SEARCH_FIELD_NAME_PREFIX + entityDescription.name
            queryTypeBuilder.field(
                GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(searchFieldName)
                    .arguments(Helper.DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS)
                    // The return type of the search API is the first in the list by convention
                    .type(
                        nonNull(
                            GraphQLTypeReference.typeRef(
                                ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX + entityDescription.name,
                            ),
                        ),
                    ),
            ) // creating a search method (field) search<EntityName>(...)

            codeRegistryBuilder
                .typeResolver(entityDescription.name, TYPE_RESOLVER)
                .dataFetcher(FieldCoordinates.coordinates(QUERY_OBJECT_TYPE_NAME, searchFieldName), searchDataFetcher)
        }
    }
}
