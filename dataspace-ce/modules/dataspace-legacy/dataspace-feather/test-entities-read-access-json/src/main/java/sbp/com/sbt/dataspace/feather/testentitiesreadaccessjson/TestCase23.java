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
 * Test case 23<ul>
 * <li>Testing properties selection with security</li>
 * <li>Testing the selection of a unique set of properties with security</li>
 * </ul>
 */
class TestCase23 extends TestCase {

    TestCase23() {
        super(false);
    }

    @Override
    void createEntities() {
        String service7Id = createEntity(Service.TYPE0, "service7", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 4));

        String operation1Id = createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service7Id));

        String action1Id = createEntity(Action.TYPE0, "action1", propBuilder()
                .add(Entity.NAME, "entity9")
                .add(Action.ALGORITHM_CODE, 1)
                .add(Action.OPERATION, operation1Id));

        String action2Id = createEntity(Action.TYPE0, "action2", propBuilder()
                .add(Action.ALGORITHM_CODE, 1)
                .add(Action.OPERATION, operation1Id));

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder()
                .add(Entity.NAME, "entity2")
                .add(Service.MANAGER_PERSONAL_CODE, 1)
                .add(Service.START_ACTION, action1Id));
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder()
                .add(Entity.NAME, "entity3")
                .add(Service.MANAGER_PERSONAL_CODE, 2)
                .add(Service.START_ACTION, action1Id));
        createEntity(Service.TYPE0, "service3", propBuilder()
                .add(Entity.NAME, "entity4")
                .add(Service.MANAGER_PERSONAL_CODE, 1)
                .add(Service.START_ACTION, action1Id));
        createEntity(Service.TYPE0, "service4", propBuilder()
                .add(Entity.NAME, "entity5")
                .add(Service.MANAGER_PERSONAL_CODE, 2)
                .add(Service.START_ACTION, action1Id));
        createEntity(Service.TYPE0, "service5", propBuilder()
                .add(Entity.NAME, "entity6")
                .add(Service.MANAGER_PERSONAL_CODE, 3)
                .add(Service.START_ACTION, action1Id));
        createEntity(Service.TYPE0, "service6", propBuilder()
                .add(Entity.NAME, "entity7")
                .add(Service.START_ACTION, action1Id));
        String service8Id = createEntity(Service.TYPE0, "service8", propBuilder());
        createEntity(Service.TYPE0, "service9", propBuilder()
                .add(Entity.NAME, "entity8")
                .add(Service.MANAGER_PERSONAL_CODE, 5)
                .add(Service.START_ACTION, action2Id));

        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Entity.NAME, "entity1")
                .add(Product.CREATOR_CODE, 1)
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id, service8Id)));

        createEntity(Product.TYPE0, "product2", propBuilder());
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"),
            testData("Unique property set search", "distinctSearch"),
            testData("Unique property set search (2)", "distinctSearch2"),
            testData("Unique property set search (3)", "distinctSearch3"),
            testData("Unique property set search (4)", "distinctSearch4"),
            testData("Unique property set search (5)", "distinctSearch5"));
    }
}
