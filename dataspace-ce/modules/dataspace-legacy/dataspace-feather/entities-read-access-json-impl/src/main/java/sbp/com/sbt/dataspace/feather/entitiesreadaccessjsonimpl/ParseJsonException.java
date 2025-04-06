package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during parsing of JSON
 */
public class ParseJsonException extends FeatherException {

    /**
     * @param throwable Exception
     * @param json      JSON
     */
    ParseJsonException(Throwable throwable, String json) {
        super(throwable, "Error during JSON parsing", param("JSON", json));
    }
}
