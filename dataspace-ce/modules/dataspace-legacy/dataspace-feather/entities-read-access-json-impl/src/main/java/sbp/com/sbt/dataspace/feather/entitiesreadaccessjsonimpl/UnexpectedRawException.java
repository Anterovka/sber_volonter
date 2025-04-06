package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected RAW object
 */
public class UnexpectedRawException extends FeatherException {

    /**
     * @param raw RAW-объект
     */
    UnexpectedRawException(Object raw) {
        super("Unexpected RAW object", param("RAW object", raw));
    }
}
