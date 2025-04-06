package sbp.com.sbt.dataspace.test.graphqlschema;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Group1;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
* Bug case 1
 */
class BugCase1 extends TestCase {

    @Override
    void createEntities() {
        String testEntity2Id = createEntity(TestEntity.TYPE0, "testEntity2", propBuilder());
        String testEntity1Id = createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.PS1, Arrays.asList("1", "2", "3"))
                .add(TestEntity.R1, testEntity2Id)
                .add(TestEntity.RC1, Arrays.asList(testEntity2Id))
                .add(TestEntity.G1, propBuilder()
                        .add(Group1.P1, "1")));
        createEntity(TestEntity.TYPE0, "testEntity3", propBuilder()
                .add(TestEntity.R1, testEntity1Id));
    }

    @Override
    List<TestData> getTestsData() {
return Arrays.asList(testData("Search", "search"));
    }
}
