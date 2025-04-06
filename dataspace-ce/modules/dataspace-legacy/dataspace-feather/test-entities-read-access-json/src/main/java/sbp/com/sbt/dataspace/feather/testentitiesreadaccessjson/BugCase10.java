package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 10
 */
class BugCase10 extends TestCase {

    @Override
    void createEntities() {
        createEntity(Service.TYPE0, "service1", propBuilder()
            .add(Service.INITIATOR, propBuilder()
                .add(Person.FIRST_NAME, "Ivan")));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
