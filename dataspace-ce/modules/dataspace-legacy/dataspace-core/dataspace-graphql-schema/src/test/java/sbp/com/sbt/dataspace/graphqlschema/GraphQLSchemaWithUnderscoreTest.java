package sbp.com.sbt.dataspace.graphqlschema;

import graphql.GraphQL;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing the GraphQL schema on H2 (Settings 1) with underscore (for id and aggregate version)")
@SpringJUnitConfig(GraphQLSchemaTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx", "withUnderscore"})
public class GraphQLSchemaWithUnderscoreTest extends sbp.com.sbt.dataspace.test.graphqlschema.GraphQLSchemaWithUnderscoreTest {

    @Autowired
    GraphQLDataFetcherHelper graphQLDataFetcherHelper;

    @Autowired
    GraphQL graphQL;

    @DisplayName("Test helper for GraphQL data loader")
    @Test
    public void graphQLDataFetcherHelperTest() {
        assertEquals(GraphQLSchemaHelper.ID_WITH_UNDERSCORE_FIELD_NAME, graphQLDataFetcherHelper.getIdFieldName());
        assertEquals(GraphQLSchemaHelper.AGGREGATE_VERSION_WITH_UNDERSCORE_FIELD_NAME, graphQLDataFetcherHelper.getAggregateVersionFieldName());
    }
}
