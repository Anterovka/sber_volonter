package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Permission;
import sbp.com.sbt.dataspace.feather.testmodel.Product;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 9<ul>
 * <li>Testing the inheritance strategy of SINGLE_TABLE</li>
 * </ul>
 */
class TestCase9 extends TestCase {

    TestCase9() {
        super(false);
    }

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());

        createEntity(Document.TYPE0, "document1", propBuilder()
                .add(Document.PRODUCT, product1Id));

        createEntity(Permission.TYPE0, "permission1", propBuilder()
                .add(Permission.NUMBER, 1)
                .add(Document.PRODUCT, product1Id));

        String agreement1Id = createEntity(Agreement.TYPE0, "agreement1", propBuilder()
                .add(Agreement.PARTICIPANT, "participant1")
                .add(Document.PRODUCT, product1Id));
        createEntity(Agreement.TYPE0, "agreement2", propBuilder()
                .add(Agreement.PARTICIPANT, "participant2")
                .add(Agreement.DOCUMENT, agreement1Id));
        createEntity(Agreement.TYPE0, "agreement3", propBuilder()
                .add(Agreement.PARTICIPANT, "participant3")
                .add(Agreement.DOCUMENT, agreement1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
testData("Reading", "read"),
testData("Search", "search"),
testData("Search with element count", "searchWithCount"),
testData("Search with limit", "searchWithLimit"),
testData("Search with limit and count of elements", "searchWithLimitAndCount"),
testData("Search with offset", "searchWithOffset"),
testData("Search with offset and count of elements", "searchWithOffsetAndCount"));
    }
}
