package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 15<ul>
 * <li>Testing query merge</li>
 * </ul>
 */
class TestCase15 extends TestCase {

    TestCase15() {
        super(false);
    }

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P1, "string1")
                .add(TestEntity.P2, 123)
                .add(TestEntity.PS1, Arrays.asList("string1", "string2")));

        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
                .add(TestEntity.P11, TestHelper.STRING_5000));

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        String product2Service2Id = createEntity(Service.TYPE0, "product2Service2", propBuilder()
                .add(Service.MANAGER_PERSONAL_CODE, 2));

        createEntity(Parameter.TYPE0, "parameter1", propBuilder()
                .add(Parameter.ENTITY, service1Id));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder());
        createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.RELATED_PRODUCT, product1Id)
                .add(Product.SERVICES, Arrays.asList(service1Id, product2Service2Id)));

        properties.put("string5000", TestHelper.STRING_5000);
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"),
            testData("Search (6)", "search6"),
            testData("Search (7)", "search7"),
            testData("Search (8)", "search8"),
            testData("Search (9)", "search9"),
            testData("Search with element count", "searchWithCount"),
            testData("Search with element count (2)", "searchWithCount2"),
            testData("Search with element count (3)", "searchWithCount3"),
            testData("Search with limit restriction", "searchWithLimit"),
            testData("Search with limit 2", "searchWithLimit2"),
            testData("Search with limit 3", "searchWithLimit3"),
            testData("Search with limit 4", "searchWithLimit4"),
            testData("Search with limit and count of elements", "searchWithLimitAndCount"),
            testData("Search with limit and element count (2)", "searchWithLimitAndCount2"),
            testData("Search with limit and element count (3)", "searchWithLimitAndCount3"),
            testData("Search with limit and element count (4)", "searchWithLimitAndCount4"),
            testData("Counting the number of elements", "count"));
    }
}
