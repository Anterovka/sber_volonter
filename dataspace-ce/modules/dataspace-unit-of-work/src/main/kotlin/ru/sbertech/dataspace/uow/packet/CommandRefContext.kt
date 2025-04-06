package ru.sbertech.dataspace.uow.packet

import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.CommandExecutionResult

const val REFERENCE = "ref:"

interface CommandRefContext {
    fun registerReference(commandQualifier: String)

    fun fillRefs(
        commandQualifier: String,
        propertyValueByName: HashMap<String, UniversalValue?>,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    )

    fun fillSingleRef(
        refExpression: String,
        commandResultByQualifier: HashMap<String, CommandExecutionResult>,
    ): UniversalValue?
}
