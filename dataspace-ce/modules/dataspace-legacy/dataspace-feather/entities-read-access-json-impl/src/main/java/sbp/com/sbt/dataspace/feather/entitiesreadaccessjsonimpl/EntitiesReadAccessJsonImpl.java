package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import reactor.core.publisher.Flux;
import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJsonHelper;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;
import sbp.com.sbt.dataspace.feather.tablequeryprovider.TableQueryProvider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getRequestData;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getRequestNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getSchemaNameNode;

/**
 * Implementation of read access to entities through JSON
 */
class EntitiesReadAccessJsonImpl implements EntitiesReadAccessJson, DisposableBean {

  ModelDescription modelDescription;
  ExpressionsProcessor expressionsProcessor;
  SecurityDriver securityDriver;
  NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  SqlDialect sqlDialect;
  Integer defaultLimit;
  Node<String> schemaNameNode;
  int maxSecurityRecursionDepth;
  int readRecordsLimit;
  TableQueryProvider tableQueryProvider;
  boolean optimizeJoins;

    /**
     * @param modelDescription               Description of the model
     * @param expressionsProcessor           The expressions processor
     * @param securityDriver                 Security driver
     * @param namedParameterJdbcTemplate     Template JDBC with named parameters
     * @param entitiesReadAccessJsonSettings The settings for entity read access via JSON
     */
  EntitiesReadAccessJsonImpl(ModelDescription modelDescription, ExpressionsProcessor expressionsProcessor, SecurityDriver securityDriver, NamedParameterJdbcTemplate namedParameterJdbcTemplate, EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings) {
    this.modelDescription = modelDescription;
    this.expressionsProcessor = expressionsProcessor;
    this.securityDriver = securityDriver;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    sqlDialect = entitiesReadAccessJsonSettings.sqlDialect;
    defaultLimit = entitiesReadAccessJsonSettings.defaultLimit;
    schemaNameNode = getSchemaNameNode(entitiesReadAccessJsonSettings.schemaName);
    maxSecurityRecursionDepth = entitiesReadAccessJsonSettings.maxSecurityRecursionDepth;
    readRecordsLimit = entitiesReadAccessJsonSettings.readRecordsLimit;
    tableQueryProvider = entitiesReadAccessJsonSettings.tableQueryProvider;
    optimizeJoins = entitiesReadAccessJsonSettings.optimizeJoins;
    MetaData.put(modelDescription, entitiesReadAccessJsonSettings.offsetDateTimeZoneId);
  }

    /**
     * Execute SQL query
     *
     * @param requestData Request data
     * @return The data of the collection of entities
     */
  CollectionData<EntityData> executeSqlQuery(RequestData requestData) {
    SpecialSortedSet<EntityData> entitiesData = new SpecialSortedSet<>(requestData.startSqlQueryProcessor.offset);
    Pointer<Integer> readRecordsCountPointer = new Pointer<>(0);
    namedParameterJdbcTemplate.query(requestData.sqlQuery, requestData.mapSqlParameterSource, resultSet -> {
      if (++readRecordsCountPointer.object > readRecordsLimit) {
        throw new ReadRecordsCountExceededLimitException(readRecordsLimit);
      }
      EntityData entityData = getEntityData(requestData, resultSet);

      int queryId = resultSet.getInt(1);
      if (requestData.startSqlQueryProcessor.mergeRequestQueryIds != null) {
        if (requestData.startSqlQueryProcessor.mergeRequestQueryIds.contains(queryId)) {
          SqlQueryProcessor sqlQueryProcessor = requestData.sqlQueryProcessors.get(queryId);
          entitiesData.add(sqlQueryProcessor.getOrder(resultSet), entityData);
        }
      } else if (queryId == 0) {
        entitiesData.add(requestData.startSqlQueryProcessor.getOrder(resultSet), entityData);
      }
    });
    entitiesData.forEach(entityData -> requestData.startSqlQueryProcessor.finalProcessors.forEach(finalProcessor -> finalProcessor.accept(entityData, Collections.emptyList())));
    return requestData.startSqlQueryProcessor.getCollectionData(null, Collections.emptyList(), entitiesData);
  }

  CollectionData<EntityData> executeSqlQuery(RequestData requestData, Connection connection) {
    SpecialSortedSet<EntityData> entitiesData = new SpecialSortedSet<>(requestData.startSqlQueryProcessor.offset);
    Pointer<Integer> readRecordsCountPointer = new Pointer<>(0);
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new SingleConnectionDataSource(connection, true));
    namedParameterJdbcTemplate.query(requestData.sqlQuery, requestData.mapSqlParameterSource, resultSet -> {
      if (++readRecordsCountPointer.object > readRecordsLimit) {
        throw new ReadRecordsCountExceededLimitException(readRecordsLimit);
      }
      EntityData entityData = getEntityData(requestData, resultSet);

      int queryId = resultSet.getInt(1);
      if (requestData.startSqlQueryProcessor.mergeRequestQueryIds != null) {
        if (requestData.startSqlQueryProcessor.mergeRequestQueryIds.contains(queryId)) {
          SqlQueryProcessor sqlQueryProcessor = requestData.sqlQueryProcessors.get(queryId);
          entitiesData.add(sqlQueryProcessor.getOrder(resultSet), entityData);
        }
      } else if (queryId == 0) {
        entitiesData.add(requestData.startSqlQueryProcessor.getOrder(resultSet), entityData);
      }
    });
    entitiesData.forEach(entityData -> requestData.startSqlQueryProcessor.finalProcessors.forEach(finalProcessor -> finalProcessor.accept(entityData, Collections.emptyList())));
    return requestData.startSqlQueryProcessor.getCollectionData(null, Collections.emptyList(), entitiesData);
  }

    /**
     * Execute an asynchronous SQL query
     *
     * @param requestData Request data
     * @return Entity data stream
     */
  Flux<EntityData> executeSqlQueryFlux(RequestData requestData) {
    return Flux.create(sink -> {
        namedParameterJdbcTemplate.query(requestData.sqlQuery, requestData.mapSqlParameterSource, (RowCallbackHandler) resultSet -> sink.next(getEntityData(requestData, resultSet)));
        sink.complete();
      }
    );
  }

    /**
     * Get entity data
     *
     * @param requestData Request data
     * @param resultSet   Result set
     */
  EntityData getEntityData(RequestData requestData, ResultSet resultSet) throws SQLException {
    int queryId = resultSet.getInt(1);
    EntityData entityData;
    if (requestData.startSqlQueryProcessor.mergeRequestQueryIds != null) {
      if (requestData.startSqlQueryProcessor.mergeRequestQueryIds.contains(queryId)) {
        SqlQueryProcessor sqlQueryProcessor = requestData.sqlQueryProcessors.get(queryId);
        entityData = new EntityData();
        entityData.queryId = queryId;
        entityData.id = resultSet.getString(sqlQueryProcessor.idColumnData.columnIndex);
        entityData.entityDescription = sqlQueryProcessor.entityDescription;
      } else {
        entityData = null;
      }
    } else if (queryId == 0) {
      entityData = new EntityData();
      if (!requestData.propertiesSelection) {
        entityData.id = resultSet.getString(requestData.startSqlQueryProcessor.idColumnData.columnIndex);
      }
      entityData.entityDescription = requestData.startSqlQueryProcessor.entityDescription;
    } else {
      entityData = null;
    }
    requestData.sqlQueryProcessors.get(queryId).recordProcessors.forEach(recordProcessor -> recordProcessor.accept(entityData, resultSet));
    return entityData;
  }

    /**
     * Process primitives
     *
     * @param entityData     Entity data
     * @param propertiesNode Property node
     */
  void processPrimitives(EntityData entityData, ObjectNode propertiesNode) {
    entityData.primitives.forEach((name, primitiveData) -> propertiesNode.set(name, getAliasedPropertyNode(primitiveData.base, primitiveData.propertyType, Helper.getPrimitiveNode(primitiveData.type, primitiveData.value))));
  }

    /**
     * Get primitive collection node
     *
     * @param primitivesCollectionData The primitive collection data
     */
  JsonNode getPrimitivesCollectionNode(CollectionData<PrimitiveData> primitivesCollectionData) {
    ObjectNode result = Helper.OBJECT_MAPPER.createObjectNode();
    ArrayNode elementsNode = result.putArray(EntitiesReadAccessJsonHelper.ELEMENTS_FIELD_NAME);
    primitivesCollectionData.elements.forEach(element -> elementsNode.add(Helper.getPrimitiveNode(element.type, element.value)));
    if (primitivesCollectionData.count != null) {
      result.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, primitivesCollectionData.count);
    }
    return getAliasedPropertyNode(primitivesCollectionData.base, result);
  }

    /**
     * Get JSON node
     *
     * @param referencesCollectionData The data of the reference collection
     */
  JsonNode getReferencesCollectionNode(CollectionData<EntityData> referencesCollectionData) {
    ObjectNode result = Helper.OBJECT_MAPPER.createObjectNode();
    ArrayNode elementsNode = result.putArray(EntitiesReadAccessJsonHelper.ELEMENTS_FIELD_NAME);
    referencesCollectionData.elements.stream()
      .map(this::getEntityNode)
      .forEach(elementsNode::add);
    if (referencesCollectionData.count != null) {
      result.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, referencesCollectionData.count.intValue());
    }
    return getAliasedPropertyNode(referencesCollectionData.base, result);
  }


    /**
     * Get grouping node
     *
     * @param groupData Grouping data
     */
  JsonNode getGroupNode(GroupData groupData) {
    ObjectNode result = Helper.OBJECT_MAPPER.createObjectNode();
    groupData.primitives.forEach((name, primitiveData) -> result.set(name, getAliasedPropertyNode(primitiveData.base, primitiveData.propertyType, Helper.getPrimitiveNode(primitiveData.type, primitiveData.value))));
    groupData.references.forEach((name, referenceEntityData) -> result.set(name, referenceEntityData == null ? null : getEntityNode(referenceEntityData)));
    return getAliasedPropertyNode(groupData.base, result);
  }

    /**
     * Get entity node
     *
     * @param entityData Entity data
     */
  JsonNode getEntityNode(EntityData entityData) {
    ObjectNode result = Helper.OBJECT_MAPPER.createObjectNode();
    result.put(EntitiesReadAccessJsonHelper.TYPE_FIELD_NAME, entityData.entityDescription.getName());
    result.put(EntitiesReadAccessJsonHelper.ID_FIELD_NAME, entityData.id);
    if (entityData.access) {
      if (entityData.invalid) {
        result.put(EntitiesReadAccessJsonHelper.INVALID_FIELD_NAME, true);
      }
      if (entityData.incorrectCasted) {
        result.put(EntitiesReadAccessJsonHelper.INCORRECT_CASTED_FIELD_NAME, true);
      }
      if (!(entityData.invalid || entityData.incorrectCasted)) {
        ObjectNode propertiesNode = Helper.OBJECT_MAPPER.createObjectNode();
        processPrimitives(entityData, propertiesNode);
        entityData.primitivesCollections.forEach((name, primitivesCollectionData) -> propertiesNode.set(name, getPrimitivesCollectionNode(primitivesCollectionData)));
        entityData.references.forEach((name, referenceEntityData) -> propertiesNode.set(name, referenceEntityData == null ? null : getEntityNode(referenceEntityData)));
        entityData.referencesCollections.forEach((name, referencesCollectionData) -> propertiesNode.set(name, getReferencesCollectionNode(referencesCollectionData)));
        entityData.groups.forEach((name, groupData) -> {
          if (!groupData.primitives.isEmpty() || !groupData.references.isEmpty()) {
            propertiesNode.set(name, getGroupNode(groupData));
          }
        });
        if (!propertiesNode.isEmpty()) {
          result.set(EntitiesReadAccessJsonHelper.PROPERTIES_FIELD_NAME, propertiesNode);
        }
        if (entityData.aggregateVersion != null) {
          result.put(EntitiesReadAccessJsonHelper.AGGREGATE_VERSION_FIELD_NAME, entityData.aggregateVersion.toString());
        }
      }
    } else {
      result.put(EntitiesReadAccessJsonHelper.ACCESS_FIELD_NAME, false);
    }
    return getAliasedPropertyNode(entityData.base, result);
  }

    /**
     * Get property node with alias
     *
     * @param base         The property on which the property is based
     * @param propertyType Type of property
     * @param valueNode    Value node
     */
  JsonNode getAliasedPropertyNode(String base, PropertyType propertyType, JsonNode valueNode) {
    if (base != null || propertyType == PropertyType.CALCULATED) {
      ObjectNode result = Helper.OBJECT_MAPPER.createObjectNode();
      if (base != null) {
        result.put(EntitiesReadAccessJsonHelper.BASE_PROPERTY_FIELD_NAME, base);
      } else {
        result.put(EntitiesReadAccessJsonHelper.CALCULATED_EXPRESSION_FIELD_NAME, true);
      }
      result.set(EntitiesReadAccessJsonHelper.VALUE_FIELD_NAME, valueNode);
      return result;
    }
    return valueNode;
  }

    /**
     * Get property node with alias
     *
     * @param base      The property on which the property is based
     * @param valueNode Value node
     */
  JsonNode getAliasedPropertyNode(String base, JsonNode valueNode) {
    return getAliasedPropertyNode(base, PropertyType.FROM_MODEL, valueNode);
  }

    /**
     * Get JSON element node
     *
     * @param entityData          Entity data
     * @param propertiesSelection Selection of properties
     */
  JsonNode getElementJsonNode(EntityData entityData, boolean propertiesSelection) {
    if (propertiesSelection) {
      ObjectNode propertiesNode = Helper.OBJECT_MAPPER.createObjectNode();
      processPrimitives(entityData, propertiesNode);
      return propertiesNode;
    } else {
      return getEntityNode(entityData);
    }
  }

    /**
     * Get JSON node
     *
     * @param entitiesCollectionData The data of the collection of entities
     * @param propertiesSelection    Selection of properties
     */
  JsonNode getJsonNode(CollectionData<EntityData> entitiesCollectionData, boolean propertiesSelection) {
    ObjectNode result = Helper.OBJECT_MAPPER.createObjectNode();
    if (propertiesSelection) {
      result.put(EntitiesReadAccessJsonHelper.PROPERTIES_SELECTION_FIELD_NAME, true);
    }
    ArrayNode elementsNode = result.putArray(EntitiesReadAccessJsonHelper.ELEMENTS_FIELD_NAME);
    if (propertiesSelection) {
      entitiesCollectionData.elements.forEach(entityData -> {
        ObjectNode propertiesNode = Helper.OBJECT_MAPPER.createObjectNode();
        processPrimitives(entityData, propertiesNode);
        elementsNode.add(propertiesNode);
      });
    } else {
      entitiesCollectionData.elements.stream()
        .map(this::getEntityNode)
        .forEach(elementsNode::add);
    }
    if (entitiesCollectionData.count != null) {
      result.put(EntitiesReadAccessJsonHelper.COUNT_FIELD_NAME, entitiesCollectionData.count.intValue());
    }
    return result;
  }

  @Override
  public void destroy() {
    MetaData.remove(modelDescription);
  }

  @Override
  public String searchEntities(String requestJson, Map<String, Object> params) {
    return searchEntities(getRequestNode(requestJson), params).toString();
  }

  @Override
  public JsonNode searchEntities(JsonNode requestNode, Map<String, Object> params) {
    RequestData requestData = getRequestData(modelDescription, expressionsProcessor, securityDriver, sqlDialect, defaultLimit, schemaNameNode, maxSecurityRecursionDepth, tableQueryProvider, optimizeJoins, requestNode, params);
    return getJsonNode(executeSqlQuery(requestData), requestData.propertiesSelection);
  }

  @Override
  public JsonNode searchEntities(JsonNode requestNode, Map<String, Object> params, Connection connection) {
    RequestData requestData = getRequestData(modelDescription, expressionsProcessor, securityDriver, sqlDialect, defaultLimit, schemaNameNode, maxSecurityRecursionDepth, tableQueryProvider, optimizeJoins, requestNode, params);
    return getJsonNode(executeSqlQuery(requestData, connection), requestData.propertiesSelection);
  }

  @Override
  public Flux<JsonNode> streamEntities(JsonNode requestNode, Map<String, Object> params) {
    RequestData requestData = getRequestData(modelDescription, expressionsProcessor, securityDriver, sqlDialect, defaultLimit, schemaNameNode, maxSecurityRecursionDepth, tableQueryProvider, optimizeJoins, requestNode, params);
    return executeSqlQueryFlux(requestData).map(entityData -> getElementJsonNode(entityData, requestData.propertiesSelection));
  }
}
