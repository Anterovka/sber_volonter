package sbp.sbt.dataspacecore.security.utils

import com.fasterxml.jackson.databind.JsonNode
import org.apache.commons.codec.binary.Base64
import sbp.sbt.dataspacecore.utils.CommonUtils
import sbp.sbt.dataspacecore.utils.FeatherHelper
import sbp.sbt.dataspacecore.utils.exceptions.ValueNodeNotFound
import java.io.IOException

class JwtHelper private constructor() {
    init {
        throw UnsupportedOperationException("Object creation is not supported for this class")
    }

    companion object {
        private const val JWT_PARTS = 3
        private const val JWT_SEPARATOR = "."
        val BASE_64 = Base64(true)
        @Throws(IOException::class)
        fun jwtStringToJsonNode(jwtString: String): JsonNode {
            val jwtParts = jwtString.split(JWT_SEPARATOR).toTypedArray()
            if (jwtParts.size != JWT_PARTS) {
                throw SecurityException("Invalid JWT format")
            }
            val base64EncodedBody = jwtParts[1]
            return JsonHelper.OBJECT_MAPPER.readTree(BASE_64.decode(base64EncodedBody))
        }

        /** Translates JWT into a path-value. Path separator ".". Arrays are translated into string representation.  */
        @Throws(IOException::class)
        fun parseJwt(jwtString: String): Map<String, String> {
            val jsonNode = jwtStringToJsonNode(jwtString)
            val resultMap: MutableMap<String, String> = HashMap()
            populateStringMapWithJsonNode(resultMap, "", jsonNode)
            return resultMap
        }

        @Throws(IOException::class)
        fun parseJson(jsonNode: JsonNode): Map<String, String> {
            val resultMap: MutableMap<String, String> = HashMap()
            populateStringMapWithJsonNode(resultMap, "", jsonNode)
            return resultMap
        }

        @Throws(IOException::class)
        fun getJwtFiledAsNode(
            jwtString: String,
            fieldPath: String?
        ): JsonNode {
            val jwtNode = jwtStringToJsonNode(jwtString)
            return jwtNode.at(fieldPath)
        }

        /**
         *
         * @param jwtString
         * @param fieldPath path to the field, separated by a slash between objects
         * @return
         * @throws IOException
         */
        @Throws(IOException::class)
        fun getJwtFieldAsString(jwtString: String, fieldPath: String): String? {
            var fieldPath = fieldPath
            if (!fieldPath.startsWith("/")) {
                fieldPath = "/$fieldPath"
            }
            val jwtFiledAsNode = getJwtFiledAsNode(jwtString, fieldPath)
            return if (jwtFiledAsNode == null || jwtFiledAsNode.isMissingNode) null else jwtFiledAsNode.asText()
        }

        /** Translates Json into a key-value map taking into account nesting. Path separator ".".
        * Arrays are translated into string representation */
        fun populateStringMapWithJsonNode(
            mapToPopulate: MutableMap<String, String>,
            pathToNode: String,
            jsonNode: JsonNode
        ) {
            if (jsonNode.isObject) {
                val fieldNamesIterator = jsonNode.fieldNames()
                while (fieldNamesIterator.hasNext()) {
                    val currentFieldName = fieldNamesIterator.next()
                    val value = jsonNode[currentFieldName]
                    val newPath = if (pathToNode.isEmpty()) currentFieldName else "$pathToNode.$currentFieldName"
                    populateStringMapWithJsonNode(mapToPopulate, newPath, value)
                }
            } else {
                mapToPopulate[pathToNode] = FeatherHelper.jsonNodeToFeatherString(jsonNode)
            }
        }

        /** Frames the string in single quotes, and replaces the single quotes inside the string with two single quotes  */
        private fun screenString(input: String): String = CommonUtils.screenString(input)

        /** Combines elements en masse through the separator '/'  */
        fun joinToPath(pathParts: Iterable<String?>?): String {
            return "/" + java.lang.String.join("/", pathParts)
        }

        /** The text is assumed to be in English and does not need translation. */
        fun getNode(jwt: String, path: Iterable<String?>): JsonNode {
            val node: JsonNode
            node = try {
                getJwtFiledAsNode(jwt, joinToPath(path))
            } catch (ex: Exception) {
                throw ValueNodeNotFound(ex)
            }
            if (node == null || node.isMissingNode) {
                throw ValueNodeNotFound(path.toString())
            }
            return node
        }
    }
}
