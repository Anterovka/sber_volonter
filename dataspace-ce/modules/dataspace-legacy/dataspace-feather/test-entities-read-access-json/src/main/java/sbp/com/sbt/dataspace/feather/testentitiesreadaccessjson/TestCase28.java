package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 28<ul>
 * <li>Streaming reading of entities</li>
 * </ul>
 */
class TestCase28 extends TestCase {

    TestCase28() {
        super(false);
    }

    @Override
    void createEntities() {
        createEntity(Service.TYPE0, "service1", propBuilder());
        createEntity(Service.TYPE0, "service2", propBuilder());
        createEntity(Product.TYPE0, "product1", propBuilder());
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Streaming reading", "stream", STREAM_ENTITIES_TEST_DATA_TYPE),
            testData("Streaming Read (2)", "stream2", STREAM_ENTITIES_TEST_DATA_TYPE),
            testData("Streaming Read (3)", "stream3", STREAM_ENTITIES_TEST_DATA_TYPE));
    }
}
