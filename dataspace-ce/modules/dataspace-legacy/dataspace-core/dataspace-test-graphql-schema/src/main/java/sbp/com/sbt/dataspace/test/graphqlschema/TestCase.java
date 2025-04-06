package sbp.com.sbt.dataspace.test.graphqlschema;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.ExecutionInput;
import graphql.GraphQL;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder;
import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static graphql.ExecutionInput.newExecutionInput;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

/**
 * Test case
 */
abstract class TestCase {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    static final String VARIABLES_TYPE = "variables";
    static final String RESPONSE_TYPE = "response";

    GraphQL graphQL;
    TestHelper testHelper;
    Map<String, String> properties = new LinkedHashMap<>();

    /**
     * Create entities
     */
    abstract void createEntities();

    /**
     * Get test data
     */
    abstract List<TestData> getTestsData();

    /**
     * Create entity
     *
     * @param entityType        Entity type
     * @param code              Type
     * @param propertiesBuilder Property builder
     * @return The entity ID
     */
    String createEntity(String entityType, String code, PropertiesBuilder propertiesBuilder) {
        String result = testHelper.createEntity(entityType, propertiesBuilder
            .add("code", code));
        properties.put(code + "Id", result);
        return result;
    }

    /**
     * Update entity
     *
     * @param entityType        Entity type
     * @param entityId          Entity ID
     * @param propertiesBuilder Property builder
     */
    void updateEntity(String entityType, String entityId, PropertiesBuilder propertiesBuilder) {
        testHelper.updateEntity(entityType, entityId, propertiesBuilder);
    }

    /**
     * Get a string with populated properties
     *
     * @param string String
     */
    String getStringWithProperties(String string) {
        Pointer<String> resultPointer = new Pointer<>(string);
        properties.forEach((propertyName, value) -> resultPointer.object = resultPointer.object.replace("${" + propertyName + "}", value));
        return resultPointer.object;
    }

    /**
     * Get JSON
     *
     * @param name Name
     * @param type Тип
     */
    String getJson(String name, String type) {
        String json = getStringFromResource(TestCase.class, getClass().getSimpleName() + "_" + name + "_" + type + ".json");
        if (json != null) {
            json = getStringWithProperties(json);
        }
        return json;
    }

    /**
     * Get test code
     *
     * @param name Name
     */
    Executable getTestCode(String name) {
        ExecutionInput.Builder executionInputBuilder = newExecutionInput()
            .query(getStringWithProperties(getStringFromResource(TestCase.class, getClass().getSimpleName() + "_" + name + "_request.graphql")));
        String variablesJson = getJson(name, VARIABLES_TYPE);
        if (variablesJson != null) {
            executionInputBuilder.variables(wrap(() -> OBJECT_MAPPER.readValue(variablesJson, Map.class)));
        }
        return () -> assertEquals(OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(OBJECT_MAPPER.readValue(getJson(name, RESPONSE_TYPE), Object.class)), wrap(() -> OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(graphQL.execute(executionInputBuilder.build()).toSpecification())));
    }

    /**
     * Get dynamic tests
     */
    Stream<DynamicTest> getDynamicTests() {
        testHelper.executeInTransaction(this::createEntities);
        return getTestsData().stream().map(testData -> DynamicTest.dynamicTest(testData.getDescription(), getTestCode(testData.getName())));
    }
}
