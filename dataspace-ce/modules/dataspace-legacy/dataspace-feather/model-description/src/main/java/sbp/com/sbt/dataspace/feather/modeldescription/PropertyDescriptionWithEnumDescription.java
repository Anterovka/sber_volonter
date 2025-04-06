package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Description of the property with enumeration description
 */
public interface PropertyDescriptionWithEnumDescription extends PropertyDescription {

    /**
     * Get enumeration description
     */
    EnumDescription getEnumDescription();
}
