package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

/**
 * Primitive data
 */
class PrimitiveData {

    String base;
    PropertyType propertyType = PropertyType.FROM_MODEL;
    DataType type;
    Object value;

    /**
     * @param type  Тип
     * @param value The value
     */
    PrimitiveData(DataType type, Object value) {
        this.type = type;
        this.value = value;
    }
}
