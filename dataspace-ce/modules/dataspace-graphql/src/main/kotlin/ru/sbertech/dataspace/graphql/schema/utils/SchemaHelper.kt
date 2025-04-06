package ru.sbertech.dataspace.graphql.schema.utils

import graphql.Scalars
import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLID
import graphql.Scalars.GraphQLString
import graphql.TypeResolutionEnvironment
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumType.newEnum
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectField.newInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputObjectType.newInputObject
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLType
import graphql.schema.TypeResolver
import ru.sbertech.dataspace.uow.command.LockMode

object SchemaHelper {
    const val TYPE = "_type"
    const val TYPE_NAME_FIELD_NAME = "__typename"

    const val SORT_ORDER_ENUM_TYPE_NAME = "_SortOrder"
    const val SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE_NAME = "_SortCriterionSpecification"

    const val ENTITY_INTERFACE_TYPE_NAME = "_Entity"

    const val QUERY_OBJECT_TYPE_NAME = "_Query"
    const val MUTATION_OBJECT_TYPE_NAME = "_Mutation"
    const val PACKET_OBJECT_TYPE_NAME = "_Packet"
    const val DICTIONARY_PACKET_TYPE_NAME = "_DictionaryPacket"
    const val CALCULATION_OBJECT_TYPE_NAME = "_Calculation"

    const val CRITERION_INPUT_OBJECT_FIELD_NAME = "crit"
    const val ORDER_INPUT_OBJECT_FIELD_NAME = "order"
    const val NULLS_LAST_OBJECT_FIELD_NAME = "nullsLast"

    const val CREATE_INPUT_TYPE_PREFIX = "_Create"
    const val UPDATE_INPUT_TYPE_PREFIX = "_Update"
    const val UPDATE_MANY_INPUT_TYPE_PREFIX = "_UpdateMany"
    const val DELETE_MANY_INPUT_TYPE_PREFIX = "_DeleteMany"
    const val UPDATE_OR_CREATE_MANY_INPUT_TYPE_PREFIX = "_UpdateOrCreateMany"
    const val EXIST_INPUT_TYPE_PREFIX = "_Exist"
    const val EXIST_UPDATE_INPUT_TYPE_PREFIX = "_ExistUpdate"
    const val COMPARE_INPUT_TYPE_PREFIX = "_Compare"
    const val INC_INPUT_TYPE_PREFIX = "_Inc"
    const val INPUT_TYPE_POSTFIX = "Input"
    const val SINGLE_REFERENCE_INPUT_TYPE_NAME = "_SingleReferenceInput"
    const val SINGLE_REFERENCE_SET_INPUT_TYPE_NAME = "_SingleReferenceSetInput"
    const val DOUBLE_REFERENCE_INPUT_TYPE_NAME = "_DoubleReferenceInput"
    const val DOUBLE_REFERENCE_SET_INPUT_TYPE_NAME = "_DoubleReferenceSetInput"

    const val INC_VALUE_FIELD_NAME = "value"
    const val INC_NEGATIVE_FIELD_NAME = "negative"
    const val INC_FAIL_FIELD_NAME = "fail"
    const val INC_OPERATION_FIELD_NAME = "operation"

    const val UPDATE_FIELD_NAME = "update"
    const val COMPARE_FIELD_NAME = "compare"
    const val INC_FIELD_NAME = "inc"
    const val PARAM_FIELD_NAME = "param"
    const val BY_KEY_FIELD_NAME = "byKey"

    const val CREATED_FIELD_NAME = "created"
    const val RETURNING_FIELD_NAME = "returning"

    const val ID_FIELD_NAME = "id"
    const val AGGREGATE_VERSION_FIELD_NAME = "aggregateVersion"
    const val ELEMENTS_FIELD_NAME = "elems"
    const val COUNT_FIELD_NAME = "count"

    const val ENTITY_ID_FIELD_NAME = "entityId"
    const val ROOT_ENTITY_ID_FIELD_NAME = "rootEntityId"
    const val CLEAR_FIELD_NAME = "clear"
    const val ADD_FIELD_NAME = "add"
    const val REMOVE_FIELD_NAME = "remove"

    const val IS_IDEMPOTENCE_RESPONSE_FIELD_NAME = "isIdempotenceResponse"
    const val PACKET_FIELD_NAME = "packet"
    const val DICTIONARY_PACKET_FIELD_NAME = "dictionaryPacket"

    const val ASCENDING_ENUM_VALUE = "ASC"
    const val DESCENDING_ENUM_VALUE = "DESC"

    const val ENTITY_OBJECT_TYPE_PREFIX = "_E_"
    const val ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX = "_EC_"
    const val GROUP_OBJECT_TYPE_PREFIX = "_G_"
    const val UPDATE_OR_CREATE_OBJECT_TYPE_PREFIX = "_UpdateOrCreate"
    const val UPDATE_OR_CREATE_MANY_OBJECT_TYPE_PREFIX = "_UpdateOrCreateMany"
    const val RESPONSE_POSTFIX = "Response"
    const val ENUM_OBJECT_TYPE_PREFIX = "_EN_"
    const val ENUM_COLLECTION_OBJECT_TYPE_PREFIX = "_ENC_"
    const val SQL_QUERY_OBJECT_TYPE_PREFIX = "_Q_"
    const val SQL_QUERY_PARAMS_TYPE_PREFIX = "_QP_"
    const val SQL_QUERY_PARAMS_TYPE_SUFFIX = "Params"

    const val SEARCH_FIELD_NAME_PREFIX = "search"
    const val SQL_QUERY_FIELD_NAME_PREFIX = "search"
    const val CREATE_FIELD_NAME_PREFIX = "create"
    const val UPDATE_FIELD_NAME_PREFIX = "update"
    const val UPDATE_OR_CREATE_FIELD_NAME_PREFIX = "updateOrCreate"
    const val GET_FIELD_NAME_PREFIX = "get"
    const val DELETE_FIELD_NAME_PREFIX = "delete"
    const val CREATE_MANY_FIELD_NAME_PREFIX = "createMany"
    const val UPDATE_MANY_FIELD_NAME_PREFIX = "updateMany"
    const val UPDATE_OR_CREATE_MANY_FIELD_NAME_PREFIX = "updateOrCreateMany"
    const val DELETE_MANY_FIELD_NAME_PREFIX = "deleteMany"

    const val CALC_FIELD_NAME = "_calc"

    val sortOrderEnumType: GraphQLEnumType =
        newEnum()
            .name(SORT_ORDER_ENUM_TYPE_NAME)
            .value(ASCENDING_ENUM_VALUE)
            .value(DESCENDING_ENUM_VALUE)
            .build()

    // TODO
    val incFailOperatorEnumType: GraphQLEnumType =
        newEnum()
            .name("_IncFailOperator")
            .value("lt")
            .value("le")
            .value("gt")
            .value("ge")
            .build()

    val lockModeEnumType: GraphQLEnumType =
        newEnum()
            .name("_GetLockMode")
            .apply { LockMode.entries.forEach { value(it.name) } }
            .build()

    val sortCriterionSpecificationInputObjectType: GraphQLInputObjectType =
        newInputObject()
            .name(SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE_NAME)
            .field(
                newInputObjectField()
                    .name(CRITERION_INPUT_OBJECT_FIELD_NAME)
                    .type(nonNull(GraphQLString)),
            ).field(
                newInputObjectField()
                    .name(ORDER_INPUT_OBJECT_FIELD_NAME)
                    .type(nonNull(sortOrderEnumType))
                    .defaultValueProgrammatic(ASCENDING_ENUM_VALUE),
            ).field(
                newInputObjectField()
                    .name(NULLS_LAST_OBJECT_FIELD_NAME)
                    .type(GraphQLBoolean),
            ).build()

    val isIdempotenceResponseFieldDefinition: GraphQLFieldDefinition =
        newFieldDefinition()
            .name(IS_IDEMPOTENCE_RESPONSE_FIELD_NAME)
            .type(GraphQLBoolean)
            .build()

    val aggregateVersionFieldDefinition: GraphQLFieldDefinition =
        newFieldDefinition()
            .name(AGGREGATE_VERSION_FIELD_NAME)
            .type(ExtendedScalars.GraphQLLong)
            .build()

    val countFieldDefinition: GraphQLFieldDefinition =
        newFieldDefinition()
            .name(COUNT_FIELD_NAME)
            .type(nonNull(Scalars.GraphQLInt))
            .build()

    val createdFieldDefinition: GraphQLFieldDefinition =
        newFieldDefinition()
            .name(CREATED_FIELD_NAME)
            .type(GraphQLBoolean)
            .build()

    val mandatoryIdFieldDefinition: GraphQLFieldDefinition =
        newFieldDefinition()
            .name(ID_FIELD_NAME)
            .type(nonNull(GraphQLID))
            .build()

    val idInputField: GraphQLInputObjectField =
        newInputObjectField()
            .name(ID_FIELD_NAME)
            .type(GraphQLID)
            .build()

    val updateOrCreateManyResponseType: GraphQLObjectType =
        newObject()
            .name("$UPDATE_OR_CREATE_MANY_OBJECT_TYPE_PREFIX$RESPONSE_POSTFIX")
            .field(mandatoryIdFieldDefinition)
            .field(createdFieldDefinition)
            .build()

    val mandatoryIdInputField: GraphQLInputObjectField =
        newInputObjectField()
            .name(ID_FIELD_NAME)
            .type(nonNull(GraphQLID))
            .build()

    val singleReferenceInputType: GraphQLInputObjectType =
        newInputObject()
            .name(SINGLE_REFERENCE_INPUT_TYPE_NAME)
            .field(
                newInputObjectField()
                    .name(ENTITY_ID_FIELD_NAME)
                    .type(nonNull(GraphQLString)),
            ).build()

    val singleReferenceSetInputType: GraphQLInputObjectType =
        newInputObject()
            .name(SINGLE_REFERENCE_SET_INPUT_TYPE_NAME)
            .field(
                newInputObjectField()
                    .name(CLEAR_FIELD_NAME)
                    .type(nonNull(GraphQLBoolean))
                    .defaultValueProgrammatic(false),
            ).field(
                newInputObjectField()
                    .name(ADD_FIELD_NAME)
                    .type(nonNull(list(nonNull(singleReferenceInputType))))
                    .defaultValueProgrammatic(emptySet<Any>()),
            ).field(
                newInputObjectField()
                    .name(REMOVE_FIELD_NAME)
                    .type(nonNull(list(nonNull(singleReferenceInputType))))
                    .defaultValueProgrammatic(emptySet<Any>()),
            ).build()

    val doubleReferenceInputType: GraphQLInputObjectType =
        newInputObject()
            .name(DOUBLE_REFERENCE_INPUT_TYPE_NAME)
            .field(
                newInputObjectField()
                    .name(ENTITY_ID_FIELD_NAME)
                    .type(nonNull(GraphQLString)),
            ).field(
                newInputObjectField()
                    .name(ROOT_ENTITY_ID_FIELD_NAME)
                    .type(GraphQLString),
            ).build()

    val doubleReferenceSetInputType: GraphQLInputObjectType =
        newInputObject()
            .name(DOUBLE_REFERENCE_SET_INPUT_TYPE_NAME)
            .field(
                newInputObjectField()
                    .name(CLEAR_FIELD_NAME)
                    .type(nonNull(GraphQLBoolean))
                    .defaultValueProgrammatic(false),
            ).field(
                newInputObjectField()
                    .name(ADD_FIELD_NAME)
                    .type(nonNull(list(nonNull(doubleReferenceInputType))))
                    .defaultValueProgrammatic(emptySet<Any>()),
            ).field(
                newInputObjectField()
                    .name(REMOVE_FIELD_NAME)
                    .type(nonNull(list(nonNull(doubleReferenceInputType))))
                    .defaultValueProgrammatic(emptySet<Any>()),
            ).build()

    val typeResolver =
        TypeResolver { env: TypeResolutionEnvironment ->
            env.schema.getObjectType(
                ENTITY_OBJECT_TYPE_PREFIX + (env.getObject<Any>() as Map<*, *>)[TYPE],
            )
        }

    // TODO возможно лишнее
//    val defaultAdditionalTypes =
//        hashSetOf<GraphQLType>(
//            scalarTypes.float4ScalarType,
//            scalarTypes.timeScalarType,
//            scalarTypes.dateScalarType,
//            scalarTypes.dateTimeScalarType,
//            scalarTypes.offsetDateTimeScalarType,
//            scalarTypes.byteArrayScalarType,
//            sortCriterionSpecificationInputObjectType,
//            sortOrderEnumType,
//            incFailOperatorEnumType,
//        )

    inline fun <reified T : GraphQLType> getType(
        type: GraphQLType?,
        isOptional: Boolean,
    ) = if (isOptional) type as T else nonNull(type) as T
}
