package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Testing access to entities for reading through JSON (4)
 */
public abstract class EntitiesReadAccessJson4Test extends CommonEntitiesReadAccessJsonTest {

    @DisplayName("Test Case 34")
    @TestFactory
    public Stream<DynamicTest> testCase34() {
        return getDynamicTests(TestCase34::new);
    }
}
