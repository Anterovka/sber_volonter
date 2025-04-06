package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The default value is not supported for collection parameters
 */
public class CollectionParamDefaultValueNotSupportedException extends FeatherException {

    /**
     * @param paramName Parameter name
     */
    CollectionParamDefaultValueNotSupportedException(String paramName) {
        super("The default value is not supported for collection parameters", param("Parameter name", paramName));
    }
}
