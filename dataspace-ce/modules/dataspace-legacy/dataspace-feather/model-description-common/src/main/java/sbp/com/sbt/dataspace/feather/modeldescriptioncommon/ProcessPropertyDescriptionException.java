package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during processing of property description
 */
public class ProcessPropertyDescriptionException extends FeatherException {

    /**
     * @param throwable    Exception
     * @param propertyName Property name
     */
    ProcessPropertyDescriptionException(Throwable throwable, String propertyName) {
        super(throwable, "Error during processing of property description", param("Property name", propertyName));
    }
}
