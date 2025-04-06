package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import sbp.com.sbt.dataspace.feather.common.CommonHelper;
import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.common.Function3;
import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.common.Procedure5;
import sbp.com.sbt.dataspace.feather.common.Procedure7;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ParamDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitivesCollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferencesCollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;
import sbp.com.sbt.dataspace.feather.stringexpressions.StringConditionBuilder;
import sbp.com.sbt.dataspace.feather.stringexpressions.StringPrimitiveExpressionBuilder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.*;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getAndStringNode;

/**
 * SQL query handler
 */
class SqlQueryProcessor {

    static final Set<String> EXPECTED_SORT_ORDERS = new LinkedHashSet<>(Arrays.asList(EntitiesReadAccessJsonHelper.ASCENDING, EntitiesReadAccessJsonHelper.DESCENDING));
    static final Set<String> EXPECTED_LOCK_MODES = new LinkedHashSet<>(Arrays.asList(EntitiesReadAccessJsonHelper.WAIT, EntitiesReadAccessJsonHelper.NOWAIT));
    static final PreparedList<Node<String>> TABLE_ALIAS_NODES;
    static final PreparedList<Node<String>> COLUMN_ALIAS_NODES;
    static final PreparedList<Node<String>> PARAMETER_NAME_NODES;
    static final PreparedList<Node<String>> QUERY_ID_NODES;
    static final PreparedList<Node<String>> COMMON_TABLE_ALIAS_NODES;
    static final BiFunction<ResultSet, Integer, Long> GET_LONG_FUNCTION = (resultSet, columnIndex) -> {
        long result = wrap(() -> resultSet.getLong(columnIndex));
        return wrap(resultSet::wasNull) ? null : result;
    };
    static final Object NOT_SET_PARAM_VALUE = new Object();
    static final DateTimeFormatter ORACLE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");

    RequestData requestData;
    SqlQueryProcessor parent;
    List<SqlQueryProcessor> children = Collections.emptyList();
    Set<Integer> mergeRequestQueryIds;

    String ownerTypeName = "";
    JsonNode paramsNode;
    Map<String, Object> parameters;
    EntityDescription entityDescription;
    boolean reference;
    boolean idOnly = true;
    SqlQueryProcessor aliasedOwner;
    Map<EntityDescription, TableData> tablesData;
    List<ColumnData> workColumnsData;
    ColumnData idColumnData;
    Pointer<UnaryOperator<Node<String>>> getTableNodeFunctionPointer;
    Map<EntityDescription, ColumnData> localIdColumnsData;
    Map<PrimitiveDescription, ColumnData> primitiveColumnsData;
    Map<ReferenceDescription, SqlQueryProcessor> referenceSqlQueryProcessors;
    Map<ReferenceDescription, SqlQueryProcessor> backReferenceReferenceSqlQueryProcessors;
    Map<String, SqlQueryProcessor> aliasedSqlQueryProcessors;
    Map<String, PropertyData> propertiesData = new HashMap<>();
    ColumnData typeColumnData;
    Map<String, AliasedEntityData> aliasedEntitiesData;
    Pointer<Supplier<Node<String>>> getAdditionalConditionNodeFunctionPointer;

    Map<Integer, ColumnData> columnsData;
    Map<Integer, ColumnData> collectionOwnerColumnsData = new LinkedHashMap<>();
    List<BiConsumer<EntityData, ResultSet>> recordProcessors;
    List<BiConsumer<EntityData, List<String>>> finalProcessors;
    ExpressionContext expressionContext;
    ConditionImpl condition;
    List<PrimitiveExpressionImpl> group;
    ConditionImpl groupCond;
    Integer limit;
    Integer offset;
    boolean nullLimitAndOffset = true;
    boolean count;
    UnaryOperator<Node<String>> getCountTableNodeFunction;
    List<SortCriterionData> sortCriteriaData = Collections.emptyList();
    int queryId;
    boolean added;
    List<ConditionImpl> securityConditions;
    Map<EntityDescription, ConditionImpl> securityConditions2;
    ColumnData securityFlagColumnData;
    int securityFlagColumnIndex;
    boolean distinct;
    boolean mandatory;
    EntityDescription mandatoryEntityDescription;
    LockMode lockMode;

    Node<String> tableStringNode;
    Node<String> conditionStringNode;
    Map<Integer, ColumnData> inheritedColumnsData;
    Node<String> mergeRequestQueryIdNode;
    Node<String> queryNode;

    Map<List<String>, Integer> counts;
    Map<List<String>, Map<String, SpecialSortedSet<Object>>> collections;
    Map<List<String>, Map<String, Integer>> inheritanceDistances = new HashMap<>();

    SqlQueryProcessor() {
    }

    /**
     * @param requestData Request data
     */
    SqlQueryProcessor(RequestData requestData) {
        this.requestData = requestData;
        this.requestData.allSqlQueryProcessors.add(this);
    }

    /**
     * @param parent Parent SQL query handler
     */
    SqlQueryProcessor(SqlQueryProcessor parent) {
        requestData = parent.requestData;
        requestData.allSqlQueryProcessors.add(this);
        this.parent = parent;
        parent.children.add(this);
    }

    /**
     * Check if the node is an object
     *
     * @param node Node
     */
    void checkIsObject(JsonNode node) {
        if (!node.isObject()) {
            throw new NotObjectNodeException(node.toString());
        }
    }

    /**
     * Check if the node is an array
     *
     * @param node Node
     */
    void checkIsArray(JsonNode node) {
        if (!node.isArray()) {
            throw new NotArrayNodeException(node.toString());
        }
    }

    /**
     * Check the field names
     *
     * @param objectNode         Object node
     * @param expectedFieldNames Expected field names
     */
    void checkFieldNames(JsonNode objectNode, Set<String> expectedFieldNames) {
        objectNode.fieldNames().forEachRemaining(fieldName -> {
            if (!expectedFieldNames.contains(fieldName)) {
                throw new UnexpectedFieldException(objectNode.toString(), fieldName, expectedFieldNames);
            }
        });
    }

    /**
     * Check unnecessary fields
     *
     * @param node                 Node
     * @param fieldNames           Field names
     * @param exceptionInitializer Exception initializer
     */
    void checkExtraFields(JsonNode node, Set<String> fieldNames, BiFunction<Set<String>, String, FeatherException> exceptionInitializer) {
        Set<String> extraFieldNames = new LinkedHashSet<>();
        node.fieldNames().forEachRemaining(fieldName -> {
            if (!fieldNames.contains(fieldName)) {
                extraFieldNames.add(fieldName);
            }
        });
        if (!extraFieldNames.isEmpty()) {
            throw exceptionInitializer.apply(extraFieldNames, node.toString());
        }
    }

    /**
     * Get string
     *
     * @param node Node
     */
    String getString(JsonNode node) {
        if (!node.isTextual()) {
            throw new NotStringNodeException(node.toString());
        }
        return node.textValue();
    }

    /**
     * Get an integer number
     *
     * @param node Node
     */
    int getInteger(JsonNode node) {
        if (!node.isInt()) {
            throw new NotIntegerNodeException(node.toString());
        }
        return node.intValue();
    }

    /**
     * Get a logical value
     *
     * @param node Node
     */
    boolean getBoolean(JsonNode node) {
        if (!node.isBoolean()) {
            throw new NotBooleanNodeException(node.toString());
        }
        return node.booleanValue();
    }

    /**
     * Get entity type
     *
     * @param specificationNode The specification node
     */
    String getEntityType(JsonNode specificationNode) {
        JsonNode typeNode = specificationNode.get(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME);
        return typeNode == null ? null : getString(typeNode);
    }

    /**
     * Obtain required entity type
     *
     * @param specificationNode The specification node
     */
    String getRequiredEntityType(JsonNode specificationNode) {
        String result = getEntityType(requestData.requestNode);
        if (result == null) {
            throw new EntityTypeNotSetException();
        }
        return result;
    }

    /**
     * Get alias table node
     */
    Node<String> getTableAliasNode() {
        // Returns "aliasPrefix#", where aliasPrefix is set when creating {@link #TABLE_ALIAS_NODES},
        // а # - number that increases each time the method is called
        return TABLE_ALIAS_NODES.get(++requestData.lastTableIndexPointer.object);
    }

    /**
     * Get alias nodes columns
     *
     * @param size Размер
     */
    List<Node<String>> getColumnAliasNodes(int size) {
        COLUMN_ALIAS_NODES.get(size - 1);
        return COLUMN_ALIAS_NODES.list;
    }

    /**
     * Get column node with alias
     *
     * @param columnNode The column node
     * @param aliasNode  Alias node
     */
    Node<String> getColumnWithAliasNode(Node<String> columnNode, Node<String> aliasNode) {
        return node(columnNode, Helper.SPACE_NODE, aliasNode);
    }

    /**
     * Get value
     *
     * @param valueNode Value node
     * @param type      Тип
     */
    Object getValue(JsonNode valueNode, DataType type) {
        Object result;
        if (type == DataType.CHARACTER) {
            String string = getString(valueNode);
            if (string.length() != 1) {
                throw new NotCharacterNodeException(valueNode.toString());
            }
            result = string;
        } else if (type == DataType.STRING) {
            result = getString(valueNode);
        } else if (type == DataType.BYTE) {
            result = Byte.valueOf(getString(valueNode));
        } else if (type == DataType.SHORT) {
            result = Short.valueOf(getString(valueNode));
        } else if (type == DataType.INTEGER) {
            result = Integer.valueOf(getString(valueNode));
        } else if (type == DataType.LONG) {
            result = Long.valueOf(getString(valueNode));
        } else if (type == DataType.FLOAT) {
            result = Float.valueOf(getString(valueNode));
        } else if (type == DataType.DOUBLE) {
            result = Float.valueOf(getString(valueNode));
        } else if (type == DataType.BIG_DECIMAL) {
            result = new BigDecimal(getString(valueNode));
        } else if (type == DataType.DATE) {
            result = LocalDate.parse(getString(valueNode), DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (type == DataType.DATETIME) {
            result = LocalDateTime.parse(getString(valueNode), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else if (type == DataType.OFFSET_DATETIME) {
            result = OffsetDateTime.parse(getString(valueNode), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            result = getBoolean(valueNode);
        }
        return result;
    }

    /**
     * Get table node
     *
     * @param tableAliasNode Table alias node
     * @param tableName      Table name
     */
    Node<String> getTableNode(Node<String> tableAliasNode, String tableName) {
        List<Node<String>> nodes = new ArrayList<>(4);
        if (requestData.schemaNameNode != null) {
            nodes.add(requestData.schemaNameNode);
        }
        nodes.add(node(tableName));
        nodes.add(Helper.SPACE_NODE);
        nodes.add(tableAliasNode);
        // Returns a node with contents of the form "schemaName.tableName tableAlias" or "tableName tableAlias"
        return node(nodes);
    }

    /**
     * Process parameter
     *
     * @param stringBuilder    String builder
     * @param paramDescription Parameter description
     */
    void processParam(StringBuilder stringBuilder, ParamDescription paramDescription) {
        Object value = NOT_SET_PARAM_VALUE;
        if (parameters != null) {
            value = parameters.getOrDefault(paramDescription.getName(), value);
        } else if (paramsNode != null) {
            JsonNode valueNode = paramsNode.get(paramDescription.getName());
            if (valueNode != null) {
                if (valueNode.isNull()) {
                    value = null;
                } else {
                    if (paramDescription.isCollection()) {
                        checkIsArray(valueNode);
                        List<Object> list = new ArrayList<>();
                        valueNode.elements().forEachRemaining(elemNode -> {
                            list.add(elemNode.isNull() ? null : getValue(elemNode, paramDescription.getType()));
                        });
                        value = list;
                    } else {
                        value = getValue(valueNode, paramDescription.getType());
                    }
                }
            }
        }
        if (value == NOT_SET_PARAM_VALUE && paramDescription.getDefaultValue() != null) {
            value = paramDescription.getDefaultValue();
            if (paramDescription.getType() == DataType.CHARACTER) {
                value = value.toString();
            }
        }
        if (value == null && paramDescription.isCollection()) {
            throw new NullParamValueSetForCollectionException(paramDescription.getName());
        }
        if (value == NOT_SET_PARAM_VALUE) {
            throw new ParamValueNotSetException(paramDescription.getName());
        }
        if (paramDescription.isCollection()) {
            List<Node<String>> nodes = new ArrayList<>();
            addNodeListToNodes(nodes, Helper.COMMA_NODE, ((List<Object>) value).stream().map(elem -> requestData.sqlDialect.processParameterNode(addParameter(elem), paramDescription.getType())));
            stringBuilder.append(CommonHelper.getString(node(nodes)));
        } else {
            stringBuilder.append(CommonHelper.getString(requestData.sqlDialect.processParameterNode(addParameter(value), paramDescription.getType())));
        }
    }

    String processTableQuery(String tableQuery, EntityDescription entityDescription) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean inString = false;
        int paramBeginIndex = -1;
        int beginIndex = 0;
        for (int i = 0; i < tableQuery.length(); ++i) {
            char symbol = tableQuery.charAt(i);
            switch (symbol) {
                case '\'':
                    inString = !inString;
                    break;
                case '$':
                    if (!inString && i + 1 < tableQuery.length() && tableQuery.charAt(i + 1) == '{') {
                        ++i;
                        paramBeginIndex = i + 1;
                    }
                    break;
                case '}':
                    if (paramBeginIndex != -1) {
                        stringBuilder.append(tableQuery, beginIndex, paramBeginIndex - 2);
                        beginIndex = i + 1;
                        String paramName = tableQuery.substring(paramBeginIndex, i);
                        if (paramName.startsWith("dspc.")) {
                            String dspcParamName = paramName.substring("dspc.".length());
                            if (dspcParamName.equals("schemaPrefix")) {
                                if (requestData.schemaNameNode != null) {
                                    stringBuilder.append(CommonHelper.getString(requestData.schemaNameNode));
                                }
                            } else {
                                Object value = requestData.params.get(dspcParamName);
                                if (value == null) {
                                    throw new ParamValueNotSetException(paramName);
                                } else {
                                    stringBuilder.append(CommonHelper.getString(addParameter(value)));
                                }
                            }
                        } else {
                            ParamDescription paramDescription = entityDescription.getParamDescriptions().get(paramName);
                            if (paramDescription == null) {
                                throw new ParamDescriptionNotFoundException(entityDescription.getName(), paramName);
                            } else {
                                processParam(stringBuilder, paramDescription);
                            }
                        }
                        paramBeginIndex = -1;
                    }
                    break;
            }
        }
        stringBuilder.append(tableQuery, beginIndex, tableQuery.length());
        return stringBuilder.toString();
    }

    /**
     * Get table node
     *
     * @param entityDescription Entity description
     * @param aliasNode         Alias node
     */
    Node<String> getTableNode(EntityDescription entityDescription, Node<String> aliasNode) {
        Node<String> result = null;
        if (entityDescription.getTableType() == TableType.SIMPLE) {
            result = getTableNode(aliasNode, entityDescription.getTableName());
        } else {
            if (requestData.tableQueryProvider != null) {
                String query = requestData.tableQueryProvider.getQuery(entityDescription.getName());
                if (query != null) {
                    result = node(Helper.BRACKET_L_NODE, node(processTableQuery(query, entityDescription)), Helper.BRACKET_R_NODE, Helper.SPACE_NODE, aliasNode);
                }
            }
        }
        if (result == null) {
            throw new TableNotFoundException(entityDescription.getName());
        }
        return result;
    }

    /**
     * Get table node
     *
     * @param tableAliasNode Table alias node
     * @param queryNode      Query node
     */
    Node<String> getTableNode2(Node<String> tableAliasNode, Node<String> queryNode) {
        return node(Helper.BRACKET_L_NODE, queryNode, Helper.BRACKET_R_NODE, tableAliasNode);
    }

    /**
     * Get common table node
     *
     * @param commonTableAliasNode The common table alias node
     * @param columnAliasNodes     Column alias nodes
     * @param columnsCount         The number of columns
     * @param queryNode            Query node
     */
    Node<String> getCommonTableNode(Node<String> commonTableAliasNode, List<Node<String>> columnAliasNodes, int columnsCount, Node<String> queryNode) {
// Here, a general tabular representation of type ct0(c0,c1) is constructed as (select ... )
        List<Node<String>> nodes = new ArrayList<>(4 + columnsCount * 2);
        nodes.add(commonTableAliasNode);
        nodes.add(Helper.BRACKET_L_NODE);
        nodes.add(columnAliasNodes.get(0));
        for (int i = 1; i < columnsCount; ++i) {
            nodes.add(Helper.COMMA_NODE);
            nodes.add(columnAliasNodes.get(i));
        }
        nodes.add(Helper.BRACKET_R_AS_BRACKET_L_NODE);
        nodes.add(queryNode);
        nodes.add(Helper.BRACKET_R_NODE);
        return node(nodes);
    }

    /**
     * Add parameter
     *
     * @param value The value
     * @return Parameter node
     */
    Node<String> addParameter(Object value) {
        Node<String> parameterNode = PARAMETER_NAME_NODES.get(++requestData.lastParameterIndexPointer.object);
        String parameterName = parameterNode.getValue().substring(1);
        if (requestData.sqlDialect == SqlDialect.ORACLE) {
            if (value instanceof LocalTime) {
                value = ORACLE_TIME_FORMATTER.format((LocalTime) value);
            }
            if (value instanceof OffsetDateTime) {
                value = ((OffsetDateTime) value).atZoneSameInstant(ZoneOffset.systemDefault()).toLocalDateTime();
            }
        } else if (requestData.sqlDialect == SqlDialect.H2) {
            if (value instanceof String) {
                parameterNode = node(parameterNode, Helper.VARCHAR_NODE);
            } else if (value instanceof Long) {
                parameterNode = node(parameterNode, Helper.BIGINT_NODE);
            } else if (value instanceof BigDecimal) {
                parameterNode = node(parameterNode, Helper.DECIMAL_NODE);
            } else if (value instanceof LocalDate) {
                parameterNode = node(parameterNode, Helper.DATE_NODE);
            } else if (value instanceof LocalDateTime) {
                parameterNode = node(parameterNode, Helper.TIMESTAMP_NODE);
            } else if (value instanceof OffsetDateTime) {
                parameterNode = node(parameterNode, Helper.TIMESTAMP_NODE);
            } else if (value instanceof LocalTime) {
                parameterNode = node(parameterNode, Helper.TIME_NODE);
            } else if (value instanceof Boolean) {
                parameterNode = node(parameterNode, Helper.BOOL_NODE);
            }
        }
        if (value instanceof Object[]) {
            Object[] array = (Object[]) value;
            Object firstElem = array[0];
            if (firstElem instanceof String) {
                value = Arrays.stream(array).map(elem -> (String) elem).toArray(String[]::new);
            } else if (firstElem instanceof Long) {
                value = Arrays.stream(array).map(elem -> (Long) elem).toArray(Long[]::new);
            } else if (firstElem instanceof BigDecimal) {
                value = Arrays.stream(array).map(elem -> (BigDecimal) elem).toArray(BigDecimal[]::new);
            } else if (firstElem instanceof LocalDate) {
                value = Arrays.stream(array).map(elem -> Date.valueOf((LocalDate) elem)).toArray(Date[]::new);
            } else if (firstElem instanceof LocalDateTime) {
                value = Arrays.stream(array).map(elem -> Timestamp.valueOf((LocalDateTime) elem)).toArray(Timestamp[]::new);
            } else if (firstElem instanceof OffsetDateTime) {
                value = Arrays.stream(array).map(elem -> Timestamp.valueOf(((OffsetDateTime) elem).atZoneSameInstant(ZoneOffset.systemDefault()).toLocalDateTime())).toArray(Timestamp[]::new);
            } else /*Boolean*/ {
                value = Arrays.stream(array).map(elem -> (Boolean) elem).toArray(Boolean[]::new);
            }
        }
        requestData.mapSqlParameterSource.addValue(parameterName, value);
        return parameterNode;
    }

    /**
     * Join tables
     *
     * @param table1Node  Table node 1
     * @param column1Node The node of column 1
     * @param table2Node  Table 2 node
     * @param column2Node The node of column 2
     */
    Node<String> join(Node<String> table1Node, Node<String> column1Node, Node<String> table2Node, Node<String> column2Node) {
        return node(table1Node, Helper.JOIN_NODE, table2Node, Helper.ON_NODE, column1Node, Helper.EQ_NODE, column2Node);
    }

    /**
     * Join tables
     *
     * @param parentQueryIdNode Parent query ID node
     * @param parentQueryId     Parent query ID
     * @param table1Node        Table node 1
     * @param column1Node       The node of column 1
     * @param table2Node        Table node 2
     * @param column2Node       The node of column 2
     */
    Node<String> join(Node<String> parentQueryIdNode, int parentQueryId, Node<String> table1Node, Node<String> column1Node, Node<String> table2Node, Node<String> column2Node) {
        return node(table1Node, Helper.JOIN_NODE, table2Node, Helper.ON_NODE, parentQueryIdNode, Helper.EQ_NODE, QUERY_ID_NODES.get(parentQueryId), Helper.AND_NODE, column1Node, Helper.EQ_NODE, column2Node);
    }

    /**
     * Join tables (left join)
     *
     * @param table1Node  Table node 1
     * @param column1Node The node of column 1
     * @param table2Node  Table 2 node
     * @param column2Node The node of column 2
     */
    Node<String> leftJoin(Node<String> table1Node, Node<String> column1Node, Node<String> table2Node, Node<String> column2Node) {
        return node(table1Node, Helper.LEFT_JOIN_NODE, table2Node, Helper.ON_NODE, column1Node, Helper.EQ_NODE, column2Node);
    }

    /**
     * Get column node with identifier
     *
     * @param entityDescription Entity description
     * @param tableAliasNode    Table alias node
     */
    Node<String> getIdColumnNode(EntityDescription entityDescription, Node<String> tableAliasNode) {
        if (entityDescription.getIdColumnName() == null) {
            return Helper.NULL_NODE;
        } else {
            return getColumnNode(tableAliasNode, node(entityDescription.getIdColumnName()));
        }
    }

    /**
     * Get table data
     *
     * @param entityDescription Entity description
     */
    TableData getTableData(EntityDescription entityDescription) {
        EntityDescription rootEntityDescription = entityDescription.getRootEntityDescription();
        if (rootEntityDescription.getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE) {
            entityDescription = rootEntityDescription;
        }
// At the moment of return, the string for FROM and the string for reading the identifier in SELECT will already be set there.
        return tablesData.computeIfAbsent(entityDescription, key -> {
            TableData result = new TableData();
            result.entityDescription = key;
            result.aliasNode = getTableAliasNode();
            result.tableNode = getTableNode(key, result.aliasNode);
            result.idColumnNode = getIdColumnNode(key, result.aliasNode);
            return result;
        });
    }

    /**
     * Add table
     *
     * @param tableData Table data
     * @param innerJoin Use inner join
     */
    void addTable(TableData tableData, boolean innerJoin) {
        if (!tableData.added) {
            UnaryOperator<Node<String>> currentGetTableNodeFunction = getTableNodeFunctionPointer.object;
            getTableNodeFunctionPointer.object = tableNode -> {
                Node<String> table2Node = currentGetTableNodeFunction.apply(tableNode);
                return innerJoin ? join(table2Node, idColumnData.columnNode, tableData.tableNode, tableData.idColumnNode) : leftJoin(table2Node, idColumnData.columnNode, tableData.tableNode, tableData.idColumnNode);
            };
            tableData.added = true;
        }
    }


    /**
     * Add table
     *
     * @param tableData Table data
     */
    void addTable(TableData tableData) {
        addTable(tableData, requestData.optimizeJoins && mandatory && mandatoryEntityDescription.isExtensionOf(tableData.entityDescription.getName()));
    }

    /**
     * Register an SQL query handler
     */
    void registerSqlQueryProcessor() {
        queryId = requestData.sqlQueryProcessors.size();
        requestData.sqlQueryProcessors.add(this);
    }

    /**
     * Get column index
     *
     * @param columnTypes       The types of columns
     * @param typeColumnIndexes Indices of columns by types
     * @param columnsData       Column data
     * @param type              Type
     */
    int getColumnIndex(List<DataType> columnTypes, Map<DataType, List<Integer>> typeColumnIndexes, Map<Integer, ColumnData> columnsData, DataType type) {
        List<Integer> columnIndexes = typeColumnIndexes.computeIfAbsent(type, key -> new ArrayList<>(1));
        return columnIndexes.stream()
            .filter(index -> !columnsData.containsKey(index))
            .findAny()
            .orElseGet(() -> {
                columnTypes.add(type);
                int result = columnTypes.size();
                columnIndexes.add(result);
                return result;
            });
    }

    /**
     * Get index of column for primitive
     *
     * @param type Тип
     */
    int getPrimitiveColumnIndex(DataType type) {
        return getColumnIndex(requestData.columnTypes, requestData.primitiveColumnIndexes, columnsData, type);
    }

    /**
     * Get index of column for number of elements
     */
    int getCountColumnIndex() {
        if (requestData.countColumnIndexPointer.object == null) {
            requestData.columnTypes.add(DataType.INTEGER);
            requestData.countColumnIndexPointer.object = requestData.columnTypes.size();
        }
        return requestData.countColumnIndexPointer.object;
    }

    /**
     * Process alias
     *
     * @param aliasNode         Alias node
     * @param entityDescription Entity description
     * @param nullable          Can it accept a null value
     */
    void processAlias(JsonNode aliasNode, EntityDescription entityDescription, boolean nullable) {
        if (aliasNode != null) {
            String alias = getString(aliasNode);
            if (aliasedEntitiesData.containsKey(alias)) {
                throw new DuplicateAliasFoundException(alias);
            }
            aliasedEntitiesData = new HashMap<>(aliasedEntitiesData);
            AliasedEntityData aliasedEntityData = new AliasedEntityData(this, entityDescription);
            aliasedEntityData.nullable = nullable;
            aliasedEntitiesData.put(alias, aliasedEntityData);
        }
    }

    /**
     * Get the final property name
     *
     * @param propertyName Property name
     * @param alias        Alias
     */
    String getFinalPropertyName(String propertyName, String alias) {
        return alias == null ? propertyName : alias;
    }

    /**
     * Process the collection of links
     *
     * @param finalPropertyName        The final name of the property
     * @param elementEntityDescription Description of the element entity
     * @param specificationNode        Specification node
     */
    void processReferencesCollection(String finalPropertyName, EntityDescription elementEntityDescription, JsonNode specificationNode) {
        entityDescription = elementEntityDescription;
        mandatoryEntityDescription = elementEntityDescription;
        if (specificationNode != null) {
            expressionContext = new ExpressionContext(this);
            expressionContext.aliasedEntitiesData = aliasedEntitiesData;
            if (specificationNode.isTextual()) {
                condition = getCondition(specificationNode.textValue());
                condition.prepare(requestData.startSqlQueryProcessor, expressionContext);
                getCountTableNodeFunction = getTableNodeFunctionPointer.object;
            } else if (specificationNode.isObject()) {
                checkFieldNames(specificationNode, EntitiesReadAccessJsonHelper.REFERENCES_COLLECTION_SPECIFICATION_FIELD_NAMES);
                entityDescription = elementEntityDescription.cast(getEntityType(specificationNode));
                processAlias(specificationNode.get(EntitiesReadAccessJsonHelper.ELEMENT_ALIAS_FIELD_NAME), entityDescription, false);
                expressionContext.aliasedEntitiesData = aliasedEntitiesData;
                addReferencesCollectionFilter(elementEntityDescription);
                processSearchSettings(specificationNode);
                processEntitySpecification(specificationNode);
            } else {
                throw new UnsupportedNodeException(specificationNode.toString());
            }
        } else {
            getCountTableNodeFunction = getTableNodeFunctionPointer.object;
        }
        List<BiConsumer<EntityData, ResultSet>> currentRecordPostProcessors = recordProcessors;
        recordProcessors = Collections.singletonList((entityData, resultSet) -> {
            EntityData referenceEntityData = new EntityData();
            referenceEntityData.id = wrap(() -> resultSet.getString(idColumnData.columnIndex));
            referenceEntityData.entityDescription = entityDescription;
            currentRecordPostProcessors.forEach(recordProcessor -> recordProcessor.accept(referenceEntityData, resultSet));
            List<String> collectionOwner = getCollectionOwner(resultSet);
            getCollectionForWrite(collectionOwner, finalPropertyName).add(getOrder(resultSet), referenceEntityData);
        });
    }

    /**
     * Get condition
     *
     * @param string String
     */
    ConditionImpl getCondition(String string) {
        StringConditionBuilder stringConditionBuilder;
        if (expressionContext.collectionElementColumnNode == null && expressionContext.collectionElementSqlQueryProcessor == null) {
            stringConditionBuilder = new StringConditionBuilder(string, requestData.modelDescription, requestData.startSqlQueryProcessor.entityDescription.getName());
        } else if (expressionContext.collectionElementColumnNode != null) {
            stringConditionBuilder = new StringConditionBuilder(string, requestData.modelDescription, requestData.startSqlQueryProcessor.entityDescription.getName(), true);
        } else {
            stringConditionBuilder = new StringConditionBuilder(string, requestData.modelDescription, requestData.startSqlQueryProcessor.entityDescription.getName(), expressionContext.collectionElementSqlQueryProcessor.entityDescription.getName());
        }
        aliasedEntitiesData.forEach((alias, aliasedEntityData) -> stringConditionBuilder.setAliasedEntityDescription(alias, aliasedEntityData.entityDescription.getName()));
        return (ConditionImpl) stringConditionBuilder.build(requestData.expressionsProcessor);
    }

    /**
     * Get primitive expression
     *
     * @param string String
     */
    PrimitiveExpressionImpl getPrimitiveExpression(String string) {
        StringPrimitiveExpressionBuilder stringPrimitiveExpressionBuilder;
        if (expressionContext.collectionElementColumnNode == null && expressionContext.collectionElementSqlQueryProcessor == null) {
            stringPrimitiveExpressionBuilder = new StringPrimitiveExpressionBuilder(string, requestData.modelDescription, requestData.startSqlQueryProcessor.entityDescription.getName());
        } else if (expressionContext.collectionElementColumnNode != null) {
            stringPrimitiveExpressionBuilder = new StringPrimitiveExpressionBuilder(string, requestData.modelDescription, requestData.startSqlQueryProcessor.entityDescription.getName(), true);
        } else {
            stringPrimitiveExpressionBuilder = new StringPrimitiveExpressionBuilder(string, requestData.modelDescription, requestData.startSqlQueryProcessor.entityDescription.getName(), expressionContext.collectionElementSqlQueryProcessor.entityDescription.getName());
        }
        aliasedEntitiesData.forEach((alias, aliasedEntityData) -> stringPrimitiveExpressionBuilder.setAliasedEntityDescription(alias, aliasedEntityData.entityDescription.getName()));
        return (PrimitiveExpressionImpl) stringPrimitiveExpressionBuilder.build(requestData.expressionsProcessor);
    }

    /**
     * Get sorting criteria data
     *
     * @param string String
     */
    SortCriterionData getSortCriterionData(String string) {
        SortCriterionData result = new SortCriterionData();
        result.criterion = getPrimitiveExpression(string);
        return result;
    }

    /**
     * Get sorting criterion data
     *
     * @param sortCriterionSpecificationNode The node of the sorting criterion specification
     */
    SortCriterionData getSortCriterionData(JsonNode sortCriterionSpecificationNode) {
        checkFieldNames(sortCriterionSpecificationNode, EntitiesReadAccessJsonHelper.SORT_CRITERION_SPECIFICATION_FIELD_NAMES);
        JsonNode criterionNode = sortCriterionSpecificationNode.get(EntitiesReadAccessJsonHelper.CRITERION_FIELD_NAME);
        if (criterionNode == null) {
            throw new SortCriterionNotSetException();
        }
        SortCriterionData result = getSortCriterionData(getString(criterionNode));
        JsonNode sortOrderNode = sortCriterionSpecificationNode.get(EntitiesReadAccessJsonHelper.ORDER_FIELD_NAME);
        if (sortOrderNode != null) {
            String sortOrder = getString(sortOrderNode);
            if (!EXPECTED_SORT_ORDERS.contains(sortOrder.toLowerCase(Locale.ENGLISH))) {
                throw new UnexpectedSortOrderException(EXPECTED_SORT_ORDERS, sortOrder);
            }
            result.descending = EntitiesReadAccessJsonHelper.DESCENDING.equalsIgnoreCase(sortOrder);
        }
        JsonNode nullsLastNode = sortCriterionSpecificationNode.get(EntitiesReadAccessJsonHelper.NULLS_LAST_FIELD_NAME);
        if (nullsLastNode != null) {
            result.nullsLast = getBoolean(nullsLastNode);
        }
        return requestData.sqlDialect.processSortCriterionData(result);
    }

    void initLimitOffsetSettings(JsonNode node) {
        JsonNode limitNode = node.get(EntitiesReadAccessJsonHelper.LIMIT_FIELD_NAME);
        if (limitNode != null) {
            limit = getInteger(limitNode);
        }
        if (limit == null) {
            limit = requestData.defaultLimit;
        }
        JsonNode offsetNode = node.get(EntitiesReadAccessJsonHelper.OFFSET_FIELD_NAME);
        if (offsetNode != null) {
            offset = getInteger(offsetNode);
        }
        nullLimitAndOffset = limit == null && offset == null;
    }

    /**
     * Initialize search settings
     *
     * @param node Node
     */
    void initSearchSettings(JsonNode node) {
        JsonNode conditionNode = node.get(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME);
        if (conditionNode != null) {
            condition = getCondition(getString(conditionNode));
        }
        JsonNode securityConditionNode = node.get(EntitiesReadAccessJsonHelper.SECURITY_CONDITION_FIELD_NAME);
        if (securityConditionNode != null) {
            ConditionImpl securityCondition = getCondition(getString(securityConditionNode));
            condition = condition == null ? securityCondition : (ConditionImpl) condition.and(securityCondition);
        }
        JsonNode groupNode = node.get(EntitiesReadAccessJsonHelper.GROUP_FIELD_NAME);
        if (groupNode != null) {
            if (groupNode.isTextual()) {
                group = new ArrayList<>(1);
                group.add(getPrimitiveExpression(groupNode.textValue()));
            } else if (groupNode.isArray()) {
                group = new ArrayList<>(groupNode.size());
                groupNode.elements().forEachRemaining(elementNode -> group.add(getPrimitiveExpression(getString(elementNode))));
            } else {
                throw new UnsupportedNodeException(node.toString());
            }
        }
        JsonNode groupCondNode = node.get(EntitiesReadAccessJsonHelper.GROUP_CONDITION_FIELD_NAME);
        if (groupCondNode != null) {
            groupCond = getCondition(getString(groupCondNode));
        }
        initLimitOffsetSettings(node);
        JsonNode countNode = node.get(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME);
        count = countNode != null && getBoolean(countNode);
        JsonNode sortNode = node.get(EntitiesReadAccessJsonHelper.SORT_FIELD_NAME);
        if (sortNode != null) {
            if (sortNode.isTextual()) {
                sortCriteriaData = new ArrayList<>(1);
                sortCriteriaData.add(requestData.sqlDialect.processSortCriterionData(getSortCriterionData(sortNode.textValue())));
            } else if (sortNode.isObject()) {
                sortCriteriaData = new ArrayList<>(1);
                sortCriteriaData.add(getSortCriterionData(sortNode));
            } else if (sortNode.isArray()) {
                sortCriteriaData = new ArrayList<>(sortNode.size());
                sortNode.elements().forEachRemaining(elementNode -> {
                    if (elementNode.isTextual()) {
                        sortCriteriaData.add(requestData.sqlDialect.processSortCriterionData(getSortCriterionData(elementNode.textValue())));
                    } else if (elementNode.isObject()) {
                        sortCriteriaData.add(getSortCriterionData(elementNode));
                    } else {
                        throw new UnsupportedNodeException(elementNode.toString());
                    }
                });
            } else {
                throw new UnsupportedNodeException(node.toString());
            }
        }
    }

    /**
     * Process the inheritance strategy SINGLE_TABLE
     */
    void processSingleTableInheritanceStrategy() {
        if (entityDescription.getRootEntityDescription().getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE && entityDescription.getParentEntityDescription() != null) {
            Node<String> additionalConditionNode = node(getTypeColumnData().columnNode, entityDescription.getMetaDataManager().get(EntityDescriptionMetaData.class).inHeirTypesStringNode);
            getAdditionalConditionNodeFunctionPointer.object = () -> additionalConditionNode;
        }
    }

    /**
     * Process search settings
     *
     * @param node Node
     */
    void processSearchSettings(JsonNode node) {
// First goes conversion from JSON`a
        initSearchSettings(node);
//        Then the general consistency of the specification
        if (limit != null && limit == 0) {
            checkExtraFields(node, EntitiesReadAccessJsonHelper.ZERO_LIMIT_SPECIFICATION_FIELD_NAMES, ExtraFieldsFoundForZeroLimitException::new);
            if (!count) {
                throw new UnreasonableSearchException(node.toString());
            }
        }
        if (count) {
            if (!nullLimitAndOffset) {
                getCountColumnIndex();
                counts = new HashMap<>();
            }
        }
        if (condition != null) {
            condition.prepare(requestData.startSqlQueryProcessor, expressionContext);
        }
        if (group != null) {
            group.forEach(groupCriterion -> groupCriterion.prepare(requestData.startSqlQueryProcessor, expressionContext));
        }
        if (groupCond != null) {
            groupCond.prepare(requestData.startSqlQueryProcessor, expressionContext);
        }
        getCountTableNodeFunction = getTableNodeFunctionPointer.object;
        if (!sortCriteriaData.isEmpty()) {
            getCountColumnIndex();
            sortCriteriaData.forEach(sortCriterionData -> sortCriterionData.criterion.prepare(requestData.startSqlQueryProcessor, expressionContext));
        }
    }

    void setIdOnlyFalse() {
        idOnly = false;
        if (aliasedOwner != null) {
            aliasedOwner.setIdOnlyFalse();
        }
    }

    /**
     * Process column with id
     */
    void processIdColumn() {
        if (limit == null || limit != 0) {
            idColumnData.columnIndex = getPrimitiveColumnIndex(DataType.STRING);
            columnsData.put(idColumnData.columnIndex, idColumnData);
        }
    }

    /**
     * Get column data for type
     */
    ColumnData getTypeColumnData() {
        if (typeColumnData == null) {
            setIdOnlyFalse();
            EntityDescription rootEntityDescription = entityDescription.getRootEntityDescription();
            Node<String> columnNode;
            if (rootEntityDescription.isFinal()) {
                columnNode = node(Helper.QUOTE_NODE, node(entityDescription.getName()), Helper.QUOTE_NODE);
            } else {
                TableData rootEntityTableData = getTableData(rootEntityDescription);
                addTable(rootEntityTableData);
                columnNode = getColumnNode(rootEntityTableData.aliasNode, node(rootEntityDescription.getTypeColumnName()));
            }
            typeColumnData = new ColumnData(this, DataType.STRING, columnNode);
            if (!rootEntityDescription.isFinal()) {
                workColumnsData.add(typeColumnData);
            }
        }
        return typeColumnData;
    }

    /**
     * Get column data with local id
     *
     * @param entityDescription Entity description
     */
    ColumnData getLocalIdColumnData(EntityDescription entityDescription) {
        return localIdColumnsData.computeIfAbsent(entityDescription, key -> {
            setIdOnlyFalse();
            TableData entityTableData = getTableData(key);
            addTable(entityTableData);
            ColumnData result = new ColumnData(this, DataType.STRING, entityTableData.idColumnNode);
            workColumnsData.add(result);
            return result;
        });
    }

    /**
     * Get column data with primitive
     *
     * @param primitiveDescription Description of the primitive
     */
    ColumnData getPrimitiveColumnData(PrimitiveDescription primitiveDescription) {
        return primitiveColumnsData.computeIfAbsent(primitiveDescription, key -> {
            setIdOnlyFalse();
            TableData ownerEntityTableData = getTableData(key.getOwnerEntityDescription());
            addTable(ownerEntityTableData);
            ColumnData result = new ColumnData(this, primitiveDescription.getType(), getColumnNode(ownerEntityTableData.aliasNode, node(primitiveDescription.getColumnName())));
            workColumnsData.add(result);
            return result;
        });
    }

    /**
     * Get a handler for the SQL query reference
     *
     * @param referenceDescription Description of the link
     */
    SqlQueryProcessor getReferenceSqlQueryProcessor(ReferenceDescription referenceDescription) {
        SqlQueryProcessor result = referenceSqlQueryProcessors.computeIfAbsent(referenceDescription, key -> {
            setIdOnlyFalse();
            TableData ownerEntityTableData = getTableData(key.getOwnerEntityDescription());
            addTable(ownerEntityTableData);

            SqlQueryProcessor result2 = new SqlQueryProcessor(requestData);
            result2.ownerTypeName = referenceDescription.getOwnerEntityDescription().getName();
            result2.parent = parent;
            result2.entityDescription = key.getEntityDescription();
            result2.reference = true;
            result2.tablesData = new HashMap<>();
            result2.workColumnsData = workColumnsData;
            result2.idColumnData = new ColumnData(result2, DataType.STRING, getColumnNode(ownerEntityTableData.aliasNode, node(referenceDescription.getColumnName())));
            result2.idColumnData.refId = true;
            result2.mandatory = mandatory && referenceDescription.isMandatory();
            result2.mandatoryEntityDescription = result2.entityDescription;
            workColumnsData.add(result2.idColumnData);
            result2.getTableNodeFunctionPointer = getTableNodeFunctionPointer;
            result2.localIdColumnsData = new HashMap<>();
            result2.primitiveColumnsData = new HashMap<>();
            result2.referenceSqlQueryProcessors = new HashMap<>();
            result2.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
            return result2;
        });
        result.aliasedEntitiesData = aliasedEntitiesData;
        return result;
    }

    /**
     * Get a SQL query handler for a link with a backlink
     *
     * @param backReferenceDescription Description of the reverse reference
     */
    SqlQueryProcessor getBackReferenceReferenceSqlQueryProcessor(ReferenceDescription backReferenceDescription) {
        SqlQueryProcessor result = backReferenceReferenceSqlQueryProcessors.computeIfAbsent(backReferenceDescription, key -> {
            setIdOnlyFalse();
            EntityDescription ownerEntityDescription = key.getOwnerEntityDescription();

            SqlQueryProcessor result2 = new SqlQueryProcessor(requestData);
            result2.ownerTypeName = backReferenceDescription.getOwnerEntityDescription().getName();
            result2.parent = parent;
            result2.entityDescription = ownerEntityDescription;
            result2.reference = true;
            result2.setIdOnlyFalse();
            result2.tablesData = new HashMap<>();
            TableData ownerEntityTableData = result2.getTableData(ownerEntityDescription);
            ownerEntityTableData.added = true;
            result2.workColumnsData = workColumnsData;
            result2.idColumnData = new ColumnData(result2, DataType.STRING, ownerEntityTableData.idColumnNode);
            result2.idColumnData.refId = true;
            workColumnsData.add(result2.idColumnData);
            result2.getTableNodeFunctionPointer = getTableNodeFunctionPointer;
            UnaryOperator<Node<String>> currentGetTableNodeFunction = getTableNodeFunctionPointer.object;
            getTableNodeFunctionPointer.object = tableNode -> {
                Node<String> table2Node = currentGetTableNodeFunction.apply(tableNode);
                return leftJoin(table2Node, idColumnData.columnNode, ownerEntityTableData.tableNode, getColumnNode(ownerEntityTableData.aliasNode, node(backReferenceDescription.getColumnName())));
            };
            result2.localIdColumnsData = new HashMap<>();
            result2.localIdColumnsData.put(ownerEntityDescription, result2.idColumnData);
            result2.primitiveColumnsData = new HashMap<>();
            result2.referenceSqlQueryProcessors = new HashMap<>();
            result2.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
            return result2;
        });
        result.aliasedEntitiesData = aliasedEntitiesData;
        return result;
    }

    /**
     * Get a SQL query handler under the alias
     *
     * @param alias             Alias
     * @param sqlQueryProcessor The SQL query handler
     */
    SqlQueryProcessor getAliasedSqlQueryProcessor(String alias, SqlQueryProcessor sqlQueryProcessor) {
        SqlQueryProcessor result = aliasedSqlQueryProcessors.computeIfAbsent(sqlQueryProcessor.ownerTypeName + alias, key -> {
            SqlQueryProcessor result2 = new SqlQueryProcessor();
            result2.aliasedOwner = sqlQueryProcessor;
            result2.requestData = sqlQueryProcessor.requestData;
            result2.parent = sqlQueryProcessor.parent;
            result2.entityDescription = sqlQueryProcessor.entityDescription;
            result2.reference = sqlQueryProcessor.reference;
            result2.idOnly = sqlQueryProcessor.idOnly;
            result2.tablesData = sqlQueryProcessor.tablesData;
            result2.workColumnsData = sqlQueryProcessor.workColumnsData;
            result2.idColumnData = sqlQueryProcessor.idColumnData;
            result2.mandatory = sqlQueryProcessor.mandatory;
            result2.mandatoryEntityDescription = sqlQueryProcessor.mandatoryEntityDescription;
            result2.getTableNodeFunctionPointer = sqlQueryProcessor.getTableNodeFunctionPointer;
            result2.localIdColumnsData = sqlQueryProcessor.localIdColumnsData;
            result2.primitiveColumnsData = sqlQueryProcessor.primitiveColumnsData;
            result2.referenceSqlQueryProcessors = sqlQueryProcessor.referenceSqlQueryProcessors;
            result2.backReferenceReferenceSqlQueryProcessors = sqlQueryProcessor.backReferenceReferenceSqlQueryProcessors;
            return result2;
        });
        result.aliasedEntitiesData = aliasedEntitiesData;
        return result;
    }

    /**
     * Analyze type
     *
     * @param entityDescription Entity description
     */
    void analyzeType(EntityDescription entityDescription) {
        if (entityDescription.getRootEntityDescription().getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE) {
            ColumnData typeColumnData2 = getTypeColumnData();
            if (typeColumnData2.columnIndex == 0) {
                typeColumnData2.columnIndex = getPrimitiveColumnIndex(DataType.STRING);
                columnsData.put(typeColumnData2.columnIndex, typeColumnData2);
                recordProcessors.add(0, (entityData, resultSet) -> {
                    if (entityData.access) {
                        String entityType = wrap(() -> resultSet.getString(typeColumnData2.columnIndex));
                        if (entityType == null) {
                            entityData.invalid = true;
                        } else {
                            EntityDescription entityDescription2 = requestData.modelDescription.getEntityDescription(entityType);
                            if (entityDescription2.isExtensionOf(entityData.entityDescription.getName())) {
                                entityData.entityDescription = entityDescription2;
                            } else {
                                entityData.invalid = true;
                            }
                        }
                    }
                });
            }
        } else {
            ColumnData localIdColumnData = getLocalIdColumnData(entityDescription);
            if (localIdColumnData.columnIndex == 0) {
                localIdColumnData.columnIndex = getPrimitiveColumnIndex(DataType.STRING);
                columnsData.put(localIdColumnData.columnIndex, localIdColumnData);
                recordProcessors.add(0, (entityData, resultSet) -> {
                    String localId = wrap(() -> resultSet.getString(localIdColumnData.columnIndex));
                    if (localId != null && entityDescription.isExtensionOf(entityData.entityDescription.getName())) {
                        entityData.entityDescription = entityDescription;
                    } else if (localId == null && entityData.entityDescription.isExtensionOf(entityDescription.getName())) {
                        entityData.invalid = true;
                    }
                });
            }
        }
    }

    /**
     * Add filter to link collection
     *
     * @param baseEntityDescription Basic entity description
     */
    void addReferencesCollectionFilter(EntityDescription baseEntityDescription) {
        if (!baseEntityDescription.equals(entityDescription)) {
            if (entityDescription.getRootEntityDescription().getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE) {
                Node<String> additionalConditionNode = node(getTypeColumnData().columnNode, entityDescription.getMetaDataManager().get(EntityDescriptionMetaData.class).inHeirTypesStringNode);
                getAdditionalConditionNodeFunctionPointer.object = () -> additionalConditionNode;
            } else {
                TableData entityTableData = getTableData(entityDescription);
                addTable(entityTableData, true);
            }
        }
    }

    /**
     * Check entity data
     *
     * @param entityDescription Entity description
     */
    boolean checkEntityData(EntityData entityData, EntityDescription entityDescription) {
        return entityData.access && !entityData.invalid && entityData.entityDescription.isExtensionOf(entityDescription.getName());
    }

    /**
     * Get collection owner
     *
     * @param collectionOwner The owner of the collection
     * @param entityData      Entity data
     */
    List<String> getCollectionOwner(List<String> collectionOwner, EntityData entityData) {
        return Stream.concat(
                collectionOwner.stream(),
                Stream.of(entityData.id))
            .collect(Collectors.toList());
    }

    /**
     * Get collection owner
     *
     * @param resultSet Result set
     */
    List<String> getCollectionOwner(ResultSet resultSet) {
        return collectionOwnerColumnsData.keySet().stream().map(columnIndex -> wrap(() -> resultSet.getString(columnIndex))).collect(Collectors.toList());
    }

    /**
     * Get order
     *
     * @param resultSet Result set
     */
    Integer getOrder(ResultSet resultSet) {
        if (requestData.countColumnIndexPointer.object == null) {
            return null;
        }
        int result = wrap(() -> resultSet.getInt(requestData.countColumnIndexPointer.object));
        return Boolean.TRUE.equals(wrap(resultSet::wasNull)) ? null : result;
    }

    /**
     * Get the distance along the inheritance chain between the description of an entity and the description of the target entity
     *
     * @param entityDescription       Entity description
     * @param targetEntityDescription Description of the target entity
     */
    int getInheritanceDistance(EntityDescription entityDescription, EntityDescription targetEntityDescription) {
        int i = 0;
        EntityDescription currentEntityDescription = entityDescription;
        while (!currentEntityDescription.equals(targetEntityDescription)) {
            ++i;
            currentEntityDescription = currentEntityDescription.getParentEntityDescription();
        }
        return i;
    }

    /**
     * Get collection for recording
     *
     * @param collectionOwner   The owner of the collection
     * @param finalPropertyName The final name of the property
     */
    SpecialSortedSet<Object> getCollectionForWrite(List<String> collectionOwner, String finalPropertyName) {
        return collections.computeIfAbsent(collectionOwner, key -> new HashMap<>()).computeIfAbsent(finalPropertyName, key -> new SpecialSortedSet<>(offset));
    }

    /**
     * Get collection for reading
     *
     * @param collectionOwner   The owner of the collection
     * @param finalPropertyName The final name of the property
     * @param <T>               Data type
     */
    <T> Set<T> getCollectionForRead(List<String> collectionOwner, String finalPropertyName) {
        return (Set<T>) Collections.unmodifiableSet(collections.getOrDefault(collectionOwner, Collections.emptyMap()).getOrDefault(finalPropertyName, SpecialSortedSet.getEmpty()));
    }

    /**
     * Get multiple collection
     *
     * @param base            The property on which the collection is based
     * @param collectionOwner The owner of the collection
     * @param collection      Collection
     * @param <E>             Element type
     */
    <E> CollectionData<E> getCollectionData(String base, List<String> collectionOwner, Collection<E> collection) {
        CollectionData<E> result = new CollectionData<>();
        result.base = base;
        result.elements = collection;
        if (count) {
            result.count = nullLimitAndOffset ? collection.size() : counts.getOrDefault(collectionOwner, 0);
        }
        return result;
    }

    /**
     * Process primitive specification
     *
     * @param getPrimitivesFunction Function for obtaining primitives
     * @param entityDescription     Entity description
     * @param primitiveDescription  Description of the primitive
     * @param alias                 Alias
     * @param specificationNode     Specification node
     */
    void processPrimitiveSpecification(Function<EntityData, Map<String, PrimitiveData>> getPrimitivesFunction, EntityDescription entityDescription, PrimitiveDescription primitiveDescription, String alias, JsonNode specificationNode) {
        if (specificationNode != null) {
            if (specificationNode.isObject()) {
                checkFieldNames(specificationNode, Collections.emptySet());
            } else {
                throw new UnsupportedNodeException(specificationNode.toString());
            }
        }
        ColumnData columnData = getPrimitiveColumnData(primitiveDescription);
        if (columnData.columnIndex == 0) {
            columnData.columnIndex = getPrimitiveColumnIndex(primitiveDescription.getType());
            columnsData.put(columnData.columnIndex, columnData);
            analyzeType(primitiveDescription.getOwnerEntityDescription());
        }
        Function3<Object, SqlDialect, ResultSet, Integer> getPrimitiveValueFunction = primitiveDescription.getType().getMetaDataManager().get(DataTypeMetaData.class).getPrimitiveValueFunction;
        String finalPropertyName = getFinalPropertyName(primitiveDescription.getName(), alias);
        recordProcessors.add((entityData, resultSet) -> {
            if (checkEntityData(entityData, entityDescription)) {
                PrimitiveData primitiveData = new PrimitiveData(primitiveDescription.getType(), getPrimitiveValueFunction.call(requestData.sqlDialect, resultSet, columnData.columnIndex));
                if (alias != null) {
                    primitiveData.base = primitiveDescription.getName();
                }
                getPrimitivesFunction.apply(entityData).put(finalPropertyName, primitiveData);
            }
        });
    }

    /**
     * Process primitive specification
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processPrimitiveSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        processPrimitiveSpecification(entityData -> entityData.primitives, entityDescription, entityDescription.getPrimitiveDescription(propertyName), alias, specificationNode);
    }

    /**
     * Process the collection of primitives specification
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processPrimitivesCollectionSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        setIdOnlyFalse();
        PrimitivesCollectionDescription primitivesCollectionDescription = entityDescription.getPrimitivesCollectionDescription(propertyName);
        Node<String> tableAliasNode = getTableAliasNode();

        SqlQueryProcessor sqlQueryProcessor = new SqlQueryProcessor(this);
        sqlQueryProcessor.workColumnsData = new ArrayList<>(2);
        sqlQueryProcessor.idColumnData = new ColumnData(sqlQueryProcessor, DataType.STRING, getColumnNode(tableAliasNode, node(primitivesCollectionDescription.getColumnName())));
        sqlQueryProcessor.workColumnsData.add(sqlQueryProcessor.idColumnData);
        ColumnData ownerColumnData = new ColumnData(sqlQueryProcessor, DataType.STRING, getColumnNode(tableAliasNode, node(primitivesCollectionDescription.getOwnerColumnName())));
        ownerColumnData.inherit = true;
        sqlQueryProcessor.workColumnsData.add(ownerColumnData);
        Node<String> collectionTableNode = getTableNode(tableAliasNode, primitivesCollectionDescription.getTableName());
        sqlQueryProcessor.getTableNodeFunctionPointer = new Pointer<>(table -> {
            table = getTableNodeFunctionPointer.object.apply(table);
            if (mergeRequestQueryIdNode == null) {
                return join(table, idColumnData.columnNode, collectionTableNode, ownerColumnData.columnNode);
            } else {
                return join(mergeRequestQueryIdNode, queryId, table, idColumnData.columnNode, collectionTableNode, ownerColumnData.columnNode);
            }
        });
        sqlQueryProcessor.aliasedEntitiesData = aliasedEntitiesData;
        sqlQueryProcessor.columnsData = new LinkedHashMap<>(collectionOwnerColumnsData);
        sqlQueryProcessor.columnsData.put(sqlQueryProcessor.getPrimitiveColumnIndex(DataType.STRING), ownerColumnData);
        sqlQueryProcessor.collectionOwnerColumnsData.putAll(sqlQueryProcessor.columnsData);
        int elementColumnIndex = sqlQueryProcessor.getPrimitiveColumnIndex(primitivesCollectionDescription.getType());
        sqlQueryProcessor.columnsData.put(elementColumnIndex, sqlQueryProcessor.idColumnData);
        Function3<Object, SqlDialect, ResultSet, Integer> getPrimitiveValueFunction = primitivesCollectionDescription.getType().getMetaDataManager().get(DataTypeMetaData.class).getPrimitiveValueFunction;
        String finalPropertyName = getFinalPropertyName(propertyName, alias);
        sqlQueryProcessor.recordProcessors = Collections.singletonList((entityData, resultSet) -> sqlQueryProcessor.getCollectionForWrite(sqlQueryProcessor.getCollectionOwner(resultSet), finalPropertyName).add(getOrder(resultSet), new PrimitiveData(primitivesCollectionDescription.getType(), getPrimitiveValueFunction.call(requestData.sqlDialect, resultSet, elementColumnIndex))));
        sqlQueryProcessor.collections = new HashMap<>();
        sqlQueryProcessor.registerSqlQueryProcessor();
        if (specificationNode != null) {
            sqlQueryProcessor.expressionContext = new ExpressionContext(sqlQueryProcessor.idColumnData.columnNode, primitivesCollectionDescription.getType());
            sqlQueryProcessor.expressionContext.aliasedEntitiesData = aliasedEntitiesData;
            if (specificationNode.isTextual()) {
                sqlQueryProcessor.condition = sqlQueryProcessor.getCondition(specificationNode.textValue());
                sqlQueryProcessor.condition.prepare(requestData.startSqlQueryProcessor, sqlQueryProcessor.expressionContext);
                getCountTableNodeFunction = getTableNodeFunctionPointer.object;
            } else if (specificationNode.isObject()) {
                checkFieldNames(specificationNode, EntitiesReadAccessJsonHelper.PRIMITIVES_COLLECTION_SPECIFICATION_FIELD_NAMES);
                sqlQueryProcessor.processSearchSettings(specificationNode);
            } else {
                throw new UnsupportedNodeException(specificationNode.toString());
            }
        } else {
            getCountTableNodeFunction = getTableNodeFunctionPointer.object;
        }

        finalProcessors.add((entityData, collectionOwner) -> {
            if (checkEntityData(entityData, entityDescription)) {
                List<String> collectionOwner2 = getCollectionOwner(collectionOwner, entityData);
                int inheritanceDistance = getInheritanceDistance(entityData.entityDescription, entityDescription);
                Integer currentInheritanceDistance = inheritanceDistances.getOrDefault(collectionOwner2, Collections.emptyMap()).get(finalPropertyName);
                if (currentInheritanceDistance == null || inheritanceDistance < currentInheritanceDistance) {
                    inheritanceDistances.computeIfAbsent(collectionOwner2, key -> new HashMap<>()).put(finalPropertyName, inheritanceDistance);
                    entityData.primitivesCollections.put(finalPropertyName, sqlQueryProcessor.getCollectionData(alias == null ? null : propertyName, collectionOwner2, sqlQueryProcessor.getCollectionForRead(collectionOwner2, finalPropertyName)));
                }
            }
        });
    }

    /**
     * Check entity availability
     *
     * @param resultSet Result set
     */
    boolean getEntityAccess(ResultSet resultSet) {
        if (securityFlagColumnIndex != 0) {
            wrap(() -> resultSet.getInt(securityFlagColumnIndex));
            return !wrap(resultSet::wasNull);
        }
        return true;
    }

    /**
     * Process link specification
     *
     * @param getReferencesFunction The function for obtaining references
     * @param entityDescription     Entity description
     * @param referenceDescription  Reference description
     * @param specificationNode     Specification node
     */
    void processReferenceSpecification(Function<EntityData, Map<String, EntityData>> getReferencesFunction, String alias, EntityDescription entityDescription, ReferenceDescription referenceDescription, JsonNode specificationNode) {
        String finalPropertyName = getFinalPropertyName(referenceDescription.getName(), alias);
        SqlQueryProcessor referenceSqlQueryProcessor = getReferenceSqlQueryProcessor(referenceDescription);
        SqlQueryProcessor sqlQueryProcessor = getAliasedSqlQueryProcessor(finalPropertyName, referenceSqlQueryProcessor);
        if (!sqlQueryProcessor.added) {
            referenceSqlQueryProcessor.added = true;
            referenceSqlQueryProcessor.columnsData = columnsData;
            sqlQueryProcessor.children = children;
            sqlQueryProcessor.aliasedSqlQueryProcessors = new HashMap<>();
            sqlQueryProcessor.columnsData = columnsData;
            sqlQueryProcessor.collectionOwnerColumnsData.putAll(collectionOwnerColumnsData);
            idColumnData.inherit = true;
            sqlQueryProcessor.collectionOwnerColumnsData.put(idColumnData.columnIndex, idColumnData);
            sqlQueryProcessor.recordProcessors = new ArrayList<>();
            sqlQueryProcessor.finalProcessors = new ArrayList<>();
            sqlQueryProcessor.expressionContext = new ExpressionContext(sqlQueryProcessor);
            sqlQueryProcessor.added = true;

            analyzeType(referenceDescription.getOwnerEntityDescription());
            finalProcessors.add((entityData, collectionOwner) -> {
                EntityData referenceEntityData = getReferencesFunction.apply(entityData).get(finalPropertyName);
                if (referenceEntityData != null && referenceEntityData.access && !referenceEntityData.invalid && !referenceEntityData.incorrectCasted) {
                    sqlQueryProcessor.finalProcessors.forEach(finalProcessor -> finalProcessor.accept(referenceEntityData, getCollectionOwner(collectionOwner, entityData)));
                }
            });
            sqlQueryProcessor.processIdColumn();
        }
        recordProcessors.add((entityData, resultSet) -> {
            if (checkEntityData(entityData, entityDescription)) {
                Map<String, EntityData> references = getReferencesFunction.apply(entityData);
                if (!references.containsKey(finalPropertyName)) {
                    EntityData referenceEntityData;
                    String referenceEntityId = wrap(() -> resultSet.getString(sqlQueryProcessor.idColumnData.columnIndex));
                    boolean access = referenceSqlQueryProcessor.getEntityAccess(resultSet);
                    if (referenceEntityId != null || !access) {
                        referenceEntityData = new EntityData();
                        if (alias != null) {
                            referenceEntityData.base = referenceDescription.getName();
                        }
                        referenceEntityData.id = referenceEntityId;
                        referenceEntityData.entityDescription = referenceDescription.getEntityDescription();
                        referenceEntityData.access = access;
                        sqlQueryProcessor.recordProcessors.forEach(recordProcessor -> recordProcessor.accept(referenceEntityData, resultSet));
                    } else {
                        referenceEntityData = null;
                    }
                    references.put(finalPropertyName, referenceEntityData);
                }
            }
        });

        if (specificationNode != null) {
            checkIsObject(specificationNode);
            checkFieldNames(specificationNode, EntitiesReadAccessJsonHelper.REFERENCE_SPECIFICATION_FIELD_NAMES);
            EntityDescription referenceEntityDescription = referenceDescription.getEntityDescription().cast(getEntityType(specificationNode));
            sqlQueryProcessor.processAlias(specificationNode.get(EntitiesReadAccessJsonHelper.ALIAS_FIELD_NAME), referenceEntityDescription, true);
            sqlQueryProcessor.expressionContext.aliasedEntitiesData = sqlQueryProcessor.aliasedEntitiesData;
            sqlQueryProcessor.processEntitySpecification(specificationNode);
            if (!referenceDescription.getEntityDescription().equals(referenceEntityDescription)) {
                recordProcessors.add((entityData, resultSet) -> {
                    if (checkEntityData(entityData, entityDescription)) {
                        EntityData referenceEntityData = getReferencesFunction.apply(entityData).get(finalPropertyName);
                        if (referenceEntityData != null && !referenceEntityData.entityDescription.isExtensionOf(referenceEntityDescription.getName())) {
                            referenceEntityData.incorrectCasted = true;
                        }
                    }
                });
            }
        }
    }

    /**
     * Process link specification
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processReferenceSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        processReferenceSpecification(entityData -> entityData.references, alias, entityDescription, entityDescription.getReferenceDescription(propertyName), specificationNode);
    }

    /**
     * Process link specification with backlink
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processBackReferenceReferenceSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        String finalPropertyName = getFinalPropertyName(propertyName, alias);
        ReferenceDescription backReferenceDescription = entityDescription.getReferenceBackReferenceDescription(propertyName);
        EntityDescription ownerEntityDescription = backReferenceDescription.getOwnerEntityDescription();
        SqlQueryProcessor backReferenceSqlQueryProcessor = getBackReferenceReferenceSqlQueryProcessor(backReferenceDescription);
        SqlQueryProcessor sqlQueryProcessor = getAliasedSqlQueryProcessor(finalPropertyName, backReferenceSqlQueryProcessor);
        if (!sqlQueryProcessor.added) {
            backReferenceSqlQueryProcessor.added = true;
            backReferenceSqlQueryProcessor.columnsData = columnsData;
            sqlQueryProcessor.children = children;
            sqlQueryProcessor.aliasedSqlQueryProcessors = new HashMap<>();
            sqlQueryProcessor.columnsData = columnsData;
            sqlQueryProcessor.collectionOwnerColumnsData.putAll(collectionOwnerColumnsData);
            idColumnData.inherit = true;
            sqlQueryProcessor.collectionOwnerColumnsData.put(idColumnData.columnIndex, idColumnData);
            sqlQueryProcessor.recordProcessors = new ArrayList<>();
            sqlQueryProcessor.finalProcessors = new ArrayList<>();
            sqlQueryProcessor.expressionContext = new ExpressionContext(sqlQueryProcessor);
            sqlQueryProcessor.added = true;

            finalProcessors.add((entityData, collectionOwner) -> {
                EntityData referenceEntityData = entityData.references.get(finalPropertyName);
                if (referenceEntityData != null && referenceEntityData.access && !referenceEntityData.invalid && !referenceEntityData.incorrectCasted) {
                    sqlQueryProcessor.finalProcessors.forEach(finalProcessor -> finalProcessor.accept(referenceEntityData, getCollectionOwner(collectionOwner, entityData)));
                }
            });
            sqlQueryProcessor.processIdColumn();
        }
        recordProcessors.add((entityData, resultSet) -> {
            if (checkEntityData(entityData, entityDescription) && !entityData.references.containsKey(finalPropertyName)) {
                EntityData referenceEntityData;
                String referenceEntityId = wrap(() -> resultSet.getString(sqlQueryProcessor.idColumnData.columnIndex));
                boolean access = backReferenceSqlQueryProcessor.getEntityAccess(resultSet);
                if (referenceEntityId != null || !access) {
                    referenceEntityData = new EntityData();
                    if (alias != null) {
                        referenceEntityData.base = propertyName;
                    }
                    referenceEntityData.id = referenceEntityId;
                    referenceEntityData.entityDescription = ownerEntityDescription;
                    referenceEntityData.access = access;
                    sqlQueryProcessor.recordProcessors.forEach(recordProcessor -> recordProcessor.accept(referenceEntityData, resultSet));
                } else {
                    referenceEntityData = null;
                }
                entityData.references.put(finalPropertyName, referenceEntityData);
            }
        });

        if (specificationNode != null) {
            checkIsObject(specificationNode);
            checkFieldNames(specificationNode, EntitiesReadAccessJsonHelper.REFERENCE_SPECIFICATION_FIELD_NAMES);
            EntityDescription referenceEntityDescription = ownerEntityDescription.cast(getEntityType(specificationNode));
            sqlQueryProcessor.processAlias(specificationNode.get(EntitiesReadAccessJsonHelper.ALIAS_FIELD_NAME), referenceEntityDescription, true);
            sqlQueryProcessor.expressionContext.aliasedEntitiesData = sqlQueryProcessor.aliasedEntitiesData;
            sqlQueryProcessor.processEntitySpecification(specificationNode);
            if (!ownerEntityDescription.equals(referenceEntityDescription)) {
                recordProcessors.add((entityData, resultSet) -> {
                    if (checkEntityData(entityData, entityDescription)) {
                        EntityData referenceEntityData = entityData.references.get(finalPropertyName);
                        if (referenceEntityData != null && !referenceEntityData.entityDescription.isExtensionOf(referenceEntityDescription.getName())) {
                            referenceEntityData.incorrectCasted = true;
                        }
                    }
                });
            }
        }
    }

    /**
     * Process link collection specification
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processReferencesCollectionSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        setIdOnlyFalse();
        ReferencesCollectionDescription referencesCollectionDescription = entityDescription.getReferencesCollectionDescription(propertyName);
        Node<String> tableAliasNode = getTableAliasNode();

        SqlQueryProcessor sqlQueryProcessor = new SqlQueryProcessor(this);
        sqlQueryProcessor.children = new ArrayList<>();
        sqlQueryProcessor.tablesData = new HashMap<>();
        sqlQueryProcessor.workColumnsData = new ArrayList<>(1);
        sqlQueryProcessor.idColumnData = new ColumnData(sqlQueryProcessor, DataType.STRING, getColumnNode(tableAliasNode, node(referencesCollectionDescription.getColumnName())));
        sqlQueryProcessor.mandatory = true;
        sqlQueryProcessor.workColumnsData.add(sqlQueryProcessor.idColumnData);
        ColumnData ownerColumnData = new ColumnData(sqlQueryProcessor, DataType.STRING, getColumnNode(tableAliasNode, node(referencesCollectionDescription.getOwnerColumnName())));
        ownerColumnData.inherit = true;
        sqlQueryProcessor.workColumnsData.add(ownerColumnData);
        Node<String> collectionTableNode = getTableNode(tableAliasNode, referencesCollectionDescription.getTableName());
        sqlQueryProcessor.getTableNodeFunctionPointer = new Pointer<>(table -> {
            table = getTableNodeFunctionPointer.object.apply(table);
            if (mergeRequestQueryIdNode == null) {
                return join(table, idColumnData.columnNode, collectionTableNode, ownerColumnData.columnNode);
            } else {
                return join(mergeRequestQueryIdNode, queryId, table, idColumnData.columnNode, collectionTableNode, ownerColumnData.columnNode);
            }
        });
        sqlQueryProcessor.localIdColumnsData = new HashMap<>();
        sqlQueryProcessor.primitiveColumnsData = new HashMap<>();
        sqlQueryProcessor.referenceSqlQueryProcessors = new HashMap<>();
        sqlQueryProcessor.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
        sqlQueryProcessor.aliasedSqlQueryProcessors = new HashMap<>();
        sqlQueryProcessor.aliasedEntitiesData = aliasedEntitiesData;
        sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
        sqlQueryProcessor.columnsData = new LinkedHashMap<>(collectionOwnerColumnsData);
        sqlQueryProcessor.columnsData.put(sqlQueryProcessor.getPrimitiveColumnIndex(DataType.STRING), ownerColumnData);
        sqlQueryProcessor.collectionOwnerColumnsData.putAll(sqlQueryProcessor.columnsData);
        sqlQueryProcessor.recordProcessors = new ArrayList<>();
        sqlQueryProcessor.finalProcessors = new ArrayList<>();
        sqlQueryProcessor.collections = new HashMap<>();
        sqlQueryProcessor.registerSqlQueryProcessor();
        sqlQueryProcessor.processIdColumn();
        String finalPropertyName = getFinalPropertyName(propertyName, alias);
        sqlQueryProcessor.processReferencesCollection(finalPropertyName, referencesCollectionDescription.getEntityDescription(), specificationNode);

        finalProcessors.add((entityData, collectionOwner) -> {
            if (checkEntityData(entityData, entityDescription)) {
                List<String> collectionOwner2 = getCollectionOwner(collectionOwner, entityData);
                int inheritanceDistance = getInheritanceDistance(entityData.entityDescription, entityDescription);
                Integer currentInheritanceDistance = inheritanceDistances.getOrDefault(collectionOwner2, Collections.emptyMap()).get(finalPropertyName);
                if (currentInheritanceDistance == null || inheritanceDistance < currentInheritanceDistance) {
                    inheritanceDistances.computeIfAbsent(collectionOwner2, key -> new HashMap<>()).put(finalPropertyName, inheritanceDistance);
                    Set<EntityData> referenceEntitiesData = sqlQueryProcessor.getCollectionForRead(collectionOwner2, finalPropertyName);
                    referenceEntitiesData.forEach(referenceEntityData -> sqlQueryProcessor.finalProcessors.forEach(finalProcessor -> finalProcessor.accept(referenceEntityData, collectionOwner2)));
                    entityData.referencesCollections.put(finalPropertyName, sqlQueryProcessor.getCollectionData(alias == null ? null : propertyName, collectionOwner2, referenceEntitiesData));
                }
            }
        });
    }

    /**
     * Process link collection specification with backlink
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processBackReferenceReferencesCollectionSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        setIdOnlyFalse();
        ReferenceDescription backReferenceDescription = entityDescription.getReferencesCollectionBackReferenceDescription(propertyName);
        EntityDescription ownerEntityDescription = backReferenceDescription.getOwnerEntityDescription();

        SqlQueryProcessor sqlQueryProcessor = new SqlQueryProcessor(this);
        sqlQueryProcessor.setIdOnlyFalse();
        sqlQueryProcessor.children = new ArrayList<>();
        sqlQueryProcessor.tablesData = new HashMap<>();
        TableData ownerEntityTableData = sqlQueryProcessor.getTableData(ownerEntityDescription);
        ownerEntityTableData.added = true;
        sqlQueryProcessor.workColumnsData = new ArrayList<>(1);
        sqlQueryProcessor.idColumnData = new ColumnData(sqlQueryProcessor, DataType.STRING, ownerEntityTableData.idColumnNode);
        sqlQueryProcessor.mandatory = true;
        sqlQueryProcessor.workColumnsData.add(sqlQueryProcessor.idColumnData);
        ColumnData ownerColumnData = new ColumnData(sqlQueryProcessor, DataType.STRING, getColumnNode(ownerEntityTableData.aliasNode, node(backReferenceDescription.getColumnName())));
        ownerColumnData.inherit = true;
        sqlQueryProcessor.workColumnsData.add(ownerColumnData);
        sqlQueryProcessor.getTableNodeFunctionPointer = new Pointer<>(table -> {
            table = getTableNodeFunctionPointer.object.apply(table);
            if (mergeRequestQueryIdNode == null) {
                return join(table, idColumnData.columnNode, ownerEntityTableData.tableNode, ownerColumnData.columnNode);
            } else {
                return join(mergeRequestQueryIdNode, queryId, table, idColumnData.columnNode, ownerEntityTableData.tableNode, ownerColumnData.columnNode);
            }
        });
        sqlQueryProcessor.localIdColumnsData = new HashMap<>();
        sqlQueryProcessor.localIdColumnsData.put(ownerEntityDescription, sqlQueryProcessor.idColumnData);
        sqlQueryProcessor.primitiveColumnsData = new HashMap<>();
        sqlQueryProcessor.referenceSqlQueryProcessors = new HashMap<>();
        sqlQueryProcessor.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
        sqlQueryProcessor.aliasedSqlQueryProcessors = new HashMap<>();
        sqlQueryProcessor.aliasedEntitiesData = aliasedEntitiesData;
        sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
        sqlQueryProcessor.columnsData = new LinkedHashMap<>(collectionOwnerColumnsData);
        sqlQueryProcessor.columnsData.put(sqlQueryProcessor.getPrimitiveColumnIndex(DataType.STRING), ownerColumnData);
        sqlQueryProcessor.collectionOwnerColumnsData.putAll(sqlQueryProcessor.columnsData);
        sqlQueryProcessor.recordProcessors = new ArrayList<>();
        sqlQueryProcessor.finalProcessors = new ArrayList<>();
        sqlQueryProcessor.collections = new HashMap<>();
        sqlQueryProcessor.registerSqlQueryProcessor();
        sqlQueryProcessor.processIdColumn();
        String finalPropertyName = getFinalPropertyName(propertyName, alias);
        sqlQueryProcessor.processReferencesCollection(finalPropertyName, ownerEntityDescription, specificationNode);

        finalProcessors.add((entityData, collectionOwner) -> {
            if (checkEntityData(entityData, entityDescription)) {
                List<String> collectionOwner2 = getCollectionOwner(collectionOwner, entityData);
                int inheritanceDistance = getInheritanceDistance(entityData.entityDescription, entityDescription);
                Integer currentInheritanceDistance = inheritanceDistances.getOrDefault(collectionOwner2, Collections.emptyMap()).get(finalPropertyName);
                if (currentInheritanceDistance == null || inheritanceDistance < currentInheritanceDistance) {
                    inheritanceDistances.computeIfAbsent(collectionOwner2, key -> new HashMap<>()).put(finalPropertyName, inheritanceDistance);
                    Set<EntityData> referenceEntitiesData = sqlQueryProcessor.getCollectionForRead(collectionOwner2, finalPropertyName);
                    referenceEntitiesData.forEach(referenceEntityData -> sqlQueryProcessor.finalProcessors.forEach(finalProcessor -> finalProcessor.accept(referenceEntityData, collectionOwner2)));
                    entityData.referencesCollections.put(finalPropertyName, sqlQueryProcessor.getCollectionData(alias == null ? null : propertyName, collectionOwner2, referenceEntitiesData));
                }
            }
        });
    }

    /**
     * Process grouping primitive specification
     *
     * @param groupDescription  Grouping description
     * @param entityDescription Entity description
     * @param groupAlias        Alias of grouping
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode Specification node
     */
    void processGroupPrimitiveSpecification(EntityDescription entityDescription, GroupDescription groupDescription, String groupAlias, String propertyName, String alias, JsonNode specificationNode) {
        processPrimitiveSpecification(entityData -> entityData.groups.computeIfAbsent(getFinalPropertyName(groupDescription.getName(), groupAlias), key -> {
            GroupData result = new GroupData();
            if (groupAlias != null) {
                result.base = groupDescription.getName();
            }
            return result;
        }).primitives, entityDescription, groupDescription.getPrimitiveDescription(propertyName), alias, specificationNode);
    }

    /**
     * Process grouping link specification
     *
     * @param groupDescription  Grouping description
     * @param entityDescription Entity description
     * @param groupAlias        Group alias
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode Specification node
     */
    void processGroupReferenceSpecification(EntityDescription entityDescription, GroupDescription groupDescription, String groupAlias, String propertyName, String alias, JsonNode specificationNode) {
        processReferenceSpecification(entityData -> entityData.groups.computeIfAbsent(getFinalPropertyName(groupDescription.getName(), groupAlias), key -> {
            GroupData result = new GroupData();
            if (groupAlias != null) {
                result.base = groupDescription.getName();
            }
            return result;
        }).references, alias, entityDescription, groupDescription.getReferenceDescription(propertyName), specificationNode);
    }

    /**
     * Process grouping specification
     *
     * @param propertyProcessFunctions Functions for processing properties
     * @param entityDescription        Entity description
     * @param groupDescription         Description of grouping
     * @param groupAlias               Group alias
     * @param propertyName             Property name
     * @param specificationNode        Specification node
     */
    void processGroupSpecification(Map<String, Procedure7<SqlQueryProcessor, EntityDescription, GroupDescription, String, String, String, JsonNode>> propertyProcessFunctions, EntityDescription entityDescription, GroupDescription groupDescription, String groupAlias, String propertyName, JsonNode specificationNode) {
        String alias = null;
        if (specificationNode != null && specificationNode.isObject()) {
            JsonNode baseNode = specificationNode.get(EntitiesReadAccessJsonHelper.BASE_PROPERTY_FIELD_NAME);
            if (baseNode != null) {
                checkFieldNames(baseNode, EntitiesReadAccessJsonHelper.ALIASED_PROPERTY_SPECIFICATION_FIELD_NAMES);
                alias = propertyName;
                propertyName = getString(baseNode);
                specificationNode = specificationNode.get(EntitiesReadAccessJsonHelper.SPECIFICATION_FIELD_NAME);
            }
        }
        Procedure7<SqlQueryProcessor, EntityDescription, GroupDescription, String, String, String, JsonNode> propertyProcessFunction = propertyProcessFunctions.get(propertyName);
        if (propertyProcessFunction == null) {
            throw new PropertyNotFoundException(entityDescription.getName(), propertyName);
        }
        checkPropertyType(propertyName, alias);
        propertyProcessFunction.call(this, entityDescription, groupDescription, groupAlias, propertyName, alias, specificationNode);
    }

    /**
     * Process the grouping specification
     *
     * @param entityDescription Entity description
     * @param propertyName      Property name
     * @param alias             Alias
     * @param specificationNode The specification node
     */
    void processGroupSpecification(EntityDescription entityDescription, String propertyName, String alias, JsonNode specificationNode) {
        if (specificationNode == null) {
            throw new GroupSpecificationNotSetException();
        }
        GroupDescription groupDescription = entityDescription.getGroupDescription(propertyName);
        SqlQueryProcessor sqlQueryProcessor = getAliasedSqlQueryProcessor(getFinalPropertyName(propertyName, alias), this);
        if (!sqlQueryProcessor.added) {
            sqlQueryProcessor.children = children;
            sqlQueryProcessor.aliasedSqlQueryProcessors = new HashMap<>();
            sqlQueryProcessor.columnsData = columnsData;
            sqlQueryProcessor.collectionOwnerColumnsData = collectionOwnerColumnsData;
            sqlQueryProcessor.recordProcessors = new ArrayList<>();
            sqlQueryProcessor.finalProcessors = new ArrayList<>();
            sqlQueryProcessor.added = true;
            sqlQueryProcessor.inheritanceDistances = inheritanceDistances;

            recordProcessors.add((entityData, resultSet) -> sqlQueryProcessor.recordProcessors.forEach(recordProcessor -> recordProcessor.accept(entityData, resultSet)));
            finalProcessors.add((entityData, collectionOwner) -> sqlQueryProcessor.finalProcessors.forEach(finalProcessors2 -> finalProcessors2.accept(entityData, collectionOwner)));
        }
        Map<String, Procedure7<SqlQueryProcessor, EntityDescription, GroupDescription, String, String, String, JsonNode>> propertyProcessFunctions = groupDescription.getMetaDataManager().get(GroupDescriptionMetaData.class).propertyProcessFunctions;
        if (specificationNode.isTextual()) {
            sqlQueryProcessor.processGroupSpecification(propertyProcessFunctions, entityDescription, groupDescription, alias, specificationNode.textValue(), null);
        } else if (specificationNode.isObject()) {
            specificationNode.fields().forEachRemaining(entry -> sqlQueryProcessor.processGroupSpecification(propertyProcessFunctions, entityDescription, groupDescription, alias, entry.getKey(), entry.getValue()));
        } else if (specificationNode.isArray()) {
            Pointer<Boolean> objectPresentedPointer = new Pointer<>(Boolean.FALSE);
            specificationNode.elements().forEachRemaining(elementNode -> {
                if (elementNode.isTextual()) {
                    sqlQueryProcessor.processGroupSpecification(propertyProcessFunctions, entityDescription, groupDescription, alias, elementNode.textValue(), null);
                } else if (elementNode.isObject()) {
                    if (Boolean.TRUE.equals(objectPresentedPointer.object)) {
                        throw new OnlyOneObjectInPropertiesPermittedException(specificationNode.toString());
                    }
                    objectPresentedPointer.object = Boolean.TRUE;
                    elementNode.fields().forEachRemaining(entry -> sqlQueryProcessor.processGroupSpecification(propertyProcessFunctions, entityDescription, groupDescription, alias, entry.getKey(), entry.getValue()));
                } else {
                    throw new UnsupportedNodeException(elementNode.toString());
                }
            });
        } else {
            throw new UnsupportedNodeException(specificationNode.toString());
        }
    }

    /**
     * Process a computable expression
     *
     * @param alias            Alias
     * @param expressionString The expression string
     * @param propertyType     Property type
     */
    void processCalculatedExpressionExpression(String alias, String expressionString, PropertyType propertyType) {
        PrimitiveExpressionImpl primitiveExpression = getPrimitiveExpression(expressionString);
        primitiveExpression.prepare(requestData.startSqlQueryProcessor, expressionContext);
        ColumnData columnData = new ColumnData(this, primitiveExpression.type, null);
        requestData.calculatedExpressions.put(columnData, primitiveExpression);
        columnData.columnIndex = getPrimitiveColumnIndex(columnData.type);
        workColumnsData.add(columnData);
        columnsData.put(columnData.columnIndex, columnData);
        Function3<Object, SqlDialect, ResultSet, Integer> getPrimitiveValueFunction = primitiveExpression.type.getMetaDataManager().get(DataTypeMetaData.class).getPrimitiveValueFunction;
        recordProcessors.add((entityData, resultSet) -> {
            PrimitiveData primitiveData = new PrimitiveData(primitiveExpression.type, getPrimitiveValueFunction.call(requestData.sqlDialect, resultSet, columnData.columnIndex));
            primitiveData.propertyType = propertyType;
            entityData.primitives.put(alias, primitiveData);
        });
    }

    /**
     * Check property type
     *
     * @param propertyName Property name
     * @param alias        Alias
     */
    void checkPropertyType(String propertyName, String alias) {
        String finalPropertyName = getFinalPropertyName(propertyName, alias);
        PropertyData propertyData = propertiesData.get(finalPropertyName);
        if (propertyData == null) {
            propertiesData.put(finalPropertyName, new PropertyData(propertyName, alias));
        } else {
            propertyData.check(new PropertyData(propertyName, alias));
        }
    }

    /**
     * Handle property specification
     *
     * @param propertyProcessFunctions Functions for processing properties
     * @param entityDescription        Entity description
     * @param propertyName             Property name
     * @param specificationNode        Specification node
     */
    void processPropertySpecification(Map<String, Procedure5<SqlQueryProcessor, EntityDescription, String, String, JsonNode>> propertyProcessFunctions, EntityDescription entityDescription, String propertyName, JsonNode specificationNode) {
        String alias = null;
        setIdOnlyFalse();
        if (specificationNode != null && specificationNode.isObject()) {
            JsonNode basePropertyNode = specificationNode.get(EntitiesReadAccessJsonHelper.BASE_PROPERTY_FIELD_NAME);
            JsonNode calculatedExpressionNode = specificationNode.get(EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_FIELD_NAME);
            if (basePropertyNode != null) {
                checkFieldNames(specificationNode, EntitiesReadAccessJsonHelper.ALIASED_PROPERTY_SPECIFICATION_FIELD_NAMES);
                alias = propertyName;
                propertyName = getString(basePropertyNode);
                specificationNode = specificationNode.get(EntitiesReadAccessJsonHelper.SPECIFICATION_FIELD_NAME);
            } else if (calculatedExpressionNode != null) {
                checkFieldNames(specificationNode, EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_SPECIFICATION_FIELD_NAMES);
                checkPropertyType(null, propertyName);
                processCalculatedExpressionExpression(propertyName, getString(calculatedExpressionNode), PropertyType.CALCULATED);
                return;
            }
        }
        Procedure5<SqlQueryProcessor, EntityDescription, String, String, JsonNode> propertyProcessFunction = propertyProcessFunctions.get(propertyName);
        if (propertyProcessFunction == null) {
            throw new PropertyNotFoundException(entityDescription.getName(), propertyName);
        }
        checkPropertyType(propertyName, alias);
        propertyProcessFunction.call(this, entityDescription, propertyName, alias, specificationNode);
    }

    /**
     * Process properties
     *
     * @param entityDescription Entity description
     * @param propertiesNode    Property node
     */
    void processProperties(EntityDescription entityDescription, JsonNode propertiesNode) {
        Map<String, Procedure5<SqlQueryProcessor, EntityDescription, String, String, JsonNode>> propertyProcessFunctions = entityDescription.getMetaDataManager().get(EntityDescriptionMetaData.class).propertyProcessFunctions;
        if (propertiesNode.isTextual()) {
            processPropertySpecification(propertyProcessFunctions, entityDescription, propertiesNode.textValue(), null);
        } else if (propertiesNode.isObject()) {
            propertiesNode.fields().forEachRemaining(entry -> processPropertySpecification(propertyProcessFunctions, entityDescription, entry.getKey(), entry.getValue()));
        } else if (propertiesNode.isArray()) {
            Pointer<Boolean> objectPresentedPointer = new Pointer<>(Boolean.FALSE);
            propertiesNode.elements().forEachRemaining(elementNode -> {
                if (elementNode.isTextual()) {
                    processPropertySpecification(propertyProcessFunctions, entityDescription, elementNode.textValue(), null);
                } else if (elementNode.isObject()) {
                    if (Boolean.TRUE.equals(objectPresentedPointer.object)) {
                        throw new OnlyOneObjectInPropertiesPermittedException(propertiesNode.toString());
                    }
                    objectPresentedPointer.object = Boolean.TRUE;
                    elementNode.fields().forEachRemaining(entry -> processPropertySpecification(propertyProcessFunctions, entityDescription, entry.getKey(), entry.getValue()));
                } else {
                    throw new UnsupportedNodeException(elementNode.toString());
                }
            });
        } else {
            throw new UnsupportedNodeException(propertiesNode.toString());
        }
    }

    /**
     * Process entity specification
     *
     * @param specificationNode The specification node
     */
    void processEntitySpecification(JsonNode specificationNode) {
        EntityDescription entityDescription2 = entityDescription.cast(getEntityType(specificationNode));
        if (!entityDescription.equals(entityDescription2)) {
            analyzeType(entityDescription2);
        }
        JsonNode propertiesNode = specificationNode.get(EntitiesReadAccessJsonHelper.PROPERTIES_FIELD_NAME);
        if (propertiesNode != null) {
            processProperties(entityDescription2, propertiesNode);
        }
        JsonNode detailsNode = specificationNode.get(EntitiesReadAccessJsonHelper.DETAILS_FIELD_NAME);
        if (detailsNode != null) {
            checkIsObject(detailsNode);
            detailsNode.fields().forEachRemaining(entry -> {
                EntityDescription entityDescription3 = entityDescription2.cast(entry.getKey());
                if (!entityDescription2.equals(entityDescription3)) {
                    analyzeType(entityDescription3);
                }
                processProperties(entityDescription3, entry.getValue());
            });
        }
        processAggregateSettings(specificationNode.get(EntitiesReadAccessJsonHelper.AGGREGATE_VERSION_FIELD_NAME));
    }

    void processLock() {
        JsonNode lockNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.LOCK_FIELD_NAME);
        if (lockNode != null) {
            String lockModeString = getString(lockNode);
            if (!EXPECTED_LOCK_MODES.contains(lockModeString.toLowerCase(Locale.ENGLISH))) {
                throw new UnexpectedLockModeException(EXPECTED_LOCK_MODES, lockModeString);
            }
            if (EntitiesReadAccessJsonHelper.WAIT.equalsIgnoreCase(lockModeString)) {
                lockMode = LockMode.WAIT;
            } else {
                lockMode = LockMode.NOWAIT;
            }
        }
    }

    /**
     * Process the unit settings
     */
    void processAggregateSettings(JsonNode aggregateVersionNode) {
        if (aggregateVersionNode != null && getBoolean(aggregateVersionNode)) {
            EntityDescription rootEntityDescription = entityDescription.getRootEntityDescription();
            if (!rootEntityDescription.isAggregate() && rootEntityDescription.getAggregateEntityDescription() == null) {
                throw new NotAggregateEntityDescriptionAndNotBelongsToOneException(requestData.startSqlQueryProcessor.entityDescription.getName());
            }
            EntityDescription aggregateEntityDescription = rootEntityDescription.getAggregateEntityDescription() == null ? rootEntityDescription : rootEntityDescription.getAggregateEntityDescription();
            Node<String> tableAliasNode = getTableAliasNode();
            Node<String> systemLocksTableNode = getTableNode(tableAliasNode, aggregateEntityDescription.getSystemLocksTableName());
            Node<String> systemLocksAggregateColumnNode = getColumnNode(tableAliasNode, node(aggregateEntityDescription.getSystemLocksAggregateColumnName()));
            if (rootEntityDescription.getAggregateEntityDescription() == null) {
                UnaryOperator<Node<String>> currentGetTableNodeFunction = getTableNodeFunctionPointer.object;
                getTableNodeFunctionPointer.object = tableNode -> leftJoin(currentGetTableNodeFunction.apply(tableNode), idColumnData.columnNode, systemLocksTableNode, systemLocksAggregateColumnNode);
            } else {
                TableData rootEntityTableData = getTableData(rootEntityDescription);
                addTable(rootEntityTableData);
                Node<String> aggregateColumnNode = getColumnNode(rootEntityTableData.aliasNode, node(rootEntityDescription.getAggregateColumnName()));
                Node<String> columnNode;
                if (rootEntityDescription.isAggregate()) {
                    columnNode = node(Helper.COALESCE_NODE, aggregateColumnNode, Helper.COMMA_NODE, idColumnData.columnNode, Helper.BRACKET_R_NODE);
                } else {
                    columnNode = aggregateColumnNode;
                }
                UnaryOperator<Node<String>> currentGetTableNodeFunction = getTableNodeFunctionPointer.object;
                getTableNodeFunctionPointer.object = tableNode -> leftJoin(currentGetTableNodeFunction.apply(tableNode), columnNode, systemLocksTableNode, systemLocksAggregateColumnNode);
            }
            ColumnData systemLocksVersionColumnData = new ColumnData(this, DataType.LONG, getColumnNode(tableAliasNode, node(aggregateEntityDescription.getSystemLocksVersionColumnName())));
            workColumnsData.add(systemLocksVersionColumnData);
            int aggregateVersionColumnIndex = getPrimitiveColumnIndex(DataType.LONG);
            columnsData.put(aggregateVersionColumnIndex, systemLocksVersionColumnData);
            recordProcessors.add((entityData, resultSet) -> {
                Long aggregateVersion = GET_LONG_FUNCTION.apply(resultSet, aggregateVersionColumnIndex);
                entityData.aggregateVersion = aggregateVersion == null ? 0 : aggregateVersion;
            });
        }
    }

    /**
     * Initialize condition string node
     */
    void initConditionStringNode() {
        if (condition == null) {
            if (getAdditionalConditionNodeFunctionPointer != null && getAdditionalConditionNodeFunctionPointer.object != null) {
                conditionStringNode = getAdditionalConditionNodeFunctionPointer.object.get();
            }
        } else {
            conditionStringNode = condition.get();
            if (getAdditionalConditionNodeFunctionPointer != null && getAdditionalConditionNodeFunctionPointer.object != null) {
                conditionStringNode = getAndStringNode(conditionStringNode, getAdditionalConditionNodeFunctionPointer.object.get());
            }
        }
    }

    /**
     * Get column nodes
     *
     * @param firstRow            Is it the first row
     * @param appendColumnAlias   Add column alias
     * @param size                Размер
     * @param getTypeFunction     Function to obtain type
     * @param firstColumnNode     First column node
     * @param columnIndexesStream Stream of column indexes
     * @param columnsData         Column data
     *                            <p>
     *                            В данном случае "данные колонок" переведено на английский как "column data", и произведена замена в исходном тексте.
     */
    List<Node<String>> getColumnNodes(boolean firstRow, boolean appendColumnAlias, int size, IntFunction<DataType> getTypeFunction, Node<String> firstColumnNode, IntStream columnIndexesStream, Map<Integer, ColumnData> columnsData) {
        List<Node<String>> result = new ArrayList<>(size);
        if (firstColumnNode != null) {
            result.add(firstColumnNode);
        }
        IntFunction<Node<String>> getNullFunction = firstRow ? (index -> requestData.sqlDialect.null0(getTypeFunction.apply(index))) : (index -> Helper.NULL_NODE);
        columnIndexesStream.forEach(columnIndex -> {
            ColumnData columnData = columnsData.get(columnIndex);
            Node<String> columnNode = columnData == null ? getNullFunction.apply(columnIndex) : columnData.columnNode;
            if (appendColumnAlias) {
                columnNode = node(columnNode, Helper.SPACE_NODE, COLUMN_ALIAS_NODES.get(columnIndex));
            }
            result.add(columnNode);
        });
        return result;
    }

    /**
     * Get column nodes
     *
     * @param appendColumnAlias Add column alias
     */
    List<Node<String>> getColumnNodes(boolean appendColumnAlias) {
        return getColumnNodes(this == requestData.startSqlQueryProcessor, appendColumnAlias, requestData.columnTypes.size(), index -> requestData.columnTypes.get(index - 1), QUERY_ID_NODES.get(queryId), IntStream.rangeClosed(2, requestData.columnTypes.size()), columnsData);
    }

    /**
     * Get column nodes
     */
    List<Node<String>> getColumnNodes() {
        return getColumnNodes(false);
    }

    /**
     * Get sorting column nodes
     *
     * @param getCriterionNodeFunction The function for obtaining the criterion node
     */
    List<Node<String>> getSortColumnNodes(Function<SortCriterionData, Node<String>> getCriterionNodeFunction) {
        List<Node<String>> result = new ArrayList<>(sortCriteriaData.size());
        sortCriteriaData.forEach(sortCriterionData -> {
            List<Node<String>> nodes = new ArrayList<>(3);
            nodes.add(getCriterionNodeFunction.apply(sortCriterionData));
            if (sortCriterionData.descending) {
                nodes.add(Helper.DESC_NODE);
            }
            if (sortCriterionData.nullsLast != null) {
                nodes.add(Boolean.TRUE.equals(sortCriterionData.nullsLast) ? Helper.NULLS_LAST_NODE : Helper.NULLS_FIRST_NODE);
            }
            result.add(node(nodes));
        });
        return result;
    }

    /**
     * Get request node
     *
     * @param distinct                 Choose whether to select unique sets
     * @param columnNodes              Column nodes
     * @param tableNode                Table node
     * @param conditionStringNode      The condition string node
     * @param groupColumnNodes         Grouping column nodes
     * @param groupConditionStringNode Nodes of grouping condition string
     * @param sortColumnNodes          Sorting columns nodes
     * @param offset                   Offset
     * @param limit                    The limitation on the number of elements
     */
    Node<String> getQueryNode(boolean distinct, List<Node<String>> columnNodes, Node<String> tableNode, Node<String> conditionStringNode, List<Node<String>> groupColumnNodes, Node<String> groupConditionStringNode, List<Node<String>> sortColumnNodes, Integer offset, Integer limit) {
        List<Node<String>> nodes = new ArrayList<>(6 + (columnNodes.size() + groupColumnNodes.size() + sortColumnNodes.size()) * 2);
        nodes.add(Helper.SELECT_NODE);
        if (distinct) {
            nodes.add(Helper.DISTINCT_NODE);
        }
        addNodeListToNodes(nodes, Helper.COMMA_NODE, columnNodes.stream());
        if (tableNode != null) {
            nodes.add(Helper.FROM_NODE);
            nodes.add(tableNode);
        }
        if (conditionStringNode != null) {
            nodes.add(Helper.WHERE_NODE);
            nodes.add(conditionStringNode);
        }
        if (!groupColumnNodes.isEmpty()) {
            nodes.add(Helper.GROUP_BY_NODE);
            addNodeListToNodes(nodes, Helper.COMMA_NODE, groupColumnNodes.stream());
        }
        if (groupConditionStringNode != null) {
            nodes.add(Helper.HAVING_NODE);
            nodes.add(groupConditionStringNode);
        }
        if (!sortColumnNodes.isEmpty()) {
            nodes.add(Helper.ORDER_BY_NODE);
            addNodeListToNodes(nodes, Helper.COMMA_NODE, sortColumnNodes.stream());
        }
        if (limit != null || offset != null) {
            nodes.add(requestData.sqlDialect.limitAndOffset(limit == null ? null : addParameter(limit), offset == null ? null : addParameter(offset)));
        }
        return node(nodes);
    }

    /**
     * Get request node
     *
     * @param columnNodes              Column nodes
     * @param tableNode                Table node
     * @param conditionStringNode      The condition string node
     * @param groupColumnNodes         Grouping columns nodes
     * @param groupConditionStringNode Nodes of grouping condition string
     * @param sortColumnNodes          Sorting column nodes
     * @param offset                   Offset
     * @param limit                    The limitation on the number of elements
     */
    Node<String> getQueryNode(List<Node<String>> columnNodes, Node<String> tableNode, Node<String> conditionStringNode, List<Node<String>> groupColumnNodes, Node<String> groupConditionStringNode, List<Node<String>> sortColumnNodes, Integer offset, Integer limit) {
        return getQueryNode(false, columnNodes, tableNode, conditionStringNode, groupColumnNodes, groupConditionStringNode, sortColumnNodes, offset, limit);
    }

    /**
     * Get request node
     *
     * @param columnNodes         Column nodes
     * @param tableNode           Table node
     * @param conditionStringNode Condition string node
     */
    Node<String> getQueryNode(List<Node<String>> columnNodes, Node<String> tableNode, Node<String> conditionStringNode) {
        return getQueryNode(columnNodes, tableNode, conditionStringNode, Collections.emptyList(), null, Collections.emptyList(), null, null);
    }

    /**
     * Get the conditional node based on the limit restriction on the number of elements and offset
     *
     * @param rowNumberColumnNode The node of the column with the row number
     */
    Node<String> getLimitAndOffsetBasedConditionStringNode(Node<String> rowNumberColumnNode) {
        if (nullLimitAndOffset) {
            return null;
        }
        List<Node<String>> nodes = new ArrayList<>(7);
        if (offset != null) {
            nodes.add(rowNumberColumnNode);
            nodes.add(Helper.GT_NODE);
            nodes.add(addParameter(offset));
        }
        if (limit != null) {
            int currentLimit = limit;
            if (offset != null) {
                currentLimit += offset;
                nodes.add(Helper.AND_NODE);
            }
            nodes.add(rowNumberColumnNode);
            nodes.add(Helper.LT_OR_EQ_NODE);
            nodes.add(addParameter(currentLimit));
        }
        return node(nodes);
    }

    /**
     * Perform column replacement
     *
     * @param columnsData      The data of the columns
     * @param tableAliasNode   Table alias node
     * @param columnAliasNodes Aliases of columns nodes
     * @param code             Код
     */
    void executeWithColumnsReplace(Collection<ColumnData> columnsData, Node<String> tableAliasNode, List<Node<String>> columnAliasNodes, Runnable code) {
        List<Node<String>> backUp = new ArrayList<>(columnsData.size());
        Pointer<Integer> indexPointer = new Pointer<>(0);
// Changing column names to selections from common table expressions (t0.OBJECT_ID -> ct0.t0)
        columnsData.forEach(columnData -> {
            backUp.add(columnData.columnNode);
            columnData.columnNode = getColumnNode(tableAliasNode, columnAliasNodes.get(indexPointer.object++));
        });

        code.run();

        indexPointer.object = 0;
        columnsData.forEach(columnData -> columnData.columnNode = backUp.get(indexPointer.object++));
    }

    /**
     * Process parameters
     *
     * @param jsonNode JSON node
     */
    void processParams(JsonNode jsonNode) {
        paramsNode = jsonNode.get(EntitiesReadAccessJsonHelper.PARAMS_FIELD_NAME);
        if (paramsNode != null) {
            checkIsObject(paramsNode);
        }
    }

    /**
     * Process request
     */
    void processRequest() {
// To read the type from the JSON and get its meta information
        processParams(requestData.requestNode);
        entityDescription = requestData.modelDescription.getEntityDescription(getRequiredEntityType(requestData.requestNode));
        children = new ArrayList<>();
        tablesData = new HashMap<>();
        TableData startTableData = getTableData(entityDescription);
        startTableData.added = true;
        workColumnsData = new ArrayList<>(1);
        idColumnData = new ColumnData(this, DataType.STRING, startTableData.idColumnNode);
        mandatory = true;
        mandatoryEntityDescription = entityDescription;
        workColumnsData.add(idColumnData);
        getTableNodeFunctionPointer = new Pointer<>(table -> startTableData.tableNode);
        localIdColumnsData = new HashMap<>();
        localIdColumnsData.put(entityDescription, idColumnData);
        primitiveColumnsData = new HashMap<>();
        referenceSqlQueryProcessors = new HashMap<>();
        backReferenceReferenceSqlQueryProcessors = new HashMap<>();
        aliasedSqlQueryProcessors = new HashMap<>();
        aliasedEntitiesData = Collections.emptyMap();
        getAdditionalConditionNodeFunctionPointer = new Pointer<>();
        processSingleTableInheritanceStrategy();
        columnsData = new HashMap<>();
        recordProcessors = new ArrayList<>();
        finalProcessors = new ArrayList<>();
        expressionContext = ExpressionContext.EMPTY;
        registerSqlQueryProcessor();
        processSearchSettings(requestData.requestNode);
        processIdColumn();
        processEntitySpecification(requestData.requestNode);
    }

    void initForCondition(Node<String> idStringNode) {
        processParams(requestData.requestNode);
        entityDescription = requestData.modelDescription.getEntityDescription(getRequiredEntityType(requestData.requestNode));
        children = new ArrayList<>();
        tablesData = new HashMap<>();
        TableData startTableData = getTableData(entityDescription);
        startTableData.added = true;
        workColumnsData = new ArrayList<>(1);
        idColumnData = new ColumnData(this, DataType.STRING, startTableData.idColumnNode);
        mandatory = true;
        mandatoryEntityDescription = entityDescription;
        workColumnsData.add(idColumnData);
        getTableNodeFunctionPointer = new Pointer<>(table -> startTableData.tableNode);
        localIdColumnsData = new HashMap<>();
        localIdColumnsData.put(entityDescription, idColumnData);
        primitiveColumnsData = new HashMap<>();
        referenceSqlQueryProcessors = new HashMap<>();
        backReferenceReferenceSqlQueryProcessors = new HashMap<>();
        aliasedSqlQueryProcessors = new HashMap<>();
        aliasedEntitiesData = Collections.emptyMap();
        getAdditionalConditionNodeFunctionPointer = new Pointer<>();
        processSingleTableInheritanceStrategy();
        columnsData = new HashMap<>();
        recordProcessors = new ArrayList<>();
        finalProcessors = new ArrayList<>();
        expressionContext = ExpressionContext.EMPTY;
        registerSqlQueryProcessor();
        processSearchSettings(requestData.requestNode);
        Supplier<Node<String>> currentGetAdditionalConditionNodeFunction = getAdditionalConditionNodeFunctionPointer.object;
        Node<String> eqCondition = node(idColumnData.columnNode, EQ_NODE, idStringNode);
        if (currentGetAdditionalConditionNodeFunction == null) {
            getAdditionalConditionNodeFunctionPointer.object = () -> eqCondition;
        } else {
            getAdditionalConditionNodeFunctionPointer.object = () -> getAndStringNode(eqCondition, currentGetAdditionalConditionNodeFunction.get());
        }

    }

    void processLockRequest() {
        checkExtraFields(requestData.requestNode, EntitiesReadAccessJsonHelper.LOCK_REQUEST_SPECIFICATION_FIELD_NAMES, ExtraFieldsFoundForPropertiesSelectionException::new);

        processParams(requestData.requestNode);
        entityDescription = requestData.modelDescription.getEntityDescription(getRequiredEntityType(requestData.requestNode));
        children = new ArrayList<>();
        tablesData = new HashMap<>();
        TableData startTableData = getTableData(entityDescription.getRootEntityDescription());
        startTableData.added = true;
        workColumnsData = new ArrayList<>(1);
        idColumnData = new ColumnData(this, DataType.STRING, startTableData.idColumnNode);
        mandatory = true;
        mandatoryEntityDescription = entityDescription;
        workColumnsData.add(idColumnData);
        getTableNodeFunctionPointer = new Pointer<>(table -> startTableData.tableNode);
        localIdColumnsData = new HashMap<>();
        localIdColumnsData.put(entityDescription, idColumnData);
        primitiveColumnsData = new HashMap<>();
        referenceSqlQueryProcessors = new HashMap<>();
        backReferenceReferenceSqlQueryProcessors = new HashMap<>();
        aliasedSqlQueryProcessors = new HashMap<>();
        aliasedEntitiesData = Collections.emptyMap();
        getAdditionalConditionNodeFunctionPointer = new Pointer<>();
        columnsData = new HashMap<>();
        recordProcessors = new ArrayList<>();
        finalProcessors = new ArrayList<>();
        expressionContext = ExpressionContext.EMPTY;
        registerSqlQueryProcessor();
        initLimitOffsetSettings(requestData.requestNode);
        processIdColumn();
        processLock();

        JsonNode conditionNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME);
        if (conditionNode == null) {
            throw new ConditionNotSetException();
        }

        RequestData subQueryRequestData = new RequestData();
        subQueryRequestData.modelDescription = requestData.modelDescription;
        subQueryRequestData.expressionsProcessor = requestData.expressionsProcessor;
        subQueryRequestData.securityDriver = requestData.securityDriver;
        subQueryRequestData.sqlDialect = requestData.sqlDialect;
        subQueryRequestData.defaultLimit = requestData.defaultLimit;
        subQueryRequestData.schemaNameNode = requestData.schemaNameNode;
        subQueryRequestData.maxSecurityRecursionDepth = requestData.maxSecurityRecursionDepth;
        subQueryRequestData.tableQueryProvider = requestData.tableQueryProvider;
        subQueryRequestData.optimizeJoins = requestData.optimizeJoins;
        ObjectNode requestNode = OBJECT_MAPPER.createObjectNode();
        requestNode.set(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, requestData.requestNode.get(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME));
        requestNode.set(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME, requestData.requestNode.get(EntitiesReadAccessJsonHelper.CONDITION_FIELD_NAME));
        JsonNode securityConditionNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.SECURITY_CONDITION_FIELD_NAME);
        if (securityConditionNode != null) {
            requestNode.set(EntitiesReadAccessJsonHelper.SECURITY_CONDITION_FIELD_NAME, securityConditionNode);
        }
        subQueryRequestData.requestNode = requestNode;
        subQueryRequestData.params = requestData.params;
        subQueryRequestData.sqlQueryProcessors = new ArrayList<>(1);
        subQueryRequestData.lastTableIndexPointer = requestData.lastTableIndexPointer;
        subQueryRequestData.lastParameterIndexPointer = requestData.lastParameterIndexPointer;
        subQueryRequestData.mapSqlParameterSource = requestData.mapSqlParameterSource;
        subQueryRequestData.columnTypes = new ArrayList<>(1);
        subQueryRequestData.columnTypes.add(DataType.INTEGER);
        subQueryRequestData.primitiveColumnIndexes = new EnumMap<>(DataType.class);
        subQueryRequestData.countColumnIndexPointer = new Pointer<>();
        subQueryRequestData.commonTableNodes = new ArrayList<>();
        subQueryRequestData.calculatedExpressions = new LinkedHashMap<>();
        subQueryRequestData.allSqlQueryProcessors = new ArrayList<>();
        subQueryRequestData.startSqlQueryProcessor = new SqlQueryProcessor(subQueryRequestData);
        subQueryRequestData.startSqlQueryProcessor.initForCondition(idColumnData.columnNode);
        subQueryRequestData.startSqlQueryProcessor.processSecurity();
        subQueryRequestData.startSqlQueryProcessor.process2();
        subQueryRequestData.startSqlQueryProcessor.process3();

        conditionStringNode = node(Helper.EXISTS_NODE, Helper.BRACKET_L_NODE, subQueryRequestData.startSqlQueryProcessor.queryNode, Helper.BRACKET_R_NODE);
    }

    /**
     * Merge requests processing
     *
     * @param mergeRequestsNode Merge requests node
     */
    void processRequestsMerge(JsonNode mergeRequestsNode) {
        checkExtraFields(requestData.requestNode, EntitiesReadAccessJsonHelper.REQUESTS_MERGE_SPECIFICATION_FIELD_NAMES, ExtraFieldsFoundForRequestsMergeException::new);
        checkIsArray(mergeRequestsNode);
        if (mergeRequestsNode.size() < 2) {
            throw new NotEnoughMergeRequestsCountException(mergeRequestsNode.size(), mergeRequestsNode.toString());
        }

        children = new ArrayList<>(mergeRequestsNode.size());
        JsonNode limitNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.LIMIT_FIELD_NAME);
        JsonNode offsetNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.OFFSET_FIELD_NAME);
        JsonNode countNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME);
        JsonNode sortNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.SORT_FIELD_NAME);
        mergeRequestsNode.elements().forEachRemaining(mergeRequestNode -> {
            checkIsObject(mergeRequestNode);
            checkFieldNames(mergeRequestNode, EntitiesReadAccessJsonHelper.MERGE_REQUEST_SPECIFICATION_FIELD_NAMES);

            ObjectNode mergeRequestNode2 = (ObjectNode) mergeRequestNode;
            if (limitNode != null) {
                mergeRequestNode2.set(EntitiesReadAccessJsonHelper.LIMIT_FIELD_NAME, limitNode);
            }
            if (offsetNode != null) {
                mergeRequestNode2.set(EntitiesReadAccessJsonHelper.OFFSET_FIELD_NAME, offsetNode);
            }
            if (countNode != null) {
                mergeRequestNode2.set(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, countNode);
            }
            if (sortNode != null) {
                mergeRequestNode2.set(EntitiesReadAccessJsonHelper.SORT_FIELD_NAME, sortNode);
            }

            RequestData mergeRequestData = new RequestData();
            mergeRequestData.modelDescription = requestData.modelDescription;
            mergeRequestData.expressionsProcessor = requestData.expressionsProcessor;
            mergeRequestData.securityDriver = requestData.securityDriver;
            mergeRequestData.sqlDialect = requestData.sqlDialect;
            mergeRequestData.defaultLimit = requestData.defaultLimit;
            mergeRequestData.schemaNameNode = requestData.schemaNameNode;
            mergeRequestData.maxSecurityRecursionDepth = requestData.maxSecurityRecursionDepth;
            mergeRequestData.tableQueryProvider = requestData.tableQueryProvider;
            mergeRequestData.optimizeJoins = requestData.optimizeJoins;
            mergeRequestData.requestNode = mergeRequestNode;
            mergeRequestData.params = requestData.params;
            mergeRequestData.sqlQueryProcessors = requestData.sqlQueryProcessors;
            mergeRequestData.lastTableIndexPointer = requestData.lastTableIndexPointer;
            mergeRequestData.lastParameterIndexPointer = requestData.lastParameterIndexPointer;
            mergeRequestData.mapSqlParameterSource = requestData.mapSqlParameterSource;
            mergeRequestData.columnTypes = requestData.columnTypes;
            mergeRequestData.primitiveColumnIndexes = requestData.primitiveColumnIndexes;
            mergeRequestData.countColumnIndexPointer = requestData.countColumnIndexPointer;
            mergeRequestData.commonTableNodes = requestData.commonTableNodes;
            mergeRequestData.calculatedExpressions = requestData.calculatedExpressions;
            mergeRequestData.allSqlQueryProcessors = requestData.allSqlQueryProcessors;
            mergeRequestData.startSqlQueryProcessor = new SqlQueryProcessor(mergeRequestData);
            mergeRequestData.startSqlQueryProcessor.processRequest();
            mergeRequestData.startSqlQueryProcessor.processSecurity();

            children.add(mergeRequestData.startSqlQueryProcessor);
            mergeRequestData.startSqlQueryProcessor.initConditionStringNode();
        });
        initCalculatedExpressions();

        SqlQueryProcessor childSqlQueryProcessor = children.get(0);
        nullLimitAndOffset = childSqlQueryProcessor.nullLimitAndOffset;
        count = childSqlQueryProcessor.count;
        counts = childSqlQueryProcessor.counts;

        if (childSqlQueryProcessor.count && !childSqlQueryProcessor.nullLimitAndOffset) {
            SqlQueryProcessor countSqlQueryProcessor = new SqlQueryProcessor(requestData);
            List<Node<String>> nodes = new ArrayList<>(1 + children.size() * 2);
            nodes.add(Helper.BRACKET_L_NODE);
            addNodeListToNodes(nodes, Helper.BRACKET_R_PLUS_BRACKET_L_NODE, children.stream()
                .map(sqlQueryProcessor -> getQueryNode(
                    Collections.singletonList(node(Helper.COUNT_BRACKET_L_NODE, sqlQueryProcessor.idColumnData.columnNode, Helper.BRACKET_R_NODE)),
                    requestData.securityDriver == null ? sqlQueryProcessor.getCountTableNodeFunction.apply(null) : sqlQueryProcessor.getTableNodeFunctionPointer.object.apply(null),
                    sqlQueryProcessor.conditionStringNode)));
            nodes.add(Helper.BRACKET_R_NODE);
            ColumnData countColumnData = new ColumnData(countSqlQueryProcessor, DataType.INTEGER, node(nodes));
            countSqlQueryProcessor.columnsData = Collections.singletonMap(getCountColumnIndex(), countColumnData);
            countSqlQueryProcessor.recordProcessors = Collections.singletonList((entityData, resultSet) -> counts.put(Collections.emptyList(), getOrder(resultSet)));
            countSqlQueryProcessor.registerSqlQueryProcessor();
            countSqlQueryProcessor.queryNode = getQueryNode(
                countSqlQueryProcessor.getColumnNodes(),
                requestData.sqlDialect.dual(),
                null);
        }

        if (childSqlQueryProcessor.limit != null && childSqlQueryProcessor.limit == 0) {
            return;
        }

        mergeRequestQueryIds = new HashSet<>();
        finalProcessors = Collections.singletonList((entityData, collectionOwner) -> requestData.sqlQueryProcessors.get(entityData.queryId).finalProcessors.forEach(finalProcessor -> finalProcessor.accept(entityData, collectionOwner)));
        offset = childSqlQueryProcessor.offset;
        children.forEach(sqlQueryProcessor -> mergeRequestQueryIds.add(sqlQueryProcessor.queryId));
        int sortCriteriaCount = childSqlQueryProcessor.sortCriteriaData.size();

        if (childSqlQueryProcessor.nullLimitAndOffset && sortCriteriaCount == 0) {
            children.forEach(SqlQueryProcessor::process2);
        } else {
            Pointer<Boolean> childrenPresentedPointer = new Pointer<>(Boolean.FALSE);
            children.forEach(sqlQueryProcessor -> {
                sqlQueryProcessor.tableStringNode = sqlQueryProcessor.getTableNodeFunctionPointer.object.apply(null);
                sqlQueryProcessor.processSecurity2();
                if (!sqlQueryProcessor.children.isEmpty()) {
                    childrenPresentedPointer.object = Boolean.TRUE;
                }
            });
            boolean childrenPresented = childrenPresentedPointer.object;
            Pointer<Integer> indexPointer = new Pointer<>();

            if (childrenPresented) {
                Map<Integer, ColumnData> mergeRequestsCommonColumnIndexes = new LinkedHashMap<>(requestData.columnTypes.size());
                List<DataType> inheritedColumnTypes = new ArrayList<>();
                Map<DataType, List<Integer>> inheritedColumnIndexes = new EnumMap<>(DataType.class);
                children.forEach(sqlQueryProcessor -> {
                    mergeRequestsCommonColumnIndexes.putAll(sqlQueryProcessor.columnsData);
                    sqlQueryProcessor.inheritedColumnsData = new LinkedHashMap<>();
                    sqlQueryProcessor.workColumnsData.stream()
                        .filter(columnData -> columnData.inherit && columnData.columnIndex == 0)
                        .forEach(columnData -> sqlQueryProcessor.inheritedColumnsData.put(getColumnIndex(inheritedColumnTypes, inheritedColumnIndexes, sqlQueryProcessor.inheritedColumnsData, columnData.type), columnData));
                });
                int columnsCount = 1 + mergeRequestsCommonColumnIndexes.size() + inheritedColumnTypes.size();
                List<Node<String>> columnAliasNodes = getColumnAliasNodes(columnsCount + sortCriteriaCount);
                List<Node<String>> nodes = new ArrayList<>(children.size() * 2);
                addNodeListToNodes(nodes, Helper.UNION_ALL_NODE, children.stream().map(sqlQueryProcessor -> {
                    boolean firstRow = sqlQueryProcessor == childSqlQueryProcessor;
                    List<Node<String>> columnNodes = sqlQueryProcessor.getColumnNodes(firstRow, false, mergeRequestsCommonColumnIndexes.size() + 1, index -> mergeRequestsCommonColumnIndexes.get(index).type, QUERY_ID_NODES.get(sqlQueryProcessor.queryId), mergeRequestsCommonColumnIndexes.keySet().stream().mapToInt(columnIndex -> columnIndex), sqlQueryProcessor.columnsData);
                    columnNodes.addAll(getColumnNodes(firstRow, false, inheritedColumnTypes.size(), index -> inheritedColumnTypes.get(index - 1), null, IntStream.rangeClosed(1, inheritedColumnTypes.size()), sqlQueryProcessor.inheritedColumnsData));
                    if (sortCriteriaCount != 0) {
                        columnNodes.addAll(sqlQueryProcessor.sortCriteriaData.stream()
                            .map(sortCriterionData -> sortCriterionData.criterion.get())
                            .collect(Collectors.toList()));
                    }
                    indexPointer.object = 0;
                    columnNodes = columnNodes.stream()
                        .map(columnNode -> getColumnWithAliasNode(columnNode, columnAliasNodes.get(indexPointer.object++)))
                        .collect(Collectors.toList());
                    return getQueryNode(
                        columnNodes,
                        sqlQueryProcessor.tableStringNode,
                        sqlQueryProcessor.conditionStringNode);
                }));
                if (sortCriteriaCount != 0) {
                    Node<String> tableAliasNode = getTableAliasNode();
                    indexPointer.object = 0;
                    List<Node<String>> sortColumnNodes = childSqlQueryProcessor.getSortColumnNodes(sortCriterionData -> getColumnNode(tableAliasNode, columnAliasNodes.get(columnsCount + indexPointer.object++)));
                    List<Node<String>> columnNodes = new ArrayList<>(columnsCount + 1);
                    for (int i = 0; i < columnsCount; ++i) {
                        columnNodes.add(getColumnNode(tableAliasNode, columnAliasNodes.get(i)));
                    }
                    Node<String> rowNumberColumnNode = requestData.sqlDialect.rowNumberFunction(Collections.emptyList(), sortColumnNodes);
                    columnNodes.add(rowNumberColumnNode);
                    indexPointer.object = 0;
                    columnNodes = columnNodes.stream()
                        .map(columnNode -> getColumnWithAliasNode(columnNode, columnAliasNodes.get(indexPointer.object++)))
                        .collect(Collectors.toList());
                    Node<String> queryNode2 = getQueryNode(columnNodes, getTableNode2(tableAliasNode, node(nodes)), null);
                    nodes = new ArrayList<>(2);
                    nodes.add(queryNode2);
                }
                if (!childSqlQueryProcessor.nullLimitAndOffset) {
                    if (sortCriteriaCount == 0) {
                        nodes.add(requestData.sqlDialect.limitAndOffset(childSqlQueryProcessor.limit == null ? null : addParameter(childSqlQueryProcessor.limit), childSqlQueryProcessor.offset == null ? null : addParameter(childSqlQueryProcessor.offset)));
                    } else {
                        Node<String> tableAliasNode = getTableAliasNode();
                        List<Node<String>> columnNodes = new ArrayList<>(columnsCount + 1);
                        for (int i = 0; i <= columnsCount; ++i) {
                            columnNodes.add(getColumnNode(tableAliasNode, columnAliasNodes.get(i)));
                        }
                        nodes = Collections.singletonList(getQueryNode(columnNodes, getTableNode2(tableAliasNode, node(nodes)), childSqlQueryProcessor.getLimitAndOffsetBasedConditionStringNode(columnNodes.get(columnsCount))));
                    }
                }
                Node<String> commonTableAliasNode = COMMON_TABLE_ALIAS_NODES.get(requestData.commonTableNodes.size());
                requestData.commonTableNodes.add(getCommonTableNode(commonTableAliasNode, columnAliasNodes, columnsCount + (sortCriteriaCount == 0 ? 0 : 1), node(nodes)));
                if (sortCriteriaCount != 0) {
                    mergeRequestsCommonColumnIndexes.put(getCountColumnIndex(), new ColumnData(this, DataType.INTEGER, getColumnNode(commonTableAliasNode, columnAliasNodes.get(columnsCount))));
                }
                children.forEach(sqlQueryProcessor -> {
                    sqlQueryProcessor.mergeRequestQueryIdNode = getColumnNode(commonTableAliasNode, columnAliasNodes.get(0));
                    sqlQueryProcessor.getTableNodeFunctionPointer.object = table -> commonTableAliasNode;
                    indexPointer.object = 0;
                    mergeRequestsCommonColumnIndexes.keySet().forEach(columnIndex -> {
                        ++indexPointer.object;
                        ColumnData columnData = sqlQueryProcessor.columnsData.get(columnIndex);
                        if (columnData != null) {
                            columnData.columnNode = getColumnNode(commonTableAliasNode, columnAliasNodes.get(indexPointer.object));
                        }
                    });
                    if (sortCriteriaCount != 0) {
                        --indexPointer.object;
                    }
                    sqlQueryProcessor.inheritedColumnsData.values().forEach(columnData -> columnData.columnNode = getColumnNode(commonTableAliasNode, columnAliasNodes.get(++indexPointer.object)));
                    sqlQueryProcessor.children.forEach(SqlQueryProcessor::process2);
                });
                childSqlQueryProcessor.queryNode = getQueryNode(
                    getColumnNodes(true, false, requestData.columnTypes.size(), index -> requestData.columnTypes.get(index - 1), childSqlQueryProcessor.mergeRequestQueryIdNode, IntStream.rangeClosed(2, requestData.columnTypes.size()), mergeRequestsCommonColumnIndexes),
                    commonTableAliasNode,
                    null);
            } else {
                List<Node<String>> columnAliasNodes = getColumnAliasNodes(requestData.columnTypes.size() + sortCriteriaCount);
                List<Node<String>> nodes = new ArrayList<>(children.size() * 2);
                addNodeListToNodes(nodes, Helper.UNION_ALL_NODE, children.stream().map(sqlQueryProcessor -> {
                    List<Node<String>> columnNodes = sqlQueryProcessor.getColumnNodes();
                    if (sortCriteriaCount != 0) {
                        columnNodes.addAll(sqlQueryProcessor.sortCriteriaData.stream()
                            .map(sortCriterionData -> sortCriterionData.criterion.get())
                            .collect(Collectors.toList()));
                        indexPointer.object = 0;
                        columnNodes = columnNodes.stream()
                            .map(columnNode -> getColumnWithAliasNode(columnNode, columnAliasNodes.get(indexPointer.object++)))
                            .collect(Collectors.toList());
                    } else if (requestData.sqlDialect == SqlDialect.ORACLE && childSqlQueryProcessor.count) {
                        // WA for Oracle bug
                        indexPointer.object = 0;
                        columnNodes = columnNodes.stream()
                            .map(columnNode -> getColumnWithAliasNode(columnNode, columnAliasNodes.get(indexPointer.object++)))
                            .collect(Collectors.toList());
                    }
                    return getQueryNode(
                        columnNodes,
                        sqlQueryProcessor.tableStringNode,
                        sqlQueryProcessor.conditionStringNode);
                }));
                if (sortCriteriaCount != 0) {
                    Node<String> tableAliasNode = getTableAliasNode();
                    indexPointer.object = 0;
                    List<Node<String>> sortColumnNodes = childSqlQueryProcessor.getSortColumnNodes(sortCriterionData -> getColumnNode(tableAliasNode, columnAliasNodes.get(requestData.columnTypes.size() + indexPointer.object++)));
                    List<Node<String>> columnNodes = new ArrayList<>(requestData.columnTypes.size());
                    for (int i = 0; i < requestData.columnTypes.size(); ++i) {
                        columnNodes.add(getColumnNode(tableAliasNode, columnAliasNodes.get(i)));
                    }
                    if (!childSqlQueryProcessor.nullLimitAndOffset && childSqlQueryProcessor.count) {
                        Node<String> rowNumberColumnNode = requestData.sqlDialect.rowNumberFunction(Collections.emptyList(), sortColumnNodes);
                        columnNodes.set(requestData.countColumnIndexPointer.object - 1, rowNumberColumnNode);
                        indexPointer.object = 0;
                        columnNodes = columnNodes.stream()
                            .map(columnNode -> getColumnWithAliasNode(columnNode, columnAliasNodes.get(indexPointer.object++)))
                            .collect(Collectors.toList());
                    }
                    Node<String> queryNode2 = getQueryNode(columnNodes, getTableNode2(tableAliasNode, node(nodes)), null);
                    nodes = new ArrayList<>(2 + sortCriteriaCount * 2);
                    nodes.add(queryNode2);
                    if (childSqlQueryProcessor.nullLimitAndOffset || !childSqlQueryProcessor.count) {
                        nodes.add(Helper.ORDER_BY_NODE);
                        addNodeListToNodes(nodes, Helper.COMMA_NODE, sortColumnNodes.stream());
                    }
                }
                if (!childSqlQueryProcessor.nullLimitAndOffset) {
                    if (sortCriteriaCount != 0 && childSqlQueryProcessor.count) {
                        Node<String> tableAliasNode = getTableAliasNode();
                        List<Node<String>> columnNodes = new ArrayList<>(requestData.columnTypes.size());
                        for (int i = 0; i < requestData.columnTypes.size(); ++i) {
                            columnNodes.add(getColumnNode(tableAliasNode, columnAliasNodes.get(i)));
                        }
                        nodes = Collections.singletonList(getQueryNode(columnNodes, getTableNode2(tableAliasNode, node(nodes)), childSqlQueryProcessor.getLimitAndOffsetBasedConditionStringNode(columnNodes.get(requestData.countColumnIndexPointer.object - 1))));
                    } else {
                        nodes.add(requestData.sqlDialect.limitAndOffset(childSqlQueryProcessor.limit == null ? null : addParameter(childSqlQueryProcessor.limit), childSqlQueryProcessor.offset == null ? null : addParameter(childSqlQueryProcessor.offset)));
                    }
                    if (requestData.sqlDialect == SqlDialect.ORACLE && sortCriteriaCount == 0 && childSqlQueryProcessor.count) {
                        //WA for Oracle bug
                        Node<String> tableAliasNode = getTableAliasNode();
                        List<Node<String>> columnNodes = new ArrayList<>(requestData.columnTypes.size());
                        for (int i = 0; i < requestData.columnTypes.size(); ++i) {
                            columnNodes.add(getColumnNode(tableAliasNode, columnAliasNodes.get(i)));
                        }
                        Node<String> queryNode2 = getQueryNode(columnNodes, getTableNode2(tableAliasNode, node(nodes)), null);
                        nodes = new ArrayList<>(2 + sortCriteriaCount * 2);
                        nodes.add(queryNode2);
                    }
                }
                childSqlQueryProcessor.queryNode = node(nodes);
                if (sortCriteriaCount == 0 && childSqlQueryProcessor.count) {
                    childSqlQueryProcessor.queryNode = node(Helper.BRACKET_L_NODE, childSqlQueryProcessor.queryNode, Helper.BRACKET_R_NODE);
                }
            }
        }
    }

    /**
     * Process property selection
     *
     * @param propertiesSelectionNode The property selection node
     */
    void processPropertiesSelection(JsonNode propertiesSelectionNode) {
        checkExtraFields(requestData.requestNode, EntitiesReadAccessJsonHelper.PROPERTIES_SELECTION_SPECIFICATION_FIELD_NAMES, ExtraFieldsFoundForPropertiesSelectionException::new);
        checkIsObject(propertiesSelectionNode);
        if (propertiesSelectionNode.isEmpty()) {
            throw new NoPropertySelectedException(propertiesSelectionNode.toString());
        }

        requestData.propertiesSelection = true;
        processParams(requestData.requestNode);
        entityDescription = requestData.modelDescription.getEntityDescription(getRequiredEntityType(requestData.requestNode));
        tablesData = new HashMap<>();
        TableData startTableData = getTableData(entityDescription);
        startTableData.added = true;
        workColumnsData = new ArrayList<>(1);
        idColumnData = new ColumnData(this, DataType.STRING, startTableData.idColumnNode);
        mandatory = true;
        mandatoryEntityDescription = entityDescription;
        workColumnsData.add(idColumnData);
        getTableNodeFunctionPointer = new Pointer<>(table -> startTableData.tableNode);
        localIdColumnsData = new HashMap<>();
        localIdColumnsData.put(entityDescription, idColumnData);
        primitiveColumnsData = new HashMap<>();
        referenceSqlQueryProcessors = new HashMap<>();
        backReferenceReferenceSqlQueryProcessors = new HashMap<>();
        aliasedEntitiesData = Collections.emptyMap();
        getAdditionalConditionNodeFunctionPointer = new Pointer<>();
        processSingleTableInheritanceStrategy();
        columnsData = new HashMap<>();
        recordProcessors = new ArrayList<>();
        finalProcessors = new ArrayList<>();
        expressionContext = ExpressionContext.EMPTY;
        registerSqlQueryProcessor();
        processSearchSettings(requestData.requestNode);

        JsonNode distinctNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.DISTINCT_FIELD_NAME);
        if (distinctNode != null && getBoolean(distinctNode)) {
            distinct = true;
        }
        propertiesSelectionNode.fields().forEachRemaining(entry -> processCalculatedExpressionExpression(entry.getKey(), getString(entry.getValue()), PropertyType.SELECTED));
    }

    /**
     * Add security requirements
     *
     * @param nodes                                    Nodes
     * @param referenceSecurityConditionInitialization Initialization of the security condition of the reference
     */
    void addSecurityConditions(List<Node<String>> nodes, boolean referenceSecurityConditionInitialization) {
        requestData.referenceSecurityConditionInitialization = referenceSecurityConditionInitialization;
        addNodeListToNodes(nodes, Helper.AND_NODE, Stream.concat(
            securityConditions.stream().map(ConditionImpl::get),
            securityConditions2.entrySet().stream().map(entry -> {
                if (entry.getKey().getRootEntityDescription().getInheritanceStrategy() == InheritanceStrategy.SINGLE_TABLE) {
                    ColumnData typeColumnData2 = getTypeColumnData();
                    typeColumnData2.inherit = true;
                    return node(Helper.BRACKET_L_NODE, Helper.NOT_BRACKET_L_NODE, typeColumnData2.columnNode, entry.getKey().getMetaDataManager().get(EntityDescriptionMetaData.class).inHeirTypesStringNode, Helper.BRACKET_R_NODE, Helper.OR_NODE, entry.getValue().get(), Helper.BRACKET_R_NODE);
                } else {
                    ColumnData localIdData = getLocalIdColumnData(entry.getKey());
                    localIdData.inherit = true;
                    return node(Helper.BRACKET_L_NODE, localIdData.columnNode, Helper.IS_NULL_NODE, Helper.OR_NODE, entry.getValue().get(), Helper.BRACKET_R_NODE);
                }
            })));
        requestData.referenceSecurityConditionInitialization = false;
    }

    /**
     * Process security for link
     *
     * @param parentSqlQueryProcessor Parent SQL query processor
     * @param sqlQueryProcessor       SQL query handler
     */
    void processReferenceSecurity(SqlQueryProcessor parentSqlQueryProcessor, SqlQueryProcessor sqlQueryProcessor) {
        if (parentSqlQueryProcessor.securityFlagColumnData != null) {
            sqlQueryProcessor.securityFlagColumnData = parentSqlQueryProcessor.securityFlagColumnData;
        }
        if (!(sqlQueryProcessor.securityConditions.isEmpty() && sqlQueryProcessor.securityConditions2.isEmpty())) {
            Node<String> tableAliasNode = getTableAliasNode();
            UnaryOperator<Node<String>> currentGetTableNodeFunction = sqlQueryProcessor.getTableNodeFunctionPointer.object;
            sqlQueryProcessor.getTableNodeFunctionPointer.object = tableNode -> {
                List<Node<String>> nodes = new ArrayList<>(14 + (sqlQueryProcessor.securityConditions.size() + sqlQueryProcessor.securityConditions2.size()) * 2);
                nodes.add(currentGetTableNodeFunction.apply(tableNode));
                nodes.add(Helper.LEFT_JOIN_NODE);
                nodes.add(requestData.sqlDialect.selectOne());
                nodes.add(Helper.SPACE_NODE);
                nodes.add(tableAliasNode);
                nodes.add(Helper.ON_NODE);
                if (parentSqlQueryProcessor.securityFlagColumnData != null) {
                    nodes.add(parentSqlQueryProcessor.securityFlagColumnData.columnNode);
                    nodes.add(Helper.EQ_NODE);
                    nodes.add(Helper.ONE_NODE);
                    nodes.add(Helper.AND_NODE);
                }
                nodes.add(Helper.BRACKET_L_NODE);
                nodes.add(sqlQueryProcessor.idColumnData.columnNode);
                nodes.add(Helper.IS_NULL_NODE);
                nodes.add(Helper.OR_NODE);
                sqlQueryProcessor.addSecurityConditions(nodes, true);
                nodes.add(Helper.BRACKET_R_NODE);
                return node(nodes);
            };
            sqlQueryProcessor.securityFlagColumnData = new ColumnData(sqlQueryProcessor, DataType.INTEGER, getColumnNode(tableAliasNode, Helper.F_NODE));
            sqlQueryProcessor.securityFlagColumnData.inherit = true;
            sqlQueryProcessor.workColumnsData.add(sqlQueryProcessor.securityFlagColumnData);
            if (sqlQueryProcessor.added) {
                sqlQueryProcessor.securityFlagColumnIndex = sqlQueryProcessor.getPrimitiveColumnIndex(DataType.INTEGER);
                sqlQueryProcessor.columnsData.put(sqlQueryProcessor.securityFlagColumnIndex, sqlQueryProcessor.securityFlagColumnData);
            }
        }
        sqlQueryProcessor.referenceSqlQueryProcessors.values().forEach(referenceSqlQueryProcessor -> processReferenceSecurity(sqlQueryProcessor, referenceSqlQueryProcessor));
        sqlQueryProcessor.backReferenceReferenceSqlQueryProcessors.values().forEach(referenceSqlQueryProcessor -> processReferenceSecurity(sqlQueryProcessor, referenceSqlQueryProcessor));
    }

    /**
     * Add entity types
     *
     * @param entityTypes       Entity types
     * @param missedEntityTypes entity types
     * @param entityDescription Description of entities
     */
    void addEntityTypes(Set<String> entityTypes, Set<String> missedEntityTypes, EntityDescription entityDescription) {
        entityDescription.getChildEntityDescriptions().forEach(childEntityDescription -> {
            if (!entityTypes.contains(childEntityDescription.getName())) {
                missedEntityTypes.add(childEntityDescription.getName());
            }
        });
        while (entityDescription != null) {
            if (!entityTypes.contains(entityDescription.getName())) {
                missedEntityTypes.add(entityDescription.getName());
            }
            entityDescription = entityDescription.getParentEntityDescription();
        }
    }

    /**
     * Initialize computable expressions
     */
    void initCalculatedExpressions() {
        requestData.calculatedExpressions.forEach((columnData, primitiveExpression) -> columnData.columnNode = getExpressionWithConditionNode(requestData.sqlDialect, primitiveExpression.get(), primitiveExpression.getConditionStringNodeFunction.get()));
    }

    /**
     * Security processing
     */
    void processSecurity() {
        if (requestData.securityDriver != null) {
            Set<String> entityTypes = new LinkedHashSet<>();
            requestData.allSqlQueryProcessors.forEach(sqlQueryProcessor -> {
                if (sqlQueryProcessor.entityDescription != null) {
                    addEntityTypes(Collections.emptySet(), entityTypes, sqlQueryProcessor.entityDescription);
                }
            });
            Map<String, String> restrictions = requestData.securityDriver.getRestrictions(entityTypes);

            List<SqlQueryProcessor> allSqlQueryProcessors = new ArrayList<>(requestData.allSqlQueryProcessors.size());
            int recursionDepth = 1;
            while (!requestData.allSqlQueryProcessors.isEmpty()) {
                if (recursionDepth > requestData.maxSecurityRecursionDepth) {
                    throw new SecurityRecursionDepthExceededMaximumException(requestData.maxSecurityRecursionDepth);
                }
                int beginIndex = allSqlQueryProcessors.size();
                Set<String> missedEntityTypes = new LinkedHashSet<>();
                requestData.allSqlQueryProcessors.forEach(sqlQueryProcessor -> {
                    if (sqlQueryProcessor.entityDescription != null) {
                        allSqlQueryProcessors.add(sqlQueryProcessor);
                        addEntityTypes(entityTypes, missedEntityTypes, sqlQueryProcessor.entityDescription);
                    }
                });
                int endIndex = allSqlQueryProcessors.size();
                if (!missedEntityTypes.isEmpty()) {
                    restrictions.putAll(requestData.securityDriver.getRestrictions(missedEntityTypes));
                    entityTypes.addAll(missedEntityTypes);
                }
                requestData.allSqlQueryProcessors.clear();
                for (int i = beginIndex; i < endIndex; ++i) {
                    SqlQueryProcessor sqlQueryProcessor = allSqlQueryProcessors.get(i);
                    sqlQueryProcessor.securityConditions = new ArrayList<>();
                    sqlQueryProcessor.securityConditions2 = new LinkedHashMap<>();
                    if (!(sqlQueryProcessor.reference && sqlQueryProcessor.idOnly)) {
                        EntityDescription entityDescription2 = sqlQueryProcessor.entityDescription;
                        EntityDescription currentEntityDescription = entityDescription2;
                        while (currentEntityDescription != null) {
                            String conditionString = restrictions.get(currentEntityDescription.getName());
                            if (conditionString != null) {
                                ConditionImpl securityCondition = (ConditionImpl) new StringConditionBuilder(conditionString, requestData.modelDescription, currentEntityDescription.getName()).build(requestData.expressionsProcessor);
                                sqlQueryProcessor.entityDescription = currentEntityDescription;
                                securityCondition.prepare(sqlQueryProcessor, ExpressionContext.EMPTY);
                                sqlQueryProcessor.securityConditions.add(securityCondition);
                            }
                            currentEntityDescription = currentEntityDescription.getParentEntityDescription();
                        }
                        entityDescription2.getChildEntityDescriptions().forEach(childEntityDescription -> {
                            String conditionString = restrictions.get(childEntityDescription.getName());
                            if (conditionString != null) {
                                ConditionImpl securityCondition = (ConditionImpl) new StringConditionBuilder(conditionString, requestData.modelDescription, childEntityDescription.getName()).build(requestData.expressionsProcessor);
                                sqlQueryProcessor.entityDescription = childEntityDescription;
                                securityCondition.prepare(sqlQueryProcessor, ExpressionContext.EMPTY);
                                if (childEntityDescription.getRootEntityDescription().getInheritanceStrategy() != InheritanceStrategy.SINGLE_TABLE) {
                                    sqlQueryProcessor.getLocalIdColumnData(childEntityDescription);
                                }
                                sqlQueryProcessor.securityConditions2.put(childEntityDescription, securityCondition);
                            }
                        });
                        sqlQueryProcessor.entityDescription = entityDescription2;
                    }
                }
                ++recursionDepth;
            }
            allSqlQueryProcessors.forEach(sqlQueryProcessor -> {
                if (!sqlQueryProcessor.reference) {
                    if (!(sqlQueryProcessor.securityConditions.isEmpty() && sqlQueryProcessor.securityConditions2.isEmpty())) {
                        Supplier<Node<String>> getAdditionalConditionNodeFunction = () -> {
                            List<Node<String>> nodes = new ArrayList<>((sqlQueryProcessor.securityConditions.size() + sqlQueryProcessor.securityConditions2.size()) * 2 - 1);
                            sqlQueryProcessor.addSecurityConditions(nodes, false);
                            return node(nodes);
                        };
                        if (sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object == null) {
                            sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object = getAdditionalConditionNodeFunction;
                        } else {
                            Supplier<Node<String>> currentGetAdditionalConditionNodeFunction = sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object;
                            sqlQueryProcessor.getAdditionalConditionNodeFunctionPointer.object = () -> node(currentGetAdditionalConditionNodeFunction.get(), Helper.AND_NODE, getAdditionalConditionNodeFunction.get());
                        }
                    }
                    sqlQueryProcessor.referenceSqlQueryProcessors.values().forEach(referenceSqlQueryProcessor -> processReferenceSecurity(sqlQueryProcessor, referenceSqlQueryProcessor));
                    sqlQueryProcessor.backReferenceReferenceSqlQueryProcessors.values().forEach(referenceSqlQueryProcessor -> processReferenceSecurity(sqlQueryProcessor, referenceSqlQueryProcessor));
                }
            });
        }
    }

    /**
     * Process security (2nd iteration)
     */
    void processSecurity2() {
        if (requestData.securityDriver != null) {
            workColumnsData.forEach(workColumnData -> {
                if (workColumnData.sqlQueryProcessor.securityFlagColumnData != null
                    && workColumnData != workColumnData.sqlQueryProcessor.securityFlagColumnData && !workColumnData.refId) {
                    workColumnData.columnNode = getExpressionWithConditionNode(requestData.sqlDialect, workColumnData.columnNode, node(workColumnData.sqlQueryProcessor.securityFlagColumnData.columnNode, Helper.EQ_NODE, Helper.ONE_NODE));
                }
            });
        }
    }

    /**
     * Processed (2nd iteration)
     */
    void process2() {
        initConditionStringNode();

        if (count && !nullLimitAndOffset) {
            SqlQueryProcessor countSqlQueryProcessor = new SqlQueryProcessor(requestData);
            countSqlQueryProcessor.columnsData = new LinkedHashMap<>(collectionOwnerColumnsData);
            countSqlQueryProcessor.registerSqlQueryProcessor();
            countSqlQueryProcessor.recordProcessors = Collections.singletonList((entityData, resultSet) -> counts.put(getCollectionOwner(resultSet), getOrder(resultSet)));
            if (distinct) {
                countSqlQueryProcessor.columnsData = new LinkedHashMap<>();
                countSqlQueryProcessor.columnsData.put(getCountColumnIndex(), new ColumnData(countSqlQueryProcessor, DataType.INTEGER, Helper.COUNT_ASTERISK_NODE));
                countSqlQueryProcessor.queryNode = getQueryNode(
                    countSqlQueryProcessor.getColumnNodes(),
                    getTableNode2(getTableAliasNode(), node(Helper.BRACKET_L_NODE, getQueryNode(
                        distinct,
                        columnsData.values().stream().map(columnData -> columnData.columnNode).collect(Collectors.toList()),
                        getTableNodeFunctionPointer.object.apply(null),
                        conditionStringNode,
                        group == null ? Collections.emptyList() : group.stream().map(CalculatedExpression::get).collect(Collectors.toList()),
                        groupCond == null ? null : groupCond.get(),
                        Collections.emptyList(),
                        null,
                        null
                    ), Helper.BRACKET_R_NODE)),
                    null);
            } else if (group != null) {
                countSqlQueryProcessor.columnsData = new LinkedHashMap<>();
                countSqlQueryProcessor.columnsData.put(getCountColumnIndex(), new ColumnData(countSqlQueryProcessor, DataType.INTEGER, Helper.COUNT_ASTERISK_NODE));
                countSqlQueryProcessor.queryNode = getQueryNode(
                    countSqlQueryProcessor.getColumnNodes(),
                    getTableNode2(getTableAliasNode(), node(Helper.BRACKET_L_NODE, getQueryNode(
                        columnsData.values().stream().map(columnData -> columnData.columnNode).collect(Collectors.toList()),
                        getTableNodeFunctionPointer.object.apply(null),
                        conditionStringNode,
                        group.stream().map(CalculatedExpression::get).collect(Collectors.toList()),
                        groupCond == null ? null : groupCond.get(),
                        Collections.emptyList(),
                        null,
                        null
                    ), Helper.BRACKET_R_NODE)),
                    null);
            } else {
                Node<String> countNode;
                if (idColumnData.columnNode == NULL_NODE) {
                    countNode = Helper.COUNT_ASTERISK_NODE;
                } else {
                    countNode = node(Helper.COUNT_BRACKET_L_NODE, idColumnData.columnNode, Helper.BRACKET_R_NODE);
                }
                countSqlQueryProcessor.columnsData.put(getCountColumnIndex(), new ColumnData(countSqlQueryProcessor, DataType.INTEGER, countNode));
                countSqlQueryProcessor.queryNode = getQueryNode(
                    countSqlQueryProcessor.getColumnNodes(),
                    requestData.securityDriver == null ? getCountTableNodeFunction.apply(null) : getTableNodeFunctionPointer.object.apply(null),
                    conditionStringNode,
                    collectionOwnerColumnsData.values().stream()
                        .map(columnData -> columnData.columnNode)
                        .collect(Collectors.toList()),
                    groupCond == null ? null : groupCond.get(),
                    Collections.emptyList(),
                    null,
                    null);

            }
        }

        if (limit != null && limit == 0) {
            return;
        }

        tableStringNode = getTableNodeFunctionPointer.object.apply(null);

        processSecurity2();

        List<Node<String>> sortColumnNodes = getSortColumnNodes(sortCriterionData -> sortCriterionData.criterion.get());

        if (collectionOwnerColumnsData.isEmpty()) {
            if (children.isEmpty()) {
                if (count && !nullLimitAndOffset && !sortColumnNodes.isEmpty()) {
                    List<Node<String>> columnAliasNodes = getColumnAliasNodes(columnsData.size() + sortColumnNodes.size());
                    Pointer<Integer> indexPointer = new Pointer<>(0);
                    List<Node<String>> columnNodes = Stream.concat(
                            columnsData.values().stream()
                                .map(columnData -> getColumnWithAliasNode(columnData.columnNode, columnAliasNodes.get(indexPointer.object++))),
                            sortCriteriaData.stream().map(sortCriterionData -> getColumnWithAliasNode(sortCriterionData.criterion.get(), columnAliasNodes.get(indexPointer.object++))))
                        .collect(Collectors.toList());
                    Node<String> query2Node = getQueryNode(
                        distinct,
                        columnNodes,
                        tableStringNode,
                        conditionStringNode,
                        group == null ? Collections.emptyList() : group.stream().map(CalculatedExpression::get).collect(Collectors.toList()),
                        groupCond == null ? null : groupCond.get(),
                        sortColumnNodes,
                        offset,
                        limit);
                    Node<String> tableAliasNode = getTableAliasNode();
                    executeWithColumnsReplace(columnsData.values(), tableAliasNode, columnAliasNodes, () -> {
                        indexPointer.object = columnsData.size();
                        Node<String> rowNumberStringNode = requestData.sqlDialect.rowNumberFunction(Collections.emptyList(), getSortColumnNodes(sortCriterionData -> getColumnNode(tableAliasNode, columnAliasNodes.get(indexPointer.object++))));
                        columnsData.put(getCountColumnIndex(), new ColumnData(this, DataType.INTEGER, offset == null ? rowNumberStringNode : node(addParameter(offset), Helper.PLUS_NODE, rowNumberStringNode)));
                        queryNode = getQueryNode(
                            getColumnNodes(true),
                            getTableNode2(tableAliasNode, query2Node),
                            null);
                        columnsData.remove(getCountColumnIndex());
                    });
                } else {
                    queryNode = getQueryNode(
                        distinct,
                        getColumnNodes(true),
                        tableStringNode,
                        conditionStringNode,
                        group == null ? Collections.emptyList() : group.stream().map(CalculatedExpression::get).collect(Collectors.toList()),
                        groupCond == null ? null : groupCond.get(),
                        sortColumnNodes,
                        offset,
                        limit);
                    if (count && !nullLimitAndOffset) {
                        queryNode = node(Helper.BRACKET_L_NODE, queryNode, Helper.BRACKET_R_NODE);
                    }
                }
            } else {
                List<Node<String>> columnAliasNodes = getColumnAliasNodes(workColumnsData.size() + sortColumnNodes.size());
                Pointer<Integer> indexPointer = new Pointer<>(0);
                List<Node<String>> columnNodes = Stream.concat(
                        workColumnsData.stream().map(columnData -> getColumnWithAliasNode(columnData.columnNode, columnAliasNodes.get(indexPointer.object++))),
                        sortCriteriaData.stream().map(sortCriterionData -> getColumnWithAliasNode(sortCriterionData.criterion.get(), columnAliasNodes.get(indexPointer.object++))))
                    .collect(Collectors.toList());
                Node<String> query2Node = getQueryNode(
                    columnNodes,
                    tableStringNode,
                    conditionStringNode,
                    Collections.emptyList(),
                    null,
                    sortColumnNodes,
                    offset,
                    limit);
                Node<String> commonTableAliasNode = COMMON_TABLE_ALIAS_NODES.get(requestData.commonTableNodes.size());
                requestData.commonTableNodes.add(getCommonTableNode(commonTableAliasNode, columnAliasNodes, columnNodes.size(), query2Node));
                getTableNodeFunctionPointer.object = table2 -> commonTableAliasNode;
                executeWithColumnsReplace(workColumnsData, commonTableAliasNode, columnAliasNodes, () -> {
                    if (!sortColumnNodes.isEmpty()) {
                        indexPointer.object = workColumnsData.size();
                        Node<String> rowNumberStringNode = requestData.sqlDialect.rowNumberFunction(Collections.emptyList(), getSortColumnNodes(sortCriterionData -> getColumnNode(commonTableAliasNode, columnAliasNodes.get(indexPointer.object++))));
                        columnsData.put(getCountColumnIndex(), new ColumnData(this, DataType.INTEGER, offset == null ? rowNumberStringNode : node(addParameter(offset), Helper.PLUS_NODE, rowNumberStringNode)));
                    }
                    queryNode = getQueryNode(
                        getColumnNodes(true),
                        commonTableAliasNode,
                        null);
                    children.forEach(SqlQueryProcessor::process2);
                });
            }
        } else {
            if (children.isEmpty()) {
                if (nullLimitAndOffset && sortColumnNodes.isEmpty()) {
                    queryNode = getQueryNode(
                        getColumnNodes(),
                        tableStringNode,
                        conditionStringNode);
                } else {
                    ColumnData rowNumberColumnData = new ColumnData(this, DataType.INTEGER, requestData.sqlDialect.rowNumberFunction(collectionOwnerColumnsData.values().stream()
                        .map(columnData -> columnData.columnNode)
                        .collect(Collectors.toList()), sortColumnNodes));
                    List<Node<String>> columnAliasNodes = getColumnAliasNodes(columnsData.size() + 1);
                    if (!sortColumnNodes.isEmpty()) {
                        columnsData.put(getCountColumnIndex(), rowNumberColumnData);
                    }
                    Pointer<Integer> indexPointer = new Pointer<>(0);
                    List<Node<String>> columnNodes = columnsData.values().stream()
                        .map(columnData -> getColumnWithAliasNode(columnData.columnNode, columnAliasNodes.get(indexPointer.object++)))
                        .collect(Collectors.toList());
                    Node<String> rowNumberColumnAliasNode = null;
                    if (sortColumnNodes.isEmpty()) {
                        rowNumberColumnAliasNode = columnAliasNodes.get(indexPointer.object);
                        columnNodes.add(getColumnWithAliasNode(rowNumberColumnData.columnNode, rowNumberColumnAliasNode));
                    }
                    Node<String> query2Node = getQueryNode(
                        columnNodes,
                        tableStringNode,
                        conditionStringNode);
                    Node<String> tableAliasNode = getTableAliasNode();
                    if (sortColumnNodes.isEmpty()) {
                        rowNumberColumnData.columnNode = getColumnNode(tableAliasNode, rowNumberColumnAliasNode);
                    }
                    executeWithColumnsReplace(columnsData.values(), tableAliasNode, columnAliasNodes, () -> queryNode = getQueryNode(
                        getColumnNodes(),
                        getTableNode2(tableAliasNode, query2Node),
                        getLimitAndOffsetBasedConditionStringNode(rowNumberColumnData.columnNode)));
                }
            } else {
                parent.workColumnsData.stream()
                    .filter(columnData -> columnData.inherit)
                    .forEach(workColumnsData::add);
                List<Node<String>> columnAliasNodes = getColumnAliasNodes(workColumnsData.size() + 1);
                Pointer<Integer> indexPointer = new Pointer<>(0);
                List<Node<String>> columnNodes = workColumnsData.stream()
                    .map(columnData -> getColumnWithAliasNode(columnData.columnNode, columnAliasNodes.get(indexPointer.object++)))
                    .collect(Collectors.toList());
                Node<String> rowNumberColumnAliasNode = columnAliasNodes.get(indexPointer.object);
                Node<String> query2Node;
                if (nullLimitAndOffset && sortColumnNodes.isEmpty()) {
                    query2Node = getQueryNode(
                        columnNodes,
                        tableStringNode,
                        conditionStringNode);
                } else {
                    columnNodes.add(getColumnWithAliasNode(requestData.sqlDialect.rowNumberFunction(collectionOwnerColumnsData.values().stream()
                        .map(columnData -> columnData.columnNode)
                        .collect(Collectors.toList()), sortColumnNodes), rowNumberColumnAliasNode));
                    Node<String> query3Node = getQueryNode(
                        columnNodes,
                        tableStringNode,
                        conditionStringNode);
                    Node<String> tableAliasNode = getTableAliasNode();
                    indexPointer.object = 0;
                    if (sortColumnNodes.isEmpty()) {
                        columnNodes.remove(columnNodes.size() - 1);
                    }
                    query2Node = getQueryNode(
                        columnNodes.stream()
                            .map(column -> getColumnNode(tableAliasNode, columnAliasNodes.get(indexPointer.object++)))
                            .collect(Collectors.toList()),
                        getTableNode2(tableAliasNode, query3Node),
                        getLimitAndOffsetBasedConditionStringNode(getColumnNode(tableAliasNode, rowNumberColumnAliasNode)));
                }
                Node<String> commonTableAliasNode = COMMON_TABLE_ALIAS_NODES.get(requestData.commonTableNodes.size());
                requestData.commonTableNodes.add(getCommonTableNode(commonTableAliasNode, columnAliasNodes, columnNodes.size(), query2Node));
                getTableNodeFunctionPointer.object = table2 -> commonTableAliasNode;
                executeWithColumnsReplace(workColumnsData, commonTableAliasNode, columnAliasNodes, () -> {
                    if (!sortColumnNodes.isEmpty()) {
                        columnsData.put(getCountColumnIndex(), new ColumnData(this, DataType.INTEGER, getColumnNode(commonTableAliasNode, rowNumberColumnAliasNode)));
                    }
                    queryNode = getQueryNode(
                        getColumnNodes(),
                        commonTableAliasNode,
                        null);
                    children.forEach(SqlQueryProcessor::process2);
                });
            }
        }
    }

    /**
     * Processed (3rd iteration)
     */
    void process3() {
        List<Node<String>> nodes = new ArrayList<>((requestData.commonTableNodes.size() + requestData.sqlQueryProcessors.size()) * 2 + 2);
        if (!requestData.commonTableNodes.isEmpty()) {
            nodes.add(Helper.WITH_NODE);
            addNodeListToNodes(nodes, Helper.COMMA_NODE, requestData.commonTableNodes.stream());
            nodes.add(Helper.SPACE_NODE);
        }
        addNodeListToNodes(nodes, Helper.UNION_ALL_NODE, requestData.sqlQueryProcessors.stream()
            .map(sqlQueryProcessor -> sqlQueryProcessor.queryNode)
            .filter(Objects::nonNull));
        if (lockMode != null) {
            nodes.add(Helper.FOR_UPDATE_NODE);
            if (lockMode == LockMode.NOWAIT) {
                nodes.add(Helper.NOWAIT_NODE);
            }
        }
        requestData.sqlQuery = CommonHelper.getString(node(nodes));
    }

    /**
     * Process
     */
    void process() {
// In the method, an SQL string is created, and information is prepared on how to read data from the final ResultSet.
// Checking JSON node for validity
        checkIsObject(requestData.requestNode);
        checkFieldNames(requestData.requestNode, EntitiesReadAccessJsonHelper.REQUEST_SPECIFICATION_FIELD_NAMES);
        JsonNode mergeRequestsNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.REQUESTS_MERGE_FIELD_NAME);
        JsonNode propertiesSelectionNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.PROPERTIES_SELECTION_FIELD_NAME);
        JsonNode lockNode = requestData.requestNode.get(EntitiesReadAccessJsonHelper.LOCK_FIELD_NAME);
        if (mergeRequestsNode != null) {
            processRequestsMerge(mergeRequestsNode);
        } else if (propertiesSelectionNode != null) {
            processPropertiesSelection(propertiesSelectionNode);
            processSecurity();
            initCalculatedExpressions();
            process2();
        } else if (lockNode != null) {
            processLockRequest();
            process2();
        } else {
            processRequest();
            processSecurity();
            initCalculatedExpressions();
            process2();
        }
        process3();
    }

    static {
        TABLE_ALIAS_NODES = new PreparedList<>(index -> node("t" + index), 100);
        COLUMN_ALIAS_NODES = new PreparedList<>(index -> node("c" + index), 100);
        PARAMETER_NAME_NODES = new PreparedList<>(index -> node(":p" + index), 100);
        QUERY_ID_NODES = new PreparedList<>(index -> node(String.valueOf(index)), 20);
        COMMON_TABLE_ALIAS_NODES = new PreparedList<>(index -> node("ct" + index), 20);
    }
}
