package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Description of property with mandatory attribute
 */
public interface PropertyDescriptionWithMandatory extends PropertyDescription {

    /**
     * Is it mandatory?
     */
    boolean isMandatory();
}
