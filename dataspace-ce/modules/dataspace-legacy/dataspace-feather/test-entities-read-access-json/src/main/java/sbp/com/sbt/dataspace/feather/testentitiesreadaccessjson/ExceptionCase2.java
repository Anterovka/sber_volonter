package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Case with exception 2<ul>
 * <li>The recursion depth for security exceeded the maximum</li>
 * </ul>
 */
class ExceptionCase2 extends ExceptionCase {

    @Override
    void createEntities() {
// Никаких действий не требуется
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("The recursion depth for security exceeded the maximum", "securityRecursionDepthExceededMaximum"));
    }
}
