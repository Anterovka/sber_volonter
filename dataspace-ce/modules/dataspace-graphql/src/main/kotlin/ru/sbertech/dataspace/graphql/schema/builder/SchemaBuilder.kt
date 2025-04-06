package ru.sbertech.dataspace.graphql.schema.builder

import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLCodeRegistry.newCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInterfaceType.newInterface
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import ru.sbertech.dataspace.entitymanager.EntityManagerFactory
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.command.CommandFactory
import ru.sbertech.dataspace.graphql.schema.datafetcher.FieldsByAliasDataFetcher
import ru.sbertech.dataspace.graphql.schema.datafetcher.PacketFieldsDataFetcher
import ru.sbertech.dataspace.graphql.schema.datafetcher.SearchFieldsDataFetcher
import ru.sbertech.dataspace.graphql.schema.utils.Arguments
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.exprArgument
import ru.sbertech.dataspace.graphql.schema.utils.Directives
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CALCULATION_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.DICTIONARY_PACKET_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ENTITY_INTERFACE_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.MUTATION_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.PACKET_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.QUERY_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.mandatoryIdFieldDefinition
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.typeResolver
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.dictionaries.hasDictionariesExtension
import ru.sbertech.dataspace.model.type.Type
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import javax.sql.DataSource

data class SchemaBuildingData(
    val queryTypeBuilder: GraphQLObjectType.Builder = newObject().name(QUERY_OBJECT_TYPE_NAME),
    val mutationTypeBuilder: GraphQLObjectType.Builder = newObject().name(MUTATION_OBJECT_TYPE_NAME),
    val codeRegistryBuilder: GraphQLCodeRegistry.Builder = newCodeRegistry(),
    val additionalTypes: MutableSet<GraphQLType> = LinkedHashSet(),
    val additionalDirectives: MutableSet<GraphQLDirective> = LinkedHashSet(),
    val scalarTypes: ScalarTypes = ScalarTypes(),
)

class SchemaBuilder(
    private val model: Model,
    private val entityManagerFactory: EntityManagerFactory,
    private val dataSource: DataSource,
    private val grammar: Grammar<Expr>,
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    private val securityRulesFetcher: SecurityRulesFetcher?,
    private val isManyAggregatesAllowed: Boolean,
) {
    fun build(): GraphQLSchema {
        val schemaBuildingData = configureSchemaBuildingData(graphQLDataFetcherHelper)
        return GraphQLSchema
            .newSchema()
            .query(schemaBuildingData.queryTypeBuilder)
            .mutation(schemaBuildingData.mutationTypeBuilder)
            .additionalTypes(schemaBuildingData.additionalTypes)
            .additionalDirectives(schemaBuildingData.additionalDirectives)
            .codeRegistry(schemaBuildingData.codeRegistryBuilder.build())
            .build()
    }

    fun configureSchemaBuildingData(
        graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
        schemaBuildingData: SchemaBuildingData = SchemaBuildingData(),
    ): SchemaBuildingData {
        val configurators =
            listOf(
                MutationConfigurator(schemaBuildingData, graphQLDataFetcherHelper),
            ).let {
                if (schemaBuildingData.scalarTypes.useLegacy) {
                    it
                } else {
                    it + QueryConfigurator(schemaBuildingData, securityRulesFetcher)
                }
            }

        model.types
            .forEach { type ->
                configurators
                    .forEach { configurator ->
                        configurator.acceptType(type)
                    }
            }

        configurators.forEach { it.allTypesAccepted() }

        return schemaBuildingData
    }

    private interface SchemaRootObjectConfigurator {
        fun acceptType(type: Type)

        fun allTypesAccepted() {}
    }

    inner class QueryConfigurator(
        private val schemaBuildingData: SchemaBuildingData,
        securityRulesFetcher: SecurityRulesFetcher?,
    ) : SchemaRootObjectConfigurator {
        private val schemaBaseTypesBuilder: SchemaBaseTypesBuilder

        init {
            val searchFieldsDataFetcher =
                SearchFieldsDataFetcher(model, entityManagerFactory, dataSource, grammar, graphQLDataFetcherHelper, securityRulesFetcher)
            schemaBaseTypesBuilder = SchemaBaseTypesBuilder(searchFieldsDataFetcher, schemaBuildingData.scalarTypes)

            // создание базового интерфейса _Entity с полем id
            val rootEntityInterfaceType =
                newInterface()
                    .name(ENTITY_INTERFACE_TYPE_NAME)
                    .field(mandatoryIdFieldDefinition)
                    .build()
            schemaBuildingData.additionalTypes.add(rootEntityInterfaceType)
            schemaBuildingData.codeRegistryBuilder.typeResolver(ENTITY_INTERFACE_TYPE_NAME, typeResolver)

            // TODO отдельный билдер?
            buildCalculationObjectType(schemaBuildingData)
        }

        override fun acceptType(type: Type) {
            type.accept(schemaBaseTypesBuilder, schemaBuildingData)
        }
    }

    private object TypeSchemaVisibleChecker {
        private val INVISIBLE_MODEL_TYPES =
            setOf(
                "SysCheckSelect",
                "SysRootSecurity",
                "SysOperation",
                "SysAdminSettings",
                "SysParamAddition",
            )

        fun check(type: Type): Boolean = !INVISIBLE_MODEL_TYPES.contains(type.name) && !type.name.endsWith("ApiCall")
    }

    inner class MutationConfigurator(
        private val schemaBuildingData: SchemaBuildingData,
        private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    ) : SchemaRootObjectConfigurator {
        private val schemaPacketFieldsBuilder: SchemaPacketFieldsBuilder
        private val packetTypeBuilder: GraphQLObjectType.Builder = newObject().name(PACKET_OBJECT_TYPE_NAME)
        private val dictionaryPacketTypeBuilder = newObject().name(DICTIONARY_PACKET_TYPE_NAME)
        private val commandFactoryByFieldName = hashMapOf<String, CommandFactory>()

        init {
            schemaPacketFieldsBuilder =
                SchemaPacketFieldsBuilder(
                    model,
                    commandFactoryByFieldName,
                    packetTypeBuilder,
                    dictionaryPacketTypeBuilder,
                    grammar,
                    schemaBuildingData.scalarTypes,
                    graphQLDataFetcherHelper,
                )

            schemaBuildingData.additionalTypes.addAll(schemaBuildingData.scalarTypes.incTypeByScalarType.values)
        }

        override fun acceptType(type: Type) {
            if (TypeSchemaVisibleChecker.check(type)) {
                type.accept(schemaPacketFieldsBuilder, schemaBuildingData)
            }
        }

        override fun allTypesAccepted() {
            val packetFieldsDataFetcher =
                PacketFieldsDataFetcher(
                    model,
                    commandFactoryByFieldName,
                    entityManagerFactory,
                    dataSource,
                    isManyAggregatesAllowed,
                    graphQLDataFetcherHelper,
                    securityRulesFetcher,
                )

            schemaBuildingData.codeRegistryBuilder
                .dataFetcher(
                    coordinates(
                        MUTATION_OBJECT_TYPE_NAME,
                        SchemaHelper.PACKET_FIELD_NAME,
                    ),
                    packetFieldsDataFetcher,
                )

            packetTypeBuilder.field(SchemaHelper.isIdempotenceResponseFieldDefinition)
            packetTypeBuilder.field(SchemaHelper.aggregateVersionFieldDefinition)

            schemaBuildingData.mutationTypeBuilder.field(
                newFieldDefinition()
                    .name(SchemaHelper.PACKET_FIELD_NAME)
                    .argument(Arguments.idempotencePacketIdArgument)
                    .argument(Arguments.aggregateVersionArgument)
                    .type(packetTypeBuilder),
            )

            if (model.hasDictionariesExtension) {
                schemaBuildingData.codeRegistryBuilder
                    .dataFetcher(
                        coordinates(
                            MUTATION_OBJECT_TYPE_NAME,
                            SchemaHelper.DICTIONARY_PACKET_FIELD_NAME,
                        ),
                        packetFieldsDataFetcher,
                    )

                schemaBuildingData.mutationTypeBuilder.field(
                    newFieldDefinition()
                        .name(SchemaHelper.DICTIONARY_PACKET_FIELD_NAME)
                        .type(dictionaryPacketTypeBuilder),
                )
            }

            schemaBuildingData.additionalDirectives.add(Directives.dependsOnByGetDirective)
            schemaBuildingData.additionalDirectives.add(Directives.dependsOnByUpdateOrCreateDirective)
        }
    }

    private fun buildCalculationObjectType(schemaBuildingData: SchemaBuildingData) {
        val calculationObjectType =
            newObject()
                .name(CALCULATION_OBJECT_TYPE_NAME)
                .fields(
                    schemaBuildingData.scalarTypes.typeMapping.values.map { scalarType ->
                        var fieldName = scalarType.name.removePrefix("_").replaceFirstChar { it.lowercase() }
                        fieldName =
                            when (fieldName) {
                                "float4" -> "float"
                                "float" -> "double"
                                else -> fieldName
                            }
                        schemaBuildingData.codeRegistryBuilder.dataFetcher(
                            coordinates(CALCULATION_OBJECT_TYPE_NAME, fieldName),
                            FieldsByAliasDataFetcher,
                        )
                        newFieldDefinition()
                            .name(fieldName)
                            .argument(exprArgument)
                            .type(scalarType)
                            .build()
                    },
                ).build()

        schemaBuildingData.additionalTypes.add(calculationObjectType)
    }
}
