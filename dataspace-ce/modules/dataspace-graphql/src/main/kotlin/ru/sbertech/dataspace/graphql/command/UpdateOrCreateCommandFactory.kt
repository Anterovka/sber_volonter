package ru.sbertech.dataspace.graphql.command

import graphql.language.Field
import graphql.language.SelectionSet
import graphql.schema.DataFetchingEnvironment
import graphql.schema.SelectedField
import ru.sbertech.dataspace.common.uncheckedCast
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.extensions.getArgument
import ru.sbertech.dataspace.graphql.extensions.getArgumentAsMap
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.EXIST_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INPUT_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.BY_KEY_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.COMPARE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.PARAM_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.RETURNING_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_FIELD_NAME
import ru.sbertech.dataspace.graphql.selection.SelectionFactory
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.Increment
import ru.sbertech.dataspace.uow.command.Selection
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOn
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

class UpdateOrCreateCommandFactory(
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
                val updateOrCreateCommands = arrayListOf<Command>()
                val manyCommand = Command.Many(commandQualifier, entityType, updateOrCreateCommands, dependsOn)
                inputArgument.uncheckedCast<Collection<Map<String, Any?>>>().forEach {
                    updateOrCreateCommands.add(
                        getUpdateOrCreateCommand(
                            commandQualifier,
                            commandRefContext,
                            it[PARAM_FIELD_NAME]?.uncheckedCast() ?: throw IllegalArgumentException(
                                "param argument is not set for one of commands in the command '$commandQualifier'",
                            ),
                            it[EXIST_ARGUMENT_NAME]?.uncheckedCast(),
                            dependsOn,
                            manyCommand,
                        ),
                    )
                }
                manyCommand
            } else {
                val existArgument = selectedField.getArgumentAsMap(EXIST_ARGUMENT_NAME)

                val returningField =
                    field.selectionSet.getSelectionsOfType(Field::class.java).firstOrNull { it.name == RETURNING_FIELD_NAME }
                        ?: Field
                            .newField()
                            .name(RETURNING_FIELD_NAME)
                            .selectionSet(
                                SelectionSet
                                    .newSelectionSet()
                                    .selection(Field.newField().name(SchemaHelper.ID_FIELD_NAME).build())
                                    .build(),
                            ).build()

                val selection = SelectionFactory.createSelection(entityType, returningField, grammar, graphQLDataFetcherHelper, environment)

                getUpdateOrCreateCommand(
                    commandQualifier,
                    commandRefContext,
                    inputArgument.uncheckedCast(),
                    existArgument,
                    dependsOn,
                    selection = selection,
                )
            }
        commandByQualifier[commandQualifier] = command
    }

    private fun getUpdateOrCreateCommand(
        commandQualifier: String,
        commandRefContext: CommandRefContext,
        inputArgument: Map<String, Any?>,
        existArgument: Map<String, Any?>?,
        dependsOn: List<DependsOn>,
        parentCommand: Command? = null,
        selection: Selection? = null,
    ): Command.UpdateOrCreate {
        val propertyValueByNameForCreate = linkedMapOf<String, UniversalValue?>()
        var propertyValueByNameForUpdate: LinkedHashMap<String, UniversalValue?>? = null
        val propertyValueByNameForCompare = linkedMapOf<String, UniversalValue?>()
        val increments = arrayListOf<Increment>()
        var byKey: String? = null

        val propertyValuesFillingVisitor = PropertyValuesFillingVisitor(commandQualifier, commandRefContext)
        fillPropertyValuesByArgument(inputArgument, propertyValueByNameForCreate, propertyValuesFillingVisitor)

        existArgument?.also {
            existArgument.forEach {
                when (it.key) {
                    UPDATE_FIELD_NAME -> {
                        propertyValueByNameForUpdate = linkedMapOf()
                        fillPropertyValuesByArgument(
                            it.value as Map<String, Any?>,
                            propertyValueByNameForUpdate!!,
                            propertyValuesFillingVisitor,
                        )
                    }

                    COMPARE_FIELD_NAME -> {
                        fillPropertyValuesByArgument(
                            it.value as Map<String, Any?>,
                            propertyValueByNameForCompare,
                            propertyValuesFillingVisitor,
                        )
                    }

                    INC_FIELD_NAME -> {
                        fillIncrements(it.value as Map<String, Any?>, increments)
                    }

                    BY_KEY_FIELD_NAME -> {
                        byKey = it.value as String
                    }
                }
            }
        }

        var updateOrCreateCommand: Command? = null
        val createCommand =
            Command.Create(
                commandQualifier,
                entityType,
                null,
                propertyValueByNameForCreate,
                emptyList(),
                lazy(LazyThreadSafetyMode.NONE) { updateOrCreateCommand },
            )

        val updateCommand: Command.Update =
            Command.Update(
                commandQualifier,
                entityType,
                null,
                propertyValueByNameForUpdate ?: propertyValueByNameForCreate,
                propertyValueByNameForCompare,
                increments,
                emptyList(),
                lazy(LazyThreadSafetyMode.NONE) { updateOrCreateCommand },
            )

        updateOrCreateCommand =
            Command.UpdateOrCreate(
                commandQualifier,
                entityType,
                createCommand,
                updateCommand,
                selection,
                dependsOn,
                byKey,
                parentCommand,
            )
        return updateOrCreateCommand
    }
}
