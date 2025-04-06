package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testing settings")
public class SettingsTest {

    @DisplayName("Тест")
    @Test
    public void test() {
        ModelDescriptionSettings modelDescriptionSettings = new ModelDescriptionSettings();
        assertNull(modelDescriptionSettings.getModelResourceName());
        assertEquals("Model Description Settings (Resource name = 'null')", modelDescriptionSettings.toString());
    }

    @DisplayName("Test for exception 'Error during model description parsing'")
    @Test
    public void parseModelDescriptionExceptionTest() {
        assertThrows(ParseModelDescriptionException.class, () -> new ModelDescriptionConfiguration().modelDescription(new ModelDescriptionSettings()
                .setModelResourceName("nonexistentResource"),
            Collections.emptyList()));
    }
}
