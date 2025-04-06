package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Invalid parameter type
 */
public class InvalidParamTypeException extends FeatherException {

    /**
     * @param paramName     Parameter name
     * @param paramType Parameter type
     */
    InvalidParamTypeException(String paramName, DataType paramType) {
        super("Invalid parameter type", param("Parameter name", paramName), param("Type of parameter", paramType));
    }
}
