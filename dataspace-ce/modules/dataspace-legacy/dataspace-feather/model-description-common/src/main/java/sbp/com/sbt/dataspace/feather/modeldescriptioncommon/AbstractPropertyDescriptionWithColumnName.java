package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescriptionWithColumnName;

/**
 * Abstract description of the property with the column name
 */
class AbstractPropertyDescriptionWithColumnName extends AbstractPropertyDescription implements PropertyDescriptionWithColumnName {

    String columnName;

    /**
     * Process
     *
     * @param propertyDescriptionSettings The property description settings
     */
    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        columnName = ((AbstractPropertyDescriptionWithColumnNameSettings<?>) propertyDescriptionSettings).columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}
