package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No reverse link description found for the link
 */
public class ReferenceBackReferenceDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    ReferenceBackReferenceDescriptionNotFoundException(String propertyName) {
        super("No description of the backlink was found for the link", param("Property name", propertyName));
    }
}
