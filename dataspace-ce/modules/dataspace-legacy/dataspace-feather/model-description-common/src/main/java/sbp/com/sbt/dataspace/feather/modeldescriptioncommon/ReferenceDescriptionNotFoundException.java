package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No description of the link was found
 */
public class ReferenceDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    ReferenceDescriptionNotFoundException(String propertyName) {
        super("Not found link description", param("Property name", propertyName));
    }
}
