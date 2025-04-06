package ru.sbertech.dataspace.graphql.command

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import graphql.schema.SelectedField
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.extensions.getArgument
import ru.sbertech.dataspace.graphql.extensions.getArgumentAsMap
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.COMPARE_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INC_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INPUT_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.PARAM_FIELD_NAME
import ru.sbertech.dataspace.graphql.selection.SelectionFactory
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.Increment
import ru.sbertech.dataspace.uow.command.Selection
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOn
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

class UpdateCommandFactory(
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
                val updateCommands = arrayListOf<Command>()
                val manyCommand = Command.Many(commandQualifier, entityType, updateCommands, dependsOn)
                inputArgument.uncheckedCast<Collection<Map<String, Any?>>>().forEach {
                    updateCommands.add(
                        getUpdateCommand(
                            commandQualifier,
                            commandRefContext,
                            it[PARAM_FIELD_NAME]?.uncheckedCast()
                                ?: throw IllegalArgumentException(
                                    "param argument is not set for one of commands in the command '$commandQualifier'",
                                ),
                            it[COMPARE_ARGUMENT_NAME]?.uncheckedCast(),
                            it[INC_ARGUMENT_NAME]?.uncheckedCast(),
                            dependsOn,
                            manyCommand,
                        ),
                    )
                }
                manyCommand
            } else {
                val compareArgument = selectedField.getArgumentAsMap(COMPARE_ARGUMENT_NAME)
                val incArgument = selectedField.getArgumentAsMap(INC_ARGUMENT_NAME)

                val selection = SelectionFactory.createSelection(entityType, field, grammar, graphQLDataFetcherHelper, environment)

                getUpdateCommand(
                    commandQualifier,
                    commandRefContext,
                    inputArgument.uncheckedCast(),
                    compareArgument,
                    incArgument,
                    dependsOn,
                    selection = selection,
                )
            }

        commandByQualifier[commandQualifier] = command
    }

    private fun getUpdateCommand(
        commandQualifier: String,
        commandRefContext: CommandRefContext,
        inputArgument: Map<String, Any?>,
        compareArgument: Map<String, Any?>?,
        incArgument: Map<String, Any?>?,
        dependsOn: List<DependsOn>,
        parentCommand: Command? = null,
        selection: Selection? = null,
    ): Command.Update {
        val propertyValueByName = linkedMapOf<String, UniversalValue?>()
        val propertyValueByNameForCompare = linkedMapOf<String, UniversalValue?>()
        val increments = arrayListOf<Increment>()

        val propertyValuesFillingVisitor = PropertyValuesFillingVisitor(commandQualifier, commandRefContext)
        fillPropertyValuesByArgument(inputArgument, propertyValueByName, propertyValuesFillingVisitor)

        compareArgument?.also {
            fillPropertyValuesByArgument(
                compareArgument,
                propertyValueByNameForCompare,
                propertyValuesFillingVisitor,
            )
        }

        incArgument?.also { fillIncrements(incArgument, increments) }

        val updateCommand =
            Command.Update(
                commandQualifier,
                entityType,
                selection,
                propertyValueByName,
                propertyValueByNameForCompare,
                increments,
                dependsOn,
                lazy(LazyThreadSafetyMode.NONE) { parentCommand },
            )
        return updateCommand
    }
}
