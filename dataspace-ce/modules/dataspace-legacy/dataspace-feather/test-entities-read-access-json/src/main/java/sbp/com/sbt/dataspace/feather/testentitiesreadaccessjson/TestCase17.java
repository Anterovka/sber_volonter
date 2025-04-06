package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Agreement;
import sbp.com.sbt.dataspace.feather.testmodel.Permission;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 17<ul>
 * <li>Testing group links</li>
 * <li>Testing the existence of a link method</li>
 * </ul>
 */
class TestCase17 extends TestCase {

    TestCase17() {
        super(false);
    }

    @Override
    void createEntities() {
        String permission1Id = createEntity(Permission.TYPE0, "permission1", propBuilder()
                .add(Permission.NUMBER, 1));

        createEntity(Agreement.TYPE0, "agreement1", propBuilder()
                .add(Agreement.DOCUMENT, permission1Id)
                .add(Agreement.PARTICIPANT, "permission1"));
        createEntity(Agreement.TYPE0, "agreement2", propBuilder()
                .add(Agreement.DOCUMENT, permission1Id));

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder());

        createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id)));

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.CREATED_ENTITY, service1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Ivan")
                        .add(Person.LAST_NAME, "Ivanov")
                        .add(Person.DOCUMENT, permission1Id)));
        createEntity(Request.TYPE0, "request2", propBuilder()
                .add(Request.CREATED_ENTITY, service2Id));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"));
    }
}
