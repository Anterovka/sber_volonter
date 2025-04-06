package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 27<ul>
 * <li>Security condition</li>
 * </ul>
 */
class TestCase27 extends TestCase {

    TestCase27() {
        super(false);
    }

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder());

        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.ALIASES, Arrays.asList("alias1", "alias2"))
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id)));
        createEntity(Product.TYPE0, "product2", propBuilder());
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"));
    }
}
