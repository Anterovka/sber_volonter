package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonWithSecurity2Test;

@DisplayName("Testing Entity Read Access via JSON with Security (2) on H2 (Settings 1)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx", "sec2"})
public class EntitiesReadAccessJsonWithSecurity2H2Settings1Test extends EntitiesReadAccessJsonWithSecurity2Test {
}
