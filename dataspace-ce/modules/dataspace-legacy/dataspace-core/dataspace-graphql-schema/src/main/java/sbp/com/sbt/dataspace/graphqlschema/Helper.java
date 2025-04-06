package sbp.com.sbt.dataspace.graphqlschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.introspection.Introspection;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLDirective.newDirective;
import static graphql.schema.GraphQLEnumType.newEnum;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLList.list;
import static graphql.schema.GraphQLNonNull.nonNull;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLScalarType.newScalar;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;
import static sbp.com.sbt.dataspace.graphqlschema.GraphQLSchemaHelper.*;

/**
 * Assistant
 * The scalar types are defined, as well as commonly used arguments and enumerations.
 */
public final class Helper {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String SKIP_DIRECTIVE_NAME = "skip";
    public static final String INCLUDE_DIRECTIVE_NAME = "include";
    public static final String IF_ARGUMENT_NAME = "if";
    public static final String TYPE = "_type";

    public static final GraphQLScalarType FLOAT4_SCALAR_TYPE = newScalar()
            .name(GraphQLSchemaHelper.FLOAT4_SCALAR_TYPE_NAME)
            .coercing(new Float4ScalarTypeCoercing())
            .build();

    public static final DateScalarTypeCoercing DATE_SCALAR_TYPE_COERCING = new DateScalarTypeCoercing();
    public static final GraphQLScalarType DATE_SCALAR_TYPE = newScalar()
            .name(GraphQLSchemaHelper.DATE_SCALAR_TYPE_NAME)
            .coercing(DATE_SCALAR_TYPE_COERCING)
            .build();

    public static final DateTimeScalarTypeCoercing DATE_TIME_SCALAR_TYPE_COERCING = new DateTimeScalarTypeCoercing();
    public static final GraphQLScalarType DATETIME_SCALAR_TYPE = newScalar()
            .name(GraphQLSchemaHelper.DATETIME_SCALAR_TYPE_NAME)
            .coercing(DATE_TIME_SCALAR_TYPE_COERCING)
            .build();

    public static final OffsetDateTimeScalarTypeCoercing OFFSET_DATE_TIME_SCALAR_TYPE_COERCING = new OffsetDateTimeScalarTypeCoercing();
    public static final GraphQLScalarType OFFSET_DATETIME_SCALAR_TYPE = newScalar()
            .name(GraphQLSchemaHelper.OFFSET_DATETIME_SCALAR_TYPE_NAME)
            .coercing(OFFSET_DATE_TIME_SCALAR_TYPE_COERCING)
            .build();

    public static final TimeScalarTypeCoercing TIME_SCALAR_TYPE_COERCING = new TimeScalarTypeCoercing();
    public static final GraphQLScalarType TIME_SCALAR_TYPE = newScalar()
            .name(GraphQLSchemaHelper.TIME_SCALAR_TYPE_NAME)
            .coercing(TIME_SCALAR_TYPE_COERCING)
            .build();

    public static final ByteArrayScalarTypeCoercing BYTE_ARRAY_SCALAR_TYPE_COERCING = new ByteArrayScalarTypeCoercing();

    public static final GraphQLScalarType BYTE_ARRAY_SCALAR_TYPE = newScalar()
            .name(GraphQLSchemaHelper.BYTE_ARRAY_SCALAR_TYPE_NAME)
            .coercing(BYTE_ARRAY_SCALAR_TYPE_COERCING)
            .build();
    public static final GraphQLEnumType SORT_ORDER_ENUM_TYPE = newEnum()
            .name(GraphQLSchemaHelper.SORT_ORDER_ENUM_TYPE_NAME)
            .value(GraphQLSchemaHelper.ASCENDING_ENUM_VALUE)
            .value(GraphQLSchemaHelper.DESCENDING_ENUM_VALUE)
            .build();
    public static final GraphQLInputObjectType SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE = newInputObject()
            .name(GraphQLSchemaHelper.SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE_NAME)
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.CRITERION_INPUT_OBJECT_FIELD_NAME)
                    .type(nonNull(Scalars.GraphQLString)))
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.ORDER_INPUT_OBJECT_FIELD_NAME)
                    .type(nonNull(SORT_ORDER_ENUM_TYPE))
                    .defaultValue(GraphQLSchemaHelper.ASCENDING_ENUM_VALUE))
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.NULLS_LAST_OBJECT_FIELD_NAME)
                    .type(Scalars.GraphQLBoolean))
            .build();
    public static final GraphQLEnumType UNAVAILABLE_ENUM_TYPE = newEnum()
            .name(GraphQLSchemaHelper.UNAVAILABLE_ENUM_TYPE_NAME)
            .value(GraphQLSchemaHelper.FAIL_ENUM_VALUE)
            .value(GraphQLSchemaHelper.RETRY_ENUM_VALUE)
            .value(GraphQLSchemaHelper.IGNORE_ENUM_VALUE)
            .build();
    public static final GraphQLEnumType INCOMPATIBLE_ENUM_TYPE = newEnum()
            .name(GraphQLSchemaHelper.INCOMPATIBLE_ENUM_TYPE_NAME)
            .value(GraphQLSchemaHelper.FAIL_ENUM_VALUE)
            .value(GraphQLSchemaHelper.IGNORE_ENUM_VALUE)
            .build();
    public static final GraphQLInputObjectType ERROR_STRATEGY_OBJECT_TYPE = newInputObject()
            .name(GraphQLSchemaHelper.ERROR_STRATEGY_OBJECT_TYPE_NAME)
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.UNAVAILABLE_OBJECT_FIELD_NAME)
                    .type(UNAVAILABLE_ENUM_TYPE))
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.INCOMPATIBLE_OBJECT_FIELD_NAME)
                    .type(INCOMPATIBLE_ENUM_TYPE))
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.RETRY_COUNT_OBJECT_FIELD_NAME)
                    .type(Scalars.GraphQLInt))
            .field(newInputObjectField()
                    .name(GraphQLSchemaHelper.RETRY_INTERVAL_MS_OBJECT_FIELD_NAME)
                    .type(Scalars.GraphQLInt))
            .build();
    public static final GraphQLOutputType SHARD_ERROR_STAT_OBJECT_TYPE = newObject()
            .name(GraphQLSchemaHelper.SHARD_ERROR_STAT_OBJECT_TYPE_NAME)
            .field(newFieldDefinition()
                    .name(GraphQLSchemaHelper.PERCENT_SKIPPED_OBJECT_FIELD_NAME)
                    .type(Scalars.GraphQLInt))
            .field(newFieldDefinition()
                    .name(GraphQLSchemaHelper.PERCENT_UNAVAILABLE_OBJECT_FIELD_NAME)
                    .type(Scalars.GraphQLInt))
            .field(newFieldDefinition()
                    .name(GraphQLSchemaHelper.PERCENT_INCOMPATIBLE_OBJECT_FIELD_NAME)
                    .type(Scalars.GraphQLInt))
            .build();
    public static final GraphQLArgument ID_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.ID_ARGUMENT_NAME)
            .type(nonNull(Scalars.GraphQLID))
            .build();
    public static final GraphQLArgument DATE_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.DATE_ARGUMENT_NAME)
            .type(nonNull(OFFSET_DATETIME_SCALAR_TYPE))
            .build();
    public static final GraphQLArgument CONDITION_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.CONDITION_ARGUMENT_NAME)
            .type(Scalars.GraphQLString)
            .build();
    public static final GraphQLArgument GROUP_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.GROUP_ARGUMENT_NAME)
            .type(list(nonNull(Scalars.GraphQLString)))
            .build();
    public static final GraphQLArgument GROUP_COND_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.GROUP_COND_ARGUMENT_NAME)
            .type(Scalars.GraphQLString)
            .build();
    public static final GraphQLArgument LIMIT_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.LIMIT_ARGUMENT_NAME)
            .type(Scalars.GraphQLInt)
            .build();
    public static final GraphQLArgument OFFSET_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.OFFSET_ARGUMENT_NAME)
            .type(Scalars.GraphQLInt)
            .build();
    public static final GraphQLArgument SORT_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.SORT_ARGUMENT_NAME)
            .type(list(nonNull(SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE)))
            .build();
    public static final GraphQLArgument HISTORY_SORT_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.SORT_ARGUMENT_NAME)
            .type(SORT_ORDER_ENUM_TYPE)
            .build();
    public static final GraphQLArgument CTX_ARGUMENT = newArgument()
            .name(MULTISEARCH_CONTEXT_NAME)
            .type(Scalars.GraphQLString)
            .build();
    public static final GraphQLArgument ERROR_STRATEGY_ARGUMENT = newArgument()
            .name(ERROR_STRATEGY_NAME)
            .type(ERROR_STRATEGY_OBJECT_TYPE)
            .build();
    public static final GraphQLArgument ALIAS_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.ALIAS_ARGUMENT_NAME)
            .type(Scalars.GraphQLString)
            .build();
    public static final GraphQLArgument ELEMENT_ALIAS_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.ELEMENT_ALIAS_ARGUMENT_NAME)
            .type(Scalars.GraphQLString)
            .build();
    public static final GraphQLArgument EXPRESSION_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.EXPRESSION_ARGUMENT_NAME)
            .type(nonNull(Scalars.GraphQLString))
            .build();
    public static final GraphQLArgument EXPR_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.EXPR_ARGUMENT_NAME)
            .type(nonNull(Scalars.GraphQLString))
            .build();
    public static final GraphQLArgument DISTINCT_ARGUMENT = newArgument()
            .name(GraphQLSchemaHelper.DISTINCT_ARGUMENT_NAME)
            .type(Scalars.GraphQLBoolean)
            .build();
    public static final List<GraphQLArgument> DEFAULT_SEARCH_SPECIFICATION_ARGUMENTS = Arrays.asList(CONDITION_ARGUMENT, LIMIT_ARGUMENT, OFFSET_ARGUMENT, SORT_ARGUMENT);
    public static final List<GraphQLArgument> DEFAULT_MULTISEARCH_SPECIFICATION_ARGUMENTS = Arrays.asList(CONDITION_ARGUMENT, LIMIT_ARGUMENT, OFFSET_ARGUMENT, SORT_ARGUMENT, CTX_ARGUMENT, ERROR_STRATEGY_ARGUMENT);
    public static final GraphQLDirective MERGE_REQUEST_SPECIFICATION_DIRECTIVE = newDirective()
            .name(GraphQLSchemaHelper.MERGE_REQUEST_SPECIFICATION_DIRECTIVE_NAME)
            .argument(CONDITION_ARGUMENT)
            .validLocation(Introspection.DirectiveLocation.INLINE_FRAGMENT)
            .build();
    public static final GraphQLFieldDefinition COUNT_FIELD_DEFINITION = newFieldDefinition()
            .name(GraphQLSchemaHelper.COUNT_FIELD_NAME)
            .type(nonNull(Scalars.GraphQLInt))
            .build();
    public static final GraphQLFieldDefinition CTX_FIELD_DEFINITION = GraphQLFieldDefinition.newFieldDefinition()
            .name(MULTISEARCH_CONTEXT_NAME)
            .type(Scalars.GraphQLString)
            .build();
    public static final GraphQLFieldDefinition SHARD_ERROR_STAT_FIELD_DEFINITION = GraphQLFieldDefinition.newFieldDefinition()
            .name(SHARD_ERROR_STAT_NAME)
            .type(SHARD_ERROR_STAT_OBJECT_TYPE)
            .build();
    public static final Map<DataType, GraphQLScalarType> TYPE_MAPPING;
    public static final Map<DataType, GraphQLOutputType> COLLECTION_TYPE_MAPPING;
    public static final Set<GraphQLType> DEFAULT_ADDITIONAL_TYPES;
    public static final TypeResolver TYPE_RESOLVER = env -> env.getSchema().getObjectType(GraphQLSchemaHelper.ENTITY_OBJECT_TYPE_PREFIX + ((Map<String, Object>) env.getObject()).get(TYPE));
    public static final Map<String, String> SORT_ORDER_MAPPING;
    public static final Map<DataType, Function<JsonNode, Object>> VALUE_CONVERTER;

    private Helper() {
    }

    static {
        TYPE_MAPPING = new EnumMap<>(DataType.class);
        TYPE_MAPPING.put(DataType.CHARACTER, ExtendedScalars.GraphQLChar);
        TYPE_MAPPING.put(DataType.STRING, Scalars.GraphQLString);
        TYPE_MAPPING.put(DataType.TEXT, Scalars.GraphQLString);
        TYPE_MAPPING.put(DataType.BYTE, ExtendedScalars.GraphQLByte);
        TYPE_MAPPING.put(DataType.SHORT, ExtendedScalars.GraphQLShort);
        TYPE_MAPPING.put(DataType.INTEGER, Scalars.GraphQLInt);
        TYPE_MAPPING.put(DataType.LONG, ExtendedScalars.GraphQLLong);
        TYPE_MAPPING.put(DataType.FLOAT, FLOAT4_SCALAR_TYPE);
        TYPE_MAPPING.put(DataType.DOUBLE, Scalars.GraphQLFloat);
        TYPE_MAPPING.put(DataType.BIG_DECIMAL, ExtendedScalars.GraphQLBigDecimal);
        TYPE_MAPPING.put(DataType.DATE, DATE_SCALAR_TYPE);
        TYPE_MAPPING.put(DataType.DATETIME, DATETIME_SCALAR_TYPE);
        TYPE_MAPPING.put(DataType.OFFSET_DATETIME, OFFSET_DATETIME_SCALAR_TYPE);
        TYPE_MAPPING.put(DataType.TIME, TIME_SCALAR_TYPE);
        TYPE_MAPPING.put(DataType.BOOLEAN, Scalars.GraphQLBoolean);
        TYPE_MAPPING.put(DataType.BYTE_ARRAY, BYTE_ARRAY_SCALAR_TYPE);

        COLLECTION_TYPE_MAPPING = new EnumMap<>(DataType.class);
        COLLECTION_TYPE_MAPPING.put(DataType.CHARACTER, nonNull(newObject()
                .name(GraphQLSchemaHelper.CHARACTER_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(ExtendedScalars.GraphQLChar)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.STRING, nonNull(newObject()
                .name(GraphQLSchemaHelper.STRING_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(Scalars.GraphQLString)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.TEXT, nonNull(newObject()
                .name(GraphQLSchemaHelper.STRING_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(Scalars.GraphQLString)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.BYTE, nonNull(newObject()
                .name(GraphQLSchemaHelper.BYTE_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(ExtendedScalars.GraphQLByte)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.SHORT, nonNull(newObject()
                .name(GraphQLSchemaHelper.SHORT_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(ExtendedScalars.GraphQLShort)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.INTEGER, nonNull(newObject()
                .name(GraphQLSchemaHelper.INTEGER_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(Scalars.GraphQLInt)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.LONG, nonNull(newObject()
                .name(GraphQLSchemaHelper.LONG_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(ExtendedScalars.GraphQLLong)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.FLOAT, nonNull(newObject()
                .name(GraphQLSchemaHelper.FLOAT_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(FLOAT4_SCALAR_TYPE)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.DOUBLE, nonNull(newObject()
                .name(GraphQLSchemaHelper.DOUBLE_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(Scalars.GraphQLFloat)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.BIG_DECIMAL, nonNull(newObject()
                .name(GraphQLSchemaHelper.BIG_DECIMAL_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(ExtendedScalars.GraphQLBigDecimal)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.DATE, nonNull(newObject()
                .name(GraphQLSchemaHelper.DATE_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(DATE_SCALAR_TYPE)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.DATETIME, nonNull(newObject()
                .name(GraphQLSchemaHelper.DATETIME_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(DATETIME_SCALAR_TYPE)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.OFFSET_DATETIME, nonNull(newObject()
                .name(GraphQLSchemaHelper.OFFSET_DATETIME_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(OFFSET_DATETIME_SCALAR_TYPE)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.TIME, nonNull(newObject()
                .name(GraphQLSchemaHelper.TIME_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(TIME_SCALAR_TYPE)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.BOOLEAN, nonNull(newObject()
                .name(GraphQLSchemaHelper.BOOLEAN_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(Scalars.GraphQLBoolean)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));
        COLLECTION_TYPE_MAPPING.put(DataType.BYTE_ARRAY, nonNull(newObject()
                .name(GraphQLSchemaHelper.BYTE_ARRAY_COLLECTION_OBJECT_TYPE_NAME)
                .field(newFieldDefinition()
                        .name(GraphQLSchemaHelper.ELEMENTS_FIELD_NAME)
                        .type(nonNull(list(nonNull(BYTE_ARRAY_SCALAR_TYPE)))))
                .field(COUNT_FIELD_DEFINITION)
                .build()));

        DEFAULT_ADDITIONAL_TYPES = new LinkedHashSet<>();
        DEFAULT_ADDITIONAL_TYPES.add(FLOAT4_SCALAR_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(DATE_SCALAR_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(DATETIME_SCALAR_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(OFFSET_DATETIME_SCALAR_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(TIME_SCALAR_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(BYTE_ARRAY_SCALAR_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(SORT_ORDER_ENUM_TYPE);
        DEFAULT_ADDITIONAL_TYPES.add(SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE);

        SORT_ORDER_MAPPING = new HashMap<>(2);
        SORT_ORDER_MAPPING.put(GraphQLSchemaHelper.ASCENDING_ENUM_VALUE, EntitiesReadAccessJsonHelper.ASCENDING);
        SORT_ORDER_MAPPING.put(GraphQLSchemaHelper.DESCENDING_ENUM_VALUE, EntitiesReadAccessJsonHelper.DESCENDING);

        VALUE_CONVERTER = new EnumMap<>(DataType.class);
        Arrays.stream(DataType.values()).forEach(dataType -> VALUE_CONVERTER.put(dataType, node -> node.isNull() ? null : node.textValue()));
        VALUE_CONVERTER.put(DataType.BOOLEAN, node -> node.isNull() ? null : node.booleanValue());
    }

    /**
     * Get type
     *
     * @param type      Тип
     * @param mandatory The indicator of obligation
     */
    public static GraphQLOutputType getType(GraphQLOutputType type, boolean mandatory) {
        return mandatory ? nonNull(type) : type;
    }

    /**
     * Get parsing exception of literal
     *
     * @param type  Тип
     * @param value The value
     */
    public static CoercingParseLiteralException getCoercingParseLiteralException(String type, Object value) {
return new CoercingParseLiteralException(getFullDescription("Error during literal parsing", param("Type", type), param("Value", value), param("Type of value", value.getClass().getName())));
    }

    /**
     * Get parsing exception value
     *
     * @param type  Тип
     * @param value The value
     */
    public static CoercingParseValueException getCoercingParseValueException(String type, Object value) {
return new CoercingParseValueException(getFullDescription("Error during parsing of value", param("Type", type), param("Value", value), param("Type of value", value.getClass().getName())));
    }

    /**
     * Get serialization value exception
     *
     * @param type  Тип
     * @param value The value
     */
    public static CoercingSerializeException getCoercingSerializeValueException(String type, Object value) {
return new CoercingSerializeException(getFullDescription("Error during serialization of value", param("Type", type), param("Value", value), param("Type of value", value.getClass().getName())));
    }

    private static Object getVariableValue(DataFetchingEnvironment environment, String variablePath) {
        Object current = environment.getVariables();
        int beginIndex = 0;
        for (int i = 0; i <= variablePath.length(); ++i) {
            char symbol;
            if (i == variablePath.length())
                symbol = '.';
            else
                symbol = variablePath.charAt(i);
            if ((symbol == '.' || symbol == '[') && beginIndex < i) {
                String propertyName = variablePath.substring(beginIndex, i);
                if (current instanceof Map) {
                    current = ((Map<String, Object>) current).get(propertyName);
                    if (current == null) {
                        return null;
                    }
                } else if (current instanceof List) {
                    int beginIndexCopy = beginIndex;
                    //Checks that current is a list of objects and throws null values
                    current = ((List<Object>) current).stream().filter(Objects::nonNull).map(elem -> {
                        if (elem instanceof Map) {
                            return ((Map<String, Object>) elem).get(propertyName);
                        } else {
                            throw new GraphQLException(variablePath.substring(0, beginIndexCopy) + " is not array of objects");
                        }
                    }).collect(Collectors.toList());
                } else {
                    throw new GraphQLException(variablePath.substring(0, beginIndex) + " is not object or array");
                }
            }
            switch (symbol) {
                case '.':
                    beginIndex = i + 1;
                    break;
                case '[':
                    if (current instanceof List) {
                        int rightBracketIndex = variablePath.indexOf(']', i + 1);
                        if (rightBracketIndex == -1) {
                            throw new GraphQLException("Missing ']' in " + variablePath);
                        }
                        int index;
                        try {
                            index = Integer.parseInt(variablePath.substring(i + 1, rightBracketIndex));
                        } catch (Exception e) {
                            throw new GraphQLException("Incorrect index in " + variablePath.substring(0, rightBracketIndex));
                        }
                        if (index >= 0 && index < ((List<Object>) current).size()) {
                            current = ((List<Object>) current).get(index);
                        } else {
                            return null;
                        }
                        i = rightBracketIndex;
                        beginIndex = i + 1;
                    } else {
                        throw new GraphQLException(variablePath.substring(0, i) + " is not array");
                    }
                    break;
            }
        }
        return current;
    }

    private static void appendVariableValue(StringBuilder stringBuilder, Object variableValue, boolean listAsListElemProhibition) {
        if (variableValue instanceof Character) {
            if (variableValue.equals('\'')) {
                stringBuilder.append("''''");
            } else {
                stringBuilder.append('\'').append((char) variableValue).append('\'');
            }
        } else if (variableValue instanceof String) {
            stringBuilder.append('\'').append(((String) variableValue).replace("'", "''")).append('\'');
        } else if (variableValue instanceof Number || variableValue instanceof Boolean) {
            stringBuilder.append(variableValue);
        } else if (variableValue instanceof LocalDate) {
            stringBuilder.append('D');
            DateTimeFormatter.ISO_LOCAL_DATE.formatTo((LocalDate) variableValue, stringBuilder);
        } else if (variableValue instanceof LocalDateTime) {
            stringBuilder.append('D');
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.formatTo((LocalDateTime) variableValue, stringBuilder);
        } else if (variableValue instanceof OffsetDateTime) {
            stringBuilder.append('D');
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.formatTo((OffsetDateTime) variableValue, stringBuilder);
        } else if (variableValue instanceof LocalTime) {
            stringBuilder.append('T');
            DateTimeFormatter.ISO_LOCAL_TIME.formatTo((LocalTime) variableValue, stringBuilder);
        } else if (variableValue instanceof List) {
            List<Object> list = ((List<Object>) variableValue).stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (list.isEmpty()) {
                throw new GraphQLException("Empty list is not allowed as variable value");
            } else if (listAsListElemProhibition) {
                throw new GraphQLException("List as list elem is not allowed");
            }
            appendVariableValue(stringBuilder, list.get(0), true);
            for (int i = 1; i < list.size(); ++i) {
                stringBuilder.append(',');
                appendVariableValue(stringBuilder, list.get(i), true);
            }
        } else {
            throw new GraphQLException("Unexpected variable value " + variableValue.getClass().getCanonicalName());
        }
    }

    // does not support inserting null values
    public static String injectVariablesIntoStringExpression(DataFetchingEnvironment environment, String stringExpression) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean inString = false;
        int variablePathBeginIndex = -1;
        int beginIndex = 0;
        for (int i = 0; i < stringExpression.length(); ++i) {
            char symbol = stringExpression.charAt(i);
            switch (symbol) {
                case '\'':
                    inString = !inString;
                    break;
                case '$':
                    if (!inString && i + 1 < stringExpression.length() && stringExpression.charAt(i + 1) == '{') {
                        ++i;
                        variablePathBeginIndex = i + 1;
                    }
                    break;
                case '}':
                    if (variablePathBeginIndex != -1) {
                        stringBuilder.append(stringExpression, beginIndex, variablePathBeginIndex - 2);
                        beginIndex = i + 1;
                        String variablePath = stringExpression.substring(variablePathBeginIndex, i);
                        Object variableValue = getVariableValue(environment, variablePath);
                        if (variableValue == null) {
                            throw new GraphQLException(variablePath + " is null (not supported)");
                        }
                        appendVariableValue(stringBuilder, variableValue, false);
                        variablePathBeginIndex = -1;
                    }
                    break;
            }
        }
        stringBuilder.append(stringExpression, beginIndex, stringExpression.length());
        return stringBuilder.toString();
    }

    public static void addSqlExprField(GraphQLObjectType.Builder typeBuilder) {
        typeBuilder.field(
            newFieldDefinition()
                .name(GraphQLSchemaHelper.STRING_EXPRESSION_NAME)
                .replaceArguments(
                    Helper.TYPE_MAPPING.values().stream()
                        .filter(scalarType -> !scalarType.getName().equals("_ByteArray"))
                        .map(scalarType -> {
                            String argumentName = scalarType.getName();
                            if (argumentName.charAt(0) == '_') {
                                argumentName = argumentName.substring(1);
                            }
                            argumentName = Character.toLowerCase(argumentName.charAt(0)) + argumentName.substring(1);
                            if (argumentName.equals("float4")) {
                                argumentName = "float";
                            } else if (argumentName.equals("float")) {
                                argumentName = "double";
                            }
                            return newArgument()
                                .name(argumentName + 's')
                                .type(list(nonNull(scalarType)))
                                .build();
                        }).collect(Collectors.toList())
                ).type(Scalars.GraphQLString)
        );
    }
}
