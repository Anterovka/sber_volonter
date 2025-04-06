package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No description of enumeration was found
 */
public class EnumDescriptionNotFoundException extends FeatherException {

    /**
     * @param enumType Enumeration type
     */
    EnumDescriptionNotFoundException(String enumType) {
        super("Description of enumeration not found", param("Enumeration Type", enumType));
    }
}
