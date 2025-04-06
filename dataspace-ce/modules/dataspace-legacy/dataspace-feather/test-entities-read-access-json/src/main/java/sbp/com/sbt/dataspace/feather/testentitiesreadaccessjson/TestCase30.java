package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 30<ul>
 * <li>Queries tables</li>
 * </ul>
 */
class TestCase30 extends TestCase {

    TestCase30() {
        super(false);
    }

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());

        String product4Id = createEntity(Product.TYPE0, "product4", propBuilder());
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.ALIASES, Arrays.asList("alias1"))
                .add(Product.RELATED_PRODUCT, product4Id)
                .add(Product.SERVICES, Arrays.asList(service1Id)));
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder());
        String product3Id = createEntity(Product.TYPE0, "product3", propBuilder());

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.CREATED_ENTITY, product1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Ivan")
                        .add(Person.LAST_NAME, "Ivanov")));
        createEntity(Request.TYPE0, "request2", propBuilder()
                .add(Request.CREATED_ENTITY, product2Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Ivan")
                        .add(Person.LAST_NAME, "Ivanov")));
        createEntity(Request.TYPE0, "request3", propBuilder()
                .add(Request.CREATED_ENTITY, product3Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Ivan")
                        .add(Person.LAST_NAME, "Ivanov")));

        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder());
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"),
            testData("Search (7)", "search7"),
            testData("Search (8)", "search8"),
            testData("Search (9)", "search9"),
            testData("Search (10)", "search10"),
            testData("Search (11)", "search11"),
            testData("Search (12)", "search12"),
            testData("Search (13)", "search13"),
            testData("Search (14)", "search14"),
            testData("Search (15)", "search15"),
            testData("Search (16)", "search16"),
            testData("Search (17)", "search17"));
    }
}
