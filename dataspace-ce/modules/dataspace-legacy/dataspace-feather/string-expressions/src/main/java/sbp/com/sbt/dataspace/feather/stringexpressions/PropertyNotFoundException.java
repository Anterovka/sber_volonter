package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The property was not found
 */
public class PropertyNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     * @param context      Контекст
     */
    PropertyNotFoundException(String propertyName, String context) {
        super("Property not found", param("Property name", propertyName), param("Context", context));
    }
}
