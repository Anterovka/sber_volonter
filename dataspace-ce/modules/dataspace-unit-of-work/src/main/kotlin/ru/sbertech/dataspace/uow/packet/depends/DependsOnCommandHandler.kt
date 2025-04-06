package ru.sbertech.dataspace.uow.packet.depends

import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.CommandExecutionResult

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
object DependsOnCommandHandler {
    fun isNeedExecuteCommand(
        command: Command,
        executionResultByQualifier: Map<String, CommandExecutionResult>,
    ): Boolean {
        return command.dependsOn.stream().allMatch { dependsOn ->
            val commandExecutionResult =
                executionResultByQualifier[dependsOn.commandId]
                    ?: throw IllegalStateException("Execution result for command '${dependsOn.commandId}' is not found")

            return@allMatch dependsOn.needHandle(commandExecutionResult)
        }
    }
}
