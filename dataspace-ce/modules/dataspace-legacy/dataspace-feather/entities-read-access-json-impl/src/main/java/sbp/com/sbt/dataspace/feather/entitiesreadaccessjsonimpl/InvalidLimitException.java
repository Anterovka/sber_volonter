package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Invalid value of the quantity limit of elements
 */
public class InvalidLimitException extends FeatherException {

    /**
     * @param limit The limitation
     */
    InvalidLimitException(int limit) {
        super("Invalid value of the quantity limitation", param("Limit", limit), "Only non-negative values are allowed");
    }
}
