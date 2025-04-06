package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.OperationSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 2
 */
class TestCase2 extends TestCase {

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 1));
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 2));
        String service3Id = createEntity(Service.TYPE0, "service3", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 3));
        String service4Id = createEntity(Service.TYPE0, "service4", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 4));
        String service5Id = createEntity(Service.TYPE0, "service5", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 5));

        String operation1Id = createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service1Id));
        createEntity(Operation.TYPE0, "operation2", propBuilder()
                .add(Operation.SERVICE, service1Id));
        String operation3Id = createEntity(Operation.TYPE0, "operation3", propBuilder()
                .add(Operation.SERVICE, service2Id));
        String operation4Id = createEntity(Operation.TYPE0, "operation4", propBuilder()
                .add(Operation.SERVICE, service3Id));
        createEntity(Operation.TYPE0, "operation5", propBuilder()
                .add(Operation.SERVICE, service5Id));
        createEntity(Operation.TYPE0, "operation6", propBuilder()
                .add(Operation.SERVICE, service5Id));

        String action1Id = createEntity(Action.TYPE0, "action1", propBuilder()
                .add(Action.ALGORITHM_CODE, 1)
                .add(Action.OPERATION, operation1Id));
        String action2Id = createEntity(Action.TYPE0, "action2", propBuilder()
                .add(Action.ALGORITHM_CODE, 2)
                .add(Action.OPERATION, operation3Id));
        String action3Id = createEntity(Action.TYPE0, "action3", propBuilder()
                .add(Action.ALGORITHM_CODE, 3)
                .add(Action.OPERATION, operation4Id));

        updateEntity(Service.TYPE0, service1Id, propBuilder()
                .add(Service.START_ACTION, action1Id));
        updateEntity(Service.TYPE0, service2Id, propBuilder()
                .add(Service.START_ACTION, action2Id));
        updateEntity(Service.TYPE0, service3Id, propBuilder()
                .add(Service.START_ACTION, action3Id));

        String operationSpecial1Id = createEntity(OperationSpecial.TYPE0, "operationSpecial1", propBuilder()
                .add(OperationSpecial.SPECIAL_OFFER, "specialOffer1")
                .add(Operation.SERVICE, service1Id));
        createEntity(OperationSpecial.TYPE0, "operationSpecial2", propBuilder()
                .add(OperationSpecial.SPECIAL_OFFER, "specialOffer2")
                .add(Operation.SERVICE, service5Id));

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(Parameter.ENTITY, operationSpecial1Id));

        String productPlus3Id = createEntity(ProductPlus.TYPE0, "productPlus3", propBuilder()
                .add(Product.SERVICES, Arrays.asList(service5Id)));
        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
                .add(Product.CREATOR_CODE, 3)
                .add(Product.RELATED_PRODUCT, productPlus3Id));
        String productPlus2Id = createEntity(ProductPlus.TYPE0, "productPlus2", propBuilder()
                .add(Product.CREATOR_CODE, 2)
                .add(Product.ALIASES, Arrays.asList("alias4", "alias5", "alias6"))
                .add(Product.RELATED_PRODUCT, productPlus1Id)
                .add(Product.SERVICES, Arrays.asList(service4Id)));

        createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
                .add(Product.CREATOR_CODE, 1)
                .add(ProductLimited.LIMITED_OFFER, "limitedOffer1")
                .add(Product.ALIASES, Arrays.asList("alias1", "alias2", "alias3"))
                .add(Product.RELATED_PRODUCT, productPlus2Id)
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id, service3Id)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Search", "search"),
            testData("Search with limit restriction", "searchWithLimit"),
            testData("Search with limit and count of elements", "searchWithLimitAndCount"),
            testData("Counting the number of elements", "count"));
    }
}
