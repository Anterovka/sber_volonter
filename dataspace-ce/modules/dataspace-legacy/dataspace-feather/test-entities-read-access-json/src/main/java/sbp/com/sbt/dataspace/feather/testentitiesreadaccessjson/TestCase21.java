package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import sbp.com.sbt.dataspace.feather.testcommon.TestData;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Operation;
import sbp.com.sbt.dataspace.feather.testmodel.Person;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductPlus;
import sbp.com.sbt.dataspace.feather.testmodel.Request;
import sbp.com.sbt.dataspace.feather.testmodel.Service;
import sbp.com.sbt.dataspace.feather.testmodel.TestEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;
import static sbp.com.sbt.dataspace.feather.testcommon.TestData.testData;

/**
 * Test case 21<ul>
 * <li>Testing computable properties</li>
 * </ul>
 */
class TestCase21 extends TestCase {

    TestCase21() {
        super(false);
    }

    @Override
    void createEntities() {
        String service3Id = createEntity(Service.TYPE0, "service3", propBuilder());

        createEntity(Operation.TYPE0, "operation1", propBuilder()
                .add(Operation.SERVICE, service3Id));

        String product1Id = createEntity(Product.TYPE0, "product1", propBuilder()
                .add(Product.CREATOR_CODE, 1)
                .add(Product.SERVICES, Arrays.asList(service3Id)));
        String product2Id = createEntity(Product.TYPE0, "product2", propBuilder()
                .add(Product.CREATOR_CODE, 2));
        String product3Id = createEntity(Product.TYPE0, "product3", propBuilder()
                .add(Product.CREATOR_CODE, 3));

        String service1Id = createEntity(Service.TYPE0, "service1", propBuilder());
        String service2Id = createEntity(Service.TYPE0, "service2", propBuilder());

        String productPlus1Id = createEntity(ProductPlus.TYPE0, "productPlus1", propBuilder()
                .add(Product.CREATOR_CODE, 4)
                .add(Product.RELATED_PRODUCT, product1Id)
                .add(Product.SERVICES, Arrays.asList(service1Id, service2Id))
                .add(ProductPlus.AFFECTED_PRODUCTS, Arrays.asList(product2Id, product3Id)));

        createEntity(Request.TYPE0, "request1", propBuilder()
                .add(Request.CREATED_ENTITY, productPlus1Id)
                .add(Request.INITIATOR, propBuilder()
                        .add(Person.LAST_NAME, "Ivanov")));

        createEntity(TestEntity.TYPE0, "testEntity1", propBuilder()
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
                .add(TestEntity.P15, TestHelper.OFFSET_DATETIME));

        properties.put("date", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE));
        properties.put("dateTime2d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).plusNanos(1_000_000)));
        properties.put("dateTime3d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).plusSeconds(1)));
        properties.put("dateTime4d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).plusMinutes(1)));
        properties.put("dateTime5d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).plusHours(1)));
        properties.put("date6", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE.plusDays(1)));
        properties.put("date7", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE.plusMonths(1)));
        properties.put("date8", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE.plusYears(1)));
        properties.put("dateTime9d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).minusNanos(1_000_000)));
        properties.put("dateTime10d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).minusSeconds(1)));
        properties.put("dateTime11d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).minusMinutes(1)));
        properties.put("dateTime12d", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.of(TestHelper.DATE, LocalTime.MIDNIGHT).minusHours(1)));
        properties.put("date13", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE.minusDays(1)));
        properties.put("date14", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE.minusMonths(1)));
        properties.put("date15", DateTimeFormatter.ISO_LOCAL_DATE.format(TestHelper.DATE.minusYears(1)));
        properties.put("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME));
        properties.put("dateTime2", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusNanos(1_000_000)));
        properties.put("dateTime3", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusSeconds(1)));
        properties.put("dateTime4", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusMinutes(1)));
        properties.put("dateTime5", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusHours(1)));
        properties.put("dateTime6", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusDays(1)));
        properties.put("dateTime7", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusMonths(1)));
        properties.put("dateTime8", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.plusYears(1)));
        properties.put("dateTime9", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusNanos(1_000_000)));
        properties.put("dateTime10", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusSeconds(1)));
        properties.put("dateTime11", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusMinutes(1)));
        properties.put("dateTime12", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusHours(1)));
        properties.put("dateTime13", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusDays(1)));
        properties.put("dateTime14", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusMonths(1)));
        properties.put("dateTime15", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TestHelper.DATETIME.minusYears(1)));
        properties.put("offsetDateTime", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime2", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusNanos(1_000_000).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime3", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusSeconds(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime4", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusMinutes(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime5", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusHours(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime6", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusDays(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime7", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusMonths(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime8", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.plusYears(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime9", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusNanos(1_000_000).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime10", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusSeconds(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime11", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusMinutes(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime12", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusHours(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime13", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusDays(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime14", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusMonths(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("offsetDateTime15", DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(TestHelper.OFFSET_DATETIME.minusYears(1).withOffsetSameInstant(ZoneOffset.UTC)));
        properties.put("bytes", Base64.getEncoder().encodeToString(TestHelper.BYTES));
        properties.put("string5000", TestHelper.STRING_5000);
    }

    @Override
    List<TestData> getTestsData() {
        return Arrays.asList(
            testData("Search", "search"),
            testData("Search (2)", "search2"),
            testData("Search (3)", "search3"),
            testData("Search (4)", "search4")
        );
    }
}
