package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Action;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug-case 7
 */
class BugCase7 extends TestCase {

    @Override
    void createEntities() {
        String action1Id = createEntity(Action.TYPE0, "action1", propBuilder()
            .add(Entity.NAME, "entity1")
            .add(Action.ALGORITHM_CODE, 1));

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder()
            .add(Entity.NAME, "entity2")
            .add(Service.START_ACTION, action1Id));

        createEntity(Operation.TYPE0, "operation1", propBuilder()
            .add(Entity.NAME, "entity3")
            .add(Operation.SERVICE, service1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
