package sbp.com.sbt.dataspace;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.model.exception.diff.VersionAlreadyDefinedException;
import com.sbt.pprb.paas.generator.PdmHandler;
import liquibase.change.Change;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.CreateTableChange;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.LiquibaseException;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.SearchPathResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.extension.status.exceptions.StatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static sbp.com.sbt.dataspace.model.ModelConverter.setParameters;

public class ModelGenerateTests {

    private static final String MODEL_FILE_NAME = "model.xml";

    private ModelArtifacts convertModel(String modelName) {
        try {
            File modelDirectory = new File("src/test/resources/models", modelName);

            boolean release = isRelease(new File(modelDirectory, MODEL_FILE_NAME));

            if (release) {
                File destDir = new File("target/out/models/", modelName);
                FileUtils.copyDirectory(modelDirectory, destDir);
                modelDirectory = destDir;
            }

            PluginParameters.Builder builder = PluginParameters.Builder.create()
                .setModel(modelDirectory)
                .setModelName(MODEL_FILE_NAME);

            setParameters(builder, key -> null);

            PluginParameters pluginParameters = builder.build();

            PdmHandler pdmHandler = new PdmHandler(pluginParameters);

            File goalDirectory = new File("target/workModels", modelName);

            pdmHandler.generate(
                goalDirectory,
                goalDirectory,
                null,
                "jpa"
            );

            XmlMapper mapper = new XmlMapper();
            PdmModel pdmModel = mapper.readValue(new File(goalDirectory, "pdm.xml"), PdmModel.class);
            File changeLogXml = new File(goalDirectory, "changelog.xml");

            ChangeLogParser parser =
                ChangeLogParserFactory.getInstance().getParser(
                    changeLogXml.getPath(),
                    new SearchPathResourceAccessor(".")
                );
            DatabaseChangeLog changeLog = parser.parse(
                changeLogXml.getPath(),
                new ChangeLogParameters(),
                new SearchPathResourceAccessor(".")
            );

            return new ModelArtifacts(pdmModel, changeLog);

        } catch (IOException | LiquibaseException ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean isRelease(File modelFile) {
        XmlMapper mapper = new XmlMapper();

        try {
            XmlModel xmlModel = mapper.readValue(modelFile, XmlModel.class);
            return !xmlModel.getVersion().endsWith("-SNAPSHOT");
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static <T extends Change> List<T> getChangesOf(List<ChangeSet> changes, Class<T> clazz) {
        return changes.stream()
            .filter(changeSet -> !changeSet.getChanges().isEmpty() &&
                changeSet.getChanges().stream().anyMatch(change -> clazz.equals(change.getClass())))
            .flatMap(changeSet -> changeSet.getChanges().stream()
                .filter(change -> clazz.equals(change.getClass()))
                .map(change -> (T) change))
            .collect(Collectors.toList());
    }

    @Test
    void newEntitiesTest() {

        ModelArtifacts modelArtifacts = convertModel("newEntities");
        XmlModel xmlModel = modelArtifacts.getPdmModel().getModel();

        Assertions.assertTrue(xmlModel.getClass("SysStatusFields").containsProperty("code"));
        Assertions.assertTrue(xmlModel.getClass("SysStatusFields").containsProperty("reason"));
        Assertions.assertEquals(300, xmlModel.getClass("SysStatusFields").getProperty("reason").getLength());

        XmlModelClass myClass = xmlModel.getClass("MyClass");

        Assertions.assertTrue(myClass.containsProperty("statusForService"));
        Assertions.assertTrue(myClass.containsProperty("statusForUser"));
    }

    @Test
    void initialFillTest() {
        String message =
            Assertions.assertThrows(StatusException.class, () -> convertModel("initialFill")).getMessage();
        Assertions.assertTrue(message.contains("specified for class MyClass in groups: [service,user]"));
    }

    @Test
    void inheritanceTest() {
        ModelArtifacts modelArtifacts = convertModel("inheritance");

        XmlModel model = modelArtifacts.getPdmModel().getModel();

        XmlModelClass myAbstractClass = model.getClass("MyAbstractClass");
        Assertions.assertTrue(myAbstractClass.containsProperty("statusForService"));

        XmlModelClass myClass = model.getClass("MyClass");
        Assertions.assertFalse(myClass.containsProperty("statusForService"));

        XmlModelClass childClass = model.getClass("ChildClass");
        Assertions.assertFalse(childClass.containsProperty("statusForService"));

        XmlModelClass classWithUserGroup = model.getClass("ClassWithUserGroup");
        Assertions.assertFalse(classWithUserGroup.containsProperty("statusForService"));
        Assertions.assertTrue(classWithUserGroup.containsProperty("statusForUser"));

        DatabaseChangeLog changeLog = modelArtifacts.getChangeLog();

        CreateTableChange createSingletonClass = getChangesOf(changeLog.getChangeSets(), CreateTableChange.class)
            .stream()
            .filter(it -> Objects.equals(it.getTableName(), "T_SINGLETABLECLASS"))
            .findFirst()
            .orElseThrow();
        Assertions.assertTrue(
            createSingletonClass.getColumns().stream()
                .anyMatch(it -> Objects.equals(it.getName(), "STATUSFORUSER_CODE"))
        );
        Assertions.assertTrue(
            createSingletonClass.getColumns().stream()
                .anyMatch(it -> Objects.equals(it.getName(), "STATUSFORSERVICE_CODE"))
        );

        List<AddColumnChange> addColumnSingleTableChanges = getChangesOf(changeLog.getChangeSets(), AddColumnChange.class)
            .stream()
            .filter(it -> Objects.equals(it.getTableName(), "T_SINGLETABLECLASS"))
            .toList();

        Assertions.assertEquals(2, addColumnSingleTableChanges.size());
        Assertions.assertTrue(
            addColumnSingleTableChanges.stream()
                .anyMatch(it -> it.getColumns().stream()
                    .anyMatch(addColumnConfig -> Objects.equals(addColumnConfig.getName(), "STATUSFORPLATFORM_CODE")))
        );
        Assertions.assertTrue(
            addColumnSingleTableChanges.stream()
                .anyMatch(it -> it.getColumns().stream()
                    .anyMatch(addColumnConfig -> Objects.equals(addColumnConfig.getName(), "STATUSFORPLATFORM_REASON")))
        );
    }

    @Test
    void emptyChangesTest() {
        ModelArtifacts modelArtifactsSameVersion = convertModel("emptyChangesSameVersion");
        Assertions.assertEquals("0.0.1", modelArtifactsSameVersion.getPdmModel().getModel().getVersion());

        ModelArtifacts modelArtifactsNextVersion = convertModel("emptyChangesNextVersion");
        Assertions.assertEquals("0.0.2", modelArtifactsNextVersion.getPdmModel().getModel().getVersion());

        Assertions.assertThrows(VersionAlreadyDefinedException.class, () -> convertModel("emptyChangesSameVersionWithChanges"));
    }
}
