package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Event;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 6<ul>
 * <li>Sorting Testing</li>
 * </ul>
 */
class TestCase6 extends TestCase {

    TestCase6() {
        super(false);
    }

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 1));
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 2));
        String service3Id = createEntity(Service.TYPE0, "service3", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 3));

        createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service1Id));
        createEntity(Operation.TYPE0, "operation2", propBuilder()
                .add(Operation.SERVICE, service1Id));
        String operation3Id = createEntity(Operation.TYPE0, "operation3", propBuilder()
                .add(Operation.SERVICE, service1Id));

        String event1Id = createEntity(Event.TYPE0, "event1", propBuilder()
                .add(Event.AUTHOR, "author1"));
        String event2Id = createEntity(Event.TYPE0, "event2", propBuilder()
                .add(Event.AUTHOR, "author2"));
        String event3Id = createEntity(Event.TYPE0, "event3", propBuilder()
                .add(Event.AUTHOR, "author3"));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.CREATOR_CODE, 1)
                .add(Product.ALIASES, Arrays.asList("alias1", "alias2", "alias3"))
                .add(Product.RATES, Arrays.asList(1.1, 1.2, 1.3))
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id, service3Id))
                .add(Product.EVENTS, Arrays.asList(event1Id, event2Id, event3Id)));
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.CREATOR_CODE, 2)
                .add(Product.RELATED_PRODUCT, product1Id));
        createEntity(Product.TYPE0, "product3", propBuilder()
                .add(Product.CREATOR_CODE, 3)
                .add(Product.RELATED_PRODUCT, product2Id));

        String parameter1Id = createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(Parameter.VALUE, "value1")
                .add(Parameter.ENTITY, product1Id));
        createEntity(Parameter.TYPE0, "parameter2", propBuilder()
                .add(Parameter.VALUE, "value2")
                .add(Parameter.ENTITY, product1Id));
        createEntity(Parameter.TYPE0, "parameter3", propBuilder()
                .add(Parameter.VALUE, "value3")
                .add(Parameter.ENTITY, product1Id));
        createEntity(Parameter.TYPE0, "parameter4", propBuilder()
                .add(Parameter.ENTITY, operation3Id));
        createEntity(Parameter.TYPE0, "parameter5", propBuilder()
                .add(Parameter.ENTITY, operation3Id));
        createEntity(Parameter.TYPE0, "parameter6", propBuilder()
                .add(Parameter.ENTITY, operation3Id));
        createEntity(Parameter.TYPE0, "parameter7", propBuilder()
                .add(Parameter.ENTITY, parameter1Id));
        createEntity(Parameter.TYPE0, "parameter8", propBuilder()
                .add(Parameter.ENTITY, event1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3")
        );
    }
}
