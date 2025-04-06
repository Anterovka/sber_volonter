package sbp.com.sbt.dataspace.graphqlschema.builder

import com.sbt.dataspace.pdm.PdmModel
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.parser.ParserOptions
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import org.springframework.stereotype.Component
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription
import sbp.com.sbt.dataspace.graphqlschema.CalcExprFieldsPlacement
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLParserSettings
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.ENTITY_INTERFACE_TYPE_NAME
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaSettings
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.COUNT_FIELD_DEFINITION
import sbp.com.sbt.dataspace.graphqlschema.Helper.DEFAULT_ADDITIONAL_TYPES
import sbp.com.sbt.dataspace.graphqlschema.Helper.TYPE_MAPPING
import sbp.com.sbt.dataspace.graphqlschema.Helper.TYPE_RESOLVER
import sbp.com.sbt.dataspace.graphqlschema.Helper.addSqlExprField
import sbp.com.sbt.dataspace.graphqlschema.Helper.getType

@Component
class GraphQLSchemaBaseBuilder(
    private val modelDescription: ModelDescription,
    private val pdmModel: PdmModel,
    private val queryBuilders: Collection<GraphQLSchemaQueryBuilder>,
    private val mutationBuilders: Collection<GraphQLSchemaMutationBuilder>,
    private val graphQLSchemaSettings: GraphQLSchemaSettings,
    graphQLParserSettings: GraphQLParserSettings,
    private val entitiesReadAccessJson: EntitiesReadAccessJson,
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    private val securityRulesFetcher: SecurityRulesFetcher?,
) {
    init {
        configureParserOptions(graphQLParserSettings.maxCharacters, graphQLParserSettings.maxTokens)
    }

    private fun configureParserOptions(
        maxCharacters: Int,
        maxTokens: Int,
    ) {
        val parserOptions =
            ParserOptions
                .newParserOptions()
                .maxCharacters(maxCharacters)
                .maxTokens(maxTokens)
                .build()
        ParserOptions.setDefaultParserOptions(parserOptions)
        ParserOptions.setDefaultOperationParserOptions(parserOptions)
        ParserOptions.setDefaultSdlParserOptions(parserOptions)
    }

    fun build(): GraphQLSchema {
        // Creation of type __Query
        val queryTypeBuilder =
            GraphQLObjectType
                .newObject()
                .name(GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME)

        val additionalTypes: MutableSet<GraphQLType> = LinkedHashSet(DEFAULT_ADDITIONAL_TYPES)
        val additionalDirectives: MutableSet<GraphQLDirective> = LinkedHashSet()
        val codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry()

        if (graphQLSchemaSettings.isGenerateStrExprVariableDefinitionDirective ||
            graphQLSchemaSettings.isGenerateStrExprFieldDirective
        ) {
            additionalDirectives.add(
                GraphQLDirective
                    .newDirective()
                    .name(GraphQLSchemaHelper.STRING_EXPRESSION_NAME)
                    .replaceArguments(
                        TYPE_MAPPING.values
                            .asSequence()
                            .filter { it.name != "_ByteArray" }
                            .flatMap { scalarType ->
                                var argumentName = scalarType.name.removePrefix("_").replaceFirstChar { it.lowercase() }
                                argumentName =
                                    when (argumentName) {
                                        "float4" -> "float"
                                        "float" -> "double"
                                        else -> argumentName
                                    }
                                listOf(
                                    GraphQLArgument
                                        .newArgument()
                                        .name(argumentName)
                                        .type(scalarType)
                                        .build(),
                                    GraphQLArgument
                                        .newArgument()
                                        .name(argumentName + 's')
                                        .type(list(nonNull(scalarType)))
                                        .build(),
                                )
                            }.toList(),
                    ).repeatable(true)
                    .apply {
                        if (graphQLSchemaSettings.isGenerateStrExprVariableDefinitionDirective) {
                            validLocation(
                                Introspection.DirectiveLocation.VARIABLE_DEFINITION,
                            )
                        }
                        if (graphQLSchemaSettings.isGenerateStrExprFieldDirective) {
                            validLocation(Introspection.DirectiveLocation.FIELD)
                        }
                    }.build(),
            )
        }

        // creation of the basic interface _Entity with the id field
        val rootEntityInterfaceType =
            GraphQLInterfaceType
                .newInterface()
                .name(ENTITY_INTERFACE_TYPE_NAME)
                .field(
                    GraphQLFieldDefinition
                        .newFieldDefinition()
                        .name(graphQLSchemaSettings.idFieldName)
                        .type(nonNull(Scalars.GraphQLID)),
                ).build()
        additionalTypes.add(rootEntityInterfaceType)

        codeRegistryBuilder
            .typeResolver(ENTITY_INTERFACE_TYPE_NAME, TYPE_RESOLVER)

        // Creating types for the Enum model
        modelDescription.enumDescriptions.values.forEach { enumDescription ->
            val enumObjectTypeBuilder =
                GraphQLEnumType
                    .newEnum()
                    .name(GraphQLSchemaHelper.ENUM_OBJECT_TYPE_PREFIX + enumDescription.name)

            // We get an enumeration from pdmMode, as we need access to the label values of the enumeration,
            // which is not in the enumDescription
            val xmlEnumDescription = pdmModel.model.enums.first { enum -> enum.name == enumDescription.name }

            xmlEnumDescription.enumValues.forEach {
                if (it.label != null && it.label.isNotEmpty()) {
                    enumObjectTypeBuilder.value(it.name, it.name, it.label)
                } else {
                    enumObjectTypeBuilder.value(it.name)
                }
            }

            val enumObjectType = enumObjectTypeBuilder.build()
            additionalTypes.add(enumObjectType)
            val enumCollectionObjectType =
                GraphQLObjectType
                    .newObject()
                    .name(GraphQLSchemaHelper.ENUM_COLLECTION_OBJECT_TYPE_PREFIX + enumDescription.name)
                    .field(
                        GraphQLFieldDefinition
                            .newFieldDefinition()
                            .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                            .type(nonNull(list(nonNull(enumObjectType)))),
                    ).field(COUNT_FIELD_DEFINITION)
                    .build()
            additionalTypes.add(enumCollectionObjectType)
        }

        // Maybe as processing of embedded types
        modelDescription.groupDescriptions.values
            .map { it[0] }
            .forEach { groupDescription ->
                val groupObjectType =
                    GraphQLObjectType
                        .newObject()
                        .name(GraphQLSchemaHelper.GROUP_OBJECT_TYPE_PREFIX + groupDescription.groupName)
                groupDescription.primitiveDescriptions.values.forEach { primitiveDescription ->
                    groupObjectType.field(
                        GraphQLFieldDefinition
                            .newFieldDefinition()
                            .name(primitiveDescription.name)
                            .type(
                                if (primitiveDescription.enumDescription == null) {
                                    getType(TYPE_MAPPING[primitiveDescription.type], primitiveDescription.isMandatory)
                                } else {
                                    getType(
                                        GraphQLTypeReference.typeRef(
                                            GraphQLSchemaHelper.ENUM_OBJECT_TYPE_PREFIX + primitiveDescription.enumDescription.name,
                                        ),
                                        primitiveDescription.isMandatory,
                                    )
                                },
                            ),
                    )
                }
                groupDescription.referenceDescriptions.values.forEach { referenceDescription ->
                    groupObjectType.field(
                        GraphQLFieldDefinition
                            .newFieldDefinition()
                            .name(referenceDescription.name)
                            .argument(Helper.ALIAS_ARGUMENT)
                            .type(
                                getType(
                                    GraphQLTypeReference.typeRef(referenceDescription.entityDescription.name),
                                    referenceDescription.isMandatory,
                                ),
                            ),
                    )
                }
                additionalTypes.add(groupObjectType.build())
            }
        if (graphQLSchemaSettings.calcExprFieldsPlacement == CalcExprFieldsPlacement.ON_SEPARATE_TYPE) {
            additionalTypes +=
                GraphQLObjectType
                    .newObject()
                    .name(GraphQLSchemaHelper.CALCULATION_OBJECT_TYPE_NAME)
                    .fields(
                        TYPE_MAPPING.values.map { scalarType ->
                            var fieldName = scalarType.name.removePrefix("_").replaceFirstChar { it.lowercase() }
                            fieldName =
                                when (fieldName) {
                                    "float4" -> "float"
                                    "float" -> "double"
                                    else -> fieldName
                                }
                            codeRegistryBuilder.dataFetcher(
                                coordinates(GraphQLSchemaHelper.CALCULATION_OBJECT_TYPE_NAME, fieldName),
                                GraphQLSchemaBaseQueryBuilder.extendedPropertyDataFetcher,
                            )
                            GraphQLFieldDefinition
                                .newFieldDefinition()
                                .name(fieldName)
                                .argument(Helper.EXPR_ARGUMENT)
                                .type(scalarType)
                                .build()
                        },
                    ).build()
        }

        val subscriptionTypeBuilder =
            GraphQLObjectType
                .newObject()
                .name(GraphQLSchemaHelper.SUBSCRIPTION_TYPE_NAME)
        if (graphQLSchemaSettings.isGenerateStrExprField) {
            addSqlExprField(subscriptionTypeBuilder)
            codeRegistryBuilder.dataFetcher(
                coordinates(
                    GraphQLSchemaHelper.SUBSCRIPTION_TYPE_NAME,
                    GraphQLSchemaHelper.STRING_EXPRESSION_NAME,
                ),
                DataFetcher {
                    null
                },
            )
        }

        queryBuilders.forEach { it.build(queryTypeBuilder, additionalTypes, additionalDirectives, codeRegistryBuilder) }
        val mutationTypeBuilderHolder = MutationTypeBuilderHolder()
        mutationBuilders.forEach {
            it.build(
                mutationTypeBuilderHolder,
                additionalTypes,
                additionalDirectives,
                codeRegistryBuilder,
                entitiesReadAccessJson,
                graphQLDataFetcherHelper,
                securityRulesFetcher,
            )
        }

        return GraphQLSchema
            .newSchema()
            .query(queryTypeBuilder)
            .apply {
                if (mutationTypeBuilderHolder.builder != null) {
                    this.mutation(mutationTypeBuilderHolder.builder)
                }
            }.additionalTypes(additionalTypes)
            .additionalDirectives(additionalDirectives)
            .codeRegistry(codeRegistryBuilder.build())
            .build()
    }
}
