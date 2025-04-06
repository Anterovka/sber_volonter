package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonTest;

@DisplayName("Testing access to entities for reading through JSON on Oracle")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"oracle", "tx", "cleanTestData"})
@EnabledIfSystemProperty(named = "db.oracle.url", matches = ".+")
public class EntitiesReadAccessJsonOracleTest extends EntitiesReadAccessJsonTest {
}
