package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Property type conflict detected
 */
public class PropertiesTypeConflictFoundException extends FeatherException {

    /**
     * @param propertyData1 Property data 1
     * @param propertyData2 Property data 2
     */
    PropertiesTypeConflictFoundException(PropertyData propertyData1, PropertyData propertyData2) {
        super("Property type conflict detected", param("Property data 1", propertyData1), param("Property data 2", propertyData2));
    }
}
