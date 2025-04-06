package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unsupported enum type values
 */
public class UnsupportedEnumValueTypeException extends FeatherException {

    /**
     * @param enumValueType The enumeration value type
     */
    UnsupportedEnumValueTypeException(DataType enumValueType) {
        super("Unsupported enumeration type value", param("Expected enumeration type value", DataType.STRING), param("Enumeration type value", enumValueType));
    }
}
