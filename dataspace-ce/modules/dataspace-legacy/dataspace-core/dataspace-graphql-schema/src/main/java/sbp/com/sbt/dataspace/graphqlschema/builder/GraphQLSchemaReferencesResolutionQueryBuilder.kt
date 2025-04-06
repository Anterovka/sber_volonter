package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.Scalars
import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInterfaceType.newInterface
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference.typeRef
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.model.helper.SecurityClassesHolder
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription
import sbp.com.sbt.dataspace.feather.modeldescription.TableType
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.ReferencesResolutionDataFetcher

@Component
class GraphQLSchemaReferencesResolutionQueryBuilder(
    modelDescription: ModelDescription,
    @Qualifier("searchGraphQLDataFetcherHelper")
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
) : GraphQLSchemaModelDescriptionAwareQueryBuilder(modelDescription) {
    companion object {
        const val REFERENCE_INTERFACE_NAME = "_Reference"
        const val ENTITY_REFERENCE_OBJECT_TYPE_PREFIX = "_R_"
        const val ENTITY_ID_FIELD_NAME = "entityId"
        const val ENTITY_FIELD_NAME = "entity"
        const val RESOLVE_REFERENCES_FIELD_NAME = "resolveReferences"
        const val REFERENCE_TYPE_ARGUMENT_NAME = "referenceType"
        const val IDS_ARGUMENT_NAME = "ids"
    }

    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        additionalTypes.add(
            newInterface()
                .name(REFERENCE_INTERFACE_NAME)
                .field(
                    newFieldDefinition()
                        .name(ENTITY_ID_FIELD_NAME)
                        .type(Scalars.GraphQLString)
                        .build(),
                ).build(),
        )

        filteredEntityDescriptions {
            it.tableType != TableType.QUERY && !SecurityClassesHolder.isSecurityClass(it.name)
        }.forEach { entityDescription ->
            additionalTypes.add(
                newObject()
                    .name(ENTITY_REFERENCE_OBJECT_TYPE_PREFIX + entityDescription.name)
                    .withInterface(typeRef(REFERENCE_INTERFACE_NAME))
                    .field(
                        newFieldDefinition()
                            .name(ENTITY_ID_FIELD_NAME)
                            .type(Scalars.GraphQLString),
                    ).field(
                        newFieldDefinition()
                            .name(ENTITY_FIELD_NAME)
                            .type(typeRef(entityDescription.name)),
                    ).build(),
            )
        }

        queryTypeBuilder.field(
            newFieldDefinition()
                .name(RESOLVE_REFERENCES_FIELD_NAME)
                .argument(
                    newArgument()
                        .name(REFERENCE_TYPE_ARGUMENT_NAME)
                        .type(nonNull(Scalars.GraphQLString))
                        .build(),
                ).argument(
                    newArgument()
                        .name(IDS_ARGUMENT_NAME)
                        .type(nonNull(list(nonNull(Scalars.GraphQLID))))
                        .build(),
                ).type(nonNull(list(nonNull(typeRef(REFERENCE_INTERFACE_NAME))))),
        )

        codeRegistryBuilder
            .typeResolver(REFERENCE_INTERFACE_NAME) { env ->
                env.schema.getObjectType(env.getObject<Map<String, Any?>>()[Helper.TYPE].toString())
            }.dataFetcher(
                coordinates(GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME, RESOLVE_REFERENCES_FIELD_NAME),
                ReferencesResolutionDataFetcher(graphQLDataFetcherHelper),
            )
    }
}
