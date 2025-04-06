package ru.sbertech.dataspace.graphql.command

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import graphql.schema.SelectedField
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.graphql.extensions.getArgument
import ru.sbertech.dataspace.graphql.extensions.getArgumentAsMap
import ru.sbertech.dataspace.graphql.extensions.getArgumentAsString
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.COMPARE_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.ID_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INPUT_ARGUMENT_NAME
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOn

class DeleteCommandFactory(
    entityType: EntityType,
) : CommandFactory(entityType) {
    override fun addCommand(
        field: Field,
        selectedField: SelectedField,
        commandByQualifier: LinkedHashMap<String, Command>,
        commandRefContext: CommandRefContext,
        environment: DataFetchingEnvironment,
        dependsOn: List<DependsOn>,
    ) {
        val commandQualifier = field.alias ?: field.name

        val inputArgument = selectedField.getArgument(INPUT_ARGUMENT_NAME)

        val command =
            if (inputArgument == null) {
                val identifier =
                    selectedField.getArgumentAsString(ID_ARGUMENT_NAME)
                        ?: throw IllegalArgumentException("id argument is not set for command '$commandQualifier'")

                val compareArgument = selectedField.getArgumentAsMap(COMPARE_ARGUMENT_NAME)
                getDeleteCommand(commandQualifier, commandRefContext, compareArgument, identifier, dependsOn)
            } else {
                val deleteCommands = arrayListOf<Command>()
                val manyCommand = Command.Many(commandQualifier, entityType, deleteCommands, dependsOn)
                inputArgument.uncheckedCast<Collection<Map<String, Any?>>>().forEach {
                    deleteCommands.add(
                        getDeleteCommand(
                            commandQualifier,
                            commandRefContext,
                            it[COMPARE_ARGUMENT_NAME]?.uncheckedCast(),
                            it[ID_ARGUMENT_NAME]?.uncheckedCast()
                                ?: throw IllegalArgumentException(
                                    "id argument is not set for one of commands in the command '$commandQualifier'",
                                ),
                            dependsOn,
                            manyCommand,
                        ),
                    )
                }
                manyCommand
            }
        commandByQualifier[commandQualifier] = command
    }

    private fun getDeleteCommand(
        commandQualifier: String,
        commandRefContext: CommandRefContext,
        compareArgument: Map<String, Any?>?,
        identifier: String,
        dependsOn: List<DependsOn>,
        parentCommand: Command? = null,
    ): Command.Delete {
        if (identifier.isEmpty()) {
            throw IllegalArgumentException("id argument is empty for command '$commandQualifier'")
        }

        val propertyValueByNameForCompare = linkedMapOf<String, UniversalValue?>()

        val propertyValuesFillingVisitor = PropertyValuesFillingVisitor(commandQualifier, commandRefContext)

        compareArgument?.also {
            fillPropertyValuesByArgument(
                compareArgument,
                propertyValueByNameForCompare,
                propertyValuesFillingVisitor,
            )
        }

        val deleteCommand =
            Command.Delete(
                commandQualifier,
                entityType,
                identifier,
                propertyValueByNameForCompare,
                dependsOn,
                parentCommand,
            )
        return deleteCommand
    }
}
