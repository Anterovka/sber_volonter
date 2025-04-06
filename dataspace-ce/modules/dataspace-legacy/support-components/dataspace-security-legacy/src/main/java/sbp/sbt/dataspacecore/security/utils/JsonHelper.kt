package sbp.sbt.dataspacecore.security.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

class JsonHelper private constructor() {
    init {
        throw UnsupportedOperationException("Object creation is not supported for this class")
    }

    companion object {
        @JvmField
        val OBJECT_MAPPER = ObjectMapper()

        /** The text inside the json is assumed to be either a jwt object or another Json in string form. */
        fun getFromJsonTyped(jsonStr: String?, path: List<String?>): JsonNode {
            var jsonNode: JsonNode
            jsonNode = try {
                OBJECT_MAPPER.readTree(jsonStr)
            } catch (e: JsonProcessingException) {
                throw SecurityException("Deserialization error Json", e)
            }
            val i = 0
            for (pathItem in path) {
                val (first, second) = SecurityUtils.clearType(pathItem!!)
                jsonNode = jsonNode.path(first)
                if (second != null) {
                    val dataType = JwtOrJson.byString(second)
                    return when (dataType) {
                        JwtOrJson.JWT ->
                            //TODO rework to pass JsonNode directly
                            JwtHelper.getNode(jsonNode.asText(), path.subList(i + 1, path.size))
                        JwtOrJson.JSON -> getFromJsonTyped(jsonNode.asText(), path.subList(i + 1, path.size))
                    }
                }
            }
            return jsonNode
        }
    }
}
