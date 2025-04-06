package sbp.com.sbt.dataspace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class RunTest {

    @Test
    void useTest() {
        File modelDirectoryFile = new File("target/test-classes/model");
        String modelDirectory = String.format("model-directory=%s", modelDirectoryFile.getAbsolutePath()).replace("\\", "/");

        String runLiquibase = "run-liquibase=false";

        File targetDirectoryFile = new File("target/test-classes/targetModel");
        if (!targetDirectoryFile.exists()) {
            targetDirectoryFile.mkdir();
        }

        String[] args = {modelDirectory, runLiquibase};

        ModelRelease.main(args);

        Assertions.assertTrue(new File("target/test-classes/targetModel/pdm.xml").exists());
        Assertions.assertTrue(new File("target/test-classes/targetModel/context-child.properties").exists());
        Assertions.assertTrue(new File("target/test-classes/targetModel/graphql-permissions.json").exists());
        Assertions.assertTrue(new File("target/test-classes/targetModel/jwks.json").exists());
    }

    @Test
    void useTestExecutionTest() {

        {
            ModelRelease.TestMavenModelReleaseConfiguration configuration = ModelRelease.TestMavenModelReleaseConfiguration
                .create(
                    "model",
                    "model-in-service"
                );

            assertThat(configuration.getModelDirectoryName())
                .isEqualTo((new File("target/test-classes/model").getAbsolutePath()).replace("\\", "/"));

            assertThat(configuration.getTargetDirectoryName())
                .isEqualTo((new File("target/test-classes/model-in-service").getAbsolutePath()).replace("\\", "/"));

            File targetDirectoryFile = new File(configuration.getTargetDirectoryName());
            if (!targetDirectoryFile.exists()) {
                targetDirectoryFile.mkdir();
            }

            assertThatCode(() ->
                ModelRelease.testExecution(
                    configuration
                )
            ).doesNotThrowAnyException();

            File file = new File(configuration.getTargetDirectoryName() + "/context-child.properties");
            assertThat(file).exists();
            final Properties contextProperties = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                contextProperties.load(inputStream);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            Set<String> propertyNameSet = contextProperties.keySet().stream().map(Object::toString).collect(Collectors.toSet());

            assertThat(propertyNameSet).doesNotContain("target-directory");
            assertThat(propertyNameSet.stream().anyMatch(name -> name.startsWith("liquibase."))).isFalse();

        }

        {
            ModelRelease.TestMavenModelReleaseConfiguration configuration = ModelRelease.TestMavenModelReleaseConfiguration
                .create("model-no-context");

            assertThatCode(() ->
                ModelRelease.testExecution(
                    configuration
                )
            ).doesNotThrowAnyException();

            assertThat(new File(configuration.getModelDirectoryName() + "/model.xml")).exists();
            assertThat(new File(configuration.getModelDirectoryName() + "/snapshot/pdm.xml")).exists();
        }
    }

}
