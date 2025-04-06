package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import sbp.com.sbt.dataspace.feather.common.Function3;
import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.common.ThrowingFunction2;
import sbp.com.sbt.dataspace.feather.common.ThrowingFunction3;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithCondition;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithElementAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithEntityType;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;
import sbp.com.sbt.dataspace.feather.tablequeryprovider.TableQueryProvider;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.common.Node.node;

/**
 * Assistant
 */
final class Helper {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final Set<DataType> NUMBER_TYPES = EnumSet.of(DataType.BYTE, DataType.SHORT, DataType.INTEGER, DataType.LONG, DataType.FLOAT, DataType.DOUBLE, DataType.BIG_DECIMAL);
    static final Set<DataType> STRING_TYPES = EnumSet.of(DataType.CHARACTER, DataType.STRING, DataType.TEXT);
    static final Set<DataType> DATE_TYPES = EnumSet.of(DataType.DATE, DataType.DATETIME, DataType.OFFSET_DATETIME);
    static final Set<DataType> TIME_TYPES = EnumSet.of(DataType.TIME);
    static final Set<DataType> BOOLEAN_TYPES = EnumSet.of(DataType.BOOLEAN);
    static final Node<String> BRACKET_L_NODE = node("(");
    static final Node<String> BRACKET_R_NODE = node(")");
    static final Node<String> IS_NULL_NODE = node(" is null");
    static final Node<String> IS_NOT_NULL_NODE = node(" is not null");
    static final Node<String> AND_NODE = node(" and ");
    static final Node<String> SPACE_NODE = node(" ");
    static final Node<String> DOT_NODE = node(".");
    static final Node<String> UPPER_BRACKET_L_NODE = node("upper(");
    static final Node<String> LOWER_BRACKET_L_NODE = node("lower(");
    static final Node<String> LENGTH_BRACKET_L_NODE = node("length(");
    static final Node<String> TRIM_BRACKET_L_NODE = node("trim(");
    static final Node<String> LTRIM_BRACKET_L_NODE = node("ltrim(");
    static final Node<String> RTRIM_BRACKET_L_NODE = node("rtrim(");
    static final Node<String> ROUND_BRACKET_L_NODE = node("round(");
    static final Node<String> CEIL_BRACKET_L_NODE = node("ceil(");
    static final Node<String> FLOOR_BRACKET_L_NODE = node("floor(");
    static final Node<String> ABS_BRACKET_L_NODE = node("abs(");
    static final Node<String> ORA_HASH_BRACKET_L_NODE = node("ora_hash(");
    static final Node<String> ORA_HASH_BRACKET_L_BRACKET_L_NODE = node("ora_hash((");
    static final Node<String> HASH_TEXT_BRACKET_L_NODE = node("hashtext((");
    static final Node<String> BRACKET_R_TEXT_BRACKET_R_NODE = node(")::text)");
    static final Node<String> BRACKET_R_VARCHAR_NODE = node(")::varchar");
    static final Node<String> BRACKET_R_DECIMAL_NODE = node(")::decimal");
    static final Node<String> TO_CHAR_NODE = node("to_char(");
    static final Node<String> TO_NUMBER_NODE = node("to_number(");
    static final Node<String> TO_TIMESTAMP_NODE = node("to_timestamp(");
    static final Node<String> TO_TIMESTAMP_TZ_NODE = node("to_timestamp_tz(");
    static final Node<String> STRING_AS_OFFSET_DATETIME_PART1_NODE = node("cast(to_timestamp_tz(");
    static final Node<String> STRING_AS_OFFSET_DATETIME_PART2_NODE = node(", 'YYYY-MM-DD\"T\"HH24:MI:SS.FF6TZH:TZM') as timestamp with local time zone)");
    static final Node<String> TO_DATE_NODE = node("to_date(");
    static final Node<String> CONCAT_BRACKET_L_NODE = node("concat(");
    static final Node<String> PLUS_NODE = node(" + ");
    static final Node<String> PLUS_INTERVAL_MILLISECOND_NODE = node(" + interval '0.001' second * ");
    static final Node<String> PLUS_INTERVAL_SECOND_NODE = node(" + interval '1' second * ");
    static final Node<String> PLUS_INTERVAL_MINUTE_NODE = node(" + interval '1' minute * ");
    static final Node<String> PLUS_INTERVAL_HOUR_NODE = node(" + interval '1' hour * ");
    static final Node<String> PLUS_INTERVAL_DAY_NODE = node(" + interval '1' day * ");
    static final Node<String> PLUS_INTERVAL_MONTH_NODE = node(" + interval '1' month * ");
    static final Node<String> PLUS_INTERVAL_YEAR_NODE = node(" + interval '1' year * ");
    static final Node<String> MINUS_NODE = node(" - ");
    static final Node<String> MINUS2_NODE = node("-");
    static final Node<String> MINUS_INTERVAL_MILLISECOND_NODE = node(" - interval '0.001' second * ");
    static final Node<String> MINUS_INTERVAL_SECOND_NODE = node(" - interval '1' second * ");
    static final Node<String> MINUS_INTERVAL_MINUTE_NODE = node(" - interval '1' minute * ");
    static final Node<String> MINUS_INTERVAL_HOUR_NODE = node(" - interval '1' hour * ");
    static final Node<String> MINUS_INTERVAL_DAY_NODE = node(" - interval '1' day * ");
    static final Node<String> MINUS_INTERVAL_MONTH_NODE = node(" - interval '1' month * ");
    static final Node<String> MINUS_INTERVAL_YEAR_NODE = node(" - interval '1' year * ");
    static final Node<String> MUL_NODE = node(" * ");
    static final Node<String> DIV_NODE = node(" / ");
    static final Node<String> BRACKET_R_DECIMAL_DIV_NODE = node(")::decimal / ");
    static final Node<String> SUBSTR_BRACKET_L_NODE = node("substr(");
    static final Node<String> REPLACE_BRACKET_L_NODE = node("replace(");
    static final Node<String> EQ_NODE = node(" = ");
    static final Node<String> NOT_EQ_NODE = node(" <> ");
    static final Node<String> GT_NODE = node(" > ");
    static final Node<String> LT_OR_EQ_NODE = node(" <= ");
    static final Node<String> LT_NODE = node(" < ");
    static final Node<String> GT_OR_EQ_NODE = node(" >= ");
    static final Node<String> LIKE_NODE = node(" like ");
    static final Node<String> BETWEEN_NODE = node(" between ");
    static final Node<String> COMMA_NODE = node(", ");
    static final Node<String> IN_NODE = node(" in (");
    static final Node<String> IN2_NODE = node(" in ");
    static final Node<String> BRACKET_L_SELECT_NODE = node("(select ");
    static final Node<String> BRACKET_R_AS_BRACKET_L_NODE = node(") as (");
    static final Node<String> JOIN_NODE = node(" join ");
    static final Node<String> LEFT_JOIN_NODE = node(" left join ");
    static final Node<String> ON_NODE = node(" on ");
    static final Node<String> QUOTE_NODE = node("'");
    static final Node<String> NULL_NODE = node("null");
    static final Node<String> SELECT_NODE = node("select ");
    static final Node<String> DISTINCT_NODE = node("distinct ");
    static final Node<String> FROM_NODE = node(" from ");
    static final Node<String> WHERE_NODE = node(" where ");
    static final Node<String> GROUP_BY_NODE = node(" group by ");
    static final Node<String> HAVING_NODE = node(" having ");
    static final Node<String> ORDER_BY_NODE = node(" order by ");
    static final Node<String> ORDER_BY2_NODE = node("order by ");
    static final Node<String> COUNT_BRACKET_L_NODE = node("count(");
    static final Node<String> COUNT_ASTERISK_NODE = node("count(*)");
    static final Node<String> MIN_BRACKET_L_NODE = node("min(");
    static final Node<String> MAX_BRACKET_L_NODE = node("max(");
    static final Node<String> SUM_BRACKET_L_NODE = node("sum(");
    static final Node<String> AVG_BRACKET_L_NODE = node("avg(");
    static final Node<String> DESC_NODE = node(" desc");
    static final Node<String> NULLS_LAST_NODE = node(" nulls last");
    static final Node<String> NULLS_FIRST_NODE = node(" nulls first");
    static final Node<String> WITH_NODE = node("with ");
    static final Node<String> UNION_ALL_NODE = node(" union all ");
    static final Node<String> FOR_UPDATE_NODE = node(" for update ");
    static final Node<String> NOWAIT_NODE = node(" nowait ");
    static final Node<String> ROW_NUMBER_OVER_NODE = node("row_number() over (");
    static final Node<String> PARTITION_BY_NODE = node("partition by ");
    static final Node<String> ONE_NODE = node("1");
    static final Node<String> OFFSET_NODE = node(" offset ");
    static final Node<String> ROWS_NODE = node(" rows");
    static final Node<String> LIMIT_NODE = node(" limit ");
    static final Node<String> FETCH_NEXT_NODE = node(" fetch next ");
    static final Node<String> ROWS_ONLY_NODE = node(" rows only");
    static final Node<String> EXISTS_NODE = node("exists");
    static final Node<String> NOT_BRACKET_L_NODE = node("not(");
    static final Node<String> OR_NODE = node(" or ");
    static final Node<String> DECIMAL_NODE = node("::decimal");
    static final Node<String> COMMA_BRACKET_L_NODE = node(", (");
    static final Node<String> BRACKET_R_INT_NODE = node(")::int");
    static final Node<String> DATEADD_MILLISECOND_NODE = node("dateadd('MS', ");
    static final Node<String> DATEADD_SECOND_NODE = node("dateadd('S', ");
    static final Node<String> DATEADD_MINUTE_NODE = node("dateadd('N', ");
    static final Node<String> DATEADD_HOUR_NODE = node("dateadd('HH', ");
    static final Node<String> DATEADD_DAY_NODE = node("dateadd('D', ");
    static final Node<String> DATEADD_MONTH_NODE = node("dateadd('M', ");
    static final Node<String> DATEADD_YEAR_NODE = node("dateadd('YY', ");
    static final Node<String> COALESCE_NODE = node("coalesce(");
    static final Node<String> BRACKET_R_PLUS_BRACKET_L_NODE = node(") + (");
    static final Node<String> DUAL = node("dual");
    static final Node<String> SELECT_ONE_NODE = node("(select 1 f)");
    static final Node<String> SELECT_ONE_FROM_DUAL_NODE = node("(select 1 f from dual)");
    static final Node<String> F_NODE = node("f");
    static final Node<String> MOD_BRACKET_L_NODE = node("mod(");
    static final Node<String> CASE_WHEN_NODE = node("case when ");
    static final Node<String> THEN_TRUE_ELSE_FALSE_END_NODE = node(" then true else false end");
    static final Node<String> THEN_ONE_ELSE_ZERO_END_NODE = node(" then 1 else 0 end");
    static final Node<String> CURRENT_TIMESTAMP_NODE = node("current_timestamp");
    static final Node<String> DATE_FORMAT_BRACKET_R_NODE = node(", 'YYYY-MM-DD')");
    static final Node<String> DATETIME_FORMAT_BRACKET_R_NODE = node(", 'YYYY-MM-DD\"T\"HH24:MI:SS.FF6')");
    static final Node<String> OFFSET_DATETIME_FORMAT_BRACKET_R_NODE = node(", 'YYYY-MM-DD\"T\"HH24:MI:SS.FF6TZH:TZM')");
    static final Node<String> OFFSET_DATETIME_AS_STRING_PART1_NODE = node("concat(to_char(");
    static final Node<String> OFFSET_DATETIME_AS_STRING_PART2_NODE = node(", 'YYYY-MM-DD\"T\"HH24:MI:SS.FF6'), nullif(insert(replace(to_char(extract(timezone_hour from ");
    static final Node<String> OFFSET_DATETIME_AS_STRING_PART3_NODE = node(") * 1000 + extract(timezone_minute from ");
    static final Node<String> OFFSET_DATETIME_AS_STRING_PART4_NODE = node("), '00000'), ' ', '+'), 4, 1, ':'), ':'))");
    static final Node<String> OFFSET_DATETIME_AS_STRING_PART2_1_NODE = node("to_char(cast(");
    static final Node<String> OFFSET_DATETIME_AS_STRING_PART2_2_NODE = node(" as timestamp with time zone), 'YYYY-MM-DD\"T\"HH24:MI:SS.FF6TZH:TZM')");
    static final Node<String> EXTRACT_YEAR_FROM_NODE = node("extract(year from ");
    static final Node<String> EXTRACT_MONTH_FROM_NODE = node("extract(month from ");
    static final Node<String> EXTRACT_DAY_FROM_NODE = node("extract(day from ");
    static final Node<String> EXTRACT_HOUR_FROM_NODE = node("extract(hour from ");
    static final Node<String> EXTRACT_MINUTE_FROM_NODE = node("extract(minute from ");
    static final Node<String> EXTRACT_SECOND_FROM_NODE = node("extract(second from ");
    static final Node<String> GET_SECOND_PART1_NODE = node("(extract(second from ");
    static final Node<String> GET_SECOND_PART2_NODE = node(") + extract(nanosecond from ");
    static final Node<String> GET_SECOND_PART3_NODE = node(") / 1000000000.0)");
    static final Node<String> EXTRACT_TIMEZONE_HOUR_FROM_NODE = node("extract(timezone_hour from ");
    static final Node<String> EXTRACT_TIMEZONE_MINUTE_FROM_NODE = node("extract(timezone_minute from ");
    static final Node<String> CAST_TIME_AS_STRING_NODE = node(", 'HH24:MI:SS.FF6')");
    static final Node<String> CAST_NODE = node("cast(");
    static final Node<String> AS_TIME_NODE = node(" as time(6))");
    static final Node<String> AS_DATE_NODE = node(" as date)");
    static final Node<String> AS_DATETIME_NODE = node(" as timestamp(6))");
    static final Node<String> TRUNC_NODE = node("trunc(");
    static final Node<String> GET_OFFSET_NODE = node(", 'TZH:TZM')");
    static final Node<String> GET_OFFSET_PART1_NODE = node("nullif(insert(replace(to_char(extract(timezone_hour from ");
    static final Node<String> GET_OFFSET_PART2_NODE = node(") * 1000 + extract(timezone_minute from ");
    static final Node<String> GET_OFFSET_PART3_NODE = node("), '00000'), ' ', '+'), 4, 1, ':'), ':')");
    static final Node<String> GET_OFFSET_PART2_1_NODE = node("to_char(cast(");
    static final Node<String> GET_OFFSET_PART2_2_NODE = node(" as timestamp with time zone), 'TZH:TZM')");
    static final Node<String> BIT_NOT_NODE = node("~");
    static final Node<String> BIT_AND_NODE = node(" & ");
    static final Node<String> BIT_OR_NODE = node(" | ");
    static final Node<String> BIT_XOR_NODE = node(" # ");
    static final Node<String> SHIFT_LEFT_NODE = node(" << ");
    static final Node<String> SHIFT_RIGHT_NODE = node(" >> ");
    static final Node<String> BIT_NOT_2_NODE = node("bitnot(");
    static final Node<String> BIT_AND_2_NODE = node("bitand(");
    static final Node<String> BIT_OR_2_NODE = node("bitor(");
    static final Node<String> BIT_XOR_2_NODE = node("bitxor(");
    static final Node<String> SHIFT_LEFT_2_NODE = node("lshift(");
    static final Node<String> SHIFT_RIGHT_2_NODE = node("rshift(");
    static final Node<String> BIT_NOT_PART1_NODE = node("(-");
    static final Node<String> BIT_NOT_PART2_NODE = node(" - 1)");
    static final Node<String> TWO_NODE = node("2");
    static final Node<String> POWER_NODE = node("power(");
    static final Node<String> INT4_NODE = node("::int4");
    static final Node<String> LPAD_NODE = node("lpad(");
    static final Node<String> RPAD_NODE = node("rpad(");
    static final Node<String> LOG_NODE = node("log(");
    static final Node<String> ANY_NODE = node("any");
    static final Node<String> ALL_NODE = node("all");
    static final Node<String> VARCHAR_NODE = node("::varchar");
    static final Node<String> BIGINT_NODE = node("::bigint");
    static final Node<String> DATE_NODE = node("::date");
    static final Node<String> TIMESTAMP_NODE = node("::timestamp");
    static final Node<String> TIME_NODE = node("::time(6)");
    static final Node<String> BOOL_NODE = node("::bool");

    private Helper() {
    }

    /**
     * Get the schema name node
     *
     * @param schemaName The name of the schema
     */
    static Node<String> getSchemaNameNode(String schemaName) {
        return schemaName == null ? null : node(schemaName + ".");
    }

    /**
     * Get request node
     *
     * @param requestJson JSON of the request
     */
    static JsonNode getRequestNode(String requestJson) {
        checkNotNull(requestJson, "JSON request");
        return wrap(() -> Helper.OBJECT_MAPPER.readTree(requestJson), throwable -> new ParseJsonException(throwable, requestJson));
    }

    /**
     * Get request data
     *
     * @param modelDescription          Model description
     * @param expressionsProcessor      The expressions processor
     * @param securityDriver            Security driver
     * @param sqlDialect                Диалект SQL
     * @param defaultLimit              The default limit
     * @param schemaNameNode            The node of the naming scheme
     * @param maxSecurityRecursionDepth The maximum recursion depth for security
     * @param tableQueryProvider        Table query provider
     * @param optimizeJoins             Optimize joins
     * @param requestNode               Request node
     * @param params                    Parameters
     */
    static RequestData getRequestData(ModelDescription modelDescription, ExpressionsProcessor expressionsProcessor, SecurityDriver securityDriver, SqlDialect sqlDialect, Integer defaultLimit, Node<String> schemaNameNode, int maxSecurityRecursionDepth, TableQueryProvider tableQueryProvider, boolean optimizeJoins, JsonNode requestNode, Map<String, Object> params) {
        checkNotNull(requestNode, "Request node");
        RequestData result = new RequestData();
        result.modelDescription = modelDescription;
        result.expressionsProcessor = expressionsProcessor;
        result.securityDriver = securityDriver;
        result.sqlDialect = sqlDialect;
        result.defaultLimit = defaultLimit;
        result.schemaNameNode = schemaNameNode;
        result.maxSecurityRecursionDepth = maxSecurityRecursionDepth;
        result.tableQueryProvider = tableQueryProvider;
        result.optimizeJoins = optimizeJoins;
        result.requestNode = requestNode;
        result.params = params;
        result.sqlQueryProcessors = new ArrayList<>(1);
        result.lastTableIndexPointer = new Pointer<>(-1);
        result.lastParameterIndexPointer = new Pointer<>(-1);
        result.mapSqlParameterSource = new MapSqlParameterSource();
        result.columnTypes = new ArrayList<>(1);
        result.columnTypes.add(DataType.INTEGER);
        result.primitiveColumnIndexes = new EnumMap<>(DataType.class);
        result.countColumnIndexPointer = new Pointer<>();
        result.commonTableNodes = new ArrayList<>();
        result.calculatedExpressions = new LinkedHashMap<>();
        result.allSqlQueryProcessors = new ArrayList<>();
        result.startSqlQueryProcessor = new SqlQueryProcessor(result);
        result.startSqlQueryProcessor.process();
        return result;
    }

    /**
     * Check type
     *
     * @param expectedTypes              Expected types
     * @param primitiveExpressionsStream Primitive expressions stream
     */
    static void checkType(Set<DataType> expectedTypes, Function<PrimitiveExpressionImpl, UnsupportedOperationException> exceptionInitializer, Stream<PrimitiveExpressionImpl> primitiveExpressionsStream) {
        primitiveExpressionsStream
                .filter(primitiveExpression -> !expectedTypes.contains(primitiveExpression.type))
                .findAny()
                .ifPresent(primitiveExpression -> {
                    throw exceptionInitializer.apply(primitiveExpression);
                });
    }

    /**
     * Process entity
     *
     * @param entity                      Entity
     * @param sqlQueryProcessor           SQL query handler
     * @param entityDescription           Entity description
     * @param specificationWithEntityType The specification with the entity type
     */
    static void processEntity(EntityImpl entity, SqlQueryProcessor sqlQueryProcessor, EntityDescription entityDescription, SpecificationWithEntityType<?> specificationWithEntityType) {
        entity.entitySqlQueryProcessor = sqlQueryProcessor;
        entity.entityDescription = entityDescription.cast(specificationWithEntityType == null ? null : specificationWithEntityType.getType());
        if (!Objects.equals(entityDescription, entity.entityDescription)) {
            // If an extension of the object is set, then it is necessary to use the requirement of equality of the type of the found object to the type that we explicitly passed
            if (entity.entityDescription.getRootEntityDescription().getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE) {
                ColumnData typeColumnData = sqlQueryProcessor.getTypeColumnData();
                typeColumnData.inherit = true;
                // In case of inheritance as SINGLE_TABLE to the condition we add type in (...)
                entity.getConditionStringNodeFunction = () -> node(typeColumnData.columnNode, entity.entityDescription.getMetaDataManager().get(EntityDescriptionMetaData.class).inHeirTypesStringNode);
            } else {
                // Inside the method, we get the column with the identifier, in the FROM section, a JOIN is added with the descendant table, it is assigned a pseudonym, everything is beautifully done.
                ColumnData localIdData = sqlQueryProcessor.getLocalIdColumnData(entity.entityDescription);
                localIdData.inherit = true;
                //In the case of inheritance JOINED, it is necessary to verify that there is a record with such an identifier in that table.
                entity.getConditionStringNodeFunction = () -> node(localIdData.columnNode, IS_NOT_NULL_NODE);
            }
        }
        entity.getIdConditionStringNodeFunction = entity.getConditionStringNodeFunction;
        if (sqlQueryProcessor.reference) {
            Supplier<Node<String>> getConditionStringNodeFunction = entity.getConditionStringNodeFunction;
            entity.getConditionStringNodeFunction = () -> {
                Node<String> conditionStringNode = getConditionStringNodeFunction.get();
                Node<String> conditionStringNode2 = sqlQueryProcessor.securityFlagColumnData == null || sqlQueryProcessor.requestData.referenceSecurityConditionInitialization ? null : (node(sqlQueryProcessor.securityFlagColumnData.columnNode, Helper.EQ_NODE, Helper.ONE_NODE));
                if (conditionStringNode == null)
                    return conditionStringNode2;
                else if (conditionStringNode2 == null) {
                    return conditionStringNode;
                } else {
                    return getAndStringNode(conditionStringNode, conditionStringNode2);
                }
            };
        }
    }

    /**
     * Process alias
     *
     * @param expressionContext      The context of the expression
     * @param entity                 Entity
     * @param specificationWithAlias Specification with alias
     */
    static void processAlias(ExpressionContext expressionContext, EntityImpl entity, SpecificationWithAlias<?> specificationWithAlias) {
        if (specificationWithAlias != null && specificationWithAlias.getAlias() != null) {
            AliasedEntityData aliasedEntityData = new AliasedEntityData(entity.entitySqlQueryProcessor, entity.entityDescription);
            aliasedEntityData.nullable = true;
            expressionContext.aliasedEntitiesData = new HashMap<>(expressionContext.aliasedEntitiesData);
            expressionContext.aliasedEntitiesData.put(specificationWithAlias.getAlias(), aliasedEntityData);
        }
    }

    /**
     * Process entity collection specification
     *
     * @param sqlQueryProcessor  SQL query handler
     * @param entitiesCollection The collection of entities
     * @param specification      Specification
     * @param <S>                Type of specification
     */
    static <S extends SpecificationWithCondition<?> & SpecificationWithElementAlias<?>> void processEntitiesCollectionSpecification(SqlQueryProcessor sqlQueryProcessor, ExpressionContext expressionContext, EntitiesCollectionImpl entitiesCollection, S specification) {
        if (specification != null && specification.getCondition() != null) {
            entitiesCollection.condition = (ConditionImpl) specification.getCondition();
            ExpressionContext entitiesCollectionExpressionContext = new ExpressionContext(entitiesCollection.elementSqlQueryProcessor);
            entitiesCollectionExpressionContext.aliasedEntitiesData = expressionContext.aliasedEntitiesData;
            if (specification.getElementAlias() != null) {
                entitiesCollectionExpressionContext.aliasedEntitiesData = new HashMap<>(entitiesCollectionExpressionContext.aliasedEntitiesData);
                entitiesCollectionExpressionContext.aliasedEntitiesData.put(specification.getElementAlias(), new AliasedEntityData(entitiesCollection.elementSqlQueryProcessor, entitiesCollection.elementSqlQueryProcessor.entityDescription));
            }
            entitiesCollection.condition.prepare(sqlQueryProcessor, entitiesCollectionExpressionContext);
        }
    }

    /**
     * Process additional condition
     *
     * @param sqlQueryProcessor  SQL query processor
     * @param entitiesCollection The collection of entities
     */
    static void processAdditionalCondition(SqlQueryProcessor sqlQueryProcessor, EntitiesCollectionImpl entitiesCollection) {
        Supplier<Node<String>> currentGetAdditionalConditionNodeFunction = entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object;
        entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object = () -> {
            List<Node<String>> nodes = new ArrayList<>(5);
            addNodeListToNodes(nodes, Helper.AND_NODE, Stream.concat(
                    Stream.of(sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object, currentGetAdditionalConditionNodeFunction)
                            .filter(Objects::nonNull)
                            .map(Supplier::get),
                    Stream.of(node(entitiesCollection.elementSqlQueryProcessor.idColumnData.columnNode, Helper.IS_NOT_NULL_NODE))));
            return node(nodes);
        };
    }

    /**
     * Get the function for retrieving the expression string node
     *
     * @param operationNode         Operation node
     * @param calculatedExpression1 Calculable expression 1
     * @param calculatedExpression2 Calculable expression 2
     */
    static Supplier<Node<String>> getGetExpressionStringNodeFunction(Node<String> operationNode, CalculatedExpression calculatedExpression1, CalculatedExpression calculatedExpression2) {
        return () -> node(Helper.BRACKET_L_NODE, calculatedExpression1.get(), operationNode, calculatedExpression2.get(), BRACKET_R_NODE);
    }

    /**
     * Get the function for retrieving the expression string node
     *
     * @param sqlQueryProcessor    SQL query handler
     * @param primitiveDescription Description of the primitive
     */
    static Supplier<Node<String>> getGetExpressionStringNodeFunction(SqlQueryProcessor sqlQueryProcessor, PrimitiveDescription primitiveDescription) {
        ColumnData primitiveColumnData = sqlQueryProcessor.getPrimitiveColumnData(primitiveDescription);
        primitiveColumnData.inherit = true;
        return () -> primitiveColumnData.columnNode;
    }

    /**
     * Get function for retrieving condition string node
     *
     * @param preparableExpression1 The expression requiring preparation, 1
     * @param preparableExpression2 The expression requiring preparation, 2
     */
    static Supplier<Node<String>> getGetConditionStringNodeFunction(PreparableExpression preparableExpression1, PreparableExpression preparableExpression2) {
        Supplier<Node<String>> currentGetConditionStringNodeFunction1 = preparableExpression1.getConditionStringNodeFunction;
        Supplier<Node<String>> currentGetConditionStringNodeFunction2 = preparableExpression2.getConditionStringNodeFunction;
        return () -> {
            Node<String> conditionStringNode1 = currentGetConditionStringNodeFunction1.get();
            Node<String> conditionStringNode2 = currentGetConditionStringNodeFunction2.get();
            if (conditionStringNode1 == null) {
                return conditionStringNode2;
            } else if (conditionStringNode2 == null) {
                return conditionStringNode1;
            }
            return getAndStringNode(conditionStringNode1, conditionStringNode2);
        };
    }

    /**
     * Get condition string node
     *
     * @param conditionStringNode  Condition string node
     * @param preparableExpression The expression that requires preparation
     */
    static Node<String> getConditionStringNode(Node<String> conditionStringNode, PreparableExpression preparableExpression) {
        Node<String> conditionStringNode2 = preparableExpression.getConditionStringNodeFunction.get();
        return conditionStringNode2 == null ? conditionStringNode : getAndStringNode(conditionStringNode, conditionStringNode2);
    }

    /**
     * Get column node
     *
     * @param tableAliasNode The table alias node
     * @param columnNameNode The node of the column name
     */
    static Node<String> getColumnNode(Node<String> tableAliasNode, Node<String> columnNameNode) {
// Returns a node with content in the form "tableName.columnName"
        return node(tableAliasNode, DOT_NODE, columnNameNode);
    }

    /**
     * Get the function for retrieving the primitive value
     *
     * @param getValueFunction The function for obtaining the value
     * @param <T>              The type of value
     */
    static <T> Function3<T, SqlDialect, ResultSet, Integer> getGetPrimitiveValueFunction(ThrowingFunction2<T, ResultSet, Integer> getValueFunction) {
        return (sqlDialect, resultSet, columnIndex) -> {
            T value = wrap((() -> getValueFunction.call(resultSet, columnIndex)));
            return Boolean.TRUE.equals(wrap(resultSet::wasNull)) ? null : value;
        };
    }

    /**
     * Get function for retrieving primitive value
     *
     * @param getValueFunction The function for obtaining the value
     * @param <T>              The type of value
     */
    static <T> Function3<T, SqlDialect, ResultSet, Integer> getGetPrimitiveValueFunction2(ThrowingFunction3<T, SqlDialect, ResultSet, Integer> getValueFunction) {
        return (sqlDialect, resultSet, columnIndex) -> {
            T value = wrap((() -> getValueFunction.call(sqlDialect, resultSet, columnIndex)));
            return Boolean.TRUE.equals(wrap(resultSet::wasNull)) ? null : value;
        };
    }

    /**
     * Get string node "I"
     *
     * @param stringNode2 Узел строки 2
     * @param stringNode2 String node 2
     */
    static Node<String> getAndStringNode(Node<String> stringNode1, Node<String> stringNode2) {
        return node(stringNode1, AND_NODE, stringNode2);
    }

    /**
     * Get node for primitive
     *
     * @param type  Тип
     * @param value The value
     */
    static JsonNode getPrimitiveNode(DataType type, Object value) {
        return value == null ? OBJECT_MAPPER.nullNode() : OBJECT_MAPPER.valueToTree(type.getMetaDataManager().get(DataTypeMetaData.class).convertValueFunction.apply(value));
    }

    /**
     * Get expression node with condition
     *
     * @param sqlDialect     Диалект SQL
     * @param expressionNode Expression node
     * @param conditionNode  Condition node
     */
    static Node<String> getExpressionWithConditionNode(SqlDialect sqlDialect, Node<String> expressionNode, Node<String> conditionNode) {
        if (conditionNode == null) {
            return expressionNode;
        }
        List<Node<String>> nodes = new ArrayList<>(7);
        nodes.add(Helper.BRACKET_L_SELECT_NODE);
        nodes.add(expressionNode);
        Node<String> dualNode = sqlDialect.dual();
        if (dualNode != null) {
            nodes.add(Helper.FROM_NODE);
            nodes.add(dualNode);
        }
        nodes.add(Helper.WHERE_NODE);
        nodes.add(conditionNode);
        nodes.add(Helper.BRACKET_R_NODE);
        return node(nodes);
    }
}
