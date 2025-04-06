package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Assistant
 */
final class Helper {

    private Helper() {
    }

    /**
     * Check
     *
     * @param map Mapping
     * @param checks  Checks
     */
    static void check(Map<String, ?> map, Map<String, ? extends AbstractCheck> checks) {
        assertTrue(checks.size() <= map.size(), map.keySet()::toString);
        checks.forEach((key, check) -> assertDoesNotThrow(() -> check.check(), key));
    }
}
