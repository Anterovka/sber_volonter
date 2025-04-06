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

@DisplayName("Testing access to entities for reading through JSON (3) on H2 (Settings 6)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s6", "tx", EntitiesReadAccessJsonConfiguration.TOOLS_PROFILE})
public class EntitiesReadAccessJson3H2Settings6Test extends EntitiesReadAccessJson3Test {

    @Autowired
    EntitiesReadAccessJsonTools entitiesReadAccessJsonTools;

    @Nested
    @DisplayName("Testing access tools to read entities through JSON")
    public class ToolsTest {

        @DisplayName("Тест")
        @Test
        public void test() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase34_search_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJson3H2Settings6Test.class, "TestCase34_search_query.sql"), searchData.getSqlQuery());
        }

        @DisplayName("Тест (2)")
        @Test
        public void test2() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase34_search2_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJson3H2Settings6Test.class, "TestCase34_search2_query.sql"), searchData.getSqlQuery());
        }

        @DisplayName("Тест (3)")
        @Test
        public void test3() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase34_search3_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJson3H2Settings6Test.class, "TestCase34_search3_query.sql"), searchData.getSqlQuery());
        }

        @DisplayName("Тест (4)")
        @Test
        public void test4() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase34_search4_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJson3H2Settings6Test.class, "TestCase34_search4_query.sql"), searchData.getSqlQuery());
        }

        @DisplayName("Тест (5)")
        @Test
        public void test5() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonTest.class, "TestCase34_search5_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJson3H2Settings6Test.class, "TestCase34_search5_query.sql"), searchData.getSqlQuery());
        }
    }
}
