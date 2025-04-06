package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 15
 */
class BugCase15 extends TestCase {

    @Override
    void createEntities() {
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder());
        createEntity(Product.TYPE0, "product1", propBuilder()
            .add(Product.RELATED_PRODUCT, product2Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
