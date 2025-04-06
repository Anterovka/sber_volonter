package sbp.com.sbt.dataspace.test.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Testing the GraphQL schema
 */
public abstract class GraphQLSchemaTest extends CommonGraphQLSchemaTest {

@DisplayName("Case with errors 1")
    @TestFactory
    public Stream<DynamicTest> errorCase1() {
        return getDynamicTests(ErrorCase1::new);
    }

@DisplayName("Test Case 1")
    @TestFactory
    public Stream<DynamicTest> testCase1() {
        return getDynamicTests(TestCase1::new);
    }

@DisplayName("Test Case 2")
    @TestFactory
    public Stream<DynamicTest> testCase2() {
        return getDynamicTests(TestCase2::new);
    }

@DisplayName("Test Case 3")
    @TestFactory
    public Stream<DynamicTest> testCase3() {
        return getDynamicTests(TestCase3::new);
    }

@DisplayName("Test Case 5")
    @TestFactory
    public Stream<DynamicTest> testCase5() {
        return getDynamicTests(TestCase5::new);
    }

@DisplayName("Bug-case 1")
    @TestFactory
    public Stream<DynamicTest> bugCase1() {
        return getDynamicTests(BugCase1::new);
    }
}
