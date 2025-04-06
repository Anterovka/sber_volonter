package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Use of non-target implementation
 */
public class UsageOfNotTargetImplementationException extends FeatherException {

    /**
     * @param context Контекст
     */
    UsageOfNotTargetImplementationException(String context) {
        super("Using non-target implementation", context);
    }
}
