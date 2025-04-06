package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Invalid value for maximum recursion depth for security
 */
public class InvalidMaxSecurityRecursionDepthException extends FeatherException {

    /**
     * @param maxSecurityRecursionDepth The maximum recursion depth for security
     */
    InvalidMaxSecurityRecursionDepthException(int maxSecurityRecursionDepth) {
        super("Invalid value of maximum recursion depth for security", param("Maximum recursion depth for security", maxSecurityRecursionDepth), "Only positive values are allowed");
    }
}
