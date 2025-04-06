package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The node is not a logical value.
 */
public class NotBooleanNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    NotBooleanNodeException(String json) {
        super("The node is not a logical value", param("JSON", json));
    }
}
