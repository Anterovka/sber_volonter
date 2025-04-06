package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The node is not a symbol.
 */
public class NotCharacterNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    NotCharacterNodeException(String json) {
        super("The node is not a symbol", param("JSON", json));
    }
}
