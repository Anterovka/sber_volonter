package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 11
 */
class BugCase11 extends TestCase {

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
            .add(TestEntity.P2, 1)
            .add(TestEntity.P3, 1)
            .add(TestEntity.P4, 1)
            .add(TestEntity.P5, 1)
            .add(TestEntity.P6, 1)
            .add(TestEntity.P10, 1)
            .add(TestEntity.P12, 1));
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
            .add(TestEntity.P2, 2)
            .add(TestEntity.P3, 2)
            .add(TestEntity.P4, 2)
            .add(TestEntity.P5, 2)
            .add(TestEntity.P6, 2)
            .add(TestEntity.P10, 2)
            .add(TestEntity.P12, 2));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
