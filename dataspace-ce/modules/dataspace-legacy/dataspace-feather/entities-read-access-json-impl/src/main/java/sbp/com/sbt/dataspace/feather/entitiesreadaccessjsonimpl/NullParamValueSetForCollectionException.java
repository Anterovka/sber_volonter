package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Null value is set for parameter collection
 */
public class NullParamValueSetForCollectionException extends FeatherException {

    /**
     * @param paramName Parameter name
     */
    NullParamValueSetForCollectionException(String paramName) {
        super("Null value is set for parameter-collection", param("Parameter name", paramName));
    }
}
