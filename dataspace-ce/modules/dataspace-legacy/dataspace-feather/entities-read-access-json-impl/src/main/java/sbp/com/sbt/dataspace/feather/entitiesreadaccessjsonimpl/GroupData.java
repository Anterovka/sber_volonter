package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data grouping
 */
class GroupData {

    String base;
    Map<String, PrimitiveData> primitives = new LinkedHashMap<>();
    Map<String, EntityData> references = new LinkedHashMap<>();
}
