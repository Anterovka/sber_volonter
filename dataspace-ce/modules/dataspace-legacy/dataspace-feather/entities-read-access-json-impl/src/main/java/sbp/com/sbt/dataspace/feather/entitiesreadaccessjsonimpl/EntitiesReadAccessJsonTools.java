package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;
import sbp.com.sbt.dataspace.feather.tablequeryprovider.TableQueryProvider;

import java.util.Collections;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getRequestData;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getRequestNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getSchemaNameNode;

/**
 * Access tools to entities for reading through JSON
 */
public class EntitiesReadAccessJsonTools {

    ModelDescription modelDescription;
    ExpressionsProcessor expressionsProcessor;
    SecurityDriver securityDriver;
    SqlDialect sqlDialect;
    Integer defaultLimit;
    Node<String> schemaNameNode;
    int maxSecurityRecursionDepth;
    TableQueryProvider tableQueryProvider;
    boolean optimizeJoins;

    /**
     * @param modelDescription               Description of the model
     * @param expressionsProcessor           The expressions processor
     * @param securityDriver                 Security driver
     * @param entitiesReadAccessJsonSettings The settings for entity read access via JSON
     */
    EntitiesReadAccessJsonTools(ModelDescription modelDescription, ExpressionsProcessor expressionsProcessor, SecurityDriver securityDriver, EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings) {
        this.modelDescription = modelDescription;
        this.expressionsProcessor = expressionsProcessor;
        this.securityDriver = securityDriver;
        sqlDialect = entitiesReadAccessJsonSettings.sqlDialect;
        defaultLimit = entitiesReadAccessJsonSettings.defaultLimit;
        schemaNameNode = getSchemaNameNode(entitiesReadAccessJsonSettings.schemaName);
        maxSecurityRecursionDepth = entitiesReadAccessJsonSettings.maxSecurityRecursionDepth;
        tableQueryProvider = entitiesReadAccessJsonSettings.tableQueryProvider;
        optimizeJoins = entitiesReadAccessJsonSettings.optimizeJoins;
    }

    /**
     * Get search data
     *
     * @param requestJson JSON of the request
     * @param params      Parameters
     */
    public SearchData getSearchData(String requestJson, Map<String, Object> params) {
        return getSearchData(getRequestNode(requestJson), params);
    }

    /**
     * Get search data
     *
     * @param requestJson JSON query
     */
    public SearchData getSearchData(String requestJson) {
        return getSearchData(requestJson, Collections.emptyMap());
    }

    /**
     * Get search data
     *
     * @param requestNode Request node
     * @param params  Parameters
     */
    public SearchData getSearchData(JsonNode requestNode, Map<String, Object> params) {
        RequestData requestData = getRequestData(modelDescription, expressionsProcessor, securityDriver, sqlDialect, defaultLimit, schemaNameNode, maxSecurityRecursionDepth, tableQueryProvider, optimizeJoins, requestNode, params);
        return new SearchData(requestData.sqlQuery, requestData.mapSqlParameterSource);
    }

    /**
     * Get search data
     *
     * @param requestNode Request node
     */
    public SearchData getSearchData(JsonNode requestNode) {
        return getSearchData(requestNode, Collections.emptyMap());
    }
}
