package ru.sbertech.dataspace.graphql.command

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import graphql.schema.SelectedField
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.extensions.getArgument
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INPUT_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.selection.SelectionFactory
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.Selection
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOn
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

class CreateCommandFactory(
    entityType: EntityType,
    private val grammar: Grammar<Expr>,
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
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

        val inputArgument =
            selectedField.getArgument(INPUT_ARGUMENT_NAME)
                ?: throw IllegalArgumentException("input argument is not set for command '$commandQualifier'")

        val command =
            if (inputArgument is Collection<*>) {
                val createCommands = arrayListOf<Command>()
                val manyCommand = Command.Many(commandQualifier, entityType, createCommands, dependsOn)
                inputArgument
                    .forEach {
                        createCommands.add(
                            getCreateCommand(
                                commandQualifier,
                                commandRefContext,
                                it?.uncheckedCast() ?: throw IllegalArgumentException(
                                    "input argument is not set for one of commands in the command '$commandQualifier'",
                                ),
                                dependsOn,
                                manyCommand,
                            ),
                        )
                    }
                manyCommand
            } else {
                val selection = SelectionFactory.createSelection(entityType, field, grammar, graphQLDataFetcherHelper, environment)

                getCreateCommand(commandQualifier, commandRefContext, inputArgument.uncheckedCast(), dependsOn, selection = selection)
            }

        commandByQualifier[commandQualifier] = command
    }

    private fun getCreateCommand(
        commandQualifier: String,
        commandRefContext: CommandRefContext,
        inputArgument: Map<String, Any?>,
        dependsOn: List<DependsOn>,
        parentCommand: Command? = null,
        selection: Selection? = null,
    ): Command.Create {
        val propertyValueByName = linkedMapOf<String, UniversalValue?>()
        val propertyValuesFillingVisitor = PropertyValuesFillingVisitor(commandQualifier, commandRefContext)

        fillPropertyValuesByArgument(inputArgument, propertyValueByName, propertyValuesFillingVisitor)

        val createCommand =
            Command.Create(
                commandQualifier,
                entityType,
                selection,
                propertyValueByName,
                dependsOn,
                lazy(LazyThreadSafetyMode.NONE) { parentCommand },
            )
        return createCommand
    }
}
