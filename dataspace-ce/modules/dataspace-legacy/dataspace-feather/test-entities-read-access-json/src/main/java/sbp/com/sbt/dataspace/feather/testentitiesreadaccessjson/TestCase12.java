package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 12<ul>
 * <li>Testing functions: hash, coalesce</li>
 * <li>Testing string functions: length, trim, substr, replace, asBigDecimal</li>
 * <li>Testing numerical functions: round, ceil, floor, asString, abs</li>
 * <li>Testing functions with dates: addMilliseconds, addSeconds, addMinutes, addHours, addDays, addMonths, addYears, subMilliseconds, subSeconds, subMinutes, subHours, subDays, subMonths, subYears</li>
 * </ul>
 */
class TestCase12 extends TestCase {

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P1, "  Hello, world!  ")
                .add(TestEntity.P2, 3)
                .add(TestEntity.P3, 5)
                .add(TestEntity.P6, 1.3)
                .add(TestEntity.P7, LocalDateTime.of(2020, 9, 3, 16, 59, 30, 123000000)));
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
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
                .add(TestEntity.P14, TestHelper.DATE));
        createEntity(TestEntity.TYPE0, "testEntity3", propBuilder()
                .add(TestEntity.P1, "123")
                .add(TestEntity.P2, 321));

        properties.put("date", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE));
        properties.put("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"));
    }
}
