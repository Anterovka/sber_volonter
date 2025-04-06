package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during enumeration description check
 */
public final class CheckEnumDescriptionException extends FeatherException {

    /**
     * @param throwable              Exception
     * @param enumDescription Description of the enumeration
     */
    public CheckEnumDescriptionException(Throwable throwable, EnumDescription enumDescription) {
        super(throwable, "Error during enumeration description check", param("Enumeration Type", enumDescription.getName()));
    }
}
