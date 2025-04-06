package sbp.com.sbt.dataspace.graphqlschema.datafetcher

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import graphql.GraphQLError
import graphql.execution.DataFetcherResult
import graphql.language.Field
import graphql.language.SelectionSet
import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription
import sbp.com.sbt.dataspace.graphqlschema.CalcExprFieldsPlacement
import sbp.com.sbt.dataspace.graphqlschema.DataFetcherContainer
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.CALC_FIELD_NAME
import sbp.com.sbt.dataspace.graphqlschema.Helper
import sbp.com.sbt.dataspace.graphqlschema.Helper.injectVariablesIntoStringExpression
import sbp.com.sbt.dataspace.graphqlschema.builder.GraphQLSchemaSelectionQueryBuilder

class SelectionDataFetcher(
    graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    securityRulesFetcher: SecurityRulesFetcher?,
    private val calcExprFieldsPlacement: CalcExprFieldsPlacement,
) : SecureDataFetcher(graphQLDataFetcherHelper, securityRulesFetcher) {
    override fun get(
        environment: DataFetchingEnvironment,
        securityContext: GraphQLSecurityContext?,
    ): Any {
        val errors: List<GraphQLError> = ArrayList()

        val entities = graphQLDataFetcherHelper.entitiesReadAccessJson.searchEntities(getRequestNode(environment, securityContext))

        val data = graphQLDataFetcherHelper.getNonTypedEntitiesCollection(entities)

        return DataFetcherResult
            .newResult<Any>()
            .data(data)
            .errors(errors)
            .build()
    }

    private fun getRequestNode(
        environment: DataFetchingEnvironment,
        variablesContainer: GraphQLSecurityContext?,
    ): JsonNode {
        val requestNode = Helper.OBJECT_MAPPER.createObjectNode()
        val selectionNode = Helper.OBJECT_MAPPER.createObjectNode()
        val queryField = environment.field

        val entityDescription =
            graphQLDataFetcherHelper.modelDescription.getEntityDescription(
                queryField.name.substring(GraphQLSchemaSelectionQueryBuilder.SELECTION_FIELD_NAME_PREFIX.length),
            )
        requestNode.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, entityDescription.name)
        val dataFetcherContainer = DataFetcherContainer(environment, variablesContainer, null)
        graphQLDataFetcherHelper.processSpecificationArguments(requestNode, dataFetcherContainer, queryField.arguments)
        graphQLDataFetcherHelper.processSelectionSet(
            dataFetcherContainer,
            queryField.selectionSet,
            { field: Field ->
                if (GraphQLSchemaHelper.ELEMENTS_FIELD_NAME == field.name) {
                    dataFetcherContainer.addStep(field, null)
                    processSelectionNode(entityDescription, selectionNode, dataFetcherContainer, field.selectionSet)
                    dataFetcherContainer.removeStep()
                } else if (GraphQLSchemaHelper.COUNT_FIELD_NAME == field.name) {
                    requestNode.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, true)
                }
            },
            { inlineFragment -> throw graphQLDataFetcherHelper.getMisplacedInlineFragmentException(inlineFragment) },
            { fragmentSpread, _ -> throw graphQLDataFetcherHelper.getMisplacedFragmentSpreadException(fragmentSpread) },
        )
        requestNode.set<JsonNode>(EntitiesReadAccessJsonHelper.PROPERTIES_SELECTION_FIELD_NAME, selectionNode)
        return requestNode
    }

    private fun processSelectionNode(
        entityDescription: EntityDescription,
        selectionNode: ObjectNode,
        dataFetcherContainer: DataFetcherContainer,
        selectionSet: SelectionSet,
    ) {
        graphQLDataFetcherHelper.processSelectionSet(
            dataFetcherContainer,
            selectionSet,
            { field: Field ->
                if (entityDescription.primitiveDescriptions.containsKey(field.name)) {
                    selectionNode.put(field.name, "it." + field.name)
                } else if (graphQLDataFetcherHelper.idFieldName == field.name) {
                    selectionNode.put(field.name, "it.\$id")
                } else if (field.name == CALC_FIELD_NAME && calcExprFieldsPlacement == CalcExprFieldsPlacement.ON_SEPARATE_TYPE) {
                    graphQLDataFetcherHelper.processCalc(
                        (if (field.alias != null) field.alias else field.name) + '#',
                        selectionNode,
                        dataFetcherContainer,
                        field.selectionSet,
                        true,
                    )
                } else if (field.name != "__typename") {
                    val expressionArgument = field.arguments.first { GraphQLSchemaHelper.EXPRESSION_ARGUMENT_NAME == it.name }
                    val value = graphQLDataFetcherHelper.getString(dataFetcherContainer, expressionArgument.value)
                    selectionNode.put(
                        field.alias ?: field.name,
                        injectVariablesIntoStringExpression(dataFetcherContainer.dataFetchingEnvironment, value),
                    )
                }
            },
            { inlineFragment -> throw graphQLDataFetcherHelper.getMisplacedInlineFragmentException(inlineFragment) },
            { fragmentSpread, _ -> throw graphQLDataFetcherHelper.getMisplacedFragmentSpreadException(fragmentSpread) },
        )
    }
}
