package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonWithSecurityTest;

@DisplayName("Testing access to entities for reading through JSON with security on H2 (Settings 1)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s2", "tx", "sec"})
public class EntitiesReadAccessJsonWithSecurityH2Settings2Test extends EntitiesReadAccessJsonWithSecurityTest {
}
