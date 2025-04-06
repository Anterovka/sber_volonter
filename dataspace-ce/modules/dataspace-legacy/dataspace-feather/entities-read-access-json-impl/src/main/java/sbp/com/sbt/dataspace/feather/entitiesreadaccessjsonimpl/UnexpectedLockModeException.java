package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import java.util.Set;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Unexpected lock mode
 */
public class UnexpectedLockModeException extends FeatherException {

    /**
     * @param expectedLockModes Expected lock modes
     * @param lockModeString    Lock mode
     */
    UnexpectedLockModeException(Set<String> expectedLockModes, String lockModeString) {
        super("Unexpected lock mode", param("Expected lock modes", expectedLockModes), param("Lock mode", lockModeString));
    }
}
