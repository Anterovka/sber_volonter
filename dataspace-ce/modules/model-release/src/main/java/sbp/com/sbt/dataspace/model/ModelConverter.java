package sbp.com.sbt.dataspace.model;

import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.pprb.paas.generator.PdmHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

public class ModelConverter {

    public interface ParameterResolver {
        String resolve(String key);
    }

    private ModelConverter() {
        //Utility class
    }

    public static void execute(File modelDirectory,
                               ParameterResolver parameterResolver,
                               String modelFileName,
                               String goalDirectoryName) {

        PluginParameters.Builder builder = PluginParameters.Builder.create()
            .setModel(modelDirectory)
            .setModelName(modelFileName);

        setParameters(builder, parameterResolver);

        PluginParameters pluginParameters = builder.build();

        PdmHandler pdmHandler = new PdmHandler(pluginParameters);

        File goalDirectory = new File(modelDirectory, goalDirectoryName);
        File snapshotDirectory = new File(modelDirectory, GoalDirectory.snapshot());

        if (snapshotDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(snapshotDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        pdmHandler.generate(
            goalDirectory,
            goalDirectory,
            null,
            "jpa"
        );
    }

    public static void setParameters(PluginParameters.Builder builder, ParameterResolver parameterResolver) {
        builder
            .setAggregateValidations(
                readParameter(
                    parameterResolver,
                    "validateAggregates",
                    value -> Objects.isNull(value) || Boolean.parseBoolean(value)
                )
            )
            .setMaxDBObjectNameLength(
                readParameter(
                    parameterResolver,
                    "maxDBObjectNameLength",
                    value -> Objects.isNull(value) ? 63 : Integer.parseInt(value)
                )
            )
            .setDropRemovedItems(
                readParameter(
                    parameterResolver,
                    "dropUnusedSchemaItems",
                    value -> Objects.nonNull(value) && Boolean.parseBoolean(value)
                )
            )
            .setOptimizeChangelog(
                readParameter(
                    parameterResolver,
                    "optimizeChangelog",
                    value -> Objects.nonNull(value) && Boolean.parseBoolean(value)
                )
            )
            .setDisableGenerateOracleLiquibase(
                readParameter(
                    parameterResolver,
                    "disableGenerateOracleLiquibase",
                    value -> Objects.isNull(value) || Boolean.parseBoolean(value)
                )
            )
            .setMaxDictionaryFileSize(
                readParameter(
                    parameterResolver,
                    "maxDictionaryFileSize",
                    value -> Objects.isNull(value) ? "524288" : value
                )
            )
            .setAllowDictionaryPacket(readParameter(
                parameterResolver,
                "allowDictionaryPacket",
                value -> Objects.isNull(value) || Boolean.parseBoolean(value)
            ))
            .setEnableHistoryGenerators(
                readParameter(
                    parameterResolver,
                    "enableHistoryGenerators",
                    value -> Objects.nonNull(value) && Boolean.parseBoolean(value)
                )
            )
            .setDisableAggregateRootReferenceCheck(
                readParameter(
                    parameterResolver,
                    "disableAggregateRootReferenceCheck",
                    value -> Objects.nonNull(value) && Boolean.parseBoolean(value)
                )
            );
    }

    private static <T> T readParameter(ParameterResolver parameterResolver,
                                       String key,
                                       Function<String, T> converter) {
        String parameterValue = parameterResolver.resolve(key);
        return converter.apply(parameterValue);
    }
}
