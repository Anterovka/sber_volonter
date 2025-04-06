package sbp.com.sbt.dataspace.feather.modeldescriptionimpl2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Testing settings")
public class SettingsTest {

    @DisplayName("Test")
    @Test
    public void test() {
        ModelDescriptionSettings modelDescriptionSettings = new ModelDescriptionSettings();
        assertNull(modelDescriptionSettings.getPdmModel());
        assertEquals("Model description settings (PDM file model = 'null')", modelDescriptionSettings.toString());
    }
}
