package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.ActionParameter;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug case 2
 */
class BugCase2 extends TestCase {

    @Override
    void createEntities() {
        String productLimited1Id = createEntity(ProductLimited.TYPE0, "productLimited1", propBuilder()
            .add(ProductLimited.LIMITED_OFFER, "limitedOffer1"));

        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
            .add(ProductPlus.AFFECTED_PRODUCTS, Arrays.asList(productLimited1Id)));

        createEntity(ActionParameter.TYPE0, "actionParameter1", propBuilder()
            .add(ActionParameter.EXECUTOR_NAME, "Vasya")
            .add(Parameter.ENTITY, productPlus1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Reading", "read"));
    }
}
