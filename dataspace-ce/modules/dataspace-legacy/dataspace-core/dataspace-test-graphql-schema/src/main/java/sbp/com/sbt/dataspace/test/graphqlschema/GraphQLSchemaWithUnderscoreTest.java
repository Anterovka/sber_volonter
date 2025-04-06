package sbp.com.sbt.dataspace.test.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Testing the GraphQL schema with underscore (for id and aggregate version)
 */
public abstract class GraphQLSchemaWithUnderscoreTest extends CommonGraphQLSchemaTest {

    @DisplayName("Test Case 4")
    @TestFactory
    public Stream<DynamicTest> testCase4() {
        return getDynamicTests(TestCase4::new);
    }
}
