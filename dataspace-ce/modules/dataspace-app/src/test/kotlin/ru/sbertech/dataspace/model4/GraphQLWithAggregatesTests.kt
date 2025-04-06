package ru.sbertech.dataspace.model4

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import ru.sbertech.dataspace.helpers.GraphQLTestHelper
import java.math.BigDecimal
import java.math.MathContext
import java.util.UUID

@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class GraphQLWithAggregatesTests : GraphQLTestHelper() {
    override fun getModelId() = "4"

    @Test
    fun createSingleAggregateTest() {
        val id = UUID.randomUUID().toString()

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/id" to id,
        ).check(result)
    }

    @Test
    fun createTooManyAggregateTest() {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()

        val query = """
            mutation {
              packet {
                create1: createTestEntity(
                  input: {
                    id: "$id1"
                    pString: "stringValue"
                    owner: {
                      firstName: "firstNameValue1"
                    }
                  }
                ) {
                  id
                }
                create2: createTestEntity(
                  input: {
                    id: "$id2"
                    pString: "stringValue"
                    owner: {
                      firstName: "firstNameValue2"
                    }
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
            "Packet 'packet' execution error: Command 'create2' execution error: Too many aggregates",
        )
    }

    @Test
    fun getDifferentAggregatesTest() {
        val id1 = UUID.randomUUID().toString()
        val id2 = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id1"
                    pString: "stringValue"
                    owner: {
                      firstName: "firstNameValue1"
                    }
                  }
                ) {
                  id
                }
              }
            }
        """

        executeQuery(query)

        query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id2"
                    pString: "stringValue"
                    owner: {
                      firstName: "firstNameValue1"
                    }
                  }
                ) {
                  id
                }
              }
            }
        """

        executeQuery(query)

        query = """
            mutation {
              packet {
                get1: getTestEntity(id: "$id1") {
                    id
                }
                get2: getTestEntity(id: "$id2") {
                    id
                }
                get3: getTestEntity(id: "nonExistent" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/get1/id" to id1,
            "/data/packet/get2/id" to id2,
            "/data/packet/get3" to "null",
        ).check(result)
    }

    @Test
    fun simpleIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    pLong: null
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  owner {
                    firstName
                  }
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                idempotenceCreate: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    pLong: null
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        assertThat(secondCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isTrue()

        assertThat(secondCallResult.at("/data/packet/idempotenceCreate/id").textValue())
            .isEqualTo(firstCallResult.at("/data/packet/create/id").textValue())

        mapOf(
            "/data/packet/idempotenceCreate/pString" to stringValue,
            "/data/packet/idempotenceCreate/pLong" to "null",
        ).check(secondCallResult)
    }

    @Test
    fun hashNotMatchIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/create/id").isMissingNode).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                idempotenceCreate: createTestEntity(
                  input: {
                    pString: "$stringValue"
                  }
                ) {
                  id
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        val errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'idempotenceCreate' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun createWithGetIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                }
                get: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
                idempotenceGet: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                    pString
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        assertThat(secondCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isTrue()

        assertThat(secondCallResult.at("/data/packet/idempotenceGet/id").textValue())
            .isEqualTo(firstCallResult.at("/data/packet/get/id").textValue())

        mapOf(
            "/data/packet/idempotenceGet/pString" to stringValue,
        ).check(secondCallResult)
    }

    @Test
    fun commandsCountChangedErrorIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                }
                get: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
                idempotenceGet: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                    pString
                }
                idempotenceGet2: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                    pString
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        var errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Commands count(3) doesn't match with commands count(2) of the previous call",
        )

        val thirdCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
              }
            }
        """

        val thirdCallResult = executeQuery(thirdCall)

        errorMessage = thirdCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Commands count(1) doesn't match with commands count(2) of the previous call",
        )
    }

    @Test
    fun idempotenceNotWorkingWithOnlyGetPacketTest() {
        val idempotenceKey = UUID.randomUUID().toString()

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                get: getTestEntity(id: "nonExistent" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                idempotenceGet: getTestEntity(id: "nonExistent") {
                    id
                    pString
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'idempotenceGet' execution error: Object not found",
        )
    }

    @Test
    fun changeCommandParamsOrderIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                    pLong: null
                  }
                ) {
                  id
                  owner {
                    firstName
                  }
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                idempotenceCreate: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    pLong: null
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        assertThat(secondCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isTrue()

        assertThat(secondCallResult.at("/data/packet/idempotenceCreate/id").textValue())
            .isEqualTo(firstCallResult.at("/data/packet/create/id").textValue())

        mapOf(
            "/data/packet/idempotenceCreate/pString" to stringValue,
            "/data/packet/idempotenceCreate/pLong" to "null",
        ).check(secondCallResult)
    }

    @Test
    fun changedCommandsOrderIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
                get1: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                }
                get2: getTestEntity(id: "ref:create") {
                    id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/errors").isMissingNode).isTrue()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pLong
                }
                get1: getTestEntity(id: "ref:create") {
                    id
                }
                get2: getTestEntity(id: "ref:create" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        val errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'get1' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun updateSingleAggregateTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        val stringValueUpd = "stringValueUpd"
        val firstNameValueUpd = "firstNameValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  owner {
                    firstName
                  }
                }
                update: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                    owner: {
                      firstName: "$firstNameValueUpd"
                    }
                  }
                ) {
                  id
                  pString
                  owner {
                    firstName
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/pString" to stringValue,
            "/data/packet/create/owner/firstName" to firstNameValue,
            "/data/packet/update/pString" to stringValueUpd,
            "/data/packet/update/owner/firstName" to firstNameValueUpd,
        ).check(result)
    }

    @Test
    fun updateWithCompareTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                update: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                  }
                  compare: {
                    pString: "$stringValue"
                  }
                ) {
                  id
                  pString
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/pString" to stringValue,
            "/data/packet/update/pString" to stringValueUpd,
        ).check(result)
    }

    @Test
    fun updateWithCompareFailTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                update: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                  }
                  compare: {
                    pString: "$stringValueUpd"
                  }
                ) {
                  id
                  pString
                }
              }
            }
        """

        val result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'update' execution error: Compare exception: actual property 'pString' value" +
                " [$stringValue] does not match with the expected value [$stringValueUpd]",
        )
    }

    @Test
    fun updateWithCompareIdempotenceTest() {
        val id = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                update: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                  }
                  compare: {
                    pString: "$stringValue"
                  }
                ) {
                  id
                  pString
                }
              }
            }
        """

        val firstCallResult = executeQuery(query)
        val secondCallResult = executeQuery(query)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()
        assertThat(secondCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isTrue()

        mapOf(
            "/data/packet/create/id" to id,
            "/data/packet/create/pString" to stringValue,
            "/data/packet/update/id" to id,
            "/data/packet/update/pString" to stringValueUpd,
        ).check(firstCallResult)

        mapOf(
            "/data/packet/create/id" to id,
            "/data/packet/create/pString" to stringValueUpd,
            "/data/packet/update/id" to id,
            "/data/packet/update/pString" to stringValueUpd,
        ).check(secondCallResult)
    }

    @Test
    fun updateWithCompareIdempotenceFailTest() {
        val id = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val firstCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                update: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                  }
                  compare: {
                    pString: "$stringValue"
                  }
                ) {
                  id
                  pString
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCall)

        assertThat(firstCallResult.at("/data/packet/isIdempotenceResponse").booleanValue()).isFalse()

        val secondCall = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                idempotenceUpdate: updateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                  }
                  compare: {
                    pString: "$stringValueUpd"
                  }
                ) {
                  id
                  pString
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCall)

        val errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'idempotenceUpdate' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun updateOrCreateTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet {
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      update: {
                            pString: "$stringValueUpd"
                      }
                      compare: {
                            pString: "$stringValue"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreate/created" to "true",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValue,
        ).check(result)

        val secondResult = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValueUpd,
        ).check(secondResult)
    }

    @Test
    fun updateOrCreateOnlyCompareTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"

        val query = """
            mutation {
              packet {
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      compare: {
                            pString: "$stringValue"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreate/created" to "true",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValue,
        ).check(result)

        val secondResult = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValue,
        ).check(secondResult)
    }

    @Test
    fun updateOrCreateCreateIdempotenceTest() {
        val id = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
              isIdempotenceResponse
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      update: {
                            pString: "$stringValueUpd"
                      }
                      compare: {
                            pString: "$stringValue"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/updateOrCreate/created" to "true",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValue,
        ).check(result)

        val secondResult = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/updateOrCreate/created" to "true",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValue,
        ).check(secondResult)
    }

    @Test
    fun updateOrCreateUpdateIdempotenceTest() {
        val id = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
                isIdempotenceResponse
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "ref:create/pString"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      update: {
                            pString: "$stringValueUpd"
                      }
                      compare: {
                            pString: "ref:create/pString"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValueUpd,
        ).check(result)

        val secondResult = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValueUpd,
        ).check(secondResult)
    }

    @Test
    fun updateOrCreateEmptyUpdateTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      update: {}
                      compare: {
                            pString: "ref:create/pString"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValue,
        ).check(result)
    }

    @Test
    fun updateOrCreateUpdateWithoutExistTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"
        val firstNameValueUpd = "firstNameValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "$stringValueUpd"
                    owner: {
                      firstName: "$firstNameValueUpd"
                    }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                    owner {
                        firstName
                    }
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pString" to stringValueUpd,
            "/data/packet/updateOrCreate/returning/owner/firstName" to firstNameValueUpd,
        ).check(result)
    }

    @Test
    fun updateOrCreateRefTest() {
        val id = UUID.randomUUID().toString()

        val stringValue = "stringValue"
        val stringValueUpd = "stringValueUpd"

        val query = """
            mutation {
              packet {
                create: createTestEntity(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                }
                updateOrCreate: updateOrCreateTestEntity(
                  input: {
                    id: "ref:create"
                    pString: "ref:create/pString"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      update: {
                            pString: "$stringValueUpd"
                      }
                      compare: {
                            pString: "ref:create/pString"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pStringAlias: pString
                  }
                }
                updateOrCreate2: updateOrCreateTestEntity(
                  input: {
                    id: "ref:updateOrCreate"
                    pString: "ref:updateOrCreate/returning/pStringAlias"
                    owner: {
                      firstName: "firstNameValue"
                    }
                  }
                  exist: {
                      update: {
                            pString: "ref:updateOrCreate/returning/pStringAlias"
                      }
                      compare: {
                            pString: "ref:updateOrCreate/returning/pStringAlias"
                      }
                  }
                ) {
                  created
                  returning {
                    id
                    pString
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/id" to id,
            "/data/packet/create/pString" to stringValue,
            "/data/packet/updateOrCreate/created" to "false",
            "/data/packet/updateOrCreate/returning/id" to id,
            "/data/packet/updateOrCreate/returning/pStringAlias" to stringValueUpd,
            "/data/packet/updateOrCreate2/created" to "false",
            "/data/packet/updateOrCreate2/returning/id" to id,
            "/data/packet/updateOrCreate2/returning/pString" to stringValueUpd,
        ).check(result)
    }

    @Test
    fun updateOrCreateByKeyTest() {
        val productName = "name" + UUID.randomUUID().toString()
        val city = "city" + UUID.randomUUID().toString()
        val street = "street" + UUID.randomUUID().toString()

        val query = """
            mutation {
              packet {
                updateOrCreateProductWithAddress(
                  input: {
                    name: "$productName"
                    address: {
                        city: "$city"
                        street: "$street"
                    }
                  }
                  exist: {
                    byKey: name_address__city_address__street
                  }
                ) {
                  created
                  returning {
                    id
                  }
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreateProductWithAddress/created" to "true",
        ).check(result)

        result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreateProductWithAddress/created" to "false",
        ).check(result)
    }

    @Test
    fun updateOrCreateByKeyIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val uniqueClientId = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
                isIdempotenceResponse
                updateOrCreateProductWithAddress(
                  input: {
                    uniqueClient: {
                        entityId: "$uniqueClientId"
                    }
                  }
                  exist: {
                    byKey: uniqueClient__entityId
                  }
                ) {
                  created
                  returning {
                    id
                  }
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/updateOrCreateProductWithAddress/created" to "true",
        ).check(result)

        result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/updateOrCreateProductWithAddress/created" to "true",
        ).check(result)

        query = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
                isIdempotenceResponse
                updateOrCreateProductWithAddress(
                  input: {
                    uniqueClient: {
                        entityId: "$uniqueClientId"
                    }
                  }
                  exist: {
                    byKey: name_address__city_address__street
                  }
                ) {
                  created
                  returning {
                    id
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'updateOrCreateProductWithAddress' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun createWithReferencesTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId = UUID.randomUUID().toString()
        val operationId = UUID.randomUUID().toString()
        val contractId = UUID.randomUUID().toString()

        val productCode = "productCode"
        val serviceCode = "serviceCode"
        val operationCode = "operationCode"
        val contractCode = "contractCode"

        val query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                  code
                  services {
                    elems {
                        code
                    }
                  }
                }
                createService(
                  input: {
                    id: "$serviceId"
                    code: "$serviceCode"
                    product: "ref:createProduct"
                  }
                ) {
                  id
                  code
                  product {
                    code
                  }
                  operations {
                    elems {
                      code
                    }
                  }
                }
                createOperation(
                  input: {
                    id: "$operationId"
                    code: "$operationCode"
                    service: "ref:createService"
                  }
                ) {
                  id
                  code
                  service {
                    code
                  }
                  contract {
                    code
                  }
                }
                createContract(
                  input: {
                    id: "$contractId"
                    code: "$contractCode"
                    operation: "ref:createOperation"
                  }
                ) {
                  id
                  code
                  operation {
                    code
                  }
                }
                getProduct(
                    id: "ref:createProduct"
                ) {
                  services {
                    elems {
                     code
                    }
                  }
                }
                getService(
                    id: "ref:createService"
                ) {
                  operations {
                    elems {
                     code
                    }
                  }
                }
                getOperation(
                    id: "ref:createOperation"
                ) {
                  contract {
                     code
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId,
            "/data/packet/createProduct/code" to productCode,
            "/data/packet/createProduct/services/elems" to "[]",
            "/data/packet/createService/id" to serviceId,
            "/data/packet/createService/code" to serviceCode,
            "/data/packet/createService/product/code" to productCode,
            "/data/packet/createService/operations/elems" to "[]",
            "/data/packet/createOperation/id" to operationId,
            "/data/packet/createOperation/code" to operationCode,
            "/data/packet/createOperation/service/code" to serviceCode,
            "/data/packet/createOperation/contract" to "null",
            "/data/packet/createContract/id" to contractId,
            "/data/packet/createContract/code" to contractCode,
            "/data/packet/createContract/operation/code" to operationCode,
            "/data/packet/getProduct/services/elems/0/code" to serviceCode,
            "/data/packet/getService/operations/elems/0/code" to operationCode,
            "/data/packet/getOperation/contract/code" to contractCode,
        ).check(result)
    }

    @Test
    fun deleteTest() {
        val productId = UUID.randomUUID().toString()

        val productCode = "productCode"

        val query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                  code
                }
                deleteProduct(id: "ref:createProduct" compare: { code: "$productCode" })
                getProduct(id: "ref:createProduct" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId,
            "/data/packet/createProduct/code" to productCode,
            "/data/packet/deleteProduct" to "success",
            "/data/packet/getProduct" to "null",
        ).check(result)
    }

    @Test
    fun deleteWithCompareFailTest() {
        val productId = UUID.randomUUID().toString()

        val productCode = "productCode"
        val wrongCode = "wrongCode"

        val query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                  code
                }
                deleteProduct(id: "ref:createProduct" compare: { code: "$wrongCode" })
                getProduct(id: "ref:createProduct" failOnEmpty: false) {
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
            "Packet 'packet' execution error: Command 'deleteProduct' execution error: Compare exception: actual property 'code' value" +
                " [$productCode] does not match with the expected value [$wrongCode]",
        )
    }

    @Test
    fun deleteIdempotenceTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val productCode = "productCode"
        val serviceCode = "serviceCode"

        val createQuery = """
            mutation {
              packet {
                isIdempotenceResponse
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                  code
                }
                createService(
                  input: {id: "$serviceId" code: "$serviceCode" product: "ref:createProduct"}
                ) {
                  code
                }
              }
            }
        """

        executeQuery(createQuery)

        val deleteQuery = """
            mutation {
              packet(idempotencePacketId: "$idempotenceKey") {
                isIdempotenceResponse
                deleteService(id: "$serviceId" compare: { code: "$serviceCode" })
              }
            }
        """

        val firstCall = executeQuery(deleteQuery)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/deleteService" to "success",
        ).check(firstCall)

        val secondCall = executeQuery(deleteQuery)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/deleteService" to "success",
        ).check(secondCall)
    }

    @Test
    fun externalReferencesTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId = UUID.randomUUID().toString()
        val clientId = UUID.randomUUID().toString()

        val productCode = "productCode"
        val serviceCode = "serviceCode"

        var query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                }
                createService(
                  input: {
                    id: "$serviceId"
                    code: "$serviceCode"
                    product: "ref:createProduct"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId,
            "/data/packet/createService/id" to serviceId,
        ).check(result)

        query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    externalProduct: { entityId: "$productId" }
                    externalService: { entityId: "$serviceId" rootEntityId: "$productId" }
                    client: { entityId: "$clientId" }
                  }
                ) {
                  client {
                    entityId
                  }
                  externalProduct {
                    entityId
                    entity {
                        id
                        code
                        services {
                            elems {
                                id
                                code
                               }
                        }
                    }
                  }
                  externalService {
                    entityId
                    rootEntityId
                    entity {
                        id
                        code
                        product {
                            id
                            code
                        }
                    }
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/client/entityId" to clientId,
            "/data/packet/createProduct/externalProduct/entityId" to productId,
            "/data/packet/createProduct/externalProduct/entity/id" to productId,
            "/data/packet/createProduct/externalProduct/entity/code" to productCode,
            "/data/packet/createProduct/externalProduct/entity/services/elems/0/id" to serviceId,
            "/data/packet/createProduct/externalProduct/entity/services/elems/0/code" to serviceCode,
            "/data/packet/createProduct/externalService/entityId" to serviceId,
            "/data/packet/createProduct/externalService/rootEntityId" to productId,
            "/data/packet/createProduct/externalService/entity/id" to serviceId,
            "/data/packet/createProduct/externalService/entity/code" to serviceCode,
            "/data/packet/createProduct/externalService/entity/product/id" to productId,
            "/data/packet/createProduct/externalService/entity/product/code" to productCode,
        ).check(result)
    }

    @Test
    fun externalReferencesWithNonExistentRootEntityIdTest() {
        val productId = UUID.randomUUID().toString()
        val nonExistentId = "nonExistentId"
        val serviceId = UUID.randomUUID().toString()

        val productCode = "productCode"
        val serviceCode = "serviceCode"

        var query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                }
                createService(
                  input: {
                    id: "$serviceId"
                    code: "$serviceCode"
                    product: "ref:createProduct"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId,
            "/data/packet/createService/id" to serviceId,
        ).check(result)

        query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    externalService: { entityId: "$serviceId" rootEntityId: "$nonExistentId" }
                  }
                ) {
                  externalService {
                    entityId
                    rootEntityId
                    entity {
                        id
                        code
                        product {
                            id
                            code
                        }
                    }
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/externalService/entityId" to serviceId,
            "/data/packet/createProduct/externalService/rootEntityId" to nonExistentId,
            "/data/packet/createProduct/externalService/entity/id" to serviceId,
            "/data/packet/createProduct/externalService/entity/code" to serviceCode,
            "/data/packet/createProduct/externalService/entity/product/id" to productId,
            "/data/packet/createProduct/externalService/entity/product/code" to productCode,
        ).check(result)
    }

    @Test
    fun externalReferencesCollectionTest() {
        val productId = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()
        val serviceId1 = UUID.randomUUID().toString()
        val serviceId2 = UUID.randomUUID().toString()

        val productCode = "productCode"
        val serviceCode = "serviceCode"
        val serviceCode2 = "serviceCode2"

        var query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    code: "$productCode"
                  }
                ) {
                  id
                }
                service1: createService(
                  input: {
                    id: "$serviceId1"
                    code: "$serviceCode"
                    product: "ref:createProduct"
                  }
                ) {
                  id
                }
                service2: createService(
                  input: {
                    id: "$serviceId2"
                    code: "$serviceCode2"
                    product: "ref:createProduct"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId,
            "/data/packet/service1/id" to serviceId1,
            "/data/packet/service2/id" to serviceId2,
        ).check(result)

        query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId2"
                    externalServices: { add: [ { entityId: "$serviceId1" rootEntityId: "$productId" },
                                               { entityId: "$serviceId1" rootEntityId: "$productId" },
                                               { entityId: "$serviceId2" rootEntityId: "$productId" }
                                             ]
                                      }
                  }
                ) {
                   externalServices {
                       elems {
                            backReference {
                                id
                            }
                            aggregateRoot {
                                id
                            }
                            reference {
                                    entityId
                                    rootEntityId
                                    entity {
                                        id
                                        code
                                        product {
                                            code
                                        }
                                    }
                            }
                       }
                   }
                }
              }
            }
        """

        result = executeQuery(query)

        assertThat(result.at("/data/packet/createProduct/externalServices/elems").size()).isEqualTo(2)

        mapOf(
            "/data/packet/createProduct/externalServices/elems/0/backReference/id" to productId2,
            "/data/packet/createProduct/externalServices/elems/0/aggregateRoot/id" to productId2,
            "/data/packet/createProduct/externalServices/elems/0/reference/rootEntityId" to productId,
            "/data/packet/createProduct/externalServices/elems/0/reference/entity/product/code" to productCode,
            "/data/packet/createProduct/externalServices/elems/1/reference/rootEntityId" to productId,
        ).check(result)

        query = """
            mutation {
              packet {
                updateProduct(
                  input: {
                    id: "$productId2"
                    externalServices: { remove: [
                                                    { entityId: "$serviceId1" rootEntityId: "$productId" }
                                                    { entityId: "$serviceId1" rootEntityId: "$productId" }
                                                    { entityId: "nonExistentEntityId" rootEntityId: "nonExistentRootEntityId" }
                                                ]
                                      }
                  }
                ) {
                   externalServices {
                       elems {
                            reference {
                                    entityId
                                    entity {
                                        code
                                    }
                            }
                       }
                   }
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/updateProduct/externalServices/elems/0/reference/entityId" to serviceId2,
            "/data/packet/updateProduct/externalServices/elems/0/reference/entity/code" to serviceCode2,
        ).check(result)
    }

    @Test
    fun externalReferencesCollectionIdempotenceTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId1 = UUID.randomUUID().toString()
        val serviceId2 = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val query = """
            mutation {

              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                createProduct(
                  input: {
                    id: "$productId"
                    externalServices: {
                                        remove: [   { entityId: "$serviceId1" rootEntityId: "$productId" },
                                                    { entityId: "$serviceId2" rootEntityId: "$productId" }
                                                ]
                                        add: [ { entityId: "$serviceId1" rootEntityId: "$productId" },
                                               { entityId: "$serviceId2" rootEntityId: "$productId" }
                                             ]
                                      }
                  }
                ) {
                    id
                }
              }
            }
        """

        val firstCall = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/createProduct/id" to productId,
        ).check(firstCall)

        val secondCall = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/createProduct/id" to productId,
        ).check(secondCall)
    }

    @Test
    fun externalReferencesCollectionAddIdempotenceFailTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId1 = UUID.randomUUID().toString()
        val serviceId2 = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val firstCallQuery = """
            mutation {

              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                createProduct(
                  input: {
                    id: "$productId"
                    externalServices: {
                                        add: [ { entityId: "$serviceId1" rootEntityId: "$productId" },
                                               { entityId: "$serviceId2" rootEntityId: "$productId" }
                                             ]
                                      }
                  }
                ) {
                    id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCallQuery)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/createProduct/id" to productId,
        ).check(firstCallResult)

        val secondCallQuery = """
            mutation {

              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                idempotenceCreate: createProduct(
                  input: {
                    id: "$productId"
                    externalServices: {
                                        add: [ { entityId: "$serviceId1" rootEntityId: "$productId" },
                                               { entityId: "$serviceId2" rootEntityId: "$productId" }
                                               { entityId: "$serviceId2" rootEntityId: "$productId" }
                                             ]
                                      }
                  }
                ) {
                    id
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCallQuery)

        val errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'idempotenceCreate' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun externalReferencesCollectionRemoveIdempotenceFailTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId1 = UUID.randomUUID().toString()
        val serviceId2 = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val firstCallQuery = """
            mutation {

              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                createProduct(
                  input: {
                    id: "$productId"
                    externalServices: {
                                        remove: [   { entityId: "$serviceId1" rootEntityId: "$productId" },
                                                    { entityId: "$serviceId2" rootEntityId: "$productId" }
                                                ]
                                      }
                  }
                ) {
                    id
                }
              }
            }
        """

        val firstCallResult = executeQuery(firstCallQuery)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/createProduct/id" to productId,
        ).check(firstCallResult)

        val secondCallQuery = """
            mutation {

              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                idempotenceCreate: createProduct(
                  input: {
                    id: "$productId"
                    externalServices: {
                                        remove: [   { entityId: "$serviceId1" rootEntityId: "$productId" },
                                                    { entityId: "$serviceId2" rootEntityId: "$productId" }
                                                    { entityId: "$serviceId2" rootEntityId: "$productId" }
                                                ]
                                      }
                  }
                ) {
                    id
                }
              }
            }
        """

        val secondCallResult = executeQuery(secondCallQuery)

        val errorMessage = secondCallResult.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'idempotenceCreate' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun variablesTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId2 = UUID.randomUUID().toString()

        val productCode = "productCode"
        val serviceCode1 = "serviceCode1"
        val serviceCode2 = "serviceCode2"

        val query = """
            mutation(
            ${'$'}productInput: _CreateProductInput!,
            ${'$'}serviceInput1: _CreateServiceInput!,
            ${'$'}serviceInput2: _CreateServiceInput!,
            ${'$'}productId: ID!,
            ${'$'}serviceId: String,
            ) {
              packet {
                createProduct(
                  input: ${'$'}productInput
                ) {
                  id
                  code
                }
                service1: createService(
                  input: ${'$'}serviceInput1
                ) {
                  code
                }
                service2: createService(
                  input: ${'$'}serviceInput2
                ) {
                  code
                }
                getProduct(
                    id: ${'$'}productId
                ) {
                  services(cond:"it.id==${'$'}{serviceId}") @strExpr(string:${'$'}serviceId) {
                    elems {
                     code
                    }
                  }
                }
              }
            }
        """

        val result =
            executeQuery(
                query,
                mapOf(
                    "productInput" to
                        mapOf("id" to productId, "code" to productCode),
                    "serviceInput1" to mapOf("code" to serviceCode1, "product" to "ref:createProduct"),
                    "serviceInput2" to mapOf("id" to serviceId2, "code" to serviceCode2, "product" to productId),
                    "productId" to productId,
                    "serviceId" to serviceId2,
                ),
            )

        mapOf(
            "/data/packet/createProduct/id" to productId,
            "/data/packet/createProduct/code" to productCode,
            "/data/packet/service1/code" to serviceCode1,
            "/data/packet/service2/code" to serviceCode2,
            "/data/packet/getProduct/services/elems" to "[{\"code\":\"serviceCode2\"}]",
        ).check(result)
    }

    @Test
    fun recursiveHierarchyTest() {
        val documentName = "documentName"
        val documentPartParentDesc1 = "documentPartParentDesc1"
        val documentPartChildDesc1 = "documentPartChildDesc1"
        val documentPartChildDesc2 = "documentPartChildDesc2"
        val documentPartParentDesc2 = "documentPartParentDesc2"
        val documentPartChildDesc3 = "documentPartChildDesc3"
        val documentPartChildDesc4 = "documentPartChildDesc4"

        val query = """
            mutation {
              packet {
                createDocument(
                  input: {
                    name: "$documentName"
                  }
                ) {
                  id
                }
                documentPartParent1: createDocumentPart(
                  input: {
                    desc: "$documentPartParentDesc1"
                    document: "ref:createDocument"
                  }
                ) {
                  id
                }
                documentPartChild1: createDocumentPart(
                  input: {
                    desc: "$documentPartChildDesc1"
                    parent: "ref:documentPartParent1"
                  }
                ) {
                  id
                }
                documentPartChild2: createDocumentPart(
                  input: {
                    desc: "$documentPartChildDesc2"
                    parent: "ref:documentPartParent1"
                  }
                ) {
                  id
                }
                documentPartParent2: createDocumentPart(
                  input: {
                    desc: "$documentPartParentDesc2"
                    document: "ref:createDocument"
                  }
                ) {
                  id
                }
                documentPartChild3: createDocumentPart(
                  input: {
                    desc: "$documentPartChildDesc3"
                    parent: "ref:documentPartParent2"
                  }
                ) {
                  id
                }
                documentPartChild4: createDocumentPart(
                  input: {
                    desc: "$documentPartChildDesc4"
                    parent: "ref:documentPartParent2"
                  }
                ) {
                  id
                }
                getDocument(id: "ref:createDocument") {
                   documentParts1: documentParts(cond:"it.desc=='$documentPartParentDesc1'") {
                        elems {
                            parts {
                                elems {
                                    desc
                                }
                            }
                        }
                   }
                   documentParts2: documentParts(cond:"it.desc=='$documentPartParentDesc2'") {
                        elems {
                            parts {
                                elems {
                                    desc
                                }
                            }
                        }
                   }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/getDocument/documentParts1/elems/0/parts/elems/0/desc" to documentPartChildDesc1,
            "/data/packet/getDocument/documentParts1/elems/0/parts/elems/1/desc" to documentPartChildDesc2,
            "/data/packet/getDocument/documentParts2/elems/0/parts/elems/0/desc" to documentPartChildDesc3,
            "/data/packet/getDocument/documentParts2/elems/0/parts/elems/1/desc" to documentPartChildDesc4,
        ).check(result)
    }

    @Test
    fun recursiveHierarchyCreateWithDifferentParentsErrorTest() {
        val documentName = "documentName"
        val documentPartParentDesc1 = "documentPartParentDesc1"
        val documentPartChildDesc1 = "documentPartChildDesc1"
        val documentPartChildDesc2 = "documentPartChildDesc2"

        val documentId1 = UUID.randomUUID().toString()
        val documentId2 = UUID.randomUUID().toString()
        val documentPartId = UUID.randomUUID().toString()

        val createDocumentQuery = """
            mutation {
              packet {
                createDocument(
                  input: {
                    id: "$documentId1"
                    name: "$documentName"
                  }
                ) {
                  id
                }
              }
            }"""

        executeQuery(createDocumentQuery)

        val createDocumentWithPartsQuery = """
            mutation {
              packet {
                createDocument(
                  input: {
                    id: "$documentId2"
                    name: "$documentName"
                  }
                ) {
                  id
                }
                documentPartParent1: createDocumentPart(
                  input: {
                    id: "$documentPartId"
                    desc: "$documentPartParentDesc1"
                    document: "ref:createDocument"
                  }
                ) {
                  id
                }
                documentPartChild1: createDocumentPart(
                  input: {
                    desc: "$documentPartChildDesc1"
                    parent: "ref:documentPartParent1"
                  }
                ) {
                  id
                }
              }
            }
        """

        executeQuery(createDocumentWithPartsQuery)

        val createAnotherDocumentPartQuery = """
            mutation {
              packet {
                documentPartChild2: createDocumentPart(
                  input: {
                    desc: "$documentPartChildDesc2"
                    document: "$documentId1"
                    parent: "$documentPartId"
                  }
                ) {
                  id
                  document {
                    id
                    }
                  parent {
                    document {
                        id
                    }
                  }
                }
              }
            }
        """

        val result = executeQuery(createAnotherDocumentPartQuery)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'documentPartChild2' execution error:" +
                " The parent property 'document' value '$documentId1' is not match with the property 'parent.document' value '$documentId2'",
        )
    }

    @Test
    fun recursiveHierarchyUpdateWithDifferentParentsTest() {
        val documentId1 = UUID.randomUUID().toString()
        val documentPartId1 = UUID.randomUUID().toString()
        val documentPartId2 = UUID.randomUUID().toString()

        val documentPartLevel2Id = UUID.randomUUID().toString()

        val documentPartLevel2Parent2Id = UUID.randomUUID().toString()

        val createDocumentWithPartsQuery = """
            mutation {
              packet {
                createDocument(
                  input: {
                    id: "$documentId1"
                  }
                ) {
                  id
                }
                documentPartParent1: createDocumentPart(
                  input: {
                    id: "$documentPartId1"
                    document: "ref:createDocument"
                  }
                ) {
                  id
                }
                documentPartParent2: createDocumentPart(
                  input: {
                    id: "$documentPartId2"
                    document: "ref:createDocument"
                  }
                ) {
                  id
                }
                documentPartLevel2Parent1: createDocumentPartLevel2(
                  input: {
                    documentPart: "ref:documentPartParent1"
                  }
                ) {
                  id
                }
                documentPartLevel2Parent2: createDocumentPartLevel2(
                  input: {
                    id:"$documentPartLevel2Parent2Id"
                    documentPart: "ref:documentPartParent2"
                  }
                ) {
                  id
                }
                documentPartLevel2Child1: createDocumentPartLevel2(
                  input: {
                    id: "$documentPartLevel2Id"
                    documentPart: "ref:documentPartParent1"
                    parent: "ref:documentPartLevel2Parent1"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(createDocumentWithPartsQuery)

        assertThat(result.at("/errors").isMissingNode).isTrue()

        val updateDocumentPartLevel2ParentQuery = """
            mutation {
              packet {
               updateDocumentPartLevel2(
                  input: {
                    id: "$documentPartLevel2Id"
                    documentPart: "$documentPartId2"
                  }
                ) {
                  id
                }
              }
            }
        """

        result = executeQuery(updateDocumentPartLevel2ParentQuery)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'updateDocumentPartLevel2' execution error:" +
                " The parent property 'documentPart' value '$documentPartId2' is not match" +
                " with the property 'parent.documentPart' value '$documentPartId1'",
        )

        val updateDocumentPartLevel2ParentAndDocumentPartQuery = """
            mutation {
              packet {
               updateDocumentPartLevel2(
                  input: {
                    id: "$documentPartLevel2Id"
                    documentPart: "$documentPartId2"
                    parent: "$documentPartLevel2Parent2Id"
                  }
                ) {
                  id
                }
              }
            }
        """

        result = executeQuery(updateDocumentPartLevel2ParentAndDocumentPartQuery)

        assertThat(result.at("/errors").isMissingNode).isTrue()
    }

    @Test
    fun changeParentErrorTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()
        val serviceId = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId1"
                  }
                ) {
                  id
                }
                createService(
                  input: {
                    id: "$serviceId"
                    product: "ref:createProduct"
                  }
                ) {
                  id
                }
              }
            }
        """

        executeQuery(query)

        query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId2"
                  }
                ) {
                  id
                }
              }
            }
        """
        executeQuery(query)

        query = """
            mutation {
              packet {
               updateService(
                  input: {
                    id: "$serviceId"
                    product: "$productId2"
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
            "Packet 'packet' execution error: Command 'updateService' execution error: The aggregate from the parent property 'product'" +
                " ($productId2) not match with the current aggregate from the property 'aggregateRoot' ($productId1)",
        )
    }

    @Test
    fun incrementAllTypesTest() {
        val integerValue = 12345
        val newIntegerValue = 500
        val integerDelta = 54321
        val longValue = 12345678
        val longDelta = 87654321
        val floatValue = 1234.567F
        val floatDelta = 567.1234F
        val doubleValue = 1234567.012345
        val doubleDelta = 7654321.54321
        val bigDecimalValue = BigDecimal(12389.01238)
        val bigDecimalDelta = BigDecimal(541.87658)

        val query =
            """mutation {
                      packet {
                        createTestEntity(input: {
                          pInteger: $integerValue
                          pLong: $longValue
                          pFloat: $floatValue
                          pDouble: $doubleValue
                          pBigDecimal: $bigDecimalValue
                          owner: {
                            firstName:"firstNameValue"
                          }
                        }) {
                          id
                        }
                        updateTestEntity(
                            input: {
                                id: "ref:createTestEntity"
                                pInteger: $newIntegerValue
                            }
                            inc: {
                                pInteger: { value: $integerDelta negative: false fail: { value: 100000 operation: lt } }
                                pLong: { value: $longDelta negative: true }
                                pDouble: { value: $doubleDelta negative: null }
                                pFloat: { value: $floatDelta fail: { value: $floatValue, operation: gt }}
                                pBigDecimal: { value: $bigDecimalDelta }
                            }
                        ){
                              pInteger
                              pLong
                              pDouble
                              pFloat
                              pBigDecimal
                        }
                      }
                    }"""

        val result = executeQuery(query)

        mapOf(
            "/data/packet/updateTestEntity/pInteger" to (newIntegerValue + integerDelta).toString(),
            "/data/packet/updateTestEntity/pLong" to (longValue - longDelta).toString(),
            "/data/packet/updateTestEntity/pDouble" to (doubleValue + doubleDelta).toString(),
            "/data/packet/updateTestEntity/pFloat" to (floatValue + floatDelta).toString(),
            "/data/packet/updateTestEntity/pBigDecimal" to bigDecimalValue.add(bigDecimalDelta, MathContext(10)).toString(),
        ).check(result)
    }

    @Test
    fun incrementCheckErrorTest() {
        val integerDelta = 54321
        val checkValue = 5000

        val query =
            """mutation {
                      packet {
                        createTestEntity(input: {
                          pInteger: null
                          owner: {
                            firstName:"firstNameValue"
                          }
                        }) {
                          id
                        }
                        updateTestEntity(
                            input: {
                                id: "ref:createTestEntity"
                            }
                            inc: {
                                pInteger: { value: $integerDelta fail: { value: $checkValue operation: lt } }
                            }
                        ){
                             pInteger
                        }
                      }
                    }"""

        val result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'updateTestEntity' execution error:" +
                " Increment error: the new value '54321' for the field 'pInteger' does not match the condition 'LESS 5000'",
        )
    }

    @Test
    fun incrementWithIdempotenceTest() {
        val id = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()
        val integerValue = 12345
        val integerDelta = 54321
        val bigDecimalValue = BigDecimal(12389.01238)
        val bigDecimalDelta = BigDecimal(541.87658)

        var query =
            """mutation {
                      packet {
                        createTestEntity(input: {
                          id: "$id"
                          pInteger: $integerValue
                          pBigDecimal: $bigDecimalValue
                          owner: {
                            firstName: "firstNameValue"
                          }
                        }) {
                          id
                        }
                      }
                    }"""

        executeQuery(query)

        query =
            """mutation {
                      packet(idempotencePacketId: "$idempotenceKey") {
                        updateTestEntity(
                            input: {
                                id: "$id"
                            }
                            inc: {
                                pInteger: { value: $integerDelta negative: null fail: { value: 100000 operation: lt } }
                                pBigDecimal: { value: $bigDecimalDelta }
                            }
                        ){
                              id
                        }
                      }
                    }"""

        var result = executeQuery(query)

        assertThat(result.at("/data/packet/updateTestEntity/id").isMissingNode).isFalse()

        // fail: { value: 100000 operation: lt } -> fail: { value: 100001 operation: lt } - error
        query =
            """mutation {
                      packet(idempotencePacketId: "$idempotenceKey") {
                        updateTestEntity(
                            input: {
                                id: "$id"
                            }
                            inc: {
                                pInteger: { value: $integerDelta negative: null fail: { value: 100001 operation: lt } }
                                pBigDecimal: { value: $bigDecimalDelta }
                            }
                        ){
                              id
                        }
                      }
                    }"""

        result = executeQuery(query)

        var errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'updateTestEntity' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )

        // remove pBigDecimal: { value: $bigDecimalDelta }  - error
        query =
            """mutation {
                      packet(idempotencePacketId: "$idempotenceKey") {
                        updateTestEntity(
                            input: {
                                id: "$id"
                            }
                            inc: {
                                pInteger: { value: $integerDelta negative: null fail: { value: 100000 operation: lt } }
                            }
                        ){
                              id
                        }
                      }
                    }"""

        result = executeQuery(query)

        errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'updateTestEntity' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )

        // 'negative: null' -> 'negative: false' - success
        query =
            """mutation {
                      packet(idempotencePacketId: "$idempotenceKey") {
                        updateTestEntity(
                            input: {
                                id: "$id"
                            }
                            inc: {
                                pInteger: { value: $integerDelta negative: false fail: { value: 100000 operation: lt } }
                                pBigDecimal: { value: $bigDecimalDelta }
                            }
                        ){
                              id
                        }
                      }
                    }"""

        result = executeQuery(query)

        assertThat(result.at("/data/packet/updateTestEntity/id").isMissingNode).isFalse()
    }

    @Test
    fun updateOrCreateIncrementTest() {
        val integerValue = 12345
        val integerDelta = 54321

        val query =
            """mutation {
                      packet {
                        createTestEntity(input: {
                          pInteger: $integerValue
                          owner: {
                            firstName:"firstNameValue"
                          }
                        }) {
                          id
                        }
                        updateOrCreateTestEntity(
                            input: {
                                id: "ref:createTestEntity"
                            }
                            exist: {
                                inc: {
                                    pInteger: { value: $integerDelta negative: false fail: { value: 100000 operation: lt } }
                                }
                            }
                        ){
                          returning {
                            pInteger
                          }
                        }
                      }
                    }"""

        val result = executeQuery(query)

        mapOf(
            "/data/packet/updateOrCreateTestEntity/returning/pInteger" to (integerValue + integerDelta).toString(),
        ).check(result)
    }

    @Test
    fun updateOrCreateIncrementIdempotenceTest() {
        val idempotenceKey = UUID.randomUUID().toString()
        val integerValue = 12345
        val integerDelta = 54321

        var query =
            """mutation {
                      packet(idempotencePacketId: "$idempotenceKey") {
                        createTestEntity(input: {
                          pInteger: $integerValue
                          owner: {
                            firstName:"firstNameValue"
                          }
                        }) {
                          id
                        }
                        updateOrCreateTestEntity(
                            input: {
                                id: "ref:createTestEntity"
                            }
                            exist: {
                                inc: {
                                    pInteger: { value: $integerDelta negative: false fail: { value: 100000 operation: lt } }
                                }
                            }
                        ){
                          returning {
                            pInteger
                          }
                        }
                      }
                    }"""

        executeQuery(query)

        // remove fail: { value: 100000 operation: lt }
        query =
            """mutation {
                      packet(idempotencePacketId: "$idempotenceKey") {
                        createTestEntity(input: {
                          pInteger: $integerValue
                          owner: {
                            firstName:"firstNameValue"
                          }
                        }) {
                          id
                        }
                        updateOrCreateTestEntity(
                            input: {
                                id: "ref:createTestEntity"
                            }
                            exist: {
                                inc: {
                                    pInteger: { value: $integerDelta negative: false }
                                }
                            }
                        ){
                          returning {
                            pInteger
                          }
                        }
                      }
                    }"""

        val result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'updateOrCreateTestEntity' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }

    @Test
    fun dependsOnByGetIdempotenceTest() {
        val id = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val firstNameValue = "firstNameValue"

        var query = """mutation {
                          packet(idempotencePacketId: "$idempotenceKey") {
                            create: createTestEntity(input: {
                              id: "$id"
                              pString: "$stringValue",
                              owner: {
                                firstName:"$firstNameValue"
                              }
                            }) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                            get: getTestEntity(id: "ref:create") {
                              id
                              pString
                            }
                            update: updateTestEntity(input: {
                              id: "ref:create"
                              pString: "stringValue",
                              owner: {
                                firstName:"firstName"
                              }
                            }) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                          }
                        }"""

        executeQuery(query)

        // add @dependsOnByGet(commandId:"get", dependency: NOT_EXISTS) -> error

        query = """mutation {
                          packet(idempotencePacketId: "$idempotenceKey") {
                            create: createTestEntity(input: {
                              id: "$id"
                              pString: "$stringValue",
                              owner: {
                                firstName:"$firstNameValue"
                              }
                            }) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                            get: getTestEntity(id: "ref:create") {
                              id
                              pString
                            }
                            update: updateTestEntity(input: {
                              id: "ref:create"
                              pString: "stringValue",
                              owner: {
                                firstName:"firstName"
                              }
                            }) @dependsOnByGet(commandId:"get", dependency: NOT_EXISTS) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                          }
                        }"""

        var result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'update' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )

        // query without changes -> success

        query = """mutation {
                          packet(idempotencePacketId: "$idempotenceKey") {
                            isIdempotenceResponse
                            create: createTestEntity(input: {
                              id: "$id"
                              pString: "$stringValue",
                              owner: {
                                firstName:"$firstNameValue"
                              }
                            }) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                            get: getTestEntity(id: "ref:create") {
                              id
                              pString
                            }
                            update: updateTestEntity(input: {
                              id: "ref:create"
                              pString: "stringValue",
                              owner: {
                                firstName:"firstName"
                              }
                            }) {
                              id
                              pString
                              owner {
                                firstName
                              }
                            }
                          }
                        }"""

        result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
        ).check(result)
    }

    @Test
    fun aggregateVersionTest() {
        val productId = UUID.randomUUID().toString()
        val serviceId1 = UUID.randomUUID().toString()
        val serviceId2 = UUID.randomUUID().toString()
        val operationId = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId"
                  }
                ) {
                  id
                }
                createService(
                  input: {
                    id: "$serviceId1"
                    product: "$productId"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "1",
        ).check(result)

        query = """
            mutation {
              packet(aggregateVersion: 1) {
                aggregateVersion
                createService(
                  input: {
                    id: "$serviceId2"
                    product: "$productId"
                  }
                ) {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "2",
        ).check(result)

        query = """
            mutation {
              packet {
                getProduct(id: "find: it.id == '$productId'") {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/getProduct/id" to productId,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                getProduct(id: "find: it.id == '$productId'") {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/getProduct/id" to productId,
        ).check(result)

        query = """
            mutation {
              packet(aggregateVersion: 1) {
                getProduct(id: "find: it.id == '$productId'") {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        var errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Only request of the aggregate version is allowed for this packet",
        )

        query = """
            mutation {
              packet {
                aggregateVersion
                getProduct(id: "find: it.id == 'nonExistentId'") {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Aggregate root is not defined: aggregate version usage is not allowed",
        )

        query = """
            mutation {
              packet(aggregateVersion: 1) {
                aggregateVersion
                createOperation(
                  input: {
                    id: "$operationId"
                    service: "$serviceId2"
                  }
                ) {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Optimistic lock exception: the expected aggregateVersion value is 1 but the actual aggregateVersion value is 2",
        )

        query = """
            mutation {
              packet(aggregateVersion: 3) {
                aggregateVersion
                getProduct(id: "$productId") {
                    id
                }
              }
            }
        """

        result = executeQuery(query)

        errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Only request of the aggregate version is allowed for this packet",
        )

        query = """
            mutation {
              packet(aggregateVersion: 2) {
                aggregateVersion
                deleteProduct(id: "$productId")
                deleteService(id: "$serviceId1")
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "3",
        ).check(result)
    }

    @Test
    fun manyAggregatesButOnlyOneIsChangedAggregateVersionTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()
        val productId3 = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId1"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId1,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId2"
                  }
                ) {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId2,
        ).check(result)

        query = """
            mutation {
              packet(aggregateVersion:0) {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId3"
                  }
                ) {
                  id
                }
                get1: getProduct(id: "$productId1") {
                    id
                }
                get2: getProduct(id: "find: it.id == '$productId2'") {
                    id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "1",
            "/data/packet/createProduct/id" to productId3,
            "/data/packet/get1/id" to productId1,
            "/data/packet/get2/id" to productId2,
        ).check(result)
    }

    @Test
    fun getManyAggregatesAggregateVersionErrorTest() {
        val productId1 = UUID.randomUUID().toString()
        val productId2 = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId1"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId1,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId2"
                  }
                ) {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId2,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                get1: getProduct(id: "$productId1") {
                    id
                }
                get2: getProduct(id: "find: it.id == '$productId2'") {
                    id
                }
              }
            }
        """

        result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Aggregate version usage is not allowed for packet with many aggregates",
        )
    }

    @Test
    fun getManyAggregatesButOnlyOneFoundAggregateVersionTest() {
        val productId1 = UUID.randomUUID().toString()

        var query = """
            mutation {
              packet {
                aggregateVersion
                createProduct(
                  input: {
                    id: "$productId1"
                  }
                ) {
                  id
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/id" to productId1,
        ).check(result)

        query = """
            mutation {
              packet {
                aggregateVersion
                get1: getProduct(id: "$productId1") {
                    id
                }
                get2: getProduct(id: "find: it.id == 'nonExistentId'") {
                    id
                }
                get3: getProduct(id: "nonExistentId" failOnEmpty: false) {
                    id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/aggregateVersion" to "1",
            "/data/packet/get1/id" to productId1,
            "/data/packet/get2" to "null",
            "/data/packet/get3" to "null",
        ).check(result)
    }

    @Test
    fun statusCreateAndUpdateTest() {
        val productId = UUID.randomUUID().toString()

        val openedStatusCode = "opened"
        val closedStatusCode = "closed"
        val statusOpenedReason = "product opened"
        val statusClosedReason = "product closed"

        var query = """
            mutation {
              packet {
                createProduct(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$openedStatusCode"
                        reason: "$statusOpenedReason"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                    reason
                  }
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProduct/statusForPlatform/code" to openedStatusCode,
            "/data/packet/createProduct/statusForPlatform/reason" to statusOpenedReason,
        ).check(result)

        query = """
            mutation {
              packet {
                updateProduct(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$closedStatusCode"
                        reason: "$statusClosedReason"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                    reason
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/updateProduct/statusForPlatform/code" to closedStatusCode,
            "/data/packet/updateProduct/statusForPlatform/reason" to statusClosedReason,
        ).check(result)
    }

    @Test
    fun extendedStatusCreateAndUpdateTest() {
        val productId = UUID.randomUUID().toString()

        val createdStatusForPlatformCode = "created"
        val deletedStatusForPlatformCode = "deleted"
        val checkedStatusForPlatformCode = "checked"

        val newStatusForServiceCode = "new"
        val oldStatusForServiceCode = "old"

        var query = """
            mutation {
              packet {
                createProductWithExtendedStatuses(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$createdStatusForPlatformCode"
                    }
                    statusForService: {
                        code: "$newStatusForServiceCode"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                  }
                  statusForService {
                    code
                  }
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/createProductWithExtendedStatuses/statusForPlatform/code" to createdStatusForPlatformCode,
            "/data/packet/createProductWithExtendedStatuses/statusForService/code" to newStatusForServiceCode,
        ).check(result)

        query = """
            mutation {
              packet {
                updateProductWithExtendedStatuses(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$deletedStatusForPlatformCode"
                    }
                    statusForService: {
                        code: "$oldStatusForServiceCode"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                  }
                  statusForService {
                    code
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/updateProductWithExtendedStatuses/statusForPlatform/code" to deletedStatusForPlatformCode,
            "/data/packet/updateProductWithExtendedStatuses/statusForService/code" to oldStatusForServiceCode,
        ).check(result)

        query = """
            mutation {
              packet {
                updateProductWithExtendedStatuses(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$checkedStatusForPlatformCode"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Transition from status code '$deletedStatusForPlatformCode' to status code '$checkedStatusForPlatformCode' in the 'platform'" +
                " status group does not defined",
        )
    }

    @Test
    fun defaultInitialStatusCreateAndUpdateTest() {
        val productId = UUID.randomUUID().toString()

        val createdStatusForPlatformCode = "created"

        val newStatusForServiceCode = "new"

        val query = """
            mutation {
              packet {
                createProductWithExtendedStatuses(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$createdStatusForPlatformCode"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                  }
                  statusForService {
                    code
                  }
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/createProductWithExtendedStatuses/statusForPlatform/code" to createdStatusForPlatformCode,
            "/data/packet/createProductWithExtendedStatuses/statusForService/code" to newStatusForServiceCode,
        ).check(result)
    }

    @Test
    fun wrongStatusCodeTest() {
        val newStatusForPlatformCode = "new"

        val query = """
            mutation {
              packet {
                createProductWithExtendedStatuses(
                  input: {
                    statusForPlatform: {
                        code: "$newStatusForPlatformCode"
                    }
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
            "Status for type 'ProductWithExtendedStatuses' with code '$newStatusForPlatformCode' " +
                "and group 'platform' is not defined",
        )
    }

    @Test
    fun wrongInitialStatusTest() {
        val checkedStatusForPlatformCode = "checked"

        val query = """
            mutation {
              packet {
                createProductWithExtendedStatuses(
                  input: {
                    statusForPlatform: {
                        code: "$checkedStatusForPlatformCode"
                    }
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
            "Status with group 'platform' and code '$checkedStatusForPlatformCode' for type 'ProductWithExtendedStatuses'" +
                " cannot be used as initial",
        )
    }

    @Test
    fun statusCreateIdempotenceTest() {
        val productId = UUID.randomUUID().toString()
        val idempotenceKey = UUID.randomUUID().toString()

        val openedStatusCode = "opened"
        val statusOpenedReason = "product opened"
        val wrongReason = "wrongReason"

        var query = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                isIdempotenceResponse
                createProduct(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$openedStatusCode"
                        reason: "$statusOpenedReason"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                    reason
                  }
                }
              }
            }
        """

        var result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "false",
            "/data/packet/createProduct/statusForPlatform/code" to openedStatusCode,
            "/data/packet/createProduct/statusForPlatform/reason" to statusOpenedReason,
        ).check(result)

        result = executeQuery(query)

        mapOf(
            "/data/packet/isIdempotenceResponse" to "true",
            "/data/packet/createProduct/statusForPlatform/code" to openedStatusCode,
            "/data/packet/createProduct/statusForPlatform/reason" to statusOpenedReason,
        ).check(result)

        query = """
            mutation {
              packet(idempotencePacketId:"$idempotenceKey") {
                createProduct(
                  input: {
                    id: "$productId"
                    statusForPlatform: {
                        code: "$openedStatusCode"
                        reason: "$wrongReason"
                    }
                  }
                ) {
                  id
                  statusForPlatform {
                    code
                    reason
                  }
                }
              }
            }
        """

        result = executeQuery(query)

        val errorMessage = result.at("/errors/0/message").textValue()
        assertThat(
            errorMessage,
        ).contains(
            "Packet 'packet' execution error: Command 'createProduct' execution error:" +
                " The hash parameters of the command doesn't match with the hash of the previous idempotence call",
        )
    }
}
