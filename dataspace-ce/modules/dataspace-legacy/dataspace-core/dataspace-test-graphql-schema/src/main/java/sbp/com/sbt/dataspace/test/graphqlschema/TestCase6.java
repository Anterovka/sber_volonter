package sbp.com.sbt.dataspace.test.graphqlschema;

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
 * Test case 6<ul>
 * <li>Testing of computable fields</li>
 * <li>Testing variables in string expressions</li>
 * </ul>
 */
class TestCase6 extends TestCase {

    static final LocalDateTime DATETIME2 = TestHelper.DATETIME.plusDays(1);
    static final LocalDateTime DATETIME3 = TestHelper.DATETIME.plusDays(2);

    @Override
    void createEntities() {
        String testEntity2Id = createEntity(TestEntity.TYPE0, "testEntity2", propBuilder());
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
                .add(TestEntity.PS4, Arrays.asList(Boolean.TRUE, Boolean.FALSE))
                .add(TestEntity.R1, testEntity2Id)
                .add(TestEntity.RC1, Arrays.asList(testEntity2Id)));
        createEntity(TestEntity.TYPE0, "testEntity3", propBuilder()
                .add(TestEntity.R1, testEntity1Id));
        createEntity(TestEntity.TYPE0, "testEntity4", propBuilder()
                .add(TestEntity.P1, "${varInt}")
                .add(TestEntity.P13, '}'));

        properties.put("date", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE));
        //When reading from the DB, we cannot retrieve information about the offset (from the timestamp); we need to convert the expected OffsetDateTime value to UTC for correct comparison of the result.
        properties.put("offsetDateTime", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("time", DateTimeFormatter.ISO_LOCAL_TIME.format(TestHelper.DATETIME.toLocalTime()));
        properties.put("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME));
        properties.put("dateTime2", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(DATETIME2));
        properties.put("dateTime3", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(DATETIME3));
        properties.put("bytes", Base64.getEncoder().encodeToString(TestHelper.BYTES));
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
            testData("Search (10)", "search10")
        );
    }
}
