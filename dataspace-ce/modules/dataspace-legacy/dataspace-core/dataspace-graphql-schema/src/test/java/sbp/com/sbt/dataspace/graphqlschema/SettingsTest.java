package sbp.com.sbt.dataspace.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Testing settings")
public class SettingsTest {

    @DisplayName("Тест")
    @Test
    public void test() {
        GraphQLSchemaSettings graphQLSchemaSettings = new GraphQLSchemaSettings();
        assertFalse(graphQLSchemaSettings.isIdWithUnderscore());
        assertFalse(graphQLSchemaSettings.isAggregateVersionWithUnderscore());
        assertFalse(graphQLSchemaSettings.isUseFasterXmlBase64Decoder());
        assertEquals(
            "GraphQL Schema Settings (Whether to use an underscore for id = 'false'; " +
                "Whether to use an underscore for the aggregate version = 'false'; " +
                "Use Base64 decoder from FasterXML library = 'false'; " +
                "Location of fields for computed expressions = 'ON_SEPARATE_TYPE'; " +
                "Generate elements for selection = 'false'; " +
                "Whether to generate the @strExpr directive for variable definitions = 'false'; " +
                "Whether to generate the @strExpr directive for fields = 'true'; " +
                "Generate field strExpr = 'false')",
            graphQLSchemaSettings.toString()
        );
    }
}
