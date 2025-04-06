package sbp.com.sbt.dataspace.feather.expressionscommon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;

@DisplayName("Testing the general helper for expressions")
public class CommonExpressionsHelperTest {

    @DisplayName("Test for obtaining specification")
    @Test
    public void getSpecificationTest() {
        assertNull(getSpecification(RootSpecificationImpl::new, null));
        assertEquals("Entity", getSpecification(RootSpecificationImpl::new, specification -> specification.setType("Entity")).getType());
    }
}
