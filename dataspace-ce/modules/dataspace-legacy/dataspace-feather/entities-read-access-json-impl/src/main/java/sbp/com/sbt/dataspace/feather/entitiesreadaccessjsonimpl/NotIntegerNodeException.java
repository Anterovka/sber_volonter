package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The node is not an integer
 */
public class NotIntegerNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    NotIntegerNodeException(String json) {
        super("The node is not an integer", param("JSON", json));
    }
}
