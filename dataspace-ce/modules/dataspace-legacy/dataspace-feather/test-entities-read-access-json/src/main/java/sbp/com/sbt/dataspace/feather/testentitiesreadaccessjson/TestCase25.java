package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 25<ul>
 * <li>Testing groupings</li>
 * </ul>
 */
class TestCase25 extends TestCase {

    TestCase25() {
        super(false);
    }

    @Override
    void createEntities() {
        String product4Id = createEntity(Product.TYPE0, "product4", propBuilder()
                .add(Product.CREATOR_CODE, 1));
        String product5Id = createEntity(Product.TYPE0, "product5", propBuilder()
                .add(Product.CREATOR_CODE, 1));
        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.RELATED_PRODUCT, product4Id)
                .add(Product.CREATOR_CODE, 1));
        createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.RELATED_PRODUCT, product4Id)
                .add(Product.CREATOR_CODE, 3));
        createEntity(Product.TYPE0, "product3", propBuilder()
                .add(Product.RELATED_PRODUCT, product5Id)
                .add(Product.CREATOR_CODE, 4));

        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P14, LocalDate.of(2021, 11, 16)));
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
                .add(TestEntity.P14, LocalDate.of(2021, 11, 17)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"));
    }
}
