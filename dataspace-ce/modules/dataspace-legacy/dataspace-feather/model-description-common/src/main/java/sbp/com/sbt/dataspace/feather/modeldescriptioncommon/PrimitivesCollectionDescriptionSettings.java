package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

/**
 * Settings for describing a collection of primitives
 */
public final class PrimitivesCollectionDescriptionSettings extends AbstractCollectionDescriptionSettings<PrimitivesCollectionDescriptionSettings> {

    DataType type;
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
    public PrimitivesCollectionDescriptionSettings setType(DataType type) {
        this.type = type;
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
    public PrimitivesCollectionDescriptionSettings setEnumType(String enumType) {
        this.enumType = enumType;
        return this;
    }
}
