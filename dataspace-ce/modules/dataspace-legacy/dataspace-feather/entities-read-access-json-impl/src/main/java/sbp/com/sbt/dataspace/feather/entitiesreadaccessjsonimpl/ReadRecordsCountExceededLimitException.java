package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The number of read records exceeded the limit
 */
public class ReadRecordsCountExceededLimitException extends FeatherException {

    /**
     * @param limit The limitation
     */
    ReadRecordsCountExceededLimitException(int limit) {
        super("The number of read records exceeded the limitation", param("Limitation", limit), "It is necessary to restrict the request (For example, enter limitations on the number of elements or set more stringent search/filtering conditions)");
    }
}
