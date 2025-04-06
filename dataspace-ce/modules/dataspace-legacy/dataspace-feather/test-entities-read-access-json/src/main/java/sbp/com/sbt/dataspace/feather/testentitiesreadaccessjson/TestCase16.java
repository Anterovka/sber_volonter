package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameter;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.RequestPlus;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 16<ul>
 * <li>Security testing</li>
 * </ul>
 */
class TestCase16 extends TestCase {

    TestCase16() {
        super(false);
    }

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.CREATOR_CODE, 1));
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Entity.NAME, "entity4")
                .add(Product.RELATED_PRODUCT, product1Id));
        String product3Id = createEntity(Product.TYPE0, "product3", propBuilder()
                .add(Entity.NAME, "entity5")
                .add(Product.CREATOR_CODE, 1)
                .add(Product.RELATED_PRODUCT, product2Id));
        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
                .add(Entity.NAME, "entity1")
                .add(Product.CREATOR_CODE, 1)
                .add(Product.RELATED_PRODUCT, product1Id)
                .add(ProductPlus.AFFECTED_PRODUCTS, Arrays.asList(product1Id, product2Id, product3Id)));
        String productLimited1Id = createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
                .add(Entity.NAME, "entity2")
                .add(Product.CREATOR_CODE, 1)
                .add(ProductLimited.LIMITED_OFFER, "limitedOffer1")
                .add(Product.RELATED_PRODUCT, productPlus1Id));
        createEntity(ProductLimited.TYPE0, "productLimited2", propBuilder()
                .add(Entity.NAME, "entity3")
                .add(Product.CREATOR_CODE, 1));
        createEntity(ProductLimited.TYPE0, "productLimited3", propBuilder()
                .add(Entity.NAME, "entity7")
                .add(Product.CREATOR_CODE, 1)
                .add(Product.RELATED_PRODUCT, product1Id));

        String actionParameter1Id = createEntity(ActionParameter.TYPE0, "actionParameter1", propBuilder()
                .add(Entity.NAME, "entity6")
                .add(Parameter.ENTITY, product2Id));

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.CREATED_ENTITY, product2Id));
        createEntity(Request.TYPE0, "request2", propBuilder()
                .add(Request.CREATED_ENTITY, product1Id));

        createEntity(RequestPlus.TYPE0, "requestPlus1", propBuilder()
                .add(Request.CREATED_ENTITY, actionParameter1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.LAST_NAME, "Ivanov")));
        createEntity(RequestPlus.TYPE0, "requestPlus2", propBuilder()
                .add(Request.CREATED_ENTITY, productLimited1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.LAST_NAME, "Petrov")));

        String document1Id = createEntity(Document.TYPE0, "document1", propBuilder()
                .add(Document.PRODUCT, product2Id));
        String document2Id = createEntity(Document.TYPE0, "document2", propBuilder()
                .add(Document.PRODUCT, product1Id));

        createEntity(Agreement.TYPE0, "agreement1", propBuilder()
                .add(Agreement.DOCUMENT, document1Id));
        createEntity(Agreement.TYPE0, "agreement2", propBuilder()
                .add(Agreement.DOCUMENT, document2Id));

        createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Entity.NAME, "entity7")
                .add(Operation.SERVICE, "nonExistent"));

        updateEntity(Product.TYPE0, product2Id, propBuilder()
                .add(Product.MAIN_DOCUMENT, document1Id));
        updateEntity(Product.TYPE0, product1Id, propBuilder()
                .add(Product.MAIN_DOCUMENT, document2Id));
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
            testData("Search (10)", "search10"));
    }
}
