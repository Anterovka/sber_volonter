package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Alias duplicate detected
 */
public class DuplicateAliasFoundException extends FeatherException {

    /**
     * @param alias   Alias
     * @param context Контекст
     */
    DuplicateAliasFoundException(String alias, String context) {
        super("A duplicate alias has been detected", param("Alias", alias), param("Context", context));
    }
}
