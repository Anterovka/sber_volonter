package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Extra fields for merging requests have been found
 */
public class ExtraFieldsFoundForRequestsMergeException extends FeatherException {

    /**
     * @param fieldNames The names of the fields
     * @param json       JSON
     */
    ExtraFieldsFoundForRequestsMergeException(Set<String> fieldNames, String json) {
        super("Additional merge fields detected", param("Field names", fieldNames), param("JSON", json));
    }
}
