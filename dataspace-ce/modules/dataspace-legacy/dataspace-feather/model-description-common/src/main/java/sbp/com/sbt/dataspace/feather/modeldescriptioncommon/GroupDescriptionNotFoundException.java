package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No description of grouping was found
 */
public class GroupDescriptionNotFoundException extends FeatherException {

    /**
     * @param propertyName Property name
     */
    GroupDescriptionNotFoundException(String propertyName) {
        super("Description of grouping not found", param("Property name", propertyName));
    }
}
