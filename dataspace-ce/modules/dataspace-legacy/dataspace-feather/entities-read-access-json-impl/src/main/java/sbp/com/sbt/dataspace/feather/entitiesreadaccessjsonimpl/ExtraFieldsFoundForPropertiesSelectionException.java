package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Extra fields for property selection were found
 */
public class ExtraFieldsFoundForPropertiesSelectionException extends FeatherException {

    /**
     * @param fieldNames The names of the fields
     * @param json       JSON
     */
    ExtraFieldsFoundForPropertiesSelectionException(Set<String> fieldNames, String json) {
        super("Additional fetch fields detected", param("Field names", fieldNames), param("JSON", json));
    }
}
