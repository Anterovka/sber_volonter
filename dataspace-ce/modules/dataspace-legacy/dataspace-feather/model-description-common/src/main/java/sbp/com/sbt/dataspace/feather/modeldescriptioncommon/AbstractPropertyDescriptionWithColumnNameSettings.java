package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

/**
 * Abstract settings of the property description with the column name
 *
 * @param <S> Type of settings
 */
class AbstractPropertyDescriptionWithColumnNameSettings<S> implements PropertyDescriptionSettings {

    String columnName;

    /**
     * Get column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Set the column name
     *
     * @return Current settings
     */
    public S setColumnName(String columnName) {
        this.columnName = columnName;
        return (S) this;
    }
}
