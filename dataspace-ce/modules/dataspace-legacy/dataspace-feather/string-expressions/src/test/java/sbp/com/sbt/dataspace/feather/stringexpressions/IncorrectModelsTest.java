package sbp.com.sbt.dataspace.feather.stringexpressions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.EntityDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionImpl;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings;

import java.util.Collections;

import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing of incorrect models")
public class IncorrectModelsTest {

    @DisplayName("Test for exception 'The name does not match the regular expression'")
    @Test
    public void nameDoesNotMatchRegExpExceptionTest() {
        assertThrowsCausedBy(NameDoesNotMatchRegExpException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity-01", new EntityDescriptionSettings()),
                Collections.singletonList(new StringExpressionsModelDescriptionCheck())));
    }
}
