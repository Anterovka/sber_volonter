package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The recursion depth for security reasons has exceeded the maximum
 */
public class SecurityRecursionDepthExceededMaximumException extends FeatherException {

    /**
     * @param maxSecurityRecursionDepth The maximum recursion depth for security
     */
    SecurityRecursionDepthExceededMaximumException(int maxSecurityRecursionDepth) {
        super("The recursion depth for security has exceeded the maximum", param("Maximum recursion depth for security", maxSecurityRecursionDepth));
    }
}
