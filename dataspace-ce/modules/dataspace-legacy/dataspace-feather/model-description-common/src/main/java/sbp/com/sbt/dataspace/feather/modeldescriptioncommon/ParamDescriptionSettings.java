package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

/**
 * Settings for parameter description
 */
public final class ParamDescriptionSettings implements PropertyDescriptionSettings {

    DataType type;
    boolean collection;
    Object defaultValue;

    /**
     * Get type
     */
    public DataType getType() {
        return type;
    }

    /**
     * Set type
     *
     * @param type Тип
     * @return Current settings
     */
    public ParamDescriptionSettings setType(DataType type) {
        this.type = type;
        return this;
    }

    /**
     * Is it a collection?
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     * Set collection flag
     *
     * @return Current settings
     */
    public ParamDescriptionSettings setCollection() {
        collection = true;
        return this;
    }

    /**
     * Get default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Set default value
     *
     * @return Current settings
     */
    public ParamDescriptionSettings setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
