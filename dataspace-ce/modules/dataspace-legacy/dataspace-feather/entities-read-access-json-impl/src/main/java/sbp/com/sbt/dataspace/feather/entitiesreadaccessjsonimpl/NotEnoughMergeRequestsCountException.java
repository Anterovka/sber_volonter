package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Insufficient number of requests for merging
 */
public class NotEnoughMergeRequestsCountException extends FeatherException {

    /**
     * @param requestsCount Количество запросов
     * @param json          JSON
     */
    NotEnoughMergeRequestsCountException(int requestsCount, String json) {
        super("Insufficient number of requests for merging", param("Number of requests", requestsCount), "At least 2 requests were expected", param("JSON", json));
    }
}
