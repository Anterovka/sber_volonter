package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.AgreementSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Permission;
import sbp.com.sbt.dataspace.feather.testmodel.Product;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 8<ul>
 * <li>Testing the inheritance strategy of SINGLE_TABLE</li>
 * </ul>
 */
class TestCase8 extends TestCase {

    TestCase8() {
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
        createEntity(Permission.TYPE0, "permission2", propBuilder()
                .add(Document.PRODUCT, product1Id));

        String specialAgreement1Id = createEntity(AgreementSpecial.TYPE0, "agreementSpecial1", propBuilder());

        createEntity(Agreement.TYPE0, "agreement1", propBuilder()
                .add(Agreement.DOCUMENT, specialAgreement1Id)
                .add(Document.PRODUCT, product1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"),
            testData("Reading (3)", "read3"),
            testData("Search", "search"),
            testData("Search with limit and element count", "searchWithLimitAndCount"));
    }
}
