package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The node is not an array
 */
public class NotArrayNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    NotArrayNodeException(String json) {
        super("The node is not an array", param("JSON", json));
    }
}
