package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Description of the property with the type
 */
public interface PropertyDescriptionWithType extends PropertyDescription {

    /**
     * Get type
     */
    // NotNull
    DataType getType();
}
