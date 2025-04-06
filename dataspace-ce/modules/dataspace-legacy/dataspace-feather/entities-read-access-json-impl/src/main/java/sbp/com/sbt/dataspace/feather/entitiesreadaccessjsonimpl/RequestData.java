package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;
import sbp.com.sbt.dataspace.feather.tablequeryprovider.TableQueryProvider;

import java.util.List;
import java.util.Map;

/**
 * The query data
 */
class RequestData {

    ModelDescription modelDescription;
    ExpressionsProcessor expressionsProcessor;
    SecurityDriver securityDriver;
    SqlDialect sqlDialect;
    Integer defaultLimit;
    Node<String> schemaNameNode;
    int maxSecurityRecursionDepth;
    TableQueryProvider tableQueryProvider;
    boolean optimizeJoins;
    JsonNode requestNode;

    List<SqlQueryProcessor> sqlQueryProcessors;
    Pointer<Integer> lastTableIndexPointer;
    Pointer<Integer> lastParameterIndexPointer;
    MapSqlParameterSource mapSqlParameterSource;
    List<DataType> columnTypes;
    Map<DataType, List<Integer>> primitiveColumnIndexes;
    Pointer<Integer> countColumnIndexPointer;
    List<Node<String>> commonTableNodes;
    Map<ColumnData, PrimitiveExpressionImpl> calculatedExpressions;
    List<SqlQueryProcessor> allSqlQueryProcessors;
    SqlQueryProcessor startSqlQueryProcessor;
    boolean referenceSecurityConditionInitialization;
    String sqlQuery;
    boolean propertiesSelection;
    Map<String, Object> params;
}
