package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Description of the property with the column name
 */
public interface PropertyDescriptionWithColumnName extends PropertyDescription {

    /**
     * Get column name
     */
    String getColumnName();
}
