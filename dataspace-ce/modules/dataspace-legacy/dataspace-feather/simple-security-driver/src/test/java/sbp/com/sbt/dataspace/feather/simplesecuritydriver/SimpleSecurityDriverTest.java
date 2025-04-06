package sbp.com.sbt.dataspace.feather.simplesecuritydriver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;
import sbp.com.sbt.dataspace.feather.testmodel.ActionSpecial;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;
import sbp.com.sbt.dataspace.feather.testmodel.Product;
import sbp.com.sbt.dataspace.feather.testmodel.ProductLimited;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testing Simple Security Driver")
@SpringJUnitConfig(SimpleSecurityDriverTestConfiguration.class)
public class SimpleSecurityDriverTest {

    @Autowired
    SecurityDriver securityDriver;

    @DisplayName("Тест 1")
    @Test
    public void test1() {
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put(Product.TYPE0, SimpleSecurityDriverTestConfiguration.PRODUCT_RESTRICTION);
        assertEquals(expectedResult, securityDriver.getRestrictions(Collections.singleton(Product.TYPE0)));
    }

    @DisplayName("Тест 2")
    @Test
    public void test2() {
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put(ProductLimited.TYPE0, SimpleSecurityDriverTestConfiguration.PRODUCT_LIMITED_RESTRICTION);
        expectedResult.put(ActionSpecial.TYPE0, SimpleSecurityDriverTestConfiguration.ACTION_SPECIAL_RESTRICTION);
        assertEquals(expectedResult, securityDriver.getRestrictions(new LinkedHashSet<>(Arrays.asList(ProductLimited.TYPE0, ActionSpecial.TYPE0, Parameter.TYPE0))));
    }
}
