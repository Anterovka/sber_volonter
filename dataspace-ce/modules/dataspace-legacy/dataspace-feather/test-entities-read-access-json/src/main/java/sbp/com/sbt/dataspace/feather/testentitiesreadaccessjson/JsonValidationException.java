package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Validation JSON error
 */
public class JsonValidationException extends FeatherException {

    /**
     * @param throwable Exception
     * @param type      Тип
     * @param json      JSON
     */
    JsonValidationException(Throwable throwable, String type, String json) {
        super(throwable, "Validation error JSON", param("Type", type), param("JSON", json));
    }
}
