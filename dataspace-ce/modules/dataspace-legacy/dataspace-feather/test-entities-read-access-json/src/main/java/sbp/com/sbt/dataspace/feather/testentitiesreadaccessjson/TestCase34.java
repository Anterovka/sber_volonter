package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.EntityA;
import sbp.com.sbt.dataspace.feather.testmodel.EntityAA;
import sbp.com.sbt.dataspace.feather.testmodel.EntityAAA;
import sbp.com.sbt.dataspace.feather.testmodel.EntityB;
import sbp.com.sbt.dataspace.feather.testmodel.EntityC;
import sbp.com.sbt.dataspace.feather.testmodel.EntityD;
import sbp.com.sbt.dataspace.feather.testmodel.EntityE;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;


/**
 * Test case 34<ul>
 * <li>Testing optimization of joins for links and inheritance</li>
 * </ul>
 */
public class TestCase34 extends TestCase {

    @Override
    void createEntities() {
        String entityE1Id = createEntity(EntityE.TYPE0, "entityE1", propBuilder());

        String entityD1Id = createEntity(EntityD.TYPE0, "entityD1", propBuilder()
                .add(EntityD.REF_E, entityE1Id));

        String entityC1Id = createEntity(EntityC.TYPE0, "entityC1", propBuilder()
                .add(EntityC.REF_D, entityD1Id));

        String entityB1Id = createEntity(EntityB.TYPE0, "entityB1", propBuilder()
                .add(EntityB.REF_C, entityC1Id));

        createEntity(EntityAAA.TYPE0, "entityAAA1", propBuilder()
                .add(EntityA.REF_B, entityB1Id)
                .add(EntityA.REF2_B, entityB1Id)
                .add(EntityAA.CODE_AA, "test1")
                .add(EntityAAA.REF_E, entityE1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"));
    }
}
