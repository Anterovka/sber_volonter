package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 3<ul>
 * <li>Testing the feature is whether the entity without a parent is a broken link.</li>
 * </ul>
 */
class TestCase3 extends TestCase {

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.R1, TestHelper.NONEXISTENT_ID)
                .add(TestEntity.RC1, Arrays.asList(TestHelper.NONEXISTENT_ID)));

        properties.put(TestHelper.NONEXISTENT_ID, TestHelper.NONEXISTENT_ID);
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Reading", "read"));
    }
}
