package sbp.com.sbt.dataspace.test.graphqlschema;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Request;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 2<ul>
 * <li>Error testing, whether it is a broken link</li>
 * <li>Testing @skip and @include</li>
 * </ul>
 */
class TestCase2 extends TestCase {

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
            .add(Product.RELATED_PRODUCT, TestHelper.NONEXISTENT_ID));

        createEntity(Request.TYPE0, "request1", propBuilder()
            .add(Request.CREATED_ENTITY, product1Id)
            .add(Request.INITIATOR, propBuilder()
                .add(Person.FIRST_NAME, "Vasya")
                .add(Person.LAST_NAME, "Vasiliev")));
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
            testData("Search (8)", "search8"));
    }
}
