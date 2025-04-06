package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during property description check
 */
public final class CheckPropertyDescriptionException extends FeatherException {

    /**
     * @param throwable           Exception
     * @param propertyDescription Property description
     */
    public CheckPropertyDescriptionException(Throwable throwable, PropertyDescription propertyDescription) {
        super(throwable, "Error during validation of property description", param("Property name", propertyDescription.getName()));
    }
}
