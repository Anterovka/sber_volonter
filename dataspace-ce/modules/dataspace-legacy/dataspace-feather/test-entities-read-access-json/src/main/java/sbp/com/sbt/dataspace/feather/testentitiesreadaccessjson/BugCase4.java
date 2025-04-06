package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.OperationLimited;
import sbp.com.sbt.dataspace.feather.testmodel.OperationSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 4
 */
class BugCase4 extends TestCase {

    @Override
    void createEntities() {
        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());

        createEntity(OperationSpecial.TYPE0, "operationSpecial1", propBuilder()
            .add(OperationSpecial.SPECIAL_OFFER, "specialOffer1")
            .add(Operation.SERVICE, service1Id));

        createEntity(OperationLimited.TYPE0, "operationLimited1", propBuilder()
            .add(OperationLimited.LIMITED_OFFER, "limitedOffer1")
            .add(Operation.SERVICE, service1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
