package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.testcommon.TestHelper;
import sbp.com.sbt.dataspace.feather.testmodel.Entity;
import sbp.com.sbt.dataspace.feather.testmodel.Parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.DebugHelper.getSqlQueryResult;
import static sbp.com.sbt.dataspace.feather.testcommon.PropertiesBuilder.propBuilder;

@DisplayName("Debugging Assistant Testing")
@SpringJUnitConfig(EntitiesReadAccessJsonTestConfiguration.class)
@ActiveProfiles({"h2", "h2s1", "tx"})
public class DebugHelperTest {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    TestHelper testHelper;

    @Test
    public void test() {
        Pointer<String> entity1IdPointer = new Pointer<>();
        Pointer<String> parameter1IdPointer = new Pointer<>();
        testHelper.executeInTransaction(() -> {
            entity1IdPointer.object = testHelper.createEntity(Entity.TYPE0, propBuilder()
                    .add(Entity.CODE, "entity1"));
            parameter1IdPointer.object = testHelper.createEntity(Parameter.TYPE0, propBuilder()
                    .add(Entity.NAME, "Parameter 1"));
        });
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("entity1Id", entity1IdPointer.object)
                .addValue("parameter1Id", parameter1IdPointer.object);

        String expectedResult = entity1IdPointer.object + "    |    Entity       |    entity1    |    null\n"
                + parameter1IdPointer.object + "    |    Parameter    |    null       |    Parameter 1\n";

        assertEquals(expectedResult, getSqlQueryResult(namedParameterJdbcTemplate, "select id, type, code, name from f_entity where id in (:entity1Id, :parameter1Id) order by type", sqlParameterSource));
    }
}
