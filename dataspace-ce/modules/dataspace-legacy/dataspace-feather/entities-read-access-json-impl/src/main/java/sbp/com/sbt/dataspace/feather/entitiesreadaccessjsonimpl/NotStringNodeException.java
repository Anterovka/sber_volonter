package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
* The node is not a string
 */
public class NotStringNodeException extends FeatherException {

    /**
     * @param json JSON
     */
    NotStringNodeException(String json) {
super("The node is not a string", param("JSON", json));
    }
}
