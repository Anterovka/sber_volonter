package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The settings for the specification "Condition" must be declared last
 */
public class CondSpecificationSettingShouldBeLastException extends FeatherException {

    /**
     * @param context Контекст
     */
    CondSpecificationSettingShouldBeLastException(String context) {
        super("Setting up the specification 'Condition' should be declared last", param("Context", context));
    }
}
