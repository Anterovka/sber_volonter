package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 29<ul>
 * <li>Obtaining a collection of primitives based on transformation</li>
 * </ul>
 */
class TestCase29 extends TestCase {

    TestCase29() {
        super(false);
    }

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder());

        String operation1Id = createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service1Id));
        String operation2Id = createEntity(Operation.TYPE0, "operation2", propBuilder()
.add(Entity.NAME, "Operation")
                .add(Operation.SERVICE, service1Id));
        String operation3Id = createEntity(Operation.TYPE0, "operation3", propBuilder()
.add(Entity.NAME, "Operation")
                .add(Operation.SERVICE, service2Id));
        String operation4Id = createEntity(Operation.TYPE0, "operation4", propBuilder()
.add(Entity.NAME, "Operation")
                .add(Operation.SERVICE, service2Id));

        createEntity(Action.TYPE0, "action1", propBuilder()
                .add(Action.OPERATION, operation1Id));
        createEntity(Action.TYPE0, "action2", propBuilder()
                .add(Action.OPERATION, operation2Id));
        createEntity(Action.TYPE0, "action3", propBuilder()
                .add(Action.OPERATION, operation3Id));
        createEntity(Action.TYPE0, "action4", propBuilder()
                .add(Action.OPERATION, operation3Id));
        createEntity(Action.TYPE0, "action5", propBuilder()
                .add(Action.OPERATION, operation4Id));

        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.ALIASES, Arrays.asList("alias1", "alias2")));
        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.RELATED_PRODUCT, product2Id)
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
