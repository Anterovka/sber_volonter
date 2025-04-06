package ru.sbertech.dataspace.security.admin

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.WebClientResponseException
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.dsl.expr
import ru.sbertech.dataspace.helpers.IntegrationTestHelper
import ru.sbertech.dataspace.security.model.dto.CheckSelect
import ru.sbertech.dataspace.security.model.dto.Operation
import ru.sbertech.dataspace.security.model.dto.PathCondition
import ru.sbertech.dataspace.security.model.interfaces.SysCheckSelect
import java.util.Collections

@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class GraphQlAdminRestServiceTest : IntegrationTestHelper() {
    companion object {
        private const val MODEL_ID = "4"

        // One template for checking the creation/updating, etc. of security data for operations (to make it easier and validation is not cursed)
        private const val BODY: String =
            """query name${"$"}{name} (${"$"}test${"$"}{number}: String, ${"$"}param1: String, ${"$"}param2: String, ${"$"}param3: String, ${"$"}param4: String) {
                    p1: searchProduct(cond: ${"$"}param1) {
                        elems { id }
                    }
                    p2: searchProduct(cond: ${"$"}param2) {
                        elems { id }
                    }
                    p3: searchProduct(cond: ${"$"}param3) {
                        elems { id }
                    }
                    p4: searchProduct(cond: ${"$"}param4) {
                        elems { id }
                    }
                    p_fake: searchProduct(cond: ${"$"}test${"$"}{number}) {
                        elems { id }
                    }
                }"""

        // Request for validation of pathCondition
        private const val VALIDATION_BODY: String =
            """query someSearchQueryWithPathConditions(${"$"}cond: String, ${"$"}v1: Int = 1, ${"$"}duplicate: String = "1==1") {
                    search: searchProduct(cond: ${"$"}cond, limit: ${"$"}v1) {
                        elems {
                            id
                            services(cond: "1==1") {
                               elems {
                                   id
                                   d: operations(cond: ${"$"}duplicate) {
                                      elems {
                                          id
                                      }
                                   }
                               }
                            }
                        }
                    }
                }"""

        private const val VALIDATION_BAD_BODY: String =
            """query badBody {
                    merge {
                       elems {
                          ... on Product @mergeReqSpec(cond: "1==1") @mergeReqSpec(cond: "2==2") {
                              id
                          }
                       }
                   }
                   veryBad(bad: "Bad") { id }
               }"""
    }

    @Test
    fun disabledSecurityApiTest() {
        val error =
            assertThrows<WebClientResponseException> {
                get("/models/1/security/permissions/operations")
            }
        assertThat(error.responseBodyAsString)
            .contains("probably admin service is disabled")
    }

    @Test
    fun commonTest() {
        // Creating an operation without checkSelect and paramAddition
        val operationName = "common_test_${getRandomString()}"
        val operationBody = BODY.replace("\${name}", operationName).replace("\${number}", "0")
        val operationNode =
            objectMapper.valueToTree<JsonNode>(
                Operation().apply {
                    name = operationName
                    body = operationBody
                    checkSelects =
                        setOf(
                            CheckSelect().apply {
                                typeName = "SysRootSecurity"
                                conditionValue = "\${id}=='1'"
                                description = "desc1"
                                orderValue = 1
                            },
                        )
                },
            )
        val resp = post("/models/$MODEL_ID/security/permissions/operations", operationNode)
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        // We check the created operation by proofreading
        val respGet =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", operationName),
            )
        assertThat(respGet.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(respGet.body).isNotNull()
        assertThat(respGet.body!!.size()).isEqualTo(1)
        assertThat(respGet.body!![0]["name"].asText()).isEqualTo(operationName)
        assertThat(respGet.body!![0]["body"].asText()).isEqualTo(operationBody)

        // Deleting the created operation
        val respDelete = delete("/models/$MODEL_ID/security/permissions/operations/$operationName")
        assertThat(respDelete.statusCode).isEqualTo(HttpStatus.OK)

        // Check that the operation is no longer in progress.
        val respGetAfterDelete =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                mapOf("name" to operationName),
            )
        assertThat(respGetAfterDelete.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(respGetAfterDelete.body).isNotNull()
        assertThat(respGetAfterDelete.body!!.size()).isZero()

        // Creating an operation with CheckSelect and PathCondition
        val checkSelect1 =
            CheckSelect().apply {
                typeName = "SysRootSecurity"
                conditionValue = "\${id}=='1'"
                description = "desc1"
                orderValue = 1
            }

        val checkSelect2 =
            CheckSelect().apply {
                typeName = "SysRootSecurity"
                conditionValue = "\${id}=='2'"
                description = "desc2"
                orderValue = 2
            }

        val pathCondition1 =
            PathCondition().apply {
                path = "p1"
                cond = "1==1"
            }

        val operationWithChecksAndAdditions =
            objectMapper.valueToTree<JsonNode>(
                Operation().apply {
                    name = operationName
                    body = operationBody
                    disableJwtVerification = true
                    allowEmptyChecks = true
                    checkSelects = setOf(checkSelect1, checkSelect2)
                    pathConditions = mapOf("p1" to pathCondition1)
                },
            )
        val respPost =
            post(
                "/models/$MODEL_ID/security/permissions/operations",
                operationWithChecksAndAdditions,
            )
        assertThat(respPost.statusCode).isEqualTo(HttpStatus.OK)

        // Checking whether the operation was created correctly
        val respGetAfterPost =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                mapOf("name" to operationName),
            )
        assertThat(respGetAfterPost.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(respGetAfterPost.body).isNotNull()
        assertThat(respGetAfterPost.body!!.size()).isEqualTo(1)
        assertThat(respGetAfterPost.body!![0]["name"].asText()).isEqualTo(operationName)
        assertThat(respGetAfterPost.body!![0]["body"].asText()).isEqualTo(operationBody)
        assertThat(respGetAfterPost.body!![0]["disableJwtVerification"].asBoolean()).isTrue()
        assertThat(respGetAfterPost.body!![0]["allowEmptyChecks"].asBoolean()).isTrue()
        // Checking the created ones checkSelect
        var operationCheckSelects = respGetAfterPost.body!![0]["checkSelects"]
        assertThat(operationCheckSelects).isNotNull()
        assertThat(operationCheckSelects.size()).isEqualTo(2)
        assertThat(operationCheckSelects[0]["typeName"].asText()).isEqualTo("SysRootSecurity")
        assertThat(operationCheckSelects[1]["typeName"].asText()).isEqualTo("SysRootSecurity")
        assertThat(operationCheckSelects[0]["conditionValue"].asText()).isEqualTo("\${id}=='1'")
        assertThat(operationCheckSelects[1]["conditionValue"].asText()).isEqualTo("\${id}=='2'")
        assertThat(operationCheckSelects[0]["description"].asText()).isEqualTo("desc1")
        assertThat(operationCheckSelects[1]["description"].asText()).isEqualTo("desc2")
        // Check PathConditions
        var operationPathConditions = respGetAfterPost.body!![0]["pathConditions"]
        assertThat(operationPathConditions.size()).isEqualTo(1)
        assertThat(operationPathConditions[0]["path"].asText()).isEqualTo("p1")
        assertThat(operationPathConditions[0]["cond"].asText()).isEqualTo("1==1")

        // Updating the operation
        val checkSelect3 =
            CheckSelect().apply {
                typeName = "SysCheckSelect"
                conditionValue = "\${id}=='3'"
                description = "desc3"
                orderValue = 3
            }

        val pathCondition3 =
            PathCondition().apply {
                path = "p1"
                cond = "3==3"
            }

        val operationBodyUpdated = BODY.replace("\${name}", operationName).replace("\${number}", "1")
        val respPut =
            put(
                "/models/$MODEL_ID/security/permissions/operations/$operationName",
                objectMapper.valueToTree(
                    Operation().apply {
                        name = operationName
                        body = operationBodyUpdated
                        disableJwtVerification = false
                        allowEmptyChecks = false
                        checkSelects = setOf(checkSelect3)
                        pathConditions = mapOf("p1" to pathCondition3)
                    },
                ),
            )
        assertThat(respPut.statusCode).isEqualTo(HttpStatus.OK)

        // Checking the result of the operation update
        val respGetAfterPut =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", operationName),
            )
        assertThat(respGetAfterPut.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(respGetAfterPut.body).isNotNull()
        assertThat(respGetAfterPut.body!!.size()).isEqualTo(1)
        assertThat(respGetAfterPut.body!![0]["name"].asText()).isEqualTo(operationName)
        assertThat(respGetAfterPut.body!![0]["body"].asText()).isEqualTo(operationBodyUpdated)
        assertThat(respGetAfterPut.body!![0]["disableJwtVerification"].asBoolean()).isFalse()
        assertThat(respGetAfterPut.body!![0]["allowEmptyChecks"].asBoolean()).isFalse()
        // Check CheckSelect
        operationCheckSelects = respGetAfterPut.body!![0]["checkSelects"]
        assertThat(operationCheckSelects).isNotNull()
        assertThat(operationCheckSelects.size()).isOne()
        assertThat(operationCheckSelects[0]["typeName"].asText()).isEqualTo("SysCheckSelect")
        assertThat(operationCheckSelects[0]["conditionValue"].asText()).isEqualTo("\${id}=='3'")
        assertThat(operationCheckSelects[0]["description"].asText()).isEqualTo("desc3")
        // Check PathConditions
        operationPathConditions = respGetAfterPut.body!![0]["pathConditions"]
        assertThat(operationPathConditions[0]["path"].asText()).isEqualTo("p1")
        assertThat(operationPathConditions[0]["cond"].asText()).isEqualTo("3==3")

        // We delete the created operation and check that the CheckSelect is not left in the database.
        val respDeleteAfterPut = delete("/models/$MODEL_ID/security/permissions/operations/$operationName")
        assertThat(respDeleteAfterPut.statusCode).isEqualTo(HttpStatus.OK)

        assertSelect(
            "4",
            Selector.EntityCollectionBased(
                SysCheckSelect.NAME,
                cond = expr { cur["objectId"].eq(value(operationCheckSelects[0]["objectId"].asText())) },
            ),
            { node -> assertThat(node).isEmpty() },
        )

        // We are trying to delete an already deleted operation
        val error =
            assertThrows<WebClientResponseException> {
                delete(
                    "/models/$MODEL_ID/security/permissions/operations/$operationName",
                    JsonNode::class.java,
                )
            }
        assertThat(error.responseBodyAsString)
            .contains("The Entity with type: SysOperation and identifier")
            .contains("doesn't exist")
    }

    /**
     * Checking
     * * Errors when checking checkSelects:
     * - bad cond condition
     * Errors when checking the pathConditions:
     * - bad cond condition
     * - unknown path
     * - the type has no cond argument along the way
     * Errors with invalid request body
     */
    @Test
    fun validationCommonTest() {
        var operationNode: ObjectNode =
            objectMapper.valueToTree(
                Operation().apply {
                    name = "someSearchQueryWithPathConditions"
                    body = VALIDATION_BODY
                    checkSelects =
                        setOf(
                            CheckSelect().apply {
                                typeName = "SysOperation"
                                conditionValue = "it.department='someString'"
                            },
                        )
                    pathConditions =
                        mapOf(
                            "search" to
                                PathCondition().apply {
                                    path = "search"
                                    cond = "1=1"
                                },
                            // misspell
                            "nonExisting" to
                                PathCondition().apply {
                                    path = "nonExisting"
                                    cond = "1==1"
                                },
                            // there is no such thing
                            "search.elems.id" to
                                PathCondition().apply {
                                    path = "search.elems.id"
                                    cond = "1==1"
                                },
                            // there is no cond here
                        )
                },
            )

        var error =
            assertThrows<WebClientResponseException> {
                // Creating an operation with the whole thing
                post(
                    "/models/$MODEL_ID/security/permissions/operations",
                    operationNode,
                )
            }
        assertThat(error).isNotNull
        assertThat(error.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        // Checking for errors
        assertThat(error.responseBodyAsString)
            // checkSelect
            .contains("Error when checking checkSelect: 'it.department='someString''")
            // pathConditions
            .contains("Error checking the pathCondition: 'search' -> '1=1'")
            .contains("The operation contains pathConditions for invalid paths: '[nonExisting]'")
            .contains(
                "Error checking the pathCondition: 'search.elems.id' -> '1==1': the main condition is missing in the specified field",
            )

        // Validity of the request body
        operationNode =
            objectMapper.valueToTree(
                Operation().apply {
                    name = "badBody"
                    body = VALIDATION_BAD_BODY
                },
            )
        error =
            assertThrows<WebClientResponseException> {
                // Creating an operation with the whole thing
                post(
                    "/models/$MODEL_ID/security/permissions/operations",
                    operationNode,
                )
            }
        assertThat(error).isNotNull
        assertThat(error.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        // Checking for errors
        assertThat(error.responseBodyAsString)
            .contains("Non repeatable directives must be uniquely named within a location.")
            .contains("Validation error")
            .contains("mergeReqSpec")
    }

    @Test
    fun bulkCommonTest() {
        val sid = getRandomString()
        val createdNames: MutableSet<String> = HashSet()
        var arrayNodeToCreate: ArrayNode = objectMapper.createArrayNode()
        val typeName = "SysRootSecurity"

        // Checking the bulk-create API
        // Forming the objects to be created
        for (i in 0..1) {
            val operationName = "name$sid$i"
            val body =
                BODY
                    .replace("\${number}", "${i % 2}")
                    .replace("\${name}", operationName)
            createdNames.add(operationName)

            arrayNodeToCreate.add(
                objectMapper.valueToTree<JsonNode>(
                    Operation().apply {
                        this.name = operationName
                        this.body = body
                        this.allowEmptyChecks = i % 2 == 0
                        this.disableJwtVerification = i % 2 == 0
                        this.checkSelects =
                            setOf(
                                CheckSelect().apply {
                                    this.typeName = typeName
                                    conditionValue = "1==1"
                                    description = "desc1"
                                    orderValue = 1
                                },
                                CheckSelect().apply {
                                    this.typeName = typeName
                                    conditionValue = "2==2"
                                    description = "desc2"
                                    orderValue = 2
                                },
                            )
                        this.pathConditions =
                            mapOf(
                                "p1" to
                                    PathCondition().apply {
                                        path = "p1"
                                        cond = "1==1"
                                    },
                                "p2" to
                                    PathCondition().apply {
                                        path = "p2"
                                        cond = "2==2"
                                    },
                            )
                    },
                ),
            )
        }

        // Creating the generated objects
        var resp =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/create",
                arrayNodeToCreate,
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        // We subtract the objects and check the correctness of the filling
        resp =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$sid%"),
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
        var searchBody = resp.body
        assertThat(searchBody).isNotNull()
        assertThat(searchBody!!.size()).isEqualTo(2)
        var operations: List<Operation> = objectMapper.convertValue(searchBody, object : TypeReference<List<Operation>>() {})
        assertThat<Any>(operations.stream().map(Operation::name)).allMatch { name ->
            createdNames.remove(name)
        }
        assertThat(createdNames).isEmpty()
        for (j in 0..1) {
            val operation = operations[j]
            assertThat(operation.name).isNotNull()
            val i = operation.name!![operation.name!!.length - 1].digitToInt()
            assertThat(operation.body).isEqualTo(
                BODY
                    .replace("\${number}", "${i % 2}")
                    .replace("\${name}", operation.name!!),
            )
            assertThat(operation.disableJwtVerification).isEqualTo(i % 2 == 0)
            assertThat(operation.allowEmptyChecks).isEqualTo(i % 2 == 0)
            assertThat(operation.checkSelects).isNotNull()
            assertThat(operation.pathConditions).isNotNull()
            val checkSelects: List<CheckSelect> = operation.checkSelects!!.toList()
            val pathConditions: List<PathCondition> = operation.pathConditions!!.values.toList()
            val conditionValues: MutableList<String> = ArrayList(mutableListOf("1==1", "2==2"))
            val descriptions: MutableList<String> = ArrayList(mutableListOf("desc1", "desc2"))
            val paths: MutableList<String> = ArrayList(mutableListOf("p1", "p2"))
            val conds: MutableList<String> = ArrayList(mutableListOf("1==1", "2==2"))
            for (k in 0..1) {
                val checkSelect = checkSelects[k]
                assertThat(checkSelect.typeName).isEqualTo(typeName)
                assertThat(checkSelect.conditionValue).matches(conditionValues::remove)
                assertThat(checkSelect.description).matches(descriptions::remove)

                val pathCondition = pathConditions[k]
                assertThat(pathCondition.path).matches(paths::remove)
                assertThat(pathCondition.cond).matches(conds::remove)
            }
            assertThat(conditionValues).isEmpty()
            assertThat(descriptions).isEmpty()
        }

        // Checking bulk-merge API
        arrayNodeToCreate = objectMapper.createArrayNode()
        for (i in 0..1) {
            val operationName = "name$sid$i"
            val body =
                BODY
                    .replace("\${number}", "2")
                    .replace("\${name}", operationName)
            createdNames.add(operationName)

            arrayNodeToCreate.add(
                objectMapper.valueToTree<JsonNode>(
                    Operation().apply {
                        this.name = operationName
                        this.body = body
                        this.allowEmptyChecks = i % 2 != 0 // flipped
                        this.disableJwtVerification = i % 2 != 0 // flipped
                        this.checkSelects =
                            setOf(
                                CheckSelect().apply {
                                    this.typeName = typeName
                                    conditionValue = "3==3"
                                    description = "desc3"
                                    orderValue = 3
                                },
                                CheckSelect().apply {
                                    this.typeName = typeName
                                    conditionValue = "4==4"
                                    description = "desc4"
                                    orderValue = 4
                                },
                            )
                        this.pathConditions =
                            mapOf(
                                "p1" to
                                    PathCondition().apply {
                                        path = "p1"
                                        cond = "3==3"
                                    },
                                "p2" to
                                    PathCondition().apply {
                                        path = "p2"
                                        cond = "4==4"
                                    },
                            )
                    },
                ),
            )
        }

        // Calling merge operation only for the first operation
        resp =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/merge",
                arrayNodeToCreate,
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        // We check that only the body of operations has been updated, and other fields and elements have not changed.
        resp =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$sid%"),
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
        searchBody = resp.body
        assertThat(searchBody).isNotNull()
        assertThat(searchBody!!.size()).isEqualTo(2)
        operations = objectMapper.convertValue(searchBody, object : TypeReference<List<Operation>>() {})
        assertThat(operations.stream().map<Any>(Operation::name)).allMatch { name ->
            createdNames.remove(name)
        }
        assertThat(createdNames).isEmpty()

        for (j in 0..1) {
            val operation = operations[j]
            assertThat(operation.name).isNotNull()
            val i = operation.name!![operation.name!!.length - 1].digitToInt()
            assertThat(operation.body).isEqualTo(
                BODY
                    .replace("\${number}", "2")
                    .replace("\${name}", operation.name!!),
            )

            assertThat(operation.disableJwtVerification).isEqualTo(i % 2 == 0)
            assertThat(operation.allowEmptyChecks).isEqualTo(i % 2 == 0)

            val checkSelects: List<CheckSelect> = operation.checkSelects!!.toList()
            val pathConditions: List<PathCondition> = operation.pathConditions!!.values.toList()
            val conditionValues: MutableList<String> = ArrayList(mutableListOf("1==1", "2==2"))
            val descriptions: MutableList<String> = ArrayList(mutableListOf("desc1", "desc2"))
            val paths: MutableList<String> = ArrayList(mutableListOf("p1", "p2"))
            val conds: MutableList<String> = ArrayList(mutableListOf("1==1", "2==2"))
            for (k in 0..1) {
                val checkSelect = checkSelects[k]
                assertThat(checkSelect.typeName).isEqualTo(typeName)
                assertThat(checkSelect.conditionValue).matches(conditionValues::remove)
                assertThat(checkSelect.description).matches(descriptions::remove)

                val pathCondition = pathConditions[k]
                assertThat(pathCondition.path).matches(paths::remove)
                assertThat(pathCondition.cond).matches(conds::remove)
            }
            assertThat(conditionValues).isEmpty()
            assertThat(descriptions).isEmpty()
        }

        // Let's perform a replacement on a single operation. At the same time, both should remain, but the information on one should be replaced.
        var subArrayNodeToCreate = objectMapper.createArrayNode()
        subArrayNodeToCreate.add(arrayNodeToCreate[0])
        val operationsMap: MutableMap<String, JsonNode> = HashMap()
        operationsMap[arrayNodeToCreate[0]["name"].asText()] = arrayNodeToCreate[0]
        operationsMap[arrayNodeToCreate[1]["name"].asText()] = arrayNodeToCreate[1]
        resp =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/replace",
                subArrayNodeToCreate,
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        // We check that only the first operation has been fully updated.
        resp =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$sid%"),
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
        searchBody = resp.body
        assertThat(searchBody).isNotNull()
        assertThat(searchBody!!.size()).isEqualTo(2)
        operations = objectMapper.convertValue(searchBody, object : TypeReference<List<Operation>>() {})

        // Restoring the names in the array for verification
        createdNames.add(arrayNodeToCreate[0]["name"].asText())
        createdNames.add(arrayNodeToCreate[1]["name"].asText())
        assertThat(operations.stream().map(Operation::name)).allMatch { name ->
            createdNames.remove(name)
        }
        assertThat(createdNames).isEmpty()
        for (j in 0..1) {
            val operation = operations[j]
            assertThat(operation.name).isNotNull()
            assertThat(operation.body).isEqualTo(
                BODY
                    .replace("\${number}", "2")
                    .replace("\${name}", operation.name!!),
            )

            assertThat(operation.disableJwtVerification).isEqualTo(false)
            assertThat(operation.allowEmptyChecks).isEqualTo(false)
            val checkSelects: List<CheckSelect> = operation.checkSelects!!.toList()
            val pathConditions: List<PathCondition> = operation.pathConditions!!.values.toList()

            // Since we did not replace the second operation, its values are old.
            val conditionValues: MutableList<String> =
                ArrayList(if (j == 0) mutableListOf("3==3", "4==4") else mutableListOf("1==1", "2==2"))
            val descriptions: MutableList<String> =
                ArrayList(if (j == 0) mutableListOf("desc3", "desc4") else mutableListOf("desc1", "desc2"))
            val paths: MutableList<String> =
                ArrayList(mutableListOf("p1", "p2"))
            val conds: MutableList<String> =
                ArrayList(if (j == 0) mutableListOf("3==3", "4==4") else mutableListOf("1==1", "2==2"))
            for (k in 0..1) {
                val checkSelect = checkSelects[k]
                assertThat(checkSelect.typeName).isEqualTo(typeName)
                assertThat(checkSelect.conditionValue).matches(conditionValues::remove)
                assertThat(checkSelect.description).matches(descriptions::remove)

                val pathCondition = pathConditions[k]
                assertThat(pathCondition.path).matches(paths::remove)
                assertThat(pathCondition.cond).matches(conds::remove)
            }
            assertThat(conditionValues).isEmpty()
            assertThat(descriptions).isEmpty()
        }

        // Let's perform a replaceAll on the second operation. In this case, only the second operation should remain, and the first one should be erased.
        subArrayNodeToCreate = objectMapper.createArrayNode()
        val baseOperationNode = arrayNodeToCreate[1]
        subArrayNodeToCreate.add(baseOperationNode)
        resp =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/replaceAll",
                subArrayNodeToCreate,
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)

        // We check that only the first operation has been fully updated.
        resp =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$sid%"),
            )
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
        searchBody = resp.body
        assertThat(searchBody).isNotNull()
        assertThat(searchBody!!.size()).isEqualTo(1)
        operations = objectMapper.convertValue(searchBody, object : TypeReference<List<Operation>>() {})
        val operation = operations[0]
        assertThat(operation.name).isEqualTo(baseOperationNode["name"].asText())
        assertThat(operation.body).isEqualTo(
            BODY
                .replace("\${number}", "2")
                .replace("\${name}", operation.name!!),
        )
        assertThat(operation.disableJwtVerification).isTrue()
        assertThat(operation.allowEmptyChecks).isTrue()
        val checkSelects: List<CheckSelect> = operation.checkSelects!!.toList()
        val pathConditions: List<PathCondition> = operation.pathConditions!!.values.toList()
        val conditionValues: MutableList<String> = ArrayList(mutableListOf("3==3", "4==4"))
        val descriptions: MutableList<String> = ArrayList(mutableListOf("desc3", "desc4"))
        val paths: MutableList<String> = ArrayList(mutableListOf("p1", "p2"))
        val conds: MutableList<String> = ArrayList(mutableListOf("3==3", "4==4"))
        for (k in 0..1) {
            val checkSelect = checkSelects[k]
            assertThat(checkSelect.typeName).isEqualTo(typeName)
            assertThat(checkSelect.conditionValue).matches(conditionValues::remove)
            assertThat(checkSelect.description).matches(descriptions::remove)

            val pathCondition = pathConditions[k]
            assertThat(pathCondition.path).matches(paths::remove)
            assertThat(pathCondition.cond).matches(conds::remove)
        }
        assertThat(conditionValues).isEmpty()
        assertThat(descriptions).isEmpty()

        // let's check that the simple values of the request header can be accessed in the check Select condition Value.
        val checkSelect =
            objectMapper
                .createObjectNode()
                .put(
                    "conditionValue",
                    "entities{type = SysRootSecurity, cond = " +
                        "1==1 || it.type==\${req:x-dspc-tenant}}.\$exists\n",
                ).put("orderValue", 1)

        arrayNodeToCreate =
            objectMapper.createArrayNode().add(
                objectMapper
                    .createObjectNode()
                    .put("name", "name" + sid + "_" + 3)
                    .put(
                        "body",
                        BODY
                            .replace(
                                "\${number}",
                                "0",
                            ).replace("\${name}", sid + "_" + 3),
                    ).set<JsonNode>("checkSelects", objectMapper.createArrayNode().add(checkSelect)),
            )

        resp =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/create",
                arrayNodeToCreate,
            )
        assertThat(resp.body).isNull()
        assertThat(resp.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun operationsBulkMerge() {
        val name1 = getRandomString()
        val name2 = getRandomString()
        val name3 = getRandomString()

        val operationsToCreate = mutableListOf<Operation>()

        operationsToCreate.add(
            Operation().apply {
                this.name = "name$name1"
                this.body =
                    BODY
                        .replace("\${number}", "1")
                        .replace("\${name}", name1)
                this.checkSelects =
                    setOf(
                        CheckSelect().apply {
                            this.conditionValue = "1==1"
                            this.orderValue = 1
                        },
                    )
            },
        )

        operationsToCreate.add(
            Operation().apply {
                this.name = "name$name2"
                this.body =
                    BODY
                        .replace("\${number}", "1")
                        .replace("\${name}", name2)
                this.checkSelects =
                    setOf(
                        CheckSelect().apply {
                            this.conditionValue = "21==21"
                            this.orderValue = 1
                        },
                        CheckSelect().apply {
                            this.conditionValue = "22==22"
                            this.orderValue = 2
                        },
                    )
            },
        )

        operationsToCreate.add(
            Operation().apply {
                this.name = "name$name3"
                this.body =
                    BODY
                        .replace("\${number}", "1")
                        .replace("\${name}", name3)
            },
        )

        val createResponseEntity =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/create",
                objectMapper.valueToTree(operationsToCreate),
            )
        assertThat(createResponseEntity.body).isNull()
        assertThat(createResponseEntity.statusCode).isEqualTo(HttpStatus.OK)

        val operationsToUpdate = mutableListOf<Operation>()

        operationsToUpdate.add(
            Operation().apply {
                this.name = "name$name1"
                this.body =
                    BODY
                        .replace("\${number}", "0")
                        .replace("\${name}", name1)
            },
        )

        operationsToUpdate.add(
            Operation().apply {
                this.name = "name$name3"
                this.body =
                    BODY
                        .replace("\${number}", "0")
                        .replace("\${name}", name3)
            },
        )

        val rewriteEntities =
            post(
                "/models/$MODEL_ID/security/permissions/operations-bulk/merge",
                objectMapper.valueToTree(operationsToUpdate),
            )
        assertThat(rewriteEntities.body).isNull()
        assertThat(rewriteEntities.statusCode).isEqualTo(HttpStatus.OK)

        // The first mutation should change - its body should be BODY0
        val searchResponseEntity1 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$name1%"),
            )
        val searchBody1 = searchResponseEntity1.body
        assertThat(searchBody1).isNotNull()
        assertThat(searchBody1!!.size()).isEqualTo(1)
        val mutation1 = objectMapper.convertValue(searchBody1, object : TypeReference<List<Operation>>() {})[0]
        assertThat(mutation1.checkSelects).hasSize(1)
        assertThat(mutation1.body).isEqualTo(
            BODY
                .replace("\${number}", "0")
                .replace("\${name}", name1),
        )

        // The second one should remain the same as when it was created - with BODY1.
        val searchResponseEntity2 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$name2%"),
            )
        val searchBody2 = searchResponseEntity2.body
        assertThat(searchBody2).isNotNull()
        assertThat(searchBody2!!.size()).isEqualTo(1)
        val mutation2 = objectMapper.convertValue(searchBody2, object : TypeReference<List<Operation>>() {})[0]
        assertThat(mutation2.checkSelects).hasSize(2)
        assertThat(mutation2.body).isEqualTo(
            BODY
                .replace("\${number}", "1")
                .replace("\${name}", name2),
        )

        // The third mutation should change - its body should be BODY0
        val searchResponseEntity3 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$name3%"),
            )
        val searchBody3 = searchResponseEntity3.body
        assertThat(searchBody3).isNotNull()
        assertThat(searchBody3!!.size()).isEqualTo(1)
        val mutation3 = objectMapper.convertValue(searchBody3, object : TypeReference<List<Operation>>() {})[0]
        assertThat(mutation3.checkSelects == null || mutation3.checkSelects!!.isEmpty()).isTrue()
        assertThat(mutation3.body).isEqualTo(
            BODY
                .replace("\${number}", "0")
                .replace("\${name}", name3),
        )
    }

    @Test
    fun operationsMerge() {
        val name = getRandomString()
        val oldConditionValue = "\${Long:jwt:count}==10000"

        val operation1: Operation =
            Operation().apply {
                this.name = "name$name"
                this.body =
                    BODY
                        .replace("\${number}", "0")
                        .replace("\${name}", name)
                this.checkSelects =
                    setOf(
                        CheckSelect().apply {
                            this.typeName = "SysRootSecurity"
                            this.conditionValue = oldConditionValue
                            this.orderValue = 1
                        },
                    )
            }

        val createResponseEntity =
            post(
                "/models/$MODEL_ID/security/permissions/operations",
                objectMapper.valueToTree(operation1),
            )
        assertThat(createResponseEntity.body).isNull()
        assertThat(createResponseEntity.statusCode).isEqualTo(HttpStatus.OK)

        val operation2 =
            Operation().apply {
                this.name = "name$name"
                this.body =
                    BODY
                        .replace("\${number}", "1")
                        .replace("\${name}", name)
                this.checkSelects =
                    setOf(
                        CheckSelect().apply {
                            this.typeName = "SysRootSecurity"
                            this.conditionValue = "\${Long:jwt:count}==20000"
                            this.orderValue = 1
                        },
                        CheckSelect().apply {
                            this.typeName = "SysRootSecurity"
                            this.conditionValue = "\${Long:jwt:count}==50000"
                            this.orderValue = 2
                        },
                    )
            }

        val rewriteEntities =
            post(
                "models/$MODEL_ID/security/permissions/operations-bulk/merge",
                objectMapper.valueToTree(listOf(operation2)),
            )
        assertThat(rewriteEntities.body).isNull()
        assertThat(rewriteEntities.statusCode).isEqualTo(HttpStatus.OK)

        // The mutation must change - its body must be BODY1
        val searchResponseEntity1 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                Collections.singletonMap("name", "%$name%"),
            )
        val searchBody1 = searchResponseEntity1.body
        assertThat(searchBody1).isNotNull()
        assertThat(searchBody1!!.size()).isEqualTo(1)
        val mutation1 = objectMapper.convertValue(searchBody1, object : TypeReference<List<Operation>>() {})[0]
        assertThat(mutation1.checkSelects).hasSize(1)
        assertThat(mutation1.checkSelects!!.toList()[0].conditionValue).isEqualTo(oldConditionValue)
        assertThat(mutation1.body).isEqualTo(
            BODY
                .replace("\${number}", "1")
                .replace("\${name}", name),
        )
    }

    @Test
    fun simpleGetWithPage() {
        val name = getRandomString()
        val createResponseEntity1 =
            post(
                "/models/$MODEL_ID/security/permissions/operations",
                objectMapper.valueToTree(
                    Operation().apply {
                        this.name = "name" + name + "1"
                        this.body =
                            BODY
                                .replace("\${number}", "0")
                                .replace("\${name}", name + "1")
                    },
                ),
            )

        assertThat(createResponseEntity1.body).isNull()
        assertThat(createResponseEntity1.statusCode).isEqualTo(HttpStatus.OK)

        val createResponseEntity2 =
            post(
                "/models/$MODEL_ID/security/permissions/operations",
                objectMapper.valueToTree(
                    Operation().apply {
                        this.name = "name" + name + "2"
                        this.body =
                            BODY
                                .replace("\${number}", "0")
                                .replace("\${name}", name + "2")
                    },
                ),
            )

        assertThat(createResponseEntity2.body).isNull()
        assertThat(createResponseEntity2.statusCode).isEqualTo(HttpStatus.OK)

        val map: MutableMap<String, Any?> = HashMap()
        map["name"] = "%$name%"
        map["page"] = 1
        map["pageSize"] = 1

        val searchResponseEntity1 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                map,
            )
        val searchBody1 = searchResponseEntity1.body
        assertThat(searchBody1).isNotNull()
        assertThat(searchBody1!!.size()).isEqualTo(1)

        assertThat(searchBody1[0]["name"].asText()).endsWith("1")

        map["page"] = 2

        val searchResponseEntity2 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                map,
            )
        val searchBody2 = searchResponseEntity2.body
        assertThat(searchBody2).isNotNull()
        assertThat(searchBody2!!.size()).isEqualTo(1)

        assertThat(searchBody2[0]["name"].asText()).endsWith("2")

        map["page"] = 1
        map["pageSize"] = 10

        val searchResponseEntity3 =
            get(
                "/models/$MODEL_ID/security/permissions/operations",
                map,
            )
        val searchBody3 = searchResponseEntity3.body
        assertThat(searchBody3).isNotNull()
        assertThat(searchBody3!!.size()).isEqualTo(2)
    }
}
