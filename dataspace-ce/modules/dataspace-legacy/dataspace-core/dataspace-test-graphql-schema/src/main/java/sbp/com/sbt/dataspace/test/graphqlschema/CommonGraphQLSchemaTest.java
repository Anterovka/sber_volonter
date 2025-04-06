package sbp.com.sbt.dataspace.test.graphqlschema;

import graphql.GraphQL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * General testing of the GraphQL schema
 */
abstract class CommonGraphQLSchemaTest {

    @Autowired
    GraphQL graphQL;
    @Autowired
    TestHelper testHelper;

    /**
     * Get dynamic tests
     *
     * @param testCaseInitializer Test case initializer
     */
    Stream<DynamicTest> getDynamicTests(Supplier<TestCase> testCaseInitializer) {
        TestCase testCase = testCaseInitializer.get();
        testCase.graphQL = graphQL;
        testCase.testHelper = testHelper;
        return testCase.getDynamicTests();
    }

    @DisplayName("Context check")
    @Test
    public final void contextCheck() {
// Никаких действий не требуется
    }
}
