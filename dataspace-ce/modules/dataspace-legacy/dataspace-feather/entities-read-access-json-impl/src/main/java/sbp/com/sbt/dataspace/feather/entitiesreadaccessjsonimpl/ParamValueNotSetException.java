package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The parameter value is not set
 */
public class ParamValueNotSetException extends FeatherException {

    /**
     * @param paramName Parameter name
     */
    ParamValueNotSetException(String paramName) {
        super("The parameter value is not set", param("Parameter name", paramName));
    }
}
