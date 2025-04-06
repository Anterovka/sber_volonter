package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;


/**
 * Test case 32<ul>
 * <li>Testing the current date and time</li>
 * <li>Testing methods for working with date/date and time/date and time with offset</li>
 * <li>Testing bitwise operations</li>
 * <li>Testing strings with right/left padding</li>
 * <li>Testing exponentiation</li>
 * <li>Testing logarithm/li>
 * <li>Testing conditional groups and methods involving them</li>
 * </ul>
 */
public class TestCase32 extends TestCase {

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P15, TestHelper.OFFSET_DATETIME));
        createEntity(TestEntity.TYPE0, "testEntity2", propBuilder()
                .add(TestEntity.P1, "2023-08-21T15:33:10.123456+00:00")
                .add(TestEntity.P2, 3)
                .add(TestEntity.P3, 6)
                .add(TestEntity.P4, 2)
                .add(TestEntity.P5, 13)
                .add(TestEntity.P13, '-')
                .add(TestEntity.P14, LocalDate.of(2023, 8, 21))
                .add(TestEntity.P7, LocalDateTime.of(2023, 8, 21, 15, 33, 10, 123000000))
                .add(TestEntity.P15, OffsetDateTime.of(2023, 8, 21, 15, 33, 10, 123000000, ZoneOffset.UTC)));
        createEntity(TestEntity.TYPE0, "testEntity3", propBuilder());
        createEntity(TestEntity.TYPE0, "testEntity4", propBuilder()
                .add(TestEntity.P2, 2)
                .add(TestEntity.P3, 4)
                .add(TestEntity.P4, 16)
                .add(TestEntity.P5, 32)
                .add(TestEntity.P12, 2.5f)
                .add(TestEntity.P15, OffsetDateTime.of(2024, 2, 13, 15, 36, 10, 123000000, ZoneOffset.of("+08:00")))
                .add(TestEntity.P7, LocalDateTime.of(2024, 2, 13, 15, 36, 10, 123000000))
                .add(TestEntity.PS2, Arrays.asList(1, 2, 3)));
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4"),
            testData("Search (5)", "search5"),
            testData("Search (6)", "search6"));
    }
}
