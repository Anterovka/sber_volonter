package ru.sbertech.dataspace.model4

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import ru.sbertech.dataspace.helpers.GraphQLTestHelper
import java.util.UUID

@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
class GraphQLWithInheritanceTests : GraphQLTestHelper() {
    override fun getModelId() = "4"

    @Test
    fun createAndGetJoinedStrategyTest() {
        val id = UUID.randomUUID().toString()
        val stringValue = "stringValue"
        val stringValueExt = "stringValueExt"
        val firstNameValue = "firstNameValue"

        val query = """
            mutation {
              packet {
                create: createTestEntityExt(
                  input: {
                    id: "$id"
                    pString: "$stringValue"
                    pStringExt: "$stringValueExt"
                    owner: {
                        firstName:"$firstNameValue"
                    }
                  }
                ) {
                  id
                  pString
                  pStringExt
                }
                get: getTestEntityExt(id: "$id") {
                  id
                  pString
                  pStringExt
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/id" to id,
            "/data/packet/create/pString" to stringValue,
            "/data/packet/create/pStringExt" to stringValueExt,
            "/data/packet/get/id" to id,
            "/data/packet/get/pString" to stringValue,
            "/data/packet/get/pStringExt" to stringValueExt,
        ).check(result)
    }

    @Test
    fun createAndGetSingleTableStrategyTest() {
        val id = UUID.randomUUID().toString()
        val stringValueExt = "stringValueExt"

        val query = """
            mutation {
              packet {
                create: createTestEntitySingleTableExt(
                  input: {
                    id: "$id"
                    pStringSingleTableExt: "$stringValueExt"
                  }
                ) {
                  id
                  pStringSingleTableExt
                }
                get: getTestEntitySingleTableExt(id: "$id") {
                  id
                  pStringSingleTableExt
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/id" to id,
            "/data/packet/create/pStringSingleTableExt" to stringValueExt,
            "/data/packet/get/id" to id,
            "/data/packet/get/pStringSingleTableExt" to stringValueExt,
        ).check(result)
    }
}
