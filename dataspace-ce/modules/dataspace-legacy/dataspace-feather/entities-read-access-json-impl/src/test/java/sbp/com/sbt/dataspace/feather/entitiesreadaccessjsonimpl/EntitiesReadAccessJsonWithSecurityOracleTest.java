package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonWithSecurityTest;

@DisplayName("Testing access to entities for reading through JSON with security on Oracle (Settings 1)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"oracle", "tx", "cleanTestData", "sec"})
@EnabledIfSystemProperty(named = "db.oracle.url", matches = ".+")
public class EntitiesReadAccessJsonWithSecurityOracleTest extends EntitiesReadAccessJsonWithSecurityTest {
}
