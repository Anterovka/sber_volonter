package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The "Type" specification setting must be declared first
 */
public class TypeSpecificationSettingShouldBeFirstException extends FeatherException {

    /**
     * @param context Контекст
     */
    TypeSpecificationSettingShouldBeFirstException(String context) {
        super("Setting up the specification 'Type' must be declared first", param("Context", context));
    }
}
