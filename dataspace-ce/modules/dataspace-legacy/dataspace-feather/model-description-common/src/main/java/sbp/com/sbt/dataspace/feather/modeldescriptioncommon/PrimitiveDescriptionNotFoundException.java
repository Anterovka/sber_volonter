package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The description of the primitive was not found
 */
public class PrimitiveDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    PrimitiveDescriptionNotFoundException(String propertyName) {
        super("No description of primitive found", param("Property name", propertyName));
    }
}
