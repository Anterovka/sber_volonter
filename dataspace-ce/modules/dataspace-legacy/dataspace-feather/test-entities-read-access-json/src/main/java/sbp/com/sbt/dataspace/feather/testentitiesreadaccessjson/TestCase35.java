package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.EntityA;
import sbp.com.sbt.dataspace.feather.testmodel.EntityB;
import sbp.com.sbt.dataspace.feather.testmodel.EntityC;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;


/**
 * Test case 35<ul>
 * <li>Testing join optimization with safety</li>
 * </ul>
 */
public class TestCase35 extends TestCase {

    @Override
    void createEntities() {
        String entityC1Id = createEntity(EntityC.TYPE0, "entityC1", propBuilder());

        String entityB1Id = createEntity(EntityB.TYPE0, "entityB1", propBuilder()
                .add(EntityB.REF_C, entityC1Id));

        createEntity(EntityA.TYPE0, "entityA1", propBuilder()
                .add(EntityA.REF_B, entityB1Id)
                .add(EntityA.REF2_B, "nonExistent"));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
