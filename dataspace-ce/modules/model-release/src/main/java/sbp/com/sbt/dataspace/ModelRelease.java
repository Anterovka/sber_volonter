package sbp.com.sbt.dataspace;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sbp.com.sbt.dataspace.liquibase.LBScriptRunner;
import sbp.com.sbt.dataspace.model.GoalDirectory;
import sbp.com.sbt.dataspace.model.ModelConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class ModelRelease {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRelease.class);

    private static final String CONTEXT_CHILD_PROPERTIES_FILE = "context-child.properties";
    private static final String GRAPHQL_JSON_FILE = "graphql-permissions.json";
    private static final String JWKS_JSON_FILE = "jwks.json";
    private static final String MODEL_FILE_NAME = "model.xml";
    private static final String PDM_FILE_NAME = "pdm.xml";

    private static final String ARG_RUN_LIQUIBASE = "run-liquibase";
    private static final String ARG_MODEL_DIRECTORY = "model-directory";
    private static final String ARG_TARGET_DIRECTORY = "target-directory";

    private static final String MODEL_CONVERTER_PARAMETERS_PREFIX = "dataspace.model-release.converter.parameters.";
    private static final String LIQUIBASE_CONVERTER_PARAMETERS_PREFIX = "liquibase.";

    public static void main(String[] args) {
        final Properties argsProperties = convertArgsToProperties(args);
        execute(
            argsProperties,
            false
        );
    }

    private static void execute(Properties argsProperties,
                                boolean allowContextPropertiesFileMissing) {
        boolean runLiquibase = Boolean.parseBoolean(argsProperties.getProperty(ARG_RUN_LIQUIBASE, "false"));

        final File modelDirectory = readAndCheckModelDirectoryProperty(argsProperties);

        final Properties contextProperties = readContextProperties(
            modelDirectory,
            !runLiquibase && allowContextPropertiesFileMissing
        );

        final File targetDirectory = readAndCheckTargetDirectoryProperty(argsProperties, contextProperties);

        final String goalDirectoryName = defineGoalDirectoryName(modelDirectory);

        ModelConverter
            .execute(
                modelDirectory,
                key -> contextProperties.getProperty(MODEL_CONVERTER_PARAMETERS_PREFIX + key),
                MODEL_FILE_NAME,
                goalDirectoryName
            );

        if (runLiquibase) {
            LBScriptRunner.execute(
                contextProperties,
                modelDirectory.getAbsolutePath() + "/" + goalDirectoryName + "/db"
            );
        }

        storeFilesToTarget(
            modelDirectory,
            goalDirectoryName,
            targetDirectory,
            contextProperties
        );

    }

    private static File readAndCheckModelDirectoryProperty(Properties properties) {
        final String modelDirectoryPropertyValue = properties.getProperty(ARG_MODEL_DIRECTORY);

        if (Objects.isNull(modelDirectoryPropertyValue)) {
            throw new IllegalArgumentException("Model directory(parameter model-directory) doesn't set.");
        }

        File modelDirectory = new File(modelDirectoryPropertyValue);

        if (modelDirectory.exists() && modelDirectory.isDirectory()) {
            return modelDirectory;
        }

        throw new IllegalArgumentException(
            String.format(
                "Model directory %s should be a valid directory.",
                modelDirectoryPropertyValue
            )
        );
    }

    private static File readAndCheckTargetDirectoryProperty(Properties argsProperties,
                                                            Properties contextProperties) {

        final String targetDirectoryPropertyValue = Optional
            .ofNullable(argsProperties.getProperty(ARG_TARGET_DIRECTORY))
            .orElseGet(() -> contextProperties.getProperty(ARG_TARGET_DIRECTORY));

        if (Objects.isNull(targetDirectoryPropertyValue)) {
            return null;
        }

        File targetDirectory = new File(targetDirectoryPropertyValue);

        if (targetDirectory.exists() && targetDirectory.isDirectory()) {
            return targetDirectory;
        }

        throw new IllegalArgumentException(
            String.format(
                "Target directory(parameter target-directory='%s') should be a valid directory.",
                targetDirectoryPropertyValue
            )
        );

    }

    private static String defineGoalDirectoryName(File modelDirectory) {
        File modelFile = new File(modelDirectory, MODEL_FILE_NAME);

        if (modelFile.exists() && modelFile.isFile()) {
            return GoalDirectory.nameByModel(modelFile);
        }

        throw new IllegalArgumentException(String.format("Model file %s doesn't find or is not a file.", modelFile));
    }

    private static void storeFilesToTarget(File modelDirectory,
                                           String goalDirectoryName,
                                           File targetDirectory,
                                           Properties contextProperties) {
        if (targetDirectory == null) {
            return;
        }
        try {
            FileUtils.copyFile(
                new File(modelDirectory, "/" + goalDirectoryName + "/" + PDM_FILE_NAME),
                new File(targetDirectory, PDM_FILE_NAME)
            );

            saveFilteredContextProperties(
                new File(targetDirectory, CONTEXT_CHILD_PROPERTIES_FILE),
                contextProperties
            );

            copySecurityFiles(modelDirectory, targetDirectory);

            LOGGER.info("Files copied successfully.");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void copySecurityFiles(File source, File destination) {
        Stream.of(GRAPHQL_JSON_FILE, JWKS_JSON_FILE).forEach(fileName -> {
            File file = new File(source, fileName);
            if (file.exists() && file.isFile()) {
                try {
                    FileUtils.copyFile(
                        file,
                        new File(destination, fileName)
                    );
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    private static void saveFilteredContextProperties(File contextPropertiesFile,
                                                      Properties contextProperties) {
        Properties properties = new Properties();
        contextProperties.forEach((key, value) -> {

            final String keyString = key.toString();

            if (Objects.isNull(value)
                || keyString.equals(ARG_TARGET_DIRECTORY)
                || keyString.startsWith(MODEL_CONVERTER_PARAMETERS_PREFIX)
                || keyString.startsWith(LIQUIBASE_CONVERTER_PARAMETERS_PREFIX)
            ) {
                return;
            }

            properties.setProperty(keyString, value.toString());
        });

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(contextPropertiesFile), StandardCharsets.UTF_8)) {
            properties.store(writer, null);
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    private static Properties convertArgsToProperties(String[] args) {
        final String argsLine = String.join("\n", args);

        final Properties argLineProperties = new Properties();

        try (InputStream inputStream = new ByteArrayInputStream(argsLine.getBytes(StandardCharsets.UTF_8))) {
            argLineProperties.load(inputStream);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return argLineProperties;
    }

    private static Properties readContextProperties(File modelDirectory,
                                                    boolean allowFileMissing) {
        File file = new File(modelDirectory + "/" + CONTEXT_CHILD_PROPERTIES_FILE);

        if (file.exists() && file.isFile()) {
            final Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            return properties;
        }

        if (allowFileMissing) {
            return new Properties();
        }

        throw new IllegalStateException(
            String.format(
                "Cannot find file '%s' in model directory '%s'",
                CONTEXT_CHILD_PROPERTIES_FILE,
                modelDirectory
            )
        );

    }

    /**
     * Testing model convertor based on maven test directories structure conversion
     */
    public static void testExecution(TestMavenModelReleaseConfiguration configuration) {
        final Properties properties = new Properties();
        properties.setProperty(
            ARG_RUN_LIQUIBASE,
            "false"
        );
        properties.setProperty(
            ARG_MODEL_DIRECTORY,
            configuration.getModelDirectoryName()
        );
        if (Objects.nonNull(configuration.getTargetDirectoryName())) {
            properties.setProperty(
                ARG_TARGET_DIRECTORY,
                configuration.getTargetDirectoryName()
            );

            if (configuration.createTargetDirectory) {
                File file = new File(configuration.getTargetDirectoryName());
                file.mkdirs();
            }

        }
        execute(
            properties,
            true
        );
    }

    public static class TestMavenModelReleaseConfiguration {

        private static final String TEST_CLASS_DIRECTORY_NAME = "target/test-classes";

        private final String modelDirectoryName;
        private final String targetDirectoryName;
        private final boolean createTargetDirectory;

        private TestMavenModelReleaseConfiguration(String modelDirectoryName,
                                                   String targetDirectoryName,
                                                   boolean createTargetDirectory) {
            this.modelDirectoryName = modelDirectoryName;
            this.targetDirectoryName = targetDirectoryName;
            this.createTargetDirectory = createTargetDirectory;
        }

        public String getModelDirectoryName() {
            return modelDirectoryName;
        }

        public String getTargetDirectoryName() {
            return targetDirectoryName;
        }

        private static String toAbsolutePath(String path) {

            if (Objects.isNull(path)) {
                return null;
            }

            return (new File(TEST_CLASS_DIRECTORY_NAME + "/" + path).getAbsolutePath()).replace("\\", "/");
        }

        private static TestMavenModelReleaseConfiguration create(String modelDirectorySortName,
                                                                String targetDirectorySortName,
                                                                boolean createTargetDirectory
        ) {
            return new TestMavenModelReleaseConfiguration(
                toAbsolutePath(
                    Objects.requireNonNull(
                        modelDirectorySortName,
                        "modelDirectorySortName cannot be null"
                    )
                ),
                toAbsolutePath(targetDirectorySortName),
                createTargetDirectory
            );
        }

        public static TestMavenModelReleaseConfiguration create(String modelDirectorySortName,
                                                                String targetDirectorySortName
        ) {
            return create(
                modelDirectorySortName,
                targetDirectorySortName,
                false
            );
        }

        public static TestMavenModelReleaseConfiguration createWithForceMkDirTargetDirectory(String modelDirectorySortName,
                                                                                             String targetDirectorySortName
        ) {
            return create(
                modelDirectorySortName,
                targetDirectorySortName,
                true
            );
        }

        public static TestMavenModelReleaseConfiguration create(String modelDirectorySortName) {
            return create(
                modelDirectorySortName,
                null
            );
        }

    }

}
