package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJson3Test;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

@DisplayName("Testing access to entities for reading through JSON (4) on H2 (Settings 7)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s6", "tx", "sec3", EntitiesReadAccessJsonConfiguration.TOOLS_PROFILE})
public class EntitiesReadAccessJson4H2Settings7Test extends EntitiesReadAccessJson3Test {

    @Autowired
    EntitiesReadAccessJsonTools entitiesReadAccessJsonTools;

    @Nested
    @DisplayName("Testing access tools to read entities through JSON")
    public class ToolsTest {

        @DisplayName("Тест")
        @Test
        public void test() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase35_search_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJson4H2Settings7Test.class, "TestCase35_search_query.sql"), searchData.getSqlQuery());
        }
    }
}
