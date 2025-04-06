package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.entitiesreadaccessjson.EntitiesReadAccessJson;
import sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder;
import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

/**
 * Test case
 */
abstract class TestCase {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static final ObjectMapper OBJECT_MAPPER_WITH_TEST_COLLECTIONS;
    static final String REQUEST_TYPE = "request";
    static final String RESPONSE_TYPE = "response";
    static final String STREAM_RESPONSE_TYPE = "streamResponse";
    static final String STREAM_ENTITIES_TEST_DATA_TYPE = "streamEntities";

    ObjectMapper objectMapper;
    TestHelper testHelper;
    EntitiesReadAccessJson entitiesReadAccessJson;
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    Map<String, String> properties = new LinkedHashMap<>();
    Map<String, Object> params = new LinkedHashMap<>();

    /**
     * @param useTestCollections Whether to use test collections
     */
    TestCase(boolean useTestCollections) {
        objectMapper = useTestCollections ? OBJECT_MAPPER_WITH_TEST_COLLECTIONS : OBJECT_MAPPER;
    }

    TestCase() {
        this(true);
    }

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
     * @return The entity's Id
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
     * Get JSON
     *
     * @param name Name
     * @param type Type
     */
    String getJson(String name, String type) {
        Pointer<String> resultPointer = new Pointer<>(getStringFromResource(TestCase.class, getClass().getSimpleName() + "_" + name + "_" + type + ".json"));
        properties.forEach((propertyName, value) -> resultPointer.object = resultPointer.object.replace("${" + propertyName + "}", value));
        return resultPointer.object;
    }

    /**
     * Get readable JSON
     *
     * @param json JSON
     */
    String getPrettyJson(String json) {
        return wrap(() -> objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readValue(json, Object.class)));
    }

    /**
     * Get search entity code
     *
     * @param name Name
     */
    Executable getTestSearchEntitiesCode(String name) {
        return () -> assertEquals(getPrettyJson(getJson(name, RESPONSE_TYPE)), getPrettyJson(entitiesReadAccessJson.searchEntities(getJson(name, REQUEST_TYPE), params)));
    }

    /**
     * Get the test code for streaming entity reading
     *
     * @param name Name
     */
    Executable getTestStreamEntitiesCode(String name) {
        return () -> {
            Pointer<Integer> indexPointer = new Pointer<>(0);
            entitiesReadAccessJson.streamEntities(wrap(() -> objectMapper.readTree(getJson(name, REQUEST_TYPE)))).subscribe(jsonNode -> assertEquals(getPrettyJson(getJson(name, STREAM_RESPONSE_TYPE + '_' + indexPointer.object++)), jsonNode.toPrettyString()));
        };
    }

    /**
     * Get dynamic test
     *
     * @param testData Test data
     */
    DynamicTest getDynamicTest(TestData testData) {
        Executable testCode;
        if (STREAM_ENTITIES_TEST_DATA_TYPE.equals(testData.getType())) {
            testCode = getTestStreamEntitiesCode(testData.getName());
        } else {
            testCode = getTestSearchEntitiesCode(testData.getName());
        }
        return DynamicTest.dynamicTest(testData.getDescription(), testCode);
    }

    /**
     * Get dynamic tests
     */
    Stream<DynamicTest> getDynamicTests() {
        testHelper.executeInTransaction(this::createEntities);
        return getTestsData().stream().map(this::getDynamicTest);
    }

    static {
        OBJECT_MAPPER_WITH_TEST_COLLECTIONS = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addAbstractTypeMapping(Map.class, TestMap.class);
        module.addAbstractTypeMapping(List.class, TestList.class);
        OBJECT_MAPPER_WITH_TEST_COLLECTIONS.registerModule(module);
    }
}
