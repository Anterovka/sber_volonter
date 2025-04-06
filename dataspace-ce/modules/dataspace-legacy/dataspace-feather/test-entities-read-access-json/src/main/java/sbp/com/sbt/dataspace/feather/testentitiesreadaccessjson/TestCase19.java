package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 19<ul>
 * <li>Testing the use of an arbitrary collection of entities with security</li>
 * </ul>
 */
class TestCase19 extends TestCase {

    TestCase19() {
        super(false);
    }

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder());

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Entity.NAME, "entity1")
                .add(Product.CREATOR_CODE, 1));

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(Entity.NAME, "entity2")
                .add(Parameter.VALUE, "testEntity1")
                .add(Parameter.ENTITY, product1Id));
        createEntity(Parameter.TYPE0, "parameter2", propBuilder()
                .add(Entity.NAME, "entity3")
                .add(Parameter.VALUE, "product1")
                .add(Parameter.ENTITY, product1Id));
        createEntity(Parameter.TYPE0, "parameter3", propBuilder()
                .add(Parameter.VALUE, "product1")
                .add(Parameter.ENTITY, product1Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
