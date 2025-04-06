package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonTest;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonWithSecurityTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

@DisplayName("Testing access to entities for reading through JSON with security on H2 (Settings 1)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx", "sec", EntitiesReadAccessJsonConfiguration.TOOLS_PROFILE})
public class EntitiesReadAccessJsonWithSecurityH2Settings1Test extends EntitiesReadAccessJsonWithSecurityTest {

    @Autowired
    EntitiesReadAccessJsonTools entitiesReadAccessJsonTools;

    @Nested
    @DisplayName("Testing access tools to read entities through JSON")
    class ToolsTest {

        @DisplayName("Тест")
        @Test
        public void test() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase16_search10_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJsonWithSecurityH2Settings1Test.class, "TestCase16_search10_query.sql"), searchData.getSqlQuery());
        }
    }
}
