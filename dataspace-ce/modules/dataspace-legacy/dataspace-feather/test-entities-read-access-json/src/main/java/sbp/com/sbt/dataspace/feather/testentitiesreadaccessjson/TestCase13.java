package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.RequestPlus;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 13<ul>
 * <li>Testing version retrieval of the aggregate</li>
 * </ul>
 */
class TestCase13 extends TestCase {

    @Override
    void createEntities() {
        String testEntity1Id = createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestHelper.VERSION_SETTING, 1));
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
                .add(TestHelper.AGGREGATE_SETTING, testEntity1Id));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(TestHelper.VERSION_SETTING, 1));
        createEntity(Product.TYPE0, "product2", propBuilder());

        createEntity(RequestPlus.TYPE0, "requestPlus1", propBuilder()
                .add(TestHelper.AGGREGATE_SETTING, product1Id)
                .add(Request.CREATED_ENTITY, product1Id)
                .add(RequestPlus.DESCRIPTION, "description1"));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"),
            testData("Reading (3)", "read3"),
            testData("Reading (4)", "read4"),
            testData("Reading (5)", "read5"));
    }
}
