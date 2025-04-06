package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Product;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Case with exception 3<ul>
 * <li>The number of read records exceeded the limit</li>
 * <li>The table for entity description was not found</li>
 * <li>Parameter description not found</li>
 * </ul>
 */
class ExceptionCase3 extends ExceptionCase {

    @Override
    void createEntities() {
        createEntity(Product.TYPE0, "product1", propBuilder());
        createEntity(Product.TYPE0, "product2", propBuilder());

        params.put("param1", "123");
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("The number of read records exceeded the limit", "readRecordsCountExceededLimit"),
            testData("The table for entity description was not found", "tableNotFound"),
            testData("Parameter description not found", "paramDescriptionNotFound"));
    }
}
