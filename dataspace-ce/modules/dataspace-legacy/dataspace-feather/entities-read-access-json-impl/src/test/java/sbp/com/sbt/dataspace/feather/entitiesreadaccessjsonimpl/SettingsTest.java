package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testing settings")
public class SettingsTest {

    @DisplayName("Тест")
    @Test
    public void test() {
        EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings = new EntitiesReadAccessJsonSettings()
            .setSqlDialect(SqlDialect.ORACLE)
            .setDefaultLimit(10)
            .setSchemaName("feather")
            .setMaxSecurityRecursionDepth(20)
            .setReadRecordsLimit(100)
            .setOptimizeJoins()
            .setOffsetDateTimeZoneId(ZoneOffset.of("+03:00"));
        assertEquals(SqlDialect.ORACLE, entitiesReadAccessJsonSettings.getSqlDialect());
        assertEquals(10, entitiesReadAccessJsonSettings.getDefaultLimit());
        assertEquals("feather", entitiesReadAccessJsonSettings.getSchemaName());
        assertEquals(20, entitiesReadAccessJsonSettings.getMaxSecurityRecursionDepth());
        assertEquals(100, entitiesReadAccessJsonSettings.getReadRecordsLimit());
        assertNull(entitiesReadAccessJsonSettings.getTableQueryProvider());
        assertTrue(entitiesReadAccessJsonSettings.doOptimizeJoins());
        assertEquals(ZoneOffset.of("+03:00"), entitiesReadAccessJsonSettings.getOffsetDateTimeZoneId());
        assertEquals("Access settings to entities for reading (SQL dialect = 'ORACLE'; Default limit of elements = '10'; Schema name = 'feather'; Maximum recursion depth for security = '20'; Limit on the number of read records = '100'; Optimize joins = 'true'; Identifier of the date and time zone with offset = '+03:00')", entitiesReadAccessJsonSettings.toString());
    }

    @DisplayName("Test for the exception 'Invalid value of the number of elements constraint'")
    @Test
    public void invalidLimitExceptionTest() {
        EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings = new EntitiesReadAccessJsonSettings();
        assertThrows(InvalidLimitException.class, () -> entitiesReadAccessJsonSettings.setDefaultLimit(-1));
    }

    @DisplayName("Test for exception 'Invalid value of maximum recursion depth for security'")
    @Test
    public void invalidMaxSecurityRecursionDepthExceptionTest() {
        EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings = new EntitiesReadAccessJsonSettings();
        assertThrows(InvalidMaxSecurityRecursionDepthException.class, () -> entitiesReadAccessJsonSettings.setMaxSecurityRecursionDepth(-1));
    }

    @DisplayName("Test for the exception 'Invalid value of the read record limit constraint'")
    @Test
    public void invalidReadRecordsLimitExceptionTest() {
        EntitiesReadAccessJsonSettings entitiesReadAccessJsonSettings = new EntitiesReadAccessJsonSettings();
        assertThrows(InvalidReadRecordsLimitException.class, () -> entitiesReadAccessJsonSettings.setReadRecordsLimit(-1));
    }
}
