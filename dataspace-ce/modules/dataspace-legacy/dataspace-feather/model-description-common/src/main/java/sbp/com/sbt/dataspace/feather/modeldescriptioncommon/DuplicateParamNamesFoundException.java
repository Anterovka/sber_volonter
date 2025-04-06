package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Duplicate parameter names were found
 */
public class DuplicateParamNamesFoundException extends FeatherException {

    /**
     * @param propertyName Parameter name
     */
    DuplicateParamNamesFoundException(String propertyName) {
        super("Duplicate parameter names have been found", param("Parameter name", propertyName));
    }
}
