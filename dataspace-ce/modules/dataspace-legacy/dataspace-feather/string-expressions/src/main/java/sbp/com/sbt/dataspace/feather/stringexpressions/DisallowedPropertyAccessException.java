package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Invalid property access
 */
public class DisallowedPropertyAccessException extends FeatherException {

    /**
     * @param propertyType Type of property
     * @param propertyName Property name
     * @param context      Контекст
     */
    DisallowedPropertyAccessException(ExpressionType propertyType, String propertyName, String context) {
        super("Invalid property access", param("Property type", propertyType), param("Property name", propertyName), param("Context", context));
    }
}
