package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Request;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 14
 */
class BugCase14 extends TestCase {

    @Override
    void createEntities() {
        String entity1Id = createEntity(Entity.TYPE0, "entity1", propBuilder());

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
            .add(Parameter.ENTITY, entity1Id));
        createEntity(Request.TYPE0, "request1", propBuilder()
            .add(Request.CREATED_ENTITY, entity1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"));
    }
}
