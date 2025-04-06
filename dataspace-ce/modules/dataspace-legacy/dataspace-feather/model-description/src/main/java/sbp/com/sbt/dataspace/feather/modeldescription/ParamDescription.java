package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Description of parameter
 */
public interface ParamDescription {

    /**
     * Get the name
     */
    String getName();

    /**
     * Get type
     */
    DataType getType();

    /**
     * Is it a collection?
     */
    boolean isCollection();

    /**
     * Get default value
     */
    Object getDefaultValue();
}
