package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The types of array elements must be of the same type.
 */
public class ArrayElemTypesShouldBeOfOneTypeException extends FeatherException {

    /**
     * @param elemTypes Types of array items
     */
    ArrayElemTypesShouldBeOfOneTypeException(DataType[] elemTypes) {
        super("The types of array elements must be of the same type", param("Array element types", elemTypes));
    }
}
