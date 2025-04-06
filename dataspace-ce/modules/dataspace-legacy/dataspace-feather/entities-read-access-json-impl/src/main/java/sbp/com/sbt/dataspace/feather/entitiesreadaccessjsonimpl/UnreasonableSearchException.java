package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Ineffective search
 */
public class UnreasonableSearchException extends FeatherException {

    /**
     * @param json JSON
     */
    UnreasonableSearchException(String json) {
        super("Inefficient search", param("JSON", json), "A zero element limit is set and the search with a total element count is not set");
    }
}
