package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Invalid value of the read record limit constraint
 */
public class InvalidReadRecordsLimitException extends FeatherException {

    /**
     * @param limit The limitation
     */
    InvalidReadRecordsLimitException(int limit) {
        super("Invalid value of read record restriction", param("Restriction", limit), "Only positive values are allowed");
    }
}
