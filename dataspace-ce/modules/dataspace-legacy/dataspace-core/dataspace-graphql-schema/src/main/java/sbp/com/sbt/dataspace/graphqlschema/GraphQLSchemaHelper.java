package sbp.com.sbt.dataspace.graphqlschema;

/**
 * Assistant for GraphQL schema
 */
public final class GraphQLSchemaHelper {

    public static final String MERGE_REQUEST_SPECIFICATION_DIRECTIVE_NAME = "mergeReqSpec";
    public static final String STRING_EXPRESSION_NAME = "strExpr";

    public static final String FLOAT4_SCALAR_TYPE_NAME = "_Float4";
    public static final String DATE_SCALAR_TYPE_NAME = "_Date";
    public static final String DATETIME_SCALAR_TYPE_NAME = "_DateTime";
    public static final String OFFSET_DATETIME_SCALAR_TYPE_NAME = "_OffsetDateTime";
    public static final String TIME_SCALAR_TYPE_NAME = "_Time";
    public static final String BYTE_ARRAY_SCALAR_TYPE_NAME = "_ByteArray";

    public static final String SORT_ORDER_ENUM_TYPE_NAME = "_SortOrder";

    public static final String SORT_CRITERION_SPECIFICATION_INPUT_OBJECT_TYPE_NAME = "_SortCriterionSpecification";

    public static final String ERROR_STRATEGY_OBJECT_TYPE_NAME = "_ErrorStrategy";
    public static final String UNAVAILABLE_ENUM_TYPE_NAME = "_Unavailable";
    public static final String INCOMPATIBLE_ENUM_TYPE_NAME = "_Incompatible";

    public static final String SHARD_ERROR_STAT_OBJECT_TYPE_NAME = "_ShardErrorStat";

    public static final String ENTITY_INTERFACE_TYPE_NAME = "_Entity";

    public static final String CHARACTER_COLLECTION_OBJECT_TYPE_NAME = "_CharCollection";
    public static final String STRING_COLLECTION_OBJECT_TYPE_NAME = "_StringCollection";
    public static final String BYTE_COLLECTION_OBJECT_TYPE_NAME = "_ByteCollection";
    public static final String SHORT_COLLECTION_OBJECT_TYPE_NAME = "_ShortCollection";
    public static final String INTEGER_COLLECTION_OBJECT_TYPE_NAME = "_IntCollection";
    public static final String LONG_COLLECTION_OBJECT_TYPE_NAME = "_LongCollection";
    public static final String FLOAT_COLLECTION_OBJECT_TYPE_NAME = "_Float4Collection";
    public static final String DOUBLE_COLLECTION_OBJECT_TYPE_NAME = "_FloatCollection";
    public static final String BIG_DECIMAL_COLLECTION_OBJECT_TYPE_NAME = "_BigDecimalCollection";
    public static final String DATE_COLLECTION_OBJECT_TYPE_NAME = "_DateCollection";
    public static final String DATETIME_COLLECTION_OBJECT_TYPE_NAME = "_DateTimeCollection";
    public static final String OFFSET_DATETIME_COLLECTION_OBJECT_TYPE_NAME = "_OffsetDateTimeCollection";
    public static final String TIME_COLLECTION_OBJECT_TYPE_NAME = "_TimeCollection";
    public static final String BOOLEAN_COLLECTION_OBJECT_TYPE_NAME = "_BooleanCollection";
    public static final String BYTE_ARRAY_COLLECTION_OBJECT_TYPE_NAME = "_ByteArrayCollection";
    public static final String MERGED_ENTITIES_COLLECTION_OBJECT_TYPE_NAME = "_MergedEntitiesCollection";
    public static final String MULTI_MERGED_ENTITIES_COLLECTION_OBJECT_TYPE_NAME = "_MultiMergedEntitiesCollection";
    public static final String QUERY_OBJECT_TYPE_NAME = "_Query";
    public static final String CALCULATION_OBJECT_TYPE_NAME = "_Calculation";

    public static final String ID_ARGUMENT_NAME = "id";
    public static final String DATE_ARGUMENT_NAME = "date";
    public static final String CONDITION_ARGUMENT_NAME = "cond";
    public static final String CRITERIA_ARGUMENT_NAME = "criteria";
    public static final String GROUP_ARGUMENT_NAME = "group";
    public static final String GROUP_COND_ARGUMENT_NAME = "groupCond";
    public static final String LIMIT_ARGUMENT_NAME = "limit";
    public static final String OFFSET_ARGUMENT_NAME = "offset";
    public static final String SORT_ARGUMENT_NAME = "sort";
    public static final String ALIAS_ARGUMENT_NAME = "alias";
    public static final String ELEMENT_ALIAS_ARGUMENT_NAME = "elemAlias";
    public static final String EXPRESSION_ARGUMENT_NAME = "expression";
    public static final String EXPR_ARGUMENT_NAME = "expr";
    public static final String DISTINCT_ARGUMENT_NAME = "distinct";
    public static final String PARAMS_ARGUMENT_NAME = "params";
    //Argument for context propagation in cross-shard search
    public static final String MULTISEARCH_CONTEXT_NAME = "ctx";

    public static final String CRITERION_INPUT_OBJECT_FIELD_NAME = "crit";
    public static final String ORDER_INPUT_OBJECT_FIELD_NAME = "order";
    public static final String NULLS_LAST_OBJECT_FIELD_NAME = "nullsLast";

    public static final String ID_FIELD_NAME = "id";
    public static final String ID_WITH_UNDERSCORE_FIELD_NAME = "_id";
    public static final String AGGREGATE_VERSION_FIELD_NAME = "aggVersion";
    public static final String AGGREGATE_VERSION_WITH_UNDERSCORE_FIELD_NAME = "_aggVersion";
    public static final String ELEMENTS_FIELD_NAME = "elems";
    public static final String COUNT_FIELD_NAME = "count";
    public static final String MERGE_FIELD_NAME = "merge";
    public static final String PACKET_FIELD_NAME = "packet";
    public static final String MULTI_MERGE_FIELD_NAME = "multimerge";

    public static final String ASCENDING_ENUM_VALUE = "ASC";
    public static final String DESCENDING_ENUM_VALUE = "DESC";

    public static final String ERROR_STRATEGY_NAME = "errorStrategy";

    public static final String UNAVAILABLE_OBJECT_FIELD_NAME = "unavailable";
    public static final String INCOMPATIBLE_OBJECT_FIELD_NAME = "incompatible";
    public static final String RETRY_COUNT_OBJECT_FIELD_NAME = "retryCount";
    public static final String RETRY_INTERVAL_MS_OBJECT_FIELD_NAME = "retryIntervalMs";

    public static final String FAIL_ENUM_VALUE = "FAIL";
    public static final String RETRY_ENUM_VALUE = "RETRY";
    public static final String IGNORE_ENUM_VALUE = "IGNORE";

    public static final String SHARD_ERROR_STAT_NAME = "shardErrorStat";

    public static final String PERCENT_SKIPPED_OBJECT_FIELD_NAME = "percentSkipped";
    public static final String PERCENT_UNAVAILABLE_OBJECT_FIELD_NAME = "percentUnavailable";
    public static final String PERCENT_INCOMPATIBLE_OBJECT_FIELD_NAME = "percentIncompatible";

    public static final String ENTITY_OBJECT_TYPE_PREFIX = "_E_";
    public static final String ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX = "_EC_";
    public static final String GROUP_OBJECT_TYPE_PREFIX = "_G_";
    public static final String ENUM_OBJECT_TYPE_PREFIX = "_EN_";
    public static final String ENUM_COLLECTION_OBJECT_TYPE_PREFIX = "_ENC_";
    public static final String SQL_QUERY_OBJECT_TYPE_PREFIX = "_Q_";
    public static final String SQL_QUERY_PARAMS_TYPE_PREFIX = "_QP_";
    public static final String SQL_QUERY_PARAMS_TYPE_SUFFIX = "Params";
    public static final String SEARCH_FIELD_NAME_PREFIX = "search";
    public static final String SQL_QUERY_FIELD_NAME_PREFIX = "search";
    // Prefix for search API, e.g.: multisearchProduct(...)
    public static final String MULTISEARCH_FIELD_NAME_PREFIX = "multisearch";
    public static final String SPECIAL_FLAG_FIELD_NAME = "_specialFlag";
    public static final String CALC_FIELD_NAME = "_calc";
    public static final String GET_PREFIX = "_get";
    public static final String MULTISEARCH_ENTITIES_COLLECTION_OBJECT_TYPE_PREFIX = "_ECM_";

    public static final String SUBSCRIPTION_TYPE_NAME = "_Subscription";
    public static final String SUBSCRIPTION_TYPE_ENTITY_FIELD_SUFFIX = "Changed";
    public static final String EVENT_SUBSCRIPTION_TYPE_FIELD_SUFFIX = "Subscription";

    public static final String ENTITY_SUBSCRIPTION_TYPE_PREFIX = "_CHANGE_";
    public static final String EVENT_SUBSCRIPTION_TYPE_PREFIX = "_SUBSCRIPTION_";
    public static final String ENTITY_SUBSCRIPTION_TYPE_ELEMENT_FIELD_NAME = "state";

    private GraphQLSchemaHelper() {
    }
}
