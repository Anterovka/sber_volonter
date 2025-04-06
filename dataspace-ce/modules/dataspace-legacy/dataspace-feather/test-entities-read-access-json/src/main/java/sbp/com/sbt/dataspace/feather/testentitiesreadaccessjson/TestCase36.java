package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;


/**
 * Test case 36<ul>
 * <li>Testing lock</li>
 * </ul>
 */
public class TestCase36 extends TestCase {

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        createEntity(Operation.TYPE0, "operation1", propBuilder()
            .add(Operation.SERVICE, service1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
