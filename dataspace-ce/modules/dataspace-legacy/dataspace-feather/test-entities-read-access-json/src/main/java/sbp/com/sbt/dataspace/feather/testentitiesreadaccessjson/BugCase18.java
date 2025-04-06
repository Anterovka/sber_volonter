package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 18
 */
public class BugCase18 extends TestCase {

    BugCase18() {
        super(false);
    }

    @Override
    void createEntities() {
        String entity1Id = createEntity(Entity.TYPE0, "entity1", propBuilder());

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
            .add(Parameter.ENTITY, entity1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
