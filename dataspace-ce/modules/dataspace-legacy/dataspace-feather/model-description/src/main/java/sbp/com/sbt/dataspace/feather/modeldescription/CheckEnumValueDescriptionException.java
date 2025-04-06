package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during enumeration value check
 */
public final class CheckEnumValueDescriptionException extends FeatherException {

    /**
     * @param throwable Exception
     * @param enumValue The enumeration value
     */
    public CheckEnumValueDescriptionException(Throwable throwable, String enumValue) {
        super(throwable, "Error during enumeration value check", param("Enumeration value", enumValue));
    }
}
