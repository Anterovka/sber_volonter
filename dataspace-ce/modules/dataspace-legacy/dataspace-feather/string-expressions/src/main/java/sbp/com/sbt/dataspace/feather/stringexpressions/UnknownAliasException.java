package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unknown alias
 */
public class UnknownAliasException extends FeatherException {

    /**
     * @param alias   Alias
     * @param context Контекст
     */
    UnknownAliasException(String alias, String context) {
        super("Unknown pseudonym", param("Pseudonym", alias), param("Context", context));
    }
}
