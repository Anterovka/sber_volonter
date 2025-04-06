package sbp.com.sbt.dataspace.feather.securitydriver.examples;

import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

/**
 * Obtaining restrictions
 */
class GetRestrictionsExample {

    /**
     * Execute
     *
     * @param securityDriver Security driver
     * @return Ограничения
     */
    Map<String, String> run(SecurityDriver securityDriver) {
        return securityDriver.getRestrictions(new HashSet<>(Arrays.asList("Product", "Service")));
    }
}
