package sbp.com.sbt.dataspace.graphqlschema;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The field name is reserved
 */
public class FieldNameReservedException extends FeatherException {

    /**
     * @param fieldName Field name
     */
    FieldNameReservedException(String fieldName) {
        super("The name of the field is reserved", param("The name of the field", fieldName));
    }
}
