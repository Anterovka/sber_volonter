package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 18<ul>
 * <li>Testing the use of an arbitrary collection of entities</li>
 * </ul>
 */
class TestCase18 extends TestCase {

    TestCase18() {
        super(false);
    }

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder());

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(Parameter.VALUE, "testEntity1")
                .add(Parameter.ENTITY, product1Id));
        createEntity(Parameter.TYPE0, "parameter2", propBuilder()
                .add(Parameter.VALUE, "product1")
                .add(Parameter.ENTITY, product1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"));
    }
}
