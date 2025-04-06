package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 22<ul>
 * <li>Testing properties selection</li>
 * <li>Testing the selection of a unique set of properties</li>
 * </ul>
 */
class TestCase22 extends TestCase {

    TestCase22() {
        super(false);
    }

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 1));
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 2));
        createEntity(Service.TYPE0, "service3", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 1));
        createEntity(Service.TYPE0, "service4", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 2));
        createEntity(Service.TYPE0, "service5", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 3));
        createEntity(Service.TYPE0, "service6", propBuilder());

        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Unique property set search", "distinctSearch"),
            testData("Unique property set search (2)", "distinctSearch2"),
            testData("Unique property set search (3)", "distinctSearch3"),
            testData("Unique property set search (4)", "distinctSearch4"));
    }
}
