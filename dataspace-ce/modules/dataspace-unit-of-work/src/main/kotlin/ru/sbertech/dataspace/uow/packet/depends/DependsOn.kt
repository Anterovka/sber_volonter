package ru.sbertech.dataspace.uow.packet.depends

import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandExecutionResult

sealed class DependsOn {
    enum class Dependency {
        EXISTS,
        NOT_EXISTS,
        CREATED,
        NOT_CREATED,
    }

    abstract val commandId: String

    abstract fun needHandle(commandExecutionResult: CommandExecutionResult): Boolean

    data class Get(
        override val commandId: String,
        val dependency: Dependency,
    ) : DependsOn() {
        override fun needHandle(commandExecutionResult: CommandExecutionResult): Boolean {
            if (commandExecutionResult.command !is Command.Get) {
                throw IllegalStateException("DependsOnGet error: command with qualifier '$commandId' should be a get-command")
            }

            return when (dependency) {
                Dependency.EXISTS -> commandExecutionResult.selectionResult != null
                Dependency.NOT_EXISTS -> commandExecutionResult.selectionResult == null
                else -> throw UnsupportedOperationException("Dependency type $dependency is not supported for DependsOnGet")
            }
        }
    }

    data class UpdateOrCreate(
        override val commandId: String,
        val dependency: Dependency,
    ) : DependsOn() {
        override fun needHandle(commandExecutionResult: CommandExecutionResult): Boolean {
            if (commandExecutionResult.command !is Command.UpdateOrCreate) {
                throw IllegalStateException(
                    "DependsOnUpdateOrCreate error: command with qualifier '$commandId' should be a updateOrCreate-command",
                )
            }

            @Suppress("UNCHECKED_CAST")
            return when (dependency) {
                Dependency.CREATED ->
                    (commandExecutionResult.selectionResult as Map<String, UniversalValue>).getValue("created")
                        as Boolean

                Dependency.NOT_CREATED ->
                    !(
                        (commandExecutionResult.selectionResult as Map<String, UniversalValue>).getValue(
                            "created",
                        ) as Boolean
                    )

                else -> throw UnsupportedOperationException("Dependency type $dependency is not supported for DependsOnUpdateOrCreate")
            }
        }
    }
}
