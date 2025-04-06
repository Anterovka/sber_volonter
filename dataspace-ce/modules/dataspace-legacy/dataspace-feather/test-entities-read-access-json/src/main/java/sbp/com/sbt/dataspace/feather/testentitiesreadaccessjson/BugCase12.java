package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.OperationLimited;
import sbp.com.sbt.dataspace.feather.testmodel.OperationSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 12
 */
class BugCase12 extends TestCase {

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());

        String operationSpecial1Id = createEntity(OperationSpecial.TYPE0, "operationSpecial1", propBuilder()
            .add(Operation.SERVICE, service1Id));

        String operationLimited1Id = createEntity(OperationLimited.TYPE0, "operationLimited1", propBuilder()
            .add(Operation.SERVICE, service1Id));

        createEntity(Request.TYPE0, "request1", propBuilder()
            .add(Request.CREATED_ENTITY, operationSpecial1Id));
        createEntity(Request.TYPE0, "request2", propBuilder()
            .add(Request.CREATED_ENTITY, operationLimited1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
