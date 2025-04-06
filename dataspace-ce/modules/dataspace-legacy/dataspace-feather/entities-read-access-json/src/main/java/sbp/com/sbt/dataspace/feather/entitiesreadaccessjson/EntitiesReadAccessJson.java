package sbp.com.sbt.dataspace.feather.entitiesreadaccessjson;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;

import java.sql.Connection;
import java.util.Collections;
import java.util.Map;

/**
 * Access to entities for reading through JSON
 */
// SpringBean
public interface EntitiesReadAccessJson {

    /**
     * Find entities
     *
     * @param requestJson JSON query
     * @param params  Parameters
     * @return JSON response
     */
    // NotNull
    String searchEntities(String requestJson, Map<String, Object> params);

    /**
     * Find entities
     *
     * @param requestJson JSON query
     * @return JSON response
     */
    // NotNull
    default String searchEntities(String requestJson) {
        return searchEntities(requestJson, Collections.emptyMap());
    }

    /**
     * Find entities
     *
     * @param requestNode Request node
     * @param params  Parameters
     * @return Response node
     */
    // NotNull
    JsonNode searchEntities(JsonNode requestNode, Map<String, Object> params);

    JsonNode searchEntities(JsonNode requestNode, Map<String, Object> params, Connection connection);

    /**
     * Find entities
     *
     * @param requestNode Request node
     * @return Response node
     */
    // NotNull
    default JsonNode searchEntities(JsonNode requestNode) {
        return searchEntities(requestNode, Collections.emptyMap());
    }

  default JsonNode searchEntities(JsonNode requestNode, Connection connection) {
    return searchEntities(requestNode, Collections.emptyMap(), connection);
  }

    /**
     * Streaming entities reading
     *
     * @param requestNode Request node
     * @param parameters  Parameters
     * @return Response stream
     */
    // NotNull
    Flux<JsonNode> streamEntities(JsonNode requestNode, Map<String, Object> params);

    /**
     * Streaming entities reading
     *
     * @param requestNode Request node
     * @return Response stream
     */
    // NotNull
    default Flux<JsonNode> streamEntities(JsonNode requestNode) {
        return streamEntities(requestNode, Collections.emptyMap());
    }
}
