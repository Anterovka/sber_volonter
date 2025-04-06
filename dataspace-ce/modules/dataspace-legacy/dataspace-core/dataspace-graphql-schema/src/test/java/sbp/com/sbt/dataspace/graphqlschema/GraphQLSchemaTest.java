package sbp.com.sbt.dataspace.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing GraphQL schema on H2 (Settings 1)")
@SpringJUnitConfig(GraphQLSchemaTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx"})
public class GraphQLSchemaTest extends sbp.com.sbt.dataspace.test.graphqlschema.GraphQLSchemaTest {

    @Autowired
    GraphQLDataFetcherHelper graphQLDataFetcherHelper;
    @Autowired
    ModelDescription modelDescription;
    @Autowired
    EntitiesReadAccessJson entitiesReadAccessJson;

    @DisplayName("Test helper for GraphQL data loader")
    @Test
    public void graphQLDataFetcherHelperTest() {
        assertEquals(modelDescription, graphQLDataFetcherHelper.getModelDescription());
        assertEquals(entitiesReadAccessJson, graphQLDataFetcherHelper.getEntitiesReadAccessJson());
        assertEquals(GraphQLSchemaHelper.ID_FIELD_NAME, graphQLDataFetcherHelper.getIdFieldName());
        assertEquals(GraphQLSchemaHelper.AGGREGATE_VERSION_FIELD_NAME, graphQLDataFetcherHelper.getAggregateVersionFieldName());
    }
}
