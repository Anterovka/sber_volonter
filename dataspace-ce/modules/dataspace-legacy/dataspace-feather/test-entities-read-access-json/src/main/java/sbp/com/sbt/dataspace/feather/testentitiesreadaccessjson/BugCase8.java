package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Bug-case 8
 */
class BugCase8 extends TestCase {

    @Override
    void createEntities() {
        String testEntity1Id = createEntity(TestEntity.TYPE0, "testEntity1", propBuilder());
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
            .add(TestEntity.R1, testEntity1Id)
            .add(TestEntity.P7, TestHelper.DATETIME)
            .add(TestEntity.P14, TestHelper.DATE)
            .add(TestEntity.P15, TestHelper.OFFSET_DATETIME));

        properties.put("date", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE));
        properties.put("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME));
        properties.put("offsetDateTime", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.withOffsetSameInstant(ZoneOffset.UTC)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
