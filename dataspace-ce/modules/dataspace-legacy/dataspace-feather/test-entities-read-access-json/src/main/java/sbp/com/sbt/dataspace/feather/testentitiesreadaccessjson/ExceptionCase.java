package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.function.Executable;
import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Case with exception
 */
abstract class ExceptionCase extends TestCase {

    @Override
    Executable getTestSearchEntitiesCode(String name) {
        return () -> assertThrows(FeatherException.class, () -> entitiesReadAccessJson.searchEntities(getJson(name, REQUEST_TYPE), params));
    }
}
