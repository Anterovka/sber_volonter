package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test Case 5
 */
class TestCase5 extends TestCase {

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.RATES, Arrays.asList(10.5, 2.5, 11.0)));

        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
                .add(Product.RATES, Arrays.asList(30.5, 4.5))
                .add(ProductPlus.AFFECTED_PRODUCTS, Arrays.asList(product1Id)));
        String productPlus2Id = createEntity(ProductPlus.TYPE0, "productPlus2", propBuilder());
        String productPlus3Id = createEntity(ProductPlus.TYPE0, "productPlus3", propBuilder()
                .add(Product.RELATED_PRODUCT, productPlus2Id));

        createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
                .add(Product.ALIASES, Arrays.asList("alias1", "alias2"))
                .add(Product.RELATED_PRODUCT, productPlus3Id)
                .add(ProductPlus.AFFECTED_PRODUCTS, Arrays.asList(productPlus1Id, product1Id)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"));
    }
}
