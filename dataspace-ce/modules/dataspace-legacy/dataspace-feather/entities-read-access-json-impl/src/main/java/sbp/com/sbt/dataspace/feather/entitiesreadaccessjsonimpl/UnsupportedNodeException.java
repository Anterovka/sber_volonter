package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unsupported node
 */
public class UnsupportedNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    UnsupportedNodeException(String json) {
        super("Unsupported node", param("JSON", json));
    }
}
