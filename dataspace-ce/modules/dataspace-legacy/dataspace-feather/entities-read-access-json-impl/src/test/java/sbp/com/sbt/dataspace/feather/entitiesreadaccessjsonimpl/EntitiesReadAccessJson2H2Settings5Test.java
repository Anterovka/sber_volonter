package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJson2Test;

@DisplayName("Testing access to entities for reading through JSON (2) on H2 (Settings 5)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s5", "tx"})
public class EntitiesReadAccessJson2H2Settings5Test extends EntitiesReadAccessJson2Test {
}
