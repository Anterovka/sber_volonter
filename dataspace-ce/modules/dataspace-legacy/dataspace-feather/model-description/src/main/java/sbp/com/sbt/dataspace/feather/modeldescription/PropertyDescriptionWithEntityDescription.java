package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Property description with entity description
 */
public interface PropertyDescriptionWithEntityDescription extends PropertyDescription {

    /**
     * Get entity description
     */
    // NotNull
    EntityDescription getEntityDescription();
}
