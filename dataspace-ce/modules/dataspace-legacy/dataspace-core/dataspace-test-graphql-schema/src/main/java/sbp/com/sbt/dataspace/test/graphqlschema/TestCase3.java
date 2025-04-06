package sbp.com.sbt.dataspace.test.graphqlschema;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Attribute;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 3<ul>
 * <li>Testing query merging</li>
 * </ul>
 */
class TestCase3 extends TestCase {

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P1, "string1"));

        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.CREATOR_CODE, 1));
        createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.CREATOR_CODE, 2)
                .add(Entity.ATTRIBUTES, Arrays.asList(Attribute.TOP_PRIORITY)));

        createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
                .add(Product.CREATOR_CODE, 3)
                .add(ProductLimited.LIMITED_OFFER, "limitedOffer1")
                .add(Entity.ATTRIBUTES, Arrays.asList(Attribute.FORBIDDEN)));

        createEntity(Service.TYPE0, "service1", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 1));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2")
        );
    }
}
