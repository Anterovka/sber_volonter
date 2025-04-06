package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;


/**
 * Test case 31<ul>
 * <li>testing the ability to obtain the version of the aggregate through external links</li>
 * </ul>
 */
public class TestCase31 extends TestCase{

    @Override
    void createEntities() {
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(TestHelper.VERSION_SETTING, 2));

        String service1Id = createEntity(Service.TYPE0, "service1" ,propBuilder()
                .add(TestHelper.VERSION_SETTING, 3));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(TestHelper.VERSION_SETTING,1 )
                .add(Product.RELATED_PRODUCT, product2Id)
                .add(Product.SERVICES, Arrays.asList(service1Id)));

        String entity1Id = createEntity(Entity.TYPE0, "entity1", propBuilder()
                .add(TestHelper.VERSION_SETTING, 4));

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(TestHelper.AGGREGATE_SETTING, entity1Id)
                .add(Request.CREATED_ENTITY, product1Id));

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(TestHelper.VERSION_SETTING, 5)
                .add(Parameter.ENTITY,  product1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Searching for data to verify getting the aggregate version through links", "search")
        );
    }
}
