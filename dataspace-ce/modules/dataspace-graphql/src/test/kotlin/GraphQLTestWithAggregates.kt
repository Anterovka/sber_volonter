import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID

@Disabled
class GraphQLTestWithAggregates : BaseGraphQLTest(modelWithAggregates) {
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
}
