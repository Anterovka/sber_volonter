package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Alias duplicate detected
 */
public class DuplicateAliasFoundException extends FeatherException {

    /**
     * @param alias Alias
     */
    DuplicateAliasFoundException(String alias) {
        super("Alias duplicate detected", param("Alias", alias));
    }
}
