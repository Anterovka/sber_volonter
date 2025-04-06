package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Testing access to entities for reading through JSON with security (2)
 */
public abstract class EntitiesReadAccessJsonWithSecurity2Test extends CommonEntitiesReadAccessJsonWithSecurityTest {

    @Override
    Map<String, String> getEntityRestrictions() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(Entity.TYPE0, "it.parameters.$exists");
        return result;
    }

    @DisplayName("Case with exception 2")
    @TestFactory
    public Stream<DynamicTest> exceptionCase2() {
        return getDynamicTests(ExceptionCase2::new);
    }
}
