package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Product;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 24<ul>
 * <li>Testing coalesce with safety</li>
 * </ul>
 */
class TestCase24 extends TestCase {

    TestCase24() {
        super(false);
    }

    @Override
    void createEntities() {
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder());
        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Entity.NAME, "entity1")
                .add(Product.RELATED_PRODUCT, product2Id)
                .add(Product.CREATOR_CODE, 1));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
