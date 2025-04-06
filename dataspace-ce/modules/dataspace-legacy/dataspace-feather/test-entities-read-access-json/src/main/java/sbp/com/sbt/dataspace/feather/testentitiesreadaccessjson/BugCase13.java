package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.*;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 13
 */
class BugCase13 extends TestCase {

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());

        createEntity(OperationSpecial.TYPE0, "operationSpecial1", propBuilder()
            .add(OperationSpecial.PRODUCT, product1Id));

        createEntity(OperationLimited.TYPE0, "operationLimited1", propBuilder()
            .add(OperationLimited.PRODUCT, product1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
