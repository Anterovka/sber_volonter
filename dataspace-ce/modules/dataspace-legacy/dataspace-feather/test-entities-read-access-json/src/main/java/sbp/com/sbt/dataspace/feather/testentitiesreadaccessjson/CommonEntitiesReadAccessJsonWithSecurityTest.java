package sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * General testing of access to entities for reading through JSON with security
 */
abstract class CommonEntitiesReadAccessJsonWithSecurityTest extends CommonEntitiesReadAccessJsonTest {

    @Autowired
    ModelDescription modelDescription;
    @Autowired
    SecurityDriver securityDriver;

    /**
     * Get entity restrictions
     */
    abstract Map<String, String> getEntityRestrictions();

    @DisplayName("Test entity restrictions")
    @Test
    public final void entityRestrictionsTest() {
        assertEquals(getEntityRestrictions(), securityDriver.getRestrictions(modelDescription.getEntityDescriptions().keySet()));
    }
}
