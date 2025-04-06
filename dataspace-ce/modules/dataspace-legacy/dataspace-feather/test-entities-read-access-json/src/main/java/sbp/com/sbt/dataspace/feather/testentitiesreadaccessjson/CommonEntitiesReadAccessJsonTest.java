package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * General testing of access to entities for reading through JSON
 */
abstract class CommonEntitiesReadAccessJsonTest {

    @Autowired
    EntitiesReadAccessJson entitiesReadAccessJson;
    @Autowired
    TestHelper testHelper;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Get dynamic tests
     *
     * @param testCaseInitializer Test case initializer
     */
    Stream<DynamicTest> getDynamicTests(Supplier<TestCase> testCaseInitializer) {
        TestCase testCase = testCaseInitializer.get();
        testCase.testHelper = testHelper;
        testCase.entitiesReadAccessJson = entitiesReadAccessJson;
        testCase.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        return testCase.getDynamicTests();
    }

    @DisplayName("Context check")
    @Test
    public final void contextCheck() {
// Никаких действий не требуется
    }
}
