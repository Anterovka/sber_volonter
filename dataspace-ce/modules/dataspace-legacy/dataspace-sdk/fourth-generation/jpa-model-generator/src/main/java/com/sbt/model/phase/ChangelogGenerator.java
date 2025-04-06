package com.sbt.model.phase;

import com.google.common.base.Strings;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.liquibase.UnusedSchemaItemsHandler;
import com.sbt.liquibase.diff.handler.ClassHandler;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerDeprecated;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerNew;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerNon;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerRemoved;
import com.sbt.liquibase.diff.handler.hclass.ClassHandlerUpdate;
import com.sbt.liquibase.diff.handler.other.ForeignKeyHandlerNew;
import com.sbt.liquibase.diff.handler.other.ForeignKeyHandlerRemoved;
import com.sbt.mg.ElementState;
import com.sbt.mg.Helper;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.exception.optimizechangelog.CustomChangelogNotFoundException;
import com.sbt.model.phase.serviceitems.H2DbFunctions;
import com.sbt.model.utils.ChangelogGeneratorChangelogCorrector;
import com.sbt.model.utils.ChangelogUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import sbp.sbt.model.config.snowflake.core.ShuffledUIDGenerator;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.Helper.wrap;
import static com.sbt.mg.Helper.writeText;
import static com.sbt.mg.ModelHelper.fkDeleteCascadeChanged;
import static com.sbt.mg.ModelHelper.fkGeneratedChangedFromFalse;
import static com.sbt.mg.ModelHelper.fkGeneratedChangedFromTrue;
import static com.sbt.mg.jpa.JpaConstants.CUSTOM_CHANGELOG_FILENAME;
import static com.sbt.mg.jpa.JpaConstants.DBMS_PROPERTIES_FILENAME;
import static com.sbt.model.utils.ChangelogUtils.AUTHOR_CUSTOM_CHANGESET;

public class ChangelogGenerator implements Phase<Void, ModelParameters> {

    private static final String CHANGELOG_TEMPLATE = getTemplate("/generate/changelog/changelog.template");
    private static final String ROOT_CHANGELOG_TEMPLATE = getTemplate("/generate/changelog/rootChangelog.template");
    private static final String DBMS_TEMPLATE = getTemplate("/generate/changelog/dbms-properties.template");
    private static final String CHECK_ENABLE_INTERMEDIARY_TEMPLATE = getTemplate("/generate/changelog/checkEnableIntermediaryRelease.changeSet.template");
    private static final String CHECK_DEPLOY_PREVIOUS_VERSION_TEMPLATE = getTemplate("/generate/changelog/checkDeployPreviousVersion.changeSet.template");
    private static final String CHECK_DEPLOY_DBMS_ORACLE_TEMPLATE = getTemplate("/generate/changelog/checkDeployDbmsOracle.changeSet.template");

    private static final String CHECK_DEPLOY_PREVIOUS_VERSION_WITHOUT_ORACLE_TEMPLATE = getTemplate("/generate/changelog/checkDeployPreviousVersion.changeSet.withoutOracle.template");

    @Override
    @Deprecated
    public Void execute(ModelParameters param) {
        throw new UnsupportedOperationException();
    }

    public Void executeGeneration(ModelParameters modelParameters, PluginParameters pluginParameters) {

        File resourcesDirectory = Helper.createDirectory(modelParameters.getGoalGenDirectory());

        File dbDirectory = Helper.createDirectory(resourcesDirectory, "db");

        XmlModel model = modelParameters.getModel();

        boolean isIntermediaryBuild = pluginParameters.isIntermediaryBuild();
        String resultChangelogFileName = JpaConstants.CHANGELOG_FILENAME;

        String includeFiles = String.format("<include file=\"%s/%s\" relativeToChangelogFile=\"true\"/>",
            model.getModelName(), resultChangelogFileName);

        writeText(Helper.createFile(dbDirectory, resultChangelogFileName),
            ROOT_CHANGELOG_TEMPLATE.replace("${includeFiles}", includeFiles));

        writeText(Helper.createFile(dbDirectory, DBMS_PROPERTIES_FILENAME),
            DBMS_TEMPLATE.replace("${modelName}", model.getModelName())
                .replace("${checkEnableIntermediaryRelease}", isIntermediaryBuild ?
                    CHECK_ENABLE_INTERMEDIARY_TEMPLATE.replace("${modelName}", model.getModelName()) : "")
                .replace("${checkDeployDbmsOracle}", pluginParameters.isDisableGenerateOracleLiquibase() ?
                    CHECK_DEPLOY_DBMS_ORACLE_TEMPLATE.replace("${modelName}", model.getModelName()) : "")
        );

        File outProjectDb = modelParameters.getModelDirectory();

        // Carefully, without creating anything extra, check if there are any past model releases
        File changelogBase;
        File userLocalModelDir = Helper.getFile(outProjectDb, JpaConstants.userLocalModelDir());
        // If there's nothing along the path, then it means that there wasn't a previous release either.
        if (!pluginParameters.isLocalDeploy() || !userLocalModelDir.exists() || !userLocalModelDir.isDirectory()) {
            changelogBase = Helper.getFile(outProjectDb, JpaConstants.userModelDir());
        } else {
            File localChangelogBase = Helper.createDirectory(outProjectDb, JpaConstants.userLocalModelDir());
            File localGeneralChangelog = Helper.getFile(localChangelogBase, resultChangelogFileName);

            if (pluginParameters.isLocalDeploy() && localGeneralChangelog.exists()) {
                changelogBase = localChangelogBase;
            } else {
                changelogBase = Helper.createDirectory(outProjectDb, JpaConstants.userModelDir());
            }
        }

// previous changelog (from the catalogue of model or localmodel)
        File previousChangelog = Helper.getFile(changelogBase, JpaConstants.CHANGELOG_FILENAME);
        if (isIntermediaryBuild) {
            File intermediaryChangelog = Helper.getFile(changelogBase, JpaConstants.CHANGELOG_BUILD_FILENAME);
            if (intermediaryChangelog.exists()) {
                previousChangelog = intermediaryChangelog;
            }
        }

        File generalChangelog = previousChangelog;

        String generalChangelogText = null;

        if (generalChangelog.exists()) {
            String changelogText = wrap(() -> FileUtils.readFileToString(generalChangelog, StandardCharsets.UTF_8));

            generalChangelogText = modificationChangelog(modelParameters, changelogText);

            if (pluginParameters.isOptimizeChangelog()) {
                if (changelogText.contains(AUTHOR_CUSTOM_CHANGESET)) {
                    if (!pluginParameters.isSkipCustomChangelogCheck()) {
                        File customChangelogFile = Helper.getFile(outProjectDb, CUSTOM_CHANGELOG_FILENAME);
                        if (!customChangelogFile.exists()) {
                            throw new CustomChangelogNotFoundException();
                        }
                    }
                }

                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
                Date date = new Date();
                //nameResult = save_changelog_before_dd-MM-yyyy_HHmmss.xml
                String nameResultFile = String.format("save_%s_before_%s.xml", "changelog", formatter.format(date));
                File arxivPrevChangelog = Helper.getFile(changelogBase, nameResultFile);
                wrap(() -> FileUtils.copyFile(generalChangelog, arxivPrevChangelog));
            }
        }

//main changelog from db\model-name\directory
        File initDirectory = Helper.createDirectory(dbDirectory, model.getModelName());
        File changelogXml = Helper.createFile(initDirectory, resultChangelogFileName);
        modelParameters.setChangelogDirectory(initDirectory);

        String text = generateChangelogText(modelParameters, pluginParameters);

        File rollbackChangelog = Helper.createFile(dbDirectory, JpaConstants.ROLLBACK_CHANGELOG_FILENAME);
        String rollbackChangelogText = text.replace("${includes}", "<include file=\"dbms-properties.xml\" relativeToChangelogFile=\"true\"/>");
        writeText(rollbackChangelog, rollbackChangelogText);

        text = text.replace("${includes}", "");

        if (generalChangelogText != null
            && !pluginParameters.isOptimizeChangelog()) {

            int indexOf = generalChangelogText.indexOf("</databaseChangeLog>");

            if (isEmptyRelease(modelParameters)) {
                text = generalChangelogText;
            } else {
                text = generalChangelogText.substring(0, indexOf)
                    + "    "
                    + text.substring(text.indexOf("<changeSet"), text.indexOf("</databaseChangeLog>"))
                    + generalChangelogText.substring(indexOf);
            }

        }

        writeText(changelogXml, text);

        return null;
    }

    private boolean isEmptyRelease(ModelParameters modelParameters) {
        return !isModelChanged(modelParameters) && isSameVersion(modelParameters);
    }

    private boolean isSameVersion(ModelParameters modelParameters) {
        return Objects.equals(modelParameters.getVersion(), modelParameters.getCurrentVersion());
    }

    private boolean isModelChanged(ModelParameters modelParameters) {
        return modelParameters.isModelChanged();
    }

    private String getCheckDeployPreviousVersionPrecondition(String previousVersion, String modelName, boolean disableGenerateOracleLiquibase) {
        if (StringUtils.isNotBlank(previousVersion)) {
            String templateCheckDeploy = CHECK_DEPLOY_PREVIOUS_VERSION_TEMPLATE;
            if (disableGenerateOracleLiquibase) {
                templateCheckDeploy = CHECK_DEPLOY_PREVIOUS_VERSION_WITHOUT_ORACLE_TEMPLATE;
            }
            return templateCheckDeploy
                .replace("${modelName}", modelName)
                .replace("${previousVersion}", previousVersion);
        }
        return "";
    }

    private void generateChangelogClassChanges(StringBuilder changesSB,
                                               MutableLong index,
                                               XmlModelClass modelClass,
                                               ModelParameters modelParameters,
                                               PluginParameters pluginParameters) {
        index.increment();
        MutableInt indexIndex = new MutableInt(0);
        MutableInt indexCollection = new MutableInt(0);

        List<ClassHandler> classHandlers = new ArrayList<>();

        if (modelParameters.containsObjectInDiff(ElementState.NEW, modelClass)) {
            classHandlers.add(new ClassHandlerNew(index, indexIndex, indexCollection));
        } else if (modelParameters.containsObjectInDiff(ElementState.DEPRECATED, modelClass)) {
            classHandlers.add(new ClassHandlerNon(index, indexIndex, indexCollection));
            classHandlers.add(new ClassHandlerDeprecated(indexCollection, indexIndex));
            if (modelParameters.containsObjectInDiff(ElementState.REMOVED, modelClass)) {
                classHandlers.add(new ClassHandlerRemoved(indexCollection, indexIndex));
            }
        } else if (modelParameters.containsObjectInDiff(ElementState.REMOVED, modelClass)) {
            classHandlers.add(new ClassHandlerNon(index, indexIndex, indexCollection));
            classHandlers.add(new ClassHandlerRemoved(indexCollection, indexIndex));
        } else if (modelParameters.containsObjectInDiff(ElementState.UPDATED, modelClass)) {
            //The original text does not contain any Russian words or phrases to be translated into English. Therefore, no replacement is needed.
            // when switching to inheritance strategy (when disabling backward compatibility check),
            //However, this change in order may break other logic (so far in theory).
            classHandlers.add(new ClassHandlerNon(index, indexIndex, indexCollection));
            classHandlers.add(new ClassHandlerUpdate(index, indexCollection, indexIndex));
        } else {
            classHandlers.add(new ClassHandlerNon(index, indexIndex, indexCollection));
        }

        classHandlers.forEach(classHandler -> classHandler.handle(changesSB, index, modelClass, modelParameters, pluginParameters));
    }

    public String modificationChangelog(ModelParameters modelParameters, String generalChangelogText) {
        List<ModelGenerate> executingModelGenerate = modelParameters.getExecutingModelGenerate();
        for (ModelGenerate iModelGenerate : executingModelGenerate) {
            generalChangelogText = iModelGenerate.modificateChangelog(generalChangelogText);
        }
        if (Objects.nonNull(modelParameters.getPdmModel())
            && Objects.nonNull(modelParameters.getPdmModel().getMetaInformation())
            && !Objects.equals(Boolean.TRUE, Boolean.valueOf(modelParameters.getPdmModel().getMetaInformation().getAddedPrecondition()))) {
            generalChangelogText = addPrecondition(generalChangelogText);
            modelParameters.getPdmModel().getMetaInformation().setAddedPrecondition(Boolean.TRUE.toString());
        }
        return generalChangelogText;
    }

    private String addPrecondition(String changelogText) {

        int indexChangeSetOpen = changelogText.indexOf("<changeSet");
        if (indexChangeSetOpen > 0) {
            int indexChangeSetClosed = changelogText.indexOf("</changeSet", indexChangeSetOpen);
            changelogText = new ChangelogGeneratorChangelogCorrector().rebuildChangelogText(changelogText, indexChangeSetOpen, indexChangeSetClosed);
        }
        return changelogText;
    }

    public String generateChangelogText(ModelParameters modelParameters, PluginParameters pluginParameters) {
        StringBuilder changesSB = new StringBuilder();
        String projectVersion = modelParameters.getVersion();
        XmlModel model = modelParameters.getModel();

        singleTableDiffHandler(modelParameters);


        MutableLong index = new MutableLong(Long.parseLong(ShuffledUIDGenerator.getInstance().getNextValue()));

        // Auxiliary functions should be placed before all others.
        H2DbFunctions.addToDb(changesSB, modelParameters.getPdmModel());

        Predicate<XmlModelClassProperty> filterForNewForeignKey = xmlModelClassProperty ->
            (fkGeneratedChangedFromFalse(xmlModelClassProperty) || fkDeleteCascadeChanged(xmlModelClassProperty))
                && xmlModelClassProperty.isFkGenerated()
                && Objects.nonNull(xmlModelClassProperty.getFkName());

        Predicate<XmlModelClassProperty> filterForRemovedForeignKey = xmlModelClassProperty ->
            (fkGeneratedChangedFromTrue(xmlModelClassProperty) && !xmlModelClassProperty.isFkGenerated())
                || fkDeleteCascadeChanged(xmlModelClassProperty);

        // The foreign key deletion is performed before the other scripts (otherwise, if indexes that are present on the foreign key field are deleted,
        // on h2 we will get an error because it uses this index under the hood as a foreign key)
        model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getPropertiesAsList().stream().anyMatch(xmlModelClassProperty ->
                xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_GENERATED_TAG) || xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_DELETE_CASCADE_TAG)))
            .forEach(modelClass -> {
                index.increment();
                MutableInt indexFk = new MutableInt(0);
                modelClass.getPropertiesAsList().stream()
                    .filter(filterForRemovedForeignKey)
                    .forEach(xmlModelClassProperty -> new ForeignKeyHandlerRemoved().handle(changesSB, index, xmlModelClassProperty, modelParameters, pluginParameters, indexFk));
            });

        model.getClassesAsList().forEach(modelClass -> generateChangelogClassChanges(changesSB, index, modelClass, modelParameters, pluginParameters));

        model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getPropertiesAsList().stream().anyMatch(xmlModelClassProperty ->
                xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_GENERATED_TAG) || xmlModelClassProperty.propertyChanged(XmlModelClassProperty.FK_DELETE_CASCADE_TAG)))
            .forEach(modelClass -> {
                index.increment();
                MutableInt indexFk = new MutableInt(0);
                modelClass.getPropertiesAsList().stream()
                    .filter(filterForNewForeignKey)
                    .forEach(xmlModelClassProperty -> new ForeignKeyHandlerNew().handle(changesSB, index, xmlModelClassProperty, modelParameters, pluginParameters, indexFk));
            });

        if (!pluginParameters.isDeprecateDeletedItems() && (pluginParameters.isDropUnusedSchemaItems() || pluginParameters.isDropDeletedItemsImmediately())) {
            UnusedSchemaItemsHandler.handle(changesSB, index, modelParameters, pluginParameters);
        }

        modelParameters.getExecutingModelGenerate().stream()
            .sorted(Comparator.comparingInt(ModelGenerate::getPriority))
            .forEach(iModelGenerate -> {
                String s = iModelGenerate.addDataToDB(index, model, modelParameters);

                if (!Strings.isNullOrEmpty(s)) {
                    changesSB.append(s);
                }
            });

        String customChangelogText = new ChangelogUtils(pluginParameters).getCustomChangelogText();
        if (!Strings.isNullOrEmpty(customChangelogText)) {
            changesSB.append(customChangelogText);
        }

        String checkDeployPreviousVersionPrecondition = "";
        if (pluginParameters.isOptimizeChangelog()) {
            checkDeployPreviousVersionPrecondition = getCheckDeployPreviousVersionPrecondition(modelParameters.getPreviousModelVersion(),
                model.getModelName(),
                pluginParameters.isDisableGenerateOracleLiquibase());
        }

        return CHANGELOG_TEMPLATE
            .replace("${changes}", changesSB.toString())
            .replace("${modelName}", model.getModelName())
            .replace("${model}", model.getModelName())
            .replace("${version}", pluginParameters.isOptimizeChangelog() ? projectVersion + "-optimize" : projectVersion)
            .replace("${checkDeployPreviousVersion}", checkDeployPreviousVersionPrecondition);
    }

    private void singleTableDiffHandler(ModelParameters parameters) {
        List<XmlModelClass> newClasses = parameters.getObjectByType(ElementState.NEW, XmlModelClass.class);

        newClasses.stream()
            .filter(modelClass -> !modelClass.isBaseClassMark())
            .filter(modelClass -> modelClass.getStrategy() == ClassStrategy.SINGLE_TABLE)
            .forEach(modelClass -> {
                parameters.dropFromCategory(ElementState.NEW, modelClass);
                modelClass.getIndices().forEach(index -> parameters.addDiffObject(ElementState.NEW, index));
            });

        Map<XmlModelClass, List<XmlModelClassProperty>> statusPropertiesInBaseClass = new HashMap<>();

        List<XmlModelClassProperty> newProperties = parameters.getObjectByType(ElementState.NEW, XmlModelClassProperty.class);
        // grouping properties from ancestors to parent, to leave only one
        newProperties.stream()
            .filter(property -> property.getModelClass().getStrategy() == ClassStrategy.SINGLE_TABLE)
            .filter(property -> "Status".equals(property.getType()))
            .forEach(property -> {
                final XmlModelClass classOwner = ModelHelper.getBaseClass(property.getModelClass());
                statusPropertiesInBaseClass.putIfAbsent(classOwner, new ArrayList<>());
                statusPropertiesInBaseClass.get(classOwner).add(property);
            });

        List<XmlIndex> newIndexes = parameters.getObjectByType(ElementState.NEW, XmlIndex.class);

        statusPropertiesInBaseClass.forEach((baseClass, properties) ->
            properties.forEach(newStatusProperty -> {
                final List<XmlModelClassProperty> equalNameStatus = properties.stream()
                    .filter(property -> Objects.equals(newStatusProperty.getName(), property.getName()))
                    .collect(Collectors.toList());

                // Deletes all properties that match by name
                for (int i = 1; i < equalNameStatus.size(); ++i) {
                    final XmlModelClassProperty removingProperty = equalNameStatus.get(i);
                    newIndexes.stream().filter(xmlIndex -> xmlIndex.getProperties().size() == 1
                            && xmlIndex.getProperties().get(0).getProperty().equals(removingProperty))
                        .forEach(xmlIndex -> parameters.dropFromCategory(ElementState.NEW, xmlIndex));

                    parameters.dropFromCategory(ElementState.NEW, removingProperty);
                }
            }));
    }
}
