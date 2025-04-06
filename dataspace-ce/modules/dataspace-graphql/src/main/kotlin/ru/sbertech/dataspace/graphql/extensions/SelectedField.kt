package ru.sbertech.dataspace.graphql.extensions

import graphql.schema.SelectedField
import ru.sbertech.dataspace.common.uncheckedCast

fun SelectedField.getArgument(argumentName: String): Any? = this.arguments[argumentName]

fun SelectedField.getArgumentAsMap(argumentName: String): Map<String, Any?>? = this.arguments[argumentName]?.uncheckedCast()

fun SelectedField.getArgumentAsString(argumentName: String): String? = this.arguments[argumentName] as String?
