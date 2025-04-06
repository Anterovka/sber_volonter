package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Procedure5;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity description metadata
 */
class EntityDescriptionMetaData {

    Node<String> inHeirTypesStringNode;
    Map<String, Procedure5<SqlQueryProcessor, EntityDescription, String, String, JsonNode>> propertyProcessFunctions = new HashMap<>();
}
