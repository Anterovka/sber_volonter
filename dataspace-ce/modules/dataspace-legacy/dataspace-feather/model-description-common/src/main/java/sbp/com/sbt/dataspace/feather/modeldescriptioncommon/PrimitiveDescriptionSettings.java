package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

/**
 * Settings for primitive description
 */
public final class PrimitiveDescriptionSettings extends AbstractPropertyDescriptionWithColumnNameSettings<PrimitiveDescriptionSettings> {

    DataType type;
    boolean mandatory;
    String enumType;

    /**
     * Get type
     */
    public DataType getType() {
        return type;
    }

    /**
     * Set type
     *
     * @return Current settings
     */
    public PrimitiveDescriptionSettings setType(DataType type) {
        this.type = type;
        return this;
    }

    /**
     * Is it mandatory?
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Set the mandatory flag
     *
     * @return Current settings
     */
    public PrimitiveDescriptionSettings setMandatory() {
        this.mandatory = true;
        return this;
    }

    /**
     * Get enum type
     */
    public String getEnumType() {
        return enumType;
    }

    /**
     * Set enumeration type
     *
     * @return Current settings
     */
    public PrimitiveDescriptionSettings setEnumType(String enumType) {
        this.enumType = enumType;
        return this;
    }
}
