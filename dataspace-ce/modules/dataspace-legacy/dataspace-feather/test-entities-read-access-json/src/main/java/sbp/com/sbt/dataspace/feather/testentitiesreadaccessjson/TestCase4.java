package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 4<ul>
 * <li>Testing the feature to see if it is a broken link</li>
 * <li>Testing the feature was not incorrectly transformed</li>
 * </ul>
 */
class TestCase4 extends TestCase {

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());

        String operation1Id = createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service1Id));

        String action1Id = createEntity(Action.TYPE0, "action1", propBuilder()
                .add(Action.OPERATION, operation1Id));

        updateEntity(Service.TYPE0, service1Id, propBuilder()
                .add(Service.START_ACTION, action1Id));

        String productLimited1Id = createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
                .add(Product.RELATED_PRODUCT, TestHelper.NONEXISTENT_ID)
                .add(Product.SERVICES, Arrays.asList(service1Id, TestHelper.NONEXISTENT_ID)));

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.CREATED_ENTITY, productLimited1Id));

        properties.put(TestHelper.NONEXISTENT_ID, TestHelper.NONEXISTENT_ID);
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Reading", "read"));
    }
}
