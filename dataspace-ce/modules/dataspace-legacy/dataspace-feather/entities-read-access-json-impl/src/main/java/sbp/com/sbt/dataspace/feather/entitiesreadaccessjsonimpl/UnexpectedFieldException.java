package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected field
 */
public class UnexpectedFieldException extends FeatherException {

    /**
     * @param json               JSON
     * @param fieldName          The name of the field
     * @param expectedFieldNames Expected field names
     */
    UnexpectedFieldException(String json, String fieldName, Set<String> expectedFieldNames) {
        super("Unexpected field", param("JSON", json), param("Field name", fieldName), param("Expected field names", expectedFieldNames));
    }
}
