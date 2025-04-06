package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonWithSecurityTest;

@DisplayName("Testing access to entities for reading through JSON with security on PostgreSQL (Settings 1)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"postgresql", "tx", "cleanTestData", "sec"})
@EnabledIfSystemProperty(named = "db.postgres.url", matches = ".+")
public class EntitiesReadAccessJsonWithSecurityPostgresqlTest extends EntitiesReadAccessJsonWithSecurityTest {
}
