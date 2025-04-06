package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The data of this entity
 */
class EntityData {

    String base;
    int queryId;
    EntityDescription entityDescription;
    String id;
    boolean invalid;
    boolean incorrectCasted;
    boolean access = true;
    Map<String, PrimitiveData> primitives = new LinkedHashMap<>();
    Map<String, CollectionData<PrimitiveData>> primitivesCollections = new LinkedHashMap<>();
    Map<String, EntityData> references = new LinkedHashMap<>();
    Map<String, CollectionData<EntityData>> referencesCollections = new LinkedHashMap<>();
    Map<String, GroupData> groups = new LinkedHashMap<>();
    Long aggregateVersion;
}
