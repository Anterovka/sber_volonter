package ru.sbertech.dataspace.security.utils

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.util.StringUtils
import ru.sbertech.dataspace.security.exception.StringReplacementException
import sbp.sbt.dataspacecore.security.utils.JsonHelper
import sbp.sbt.dataspacecore.security.utils.JwtHelper
import sbp.sbt.dataspacecore.security.utils.SecurityUtils
import sbp.sbt.dataspacecore.utils.CommonUtils
import sbp.sbt.dataspacecore.utils.DataType
import sbp.sbt.dataspacecore.utils.FeatherHelper
import sbp.sbt.dataspacecore.utils.exceptions.ValueNodeNotFound
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class GraphQLStringReplacer {
    /**
     * General class of result replacement of variable name with its value.
     * Has two descendants.
     * SingleReplacement - represents the variable value expressed by primitive (including collection values - turned into string)
     * CollectionReplacement - represents a collection of replacements. When an operation is performed with a list, for example,
     * Entity list creation or update.
     */
    interface Replacement

    class SingleReplacement(
        var singleValue: String,
    ) : Replacement

    /** Represents a collection of replacements*/
    class CollectionReplacement(
        /** Path to the first collection.
         * Used to control that all collections from one "source" */
        val pathBeforeCollection: String,
        /** Path inside the first collection */
        private val pathAfterCollection: String,
        /** Escaped values (full path) */
        var collectionValue: List<Any>?,
        /** Position of the variable inside the string expression */
        var position: Int = 0,
    ) : Replacement {
        val fullName get() = this.pathBeforeCollection + '.' + this.pathAfterCollection
    }

    enum class ConditionParsState {
        NONE,
        DOLLAR_SIGN,
        DOLLAR_SIGN_WITH_OPENING_BRACKET,
    }

    companion object {
        private val TYPES_MAPPING = HashMap<String, DataType>()

        init {
            TYPES_MAPPING["character"] = DataType.CHARACTER
            TYPES_MAPPING["string"] = DataType.STRING
            TYPES_MAPPING["byte"] = DataType.BYTE
            TYPES_MAPPING["short"] = DataType.SHORT
            TYPES_MAPPING["integer"] = DataType.INTEGER
            TYPES_MAPPING["long"] = DataType.LONG
            TYPES_MAPPING["float"] = DataType.FLOAT
            TYPES_MAPPING["double"] = DataType.DOUBLE
            TYPES_MAPPING["bigdecimal"] = DataType.BIG_DECIMAL
            TYPES_MAPPING["localdate"] = DataType.DATE
            TYPES_MAPPING["localdatetime"] = DataType.DATETIME
            TYPES_MAPPING["offsetdatetime"] = DataType.OFFSET_DATETIME
            TYPES_MAPPING["time"] = DataType.TIME
            TYPES_MAPPING["date"] = DataType.DATE
            TYPES_MAPPING["boolean"] = DataType.BOOLEAN
            TYPES_MAPPING["byte[]"] = DataType.BYTE_ARRAY
        }

        fun replaceAndReturn(
            condition: String,
            nonParsedVariables: Map<String, Any>,
        ): List<String> {
            val auth = SecurityUtils.getCurrentToken()
            val attributes = auth.attributes
            return replaceAndReturn(condition, nonParsedVariables, attributes)
        }

        // TODO remove variables2 and change to query approach

        /**
         * variables2 - structure where the key is the full path to the string value (e.g., the key could be "obj.field"
         */
        fun replaceAndReturn(
            condition: String,
            variables1: Map<String, Any>,
            variables2: Map<String, String>,
        ): List<String> =
            replaceAndReturn(condition) { replacementName: String ->
                getReplacement(replacementName, variables1, variables2)
            }

        fun replaceAndReturn(
            condition: String,
            getReplacementFunction: (replacementName: String) -> Replacement?,
        ): List<String> {
            // conditionWithoutCollectionReplacements - string where collection variables are not replaced (the rest are replaced with values)
            // collectionReplacements - list of information about positions and values of collection variables
            val (conditionWithoutCollectionReplacements, collectionReplacements) =
                replaceSingleReplacementsAndSaveCollectionReplacements(
                    condition,
                    getReplacementFunction,
                )

            // Creates a directory at the specified File
            if (collectionReplacements.isEmpty()) {
                return listOf(conditionWithoutCollectionReplacements)
            }

            // Creates a directory at the specified File
            // Let's check if they have a common source of "collectiveness" (i.e., there is no Cartesian product).
            if (collectionReplacements
                    .map {
                        it.pathBeforeCollection
                    }.distinct()
                    .size > 1
            ) {
                throw StringReplacementException("CheckSelect $condition содержит две разные коллекции. Декартово произведение запрещено")
            }

            val distinctSizes =
                collectionReplacements
                    .map {
                        it.collectionValue!!.size
                    }.distinct()
            if (distinctSizes.size > 1) {
                throw StringReplacementException("For CheckSelect-a $condition, several collections of different lengths were received")
            }

            var shift: Int
            // Currently, we are going to replace the collections of elements.
            // It is important to remember that one expression may contain several replacements, so here we need to maintain the offset
            val resultList = mutableListOf<String>()
            for (i in 0 until distinctSizes[0]) {
                shift = 0
                var elemCondition = conditionWithoutCollectionReplacements
                for (replacement in collectionReplacements) {
                    val srtToReplace = replacement.collectionValue!![i] as String
                    elemCondition =
                        elemCondition.replaceRange(
                            replacement.position + shift,
                            replacement.position + replacement.fullName.length + 3 + shift,
                            srtToReplace,
                        )
                    // The algorithm for obtaining this strange formula is described in the method replaceSingleReplacementsAndSaveCollectionReplacements
                    shift = shift + (srtToReplace.length) - (replacement.fullName.length + 3)
                }
                resultList.add(elemCondition)
            }

            return resultList
        }

        /**
         * Accepts filtering condition with variables.
         * Returns &lt;filtering condition with replaced non-collection variables (collections are not replaced); position and value information of collection variables&gt;
         *
         * @getReplacementFunction - a function that returns a variable's value represented by an object
         */
        private fun replaceSingleReplacementsAndSaveCollectionReplacements(
            condition: String,
            getReplacementFunction: (replacementName: String) -> Replacement?,
        ): Pair<String, List<CollectionReplacement>> {
            val collectionReplacements = mutableListOf<CollectionReplacement>()

            /**
             * Generally, there shouldn't be initialization here, but Kotlin can't understand that if this variable is used,
             * then according to the algorithm it will always be initialized
             * position of the opening bracket of the variable
             */
            var openingCurlyBracket = -1

            /**
             * Removing dollar escaping reduces the string, this variable accounts for how many characters we shortened the string
             * Because we are running along the original line, and we are correcting the resulting one.
             */
            var shift = 0

            /** This line will contain the result of replacing non-collection variables in the original string.
             * Collection variables are not replaced, but information about their position and value in another variable is preserved */
            var conditionWithoutCollectionReplacements = condition
            val charArray = condition.toCharArray()
            var parseState = ConditionParsState.NONE

            for (i in charArray.indices) {
                when (parseState) {
                    ConditionParsState.NONE -> {
                        if (charArray[i] == '$') {
                            if (i != 0 && charArray[i - 1] == '\\') {
                                // If there is a dollar sign and it is escaped, then you need to remove the escaping
                                // Creates a directory at the specified File
                                conditionWithoutCollectionReplacements =
                                    conditionWithoutCollectionReplacements.replaceRange(
                                        i - 1 + shift,
                                        i + 1 + shift,
                                        "$",
                                    )
                                shift--
                            } else {
                                parseState = ConditionParsState.DOLLAR_SIGN
                            }
                        }
                    }

                    ConditionParsState.DOLLAR_SIGN -> {
                        if (charArray[i] == '{') {
                            // If the last character was '$' and it was not escaped, and the current one is an opening curly brace
                            parseState = ConditionParsState.DOLLAR_SIGN_WITH_OPENING_BRACKET
                            openingCurlyBracket = i
                        } else {
                            parseState = ConditionParsState.NONE
                        }
                    }

                    ConditionParsState.DOLLAR_SIGN_WITH_OPENING_BRACKET -> {
                        // Skip the characters to the closing curly brace
                        if (charArray[i] != '}') {
                            continue
                        } else {
                            // everything between openingCurlyBracket and this place is the variable name"
                            getReplacementFunction(
                                String(charArray.copyOfRange(openingCurlyBracket + 1, i)),
                            )?.let {
                                if (it is SingleReplacement) {
                                    // If the replacement is not a collection, then we do it here and now
                                    conditionWithoutCollectionReplacements =
                                        conditionWithoutCollectionReplacements.replaceRange(
                                            openingCurlyBracket - 1 + shift,
                                            i + 1 + shift,
                                            it.singleValue,
                                        )

                                    // to shift we add how much shorter (or longer) the string became.
                                    // The formula looks like a random set of characters, but this is a consequence of expanding the brackets. Originally, it was like this:
                                    // Need to change the shift value to the difference between the length of the replaced string and the placeholder's length."
                                    // So, we add the length of the new piece and subtract the length of the old one.
                                    // But we don't have a variable that contains the length of the old piece. We need to calculate it.
                                    // This is equal to %second argument of the replaceRange method% minus %first argument of the replaceRange method%.
                                    // There appears to be a lot there, but if you expand the brackets, it boils down to (i - openingCurlyBracket + 2)
                                    shift = shift + it.singleValue.length - (i - openingCurlyBracket + 2)
                                } else if (it is CollectionReplacement) {
// if a variable path contains a collection, then it is necessary to perform a check for each element of the collection then
                                    it.position = openingCurlyBracket - 1 + shift
                                    collectionReplacements.add(it)
                                }
                            }
                            parseState = ConditionParsState.NONE
                        }
                    }
                }
            }
            return Pair(conditionWithoutCollectionReplacements, collectionReplacements)
        }

        /**
         * There are several data sources for replacements:
         * 1) Variables in GraphQL that are declared in the query.
         They have already been processed by the GraphQL engine and all disputed points with date deserialization have been passed.
         All that is needed is to take the object (which is absolutely certain to be an object of the correct type) and serialize it.
         * 2) Variables GraphQL that are not declared in the query.
         The concept is worse, as objects are deserialized into something incomprehensible.
         *     TODO is not yet being routed through
         * 3) Variables from JWT
         * Formally, it may not even be a JWT.
         The essence is that we can insert data from JWT into the request.
         *
         * @param replacementName - string located inside the ${} construct
         * @param variables1 - variables from GQL
         * @param variables2 - variables from jwt/json/header
         */
        private fun getReplacement(
            replacementName: String,
            variables1: Map<String, Any>,
            variables2: Map<String, String>,
        ): Replacement? {
            val (dataType, varPath, dataSource) = getVarInfo(replacementName)

            when (dataSource) {
                "req" -> {
                    val token =
                        SecurityUtils.findCurrentToken()
                            ?: throw StringReplacementException("Accessing the variable in the http request, but the token is missing")

                    if (token.request == null) {
                        throw StringReplacementException(
                            "Accessing the variable in the http request, but the request is missing in the token",
                        )
                    }

                    val pathItems = varPath.split(".")
                    val (headerName, headerType) = SecurityUtils.clearType(pathItems[0])
                    // If the header is typed, we get the value
                    var headerValue: String = token.request!!.getHeader(headerName) ?: throw ValueNodeNotFound("$pathItems")
                    var screened = false
                    if (StringUtils.hasLength(headerType)) {
                        val node: JsonNode =
                            when (headerType!!.lowercase()) {
                                "jwt" -> JwtHelper.getNode(headerValue, pathItems.subList(1, pathItems.size))
                                "json" ->
                                    JsonHelper.getFromJsonTyped(
                                        headerValue,
                                        pathItems.subList(1, pathItems.size),
                                    )
                                else ->
                                    throw StringReplacementException("Unknown variable type $headerType of variable $headerName")
                            }
                        if (node.isMissingNode) {
                            throw ValueNodeNotFound("$pathItems")
                        }
                        headerValue =
                            FeatherHelper.jsonNodeToFeatherString(
                                node,
                                DataType.valueOf(dataType.first.name),
                            )
                        screened = true
                    }
                    if (dataType.second || screened) {
                        return SingleReplacement(headerValue)
                    }
                    return SingleReplacement(screen(replacementName, headerValue, dataType.first))
                }

                "jwt" -> {
                    // т.к. != "default", то достает значение токена
                    // The text inside cannot contain intermediate collections, as it is unclear what to do with them.
                    val valueFromToken = variables2[varPath]
                    if (valueFromToken != null) {
                        // valueFromToken is already in quotes, so we don't wrap it
                        // Quotation marks are inserted when subtracting. The reason was that it was more convenient
                        return SingleReplacement(valueFromToken)
                    }
                    // Getting null hereafter replacement will not be performed, as the value was not found and most likely will fail during execution.
                    return null
                }

                "default" -> {
                    val tripleResult = iterateThroughObject(variables1, varPath.split("."), false)
                    // If the result is obtained from the collection
                    if (tripleResult.third != null) {
                        val screenedValues =
                            (tripleResult.first as List<Any>)
                                .map {
                                    screen(replacementName, it, dataType.first)
                                }.toList()

                        return CollectionReplacement(tripleResult.second!!, tripleResult.third!!, screenedValues)
                    }
                    // If the collection was not encountered in the path
                    val result = tripleResult.first
                    val resultPath = tripleResult.second

                    if (result == null) {
                        throw StringReplacementException("In checkSelect along the path '$resultPath' a null value was found")
                    }
                    // The value cannot be an object (Map) or list if the user did not specify that it is a list.
                    val isWrongValue = result is Map<*, *> || result is List<*> && !dataType.second
                    if (isWrongValue) {
                        throw StringReplacementException("In checkSelect along the path '$resultPath' a non-primitive field was found")
                    }
                    // The value is escaped because the value is transmitted by the consumer,
                    // which can internally use a quote and modify the string expression (SQL injection)
                    return SingleReplacement(screen(replacementName, result, dataType.first))
                }

                else -> {
                    throw StringReplacementException("Unknown variable source " + dataSource)
                }
            }
        }

        /**
         The method is called iteratively, so the first argument is Any?.
         * This object can be either Map<String, Any?>? or some primitive type supported by GraphQL
         *
         * @param objectToExplore - Map<String, Object> in which you need to find the value
         * @param pathToField - the path to the value, represented as a list of node names (the path is compiled as if there were no intermediate collections)
         * @param throwIfIterable - throw an exception if the intermediate element in the path is a collection
         * @param outerName - already passed path (do not fill)
         *
         * @return <Found result (can be a collection), path to the result/to the first collection, path from the first collection to the result>
         */
        private fun iterateThroughObject(
            objectToExplore: Any?,
            pathToField: List<String>,
            throwIfIterable: Boolean,
            outerName: String = "",
        ): Triple<Any?, String?, String?> {
            /** The string with the path that we have already traversed. */
            var nameBefore = outerName

            // The path that still needs to be traveled
            val restPathToPass: List<String>

            var iterated = objectToExplore

            //  input.code
            for ((pathCount, pathElem) in pathToField.withIndex()) {
                if (iterated == null) {
                    throw StringReplacementException("Along the way '$nameBefore' the '$pathElem' property is null")
                }

                if (iterated is Map<*, *>) {
                    if (iterated.containsKey(pathElem)) {
                        iterated = iterated[pathElem]
                    } else {
                        throw StringReplacementException("Along the way '$nameBefore' the property '$pathElem' was not found")
                    }
                } else if (iterated is Iterable<*>) {
                    // Updates a collection of objects
                    if (throwIfIterable) {
                        throw StringReplacementException("The path '$nameBefore' is a collection inside another collection")
                    }

                    val allElemsInPath = mutableListOf<Any?>()
                    // Остальные элементы пути, включая текущий.
                    restPathToPass = pathToField.subList(pathCount, pathToField.size)
                    for (elem in iterated) {
                        // We need to walk through some path for each element in the collection, but this time the collection does not resolve.
                        // Therefore, the algorithm
                        val inner = iterateThroughObject(elem, restPathToPass, true, nameBefore)
                        allElemsInPath.add(inner.first)
                    }
                    return Triple(allElemsInPath, nameBefore, restPathToPass.joinToString(separator = "."))
                }

                if (nameBefore.isNotEmpty()) {
                    nameBefore += "."
                }
                nameBefore += pathElem
            }
            return Triple(iterated, nameBefore, null)
        }

        /**
         * Accepts the full name of the variable.
         * Returns Type, array, path of variable in data source, type of data source (jwt, default).
         * Example of variable: ${integer[]:jwt:/userInfo/sub}
         * TODO replace the result with some kind of object
         *
         * @return Triple&lt;Pair&lt;Variable type, is array&gt;, path/variable name, Variable data source&gt;
         */
        private fun getVarInfo(fullStringToReplace: String): Triple<Pair<DataType, Boolean>, String, String> {
            val splitByColon = fullStringToReplace.split(":")
            var dataType = DataType.STRING
            var pathToReplace = fullStringToReplace
            var dataSource = "default"
            var isArray = false

            /* The variable name can include 3 parts:
             * 1 - Type of variable and array flag
             * 2 - Data source (jwt)
             * 3 - Path to the variable inside the data source */
            if (splitByColon.size == 3) {
                val realType = getType(splitByColon[0])
                dataType = TYPES_MAPPING[realType.lowercase(Locale.getDefault())]
                    ?: throw StringReplacementException(
                        "Unable to find type $realType",
                    )
                isArray = isArray(splitByColon[0])
                dataSource = splitByColon[1]
                pathToReplace = splitByColon[2]
            } else if (splitByColon.size == 2) {
                /*
                 * Here are 2 options
                 * 1 - the first part defines the source (jwt)
                 * 2 - the first part defines the variable type
                 * TODO possibly need to make it case-insensitive if not done before.
                 * TODO the list of acceptable data sources needs to be moved somewhere.
                 */
                if (splitByColon[0] == "jwt" || splitByColon[0] == "req") {
                    dataSource = splitByColon[0]
                    pathToReplace = splitByColon[1]
                } else {
                    val realType = getType(splitByColon[0])
                    dataType = TYPES_MAPPING[realType.lowercase(Locale.getDefault())]
                        ?: throw StringReplacementException(
                            "The type $realType could not be found",
                        )
                    isArray = isArray(splitByColon[0])
                    pathToReplace = splitByColon[1]
                }
            }
            // If split returns more than 3 values, then we do not throw an exception
            // next, there will be no variable with such a complex name and no replacement will be performed
            return Triple(Pair(dataType, isArray), pathToReplace, dataSource)
        }

        /** Removes trailing brackets [] (array indicator), if present */
        fun getType(input: String): String {
            if (isArray(input)) {
                return if (input.length == 2) {
                    "string"
                } else {
                    input.substring(0, input.length - 2)
                }
            }
            return input
        }

        fun isArray(input: String): Boolean = input.endsWith("[]")

        /** Escaping variable values for substitution into string expressions, for example, dates are added with the prefix 'D'.
         * Strings, Dates, DateTimes, and OffsetDateTimes are processed; for other types, the replacement is ' -> "" without framing.
         * If the value cannot be converted to the given type, then an exception.
         *
         * @param replacementName - exception variable name, used in exceptions
         * @param value - variable value to be escaped
         * @param dataType - expected type of variable.
         * */
        private fun screen(
            replacementName: String,
            value: Any,
            dataType: DataType,
        ): String {
            return when (dataType) {
                // escaping quotes ' -> '' + framing a string in single quotes
                DataType.STRING -> screenString(value as String)
                DataType.DATE -> {
                    if (value !is String) {
                        throw StringReplacementException(
                            "The replacement of $replacementName is a date, but an object that is not a string was found",
                        )
                    }
                    val parsed = LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
                    // TODO it is not clear why, if only the date is needed, the value is reduced to the date of time???
                    // to clarify whether the physical can accept a date without a time
                    // perhaps you need to apply ISO_LOCAL_DATE
                    return "D" +
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(
                            LocalDateTime.ofInstant(
                                parsed.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                                ZoneId.systemDefault(),
                            ),
                        )
                }

                DataType.DATETIME -> {
                    if (value !is String) {
                        throw StringReplacementException(
                            "The replacement of $replacementName is a date, but an object that is not a string was found",
                        )
                    }
                    val parsed = LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    "D" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(parsed)
                }

                DataType.OFFSET_DATETIME -> {
                    if (value !is String) {
                        throw StringReplacementException(
                            "The replacement of $replacementName is a date, but an object that is not a string was found",
                        )
                    }
                    val parsed = OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    "D" + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(parsed)
                }

                DataType.TIME -> {
                    if (value !is String) {
                        throw StringReplacementException(
                            "The replacement of $replacementName is time-consuming, but an object that is not a string was found",
                        )
                    }
                    val parsed = LocalTime.parse(value, DateTimeFormatter.ISO_LOCAL_TIME)
                    "T" + DateTimeFormatter.ISO_LOCAL_TIME.format(parsed)
                }

                else -> {
                    value.toString().replace("'", "''")
                }
            }
        }

        /** Wraps a string in single quotes, inside the string replaces single quotes with two single quotes */
        fun screenString(input: String): String = CommonUtils.screenString(input)
    }
}
