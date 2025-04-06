package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.RequestPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 20<ul>
 * <li>Testing properties under aliases</li>
 * </ul>
 */
class TestCase20 extends TestCase {

    TestCase20() {
        super(false);
    }

    @Override
    void createEntities() {
        String productLimited1Id = createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
                .add(Product.CREATOR_CODE, 2)
                .add(ProductLimited.LIMITED_OFFER, "limitedOffer1"));

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder());

        createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service2Id));
        createEntity(Operation.TYPE0, "operation2", propBuilder()
                .add(Operation.SERVICE, service2Id));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.ALIASES, Arrays.asList("alias1", "alias2"))
                .add(Product.RELATED_PRODUCT, productLimited1Id)
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id)));

        String document1Id = createEntity(Document.TYPE0, "document1", propBuilder());

        createEntity(RequestPlus.TYPE0, "requestPlus1", propBuilder()
                .add(Request.CREATED_ENTITY, product1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.LAST_NAME, "Ivanov")
                        .add(Person.DOCUMENT, document1Id))
                .add(RequestPlus.DESCRIPTION, "description1"));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"));
    }
}
