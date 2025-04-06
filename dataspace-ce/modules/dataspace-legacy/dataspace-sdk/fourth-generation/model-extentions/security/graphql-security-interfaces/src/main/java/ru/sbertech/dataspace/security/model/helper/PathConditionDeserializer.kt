package ru.sbertech.dataspace.security.model.helper

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import ru.sbertech.dataspace.security.model.dto.PathCondition
import java.io.IOException

/**
 * The list of routing conditions is stored in the form of a serialized string "[{...}, {...}, ...]" (when reading from the DB - TextNode).
 * is passed from outside in the usual json array format (when reading the request from the user - ArrayNode).
 * We parse automatically at the Jackson level, depending on the case.
 * <pre>
 * Example from db:
 * {
 * ...
 * "pathConditions": "[{\"path\": \"...\", \"cond\": \"...\"}, ...]" (serialized string)
 * }
 * Example from the user's request
 * {
 * ...
 * "pathConditions": [
 * {
 * "path": "...",
 * "cond": "...
 * },
 * ...
 * ]
 * }
</pre> *
 */
class PathConditionDeserializer : JsonDeserializer<Map<String, PathCondition>?>() {
    private val objectMapper: ObjectMapper = ObjectMapper()

    @Throws(IOException::class)
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext?,
    ): Map<String, PathCondition> {
        // If an array is received as input, it means we parse it as an array.
        // Otherwise, we expect a string there (storing the serialized original array) - we parse it into a string, then the string into an array.
        val pathConditionsList =
            if (p.isExpectedStartArrayToken) {
                objectMapper.readValue(p, object : TypeReference<List<PathCondition>>() {})
            } else {
                objectMapper.readValue(p.readValueAs(String::class.java), object : TypeReference<List<PathCondition>>() {})
            }

        return PathCondition.asMap(pathConditionsList)
    }
}
