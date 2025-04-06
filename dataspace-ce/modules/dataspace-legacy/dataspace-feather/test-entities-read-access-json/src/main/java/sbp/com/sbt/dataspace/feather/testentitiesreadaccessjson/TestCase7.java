package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 7<ul>
 * <li>Sorting Testing</li>
 * </ul>
 */
class TestCase7 extends TestCase {

    TestCase7() {
        super(false);
    }

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder()
                .add(Entity.NAME, "Service 1"));
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder()
                .add(Entity.NAME, "Service 2"));
        String service3Id = createEntity(Service.TYPE0, "service3", propBuilder());

        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id, service3Id)));
        createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id)));
        createEntity(Product.TYPE0, "product3", propBuilder()
                .add(Product.SERVICES, Arrays.asList(service1Id)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"),
            testData("Search (6)", "search6"),
            testData("Search (7)", "search7"),
            testData("Search (8)", "search8"),
            testData("Search (9)", "search9"),
            testData("Search (10)", "search10")
        );
    }
}
