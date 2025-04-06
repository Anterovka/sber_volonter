package sbp.com.sbt.dataspace.feather.simplesecuritydriver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing settings")
public class SettingsTest {

    @DisplayName("Test")
    @Test
    public void test() {
        SimpleSecurityDriverSettings simpleSecurityDriverSettings = new SimpleSecurityDriverSettings();
        assertEquals(Collections.emptyMap(), simpleSecurityDriverSettings.getEntityRestrictions());
        assertEquals("The security driver settings (Entity restrictions = '{}')", simpleSecurityDriverSettings.toString());
    }
}
