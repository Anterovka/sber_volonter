import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID

@Disabled
class InheritanceTest : BaseGraphQLTest(modelWithAggregates) {
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
                create: createTestEntityExtSingleTable(
                  input: {
                    id: "$id"
                    pStringExtSingleTable: "$stringValueExt"
                  }
                ) {
                  id
                  pStringExtSingleTable
                }
                get: getTestEntityExtSingleTable(id: "$id") {
                  id
                  pStringExtSingleTable
                }
              }
            }
        """

        val result = executeQuery(query)

        mapOf(
            "/data/packet/create/id" to id,
            "/data/packet/create/pStringExtSingleTable" to stringValueExt,
            "/data/packet/get/id" to id,
            "/data/packet/get/pStringExtSingleTable" to stringValueExt,
        ).check(result)
    }
}
