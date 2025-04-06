package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;


/**
 * Test case 33<ul>
 * <li>Testing query parameters (for query tables)</li>
 * </ul>
 */
public class TestCase33 extends TestCase {

    @Override
    void createEntities() {
        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
                .add(TestEntity.P13, 'A')
                .add(TestEntity.P1, "Test")
                .add(TestEntity.P2, (byte) 1)
                .add(TestEntity.P3, (short) 2)
                .add(TestEntity.P4, 3)
                .add(TestEntity.P5, (long) 4)
                .add(TestEntity.P12, (float) 1.1)
                .add(TestEntity.P6, 2.2)
                .add(TestEntity.P10, new BigDecimal("3.3"))
                .add(TestEntity.P14, LocalDate.of(2023, 11, 24))
                .add(TestEntity.P7, LocalDateTime.of(2023, 11, 24, 17, 48, 10, 123000000))
                .add(TestEntity.P15, OffsetDateTime.of(2023, 11, 24, 17, 48, 10, 123000000, ZoneOffset.of("+08:00")))
                .add(TestEntity.P8, true));

        params.put("character", 'A');
        params.put("string", "Test");
        params.put("byte", (byte) 1);
        params.put("short", (short) 2);
        params.put("integer", 3);
        params.put("long", (long) 4);
        params.put("float", (float) 1.1);
        params.put("double", 2.2);
        params.put("bigDecimal", new BigDecimal("3.3"));
        params.put("date", LocalDate.of(2023, 11, 24));
        params.put("dateTime", LocalDateTime.of(2023, 11, 24, 17, 48, 10, 123000000));
        params.put("offsetDateTime", OffsetDateTime.of(2023, 11, 24, 17, 48, 10, 123000000, ZoneOffset.of("+08:00")));
        params.put("boolean", true);
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(testData("Search", "search"));
    }
}
