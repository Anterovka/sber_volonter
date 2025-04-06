package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Group2;
import sbp.com.sbt.dataspace.feather.testmodel.Group3;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug-case 9
 */
class BugCase9 extends TestCase {

    @Override
    void createEntities() {
        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());

        String testEntity1Id = createEntity(TestEntity.TYPE0, "testEntity1", propBuilder());
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
            .add(TestEntity.G2, propBuilder()
                .add(Group2.R1, testEntity1Id))
            .add(TestEntity.G3, propBuilder()
                .add(Group3.R1, product1Id)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
