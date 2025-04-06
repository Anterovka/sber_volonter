package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Extra fields were found when the number of elements was limited to zero
 */
public class ExtraFieldsFoundForZeroLimitException extends FeatherException {

    /**
     * @param fieldNames The names of the fields
     * @param json       JSON
     */
    ExtraFieldsFoundForZeroLimitException(Set<String> fieldNames, String json) {
        super("Additional fields found with zero element limitation", param("Field names", fieldNames), param("JSON", json));
    }
}
