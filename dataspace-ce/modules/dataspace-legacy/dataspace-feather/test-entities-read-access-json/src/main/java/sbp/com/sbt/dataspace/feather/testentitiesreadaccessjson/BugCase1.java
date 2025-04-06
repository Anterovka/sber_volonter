package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 1
 */
class BugCase1 extends TestCase {

    @Override
    void createEntities() {
// Никаких действий не требуется
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Reading", "read"));
    }
}
