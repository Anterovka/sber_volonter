package sbp.com.sbt.dataspace.test.graphqlschema;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.Document;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Request;

import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 5<ul>
 * <li>Testing the grouping link</li>
 * </ul>
 */
class TestCase5 extends TestCase {

    @Override
    void createEntities() {
        String document1Id = createEntity(Document.TYPE0, "document1", propBuilder());

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.LAST_NAME, "Ivanov")
                        .add(Person.FIRST_NAME, "Ivan")
                        .add(Person.DOCUMENT, document1Id)));

        createEntity(Request.TYPE0, "request2", propBuilder()
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.FIRST_NAME, "Vasya")));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
