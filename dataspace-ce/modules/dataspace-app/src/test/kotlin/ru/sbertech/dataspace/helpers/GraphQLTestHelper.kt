package ru.sbertech.dataspace.helpers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.assertj.core.api.Assertions.assertThat

abstract class GraphQLTestHelper : IntegrationTestHelper() {
    abstract fun getModelId(): String

    protected fun executeQuery(
        query: String,
        variables: Map<String, Any?> = mapOf(),
        modelId: String = "4",
    ): JsonNode {
        val graphQL = getGraphQLForModel(modelId)

        val executionInput =
            ExecutionInput
                .newExecutionInput()
                .query(query)
                .apply {
                    if (variables.isNotEmpty()) this.variables(variables)
                }.build()

        val executionResult = graphQL.execute(executionInput)

        val result =
            objectMapper.readTree(
                ObjectMapper()
                    .writeValueAsString(executionResult.toSpecification()),
            )

//            logger.info("\n${result.toPrettyString()}")

        println(result.toPrettyString())

        return result
    }

    protected fun executeQuery(
        query: String,
        variables: Map<String, Any?> = mapOf(),
    ): JsonNode = executeQuery(query, variables, getModelId())

    protected fun Map<String, String>.check(resultAsJson: JsonNode) {
        this.entries
            .forEach {
//                logger.info("VALIDATE '${it.key}' WITH '${it.value}'")

                val node = resultAsJson.at(it.key)
//                if (node.isMissingNode) {
//                    logger.info("resultAsJson = {}", resultAsJson)
//                }
                assertThat(node.isMissingNode).`as`("missing node: ${it.key}").isFalse

                when {
                    node.isTextual -> assertThat(node.textValue()).describedAs(it.key).isEqualTo(it.value)
                    node.isDouble -> assertThat(node.doubleValue()).describedAs(it.key).isEqualTo(it.value.toDouble())
                    else -> assertThat(node.toString()).describedAs(it.key).isEqualTo(it.value)
                }
            }
    }

    protected fun getGraphQLForModel(modelId: String): GraphQL =
        GraphQL
            .newGraphQL(
                modelMetaInfo
                    .getModelByModelId(modelId)
                    .containersInfo
                    ?.get(0)
                    ?.context
                    ?.getBean(GraphQLSchema::class.java),
            ).build()
}
