package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * A duplicate of the enumeration type is detected
 */
public class DuplicateEnumTypeFoundException extends FeatherException {

    /**
     * @param enumType The enumeration type
     */
    DuplicateEnumTypeFoundException(String enumType) {
        super("Duplicate enumeration type detected", param("Enumeration type", enumType));
    }
}
