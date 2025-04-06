package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Different grouping structures with the same name have been detected
 */
public class DifferentGroupStructuresWithSameNameFoundException extends FeatherException {

    /**
     * @param groupName The name of the grouping
     */
    DifferentGroupStructuresWithSameNameFoundException(String groupName) {
        super("Different grouping structures with the same name have been detected", param("Grouping Name", groupName));
    }
}
