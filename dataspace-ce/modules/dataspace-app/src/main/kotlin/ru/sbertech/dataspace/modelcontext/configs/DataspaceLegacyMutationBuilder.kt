package ru.sbertech.dataspace.modelcontext.configs

import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType
import ru.sbertech.dataspace.entitymanager.default.DefaultEntityManagerFactory
import ru.sbertech.dataspace.grammar.expr.ExprGrammar
import ru.sbertech.dataspace.graphql.schema.builder.SchemaBuilder
import ru.sbertech.dataspace.graphql.schema.builder.SchemaBuildingData
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.sql.dialect.postgres.PostgresDialect
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.builder.GraphQLSchemaMutationBuilder
import sbp.com.sbt.dataspace.graphqlschema.builder.MutationTypeBuilderHolder
import javax.sql.DataSource

class DataspaceLegacyMutationBuilder(
    private val dataSource: DataSource,
    private val model: Model,
    private val isManyAggregatesAllowed: Boolean,
) : GraphQLSchemaMutationBuilder {
    override fun build(
        mutationTypeBuilderHolder: MutationTypeBuilderHolder,
        additionalTypes: MutableSet<GraphQLType>,
        additionalDirectives: MutableSet<GraphQLDirective>,
        codeRegistryBuilder: GraphQLCodeRegistry.Builder,
        entitiesReadAccessJson: EntitiesReadAccessJson,
        graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
        securityRulesFetcher: SecurityRulesFetcher?,
    ) {
        val schemaBuildingData =
            SchemaBuilder(
                model,
                DefaultEntityManagerFactory(model, PostgresDialect()),
                dataSource,
                ExprGrammar(),
                graphQLDataFetcherHelper,
                securityRulesFetcher,
                isManyAggregatesAllowed,
            ).configureSchemaBuildingData(
                graphQLDataFetcherHelper,
                SchemaBuildingData(
                    codeRegistryBuilder = codeRegistryBuilder,
                    scalarTypes = ScalarTypes(true),
                ),
            )

        mutationTypeBuilderHolder.builder = schemaBuildingData.mutationTypeBuilder
        additionalTypes.addAll(schemaBuildingData.additionalTypes)
        additionalDirectives.addAll(schemaBuildingData.additionalDirectives)
    }
}
