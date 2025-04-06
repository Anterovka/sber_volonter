package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.testentitiesreadaccessjson.EntitiesReadAccessJsonTest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

@DisplayName("Testing access to entities for reading through JSON on H2 (Settings 1)")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx", EntitiesReadAccessJsonConfiguration.TOOLS_PROFILE})
public class EntitiesReadAccessJsonH2Settings1Test extends EntitiesReadAccessJsonTest {

    @Autowired
    @Qualifier(EntitiesReadAccessJsonConfiguration.EXPRESSIONS_PROCESSOR_BEAN_NAME)
    private ExpressionsProcessor expressionsProcessor;

    @Autowired
    EntitiesReadAccessJsonTools entitiesReadAccessJsonTools;

    @Test
    public void unexpectedRawExceptionTest() {
        assertThrowsCausedBy(UnexpectedRawException.class, () -> expressionsProcessor.rawPE("raw"));
    }

    @Nested
    @DisplayName("Testing access tools to read entities through JSON")
    public class ToolsTest {

        @DisplayName("Тест")
        @Test
        public void test() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(getStringFromResource(EntitiesReadAccessJsonH2Settings1Test.class, "test_request.json"));
            assertEquals(getStringFromResource(EntitiesReadAccessJsonH2Settings1Test.class, "test_query.sql"), searchData.getSqlQuery());
            assertArrayEquals(new String[]{"p0"}, searchData.getSqlParameterSource().getParameterNames());
            assertEquals(10L, searchData.getSqlParameterSource().getValue("p0"));
        }

        @DisplayName("Тест (2)")
        @Test
        public void test2() {
            SearchData searchData = entitiesReadAccessJsonTools.getSearchData(wrap(() -> new ObjectMapper().readTree(getStringFromResource(EntitiesReadAccessJsonH2Settings1Test.class, "test_request.json"))));
            assertEquals(getStringFromResource(EntitiesReadAccessJsonH2Settings1Test.class, "test_query.sql"), searchData.getSqlQuery());
            assertArrayEquals(new String[]{"p0"}, searchData.getSqlParameterSource().getParameterNames());
            assertEquals(10L, searchData.getSqlParameterSource().getValue("p0"));
        }
    }
}
