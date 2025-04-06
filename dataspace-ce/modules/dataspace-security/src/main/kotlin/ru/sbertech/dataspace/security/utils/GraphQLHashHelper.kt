package ru.sbertech.dataspace.security.utils

import graphql.ExecutionInput
import graphql.ParseAndValidate
import graphql.language.Document
import graphql.language.Field
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.ListType
import graphql.language.NonNullType
import graphql.language.ObjectValue
import graphql.language.OperationDefinition
import graphql.language.Selection
import graphql.language.SelectionSet
import graphql.language.Type
import graphql.language.TypeName
import graphql.language.Value
import graphql.language.VariableReference
import org.apache.commons.codec.digest.DigestUtils
import ru.sbertech.dataspace.security.model.dto.Operation
import java.util.StringJoiner

object GraphQLHashHelper {
    private fun graphQLHashHelper() {
        // no-ops
    }

    private const val TYPENAME = "__typename"

    private fun documentToSomething(
        document: Document,
        fragmentsHolder: FragmentsHolder = FragmentsHolder(),
    ): String {
        val innerDocument = InnerDocument()
        for (definition in document.definitions) {
            if (definition is OperationDefinition) {
                val innerOperationDefinition = InnerOperationDefinition()
                val operationDefinition = definition
                val variableDefinitions = operationDefinition.variableDefinitions
                for (variableDefinition in variableDefinitions) {
                    val innerVariableDefinition = InnerVariableDefinition()
                    innerVariableDefinition.name = variableDefinition.name
                    val typeBuilder = StringBuilder()
                    transformType(variableDefinition.type, typeBuilder)
                    innerVariableDefinition.type = typeBuilder.toString()
                    innerOperationDefinition.variableDefinitions.add(innerVariableDefinition)
                }
                innerOperationDefinition.selections = transformSelectionSet(operationDefinition.selectionSet)
                innerDocument.operationDefinitions.add(innerOperationDefinition)
            }
            if (definition is FragmentDefinition) {
                val innerOperationDefinition = InnerOperationDefinition()
                val fragmentDefinition = definition
                innerOperationDefinition.name = "..." + fragmentDefinition.name
                fragmentsHolder.addSpec(
                    innerOperationDefinition.name,
                    transformSelectionSet(fragmentDefinition.selectionSet),
                )
            }
        }
        replaceAllFragmentsWithFields(innerDocument.operationDefinitions, fragmentsHolder)
        return innerDocument.toString()
    }

    private fun transformType(
        type: Type<*>,
        stringBuilder: StringBuilder,
    ) {
        if (type is ListType) {
            stringBuilder.append('[')
            transformType(type.type, stringBuilder)
            stringBuilder.append(']')
        }
        if (type is NonNullType) {
            transformType(type.type, stringBuilder)
            stringBuilder.append('!')
        }
        if (type is TypeName) {
            stringBuilder.append(type.name)
        }
    }

    private fun transformSelectionSet(selectionSet: SelectionSet): MutableSet<InnerSelection> {
        val innerSelections: MutableSet<InnerSelection> = LinkedHashSet()
        for (selection in selectionSet.selections) {
            transformAndInsertSelection(selection, innerSelections)
        }
        return innerSelections
    }

    private fun transformAndInsertSelection(
        selection: Selection<*>,
        collectionToInsert: MutableSet<InnerSelection>,
    ) {
        val innerSelection = InnerSelection()
        if (selection is Field) {
            val field = selection
            if (field.name == TYPENAME) {
                return
            }
            innerSelection.name = field.name
            if (field.alias != null) {
                innerSelection.alias = field.alias
            }
            for (argument in field.arguments) {
                val innerArgument = InnerArgument()
                innerArgument.name = argument.name
                if (argument.value != null) {
                    innerArgument.value = valueToString(argument.value)
                }
                innerSelection.arguments.add(innerArgument)
            }
            if (field.selectionSet != null) {
                innerSelection.selections = transformSelectionSet(field.selectionSet)
            }
        }
        if (selection is FragmentSpread) {
            innerSelection.name = "..." + selection.name
        }
        if (selection is InlineFragment) {
            val inlineFragment = selection
            innerSelection.name = "_on_" + inlineFragment.typeCondition.name
            if (inlineFragment.selectionSet != null) {
                innerSelection.selections = transformSelectionSet(inlineFragment.selectionSet)
            }
        }
        collectionToInsert.add(innerSelection)
    }

    private fun valueToString(value: Value<*>): String =
        if (value is ObjectValue) {
            val objectFields = value.objectFields
            val fields: MutableSet<String> = LinkedHashSet()
            for (objectField in objectFields) {
                fields.add(objectField.name)
                if (objectField.value != null) {
                    fields.add(valueToString(objectField.value))
                }
            }
            val stringJoiner = StringJoiner(" ")
            for (str in fields) {
                stringJoiner.add(str)
            }
            "($stringJoiner)"
        } else if (value is VariableReference) {
            '$'.toString() + value.name
        } else {
            value.toString()
        }

    private fun replaceAllFragmentsWithFields(
        operations: List<InnerOperationDefinition>?,
        fragmentsHolder: FragmentsHolder,
    ) {
        if (operations != null && !operations.isEmpty()) {
            for (operation in operations) {
                replaceAllFragmentsWithFieldsInSelections(operation.selections, fragmentsHolder)
            }
        }
    }

    /**
     * Replace in the selection all sub-selections with fields
     * @param selections some collection of selections
     * @param fragmentsHolder storage with fragment information
     */
    private fun replaceAllFragmentsWithFieldsInSelections(
        selections: MutableSet<InnerSelection>?,
        fragmentsHolder: FragmentsHolder,
    ) {
        if (selections != null && !selections.isEmpty()) {
            var selectionsReplacedByFragment: MutableSet<InnerSelection>? = null
            val iterator = selections.iterator()
            while (iterator.hasNext()) {
                val innerSelection = iterator.next()
                if (fragmentsHolder.isFragment(innerSelection.name)) {
                    if (selectionsReplacedByFragment == null) {
                        selectionsReplacedByFragment = LinkedHashSet()
                    }
                    val specAsStringByName = fragmentsHolder.getSpecByName(innerSelection.name)
                    replaceAllFragmentsWithFieldsInSelections(specAsStringByName, fragmentsHolder)
                    selectionsReplacedByFragment.addAll(specAsStringByName)
                    iterator.remove()
                }
                replaceAllFragmentsWithFieldsInSelections(innerSelection.selections, fragmentsHolder)
            }
            if (selectionsReplacedByFragment != null) {
                selections.addAll(selectionsReplacedByFragment)
            }
        }
    }

    fun calculateHash(operation: Operation): String {
        val executionInput =
            ExecutionInput
                .newExecutionInput()
                .query(operation.body)
                .operationName(operation.name)
                .build()
        val parsed = ParseAndValidate.parse(executionInput)
        if (parsed.syntaxException != null) {
            throw RuntimeException("Error parsing the passed GraphQL operation", parsed.syntaxException)
        }
        return calculateHash(parsed.document)
    }

    fun calculateHash(document: Document): String = calculateHash(documentToSomething(document))

    private fun calculateHash(str: String): String = DigestUtils.sha256Hex(str)

    private class FragmentsHolder {
        private var fragmentNameToSpec: MutableMap<String?, MutableSet<InnerSelection>>? = null

        fun addSpec(
            name: String?,
            spec: MutableSet<InnerSelection>,
        ) {
            if (fragmentNameToSpec == null) {
                fragmentNameToSpec = LinkedHashMap()
            }
            fragmentNameToSpec!![name] = spec
        }

        fun isFragment(name: String?): Boolean =
            if (fragmentNameToSpec != null) {
                fragmentNameToSpec!!.containsKey(name)
            } else {
                false
            }

        fun getSpecByName(name: String?): MutableSet<InnerSelection> = fragmentNameToSpec!![name]!!
    }

    private class InnerVariableDefinition : Comparable<InnerVariableDefinition> {
        var name: String? = null
        var type: String? = null

        override fun toString(): String = "$name:$type"

        override fun compareTo(other: InnerVariableDefinition): Int = name!!.compareTo(other.name!!)
    }

    private class InnerArgument : Comparable<InnerArgument> {
        var name: String? = null
        var value: String? = null

        override fun toString(): String = "$name:$value"

        override fun compareTo(other: InnerArgument): Int = name!!.compareTo(other.name!!)
    }

    private class InnerSelection : Comparable<InnerSelection> {
        var name: String? = null
        var alias: String? = null
        val arguments: MutableSet<InnerArgument> = LinkedHashSet()
        var selections: MutableSet<InnerSelection>? = null

        override fun toString(): String {
            val stringJoiner = StringJoiner("\n")
            if (alias != null) {
                stringJoiner.add("$alias:$name")
            } else {
                stringJoiner.add(name)
            }
            if (!arguments.isEmpty()) {
                val sj = StringJoiner("\n")
                for (argument in arguments) {
                    sj.add(argument.toString())
                }
                stringJoiner.add("($sj)")
            }
            if (selections != null && !selections!!.isEmpty()) {
                val sj = StringJoiner("\n")
                for (selection in selections!!) {
                    sj.add(selection.toString())
                }
                stringJoiner.add("{$sj}")
            }
            return stringJoiner.toString()
        }

        override fun compareTo(other: InnerSelection): Int = name!!.compareTo(other.name!!)
    }

    private class InnerOperationDefinition {
        var name: String? = null
        val variableDefinitions: MutableSet<InnerVariableDefinition> = LinkedHashSet()
        var selections: MutableSet<InnerSelection>? = null

        override fun toString(): String {
            val stringJoiner = StringJoiner("\n")
            if (name != null) {
                stringJoiner.add(name)
            }
            if (!variableDefinitions.isEmpty()) {
                val sj = StringJoiner("\n")
                for (variableDefinition in variableDefinitions) {
                    sj.add(variableDefinition.toString())
                }
                stringJoiner.add("($sj)")
            }
            if (selections != null && !selections!!.isEmpty()) {
                val sj = StringJoiner("\n")
                for (selection in selections!!) {
                    sj.add(selection.toString())
                }
                stringJoiner.add("{$sj}")
            }
            return stringJoiner.toString()
        }
    }

    private class InnerDocument {
        val operationDefinitions: MutableList<InnerOperationDefinition> = ArrayList()

        override fun toString(): String {
            if (!operationDefinitions.isEmpty()) {
                val stringJoiner = StringJoiner("\n\n")
                for (operationDefinition in operationDefinitions) {
                    stringJoiner.add(operationDefinition.toString())
                }
                return stringJoiner.toString()
            }
            return ""
        }
    }
}
