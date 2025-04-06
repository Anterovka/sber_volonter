import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID

@Disabled
class DictionariesTest : BaseGraphQLTest(modelWithAggregates) {
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
}
