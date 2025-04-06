package ru.sbertech.dataspace.graphql.command

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import graphql.schema.SelectedField
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.extensions.getArgumentAsString
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.FAIL_ON_EMPTY_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.ID_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.LOCK_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.selection.SelectionFactory
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.universalvalue.UniversalValue
import ru.sbertech.dataspace.uow.command.Command
import ru.sbertech.dataspace.uow.command.LockMode
import ru.sbertech.dataspace.uow.packet.CommandRefContext
import ru.sbertech.dataspace.uow.packet.depends.DependsOn
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

private const val FIND_EXPRESSION = "find:"

class GetCommandFactory(
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

        val idArgumentValue =
            selectedField.getArgumentAsString(ID_ARGUMENT_NAME)
                ?: throw IllegalArgumentException("id argument is not set for command '$commandQualifier'")

        if (idArgumentValue.isEmpty()) {
            throw IllegalArgumentException("id argument is empty for command '$commandQualifier'")
        }

        val failOnEmpty = selectedField.arguments[FAIL_ON_EMPTY_ARGUMENT_NAME] as Boolean?

        val lock = selectedField.arguments[LOCK_ARGUMENT_NAME] as String

        val selection = SelectionFactory.createSelection(entityType, field, grammar, graphQLDataFetcherHelper, environment)

        // TODO LEGACY
//        var condition: Expr? = null
        var condition: String? = null
        var identifier: UniversalValue? = null
        if (idArgumentValue.contains(FIND_EXPRESSION)) {
//            condition = grammar.parse(idArgumentValue.replace(FIND_EXPRESSION, ""))
            condition = idArgumentValue.replace(FIND_EXPRESSION, "")
        } else {
            identifier = idArgumentValue
        }

        commandByQualifier[commandQualifier] =
            Command.Get(
                commandQualifier,
                entityType,
                identifier,
                condition,
                selection,
                failOnEmpty ?: (identifier != null),
                LockMode.valueOf(lock),
                emptyList(),
            )
    }
}
