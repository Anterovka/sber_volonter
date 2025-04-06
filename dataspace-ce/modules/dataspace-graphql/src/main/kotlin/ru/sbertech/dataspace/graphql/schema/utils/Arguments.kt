package ru.sbertech.dataspace.graphql.schema.utils

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLID
import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.lockModeEnumType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.sortCriterionSpecificationInputObjectType
import ru.sbertech.dataspace.uow.command.LockMode
import ru.sbertech.dataspace.uow.packet.depends.DependsOn

object Arguments {
    const val ID_ARGUMENT_NAME = "id"

    const val CONDITION_ARGUMENT_NAME = "cond"
    const val GROUP_ARGUMENT_NAME = "group"
    const val GROUP_COND_ARGUMENT_NAME = "groupCond"

    const val LIMIT_ARGUMENT_NAME = "limit"
    const val OFFSET_ARGUMENT_NAME = "offset"
    const val SORT_ARGUMENT_NAME = "sort"

    const val ALIAS_ARGUMENT_NAME = "alias"
    const val ELEMENT_ALIAS_ARGUMENT_NAME = "elemAlias"

    const val EXPRESSION_ARGUMENT_NAME = "expression"
    const val EXPR_ARGUMENT_NAME = "expr"

    const val DISTINCT_ARGUMENT_NAME = "distinct"

    const val PARAMS_ARGUMENT_NAME = "params"

    const val IDEMPOTENCE_PACKET_ID_ARGUMENT_NAME = "idempotencePacketId"
    const val AGGREGATE_VERSION_ARGUMENT_NAME = "aggregateVersion"
    const val FAIL_ON_EMPTY_ARGUMENT_NAME = "failOnEmpty"
    const val LOCK_ARGUMENT_NAME = "lock"
    const val INPUT_ARGUMENT_NAME = "input"
    const val EXIST_ARGUMENT_NAME = "exist"
    const val COMPARE_ARGUMENT_NAME = "compare"
    const val INC_ARGUMENT_NAME = "inc"
    const val COMMAND_ID_ARGUMENT_NAME = "commandId"
    const val DEPENDENCY_ARGUMENT_NAME = "dependency"
    const val DEPENDENCY_ON_BY_CREATE_OR_UPDATE_ARGUMENT_TYPE_NAME = "_DependsOnDependencyByGet"
    const val DEPENDENCY_ON_BY_GET_ARGUMENT_TYPE_NAME = "_DependsOnDependencyByUpdateOrCreate"

    val idArgument: GraphQLArgument =
        newArgument()
            .name(ID_ARGUMENT_NAME)
            .type(nonNull(GraphQLID))
            .build()

    val conditionArgument: GraphQLArgument =
        newArgument()
            .name(CONDITION_ARGUMENT_NAME)
            .type(GraphQLString)
            .build()

    val groupArgument: GraphQLArgument =
        newArgument()
            .name(GROUP_ARGUMENT_NAME)
            .type(list(nonNull(GraphQLString)))
            .build()

    val groupCondArgument: GraphQLArgument =
        newArgument()
            .name(GROUP_COND_ARGUMENT_NAME)
            .type(GraphQLString)
            .build()

    val limitArgument: GraphQLArgument =
        newArgument()
            .name(LIMIT_ARGUMENT_NAME)
            .type(GraphQLInt)
            .build()

    val offsetArgument: GraphQLArgument =
        newArgument()
            .name(OFFSET_ARGUMENT_NAME)
            .type(GraphQLInt)
            .build()

    val sortArgument: GraphQLArgument =
        newArgument()
            .name(SORT_ARGUMENT_NAME)
            .type(list(nonNull(sortCriterionSpecificationInputObjectType)))
            .build()

    val defaultSearchSpecificationArguments = arrayListOf(conditionArgument, limitArgument, offsetArgument, sortArgument)

    val failOnEmptyArgument: GraphQLArgument =
        newArgument()
            .name(FAIL_ON_EMPTY_ARGUMENT_NAME)
            .type(GraphQLBoolean)
            .defaultValueProgrammatic(null)
            .build()

    val lockArgument: GraphQLArgument =
        newArgument()
            .name(LOCK_ARGUMENT_NAME)
            .type(nonNull(lockModeEnumType))
            .defaultValueProgrammatic(LockMode.NOT_USE.name)
            .build()

    val exprArgument: GraphQLArgument =
        newArgument()
            .name(EXPR_ARGUMENT_NAME)
            .type(nonNull(GraphQLString))
            .build()

    val idempotencePacketIdArgument: GraphQLArgument =
        newArgument()
            .name(IDEMPOTENCE_PACKET_ID_ARGUMENT_NAME)
            .type(GraphQLString)
            .build()

    val aggregateVersionArgument: GraphQLArgument =
        newArgument()
            .name(AGGREGATE_VERSION_ARGUMENT_NAME)
            .type(ExtendedScalars.GraphQLLong)
            .build()

    val commandIdArgument: GraphQLArgument =
        newArgument()
            .name(COMMAND_ID_ARGUMENT_NAME)
            .type(nonNull(GraphQLString))
            .build()

    val dependencyOnByGetArgument: GraphQLArgument =
        newArgument()
            .name(DEPENDENCY_ARGUMENT_NAME)
            .type(
                nonNull(
                    GraphQLEnumType
                        .newEnum()
                        .name(DEPENDENCY_ON_BY_CREATE_OR_UPDATE_ARGUMENT_TYPE_NAME)
                        .value(DependsOn.Dependency.EXISTS.name)
                        .value(DependsOn.Dependency.NOT_EXISTS.name)
                        .build(),
                ),
            ).build()

    val dependencyOnByUpdateOrCreateArgument: GraphQLArgument =
        newArgument()
            .name(DEPENDENCY_ARGUMENT_NAME)
            .type(
                nonNull(
                    GraphQLEnumType
                        .newEnum()
                        .name(DEPENDENCY_ON_BY_GET_ARGUMENT_TYPE_NAME)
                        .value(DependsOn.Dependency.CREATED.name)
                        .value(DependsOn.Dependency.NOT_CREATED.name)
                        .build(),
                ),
            ).build()
}
