package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing the type expression resolver")
@SpringJUnitConfig(ExpressionTypeResolverTestConfiguration.class)
public class ExpressionTypeResolverTest {

    @Autowired
    ExpressionTypeResolver expressionTypeResolver;

    @Autowired
    ModelDescription modelDescription;

    @DisplayName("Тест 1")
    @Test
    void test1() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.code"));
    }

    @DisplayName("Тест 2")
    @Test
    void test2() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it{type = ProductLimited}.limitedOffer"));
    }

    @DisplayName("Тест 3")
    @Test
    void test3() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.aliases.$count"));
    }

    @DisplayName("Тест 4")
    @Test
    void test4() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.relatedProduct.code"));
    }

    @DisplayName("Тест 5")
    @Test
    void test5() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.request.code"));
    }

    @DisplayName("Тест 6")
    @Test
    void test6() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.$count"));
    }

    @DisplayName("Тест 7")
    @Test
    void test7() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.parameters.$count"));
    }

    @DisplayName("Тест 8")
    @Test
    void test8() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.request.initiator.firstName"));
    }

    @DisplayName("Тест 9")
    @Test
    void test9() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.$exists.$asBoolean"));
    }

    @DisplayName("Тест 10")
    @Test
    void test10() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.$id"));
    }

    @DisplayName("Тест 11")
    @Test
    void test11() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.$type"));
    }

    @DisplayName("Тест 12")
    @Test
    void test12() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.code.$count"));
    }

    @DisplayName("Тест 13")
    @Test
    void test13() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.startAction.request.initiator.firstName.$count"));
    }

    @DisplayName("Тест 14")
    @Test
    void test14() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.$type.$count"));
    }

    @DisplayName("Тест 15")
    @Test
    void test15() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.request.initiator.document.code"));
    }

    @DisplayName("Тест 16")
    @Test
    void test16() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "(it.request.initiator == null).$asBoolean"));
    }

    @DisplayName("Тест 17")
    @Test
    void test17() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "(it.request.initiator != null).$asBoolean"));
    }

    @DisplayName("Тест 18")
    @Test
    void test18() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.request.initiator.document.$count"));
    }

    @DisplayName("Тест 19")
    @Test
    void test19() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "(it.$exists || it.$exists && !it.$exists).$asBoolean"));
    }

    @DisplayName("Тест 20")
    @Test
    void test20() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "'Hello'"));
    }

    @DisplayName("Тест 21")
    @Test
    void test21() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "1"));
    }

    @DisplayName("Тест 22")
    @Test
    void test22() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "1.1"));
    }

    @DisplayName("Тест 23")
    @Test
    void test23() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "D2022-02-09"));
    }

    @DisplayName("Тест 24")
    @Test
    void test24() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "D2022-02-09T23:35:10.123"));
    }

    @DisplayName("Тест 25")
    @Test
    void test25() {
        assertEquals(DataType.OFFSET_DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "D2022-02-09T23:35:10.123Z"));
    }

    @DisplayName("Тест 26")
    @Test
    void test26() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "true"));
    }

    @DisplayName("Тест 27")
    @Test
    void test27() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "coalesce(it.p1)"));
    }

    @DisplayName("Тест 28")
    @Test
    void test28() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "coalesce(it.p2)"));
    }

    @DisplayName("Тест 29")
    @Test
    void test29() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "coalesce(it.p7)"));
    }

    @DisplayName("Тест 30")
    @Test
    void test30() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "coalesce(it.p8)"));
    }

    @DisplayName("Тест 31")
    @Test
    void test31() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "entities{type = Product}.$count"));
    }

    @DisplayName("Тест 32")
    @Test
    void test32() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps1.$min"));
    }

    @DisplayName("Тест 33")
    @Test
    void test33() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps2.$min"));
    }

    @DisplayName("Тест 34")
    @Test
    void test34() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps3.$min"));
    }

    @DisplayName("Тест 35")
    @Test
    void test35() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps1.$max"));
    }

    @DisplayName("Тест 36")
    @Test
    void test36() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps2.$max"));
    }

    @DisplayName("Тест 37")
    @Test
    void test37() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps3.$max"));
    }

    @DisplayName("Тест 38")
    @Test
    void test38() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps2.$sum"));
    }

    @DisplayName("Тест 39")
    @Test
    void test39() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps2.$avg"));
    }

    @DisplayName("Тест 40")
    @Test
    void test40() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps2.$exists.$asBoolean"));
    }

    @DisplayName("Тест 41")
    @Test
    void test41() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "-it.p2"));
    }

    @DisplayName("Тест 42")
    @Test
    void test42() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$upper"));
    }

    @DisplayName("Тест 43")
    @Test
    void test43() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$lower"));
    }

    @DisplayName("Тест 44")
    @Test
    void test44() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$length"));
    }

    @DisplayName("Тест 45")
    @Test
    void test45() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$trim"));
    }

    @DisplayName("Тест 46")
    @Test
    void test46() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$ltrim"));
    }

    @DisplayName("Тест 47")
    @Test
    void test47() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$rtrim"));
    }

    @DisplayName("Тест 48")
    @Test
    void test48() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$round"));
    }

    @DisplayName("Тест 49")
    @Test
    void test49() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$ceil"));
    }

    @DisplayName("Тест 50")
    @Test
    void test50() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$floor"));
    }

    @DisplayName("Тест 51")
    @Test
    void test51() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$asString"));
    }

    @DisplayName("Тест 52")
    @Test
    void test52() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$asString"));
    }

    @DisplayName("Тест 53")
    @Test
    void test53() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$asBigDecimal"));
    }

    @DisplayName("Тест 54")
    @Test
    void test54() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$asBigDecimal"));
    }

    @DisplayName("Тест 55")
    @Test
    void test55() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$abs"));
    }

    @DisplayName("Тест 56")
    @Test
    void test56() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$hash"));
    }

    @DisplayName("Тест 57")
    @Test
    void test57() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'1' + '2'"));
    }

    @DisplayName("Тест 58")
    @Test
    void test58() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 + 2"));
    }

    @DisplayName("Тест 59")
    @Test
    void test59() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 + D2022-02-10T00:20:10.123"));
    }

    @DisplayName("Тест 60")
    @Test
    void test60() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10T00:20:10.123 + 1"));
    }

    @DisplayName("Тест 61")
    @Test
    void test61() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 - 1"));
    }

    @DisplayName("Тест 62")
    @Test
    void test62() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10T00:20:10.123 - 1"));
    }

    @DisplayName("Тест 63")
    @Test
    void test63() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 * 1"));
    }

    @DisplayName("Тест 64")
    @Test
    void test64() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 / 1"));
    }

    @DisplayName("Тест 65")
    @Test
    void test65() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$substr(1, 2)"));
    }

    @DisplayName("Тест 66")
    @Test
    void test66() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$substr(1)"));
    }

    @DisplayName("Тест 67")
    @Test
    void test67() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$replace('old', 'new')"));
    }

    @DisplayName("Тест 68")
    @Test
    void test68() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addMilliseconds(1)"));
    }

    @DisplayName("Тест 69")
    @Test
    void test69() {
        assertEquals(DataType.OFFSET_DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10T11:47:10.123+03:00.$addMilliseconds(1)"));
    }

    @DisplayName("Тест 70")
    @Test
    void test70() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addSeconds(1)"));
    }

    @DisplayName("Тест 71")
    @Test
    void test71() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addMinutes(1)"));
    }

    @DisplayName("Тест 72")
    @Test
    void test72() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addHours(1)"));
    }

    @DisplayName("Тест 73")
    @Test
    void test73() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addDays(1)"));
    }

    @DisplayName("Тест 74")
    @Test
    void test74() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addMonths(1)"));
    }

    @DisplayName("Тест 75")
    @Test
    void test75() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addYears(1)"));
    }

    @DisplayName("Тест 76")
    @Test
    void test76() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subMilliseconds(1)"));
    }

    @DisplayName("Тест 77")
    @Test
    void test77() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subSeconds(1)"));
    }

    @DisplayName("Тест 78")
    @Test
    void test78() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subMinutes(1)"));
    }

    @DisplayName("Тест 79")
    @Test
    void test79() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subHours(1)"));
    }

    @DisplayName("Тест 80")
    @Test
    void test80() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subDays(1)"));
    }

    @DisplayName("Тест 81")
    @Test
    void test81() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subMonths(1)"));
    }

    @DisplayName("Тест 82")
    @Test
    void test82() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subYears(1)"));
    }

    @DisplayName("Тест 83")
    @Test
    void test83() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code == null).$asBoolean"));
    }

    @DisplayName("Тест 84")
    @Test
    void test84() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code != null).$asBoolean"));
    }

    @DisplayName("Тест 85")
    @Test
    void test85() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code == 'Hello').$asBoolean"));
    }

    @DisplayName("Тест 86")
    @Test
    void test86() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code != 'Hello').$asBoolean"));
    }

    @DisplayName("Тест 87")
    @Test
    void test87() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code > 'Hello').$asBoolean"));
    }

    @DisplayName("Тест 88")
    @Test
    void test88() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code <= 'Hello').$asBoolean"));
    }

    @DisplayName("Тест 89")
    @Test
    void test89() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code < 'Hello').$asBoolean"));
    }

    @DisplayName("Тест 90")
    @Test
    void test90() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code >= 'Hello').$asBoolean"));
    }

    @DisplayName("Тест 91")
    @Test
    void test91() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.code $like 'Hello%').$asBoolean"));
    }

    @DisplayName("Тест 92")
    @Test
    void test92() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.p2 $between (1, 2)).$asBoolean"));
    }

    @DisplayName("Тест 93")
    @Test
    void test93() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.p2 $in [1, 2]).$asBoolean"));
    }

    @DisplayName("Тест 94")
    @Test
    void test94() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "(it.p2 $in it.ps2).$asBoolean"));
    }

    @DisplayName("Тест 95")
    @Test
    void test95() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 $mod 2"));
    }

    @DisplayName("Тест 96")
    @Test
    void test96() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$min"));
    }

    @DisplayName("Тест 97")
    @Test
    void test97() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$min"));
    }

    @DisplayName("Тест 98")
    @Test
    void test98() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$min"));
    }

    @DisplayName("Тест 99")
    @Test
    void test99() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$max"));
    }

    @DisplayName("Тест 100")
    @Test
    void test100() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$max"));
    }

    @DisplayName("Тест 101")
    @Test
    void test101() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$max"));
    }

    @DisplayName("Тест 102")
    @Test
    void test102() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$sum"));
    }

    @DisplayName("Тест 103")
    @Test
    void test103() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$avg"));
    }

    @DisplayName("Тест 104")
    @Test
    void test104() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$count"));
    }

    @DisplayName("Тест 105")
    @Test
    void test105() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.$map(it.operations.$count).$sum"));
    }

    @DisplayName("Тест 106")
    @Test
    void test106() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Service", "it.operations.code.$min"));
    }

    @DisplayName("Тест 107")
    @Test
    void test107() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "entities{type = Service}.code.$min"));
    }

    @DisplayName("Тест 108")
    @Test
    void test108() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.$map(it.code.$asBigDecimal).$min"));
    }

    @DisplayName("Тест 109")
    @Test
    void test109() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.initiator.lastName.$min"));
    }

    @DisplayName("Тест 110")
    @Test
    void test110() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.initiator.document.code.$min"));
    }

    @DisplayName("Тест 111")
    @Test
    void test111() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.$id.$min"));
    }

    @DisplayName("Тест 112")
    @Test
    void test112() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.$type.$min"));
    }

    @DisplayName("Тест 113")
    @Test
    void test113() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.services.startAction.code.$min"));
    }

    @DisplayName("Тест 114")
    @Test
    void test114() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("Service", "it.operations.request.code.$min"));
    }

    @DisplayName("Тест 115")
    @Test
    void test115() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.aliases.$map(it.$asBigDecimal).$min"));
    }

    @DisplayName("Тест 116")
    @Test
    void test116() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.relatedProduct{alias = product}.aliases.$map((it + @product.code).$asBigDecimal).$min"));
    }

    @DisplayName("Тест 117")
    @Test
    void test117() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.request{alias = request}.createdEntity{type = Product}.services.$map((it.code + @request.code).$asBigDecimal).$min"));
    }

    @DisplayName("Тест 118")
    @Test
    void test118() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "it.request{type = Request}.createdEntity{type = Product}.services.$map((it.code).$asBigDecimal).$min"));
    }

    @DisplayName("Тест 119")
    @Test
    void test121() {
        assertEquals(DataType.OFFSET_DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "now"));
    }

    @DisplayName("Тест 122")
    @Test
    void test122() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p14.$asString"));
    }

    @DisplayName("Тест 123")
    @Test
    void test123() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$asString"));
    }

    @DisplayName("Тест 124")
    @Test
    void test124() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$asString"));
    }

    @DisplayName("Тест 125")
    @Test
    void test125() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$asDate"));
    }

    @DisplayName("Тест 126")
    @Test
    void test126() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$asDateTime"));
    }

    @DisplayName("Тест 127")
    @Test
    void test127() {
        assertEquals(DataType.OFFSET_DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$asOffsetDateTime"));
    }

    @DisplayName("Тест 128")
    @Test
    void test128() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p14.$year"));
    }

    @DisplayName("Тест 129")
    @Test
    void test129() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p14.$month"));
    }

    @DisplayName("Тест 130")
    @Test
    void test130() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p14.$day"));
    }

    @DisplayName("Тест 131")
    @Test
    void test131() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$hour"));
    }

    @DisplayName("Тест 132")
    @Test
    void test132() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$hour"));
    }

    @DisplayName("Тест 133")
    @Test
    void test133() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$minute"));
    }

    @DisplayName("Тест 134")
    @Test
    void test134() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$minute"));
    }

    @DisplayName("Тест 135")
    @Test
    void test135() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$second"));
    }

    @DisplayName("Тест 136")
    @Test
    void test136() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$second"));
    }

    @DisplayName("Тест 137")
    @Test
    void test139() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$offsetHour"));
    }

    @DisplayName("Тест 138")
    @Test
    void test140() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$offsetMinute"));
    }

    @DisplayName("Тест 141")
    @Test
    void test141() {
        assertEquals(DataType.TIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "T16:11:10.123"));
    }

    @DisplayName("Тест 142")
    @Test
    void test142() {
        assertEquals(DataType.TIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$time.$min"));
    }

    @DisplayName("Тест 143")
    @Test
    void test143() {
        assertEquals(DataType.TIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$time.$max"));
    }

    @DisplayName("Тест 144")
    @Test
    void test144() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$date"));
    }

    @DisplayName("Тест 145")
    @Test
    void test145() {
        assertEquals(DataType.TIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'16:16:10.123456'.$asTime"));
    }

    @DisplayName("Тест 146")
    @Test
    void test146() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$time.$asString"));
    }

    @DisplayName("Тест 147")
    @Test
    void test147() {
        assertEquals(DataType.DATE, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$date"));
    }

    @DisplayName("Тест 148")
    @Test
    void test148() {
        assertEquals(DataType.TIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$time"));
    }

    @DisplayName("Тест 149")
    @Test
    void test149() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$offset"));
    }

    @DisplayName("Тест 150")
    @Test
    void test150() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "~it.p2"));
    }

    @DisplayName("Тест 151")
    @Test
    void test151() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 & it.p2"));
    }

    @DisplayName("Тест 152")
    @Test
    void test152() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 | it.p2"));
    }

    @DisplayName("Тест 153")
    @Test
    void test153() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 ^ it.p2"));
    }

    @DisplayName("Тест 154")
    @Test
    void test154() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 << it.p2"));
    }

    @DisplayName("Тест 155")
    @Test
    void test155() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 >> it.p2"));
    }

    @DisplayName("Тест 156")
    @Test
    void test156() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$lpad(10, ' ')"));
    }

    @DisplayName("Тест 157")
    @Test
    void test157() {
        assertEquals(DataType.STRING, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$rpad(10, ' ')"));
    }

    @DisplayName("Тест 158")
    @Test
    void test158() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "('1'==any(it.aliases)).$asBoolean"));
    }

    @DisplayName("Тест 159")
    @Test
    void test159() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "('1'!=any(['1', '2', '3'])).$asBoolean"));
    }

    @DisplayName("Тест 160")
    @Test
    void test160() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "('1'>all(it.aliases)).$asBoolean"));
    }

    @DisplayName("Тест 161")
    @Test
    void test161() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "('1'<=all(['1', '2', '3'])).$asBoolean"));
    }

    @DisplayName("Тест 162")
    @Test
    void test162() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "('1'<any(['1'])).$asBoolean"));
    }

    @DisplayName("Тест 163")
    @Test
    void test163() {
        assertEquals(DataType.BOOLEAN, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "('1'>=any(it.aliases)).$asBoolean"));
    }

    @DisplayName("Тест 164")
    @Test
    void test164() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "2.$power(3)"));
    }

    @DisplayName("Тест 165")
    @Test
    void test165() {
        assertEquals(DataType.BIG_DECIMAL, expressionTypeResolver.resolvePrimitiveExpressionType("Product", "8.$log(2)"));
    }

    @DisplayName("Тест 166")
    @Test
    void test166() {
        assertEquals(DataType.DATETIME, expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p15.$dateTime"));
    }

    @DisplayName("Unsupported operation")
    @Test
    void unsupportedOperationExceptionTest() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "coalesce(true, 1)"));
    }

    @DisplayName("Unsupported operation (2)")
    @Test
    void unsupportedOperationExceptionTest2() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "coalesce('1', 1)"));
    }

    @DisplayName("Unsupported operation (3)")
    @Test
    void unsupportedOperationExceptionTest3() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps4.$min"));
    }

    @DisplayName("Unsupported operation (4)")
    @Test
    void unsupportedOperationExceptionTest4() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps4.$max"));
    }

    @DisplayName("Unsupported operation (5)")
    @Test
    void unsupportedOperationExceptionTest5() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps4.$sum"));
    }

    @DisplayName("Unsupported operation (6)")
    @Test
    void unsupportedOperationExceptionTest6() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.ps4.$avg"));
    }

    @DisplayName("Unsupported operation (7)")
    @Test
    void unsupportedOperationExceptionTest7() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "-it.p1"));
    }

    @DisplayName("Unsupported operation (8)")
    @Test
    void unsupportedOperationExceptionTest8() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$upper"));
    }

    @DisplayName("Unsupported operation (9)")
    @Test
    void unsupportedOperationExceptionTest9() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$lower"));
    }

    @DisplayName("Unsupported operation (10)")
    @Test
    void unsupportedOperationExceptionTest10() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$length"));
    }

    @DisplayName("Unsupported operation (11)")
    @Test
    void unsupportedOperationExceptionTest11() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$trim"));
    }

    @DisplayName("Unsupported operation (12)")
    @Test
    void unsupportedOperationExceptionTest12() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$ltrim"));
    }

    @DisplayName("Unsupported operation (13)")
    @Test
    void unsupportedOperationExceptionTest13() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2.$rtrim"));
    }

    @DisplayName("Unsupported operation (14)")
    @Test
    void unsupportedOperationExceptionTest14() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$round"));
    }

    @DisplayName("Unsupported operation (15)")
    @Test
    void unsupportedOperationExceptionTest15() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$ceil"));
    }

    @DisplayName("Unsupported operation (16)")
    @Test
    void unsupportedOperationExceptionTest16() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$floor"));
    }

    @DisplayName("Unsupported operation (17)")
    @Test
    void unsupportedOperationExceptionTest17() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$asString"));
    }

    @DisplayName("Unsupported operation (18)")
    @Test
    void unsupportedOperationExceptionTest18() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p7.$asBigDecimal"));
    }

    @DisplayName("Unsupported operation (19)")
    @Test
    void unsupportedOperationExceptionTest19() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$abs"));
    }

    @DisplayName("Unsupported operation (20)")
    @Test
    void unsupportedOperationExceptionTest20() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 + true"));
    }

    @DisplayName("Unsupported operation (21)")
    @Test
    void unsupportedOperationExceptionTest21() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true + 1"));
    }

    @DisplayName("Unsupported operation (22)")
    @Test
    void unsupportedOperationExceptionTest22() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'1' + true"));
    }

    @DisplayName("Unsupported operation (23)")
    @Test
    void unsupportedOperationExceptionTest23() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10 + true"));
    }

    @DisplayName("Unsupported operation (24)")
    @Test
    void unsupportedOperationExceptionTest24() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 - true"));
    }

    @DisplayName("Unsupported operation (25)")
    @Test
    void unsupportedOperationExceptionTest25() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10 - true"));
    }

    @DisplayName("Unsupported operation (26)")
    @Test
    void unsupportedOperationExceptionTest26() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 * true"));
    }

    @DisplayName("Unsupported operation (27)")
    @Test
    void unsupportedOperationExceptionTest27() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10 * true"));
    }

    @DisplayName("Unsupported operation (28)")
    @Test
    void unsupportedOperationExceptionTest28() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 / true"));
    }

    @DisplayName("Unsupported operation (29)")
    @Test
    void unsupportedOperationExceptionTest29() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10 / true"));
    }

    @DisplayName("Unsupported operation (30)")
    @Test
    void unsupportedOperationExceptionTest30() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$substr(1, 2)"));
    }

    @DisplayName("Unsupported operation (31)")
    @Test
    void unsupportedOperationExceptionTest31() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$substr(true, 2)"));
    }

    @DisplayName("Unsupported operation (32)")
    @Test
    void unsupportedOperationExceptionTest32() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$substr(1, true)"));
    }

    @DisplayName("Unsupported operation (33)")
    @Test
    void unsupportedOperationExceptionTest33() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$substr(1)"));
    }

    @DisplayName("Unsupported operation (34)")
    @Test
    void unsupportedOperationExceptionTest34() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$substr(true)"));
    }

    @DisplayName("Unsupported operation (35)")
    @Test
    void unsupportedOperationExceptionTest35() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$replace('old', 'new')"));
    }

    @DisplayName("Unsupported operation (36)")
    @Test
    void unsupportedOperationExceptionTest36() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$replace(true, 'new')"));
    }

    @DisplayName("Unsupported operation (37)")
    @Test
    void unsupportedOperationExceptionTest37() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "'Hello'.$replace('old', true)"));
    }

    @DisplayName("Unsupported operation (38)")
    @Test
    void unsupportedOperationExceptionTest38() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addMilliseconds(1)"));
    }

    @DisplayName("Unsupported operation (39)")
    @Test
    void unsupportedOperationExceptionTest39() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addMilliseconds(true)"));
    }

    @DisplayName("Unsupported operation (40)")
    @Test
    void unsupportedOperationExceptionTest40() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addSeconds(1)"));
    }

    @DisplayName("Unsupported operation (41)")
    @Test
    void unsupportedOperationExceptionTest41() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addSeconds(true)"));
    }

    @DisplayName("Unsupported operation (42)")
    @Test
    void unsupportedOperationExceptionTest42() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addMinutes(1)"));
    }

    @DisplayName("Unsupported operation (43)")
    @Test
    void unsupportedOperationExceptionTest43() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addMinutes(true)"));
    }

    @DisplayName("Unsupported operation (44)")
    @Test
    void unsupportedOperationExceptionTest44() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addHours(1)"));
    }

    @DisplayName("Unsupported operation (45)")
    @Test
    void unsupportedOperationExceptionTest45() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addHours(true)"));
    }

    @DisplayName("Unsupported operation (46)")
    @Test
    void unsupportedOperationExceptionTest46() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addDays(1)"));
    }

    @DisplayName("Unsupported operation (47)")
    @Test
    void unsupportedOperationExceptionTest47() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addDays(true)"));
    }

    @DisplayName("Unsupported operation (48)")
    @Test
    void unsupportedOperationExceptionTest48() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addMonths(1)"));
    }

    @DisplayName("Unsupported operation (49)")
    @Test
    void unsupportedOperationExceptionTest49() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addMonths(true)"));
    }

    @DisplayName("Unsupported operation (50)")
    @Test
    void unsupportedOperationExceptionTest50() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$addYears(1)"));
    }

    @DisplayName("Unsupported operation (51)")
    @Test
    void unsupportedOperationExceptionTest51() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$addYears(true)"));
    }

    @DisplayName("Unsupported operation (52)")
    @Test
    void unsupportedOperationExceptionTest52() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subMilliseconds(1)"));
    }

    @DisplayName("Unsupported operation (53)")
    @Test
    void unsupportedOperationExceptionTest53() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subMilliseconds(true)"));
    }

    @DisplayName("Unsupported operation (54)")
    @Test
    void unsupportedOperationExceptionTest54() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subSeconds(1)"));
    }

    @DisplayName("Unsupported operation (55)")
    @Test
    void unsupportedOperationExceptionTest55() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subSeconds(true)"));
    }

    @DisplayName("Unsupported operation (56)")
    @Test
    void unsupportedOperationExceptionTest56() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subMinutes(1)"));
    }

    @DisplayName("Unsupported operation (57)")
    @Test
    void unsupportedOperationExceptionTest57() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subMinutes(true)"));
    }

    @DisplayName("Unsupported operation (58)")
    @Test
    void unsupportedOperationExceptionTest58() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subHours(1)"));
    }

    @DisplayName("Unsupported operation (59)")
    @Test
    void unsupportedOperationExceptionTest59() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subHours(true)"));
    }

    @DisplayName("Unsupported operation (60)")
    @Test
    void unsupportedOperationExceptionTest60() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subDays(1)"));
    }

    @DisplayName("Unsupported operation (61)")
    @Test
    void unsupportedOperationExceptionTest61() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subDays(true)"));
    }

    @DisplayName("Unsupported operation (62)")
    @Test
    void unsupportedOperationExceptionTest62() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subMonths(1)"));
    }

    @DisplayName("Unsupported operation (63)")
    @Test
    void unsupportedOperationExceptionTest63() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subMonths(true)"));
    }

    @DisplayName("Unsupported operation (64)")
    @Test
    void unsupportedOperationExceptionTest64() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true.$subYears(1)"));
    }

    @DisplayName("Unsupported operation (65)")
    @Test
    void unsupportedOperationExceptionTest65() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "D2022-02-10.$subYears(true)"));
    }

    @DisplayName("Unsupported operation (66)")
    @Test
    void unsupportedOperationExceptionTest66() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "true $mod 2"));
    }

    @DisplayName("Unsupported operation (67)")
    @Test
    void unsupportedOperationExceptionTest67() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "1 $mod true"));
    }

    @DisplayName("Unsupported operation (68)")
    @Test
    void unsupportedOperationExceptionTest68() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p8.$min"));
    }

    @DisplayName("Unsupported operation (69)")
    @Test
    void unsupportedOperationExceptionTest69() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p8.$max"));
    }

    @DisplayName("Unsupported operation (70)")
    @Test
    void unsupportedOperationExceptionTest70() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p8.$sum"));
    }

    @DisplayName("Unsupported operation (71)")
    @Test
    void unsupportedOperationExceptionTest71() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p8.$avg"));
    }

    @DisplayName("Unsupported operation (72)")
    @Test
    void unsupportedOperationExceptionTest72() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$asDate"));
    }

    @DisplayName("Unsupported operation (73)")
    @Test
    void unsupportedOperationExceptionTest73() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$asDateTime"));
    }

    @DisplayName("Unsupported operation (74)")
    @Test
    void unsupportedOperationExceptionTest74() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$asOffsetDateTime"));
    }

    @DisplayName("Unsupported operation (75)")
    @Test
    void unsupportedOperationExceptionTest75() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$year"));
    }

    @DisplayName("Unsupported operation (76)")
    @Test
    void unsupportedOperationExceptionTest76() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$month"));
    }

    @DisplayName("Unsupported operation (77)")
    @Test
    void unsupportedOperationExceptionTest77() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$day"));
    }

    @DisplayName("Unsupported operation (78)")
    @Test
    void unsupportedOperationExceptionTest78() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$hour"));
    }

    @DisplayName("Unsupported operation (79)")
    @Test
    void unsupportedOperationExceptionTest79() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$minute"));
    }

    @DisplayName("Unsupported operation (80)")
    @Test
    void unsupportedOperationExceptionTest80() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$second"));
    }

    @DisplayName("Unsupported operation (81)")
    @Test
    void unsupportedOperationExceptionTest81() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$offsetHour"));
    }

    @DisplayName("Unsupported operation (82)")
    @Test
    void unsupportedOperationExceptionTest82() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$offsetMinute"));
    }

    @DisplayName("Unsupported operation (83)")
    @Test
    void unsupportedOperationExceptionTest83() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$asTime"));
    }

    @DisplayName("Unsupported operation (84)")
    @Test
    void unsupportedOperationExceptionTest84() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$date"));
    }

    @DisplayName("Unsupported operation (85)")
    @Test
    void unsupportedOperationExceptionTest85() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$time"));
    }

    @DisplayName("Unsupported operation (86)")
    @Test
    void unsupportedOperationExceptionTest86() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$offset"));
    }

    @DisplayName("Unsupported operation (87)")
    @Test
    void unsupportedOperationExceptionTest87() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "~it.p9"));
    }

    @DisplayName("Unsupported operation (88)")
    @Test
    void unsupportedOperationExceptionTest88() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9 & it.p2"));
    }

    @DisplayName("Unsupported operation (89)")
    @Test
    void unsupportedOperationExceptionTest89() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 & it.p9"));
    }

    @DisplayName("Unsupported operation (90)")
    @Test
    void unsupportedOperationExceptionTest90() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9 | it.p2"));
    }

    @DisplayName("Unsupported operation (91)")
    @Test
    void unsupportedOperationExceptionTest91() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 | it.p9"));
    }

    @DisplayName("Unsupported operation (92)")
    @Test
    void unsupportedOperationExceptionTest92() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9 ^ it.p2"));
    }

    @DisplayName("Unsupported operation (93)")
    @Test
    void unsupportedOperationExceptionTest93() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 ^ it.p9"));
    }

    @DisplayName("Unsupported operation (94)")
    @Test
    void unsupportedOperationExceptionTest94() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9 << it.p2"));
    }

    @DisplayName("Unsupported operation (95)")
    @Test
    void unsupportedOperationExceptionTest95() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 << it.p9"));
    }

    @DisplayName("Unsupported operation (96)")
    @Test
    void unsupportedOperationExceptionTest96() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9 >> it.p2"));
    }

    @DisplayName("Unsupported operation (97)")
    @Test
    void unsupportedOperationExceptionTest97() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p2 >> it.p9"));
    }

    @DisplayName("Unsupported operation (98)")
    @Test
    void unsupportedOperationExceptionTest98() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$lpad(it.p2, it.p1)"));
    }

    @DisplayName("Unsupported operation (99)")
    @Test
    void unsupportedOperationExceptionTest99() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$lpad(it.p9, it.p1)"));
    }

    @DisplayName("Unsupported operation (100)")
    @Test
    void unsupportedOperationExceptionTest100() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$lpad(it.p2, it.p9)"));
    }

    @DisplayName("Unsupported operation (101)")
    @Test
    void unsupportedOperationExceptionTest101() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$rpad(it.p2, it.p1)"));
    }

    @DisplayName("Unsupported operation (102)")
    @Test
    void unsupportedOperationExceptionTest102() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$rpad(it.p9, it.p1)"));
    }

    @DisplayName("Unsupported operation (103)")
    @Test
    void unsupportedOperationExceptionTest103() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p1.$rpad(it.p2, it.p9)"));
    }

    @DisplayName("Unsupported operation (104)")
    @Test
    void unsupportedOperationExceptionTest104() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.code.$power(3)"));
    }

    @DisplayName("Unsupported operation (105)")
    @Test
    void unsupportedOperationExceptionTest105() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "2.$power(it.code)"));
    }

    @DisplayName("Unsupported operation (106)")
    @Test
    void unsupportedOperationExceptionTest106() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.code.$log(2)"));
    }

    @DisplayName("Unsupported operation (107)")
    @Test
    void unsupportedOperationExceptionTest107() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "8.$log(it.code)"));
    }

    @DisplayName("Unsupported operation (108)")
    @Test
    void unsupportedOperationExceptionTest108() {
        assertThrowsCausedBy(UnsupportedOperationException.class, () -> expressionTypeResolver.resolvePrimitiveExpressionType("TestEntity", "it.p9.$dateTime"));
    }

    @DisplayName("Unexpected RAW object")
    @Test
    void unexpectedRawExceptionTest() {
        assertThrowsCausedBy(UnexpectedRawException.class, () -> new ExpressionsProcessorImpl(modelDescription.getEntityDescription("Product")).rawPE("raw"));
    }
}
