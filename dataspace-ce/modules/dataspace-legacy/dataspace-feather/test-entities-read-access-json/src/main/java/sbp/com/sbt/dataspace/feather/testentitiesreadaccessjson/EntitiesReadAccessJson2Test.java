package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Testing access to entities for reading through JSON (2)
 */
public abstract class EntitiesReadAccessJson2Test extends CommonEntitiesReadAccessJsonTest {

    @DisplayName("Case with exception 3")
    @TestFactory
    public Stream<DynamicTest> exceptionCase3() {
        return getDynamicTests(ExceptionCase3::new);
    }

    @DisplayName("Case with exception 4")
    @TestFactory
    public Stream<DynamicTest> exceptionCase4() {
        return getDynamicTests(ExceptionCase4::new);
    }
}
