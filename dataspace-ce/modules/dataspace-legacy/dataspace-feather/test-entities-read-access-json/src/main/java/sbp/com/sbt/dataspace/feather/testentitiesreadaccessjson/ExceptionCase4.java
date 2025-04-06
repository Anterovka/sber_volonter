package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Case with exception 4<ul>
 * <li>The parameter value is not set</li>
 * </ul>
 */
class ExceptionCase4 extends ExceptionCase {

    @Override
    void createEntities() {
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("The parameter value is not set", "paramValueNotSet"));
    }
}
