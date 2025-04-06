package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Property not found on entity
 */
public class PropertyNotFoundException extends FeatherException {

    /**
     * @param entityType   Entity type
     * @param propertyName Property name
     */
    PropertyNotFoundException(String entityType, String propertyName) {
        super("Property not found for entity", param("Entity type", entityType), param("Property name", propertyName));
    }
}
