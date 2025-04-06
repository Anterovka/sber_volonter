package sbp.com.sbt.dataspace.graphqlschema.builder

import graphql.Scalars
import graphql.scalars.ExtendedScalars
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.model.helper.SecurityClassesHolder
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription
import sbp.com.sbt.dataspace.feather.modeldescription.TableType
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaSettings
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.CONDITION_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.DISTINCT_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.GROUP_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.GROUP_COND_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.LIMIT_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.OFFSET_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.Helper.SORT_ARGUMENT
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.SelectionDataFetcher
import javax.annotation.Nullable

@Component
@ConditionalOnBean(EntitiesReadAccessJson::class)
class GraphQLSchemaSelectionQueryBuilder(
    modelDescription: ModelDescription,
    @Qualifier("searchGraphQLDataFetcherHelper")
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    @Nullable private val securityRulesFetcher: SecurityRulesFetcher?,
    private val graphQLSchemaSettings: GraphQLSchemaSettings,
) : GraphQLSchemaModelDescriptionAwareQueryBuilder(modelDescription) {
    companion object {
        const val SELECTION_ENTITY_OBJECT_TYPE_PREFIX = "_SE_"
        const val SELECTIONS_ENTITY_COLLECTION_OBJECT_TYPE_PREFIX = "_SEC_"
        const val SELECTION_FIELD_NAME_PREFIX = "selectionBy"
    }

    override fun build(
        queryTypeBuilder: GraphQLObjectType.Builder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
    ) {
        if (!graphQLSchemaSettings.isGenerateElemsForSelection) return
        val selectionDataFetcher =
            SelectionDataFetcher(
                graphQLDataFetcherHelper,
                securityRulesFetcher,
                graphQLSchemaSettings.calcExprFieldsPlacement,
            )

        filteredEntityDescriptions {
            it.tableType != TableType.QUERY && !SecurityClassesHolder.isSecurityClass(it.name)
        }.forEach { entityDescription ->
            val fieldDefinitions = ArrayList<GraphQLFieldDefinition>()
            fieldDefinitions.add(
                GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(graphQLDataFetcherHelper.idFieldName)
                    .type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
                    .build(),
            )
            fieldDefinitions.add(
                GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(graphQLDataFetcherHelper.aggregateVersionFieldName)
                    .type(GraphQLNonNull.nonNull(ExtendedScalars.GraphQLLong))
                    .build(),
            )
            entityDescription.primitiveDescriptions.values.forEach { primitiveDescription ->
                fieldDefinitions.add(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(primitiveDescription.name)
                        .type(
                            if (primitiveDescription.enumDescription == null) {
                                Helper.getType(Helper.TYPE_MAPPING[primitiveDescription.type], primitiveDescription.isMandatory)
                            } else {
                                GraphQLTypeReference.typeRef(
                                    GraphQLSchemaHelper.ENUM_OBJECT_TYPE_PREFIX + primitiveDescription.enumDescription.name,
                                )
                            },
                        ).build(),
                )
            }

            val typeName = SELECTION_ENTITY_OBJECT_TYPE_PREFIX + entityDescription.name

            addCalcFields(graphQLSchemaSettings, fieldDefinitions)

            addExtendedPropertyDataFetchers(
                entityDescription,
                typeName,
                codeRegistryBuilder,
                fieldDefinitions,
            )

            val selectionEntityObjectType =
                GraphQLObjectType
                    .newObject()
                    .name(typeName)
                    .fields(fieldDefinitions)
                    .build()
            additionalTypes.add(selectionEntityObjectType)

            val selectionEntitiesCollectionObjectType =
                GraphQLObjectType
                    .newObject()
                    .name(SELECTIONS_ENTITY_COLLECTION_OBJECT_TYPE_PREFIX + entityDescription.name)
                    .field(
                        GraphQLFieldDefinition
                            .newFieldDefinition()
                            .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                            .type(GraphQLNonNull.nonNull(GraphQLList.list(GraphQLNonNull.nonNull(selectionEntityObjectType)))),
                    ).field(Helper.COUNT_FIELD_DEFINITION)
                    .build()
            additionalTypes.add(selectionEntitiesCollectionObjectType)

            val selectionFieldName = SELECTION_FIELD_NAME_PREFIX + entityDescription.name

            queryTypeBuilder.field(
                GraphQLFieldDefinition
                    .newFieldDefinition()
                    .name(selectionFieldName)
                    .arguments(
                        listOf(
                            CONDITION_ARGUMENT,
                            GROUP_ARGUMENT,
                            GROUP_COND_ARGUMENT,
                            LIMIT_ARGUMENT,
                            OFFSET_ARGUMENT,
                            SORT_ARGUMENT,
                            DISTINCT_ARGUMENT,
                        ),
                    ).type(GraphQLNonNull.nonNull(selectionEntitiesCollectionObjectType)),
            )

            codeRegistryBuilder
                .typeResolver(entityDescription.name, Helper.TYPE_RESOLVER)
                .dataFetcher(
                    FieldCoordinates.coordinates(GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME, selectionFieldName),
                    selectionDataFetcher,
                )
        }
    }
}
