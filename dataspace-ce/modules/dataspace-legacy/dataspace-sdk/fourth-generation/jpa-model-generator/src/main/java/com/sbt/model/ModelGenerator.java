package com.sbt.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.dataspace.pdm.TargetFileHolder;
import com.sbt.dataspace.pdm.xml.XmlImportModel;
import com.sbt.dataspace.pdm.xml.XmlMetaInformation;
import com.sbt.dataspace.pdm.xml.XmlRootModel;
import com.sbt.dataspace.pdm.xml.XmlSourceModels;
import com.sbt.mg.ElementState;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.XmlCciIndex;
import com.sbt.mg.data.model.XmlEmbeddedList;
import com.sbt.mg.data.model.XmlEmbeddedProperty;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassEnum;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlQuery;
import com.sbt.mg.data.model.history.XmlHistoryProperty;
import com.sbt.mg.data.model.history.XmlHistoryVersion;
import com.sbt.mg.data.model.history.XmlHistoryVersions;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedColumn;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedSchemaItems;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedTable;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.exception.optimizechangelog.NeedToDoChangelogOptimizationException;
import com.sbt.model.phase.ChangelogGenerator;
import com.sbt.model.phase.CheckModel;
import com.sbt.model.utils.AggregateScopeReferenceValidationSqlGenerator;
import com.sbt.model.utils.PdmMatcher;
import com.sbt.model.utils.exception.JsonValidationException;
import com.sbt.model.utils.schemaintegritycheckgenerator.DBIntegrityValidationSqlGenerator;
import org.apache.commons.io.FileUtils;
import sbp.com.sbt.semver.Semver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.sbt.dataspace.pdm.ModelGenerateUtils.getModelVersion;
import static com.sbt.mg.Helper.compress;
import static com.sbt.mg.Helper.isSnapshotVersion;
import static com.sbt.mg.Helper.isXmlModelFile;
import static com.sbt.mg.Helper.sha256;
import static com.sbt.mg.Helper.wrap;
import static com.sbt.mg.ModelHelper.XML_MAPPER;
import static com.sbt.mg.jpa.JpaConstants.CUSTOM_CHANGELOG;
import static com.sbt.mg.jpa.JpaConstants.DEFAULT_DICTIONARY_NAME_DIR;
import static com.sbt.model.checker.CheckerUtils.isCorrectVersionToDelete;

public class ModelGenerator {
    private static final Logger LOGGER = Logger.getLogger(ModelGenerator.class.getName());
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);

    public ModelParameters generatePdm(
            PluginParameters pluginParameters,
            String version,
            String packageName,
            File goalSourceDirectory,
            TargetFileHolder goalResourceDirectoryHolder,
            String pluginVersion) {
        return this.generatePdm(pluginParameters, version, packageName, goalSourceDirectory,
                goalResourceDirectoryHolder, pluginVersion, true, true);
    }

    public ModelParameters generatePdm(
            PluginParameters pluginParameters,
            String version,
            String packageName,
            File goalSourceDirectory,
            TargetFileHolder goalResourceDirectoryHolder,
            String pluginVersion,
            boolean addToGit,
            boolean makeDbChangeLog) {
        return generatePdm(pluginParameters,
                version,
                packageName,
                goalSourceDirectory,
                goalResourceDirectoryHolder,
                pluginVersion,
                addToGit,
                makeDbChangeLog,
                true);
    }

    public ModelParameters generatePdm(
            PluginParameters pluginParameters,
            String version,
            String packageName,
            File goalSourceDirectory,
            TargetFileHolder goalResourceDirectoryHolder,
            String pluginVersion,
            boolean addToGit,
            boolean makeDbChangeLog,
            boolean saveToFile) {
        return wrap(() -> {
            LOGGER.info("Starting model generator version: " + pluginVersion);

            // Creating a directory for the final pdm file (if it does not already exist)
            pluginParameters.setTargetFile(Helper.getFile(pluginParameters.getModel(), JpaConstants.userModelDir()));

            // since after the end of the method optimizeChangelog, the property pluginParameters.optimizeChangelog will be switched to false
            // (this is necessary so that subsequent checks run in standard mode),
            // save the property value to a variable; we'll need it later in ModelGenerator
            boolean isOptimizeChangelog = pluginParameters.isOptimizeChangelog();
            if (isOptimizeChangelog) {
                // optimization ?
                optimizeChangelog(pluginParameters, makeDbChangeLog, goalSourceDirectory, goalResourceDirectoryHolder);
            }

            // Perform a check on the correctness of the transition from the previous version to the current one
            ModelParameters modelParameters = new CheckModel().execute(pluginParameters);

            if (addToGit && makeDbChangeLog) {
                if (!isOptimizeChangelog) {
                    if (parameterDisableGenerateOracleLiquibaseHasChanged(modelParameters, pluginParameters)) {
                        throw new NeedToDoChangelogOptimizationException();
                    }
                }
                saveDisabledGenerateOracleLiquibase(modelParameters, pluginParameters.isDisableGenerateOracleLiquibase());
            }

            savePluginVersion(modelParameters, pluginVersion);
            saveUseRenamedFields(modelParameters, pluginParameters.isUseRenamedFields());
            saveDisableBaseEntityFields(modelParameters, pluginParameters.isDisableBaseEntityFields());

            String projectVersion = version != null ? version : modelParameters.getModel().getVersion();

            boolean localDeploy = pluginParameters.isLocalDeploy();

            if (localDeploy) {
                projectVersion = UUID.randomUUID().toString().substring(0, 9) + '-' + projectVersion;
            }

            FileUtils.forceMkdir(goalResourceDirectoryHolder.getTargetFile());

            FileUtils.forceMkdir(goalSourceDirectory);
            modelParameters.setGoalGenDirectory(goalSourceDirectory);

            modelParameters.setHibernatePackage(packageName);
            modelParameters.setVersion(projectVersion);

            modelParameters.setGoalGenDirectory(goalResourceDirectoryHolder.getTargetFile());

            if (!pluginParameters.isDeprecateDeletedItems() && (pluginParameters.isDropUnusedSchemaItems() || pluginParameters.isDropDeletedItemsImmediately())) {
                setMinCompatibleVersion(modelParameters);
            }

            if (makeDbChangeLog) {
                new ChangelogGenerator().executeGeneration(modelParameters, pluginParameters);
            }

            if (!pluginParameters.isDeprecateDeletedItems() || (pluginParameters.isDeprecateDeletedItems() && pluginParameters.isDropRemovedItems())
                    || pluginParameters.isIntermediaryBuild()
                    || pluginParameters.isDisableCompatibilityCheck()) {
                deleteRemovedElements(modelParameters);
            }

            // deprecated indexes are always removed, so we remove them immediately
            deleteDeprecatedIndexes(modelParameters.getModel());

            deleteDeprecatedEnums(modelParameters.getModel());

            if (!pluginParameters.isDeprecateDeletedItems() && (pluginParameters.isDropUnusedSchemaItems() || pluginParameters.isDropDeletedItemsImmediately())) {
                deleteUnusedSchemaItems(modelParameters, pluginParameters);
            }

            LOGGER.info(modelParameters.toString());

            // And one more time
            if (pluginParameters.isTwiceBuilding()) {
                savePdmModelInFile(modelParameters, pluginParameters, goalResourceDirectoryHolder, false, false);
                pluginParameters.setTargetFile(goalResourceDirectoryHolder.getTargetFile());
                //I don't see any point in re-checking compatibility in the build of intermediate releases.
                //as in intermediate releases we disable it
                if (!pluginParameters.isIntermediaryBuild() && !pluginParameters.isDisableCompatibilityCheck()) {
                    //The 2nd run will be with the same version of pdm and model. If you do not disable the check, an exception will be thrown.
                    pluginParameters.setDisableIncreaseVersionCheck(true);
                    new CheckModel().execute(pluginParameters);
                }
            }

            if (saveToFile) {
                savePdmModelInFile(modelParameters, pluginParameters, goalResourceDirectoryHolder, false, makeDbChangeLog);
                saveCciInfoFile(modelParameters);

                if (pluginParameters.isGenerateAggRefValidationInfo()) {
                    saveAggregateReferenceValidationSqlFile(modelParameters);
                }

                saveDBIntegrityValidationSqlFile(modelParameters);

                if (localDeploy) {
                    File resultFileModel = Helper.createDirectory(pluginParameters.getModel(), JpaConstants.userLocalModelDir());
                    savePdmModelInFile(
                            modelParameters,
                            pluginParameters,
                            new TargetFileHolder(resultFileModel, false),
                            false,
                            makeDbChangeLog);
                }
            }

            if (addToGit && !isSnapshotVersion(projectVersion)) {
                FileUtils.forceDelete(Helper.createDirectory(pluginParameters.getModel(), JpaConstants.userLocalModelDir()));

                File resultFileModel = Helper.createDirectory(pluginParameters.getModel(), JpaConstants.userModelDir());

                savePdmModelInFile(
                        modelParameters,
                        pluginParameters,
                        new TargetFileHolder(resultFileModel, false),
                        true,
                        makeDbChangeLog,
                        true);

            }

            // to delete the temporary folder
            FileUtils.deleteQuietly(modelParameters.getTempDictionaryDirectory());

            LOGGER.info("Completion of the model generator operation");
            return modelParameters;
        });
    }

    /**
     * Here we check that the build parameter "disableGenerateOracleLiquibase" has been changed in the current release, and that
     * if the model did not have a previous state (pdm.xml is missing), then we consider that the parameter has not changed
     * (even if it has changed relative to the default value)
     *
     * @param modelParameters
     * @param pluginParameters
     * @return
     */
    private boolean parameterDisableGenerateOracleLiquibaseHasChanged(ModelParameters modelParameters, PluginParameters pluginParameters) {
        if (Objects.nonNull(modelParameters.getImmutablePreviousPdmModel()) && Objects.nonNull(modelParameters.getImmutablePreviousPdmModel().getModel())) {
            if (Objects.isNull(modelParameters.getImmutablePreviousPdmModel().getMetaInformation()) && pluginParameters.isDisableGenerateOracleLiquibase()) {
                return true;
            }
            if (Objects.nonNull(modelParameters.getImmutablePreviousPdmModel().getMetaInformation())) {
                if ((pluginParameters.isDisableGenerateOracleLiquibase()
                        && !Objects.equals(Boolean.TRUE, Boolean.valueOf(modelParameters.getImmutablePreviousPdmModel().getMetaInformation().getDisabledGenerateOracleLiquibase())))
                        || (!pluginParameters.isDisableGenerateOracleLiquibase()
                        && Objects.equals(Boolean.TRUE, Boolean.valueOf(modelParameters.getImmutablePreviousPdmModel().getMetaInformation().getDisabledGenerateOracleLiquibase())))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void optimizeChangelog(PluginParameters pluginParameters,
                                   boolean makeDbChangeLog,
                                   File goalSourceDirectory,
                                   TargetFileHolder targetFileHolder) throws IOException{
        ModelParameters modelParameters = new CheckModel().execute(pluginParameters);

        String previousModelVersion = modelParameters.getPreviousModelVersion();

        FileUtils.forceMkdir(targetFileHolder.getTargetFile());
        FileUtils.forceMkdir(goalSourceDirectory);
        modelParameters.setGoalGenDirectory(targetFileHolder.getTargetFile());
        modelParameters.setVersion(previousModelVersion);

        if (makeDbChangeLog) {
            PdmModel pdmModel = modelParameters.getPdmModel();
            if (pdmModel != null && pdmModel.getMetaInformation() != null) {
                // before optimization, we reset the state of haveH2DecodeBase64Function,
                // so that the script with the function gets into the beginning of the optimization change log
                pdmModel.getMetaInformation().setHaveH2DecodeBase64Function(Boolean.FALSE.toString());
                pdmModel.getMetaInformation().setHaveInsertRootDictionary(Boolean.FALSE.toString());
                pdmModel.getMetaInformation().setHaveInsertRootSecurity(Boolean.FALSE.toString());
            }

            modelParameters.getModel().getClassesAsList()
                    .forEach(xmlModelClass -> {
                        modelParameters.addDiffObject(ElementState.NEW, xmlModelClass);
                        xmlModelClass.getPropertiesAsList()
                                .forEach(xmlModelClassProperty -> modelParameters.addDiffObject(ElementState.NEW, xmlModelClassProperty));
                    });
            new ChangelogGenerator().executeGeneration(modelParameters, pluginParameters);

            File srcChangelogFile = Helper.createFile(modelParameters.getChangelogDirectory(), JpaConstants.CHANGELOG_FILENAME);
            File destChangelogFile = modelParameters.getPathToPreviousChangelog();
            wrap(() -> FileUtils.copyFile(srcChangelogFile, destChangelogFile));
            moveCustomChangelog(modelParameters, true, targetFileHolder);
        }
        pluginParameters.setOptimizeChangelog(false);
    }

    private void deleteUnusedSchemaItems(ModelParameters modelParameters, PluginParameters pluginParameters) {
        String newModelVersion = getModelVersion(modelParameters, modelParameters.getModel());
        if (isSnapshotVersion(newModelVersion)) {
            return;
        }

        boolean dropDeletedItemsImmediately = pluginParameters.isDropDeletedItemsImmediately();

        XmlModel model = modelParameters.getModel();
        XmlUnusedSchemaItems unusedSchemaItems = model.getUnusedSchemaItems();

        if (Objects.isNull(unusedSchemaItems)) {
            return;
        }

        List<XmlUnusedColumn> unusedColumns = unusedSchemaItems.getUnusedColumns();
        List<XmlUnusedTable> unusedTables = unusedSchemaItems.getUnusedTables();

        if (Objects.nonNull(unusedColumns)) {
            List<XmlUnusedColumn> deletedUnusedColumns = unusedColumns.stream()
                    .filter(unusedColumn -> isCorrectVersionToDelete(newModelVersion, unusedColumn.getDeletedInVersion(), dropDeletedItemsImmediately))
                    .toList();
            deletedUnusedColumns.forEach(unusedColumns::remove);
        }

        if (Objects.nonNull(unusedTables)) {
            List<XmlUnusedTable> deletedUnusedTables = unusedTables.stream()
                    .filter(unusedTable -> isCorrectVersionToDelete(newModelVersion, unusedTable.getDeletedInVersion(), dropDeletedItemsImmediately))
                    .toList();
            deletedUnusedTables.forEach(unusedTables::remove);
        }

        if ((Objects.isNull(unusedColumns) || unusedColumns.isEmpty()) && (Objects.isNull(unusedTables) || unusedTables.isEmpty())) {
            model.setUnusedSchemaItems(null);
        }

    }

    private void setMinCompatibleVersion(ModelParameters modelParameters) {
        String newModelVersion = getModelVersion(modelParameters, modelParameters.getModel());
        if (isSnapshotVersion(newModelVersion)) {
            return;
        }

        XmlModel model = modelParameters.getModel();
        XmlUnusedSchemaItems unusedSchemaItems = model.getUnusedSchemaItems();

        if (Objects.isNull(unusedSchemaItems)) {
            return;
        }

        List<XmlUnusedColumn> unusedColumns = unusedSchemaItems.getUnusedColumns();
        List<XmlUnusedTable> unusedTables = unusedSchemaItems.getUnusedTables();

        TreeSet<Semver> versions = new TreeSet<>();

        if (Objects.nonNull(unusedColumns)) {
            unusedColumns.forEach(unusedColumn -> versions.add(Semver.of(unusedColumn.getDeletedInVersion())));
        }

        if (Objects.nonNull(unusedTables)) {
            unusedTables.forEach(unusedTable -> versions.add(Semver.of(unusedTable.getDeletedInVersion())));
        }

        String currentMinCompatibleVersion = model.getMinCompatibleVersion();
        if (Objects.nonNull(currentMinCompatibleVersion)) {
            versions.add(Semver.of(currentMinCompatibleVersion));
        }
        if (!versions.isEmpty()) {
            Semver last = versions.last();
            model.setMinCompatibleVersion(last.getVersion());
        }
    }

    /**
     * The enumeration will be completely deleted from pdm.xml when it is removed from model.xml.
     * and no references to it will remain in any property
     *
     * @param model
     */
    private void deleteDeprecatedEnums(XmlModel model) {
        List<XmlModelClassEnum> deletedEnums = model.getUserEnums().stream()
                .filter(XmlModelClassEnum::isDeprecated)
                .filter(xmlEnum -> {
                    // in this filter, we exclude enumerations if they come across at least once in any property as a type
                    // todo: streams couldn't be sorted out, maybe someone will be more lucky :)
                    List<XmlModelClass> xmlModelClasses = model.getClassesAsList();
                    for (XmlModelClass xmlModelClass: xmlModelClasses) {
                        if(xmlModelClass.getPropertiesAsList().stream().anyMatch(property -> property.getType().equals(xmlEnum.getName()))) {
                            return false;
                        }
                    }
                    return true;
                })
                .toList();

        if (!deletedEnums.isEmpty()) {
            List<XmlModelClassEnum> updatedEnumList = new ArrayList<>(model.getEnums());
            updatedEnumList.removeAll(deletedEnums);
            model.setEnums(updatedEnumList);
        }
    }

    private void deleteDeprecatedIndexes(XmlModel model) {
        model.getClassesAsList().forEach(xmlModelClass -> {
            List<XmlIndex> xmlIndicesNotDeprecated = xmlModelClass.getIndices().stream().filter(xmlIndex -> !xmlIndex.isDeprecated()).collect(Collectors.toList());
            xmlModelClass.setIndices(xmlIndicesNotDeprecated);
        });
    }

    private void deleteRemovedElements(ModelParameters modelParameters) {
        XmlModel model = modelParameters.getModel();

        model.getClassesAsList().forEach(xmlModelClass -> {
            xmlModelClass.getPropertiesAsList().stream()
                    .filter(XmlModelClassProperty::isRemoved)
                    .forEach(property -> {
                        if (xmlModelClass.isEmbeddable()) {
                            model.getClassesAsList().stream()
                                    //получаем класс, в котором есть свойство, типом которого является embedded класс (xmlModelClass)
                                    .filter(modelClass -> modelClass.getPropertiesAsList().stream()
                                            .anyMatch(property1 -> property1.getType().equals(xmlModelClass.getName())))
                                    .forEach(modelClass -> {
                                        //получаем свойства, типами которых является embedded класс (xmlModelClass)
                                        modelClass.getPropertiesAsList().stream()
                                                .filter(property1 -> property1.getType().equals(xmlModelClass.getName()))
                                                .forEach(xmlModelClassProperty -> {
                                                    //получаем embedded list, который относится к найденному свойству

                                                    Optional<XmlEmbeddedList> optionalXmlEmbeddedList = modelClass.getEmbeddedPropertyList().stream()
                                                            .filter(xmlEmbeddedList1 -> xmlEmbeddedList1.getName().equals(xmlModelClassProperty.getName()))
                                                            .findFirst();
                                                    if (optionalXmlEmbeddedList.isPresent()) {
                                                        XmlEmbeddedList xmlEmbeddedList = optionalXmlEmbeddedList.get();
                                                        // получаем элемент embedded list-а , который относится (property) к свойству из embedded класса (xmlModelClass)

                                                        Optional<XmlEmbeddedProperty> optionalXmlEmbeddedProperty = xmlEmbeddedList.getEmbeddedPropertyList().stream()
                                                                .filter(xmlEmbeddedProperty1 -> xmlEmbeddedProperty1.getName().equals(property.getName()))
                                                                .findFirst();
                                                        if (optionalXmlEmbeddedProperty.isPresent()) {
                                                            XmlEmbeddedProperty xmlEmbeddedProperty = optionalXmlEmbeddedProperty.get();
                                                            xmlEmbeddedList.getEmbeddedPropertyList().remove(xmlEmbeddedProperty);
                                                        }
                                                    }
                                                });
                                    });
                        } else {
                            if (property.isEmbedded()) {
                                xmlModelClass.removeEmbeddedList(property);
                            }
                        }

                        removePropertyFromHistoryVersions(modelParameters.getPdmModel(), property);
                        xmlModelClass.removeProperty(property.getName());
                    });

            // Удаляет из EmbeddablePropertyList класса элементы отмеченные как isRemoved=true
            // Актульно при удалении embedded свойства
            removeMarkedEmbeddablePropertyListItems(xmlModelClass);
        });

        model.getClassesAsList().stream()
                .filter(XmlModelClass::isRemoved)
                .forEach(xmlModelClass -> {
                    removeClassFromHistoryVersions(modelParameters.getPdmModel(), xmlModelClass);
                    model.removeClass(xmlModelClass.getName());
                    if (xmlModelClass.isEvent()) {
                        model.removeEvent(xmlModelClass.getName());
                    }
                });
        model.getQueriesAsList().stream()
                .filter(XmlQuery::isRemoved)
                .forEach(xmlQuery -> model.removeQuery(xmlQuery.getName()));
    }

    /**
     * Удаляет из EmbeddablePropertyList класса элементы отмеченные как isRemoved=true
     */
    private static void removeMarkedEmbeddablePropertyListItems(XmlModelClass xmlModelClass) {
        List<XmlEmbeddedList> toRemove = xmlModelClass.getEmbeddedPropertyList().stream()
                .filter(XmlEmbeddedList::isRemoved)
                .toList();
        toRemove.forEach(xmlModelClass::removeEmbeddedList);
    }

    private void removePropertyFromHistoryVersions(PdmModel pdmModel, XmlModelClassProperty property) {
        XmlHistoryVersions xmlHistoryVersions = pdmModel.getXmlHistoryVersions();
        if (xmlHistoryVersions != null) {
            Iterator<XmlHistoryVersion> histVerIterator = xmlHistoryVersions.getHistoryVersions().iterator();
            while (histVerIterator.hasNext()) {
                XmlHistoryVersion histVersion = histVerIterator.next();
                if (!histVersion.getName().equals(property.getModelClass().getName())) {
                    continue;
                }
                Iterator<XmlHistoryProperty> histVerPropIterator = histVersion.getXmlHistoryProperties().iterator();
                while (histVerPropIterator.hasNext()) {
                    XmlHistoryProperty histVerProp = histVerPropIterator.next();
                    if (histVerProp.getName().equals(property.getName())) {
                        histVerPropIterator.remove();
                    }
                }
                // Если в записе не осталось свойств, то удаляем и саму версию
                if (histVersion.getXmlHistoryProperties().isEmpty()) {
                    histVerIterator.remove();
                }
            }
        }
    }

    private void removeClassFromHistoryVersions(PdmModel pdmModel, XmlModelClass clazz) {
        XmlHistoryVersions xmlHistoryVersions = pdmModel.getXmlHistoryVersions();
        if (xmlHistoryVersions != null) {
            xmlHistoryVersions.getHistoryVersions().removeIf(historyVersion -> historyVersion.getName().equals(clazz.getName()));
        }
    }

    private void savePluginVersion(ModelParameters modelParameters, String pluginVersion) {
        if (modelParameters.getPdmModel().getMetaInformation() == null) {
            modelParameters.getPdmModel().setMetaInformation(new XmlMetaInformation());
        }
        modelParameters.getPdmModel().getMetaInformation().setPluginVersion(pluginVersion);
    }

    private void saveUseRenamedFields(ModelParameters modelParameters, boolean useRenamedFields) {
        if (modelParameters.getPdmModel().getMetaInformation() == null) {
            modelParameters.getPdmModel().setMetaInformation(new XmlMetaInformation());
        }
        modelParameters.getPdmModel().getMetaInformation().setUseRenamedFields(String.valueOf(useRenamedFields));
    }

    private void saveDisableBaseEntityFields(ModelParameters modelParameters, boolean disableBaseEntityFields) {
        if (modelParameters.getPdmModel().getMetaInformation() == null) {
            modelParameters.getPdmModel().setMetaInformation(new XmlMetaInformation());
        }
        modelParameters.getPdmModel().getMetaInformation().setDisableBaseEntityFields(String.valueOf(disableBaseEntityFields));
    }

    private void saveDisabledGenerateOracleLiquibase(ModelParameters modelParameters, Boolean disabledGenerateOracleLiquibase) {
        if (modelParameters.getPdmModel().getMetaInformation() == null) {
            modelParameters.getPdmModel().setMetaInformation(new XmlMetaInformation());
        }
        modelParameters.getPdmModel().getMetaInformation().setDisabledGenerateOracleLiquibase(disabledGenerateOracleLiquibase.toString());
    }

    private void savePdmModelInFile(
            ModelParameters modelParameters,
            PluginParameters pluginParameters,
            TargetFileHolder targetFileHolder,
            boolean addToGit,
            boolean handleChangeLog) throws IOException {
        savePdmModelInFile(modelParameters, pluginParameters, targetFileHolder, addToGit, handleChangeLog, false);
    }

    private void savePdmModelInFile(
            ModelParameters modelParameters,
            PluginParameters pluginParameters,
            TargetFileHolder targetFileHolder,
            boolean addToGit,
            boolean handleChangeLog,
            boolean copyDictFiles) throws IOException {
        PdmModel pdmModel = modelParameters.getPdmModel();

        pdmModel.setModel(modelParameters.getModel());

        modelParameters.getExecutingModelGenerate().forEach(iModelGenerate ->
                iModelGenerate.saveModel(pdmModel, targetFileHolder));

        if (addToGit) {
            addSourceModelsToPdm(pdmModel, pluginParameters);
        }

        boolean isIntermediaryBuild = pluginParameters.isIntermediaryBuild();

        String pdmFileName = JpaConstants.PDM_MODEL_FILENAME;
        String resultChangelogFileName = JpaConstants.CHANGELOG_FILENAME;
        File resultPdmFile;

        if (isIntermediaryBuild) {
            if (targetFileHolder.isReleaseDirectory()) {
                pdmFileName = JpaConstants.PDM_BUILD_MODEL_FILENAME;
            }
            resultChangelogFileName = JpaConstants.CHANGELOG_BUILD_FILENAME;

            resultPdmFile = Helper.createFile(targetFileHolder.getTargetFile(), pdmFileName);
            writePdmToFile(pdmModel, resultPdmFile);
        } else {
            resultPdmFile = Helper.createFile(targetFileHolder.getTargetFile(), pdmFileName);
            writePdmToFile(pdmModel, resultPdmFile);

            moveCustomChangelog(modelParameters, addToGit, targetFileHolder);

            File pdmBuildFile = Helper.getFile(targetFileHolder.getTargetFile(), JpaConstants.PDM_BUILD_MODEL_FILENAME);
            File changelogBuildFile = Helper.getFile(targetFileHolder.getTargetFile(), JpaConstants.CHANGELOG_BUILD_FILENAME);

            if (pdmBuildFile.exists()) {
                if (pdmMatchesPdmBuild(resultPdmFile, pdmBuildFile)) {
                    FileUtils.deleteQuietly(pdmBuildFile);
                }
            }

            if (changelogBuildFile.exists()) {
                FileUtils.deleteQuietly(changelogBuildFile);
            }
        }
        copySecurityFiles(modelParameters.getModelDirectory(), targetFileHolder.getTargetFile());

        File changelogFile = null;
        if (handleChangeLog) {
            File srcChangelogFile = Helper.createFile(modelParameters.getChangelogDirectory(), JpaConstants.CHANGELOG_FILENAME);
            File destChangelogFile = Helper.createFile(targetFileHolder.getTargetFile(), resultChangelogFileName);
            //копируем changelog из каталога db\model-name\ в каталог, где хранится предыдущий changelog (model или localmodel)
            wrap(() -> FileUtils.copyFile(srcChangelogFile, destChangelogFile));

            // скопировать из temp changlogFiles в target
            final File tempDirectoryChangelogDataFiles =
                    Helper.getFile(modelParameters.getGoalGenDirectory(), JpaConstants.temporaryChangelogFilesDirectory());

            if (tempDirectoryChangelogDataFiles.exists()) {
                final File targetDictionaryDir =
                        Helper.createDirectory(modelParameters.getGoalGenDirectory(), DEFAULT_DICTIONARY_NAME_DIR);
                Arrays.stream(targetDictionaryDir.listFiles())
                        .forEach(FileUtils::deleteQuietly);
                wrap(() -> FileUtils.copyDirectory(tempDirectoryChangelogDataFiles, targetDictionaryDir));
            }

            // скопировать в current модели
            if (copyDictFiles && tempDirectoryChangelogDataFiles.exists()) {
                wrap(() -> {
                    File destination = new File(targetFileHolder.getTargetFile(), JpaConstants.dictionaryPartsDir());
                    FileUtils.copyDirectory(tempDirectoryChangelogDataFiles, destination);
                });
            }

            File srcSecurityChangelogFile = Helper.getFile(modelParameters.getChangelogDirectory(), JpaConstants.SECURITY_CHANGELOG_FILENAME);
            if (srcSecurityChangelogFile.exists()) {
                File destSecurityChangelogFile = Helper.createFile(targetFileHolder.getTargetFile(), JpaConstants.SECURITY_CHANGELOG_FILENAME);
                wrap(() -> FileUtils.copyFile(srcSecurityChangelogFile, destSecurityChangelogFile));
            }
            changelogFile = destChangelogFile;
        }

        if (addToGit) {
            Helper.addToGit(resultPdmFile);
            if (handleChangeLog) {
                Helper.addToGit(changelogFile);
            }
        }
    }

    private void copyFile(File sourceDirectory, String filePath, File destinationDirectory, Consumer<File> processFile) throws IOException {
        final File srcFile = new File(sourceDirectory, filePath);
        if (srcFile.exists()) {
            LOGGER.info("File " + filePath + " not found.");
            processFile.accept(srcFile);
            FileUtils.copyFile(srcFile,
                    new File(destinationDirectory, filePath));
        } else {
            LOGGER.info("File " + filePath + " not found.");
        }
    }

    private final Consumer<File> isValidJson = (file) -> {
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            objectMapper.readTree(fileInputStream);
        } catch (Exception ex) {
            throw new JsonValidationException(ex);
        }
    };

    private void copySecurityFiles(File sourceDirectory, File destinationDirectory) throws IOException {
        // Проверяем, что в директории нет лишних файлов, которые не обрабатываются
        File securityCnonfigDir = new File(sourceDirectory, JpaConstants.SECURITY_CONFIG_DIR_NAME);
        if (!securityCnonfigDir.exists()) {
            LOGGER.info("The directory " + JpaConstants.SECURITY_CONFIG_DIR_NAME + " missing from the model.");
            return;
        }

        File[] srcDirFiles = securityCnonfigDir.listFiles();
        List<String> expectedFileNames = Arrays.asList(JpaConstants.GRAPHQLPERMISSIONS_FILE_NAME, JpaConstants.JWKS_FILE_NAME);
        List<String> unexpectedFileNames = Arrays.stream(srcDirFiles)
                .map(File::getName)
                .filter(name -> !expectedFileNames.contains(name))
                .toList();

        if (!unexpectedFileNames.isEmpty()) {
            throw new RuntimeException("Unexpected files have been found in the SecurityConfig directory, they must be deleted: " +
                    unexpectedFileNames);
        }

        copyFile(sourceDirectory,
                JpaConstants.SECURITY_CONFIG_DIR_NAME + "/" + JpaConstants.GRAPHQLPERMISSIONS_FILE_NAME,
                destinationDirectory,
                isValidJson);
        copyFile(sourceDirectory,
                JpaConstants.SECURITY_CONFIG_DIR_NAME + "/" + JpaConstants.JWKS_FILE_NAME,
                destinationDirectory,
                isValidJson);
    }

    private void addSourceModelsToPdm(PdmModel pdmModel, PluginParameters pluginParameters) {
        File modelDir = pluginParameters.getModel();
        File rootModelFile = Helper.getFile(modelDir, pluginParameters.getModelName());

        String strRootModelFile = wrap(() -> FileUtils.readFileToString(rootModelFile, StandardCharsets.UTF_8));
        XmlModel rootModel = Helper.wrap(() -> XML_MAPPER.readValue(rootModelFile, XmlModel.class));

        XmlRootModel xmlRootModel = Helper.wrap(() ->
                new XmlRootModel(rootModel.getModelName(), sha256(strRootModelFile), compress(strRootModelFile)));

        XmlSourceModels xmlSourceModels = new XmlSourceModels();
        xmlSourceModels.setPreviousRootModel(xmlRootModel);

        String typeImportName = "IMPORT";
        String nameDirImport = "import";

        Optional<XmlImport> optionalImport = rootModel.getImports().stream()
                .filter(xmlImport -> typeImportName.equals(xmlImport.getType()))
                .findFirst();

        File importModelDir;

        if (optionalImport.isPresent()) {
            XmlImport xmlImport = optionalImport.get();
            String filePath = xmlImport.getFile();
            importModelDir = Helper.getFile(modelDir, filePath != null ? filePath : nameDirImport);
        } else {
            importModelDir = Helper.getFile(modelDir, nameDirImport);
        }

        if (importModelDir.exists()) {
            FileUtils.iterateFiles(importModelDir, new String[]{"xml"}, true)
                    .forEachRemaining(file -> {
                        if (isXmlModelFile(file)) {
                            Helper.wrap(() -> {
                                String strImportModel = wrap(() -> FileUtils.readFileToString(file, StandardCharsets.UTF_8));
                                XmlModel importModel = Helper.wrap(() -> XML_MAPPER.readValue(file, XmlModel.class));
                                XmlImportModel xmlImportModel = new XmlImportModel(importModel.getModelName(), sha256(strImportModel), compress(strImportModel));
                                xmlSourceModels.addImportModel(xmlImportModel);
                            });
                        }
                    });
        }
        pdmModel.setSourceModels(xmlSourceModels);
    }

    private void moveCustomChangelog(ModelParameters modelParameters, boolean addToGit, TargetFileHolder targetFileHolder) {
        File modelDir = modelParameters.getModelDirectory();
        File customChangelogFile = Helper.getFile(modelDir, JpaConstants.CUSTOM_CHANGELOG_FILENAME);
        if (customChangelogFile.exists() && addToGit) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HHmmss");
            Date date = new Date();
            //nameResult = custom-changelog_dd-MM-yyyy_HHmmss.xml
            String nameResultFile = String.format("%s_%s.xml", CUSTOM_CHANGELOG, formatter.format(date));
            File savedCustomChangelogFile = new File(targetFileHolder.getTargetFile(), nameResultFile);
            wrap(() -> FileUtils.moveFile(customChangelogFile, savedCustomChangelogFile));
            Helper.addToGit(savedCustomChangelogFile);
        }
    }

    private boolean pdmMatchesPdmBuild(File resultPdmFile, File pdmBuildFile) {
        return new PdmMatcher().pdmMatchesAnotherPdm(resultPdmFile, pdmBuildFile);
    }

    private void writePdmToFile(PdmModel pdmModel, File resultFile) {
        // Layout нужен только в model.xml. Убираем тут, чтобы удалить для существующих pdm.
        pdmModel.getModel().setLayout(null);
        wrap(() -> {
            Writer writer = new PrintWriter(resultFile, "UTF-8");
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\r\n");
            writer.append(XML_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(pdmModel));
            writer.flush();
            writer.close();
        });
    }

    private void saveCciInfoFile(ModelParameters modelParameters) {
        XmlModel model = modelParameters.getModel();
        if (model != null) {
            String cciIndexNames = model.getClassesAsList().stream()
                    .flatMap(it -> it.getCciIndices().stream())
                    .map(XmlCciIndex::getName)
                    .collect(Collectors.joining("\n"));

            if (!Strings.isNullOrEmpty(cciIndexNames)) {
                File resultFile = Helper.createFile(modelParameters.getModelDirectory(), JpaConstants.CCI_INDEX_INFO_FILENAME);

                wrap(() -> {
                    FileWriter fileWriter = new FileWriter(resultFile);
                    fileWriter.write(cciIndexNames);
                    fileWriter.flush();
                    fileWriter.close();
                });
            }
        }
    }

    private void saveDBIntegrityValidationSqlFile(ModelParameters modelParameters) {
        final XmlModel model = modelParameters.getModel();
        if (Objects.isNull(model)) {
            return;
        }
        String content = new DBIntegrityValidationSqlGenerator().generate(model);
        if (Strings.isNullOrEmpty(content)) {
            return;
        }
        final File resultFile = Helper.createFile(
                modelParameters.getGoalGenDirectory(),
                JpaConstants.DB_INTEGRITY_VALIDATION_SQL_FILENAME
        );
        wrap(() -> {
            FileWriter fileWriter = new FileWriter(resultFile, StandardCharsets.UTF_8);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        });
    }

    private void saveAggregateReferenceValidationSqlFile(ModelParameters modelParameters) {

        final XmlModel model = modelParameters.getModel();

        if (Objects.isNull(model)) {
            return;
        }

        final String content = new AggregateScopeReferenceValidationSqlGenerator()
                .generate(model)
                .stream()
                .map(AggregateScopeReferenceValidationSqlGenerator.Result::toString)
                .collect(Collectors.joining());

        if (Strings.isNullOrEmpty(content)) {
            return;
        }

        final File resultFile = Helper.createFile(
                modelParameters.getModelDirectory(),
                JpaConstants.AGGREGATE_REFERENCE_VALIDATION_SQL_FILENAME
        );

        wrap(() -> {
            FileWriter fileWriter = new FileWriter(resultFile);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        });

    }

}
