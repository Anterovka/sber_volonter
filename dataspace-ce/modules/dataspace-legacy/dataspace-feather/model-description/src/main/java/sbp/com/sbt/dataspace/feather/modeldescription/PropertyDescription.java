package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Property description
 */
public interface PropertyDescription extends ObjectWithMetaDataManager {

    /**
     * Get description of entity owner
     */
    // NotNull
    EntityDescription getOwnerEntityDescription();

    /**
     * Get the name
     */
    // NotNull
    String getName();
}
