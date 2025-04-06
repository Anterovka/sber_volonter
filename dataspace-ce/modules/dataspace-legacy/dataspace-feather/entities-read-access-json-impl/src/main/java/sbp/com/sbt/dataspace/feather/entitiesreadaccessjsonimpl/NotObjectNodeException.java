package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The node is not an object
 */
public class NotObjectNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    NotObjectNodeException(String json) {
        super("The node is not an object", param("JSON", json));
    }
}
