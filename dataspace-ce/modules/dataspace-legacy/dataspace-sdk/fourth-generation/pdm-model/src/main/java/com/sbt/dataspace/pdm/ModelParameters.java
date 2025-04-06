package com.sbt.dataspace.pdm;

import com.sbt.dataspace.pdm.exception.ModelVersionException;
import com.sbt.dataspace.pdm.exception.NotDefinedPreviousModelVersionException;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.mg.utils.FileData;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelParameters {
    private XmlModel model;
    private String version;
    private String currentVersion;
    private File modelDirectory;
    private File changelogDirectory;
    private String hibernatePackage;
    private File goalGenDirectory;
    private File pathToPreviousChangelog;
    /**
     * Selected generators from the general list that process the model.
     * Different methods of these generators are launched at different stages of model processing
     * (verification, difference search, JPA generation, etc.)
     */
    private final List<ModelGenerate> executingModelGenerate = new ArrayList<>();
    private PdmModel pdmModel = new PdmModel();
    private PdmModel immutablePreviousPdmModel = new PdmModel();
    private File targetModelPath;
    private final Set<File> dictionaryFiles = new HashSet<>();
    private List<String> backwardCompatibilityViolationMessages = new ArrayList<>();

    // List of changes for building correct liquibase script changelog.xml
    private final EnumMap<ElementState, List<EntityDiff>> diffs = new EnumMap<>(ElementState.class);

    private List<String> warnings;

    public ModelParameters() {
        diffs.put(ElementState.NEW, new ArrayList<>());
        diffs.put(ElementState.UPDATED, new ArrayList<>());
        diffs.put(ElementState.DEPRECATED, new ArrayList<>());
        diffs.put(ElementState.REMOVED, new ArrayList<>());
    }

    public ModelParameters(XmlModel model, File targetModelPath) {
        this();
        this.model = model;
        this.targetModelPath = targetModelPath;
    }

    public XmlModel getModel() {
        return model;
    }

    public void setModel(XmlModel model) {
        this.model = model;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        ModelHelper.validateModelVersionName(version, new ModelVersionException());
        this.version = version;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public File getModelDirectory() {
        return modelDirectory;
    }

    public ModelParameters setModelDirectory(File modelDirectory) {
        this.modelDirectory = modelDirectory;
        return this;
    }

    public File getGoalGenDirectory() {
        return goalGenDirectory;
    }

    public void setGoalGenDirectory(File goalGenDirectory) {
        this.goalGenDirectory = goalGenDirectory;
    }

    public File getTempDictionaryDirectory() {
        return new File(this.goalGenDirectory, JpaConstants.temporaryChangelogFilesDirectory());
    }

    public void setHibernatePackage(String hibernatePackage) {
        this.hibernatePackage = hibernatePackage;
    }

    public void addDiffObject(ElementState state, EntityDiff object) {
// TODO need to remove the if and see which tests will break.
// this will highlight the problem in the logic. So far, only one test is known about checkRollbackDropColumnDropTableTest
// The problem with double deletion of the index I_SECONDTESTCLASSAPICALL_APICALLID
        if (!diffs.get(state).contains(object)) {
            diffs.get(state).add(object);
        }
    }

    public boolean isModelChanged() {
        return diffs.entrySet().stream()
            .anyMatch(entry -> !entry.getValue().isEmpty());
    }

    public File getChangelogDirectory() {
        return changelogDirectory;
    }

    public void setChangelogDirectory(File changelogDirectory) {
        this.changelogDirectory = changelogDirectory;
    }

    public void addExecutingModelGenerate(ModelGenerate modelGenerate) {
        if (executingModelGenerate.stream().noneMatch(
            modelGenerate1 -> modelGenerate1.getProjectName().equals(modelGenerate.getProjectName()))) {
            executingModelGenerate.add(modelGenerate);
        }
    }

    public boolean containsObjectInDiff(ElementState state, EntityDiff object) {
        return diffs.get(state) != null && diffs.get(state).contains(object);
    }

    public <T extends EntityDiff> List<T> getObjectByType(ElementState state, Class<T> type) {
        return diffs.get(state).stream()
            .filter(o -> type.isAssignableFrom(o.getClass()))
            .map(o -> (T) o).collect(Collectors.toList());
    }

    public void dropFromCategory(ElementState state, EntityDiff element) {
        diffs.get(state).remove(element);
    }

    public List<ModelGenerate> getExecutingModelGenerate() {
        return executingModelGenerate;
    }

    public void setPdmModel(PdmModel pdmModel) {
        this.pdmModel = pdmModel;
    }

    public PdmModel getPdmModel() {
        return pdmModel;
    }

    public PdmModel getImmutablePreviousPdmModel() {
        return immutablePreviousPdmModel;
    }

    public void setImmutablePreviousPdmModel(PdmModel immutablePreviousPdmModel) {
        this.immutablePreviousPdmModel = immutablePreviousPdmModel;
    }

    public File getPathToPreviousChangelog() {
        return pathToPreviousChangelog;
    }

    public void setPathToPreviousChangelog(File pathToPreviousChangelog) {
        this.pathToPreviousChangelog = pathToPreviousChangelog;
    }

    public File getTargetModelPath() {
        return targetModelPath;
    }

    public ModelParameters setTargetModelPath(File targetModelPath) {
        this.targetModelPath = targetModelPath;
        return this;
    }

    public Set<File> getDictionaryFiles() {
        return dictionaryFiles;
    }

    public List<String> getBackwardCompatibilityViolationMessages() {
        return backwardCompatibilityViolationMessages;
    }

    public void addBackwardCompatibilityViolationMessage(String message) {
        this.backwardCompatibilityViolationMessages.add(message);
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public String getPreviousModelVersion() {
        String previousModelVersion = null;
        if (Objects.nonNull(immutablePreviousPdmModel) && Objects.nonNull(immutablePreviousPdmModel.getModel())) {
            previousModelVersion = immutablePreviousPdmModel.getModel().getVersion();
        }
// todo: Maybe additionally parse the previous changelog,
// to extract the number of the previous version from there, if previousModelVersion == null?
        if (Objects.isNull(previousModelVersion)) {
//todo: for now, the previous version is used only for optimizing the changelog,
// if you will use it for other purposes, correct the error text -
// make it more versatile
            throw new NotDefinedPreviousModelVersionException();
        }
        return previousModelVersion;
    }

    @Override
    public String toString() {
        return "******************************\n"
            + "Data that has been changed {"
            + "projectVersion='" + version + '\''
            + "\n\ndiffs=" + diffs
            + "}\n******************************";
    }
}
