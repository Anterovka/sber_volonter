package sbp.com.sbt.dataspace.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@DisplayName("Testing the GraphQL schema on H2 (Settings 1) with fields for computed expressions on each type")
@SpringJUnitConfig(GraphQLSchemaTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx", "withCalcExprFieldsOnEachType"})
public class GraphQLSchemaWithCalcExprFieldsOnEachTypeTest extends sbp.com.sbt.dataspace.test.graphqlschema.GraphQLSchemaWithCalcExprFieldsOnEachTypeTest {
}
