package ru.sbertech.dataspace.security.graphql

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.util.RawValue
import graphql.util.IdGenerator.uuid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import ru.sbertech.dataspace.helpers.GraphQLTestHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper
import sbp.com.sbt.dataspace.graphqlschema.datafetcher.SecureDataFetcher

@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class GraphQlWithSecurityTests : GraphQLTestHelper() {
    override fun getModelId() = "4"

    companion object {
        private const val MODEL_ID_FROM_DB = "4"
        private const val MODEL_ID_FROM_FILE = "3"
    }

    @Test
    fun differentOperationsWithSameNameTest() {
        // Suppose there is a mutation that every user can perform
        val query1 =
            """
            mutation differentMutationsWithSameName(${'$'}ppCode:String) {
                packet {
                    p: createProduct(input: {
                        code: ${'$'}ppCode
                    }) {
                        id
                    }
                }
            }
            """.trimIndent()

        // The allowEmptyChecks flag is set in the security condition, which means there are no checks.
        val mutationNode =
            objectMapper
                .createObjectNode()
                .put("name", "differentMutationsWithSameName")
                .put("allowEmptyChecks", true)
                .put("body", query1)

        // Creating an entry in the security database.
        val resp = post("/models/$MODEL_ID_FROM_DB/security/permissions/operations", mutationNode)
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        val uuid = uuid()
        val objectNode =
            JsonNodeFactory.instance
                .objectNode()
                .put("query", query1)
                .putRawValue("variables", RawValue("{ \"ppCode\": \"$uuid\" }"))

        val post =
            post("/models/$MODEL_ID_FROM_DB/graphql", objectNode) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_2",
                )
            }
        val result = post.body!!
        assertThat(result.at("/errors").isMissingNode).isTrue

        // Create a mutation with the same name but a different body
        val query2 =
            """
            mutation differentMutationsWithSameName(${'$'}ppCode:String) {
                packet {
                    p: createTestEntity(input: {
                        pString: ${'$'}ppCode
                    }) {
                        id
                    }
                }
            }
            """.trimIndent()

        val objectNode2 =
            JsonNodeFactory.instance
                .objectNode()
                .put("query", query2)
                .putRawValue("variables", RawValue("{ \"ppCode\": \"$uuid\" }"))

        val post2 =
            post("/models/$MODEL_ID_FROM_DB/graphql", objectNode2) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_2",
                )
            }

        val failedResult2 = post2.body!!
        assertThat(
            failedResult2
                .at("/errors")
                .toString(),
        ).contains("Access is denied. The current and saved operation hash do not match")
    }

    @Test
    fun operationWithoutNameTest() {
        // Creating a mutation without a name
        val query =
            """
            mutation (${'$'}ppCode:String) {
                packet {
                    p: createProduct(input: {
                        code: ${'$'}ppCode
                    }) {
                        id
                    }

                    g1: getProduct(id: "ref:p") {
                        id
                        code
                    }
                }
            }
            """.trimIndent()

        val uuid = uuid()

        val failedPost1 =
            post(
                "/models/$MODEL_ID_FROM_DB/graphql",
                JsonNodeFactory.instance
                    .objectNode()
                    .put("query", query)
                    .putRawValue("variables", RawValue("{ \"ppCode\": \"$uuid\"}")),
            ) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_2",
                )
            }

        val failedResult1 = failedPost1.body!!

        assertThat(
            failedResult1.at("/errors/0/message").asText(),
        ).contains("Security Error. Anonymous transactions are prohibited")
    }

    @Test
    fun nonRegisteredOperationTest() {
        val query =
            """
                query doesNotMatter {
                    searchProduct {
                        elems {
                            id
                        }
                    }
                }
            """
        val resp =
            post(
                "/models/$MODEL_ID_FROM_DB/graphql",
                objectMapper
                    .createObjectNode()
                    .put("query", query),
            )
        assertThat(resp.body).isNotNull
        assertThat(resp.body!!.at("/errors/0/message").asText()).contains("missing from the allowed list")
    }

    @Test
    fun checkSelectsTest() {
        val secondTargetValue = "two"

        // Create object for checkSelects
        executeQuery(
            """
            mutation {
                packet {
                    createProduct(input: {code: "two"}) { id }
                }
            }
            """.trimIndent(),
        )

        val query =
            """
            mutation severalCheckSelects(${'$'}ppCode: String) {
                packet {
                    p: createProduct(input: {
                        code: ${'$'}ppCode
                    }) {
                        id
                    }

                    g1: getProduct(id:"ref:p") {
                        id
                        code
                    }
                }
            }
            """.trimIndent()

        val checkSelect1 =
            objectMapper
                .createObjectNode()
                .put("typeName", "Product")
                .put("conditionValue", "it.code\$in\${[]:jwt:arrayTest}")

        val checkSelect2 =
            objectMapper
                .createObjectNode()
                .put("typeName", "Product")
                .put("conditionValue", "\${jwt:obj.inner}=='innerValue'")

        val arrayNode =
            objectMapper
                .createArrayNode()
                .add(checkSelect1)
                .add(checkSelect2)

        val mutationNode =
            objectMapper
                .createObjectNode()
                .put("name", "severalCheckSelects")
                .put("body", query)
                .set<JsonNode>("checkSelects", arrayNode)

        val res = post("/models/$MODEL_ID_FROM_DB/security/permissions/operations", mutationNode)
        assertThat(res.statusCode).isEqualTo(HttpStatus.OK)

        val objectNode =
            JsonNodeFactory.instance
                .objectNode()
                .put("query", query)
                .putRawValue("variables", RawValue("{ \"ppCode\": \"$secondTargetValue\"}"))

        val post =
            post("/models/$MODEL_ID_FROM_DB/graphql", objectNode) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_1",
                )
            }

        val result = post.body!!
        assertThat(result.at("/errors").isMissingNode).isTrue

        val failedPost =
            post(
                "/models/$MODEL_ID_FROM_DB/graphql",
                JsonNodeFactory.instance
                    .objectNode()
                    .put("query", query)
                    .putRawValue("variables", RawValue("{ \"ppCode\": \"$secondTargetValue\" }")),
            ) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_2",
                )
            }
        val failedResult = failedPost.body!!
        assertThat(failedResult.at("/errors").toString().contains(SecureDataFetcher.FAILED_CHECK_SELECT)).isTrue
    }

    // TODO: a stripped-down version of the test, not enough:
    //  - merge
    //  - literal main cond
    @Test
    fun pathConditionsQueryTest() {
        // We isolate ourselves from others
        val prefix = "pathConditions_" + getRandomString()

        val cond = "it.code ${'$'}like '$prefix%'" // the cond variable is always executed, we are not interested in it.
        val query = """
            query someSearchQueryWithPathConditions(${'$'}cond: String!) {
              s: searchProduct(cond: ${'$'}cond) {
                elems {
                  id
                  services {
                    elems {
                        id
                    }
                  }
                  externalProduct {
                    entity {
                      services {
                        elems {
                          id
                        }
                      }
                    }
                  }
                }
              }
            }"""

        val subFromJWT = "9876543210"
        val code1 = prefix + "1"
        val code2Base = prefix + "2"
        val code21 = "${code2Base}_$subFromJWT"
        val code22 = "${code2Base}_notSub"

        val productCond = "it.code == '$code1'"
        val serviceCond = "it.code \$like '$code2Base%' && it.code \$like '%' + \${req:Authorization[jwt].sub}"

        // PathConditions
        val pathConditions =
            objectMapper
                .createArrayNode()
                .add(objectMapper.createObjectNode().put("path", "s").put("cond", productCond))
                .add(objectMapper.createObjectNode().put("path", "s.elems.services").put("cond", serviceCond))
                .add(objectMapper.createObjectNode().put("path", "s.elems.externalProduct.entity.services").put("cond", serviceCond))

        // A mutation for a safety rule
        val mutationNode =
            objectMapper
                .createObjectNode()
                .put("name", "someSearchQueryWithPathConditions")
                .put("body", query)
                .put("allowEmptyChecks", true)
                .set<JsonNode>("pathConditions", pathConditions)

        // Creating security rules
        val resp = post("/models/$MODEL_ID_FROM_DB/security/permissions/operations", mutationNode)
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        // Now let's create entities for verification
        executeQuery(
            """
            mutation {
                packet {
                    badProduct: createProduct(input: {code: "$code2Base"}) {
                        id
                    }
                }
            }
            """.trimIndent(),
        )
        val data =
            executeQuery(
                """
                mutation {
                    packet {
                        # The product satisfies the path conditions
                        goodProduct: createProduct(input: {code: "$code1"}) {
                            id
                        }
                        # Let's also set up a link to ourselves to check the operation of the pathConditions on the reference field.
                        updateProduct(input: {id: "ref:goodProduct", externalProduct: {entityId: "ref:goodProduct"}}) {
                            id
                        }
                        goodService: createService(input: {product: "ref:goodProduct", code: "$code21"}) {
                            id
                        }
                        badService: createService(input: {product: "ref:goodProduct", code: "$code22"}) {
                            id
                        }
                        veryBadService: createService(input: {product: "ref:goodProduct", code: "123"}) {
                            id
                        }
                    }
                }
                """.trimIndent(),
            )
        println(data)
        val goodProductParty = data.at("/data/packet/goodProduct/id").asText()
        val goodService = data.at("/data/packet/goodService/id").asText()
        // Total 1 good Product, which has 1 good Service

        // Now let's perform the search
        val queryNode =
            JsonNodeFactory.instance
                .objectNode()
                .put("query", query)
                .putRawValue("variables", RawValue("{ \"cond\": \"$cond\" }"))
        val post =
            post("/models/$MODEL_ID_FROM_DB/graphql", queryNode) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_2",
                )
            }
        assertThat(post.statusCode).isEqualTo(HttpStatus.OK)

        val result = post.body!!
        assertThat(result.at("/errors").isMissingNode).isTrue
        // Checking the search part (1 Good Product, which has 1 Good Service)
        assertThat(result.at("/data/s/elems").size()).isEqualTo(1)
        assertThat(result.at("/data/s/elems/0/id").asText()).isEqualTo(goodProductParty)
        assertThat(result.at("/data/s/elems/0/services/elems").size()).isEqualTo(1)
        assertThat(result.at("/data/s/elems/0/services/elems/0/id").asText()).isEqualTo(goodService)
        assertThat(result.at("/data/s/elems/0/externalProduct/entity/services/elems").size()).isEqualTo(1)
        assertThat(result.at("/data/s/elems/0/externalProduct/entity/services/elems/0/id").asText()).isEqualTo(goodService)
    }

    /**
     * Check that pathConditions are applicable to Mutation as well
     * TODO: does not work yet, need to pass securityContext somewhere in the Packet
     */
    @Test
    @Disabled
    fun packetAdditionalConditionsTest() {
        val preparationResult =
            executeQuery(
                """
                mutation {
                  packet {
                    createProduct(input: {code: "external"}) {
                        id
                    }
                    good: createService(input: {product: "ref:createProduct", code: "goodService"}) {
                        id
                    }
                    bad: createService(input: {product: "ref:createProduct", code: "badService"}) {
                        id
                    }
                  }
                }
                """.trimIndent(),
                modelId = MODEL_ID_FROM_DB,
            )
        assertThat(preparationResult.at("/errors").isMissingNode).isTrue
        val externalProductId = preparationResult.at("/data/packet/createProduct/id").asText()
        val goodServiceId = preparationResult.at("/data/packet/good/id").asText()
        val badServiceId = preparationResult.at("/data/packet/bad/id").asText()

        val query = """
            mutation createProduct {
              packet {
                createProduct(input: {externalServices: {add: [{rootEntityId: "$externalProductId", entityId: "$goodServiceId"}, {rootEntityId: "$externalProductId", entityId: "$badServiceId"}]}}) {
                  externalServices {
                    elems {
                      reference {
                        entityId
                      }
                    }
                  }
                }
              }
            }"""

        val pathConditions =
            objectMapper
                .createArrayNode()
                .add(
                    objectMapper
                        .createObjectNode()
                        .put(
                            "path",
                            "packet.createProduct.externalServices",
                        ).put("cond", "it.reference.entity.code == 'goodService'"),
                )

        // Permission
        val mutationNode =
            objectMapper
                .createObjectNode()
                .put("name", "createProduct")
                .put("body", query)
                .put("allowEmptyChecks", true)

        mutationNode.set<ArrayNode>("pathConditions", pathConditions)

        val response = post("/models/$MODEL_ID_FROM_DB/security/permissions/operations", mutationNode)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        // Execute Packet
        val post =
            post(
                "/models/$MODEL_ID_FROM_DB/graphql",
                JsonNodeFactory.instance
                    .objectNode()
                    .put("query", query),
            ) { httpHeaders: HttpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_1",
                )
            }
        val result = post.body!!

        assertThat(result.at("/errors").isMissingNode).isTrue
        assertThat(result.at("/data/packet/createProduct/externalServices/elems")).hasSize(1)
        assertThat(result.at("/data/packet/createProduct/externalServices/elems/0/entityId").asText()).isEqualTo(goodServiceId)
    }

    @Test
    fun fileJwksCheckTest() {
        val query =
            """
            query preparedQuery {
                searchTestEntity {
                    elems {
                        id
                    }
                }
            }
            """.trimIndent()

        val objectNode =
            JsonNodeFactory.instance
                .objectNode()
                .put("query", query)

        // No JWT
        val postFailed = post("/models/$MODEL_ID_FROM_FILE/graphql", objectNode)
        val resultFailed = postFailed.body!!
        assertThat(resultFailed.at("/errors/0/message").asText()).contains("Not authorized")

        // With Bad JWT (Expired)
        val fail1 =
            assertThrows<WebClientResponseException> {
                post("/models/$MODEL_ID_FROM_FILE/graphql", objectNode) { httpHeaders ->
                    httpHeaders.add(
                        "Authorization",
                        "Bearer $OUTDATED_JWT_FOR_JWKS_FROM_FILE",
                    )
                }
            }
        assertThat(fail1.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        // TODO: validate error message, its absent
        // assertThat(fail1.responseBodyAsString).contains("JWT is expired")

        // With Bad JWT (Invalid)
        val fail2 =
            assertThrows<WebClientResponseException> {
                post("/models/$MODEL_ID_FROM_FILE/graphql", objectNode) { httpHeaders ->
                    httpHeaders.add(
                        "Authorization",
                        "Bearer 123",
                    )
                }
            }
        assertThat(fail2.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        // TODO: validate error message, its absent
        // assertThat(fail2.responseBodyAsString).contains("Could not read JWT")

        // With JWT
        val post =
            post("/models/$MODEL_ID_FROM_FILE/graphql", objectNode) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_FOR_JWKS_FROM_FILE",
                )
            }
        val result = post.body!!
        assertThat(result.at("/errors/0/message").isMissingNode).isTrue
    }

    @Test
    fun filePermissionsCheckTest() {
        val badQuery =
            """
            query notPreparedBecauseItsDifferent {
                searchTestEntity {
                    elems {
                        id
                        pString
                    }
                }
            }
            """.trimIndent()

        val respBad =
            post(
                "/models/$MODEL_ID_FROM_FILE/graphql",
                objectMapper
                    .createObjectNode()
                    .put("query", badQuery),
            )
        assertThat(respBad.body).isNotNull
        assertThat(respBad.body!!.at("/errors/0/message").asText()).contains("not found in the list of permitted operations")

        val query =
            """
            query preparedQuery {
                searchTestEntity {
                    elems {
                        id
                    }
                }
            }
            """.trimIndent()

        val resp =
            post(
                "/models/$MODEL_ID_FROM_FILE/graphql",
                objectMapper
                    .createObjectNode()
                    .put("query", query),
            ) { httpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_FOR_JWKS_FROM_FILE",
                )
            }
        assertThat(resp.body).isNotNull
        assertThat(resp.body!!.at("/errors").isMissingNode).isTrue
    }

    // Checking security on introspection checkSelect
    // Cases:
    // 1. Checking on __schema
    // 2. Checking on __type
    // 3. Checking on __schema and __type
    // 4. Checking on __schema and __type with additional fields
    // 5. Checking on __schema in a fragment
    // 6. Checking on __schema in an inline fragment
    @ParameterizedTest
    @ValueSource(
        strings = [
            "query { __schema { __typename } }",
            "query { __type(name: \"__Schema\") { __typename } }",
            "query { __schema { __typename } __type(name: \"__Schema\") { __typename } }",
            """
            query {
              __schema {
                __typename
                queryType { name }
                mutationType { name }
              }
              __type(name: "__Schema") {
                __typename
                fields {
                  name
                }
              }
            }
        """,
            """
            query {
              ...introFragment
            }
            fragment introFragment on ${GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME} {
              __schema {
                __typename
              }
            }
        """,
            """
            query {
              ...on ${GraphQLSchemaHelper.QUERY_OBJECT_TYPE_NAME} {
                __schema {
                  __typename
                }
              }
            }
        """,
        ],
    )
    fun introspectionSchemaCheckSelectTest(query: String) {
        val request: ObjectNode =
            objectMapper
                .createObjectNode()
                .put("query", query)

        // Model requires checkSelect for introspection, bad JWT
        val failResult =
            post("/models/$MODEL_ID_FROM_DB/graphql", request) { httpHeaders: HttpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_1", // It expects JWT_CREATED_2
                )
            }
        assertThat(failResult.body!!.at("/errors/0/message").asText()).contains(SecureDataFetcher.FAILED_CHECK_SELECT)

        // Model requires checkSelect for introspection, good JWT
        val successResult1 =
            post("/models/$MODEL_ID_FROM_DB/graphql", request) { httpHeaders: HttpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_CREATED_2",
                )
            }
        assertThat(successResult1.body!!.at("/errors").isMissingNode).isTrue()

        // Model does not require checkSelect for introspection
        val successResult2 =
            post("/models/$MODEL_ID_FROM_FILE/graphql", request) { httpHeaders: HttpHeaders ->
                httpHeaders.add(
                    "Authorization",
                    "Bearer $JWT_FOR_JWKS_FROM_FILE",
                )
            }
        assertThat(successResult2.body!!.at("/errors").isMissingNode).isTrue()
    }
}
