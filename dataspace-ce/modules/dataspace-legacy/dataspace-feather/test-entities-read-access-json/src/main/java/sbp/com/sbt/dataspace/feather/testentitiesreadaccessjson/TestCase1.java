package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
* Test case 1<ul>
* <li>Testing save/read of primitives of all kinds</li>
* <li>Testing the saving/reading of the final entity (without a parent)</li>
 * </ul>
 */
class TestCase1 extends TestCase {

    static final LocalDateTime DATETIME2 = TestHelper.DATETIME.plusDays(1);
    static final LocalDateTime DATETIME3 = TestHelper.DATETIME.plusDays(2);

    @Override
    void createEntities() {
        String testEntity1Id = createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P1, "Hello")
                .add(TestEntity.P2, Byte.MAX_VALUE)
                .add(TestEntity.P3, Short.MAX_VALUE)
                .add(TestEntity.P4, Integer.MAX_VALUE)
                .add(TestEntity.P5, Long.MAX_VALUE)
                .add(TestEntity.P6, 1234567.890123456)
                .add(TestEntity.P7, TestHelper.DATETIME)
                .add(TestEntity.P8, Boolean.TRUE)
                .add(TestEntity.P9, TestHelper.BYTES)
                .add(TestEntity.P10, new BigDecimal("123456789.0123456789"))
                .add(TestEntity.P11, TestHelper.STRING_5000)
                .add(TestEntity.P12, 123.4567F)
                .add(TestEntity.P13, "G")
                .add(TestEntity.P14, TestHelper.DATE)
                .add(TestEntity.P15, TestHelper.OFFSET_DATETIME)
                .add(TestEntity.PS1, Arrays.asList("1", "2", "3"))
                .add(TestEntity.PS2, Arrays.asList(1, 2, 3))
                .add(TestEntity.PS3, Arrays.asList(TestHelper.DATETIME, DATETIME2, DATETIME3))
                .add(TestEntity.PS4, Arrays.asList(Boolean.TRUE, Boolean.FALSE)));

        updateEntity(TestEntity.TYPE0, testEntity1Id, propBuilder()
                .add(TestEntity.R1, testEntity1Id)
                .add(TestEntity.RC1, Arrays.asList(testEntity1Id)));

        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder());

        properties.put("date", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE));
        properties.put("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME));
        //When reading from the DB, we cannot obtain information about the offset (from the timestamp); we need to convert the expected OffsetDateTime value to UTC for correct comparison of the result.
        properties.put("offsetDateTime", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("dateTime2", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(DATETIME2));
        properties.put("dateTime3", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(DATETIME3));
        properties.put("bytes", Base64.getEncoder().encodeToString(TestHelper.BYTES));
        properties.put("string5000", TestHelper.STRING_5000);
        properties.put("string10", TestHelper.STRING_5000.substring(0, 10));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Reading", "read"),
            testData("Reading (2)", "read2"),
            testData("Reading (3)", "read3"),
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"),
            testData("Search (6)", "search6"),
            testData("Search (7)", "search7"),
            testData("Search with limit restriction", "searchWithLimit"),
            testData("Search with limit and count of elements", "searchWithLimitAndCount"),
            testData("Counting the number of elements", "count"));
    }
}
