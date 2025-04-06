package sbp.com.sbt.dataspace.graphqlschema.datafetcher

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.sbt.dataspace.pdm.PdmModel
import com.sbt.pprb.ac.graph.converter.PrimitiveToStringHelper
import graphql.GraphQLError
import graphql.execution.DataFetcherResult
import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.security.graphql.SecurityRulesFetcher
import ru.sbertech.dataspace.security.utils.GraphQLSecurityContext
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper
import sbp.com.sbt.dataspace.graphqlschema.DataFetcherContainer
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.Helper

class UserQueryDataFetcher(
    graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
    private val pdmModel: PdmModel,
    private val customQueryPrefixGetter: () -> String,
    securityRulesFetcher: SecurityRulesFetcher?,
) : SecureDataFetcher(graphQLDataFetcherHelper, securityRulesFetcher) {
    fun getRequestNode(environment: DataFetchingEnvironment): JsonNode {
        val queryField = environment.field
        val queryName = queryField.name.substring(customQueryPrefixGetter.invoke().length)
        val xmlQuery = pdmModel.model.getQuery(queryName)

        val fields = Helper.OBJECT_MAPPER.createArrayNode()
        val findFirst =
            queryField.selectionSet
                .getSelectionsOfType(Field::class.java)
                .stream()
                .filter { field: Field ->
                    graphQLDataFetcherHelper.isIncluded(
                        DataFetcherContainer(environment, null, null),
                    ) { directiveName: String? ->
                        field.getDirectives(
                            directiveName,
                        )
                    }
                }.filter {
                    it.name == "elems"
                }.findFirst()
        if (findFirst.isPresent) {
            findFirst.get().selectionSet.getSelectionsOfType(Field::class.java).stream().forEach {
                fields.add(it.name)
            }
        }

        val featherJson =
            Helper.OBJECT_MAPPER
                .createObjectNode()
                .put("type", queryName)

        val dataFetcherContainer = DataFetcherContainer(environment, null, null)
        graphQLDataFetcherHelper.processSpecificationArguments(
            featherJson,
            dataFetcherContainer,
            queryField.arguments,
        )
        val entityDescription = graphQLDataFetcherHelper.modelDescription.getEntityDescription(queryName)
        graphQLDataFetcherHelper.processEntitiesCollection(
            entityDescription,
            featherJson,
            dataFetcherContainer,
            queryField.selectionSet,
        )
        graphQLDataFetcherHelper.postProcessNode(featherJson)

        val paramsInJson = featherJson.get(EntitiesReadAccessJsonHelper.PARAMS_FIELD_NAME) as ObjectNode?

        val parameters =
            if (paramsInJson != null) {
                paramsInJson
            } else {
                val createObjectNode = Helper.OBJECT_MAPPER.createObjectNode()
                featherJson.set<JsonNode>(EntitiesReadAccessJsonHelper.PARAMS_FIELD_NAME, createObjectNode)
                createObjectNode
            }

        for (arg in xmlQuery.params) {
            if (parameters[arg.name] == null && arg.defaultValue != null) {
                parameters.set<JsonNode>(arg.name, PrimitiveToStringHelper.getValue(arg.defaultValue))
            }
        }

//        featherJson.set<JsonNode>("props", fields)

        return featherJson
    }

    override fun get(
        environment: DataFetchingEnvironment,
        securityContext: GraphQLSecurityContext?,
    ): Any {
        val errors: List<GraphQLError> = ArrayList()
        val data: Any =
            graphQLDataFetcherHelper.getEntitiesCollection(
                errors,
                listOf<Any>(environment.field.name),
                graphQLDataFetcherHelper.entitiesReadAccessJson.searchEntities(
                    getRequestNode(environment),
                ),
            )
        return DataFetcherResult
            .newResult<Any>()
            .data(data)
            .errors(errors)
            .build()
    }
}
