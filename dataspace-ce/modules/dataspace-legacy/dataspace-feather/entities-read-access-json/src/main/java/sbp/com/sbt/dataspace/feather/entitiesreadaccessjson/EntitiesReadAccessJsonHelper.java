package sbp.com.sbt.dataspace.feather.entitiesreadaccessjson;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Assistant for reading entity access via JSON
 */
public final class EntitiesReadAccessJsonHelper {

    public static final String REQUESTS_MERGE_FIELD_NAME = "merge";
    public static final String TYPE_FIELD_NAME = "type";
    public static final String PARAMS_FIELD_NAME = "params";
    public static final String PROPERTIES_SELECTION_FIELD_NAME = "selection";
    public static final String DISTINCT_FIELD_NAME = "distinct";
    public static final String PROPERTIES_FIELD_NAME = "props";
    public static final String DETAILS_FIELD_NAME = "details";
    public static final String CONDITION_FIELD_NAME = "cond";
    public static final String SECURITY_CONDITION_FIELD_NAME = "secCond";
    public static final String GROUP_FIELD_NAME = "group";
    public static final String GROUP_CONDITION_FIELD_NAME = "groupCond";
    public static final String LIMIT_FIELD_NAME = "limit";
    public static final String OFFSET_FIELD_NAME = "offset";
    public static final String COUNT_FIELD_NAME = "count";
    public static final String SORT_FIELD_NAME = "sort";
    public static final String AGGREGATE_VERSION_FIELD_NAME = "aggVersion";
    public static final String CRITERION_FIELD_NAME = "crit";
    public static final String ORDER_FIELD_NAME = "order";
    public static final String NULLS_LAST_FIELD_NAME = "nullsLast";
    public static final String LOCK_FIELD_NAME = "lock";
    public static final String BASE_PROPERTY_FIELD_NAME = "base";
    public static final String SPECIFICATION_FIELD_NAME = "spec";
    public static final String CALCULATED_EXPRESSION_FIELD_NAME = "calc";
    public static final String ALIAS_FIELD_NAME = "alias";
    public static final String ELEMENT_ALIAS_FIELD_NAME = "elemAlias";
    public static final String ELEMENTS_FIELD_NAME = "elems";
    public static final String VALUE_FIELD_NAME = "value";
    public static final String ID_FIELD_NAME = "id";
    public static final String INVALID_FIELD_NAME = "invalid";
    public static final String INCORRECT_CASTED_FIELD_NAME = "incorrectCasted";
    public static final String ACCESS_FIELD_NAME = "access";

    public static final String ASCENDING = "asc";
    public static final String DESCENDING = "desc";
    public static final String WAIT = "wait";
    public static final String NOWAIT = "nowait";

    public static final Set<String> REQUEST_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(REQUESTS_MERGE_FIELD_NAME, TYPE_FIELD_NAME, PARAMS_FIELD_NAME, PROPERTIES_SELECTION_FIELD_NAME, DISTINCT_FIELD_NAME, PROPERTIES_FIELD_NAME, DETAILS_FIELD_NAME, AGGREGATE_VERSION_FIELD_NAME, CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, GROUP_FIELD_NAME, GROUP_CONDITION_FIELD_NAME, LIMIT_FIELD_NAME, OFFSET_FIELD_NAME, COUNT_FIELD_NAME, SORT_FIELD_NAME, LOCK_FIELD_NAME)));
    public static final Set<String> REQUESTS_MERGE_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(REQUESTS_MERGE_FIELD_NAME, LIMIT_FIELD_NAME, OFFSET_FIELD_NAME, COUNT_FIELD_NAME, SORT_FIELD_NAME)));
    public static final Set<String> ZERO_LIMIT_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(REQUESTS_MERGE_FIELD_NAME, TYPE_FIELD_NAME, CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, LIMIT_FIELD_NAME, COUNT_FIELD_NAME)));
    public static final Set<String> PROPERTIES_SELECTION_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(TYPE_FIELD_NAME, PARAMS_FIELD_NAME, PROPERTIES_SELECTION_FIELD_NAME, DISTINCT_FIELD_NAME, CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, GROUP_FIELD_NAME, GROUP_CONDITION_FIELD_NAME, LIMIT_FIELD_NAME, OFFSET_FIELD_NAME, COUNT_FIELD_NAME, SORT_FIELD_NAME)));
    public static final Set<String> LOCK_REQUEST_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(TYPE_FIELD_NAME, PARAMS_FIELD_NAME, CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, LIMIT_FIELD_NAME, OFFSET_FIELD_NAME, LOCK_FIELD_NAME)));
    public static final Set<String> MERGE_REQUEST_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(TYPE_FIELD_NAME, PARAMS_FIELD_NAME, PROPERTIES_FIELD_NAME, DETAILS_FIELD_NAME, CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, AGGREGATE_VERSION_FIELD_NAME)));
    public static final Set<String> PRIMITIVES_COLLECTION_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, LIMIT_FIELD_NAME, OFFSET_FIELD_NAME, COUNT_FIELD_NAME, SORT_FIELD_NAME)));
    public static final Set<String> REFERENCE_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(TYPE_FIELD_NAME, ALIAS_FIELD_NAME, PROPERTIES_FIELD_NAME, DETAILS_FIELD_NAME, AGGREGATE_VERSION_FIELD_NAME)));
    public static final Set<String> REFERENCES_COLLECTION_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(TYPE_FIELD_NAME, ELEMENT_ALIAS_FIELD_NAME, PROPERTIES_FIELD_NAME, DETAILS_FIELD_NAME, AGGREGATE_VERSION_FIELD_NAME, CONDITION_FIELD_NAME, SECURITY_CONDITION_FIELD_NAME, LIMIT_FIELD_NAME, OFFSET_FIELD_NAME, COUNT_FIELD_NAME, SORT_FIELD_NAME)));
    public static final Set<String> SORT_CRITERION_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(CRITERION_FIELD_NAME, ORDER_FIELD_NAME, NULLS_LAST_FIELD_NAME)));
    public static final Set<String> ALIASED_PROPERTY_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(BASE_PROPERTY_FIELD_NAME, SPECIFICATION_FIELD_NAME)));
    public static final Set<String> CALCULATED_EXPRESSION_SPECIFICATION_FIELD_NAMES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(CALCULATED_EXPRESSION_FIELD_NAME)));

    private EntitiesReadAccessJsonHelper() {
    }
}
