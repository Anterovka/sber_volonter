package sbp.com.sbt.dataspace.test.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Testing the GraphQL schema with fields for computed expressions on each type
 */
public abstract class GraphQLSchemaWithCalcExprFieldsOnEachTypeTest extends CommonGraphQLSchemaTest {

    @DisplayName("Test Case 6")
    @TestFactory
    public Stream<DynamicTest> testCase6() {
        return getDynamicTests(TestCase6::new);
    }

    @DisplayName("Bug-case 2")
    @TestFactory
    public Stream<DynamicTest> bugCase2() {
        return getDynamicTests(BugCase2::new);
    }
}
