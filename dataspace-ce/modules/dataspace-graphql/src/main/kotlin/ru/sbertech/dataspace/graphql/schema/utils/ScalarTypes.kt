package ru.sbertech.dataspace.graphql.schema.utils

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLFloat
import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.scalars.ExtendedScalars.GraphQLBigDecimal
import graphql.scalars.ExtendedScalars.GraphQLByte
import graphql.scalars.ExtendedScalars.GraphQLChar
import graphql.scalars.ExtendedScalars.GraphQLLong
import graphql.scalars.ExtendedScalars.GraphQLShort
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLScalarType.newScalar
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.ELEMENTS_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_FAIL_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_NEGATIVE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_OPERATION_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.INC_VALUE_FIELD_NAME
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.countFieldDefinition
import ru.sbertech.dataspace.graphql.schema.utils.SchemaHelper.incFailOperatorEnumType
import ru.sbertech.dataspace.graphql.schema.utils.coercing.ByteArrayScalarTypeCoercing
import ru.sbertech.dataspace.graphql.schema.utils.coercing.DateScalarTypeCoercing
import ru.sbertech.dataspace.graphql.schema.utils.coercing.DateTimeScalarTypeCoercing
import ru.sbertech.dataspace.graphql.schema.utils.coercing.Float4ScalarTypeCoercing
import ru.sbertech.dataspace.graphql.schema.utils.coercing.OffsetDateTimeScalarTypeCoercing
import ru.sbertech.dataspace.graphql.schema.utils.coercing.TimeScalarTypeCoercing
import ru.sbertech.dataspace.primitive.type.PrimitiveType
import sbp.com.sbt.dataspace.graphqlschema.Helper

class ScalarTypes(
    val useLegacy: Boolean = false,
) {
    companion object {
        const val FLOAT4_SCALAR_TYPE_NAME = "_Float4"

        const val TIME_SCALAR_TYPE_NAME = "_Time"

        const val DATE_SCALAR_TYPE_NAME = "_Date"

        const val DATETIME_SCALAR_TYPE_NAME = "_DateTime"

        const val OFFSET_DATETIME_SCALAR_TYPE_NAME = "_OffsetDateTime"

        const val BYTE_ARRAY_SCALAR_TYPE_NAME = "_ByteArray"

        const val CHARACTER_COLLECTION_OBJECT_TYPE_NAME = "_CharCollection"

        const val STRING_COLLECTION_OBJECT_TYPE_NAME = "_StringCollection"

        const val BYTE_COLLECTION_OBJECT_TYPE_NAME = "_ByteCollection"

        const val SHORT_COLLECTION_OBJECT_TYPE_NAME = "_ShortCollection"

        const val INTEGER_COLLECTION_OBJECT_TYPE_NAME = "_IntCollection"

        const val LONG_COLLECTION_OBJECT_TYPE_NAME = "_LongCollection"

        const val FLOAT_COLLECTION_OBJECT_TYPE_NAME = "_Float4Collection"

        const val DOUBLE_COLLECTION_OBJECT_TYPE_NAME = "_FloatCollection"

        const val BIG_DECIMAL_COLLECTION_OBJECT_TYPE_NAME = "_BigDecimalCollection"

        const val DATE_COLLECTION_OBJECT_TYPE_NAME = "_DateCollection"

        const val DATETIME_COLLECTION_OBJECT_TYPE_NAME = "_DateTimeCollection"

        const val OFFSET_DATETIME_COLLECTION_OBJECT_TYPE_NAME = "_OffsetDateTimeCollection"

        const val TIME_COLLECTION_OBJECT_TYPE_NAME = "_TimeCollection"

        const val BOOLEAN_COLLECTION_OBJECT_TYPE_NAME = "_BooleanCollection"

        const val BYTE_ARRAY_COLLECTION_OBJECT_TYPE_NAME = "_ByteArrayCollection"
    }

    val float4ScalarType: GraphQLScalarType =
        if (useLegacy) {
            Helper.FLOAT4_SCALAR_TYPE
        } else {
            newScalar()
                .name(FLOAT4_SCALAR_TYPE_NAME)
                .coercing(Float4ScalarTypeCoercing)
                .build()
        }

    val timeScalarType: GraphQLScalarType =
        if (useLegacy) {
            Helper.TIME_SCALAR_TYPE
        } else {
            newScalar()
                .name(TIME_SCALAR_TYPE_NAME)
                .coercing(TimeScalarTypeCoercing)
                .build()
        }

    val dateScalarType: GraphQLScalarType =
        if (useLegacy) {
            Helper.DATE_SCALAR_TYPE
        } else {
            newScalar()
                .name(DATE_SCALAR_TYPE_NAME)
                .coercing(DateScalarTypeCoercing)
                .build()
        }

    val dateTimeScalarType: GraphQLScalarType =
        if (useLegacy) {
            Helper.DATETIME_SCALAR_TYPE
        } else {
            newScalar()
                .name(DATETIME_SCALAR_TYPE_NAME)
                .coercing(DateTimeScalarTypeCoercing)
                .build()
        }

    val offsetDateTimeScalarType: GraphQLScalarType =
        if (useLegacy) {
            Helper.OFFSET_DATETIME_SCALAR_TYPE
        } else {
            newScalar()
                .name(OFFSET_DATETIME_SCALAR_TYPE_NAME)
                .coercing(OffsetDateTimeScalarTypeCoercing)
                .build()
        }

    val byteArrayScalarType: GraphQLScalarType =
        if (useLegacy) {
            Helper.BYTE_ARRAY_SCALAR_TYPE
        } else {
            newScalar()
                .name(BYTE_ARRAY_SCALAR_TYPE_NAME)
                .coercing(ByteArrayScalarTypeCoercing)
                .build()
        }

    val typeMapping: Map<PrimitiveType, GraphQLScalarType> =
        mapOf(
            PrimitiveType.Char to GraphQLChar,
            PrimitiveType.String to GraphQLString,
            PrimitiveType.Text to GraphQLString,
            PrimitiveType.Byte to GraphQLByte,
            PrimitiveType.Short to GraphQLShort,
            PrimitiveType.Int to GraphQLInt,
            PrimitiveType.Long to GraphQLLong,
            PrimitiveType.Double to GraphQLFloat,
            PrimitiveType.BigDecimal to GraphQLBigDecimal,
            PrimitiveType.Boolean to GraphQLBoolean,
            PrimitiveType.Float to float4ScalarType,
            PrimitiveType.LocalTime to timeScalarType,
            PrimitiveType.LocalDate to dateScalarType,
            PrimitiveType.LocalDateTime to dateTimeScalarType,
            PrimitiveType.OffsetDateTime to offsetDateTimeScalarType,
            PrimitiveType.ByteArray to byteArrayScalarType,
        )

    val characterCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(CHARACTER_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLChar)))),
            ).field(countFieldDefinition)
            .build()

    val stringCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(STRING_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLString)))),
            ).field(countFieldDefinition)
            .build()

    val byteCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(BYTE_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLByte)))),
            ).field(countFieldDefinition)
            .build()

    val shortCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(SHORT_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLShort)))),
            ).field(countFieldDefinition)
            .build()

    val integerCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(INTEGER_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLInt)))),
            ).field(countFieldDefinition)
            .build()

    val longCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(LONG_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLLong)))),
            ).field(countFieldDefinition)
            .build()

    val doubleCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(DOUBLE_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLFloat)))),
            ).field(countFieldDefinition)
            .build()

    val bigDecimalCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(BIG_DECIMAL_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLBigDecimal)))),
            ).field(countFieldDefinition)
            .build()

    val booleanCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(BOOLEAN_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(GraphQLBoolean)))),
            ).field(countFieldDefinition)
            .build()

    val floatCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(FLOAT_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(float4ScalarType)))),
            ).field(countFieldDefinition)
            .build()

    val timeCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(TIME_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(timeScalarType)))),
            ).field(countFieldDefinition)
            .build()

    val dateCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(DATE_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(dateScalarType)))),
            ).field(countFieldDefinition)
            .build()

    val dateTimeCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(DATETIME_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(dateTimeScalarType)))),
            ).field(countFieldDefinition)
            .build()

    val offsetDateTimeCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(OFFSET_DATETIME_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(offsetDateTimeScalarType)))),
            ).field(countFieldDefinition)
            .build()

    val byteArrayCollectionObjectType: GraphQLObjectType =
        newObject()
            .name(BYTE_ARRAY_COLLECTION_OBJECT_TYPE_NAME)
            .field(
                newFieldDefinition()
                    .name(ELEMENTS_FIELD_NAME)
                    .type(nonNull(list(nonNull(byteArrayScalarType)))),
            ).field(countFieldDefinition)
            .build()

    val collectionTypeMapping: Map<PrimitiveType, GraphQLOutputType> =
        mapOf(
            PrimitiveType.Char to nonNull(characterCollectionObjectType),
            PrimitiveType.String to nonNull(stringCollectionObjectType),
            PrimitiveType.Text to nonNull(stringCollectionObjectType),
            PrimitiveType.Byte to nonNull(byteCollectionObjectType),
            PrimitiveType.Short to nonNull(shortCollectionObjectType),
            PrimitiveType.Int to nonNull(integerCollectionObjectType),
            PrimitiveType.Long to nonNull(longCollectionObjectType),
            PrimitiveType.Double to nonNull(doubleCollectionObjectType),
            PrimitiveType.BigDecimal to nonNull(bigDecimalCollectionObjectType),
            PrimitiveType.Boolean to nonNull(booleanCollectionObjectType),
            PrimitiveType.Float to nonNull(floatCollectionObjectType),
            PrimitiveType.LocalTime to nonNull(timeCollectionObjectType),
            PrimitiveType.LocalDate to nonNull(dateCollectionObjectType),
            PrimitiveType.LocalDateTime to nonNull(dateTimeCollectionObjectType),
            PrimitiveType.OffsetDateTime to nonNull(offsetDateTimeCollectionObjectType),
            PrimitiveType.ByteArray to nonNull(byteArrayCollectionObjectType),
        )

    private fun incInput(primitiveType: PrimitiveType): GraphQLInputObjectType =
        GraphQLInputObjectType
            .newInputObject()
            .name("_Inc${primitiveType::class.simpleName}Value")
            .field(
                GraphQLInputObjectField
                    .newInputObjectField()
                    .name(INC_VALUE_FIELD_NAME)
                    .type(nonNull(typeMapping[primitiveType]))
                    .build(),
            ).field(
                GraphQLInputObjectField
                    .newInputObjectField()
                    .name(INC_NEGATIVE_FIELD_NAME)
                    .type(GraphQLBoolean)
                    .defaultValueProgrammatic(null)
                    .build(),
            ).field(
                GraphQLInputObjectField
                    .newInputObjectField()
                    .name(INC_FAIL_FIELD_NAME)
                    .type(
                        GraphQLInputObjectType
                            .newInputObject()
                            .name("_Inc${primitiveType::class.simpleName}ValueFail")
                            .field(
                                GraphQLInputObjectField
                                    .newInputObjectField()
                                    .name(INC_OPERATION_FIELD_NAME)
                                    .type(nonNull(incFailOperatorEnumType))
                                    .build(),
                            ).field(
                                GraphQLInputObjectField
                                    .newInputObjectField()
                                    .name(INC_VALUE_FIELD_NAME)
                                    .type(nonNull(typeMapping[primitiveType]))
                                    .build(),
                            ).build(),
                    ).build(),
            ).build()

    val incTypeByScalarType: Map<PrimitiveType, GraphQLInputObjectType> =
        setOf(
            PrimitiveType.Int,
            PrimitiveType.Long,
            PrimitiveType.Float,
            PrimitiveType.Double,
            PrimitiveType.BigDecimal,
        ).associateWith { incInput(it) }
}
