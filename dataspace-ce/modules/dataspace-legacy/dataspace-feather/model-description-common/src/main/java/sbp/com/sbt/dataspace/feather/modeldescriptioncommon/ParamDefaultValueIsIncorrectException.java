package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The default value of the parameter is incorrect
 */
public class ParamDefaultValueIsIncorrectException extends FeatherException {

    /**
     * @param paramName Parameter name
     */
    ParamDefaultValueIsIncorrectException(String paramName) {
        super("The default parameter value is incorrect", param("Parameter name", paramName));
    }
}
