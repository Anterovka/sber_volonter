package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 17
 */
public class BugCase17 extends TestCase {

    BugCase17() {super(false);};

    @Override
    void createEntities() {
        String product4Id = createEntity(Product.TYPE0, "product4", propBuilder()
            .add(Product.CREATOR_CODE, 4));
        String product5Id = createEntity(Product.TYPE0, "product5", propBuilder()
            .add(Product.CREATOR_CODE, 4));
        createEntity(Product.TYPE0, "product1", propBuilder()
            .add(Product.RELATED_PRODUCT, product4Id)
            .add(Product.CREATOR_CODE, 1));
        createEntity(Product.TYPE0, "product2", propBuilder()
            .add(Product.RELATED_PRODUCT, product4Id)
            .add(Product.CREATOR_CODE, 2));
        createEntity(Product.TYPE0, "product3", propBuilder()
            .add(Product.RELATED_PRODUCT, product5Id)
            .add(Product.CREATOR_CODE, 3));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search(2)", "search2"),
            testData("Search(3)", "search3"),
            testData("Search(4)", "search4")/*,
            testData("Search(5)", "search5") TODO Issues with H2 */);
    }
}
