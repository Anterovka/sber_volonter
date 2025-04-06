package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

/**
 * Abstract collection description settings
 */
class AbstractCollectionDescriptionSettings<S> extends AbstractPropertyDescriptionWithColumnNameSettings<S> {

    String tableName;
    String ownerColumnName;

    /**
     * Get table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Set table name
     *
     * @return Current settings
     */
    public S setTableName(String tableName) {
        this.tableName = tableName;
        return (S) this;
    }

    /**
     * Get column name with owner
     */
    public String getOwnerColumnName() {
        return ownerColumnName;
    }

    /**
     * Set the column name with the owner
     *
     * @return Current settings
     */
    public S setOwnerColumnName(String ownerColumnName) {
        this.ownerColumnName = ownerColumnName;
        return (S) this;
    }
}
