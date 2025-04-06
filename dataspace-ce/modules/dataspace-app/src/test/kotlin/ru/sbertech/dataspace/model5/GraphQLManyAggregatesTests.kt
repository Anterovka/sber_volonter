package ru.sbertech.dataspace.model5

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import ru.sbertech.dataspace.helpers.GraphQLTestHelper
import java.util.UUID

@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class GraphQLManyAggregatesTests : GraphQLTestHelper() {
    override fun getModelId() = "5"

    @Test
    fun createUpdateDeleteManyCommandsTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()

        val productCode1 = "productCode"
        val productCode2 = "productCode2"

        val productUpdatedCode1 = "productUpdatedCode"
        val productUpdatedCode2 = "productUpdatedCode2"

        val query = """
            mutation {
              packet {
                createManyProduct(
                  input: [
                  {
                    id: "$productId1"
                    code: "$productCode1"
                  },
                  {
                    id: "$productId2"
                    code: "$productCode2"
                  },
                  ]
                )
                getCreated1: getProduct(id: "ref:createManyProduct/0") {
                    code
                }
                getCreated2: getProduct(id: "ref:createManyProduct/1") {
                    code
                }
                updateManyProduct(
                  input: [
                  {
                    param: {
                        id: "ref:createManyProduct/0"
                        code: "$productUpdatedCode1"
                    }
                    compare: {
                        code: "$productCode1"
                    }
                  },
                  {
                    param: {
                        id: "ref:createManyProduct/1"
                        code: "$productUpdatedCode2"
                    }
                  },
                  ]
                )
                getUpdated1: getProduct(id: "ref:createManyProduct/0") {
                    code
                }
                getUpdated2: getProduct(id: "ref:createManyProduct/1") {
                    code
                }
                deleteManyProduct(
                  input: [
                  {
                    id: "ref:createManyProduct/0"
                  },
                  {
                    id: "ref:createManyProduct/1"
                    compare: {
                        code: "$productUpdatedCode2"
                    }
                  },
                  ]
                )
                getDeleted1: getProduct(id: "ref:createManyProduct/0" failOnEmpty: false) {
                    code
                }
                getDeleted2: getProduct(id: "ref:createManyProduct/1" failOnEmpty: false) {
                    code
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/createManyProduct/0" to productId1,
            "/data/packet/createManyProduct/1" to productId2,
            "/data/packet/getCreated1/code" to productCode1,
            "/data/packet/getCreated2/code" to productCode2,
            "/data/packet/updateManyProduct" to "success",
            "/data/packet/getUpdated1/code" to productUpdatedCode1,
            "/data/packet/getUpdated2/code" to productUpdatedCode2,
            "/data/packet/deleteManyProduct" to "success",
            "/data/packet/getDeleted1" to "null",
            "/data/packet/getDeleted2" to "null",
        ).check(result)
    }

    @Test
    fun updateOrCreateManyTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()

        val productCode1 = "productCode"
        val productCode2 = "productCode2"

        val productUpdatedCode1 = "productUpdatedCode"
        val productUpdatedCode2 = "productUpdatedCode2"

        val query = """
            mutation {
              packet {
                create: updateOrCreateManyProduct(
                  input: [
                  {
                    param: {
                        id: "$productId1"
                        code: "$productCode1"
                    }
                  },
                  {
                    param: {
                        id: "$productId2"
                        code: "$productCode2"
                    }
                  }
                  ]
                ){
                    id
                    created
                }
                getCreated1: getProduct(id: "ref:create/0/id") {
                    code
                }
                getCreated2: getProduct(id: "ref:create/1/id") {
                    code
                }
                update: updateOrCreateManyProduct(
                  input: [
                  {
                    param: {
                        id: "ref:create/0/id"
                    }
                    exist: {
                        update: {
                            code: "$productUpdatedCode1"
                        }
                        compare: {
                            code: "$productCode1"
                        }
                    }
                  },
                  {
                    param: {
                        id: "ref:create/1/id"
                    }
                    exist: {
                        update: {
                            code: "$productUpdatedCode2"
                        }
                        compare: {
                            code: "$productCode2"
                        }
                    }
                  }
                  ]
                ){
                    id
                    created
                }
                getUpdated1: getProduct(id: "ref:update/0/id") {
                    code
                }
                getUpdated2: getProduct(id: "ref:update/1/id") {
                    code
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/0/id" to productId1,
            "/data/packet/create/0/created" to "true",
            "/data/packet/create/1/id" to productId2,
            "/data/packet/create/1/created" to "true",
            "/data/packet/getCreated1/code" to productCode1,
            "/data/packet/getCreated2/code" to productCode2,
            "/data/packet/update/0/id" to productId1,
            "/data/packet/update/0/created" to "false",
            "/data/packet/update/1/id" to productId2,
            "/data/packet/update/1/created" to "false",
            "/data/packet/getUpdated1/code" to productUpdatedCode1,
            "/data/packet/getUpdated2/code" to productUpdatedCode2,
        ).check(result)
    }

    @Test
    fun manyCommandsIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()

        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()
        val productId3 = UUID.randomUUID().toString()

        val productCode1 = "productCode1"
        val productCode2 = "productCode2"
        val productCode3 = "productCode3"

        val productUpdatedCode1 = "productUpdatedCode"
        val productUpdatedCode2 = "productUpdatedCode2"

        val query = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
              isIdempotenceResponse
                updateOrCreateProduct(
                    input: {
                        id: "$productId1"
                        code: "$productCode1"
                    }
                ){
                  created
                  returning {
                    id
                  }
                }
                createManyProduct(
                  input: [
                  {
                    id: "$productId2"
                    code: "$productCode2"
                  },
                  {
                    id: "$productId3"
                    code: "$productCode3"
                  },
                  ]
                )
                getCreated1: getProduct(id: "ref:createManyProduct/0") {
                    code
                }
                getCreated2: getProduct(id: "ref:createManyProduct/1") {
                    code
                }
                updateManyProduct(
                  input: [
                  {
                    param: {
                        id: "ref:createManyProduct/0"
                        code: "$productUpdatedCode1"
                    }
                    compare: {
                        code: "$productCode2"
                    }
                  },
                  {
                    param: {
                        id: "ref:createManyProduct/1"
                        code: "$productUpdatedCode2"
                    }
                  },
                  ]
                )
                getUpdated1: getProduct(id: "ref:createManyProduct/0") {
                    code
                }
                getUpdated2: getProduct(id: "ref:createManyProduct/1") {
                    code
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/createManyProduct/0" to productId2,
            "/data/packet/createManyProduct/1" to productId3,
            "/data/packet/getCreated1/code" to productCode2,
            "/data/packet/getCreated2/code" to productCode3,
            "/data/packet/updateManyProduct" to "success",
            "/data/packet/getUpdated1/code" to productUpdatedCode1,
            "/data/packet/getUpdated2/code" to productUpdatedCode2,
        ).check(result)

        result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/createManyProduct/0" to productId2,
            "/data/packet/createManyProduct/1" to productId3,
            "/data/packet/getCreated1/code" to productUpdatedCode1,
            "/data/packet/getCreated2/code" to productUpdatedCode2,
            "/data/packet/updateManyProduct" to "success",
            "/data/packet/getUpdated1/code" to productUpdatedCode1,
            "/data/packet/getUpdated2/code" to productUpdatedCode2,
        ).check(result)
    }

    @Test
    fun updateOrCreateManyIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()

        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()

        val productCode1 = "productCode"
        val productCode2 = "productCode2"

        val productUpdatedCode1 = "productUpdatedCode"
        val productUpdatedCode2 = "productUpdatedCode2"

        val query = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
                isIdempotenceResponse
                create: updateOrCreateManyProduct(
                  input: [
                  {
                    param: {
                        id: "$productId1"
                        code: "$productCode1"
                    }
                  },
                  {
                    param: {
                        id: "$productId2"
                        code: "$productCode2"
                    }
                  }
                  ]
                ){
                    id
                    created
                }
                getCreated1: getProduct(id: "ref:create/0/id") {
                    code
                }
                getCreated2: getProduct(id: "ref:create/1/id") {
                    code
                }
                update: updateOrCreateManyProduct(
                  input: [
                  {
                    param: {
                        id: "ref:create/0/id"
                    }
                    exist: {
                        update: {
                            code: "$productUpdatedCode1"
                        }
                        compare: {
                            code: "$productCode1"
                        }
                    }
                  },
                  {
                    param: {
                        id: "ref:create/1/id"
                    }
                    exist: {
                        update: {
                            code: "$productUpdatedCode2"
                        }
                        compare: {
                            code: "$productCode2"
                        }
                    }
                  }
                  ]
                ){
                    id
                    created
                }
                getUpdated1: getProduct(id: "ref:update/0/id") {
                    code
                }
                getUpdated2: getProduct(id: "ref:update/1/id") {
                    code
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/create/0/id" to productId1,
            "/data/packet/create/0/created" to "true",
            "/data/packet/create/1/id" to productId2,
            "/data/packet/create/1/created" to "true",
            "/data/packet/getCreated1/code" to productCode1,
            "/data/packet/getCreated2/code" to productCode2,
            "/data/packet/update/0/id" to productId1,
            "/data/packet/update/0/created" to "false",
            "/data/packet/update/1/id" to productId2,
            "/data/packet/update/1/created" to "false",
            "/data/packet/getUpdated1/code" to productUpdatedCode1,
            "/data/packet/getUpdated2/code" to productUpdatedCode2,
        ).check(result)

        result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/create/0/id" to productId1,
            "/data/packet/create/0/created" to "true",
            "/data/packet/create/1/id" to productId2,
            "/data/packet/create/1/created" to "true",
            "/data/packet/getCreated1/code" to productUpdatedCode1,
            "/data/packet/getCreated2/code" to productUpdatedCode2,
            "/data/packet/update/0/id" to productId1,
            "/data/packet/update/0/created" to "false",
            "/data/packet/update/1/id" to productId2,
            "/data/packet/update/1/created" to "false",
            "/data/packet/getUpdated1/code" to productUpdatedCode1,
            "/data/packet/getUpdated2/code" to productUpdatedCode2,
        ).check(result)
    }

    @Test
    fun manyAggregatesInOnePacketTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                create1: createProduct(
                  input: {
                    id: "$productId1"
                  }
                ) {
                  id
                }
                create2: createProduct(
                  input: {
                    id: "$productId2"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/create1/id" to productId1,
            "/data/packet/create2/id" to productId2,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                getProduct(id: "$productId1") {
                    id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "1",
            "/data/packet/getProduct/id" to productId1,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                getProduct(id: "$productId2") {
                    id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "1",
            "/data/packet/getProduct/id" to productId2,
        ).check(result)
    }

    @Test
    fun manyAggregatesInOnePacketAggregateVersionErrorTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()

        val query = """
            mutation {
              packet {
                aggregateVersion
                create1: createProduct(
                  input: {
                    id: "$productId1"
                  }
                ) {
                  id
                }
                create2: createProduct(
                  input: {
                    id: "$productId2"
                  }
                ) {
                  id
                }
              }
            }
        """

        val result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Aggregate version usage is not allowed for packet with many aggregates",
        )
    }
}
