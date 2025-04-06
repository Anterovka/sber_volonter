package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.JsonNode;
import sbp.com.sbt.dataspace.feather.common.Procedure7;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity description metadata
 */
class GroupDescriptionMetaData {

    Map<String, Procedure7<SqlQueryProcessor, EntityDescription, GroupDescription, String, String, String, JsonNode>> propertyProcessFunctions = new HashMap<>();
}
