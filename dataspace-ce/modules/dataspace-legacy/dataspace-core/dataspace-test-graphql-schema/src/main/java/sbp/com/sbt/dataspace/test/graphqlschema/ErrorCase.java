package sbp.com.sbt.dataspace.test.graphqlschema;

import graphql.ExecutionInput;
import org.junit.jupiter.api.function.Executable;

import java.util.Map;

import static graphql.ExecutionInput.newExecutionInput;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;
import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.getStringFromResource;

/**
 * Case with errors
 */
abstract class ErrorCase extends TestCase {

    @Override
    void createEntities() {
        // Никаких действий не требуется
    }

    @Override
    Executable getTestCode(String name) {
        ExecutionInput.Builder executionInputBuilder = newExecutionInput()
                .query(getStringWithProperties(getStringFromResource(ErrorCase.class, getClass().getSimpleName() + "_" + name + "_request.graphql")));
        String variablesJson = getJson(name, VARIABLES_TYPE);
        if (variablesJson != null) {
            executionInputBuilder.variables(wrap(() -> OBJECT_MAPPER.readValue(variablesJson, Map.class)));
        }
        return () -> assertFalse(graphQL.execute(executionInputBuilder.build()).getErrors().isEmpty());
    }
}
