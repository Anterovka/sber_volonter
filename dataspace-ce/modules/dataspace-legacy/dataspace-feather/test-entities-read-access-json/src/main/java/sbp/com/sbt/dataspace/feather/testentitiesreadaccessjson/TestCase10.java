package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.RequestPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 10<ul>
 * <li>Testing links with backlink</li>
 * <li>Testing groupings</li>
 * </ul>
 */
class TestCase10 extends TestCase {

    TestCase10() {
        super(false);
    }

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder());

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());

        createEntity(Request.TYPE0, "request2", propBuilder()
                .add(Request.CREATED_ENTITY, service1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Vasya")
                        .add(Person.LAST_NAME, "Vasiliev")));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());

        createEntity(Request.TYPE0, "request3", propBuilder()
                .add(Request.CREATED_ENTITY, product1Id));

        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
                .add(Product.RELATED_PRODUCT, product1Id)
                .add(Product.SERVICES, Arrays.asList(service1Id)));

        createEntity(RequestPlus.TYPE0, "requestPlus1", propBuilder()
                .add(Request.CREATED_ENTITY, productPlus1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Ivan")
                        .add(Person.LAST_NAME, "Ivanov"))
                .add(RequestPlus.DESCRIPTION, "description1"));

        createEntity(Document.TYPE0, "document1", propBuilder()
                .add(Document.PRODUCT, productPlus1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"),
            testData("Reading (3)", "read3"),
            testData("Reading (4)", "read4"),
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"));
    }
}
