import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.GraphQL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import ru.sbertech.dataspace.model.Model

@Disabled
abstract class BaseGraphQLTest(
    model: Model,
) {
//    private val graphQLSchema =
//        SchemaBuilder(
//            model,
//            DefaultEntityManagerFactory(model, PostgresDialect()),
//            dataSource,
//            ExprGrammar(),
//        ).build()

    private var graphQL: GraphQL? = null // GraphQL.newGraphQL(graphQLSchema).build()

    fun executeQuery(
        query: String,
        variables: Map<String, Any?> = mapOf(),
    ): JsonNode {
        val executionInput =
            ExecutionInput
                .newExecutionInput()
                .query(query)
                .apply {
                    if (variables.isNotEmpty()) this.variables(variables)
                }.build()

        val executionResult = graphQL?.execute(executionInput)

        val result =
            objectMapper.readTree(
                ObjectMapper()
                    .writeValueAsString(executionResult?.toSpecification()),
            )

//            logger.info("\n${result.toPrettyString()}")

        println(result.toPrettyString())

        return result
    }

    fun Map<String, String>.check(resultAsJson: JsonNode) {
        this.entries
            .forEach {
//                logger.info("VALIDATE '${it.key}' WITH '${it.value}'")

                val node = resultAsJson.at(it.key)
//                if (node.isMissingNode) {
//                    logger.info("resultAsJson = {}", resultAsJson)
//                }
                assertThat(node.isMissingNode).`as`("missing node: ${it.key}").isFalse

                val nodeValue = if (node.isTextual) node.textValue() else node.toString()

                assertThat(nodeValue).isEqualTo(it.value)
            }
    }
}
