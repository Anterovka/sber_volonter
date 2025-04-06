package ru.sbertech.dataspace.model4

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import ru.sbertech.dataspace.helpers.GraphQLTestHelper
import java.util.UUID

@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class GraphQLWithDictionariesTests : GraphQLTestHelper() {
    override fun getModelId() = "4"

    @Test
    fun createAndGetDictionaryTest() {
        val id = UUID.randomUUID().toString()
        val currencyName = "RUB"

        val query = """
            mutation {
              dictionaryPacket {
                updateOrCreate: updateOrCreateCurrency(
                  input: {
                    id: "$id"
                    name: "$currencyName"
                  }
                ) {
                    created
                    returning {
                        id
                        name
                    }
                }
                get: getCurrency(id: "$id") {
                  id
                  name
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/dictionaryPacket/updateOrCreate/created" to "true",
            "/data/dictionaryPacket/updateOrCreate/returning/id" to id,
            "/data/dictionaryPacket/updateOrCreate/returning/name" to currencyName,
            "/data/dictionaryPacket/get/id" to id,
            "/data/dictionaryPacket/get/name" to currencyName,
        ).check(result)
    }

    @Test
    fun updateOrCreateWithoutReturningTest() {
        val id = UUID.randomUUID().toString()
        val currencyName = "RUB"

        val query = """
            mutation {
              dictionaryPacket {
                updateOrCreate: updateOrCreateCurrency(
                  input: {
                    id: "$id"
                    name: "$currencyName"
                  }
                ) {
                    created
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/dictionaryPacket/updateOrCreate/created" to "true",
        ).check(result)
    }

    @Test
    fun getDictionaryInPacketTest() {
        val id = UUID.randomUUID().toString()

        var query = """
            mutation {
              dictionaryPacket {
                updateOrCreate: updateOrCreateCurrency(
                  input: {
                    id: "$id"
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
            "/data/dictionaryPacket/updateOrCreate/returning/id" to id,
        ).check(result)

        query = """
            mutation {
              packet {
                get: getCurrency(id: "$id") {
                  id
                }
              }
            }
        """

        result = executeQuery(query)

        mapOf(
            "/data/packet/get/id" to id,
        ).check(result)
    }
}
