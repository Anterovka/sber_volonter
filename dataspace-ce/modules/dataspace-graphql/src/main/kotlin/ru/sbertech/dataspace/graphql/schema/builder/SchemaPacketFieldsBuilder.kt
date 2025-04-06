package ru.sbertech.dataspace.graphql.schema.builder

import graphql.Scalars
import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLEnumType.newEnum
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectField.newInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputObjectType.newInputObject
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLTypeReference.typeRef
import ru.sbertech.dataspace.common.onTrue
import ru.sbertech.dataspace.expr.Expr
import ru.sbertech.dataspace.grammar.Grammar
import ru.sbertech.dataspace.graphql.command.CommandFactory
import ru.sbertech.dataspace.graphql.command.CreateCommandFactory
import ru.sbertech.dataspace.graphql.command.DeleteCommandFactory
import ru.sbertech.dataspace.graphql.command.GetCommandFactory
import ru.sbertech.dataspace.graphql.command.UpdateCommandFactory
import ru.sbertech.dataspace.graphql.command.UpdateOrCreateCommandFactory
import ru.sbertech.dataspace.graphql.schema.datafetcher.FieldsByAliasDataFetcher
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.COMPARE_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.EXIST_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INC_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.INPUT_ARGUMENT_NAME
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.failOnEmptyArgument
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.idArgument
import ru.sbertech.dataspace.graphql.schema.utils.Arguments.lockArgument
import ru.sbertech.dataspace.graphql.schema.utils.ScalarTypes
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.BY_KEY_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.COMPARE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.COMPARE_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CREATE_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CREATE_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.CREATE_MANY_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.DELETE_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.DELETE_MANY_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.DELETE_MANY_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.DICTIONARY_PACKET_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ENUM_OBJECT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.EXIST_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.EXIST_UPDATE_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.GET_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ID_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INPUT_TYPE_POSTFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.PACKET_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.PARAM_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.QUERY_OBJECT_TYPE_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.RESPONSE_POSTFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.RETURNING_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_MANY_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_MANY_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_OR_CREATE_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_OR_CREATE_MANY_FIELD_NAME_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_OR_CREATE_MANY_INPUT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.UPDATE_OR_CREATE_OBJECT_TYPE_PREFIX
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.createdFieldDefinition
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.doubleReferenceInputType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.doubleReferenceSetInputType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.getType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.idInputField
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.mandatoryIdInputField
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.singleReferenceInputType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.singleReferenceSetInputType
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.updateOrCreateManyResponseType
import ru.sbertech.dataspace.model.Model
import ru.sbertech.dataspace.model.aggregates.Leaf
import ru.sbertech.dataspace.model.aggregates.aggregatesModel
import ru.sbertech.dataspace.model.aggregates.isExternalReference
import ru.sbertech.dataspace.model.aggregates.isMandatoryExternalReference
import ru.sbertech.dataspace.model.dictionaries.isDictionary
import ru.sbertech.dataspace.model.idstrategy.AutoOnEmptyIdStrategy
import ru.sbertech.dataspace.model.idstrategy.ManualIdStrategy
import ru.sbertech.dataspace.model.idstrategy.StringSnowflakeIdStrategy
import ru.sbertech.dataspace.model.idstrategy.StringUUIDIdStrategy
import ru.sbertech.dataspace.model.property.EmbeddedProperty
import ru.sbertech.dataspace.model.property.EnumCollectionProperty
import ru.sbertech.dataspace.model.property.EnumProperty
import ru.sbertech.dataspace.model.property.MappedReferenceCollectionProperty
import ru.sbertech.dataspace.model.property.MappedReferenceProperty
import ru.sbertech.dataspace.model.property.PrimitiveCollectionProperty
import ru.sbertech.dataspace.model.property.PrimitiveProperty
import ru.sbertech.dataspace.model.property.PropertyParameterizedVisitor
import ru.sbertech.dataspace.model.property.ReferenceProperty
import ru.sbertech.dataspace.model.system.indexes
import ru.sbertech.dataspace.model.type.EmbeddableType
import ru.sbertech.dataspace.model.type.EntityType
import ru.sbertech.dataspace.model.type.EnumType
import ru.sbertech.dataspace.model.type.TypeParameterizedVisitor
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import sbp.com.sbt.dataspace.graphqlschema.GraphQLDataFetcherHelper

data class InputObjectFieldsData(
    val createInputFields: MutableList<GraphQLInputObjectField>?,
    val updateInputFields: MutableList<GraphQLInputObjectField>?,
    val embeddedInputFields: MutableList<GraphQLInputObjectField>?,
    val compareInputFields: MutableList<GraphQLInputObjectField>?,
    val incInputFields: MutableList<GraphQLInputObjectField>?,
)

class SchemaPacketFieldsBuilder(
    private val model: Model,
    private val commandFactoryByFieldName: HashMap<String, CommandFactory>,
    private val packetTypeBuilder: GraphQLObjectType.Builder,
    private val dictionaryPacketTypeBuilder: GraphQLObjectType.Builder,
    private val grammar: Grammar<Expr>,
    private val scalarTypes: ScalarTypes,
    private val graphQLDataFetcherHelper: GraphQLDataFetcherHelper,
) : TypeParameterizedVisitor<SchemaBuildingData, Unit>,
    PropertyParameterizedVisitor<InputObjectFieldsData, Unit> {
    private fun addCreateField(
        entityType: EntityType,
        schemaBuildingData: SchemaBuildingData,
    ) {
        val createInputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(typeRef("$CREATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")))
                .build()

        val createFieldName = "$CREATE_FIELD_NAME_PREFIX${entityType.name}"
        packetTypeBuilder.field(
            newFieldDefinition()
                .name(createFieldName)
                .argument(createInputArgument)
                .type(typeRef(entityType.name)),
        )

        schemaBuildingData.codeRegistryBuilder.dataFetcher(coordinates(PACKET_OBJECT_TYPE_NAME, createFieldName), FieldsByAliasDataFetcher)
        schemaBuildingData.codeRegistryBuilder.dataFetcher(coordinates(QUERY_OBJECT_TYPE_NAME, entityType.name), FieldsByAliasDataFetcher)
        commandFactoryByFieldName[createFieldName] = CreateCommandFactory(entityType, grammar, graphQLDataFetcherHelper)

        addCreateManyField(entityType, schemaBuildingData)
    }

    private fun addCreateManyField(
        entityType: EntityType,
        schemaBuildingData: SchemaBuildingData,
    ) {
        val createManyInputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(list(nonNull(typeRef("$CREATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")))))
                .build()

        val createManyFieldName = "$CREATE_MANY_FIELD_NAME_PREFIX${entityType.name}"
        packetTypeBuilder.field(
            newFieldDefinition()
                .name(createManyFieldName)
                .argument(createManyInputArgument)
                .type(list(Scalars.GraphQLString)),
        )

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(PACKET_OBJECT_TYPE_NAME, createManyFieldName),
            FieldsByAliasDataFetcher,
        )
        commandFactoryByFieldName[createManyFieldName] = CreateCommandFactory(entityType, grammar, graphQLDataFetcherHelper)
    }

    private fun addGetField(
        entityType: EntityType,
        schemaBuildingData: SchemaBuildingData,
        packetTypeBuilder: GraphQLObjectType.Builder,
        packetTypeName: String,
    ) {
        val getFieldName = "$GET_FIELD_NAME_PREFIX${entityType.name}"
        packetTypeBuilder.field(
            newFieldDefinition()
                .name(getFieldName)
                .argument(idArgument)
                .argument(failOnEmptyArgument)
                .argument(lockArgument)
                .type(typeRef(entityType.name)),
        )

        schemaBuildingData.codeRegistryBuilder.dataFetcher(coordinates(packetTypeName, getFieldName), FieldsByAliasDataFetcher)
        commandFactoryByFieldName[getFieldName] = GetCommandFactory(entityType, grammar, graphQLDataFetcherHelper)
    }

    private fun addUpdateField(
        entityType: EntityType,
        inputObjectFieldsData: InputObjectFieldsData,
        schemaBuildingData: SchemaBuildingData,
    ) {
        if (inputObjectFieldsData.updateInputFields.isNullOrEmpty()) {
            return
        }

        val updateInputType =
            newInputObject()
                .name("$UPDATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .field(mandatoryIdInputField)
                .fields(inputObjectFieldsData.updateInputFields)
                .build()

        val updateInputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(updateInputType))
                .build()

        val updateFieldName = "$UPDATE_FIELD_NAME_PREFIX${entityType.name}"

        val updateFieldDefinition =
            newFieldDefinition()
                .name(updateFieldName)
                .argument(updateInputArgument)
                .type(typeRef(entityType.name))

        addCompareArgumentIfNeed(inputObjectFieldsData, entityType, updateFieldDefinition)
        addIncArgumentIfNeed(inputObjectFieldsData, entityType, updateFieldDefinition)

        packetTypeBuilder.field(updateFieldDefinition)

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(PACKET_OBJECT_TYPE_NAME, updateFieldName),
            FieldsByAliasDataFetcher,
        )
        commandFactoryByFieldName[updateFieldName] = UpdateCommandFactory(entityType, grammar, graphQLDataFetcherHelper)

        addUpdateManyField(entityType, updateInputType, inputObjectFieldsData, schemaBuildingData)
    }

    private fun addUpdateManyField(
        entityType: EntityType,
        updateInputType: GraphQLInputObjectType,
        inputObjectFieldsData: InputObjectFieldsData,
        schemaBuildingData: SchemaBuildingData,
    ) {
        val updateManyInputType =
            newInputObject()
                .name("$UPDATE_MANY_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .field(newInputObjectField().name(PARAM_FIELD_NAME).type(updateInputType))

        addIncInputObjectFieldsIfNeed(inputObjectFieldsData, entityType, updateManyInputType)
        addCompareInputObjectFieldsIfNeed(inputObjectFieldsData, entityType, updateManyInputType)

        val updateManyInputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(list(nonNull(updateManyInputType.build()))))
                .build()

        val updateManyFieldName = "$UPDATE_MANY_FIELD_NAME_PREFIX${entityType.name}"

        val updateManyFieldDefinition =
            newFieldDefinition()
                .name(updateManyFieldName)
                .argument(updateManyInputArgument)
                .type(nonNull(Scalars.GraphQLString))

        packetTypeBuilder.field(updateManyFieldDefinition)

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(PACKET_OBJECT_TYPE_NAME, updateManyFieldName),
            FieldsByAliasDataFetcher,
        )
        commandFactoryByFieldName[updateManyFieldName] = UpdateCommandFactory(entityType, grammar, graphQLDataFetcherHelper)
    }

    private fun addUpdateOrCreateField(
        entityType: EntityType,
        inputObjectFieldsData: InputObjectFieldsData,
        schemaBuildingData: SchemaBuildingData,
        packetTypeBuilder: GraphQLObjectType.Builder,
        packetTypeName: String,
    ) {
        when (entityType.rootEntityType.idStrategy) {
            is StringSnowflakeIdStrategy, StringUUIDIdStrategy -> {
                if (!entityType.indexes.any { it.isUnique }) {
                    return
                }
            }

            else -> { // do nothing
            }
        }

        if (inputObjectFieldsData.updateInputFields.isNullOrEmpty()) {
            return
        }

        val existUpdateInputType =
            newInputObject()
                .name("$EXIST_UPDATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .fields(inputObjectFieldsData.updateInputFields)
                .build()

        val existInputTypeBuilder =
            newInputObject()
                .name("$EXIST_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .field(
                    newInputObjectField()
                        .name(UPDATE_FIELD_NAME)
                        .type(existUpdateInputType)
                        .build(),
                )

        inputObjectFieldsData.compareInputFields?.takeIf { it.isNotEmpty() }?.also {
            existInputTypeBuilder.field(
                newInputObjectField()
                    .name(COMPARE_FIELD_NAME)
                    .type(typeRef("$COMPARE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))
                    .build(),
            )
        }

        inputObjectFieldsData.incInputFields?.takeIf { it.isNotEmpty() }?.also {
            existInputTypeBuilder.field(
                newInputObjectField()
                    .name(INC_FIELD_NAME)
                    .type(typeRef("$INC_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))
                    .build(),
            )
        }

        val byKeyType: GraphQLInputType? =
            entityType.indexes
                .filter { it.isUnique }
                .takeIf { it.isNotEmpty() }
                ?.let { indexes ->
                    newEnum()
                        .name("_Key${entityType.name}")
                        .values(
                            indexes.map { index ->
                                GraphQLEnumValueDefinition
                                    .newEnumValueDefinition()
                                    .name(index.name)
                                    .value(index.name)
                                    .build()
                            },
                        ).build()
                }?.let { enumType ->
                    when (entityType.rootEntityType.idStrategy) {
                        is StringSnowflakeIdStrategy, StringUUIDIdStrategy -> {
                            nonNull(enumType)
                        }

                        else -> {
                            enumType
                        }
                    }
                }

        if (byKeyType != null) {
            existInputTypeBuilder.field(
                newInputObjectField()
                    .name(BY_KEY_FIELD_NAME)
                    .type(byKeyType),
            )
        }

        val inputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(typeRef("$CREATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")))
                .build()

        val existInputType = existInputTypeBuilder.build()
        val existArgument =
            newArgument()
                .name(EXIST_ARGUMENT_NAME)
                .type(if (byKeyType is GraphQLNonNull) nonNull(existInputType) else existInputType)
                .build()

        val updateOrCreateType: GraphQLObjectType =
            newObject()
                .name("$UPDATE_OR_CREATE_OBJECT_TYPE_PREFIX${entityType.name}$RESPONSE_POSTFIX")
                .field(createdFieldDefinition)
                .field(
                    newFieldDefinition()
                        .name(RETURNING_FIELD_NAME)
                        .type(typeRef(entityType.name))
                        .build(),
                ).build()

        val updateOrCreateFieldName = "$UPDATE_OR_CREATE_FIELD_NAME_PREFIX${entityType.name}"

        val updateOrCreateFieldDefinition =
            newFieldDefinition()
                .name(updateOrCreateFieldName)
                .argument(inputArgument)
                .argument(existArgument)
                .type(updateOrCreateType)

        packetTypeBuilder.field(updateOrCreateFieldDefinition)

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(packetTypeName, updateOrCreateFieldName),
            FieldsByAliasDataFetcher,
        )

        commandFactoryByFieldName[updateOrCreateFieldName] = UpdateOrCreateCommandFactory(entityType, grammar, graphQLDataFetcherHelper)

        addUpdateOrCreateManyField(entityType, existInputType, schemaBuildingData, packetTypeBuilder, packetTypeName)
    }

    private fun addUpdateOrCreateManyField(
        entityType: EntityType,
        existInputObjectType: GraphQLInputObjectType,
        schemaBuildingData: SchemaBuildingData,
        packetTypeBuilder: GraphQLObjectType.Builder,
        packetTypeName: String,
    ) {
        val updateOrCreateManyFieldName = "$UPDATE_OR_CREATE_MANY_FIELD_NAME_PREFIX${entityType.name}"

        val updateOrCreateManyInputType =
            newInputObject()
                .name("$UPDATE_OR_CREATE_MANY_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .field(
                    newInputObjectField()
                        .name(PARAM_FIELD_NAME)
                        .type(nonNull(typeRef("$CREATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))),
                ).field(
                    newInputObjectField()
                        .name(EXIST_ARGUMENT_NAME)
                        .type(existInputObjectType),
                ).build()

        val updateOrCreateManyInputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(list(nonNull(updateOrCreateManyInputType))))
                .build()

        val updateOrCreateManyFieldDefinition =
            newFieldDefinition()
                .name(updateOrCreateManyFieldName)
                .argument(updateOrCreateManyInputArgument)
                .type(list(updateOrCreateManyResponseType))

        packetTypeBuilder.field(updateOrCreateManyFieldDefinition)

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(packetTypeName, updateOrCreateManyFieldName),
            FieldsByAliasDataFetcher,
        )

        commandFactoryByFieldName[updateOrCreateManyFieldName] = UpdateOrCreateCommandFactory(entityType, grammar, graphQLDataFetcherHelper)
    }

    private fun addDeleteField(
        entityType: EntityType,
        inputObjectFieldsData: InputObjectFieldsData,
        schemaBuildingData: SchemaBuildingData,
        packetTypeBuilder: GraphQLObjectType.Builder,
        packetTypeName: String,
    ) {
        val deleteFieldName = "$DELETE_FIELD_NAME_PREFIX${entityType.name}"

        val deleteFieldDefinition =
            newFieldDefinition()
                .name(deleteFieldName)
                .argument(idArgument)
                .type(Scalars.GraphQLString)

        addCompareArgumentIfNeed(inputObjectFieldsData, entityType, deleteFieldDefinition)

        packetTypeBuilder.field(deleteFieldDefinition)

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(packetTypeName, deleteFieldName),
            FieldsByAliasDataFetcher,
        )
        commandFactoryByFieldName[deleteFieldName] = DeleteCommandFactory(entityType)

        addDeleteManyField(entityType, inputObjectFieldsData, schemaBuildingData, packetTypeBuilder, packetTypeName)
    }

    private fun addDeleteManyField(
        entityType: EntityType,
        inputObjectFieldsData: InputObjectFieldsData,
        schemaBuildingData: SchemaBuildingData,
        packetTypeBuilder: GraphQLObjectType.Builder,
        packetTypeName: String,
    ) {
        val deleteManyFieldName = "$DELETE_MANY_FIELD_NAME_PREFIX${entityType.name}"

        val deleteManyInputType =
            newInputObject()
                .name("$DELETE_MANY_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .field(newInputObjectField().name(ID_FIELD_NAME).type(nonNull(Scalars.GraphQLString)))

        addCompareInputObjectFieldsIfNeed(inputObjectFieldsData, entityType, deleteManyInputType)

        val deleteManyInputArgument =
            newArgument()
                .name(INPUT_ARGUMENT_NAME)
                .type(nonNull(list(nonNull(deleteManyInputType.build()))))
                .build()

        val deleteManyFieldDefinition =
            newFieldDefinition()
                .name(deleteManyFieldName)
                .argument(deleteManyInputArgument)
                .type(Scalars.GraphQLString)

        packetTypeBuilder.field(deleteManyFieldDefinition)

        schemaBuildingData.codeRegistryBuilder.dataFetcher(
            coordinates(packetTypeName, deleteManyFieldName),
            FieldsByAliasDataFetcher,
        )
        commandFactoryByFieldName[deleteManyFieldName] = DeleteCommandFactory(entityType)
    }

    private fun addIncArgumentIfNeed(
        inputObjectFieldsData: InputObjectFieldsData,
        entityType: EntityType,
        packetFieldDefinition: GraphQLFieldDefinition.Builder,
    ) {
        inputObjectFieldsData.incInputFields?.takeIf { it.isNotEmpty() }?.also {
            val incInputArgument =
                newArgument()
                    .name(INC_ARGUMENT_NAME)
                    .type(typeRef("$INC_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))
                    .build()

            packetFieldDefinition.argument(incInputArgument)
        }
    }

    private fun addCompareArgumentIfNeed(
        inputObjectFieldsData: InputObjectFieldsData,
        entityType: EntityType,
        packetFieldDefinition: GraphQLFieldDefinition.Builder,
    ) {
        inputObjectFieldsData.compareInputFields?.takeIf { it.isNotEmpty() }?.also {
            val compareInputArgument =
                newArgument()
                    .name(COMPARE_ARGUMENT_NAME)
                    .type(typeRef("$COMPARE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))
                    .build()

            packetFieldDefinition.argument(compareInputArgument)
        }
    }

    private fun addIncInputObjectFieldsIfNeed(
        inputObjectFieldsData: InputObjectFieldsData,
        entityType: EntityType,
        inputType: GraphQLInputObjectType.Builder,
    ) {
        inputObjectFieldsData.incInputFields?.takeIf { it.isNotEmpty() }?.also {
            val incInputObjectField =
                newInputObjectField()
                    .name(INC_ARGUMENT_NAME)
                    .type(typeRef("$INC_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))
                    .build()

            inputType.field(incInputObjectField)
        }
    }

    private fun addCompareInputObjectFieldsIfNeed(
        inputObjectFieldsData: InputObjectFieldsData,
        entityType: EntityType,
        inputType: GraphQLInputObjectType.Builder,
    ) {
        inputObjectFieldsData.compareInputFields?.takeIf { it.isNotEmpty() }?.also {
            val compareInputObjectField =
                newInputObjectField()
                    .name(COMPARE_ARGUMENT_NAME)
                    .type(typeRef("$COMPARE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX"))
                    .build()

            inputType.field(compareInputObjectField)
        }
    }

    private fun isNeedCompareInput(primitiveProperty: PrimitiveProperty): Boolean =
        if (primitiveProperty.isSettableOnUpdate) {
            when (primitiveProperty.type) {
                is PrimitiveType.Long,
                PrimitiveType.Int,
                PrimitiveType.String,
                PrimitiveType.LocalDate,
                PrimitiveType.LocalDateTime,
                PrimitiveType.OffsetDateTime,
                PrimitiveType.LocalTime,
                -> {
                    true
                }

                else -> {
                    false
                }
            }
        } else {
            false
        }

    private fun isNeedIncInput(primitiveProperty: PrimitiveProperty): Boolean =
        if (primitiveProperty.isSettableOnUpdate) {
            scalarTypes.incTypeByScalarType.containsKey(primitiveProperty.type)
        } else {
            false
        }

    override fun visit(
        enumType: EnumType,
        param: SchemaBuildingData,
    ) {
        // do nothing
    }

    override fun visit(
        embeddableType: EmbeddableType,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") schemaBuildingData: SchemaBuildingData,
    ) {
        if (embeddableType.isExternalReference) {
            return
        }

        val inputObjectFieldsData =
            InputObjectFieldsData(
                createInputFields = null,
                updateInputFields = null,
                embeddedInputFields = arrayListOf(),
                compareInputFields = null,
                incInputFields = null,
            )

        embeddableType.properties.forEach {
            it.accept(this, inputObjectFieldsData)
        }

        val embeddableInputType =
            newInputObject()
                .name("_${embeddableType.name}$INPUT_TYPE_POSTFIX")
                .fields(inputObjectFieldsData.embeddedInputFields)
                .build()

        schemaBuildingData.additionalTypes.add(embeddableInputType)
    }

    override fun visit(
        entityType: EntityType,
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") schemaBuildingData: SchemaBuildingData,
    ) {
        if (entityType.isExternalReference) {
            return
        }

        val inputObjectFieldsData =
            InputObjectFieldsData(
                createInputFields = arrayListOf(),
                updateInputFields = arrayListOf(),
                embeddedInputFields = null,
                compareInputFields = arrayListOf(),
                incInputFields = arrayListOf(),
            )

        entityType.inheritedPersistableProperties
            // todo something went wrong here...
            .filter {
                !it.isId &&
                    it.name != "aggregateRoot" &&
                    it.name != "chgCnt" &&
                    it.name != "sys_ver"
            }.forEach {
                it.accept(this, inputObjectFieldsData)
            }

        val createInputObjectType =
            newInputObject()
                .name("$CREATE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                .apply {
                    when (entityType.rootEntityType.idStrategy) {
                        is ManualIdStrategy -> this.field(mandatoryIdInputField)
                        is AutoOnEmptyIdStrategy -> this.field(idInputField)
                    }
                }.fields(inputObjectFieldsData.createInputFields)
                .build()

        schemaBuildingData.additionalTypes.add(createInputObjectType)

        inputObjectFieldsData.compareInputFields?.takeIf { it.isNotEmpty() }?.also {
            val compareInputObjectType =
                newInputObject()
                    .name("$COMPARE_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                    .fields(it)
                    .build()
            schemaBuildingData.additionalTypes.add(compareInputObjectType)
        }

        inputObjectFieldsData.incInputFields?.takeIf { it.isNotEmpty() }?.also {
            val incInputType =
                newInputObject()
                    .name("$INC_INPUT_TYPE_PREFIX${entityType.name}$INPUT_TYPE_POSTFIX")
                    .fields(it)
                    .build()
            schemaBuildingData.additionalTypes.add(incInputType)
        }

        if (entityType.isDictionary) {
            addGetField(entityType, schemaBuildingData, dictionaryPacketTypeBuilder, DICTIONARY_PACKET_TYPE_NAME)
            addGetField(entityType, schemaBuildingData, packetTypeBuilder, PACKET_OBJECT_TYPE_NAME)
            addUpdateOrCreateField(
                entityType,
                inputObjectFieldsData,
                schemaBuildingData,
                dictionaryPacketTypeBuilder,
                DICTIONARY_PACKET_TYPE_NAME,
            )
            addDeleteField(
                entityType,
                inputObjectFieldsData,
                schemaBuildingData,
                dictionaryPacketTypeBuilder,
                DICTIONARY_PACKET_TYPE_NAME,
            )
        } else {
            addCreateField(entityType, schemaBuildingData)
            addUpdateField(entityType, inputObjectFieldsData, schemaBuildingData)
            addGetField(entityType, schemaBuildingData, packetTypeBuilder, PACKET_OBJECT_TYPE_NAME)
            addUpdateOrCreateField(entityType, inputObjectFieldsData, schemaBuildingData, packetTypeBuilder, PACKET_OBJECT_TYPE_NAME)
            addDeleteField(entityType, inputObjectFieldsData, schemaBuildingData, packetTypeBuilder, PACKET_OBJECT_TYPE_NAME)
        }
    }

    override fun visit(
        primitiveProperty: PrimitiveProperty,
        param: InputObjectFieldsData,
    ) {
        val inputObjectField =
            newInputObjectField()
                .name(primitiveProperty.name)
                .type(getType<GraphQLInputType>(scalarTypes.typeMapping[primitiveProperty.type], true))
                .build()

        val createInputObjectField =
            if (primitiveProperty.isOptional) {
                inputObjectField
            } else {
                newInputObjectField()
                    .name(primitiveProperty.name)
                    .type(getType<GraphQLInputType>(scalarTypes.typeMapping[primitiveProperty.type], false))
                    .build()
            }

        primitiveProperty.isSettableOnCreate.onTrue { param.createInputFields?.add(createInputObjectField) }
        primitiveProperty.isSettableOnUpdate.onTrue { param.updateInputFields?.add(inputObjectField) }
        param.embeddedInputFields?.add(inputObjectField)
        isNeedCompareInput(primitiveProperty).onTrue { param.compareInputFields?.add(inputObjectField) }
        isNeedIncInput(primitiveProperty).onTrue {
            val incInputObjectField =
                newInputObjectField()
                    .name(primitiveProperty.name)
                    .type(scalarTypes.incTypeByScalarType[primitiveProperty.type])
                    .build()
            param.incInputFields?.add(incInputObjectField)
        }
    }

    override fun visit(
        enumProperty: EnumProperty,
        param: InputObjectFieldsData,
    ) {
        val inputObjectField =
            newInputObjectField()
                .name(enumProperty.name)
                .type(getType<GraphQLInputType>(typeRef(ENUM_OBJECT_TYPE_PREFIX + enumProperty.type.name), true))
                .build()

        val createInputObjectField =
            if (enumProperty.isOptional) {
                inputObjectField
            } else {
                newInputObjectField()
                    .name(enumProperty.name)
                    .type(getType<GraphQLInputType>(typeRef(ENUM_OBJECT_TYPE_PREFIX + enumProperty.type.name), false))
                    .build()
            }

        enumProperty.isSettableOnCreate.onTrue { param.createInputFields?.add(createInputObjectField) }
        enumProperty.isSettableOnUpdate.onTrue { param.updateInputFields?.add(inputObjectField) }
        param.embeddedInputFields?.add(inputObjectField)
        enumProperty.isSettableOnUpdate.onTrue { param.compareInputFields?.add(inputObjectField) }
    }

    override fun visit(
        primitiveCollectionProperty: PrimitiveCollectionProperty,
        param: InputObjectFieldsData,
    ) {
        val inputObjectField =
            newInputObjectField()
                .name(primitiveCollectionProperty.name)
                .type(list(scalarTypes.typeMapping[primitiveCollectionProperty.type]))
                .build()

        param.createInputFields?.add(inputObjectField)
        param.updateInputFields?.add(inputObjectField)
        param.embeddedInputFields?.add(inputObjectField)
    }

    override fun visit(
        enumCollectionProperty: EnumCollectionProperty,
        param: InputObjectFieldsData,
    ) {
        val inputObjectField =
            newInputObjectField()
                .name(enumCollectionProperty.name)
                .type(list(typeRef(ENUM_OBJECT_TYPE_PREFIX + enumCollectionProperty.type.name)))
                .build()

        param.createInputFields?.add(inputObjectField)
        param.updateInputFields?.add(inputObjectField)
        param.embeddedInputFields?.add(inputObjectField)
    }

    override fun visit(
        embeddedProperty: EmbeddedProperty,
        param: InputObjectFieldsData,
    ) {
        val (inputObjectField, createInputObjectField) =
            if (embeddedProperty.isExternalReference) {
                val type =
                    if (embeddedProperty.type.properties.size == 1) {
                        singleReferenceInputType
                    } else {
                        doubleReferenceInputType
                    }
                val optionalField =
                    newInputObjectField()
                        .name(embeddedProperty.name)
                        .type(type)
                        .build()
                optionalField to
                    if (embeddedProperty.isMandatoryExternalReference) {
                        newInputObjectField()
                            .name(embeddedProperty.name)
                            .type(nonNull(type))
                            .build()
                    } else {
                        optionalField
                    }
            } else {
                newInputObjectField()
                    .name(embeddedProperty.name)
                    .type(typeRef("_${embeddedProperty.type.name}$INPUT_TYPE_POSTFIX"))
                    .build()
                    .let {
                        it to it
                    }
            }

        param.createInputFields?.add(createInputObjectField)
        param.updateInputFields?.add(inputObjectField)
        param.embeddedInputFields?.add(inputObjectField)
    }

    override fun visit(
        referenceProperty: ReferenceProperty,
        param: InputObjectFieldsData,
    ) {
        val isOptional =
            when (val aggregateOrLeaf = model.aggregatesModel?.aggregateOrLeaf(referenceProperty.owningEntityType.name)) {
                is Leaf -> {
                    if (referenceProperty.name == aggregateOrLeaf.parentProperty.name) {
                        aggregateOrLeaf.treeParentProperty != null
                    } else {
                        referenceProperty.isOptional
                    }
                }

                else -> {
                    referenceProperty.isOptional
                }
            }

        val inputObjectField =
            newInputObjectField()
                .name(referenceProperty.name)
                .type(Scalars.GraphQLID)
                .build()

        val createInputObjectField =
            if (isOptional) {
                inputObjectField
            } else {
                newInputObjectField()
                    .name(referenceProperty.name)
                    .type(nonNull(Scalars.GraphQLID))
                    .build()
            }

        param.createInputFields?.add(createInputObjectField)
        param.updateInputFields?.add(inputObjectField)
        param.embeddedInputFields?.add(inputObjectField)
    }

    override fun visit(
        mappedReferenceProperty: MappedReferenceProperty,
        param: InputObjectFieldsData,
    ) {
        // do nothing
    }

    override fun visit(
        mappedReferenceCollectionProperty: MappedReferenceCollectionProperty,
        param: InputObjectFieldsData,
    ) {
        if (!mappedReferenceCollectionProperty.isExternalReference) {
            return
        }

        val referenceProperty = mappedReferenceCollectionProperty.type.property("reference")

        val inputType =
            if (referenceProperty is EmbeddedProperty) {
                if (referenceProperty.type.properties.size == 1) {
                    singleReferenceSetInputType
                } else {
                    doubleReferenceSetInputType
                }
            } else {
                throw IllegalStateException(
                    "Property 'reference' of the ${mappedReferenceCollectionProperty.type.name} type must be an EmbeddedProperty," +
                        " but it is the ${referenceProperty::class.simpleName}",
                )
            }

        val inputObjectField =
            newInputObjectField()
                .name(mappedReferenceCollectionProperty.name)
                .type(inputType)
                .build()

        param.createInputFields?.add(inputObjectField)
        param.updateInputFields?.add(inputObjectField)
    }
}
