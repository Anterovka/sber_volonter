package ru.sbertech.dataspace.graphql.selection

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import graphql.language.Field
import graphql.language.StringValue
import graphql.schema.DataFetchingEnvironment
import ru.sbertech.dataspace.entitymanager.selector.Selector
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.CONDITION_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.EXPR_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.TYPE
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.uow.command.Selection
import sbp.com.sbt.dataspace.graphqlschema.DataFetcherContainer
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper
import sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper

object SelectionFactory {
    private fun handleCalculation(
        field: Field,
        grammar: Grammar<Expr>,
    ): HashMap<String, Selector> {
        val valueSelectorByName = linkedMapOf<String, Selector>()
        field.selectionSet
            .getSelectionsOfType(Field::class.java)
            .forEach { calcField ->
                val exprArgument =
                    calcField.arguments.firstOrNull { it.name == EXPR_ARGUMENT_NAME }
                        ?: throw IllegalArgumentException(
                            "expr argument is not set for calculation field ${calcField.alias ?: calcField.name}",
                        )

                valueSelectorByName[calcField.alias ?: calcField.name] =
                    Selector.Expr(grammar.parse((exprArgument.value as StringValue).value))
            }
        return valueSelectorByName
    }

    fun createSelection(
        entityType: EntityType,
        field: Field,
        grammar: Grammar<Expr>,
        graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
        environment: DataFetchingEnvironment,
    ): Selection {
        // TODO LEGACY
//        val selectorFillingVisitor = SelectorFillingVisitor(grammar)
//
        val valueSelectorByName = linkedMapOf<String, Selector>()
//        field.selectionSet
//            .getSelectionsOfType(Field::class.java)
//            .forEach {
//                when (it.name) {
//                    ID_FIELD_NAME -> {
//                        valueSelectorByName[it.aliasOrName] = Selector.PropertyBased(entityType.tableIdProperty.name)
//                    }
//
//                    CALC_FIELD_NAME -> {
//                        valueSelectorByName[it.aliasOrName] = Selector.Group(handleCalculation(it, grammar))
//                    }
//
//                    TYPE_NAME_FIELD_NAME -> {
//                        // do nothing
//                    }
//
//                    else -> {
//                        val property = entityType.inheritedPersistableProperty(it.name)
//                        valueSelectorByName[it.aliasOrName] = property.accept(selectorFillingVisitor, it)
//                    }
//                }
//            }
//
//        valueSelectorByName[TYPE] = Selector.ApproximateType

        // TODO LEGACY
        val queryNode = JsonNodeFactory.instance.objectNode()
        queryNode.put(GraphQLSchemaHelper.SPECIAL_FLAG_FIELD_NAME, true)

        graphQLDataFetcherHelper.processProperties(
            graphQLDataFetcherHelper.modelDescription.getEntityDescription(entityType.name),
            queryNode,
            graphQLDataFetcherHelper.getPropertiesNode(queryNode),
            DataFetcherContainer(
                environment,
                null,
                null,
            ),
            field.selectionSet,
        )
        graphQLDataFetcherHelper.postProcessNode(queryNode)

        valueSelectorByName[TYPE] = Selector.ApproximateType
        return Selection(entityType.name, valueSelectorByName, queryNode)
    }
}

fun Field.getConditionExpr(grammar: Grammar<Expr>) =
    this.arguments
        .firstOrNull { it.name == CONDITION_ARGUMENT_NAME }
        ?.let {
            val value = (it.value as StringValue).value

            if (value.isNullOrEmpty()) {
                null
            } else {
                grammar.parse(value)
            }
        }

fun Field.getCondition() =
    this.arguments
        .firstOrNull { it.name == CONDITION_ARGUMENT_NAME }
        ?.let {
            (it.value as StringValue).value
        }

val Field.aliasOrName: String
    get() = this.alias ?: this.name
