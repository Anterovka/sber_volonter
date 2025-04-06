package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during parsing of model description
 */
public class ParseModelDescriptionException extends FeatherException {

    /**
     * @param throwable
     * @param resourceName Resource name
     */
    ParseModelDescriptionException(Throwable throwable, String resourceName) {
        super(throwable, "Error during parsing of model description", param("Resource name", resourceName));
    }
}
