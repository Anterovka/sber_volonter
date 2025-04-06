package ru.sbertech.dataspace.graphql.command

import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.CommandExecutionResult
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.REFERENCE

// TODO нужна детализированная обработка ошибок
class GraphQLCommandRefContext : CommandRefContext {
    private val commandsWithRefs = hashSetOf<String>()

    private fun handle(
        propertyValue: UniversalValue?,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    ): UniversalValue? {
        @Suppress("UNCHECKED_CAST")
        return when (propertyValue) {
            is MutableMap<*, *> -> handleMap(propertyValue as MutableMap<String, UniversalValue?>, commandResultByQualifier)
            is Collection<*> -> handleCollection(propertyValue as Collection<UniversalValue>, commandResultByQualifier)
            is String -> {
                if (propertyValue.contains(REFERENCE)) {
                    fillSingleRef(propertyValue, commandResultByQualifier)
                } else {
                    propertyValue
                }
            }

            else -> propertyValue
        }
    }

    private fun handleMap(
        map: MutableMap<String, UniversalValue?>,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    ): UniversalValue {
        map.forEach {
            map[it.key] = handle(it.value, commandResultByQualifier)
        }

        return map
    }

    private fun handleCollection(
        collection: Collection<UniversalValue?>,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    ): UniversalValue {
        val newList = arrayListOf<UniversalValue?>()
        collection.forEach {
            newList.add(handle(it, commandResultByQualifier))
        }
        return newList
    }

    override fun registerReference(commandQualifier: String) {
        commandsWithRefs.add(commandQualifier)
    }

    override fun fillRefs(
        commandQualifier: String,
        propertyValueByName: HashMap<String, UniversalValue?>,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    ) {
        if (!commandsWithRefs.contains(commandQualifier)) {
            return
        }
        propertyValueByName.forEach { (propertyName, propertyValue) ->
            propertyValueByName[propertyName] = handle(propertyValue, commandResultByQualifier)
        }
    }

    override fun fillSingleRef(
        refExpression: String,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    ): UniversalValue? = ReferenceExpHandler.handle(commandResultByQualifier, refExpression)
}

private const val SLASH = "/"
private const val COMMA = ":"

object ReferenceExpHandler {
    fun handle(
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
        refExpression: String,
    ): UniversalValue? {
        var position: Int
        val commaIndex = refExpression.indexOf(COMMA)
        var index = refExpression.indexOf(SLASH)
        var currentProperty: Any

        val commandQualifier: String
        if (index != -1) {
            commandQualifier = refExpression.substring(commaIndex + 1, index)
            currentProperty = commandResultByQualifier[commandQualifier]?.selectionResult
                ?: throw IllegalStateException(
                    "Selection result for command '$commandQualifier' not found. Reference expression: $refExpression",
                )
        } else {
            commandQualifier = refExpression.substring(commaIndex + 1, refExpression.length)

            return commandResultByQualifier[commandQualifier]?.identifier
                ?: throw IllegalStateException(
                    "Result for command '$commandQualifier' doesn't contains entity identifier. Reference expression: $refExpression",
                )
        }

        position = index + 1

        while (position < refExpression.length) {
            index = getIndex(refExpression, position)

            var propertyNameOrCollectionIndex = refExpression.substring(position, index)
            position = index + 1

            if (propertyNameOrCollectionIndex == SchemaHelper.ELEMENTS_FIELD_NAME) {
                index = getIndex(refExpression, position)
                propertyNameOrCollectionIndex = refExpression.substring(position, index)
                position = index + 1
            }

            currentProperty =
                when (currentProperty) {
                    is Map<*, *> -> handleMap(propertyNameOrCollectionIndex, currentProperty, refExpression)
                    is List<*> -> {
                        // TODO обработать NumberFormatException?
                        handleCollection(currentProperty, propertyNameOrCollectionIndex.toInt(), refExpression)
                    }

                    else -> {}
                }
        }

        return currentProperty
    }

    private fun handleMap(
        propertyName: String,
        map: Map<*, *>,
        refExpression: String,
    ): Any =
        map[propertyName]
            ?: throw IllegalStateException("Data for property '$propertyName' in ref-expression $refExpression not found")

    private fun handleCollection(
        collection: List<*>,
        index: Int,
        refExpression: String,
    ): Any =
        collection[index]
            ?: throw IllegalStateException(
                "Data for collection property with index '$index' in ref-expression $refExpression not found",
            )

    private fun getIndex(
        refExpression: String,
        position: Int,
    ): Int {
        val slashIndex = refExpression.indexOf(SLASH, position)
        if (slashIndex == -1) {
            return refExpression.length
        }
        return slashIndex
    }
}
