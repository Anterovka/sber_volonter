package sbp.com.sbt.dataspace.feather.modeldescription;

/**
 * Collection description
 */
public interface CollectionDescription extends PropertyDescriptionWithColumnName {

    /**
     * Get table name
     */
    String getTableName();

    /**
     * Get column name with owner
     */
    String getOwnerColumnName();
}
