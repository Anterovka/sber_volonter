package com.sbt.dataspace.pdm;

import com.sbt.dataspace.pdm.exception.PluginPropertiesException;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.exception.common.PluginParametersException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.dto.SysVersionFillParams;
import com.sbt.parameters.plugin.ImportSpecification;
import org.springframework.util.unit.DataSize;

import java.io.File;

import static com.sbt.mg.jpa.JpaConstants.DEFAULT_MAX_DB_OBJECT_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.DEFAULT_MIN_CROPPED_CLASS_NAME_LENGTH;
import static com.sbt.mg.jpa.JpaConstants.DEFAULT_MIN_CROPPED_PROPERTY_NAME_LENGTH;

public class PluginParameters {
    private static final String ERROR_TEXT = "Simultaneous setting of build parameters <intermediaryBuild> and <disableCompatibilityCheck> to true is prohibited!";
    private File model;
    private boolean aggregateValidations;
    private String modelName;
    private boolean enableDynamicUpdate;
    private File targetFile;
    private boolean twiceBuilding;
    private boolean localDeploy;
    private boolean changelogChecks;
    private int maxDBObjectNameLength;
    private Integer maxClassNameLength;
    private Integer maxPropertyNameLength;
    private Integer minCroppedClassNameLength;
    private Integer minCroppedPropertyNameLength;
    private boolean enableHistoryGenerators;

    public enum HistoryUpdatedIndexStrategy {
        WITH_INDEX,
        WITHOUT_INDEX,
        KEEP_OLD // keep the current state
    }

    private HistoryUpdatedIndexStrategy historyUpdatedIndexStrategy;
    private boolean singleTableInHistoricalClasses;
    private String importModelName;
    private int maxCciKeyLength;
    private boolean enableCustomDML;
    private boolean dropRemovedItems;
    private String modelVersion;
    /**
     * Allows for backward-incompatible changes to be made within the confines of minor releases
     */
    private boolean intermediaryBuild;
    /**
     * Disabling backward compatibility check
     */
    //Пока неясно, чем отличается от intermediaryBuild.
    private boolean disableCompatibilityCheck;
    private boolean loadDictionaryByTable;
    private boolean generateAllIndices;
    private SysVersionFillParams sysVersionFillParams;
    private boolean exceptionOnDictionaryReference;
    private boolean allowLastChangeDateCompare;
    private boolean allowNullWithCreateParam;
    private boolean enableDictionaryDataCheck;
    private boolean disableAggregateRootReferenceCheck;
    /**
     * Disables checking for duplicates of physical class and field names
     */
    private boolean disablePhysicNamesCheck;
    private boolean disableIncreaseVersionCheck;
    private boolean disableEmptyTypeReferenceCheck;

    private boolean deprecateDeletedItems;
    private boolean dropUnusedSchemaItems;
    private boolean dropDeletedItemsImmediately;
    private boolean allowDeleteNonDeprecatedItems;
    private boolean enableVersionedEntities;
    private boolean optimizeChangelog;
    private boolean skipCustomChangelogCheck;
    private boolean disableGenerateOracleLiquibase;

    private boolean generateAggRefValidationInfo;

    private boolean enableModelNameChangeCheck;
    private Long maxDictionaryFileSize;

    private boolean allowDictionaryPacket;
    private boolean allowCalculatedWithGetParam;
    private String foreignKeys;
    private boolean enableDeleteCascade;
    private boolean customChangelogCDATAValidation;

    private String versionCompatibility;
    private boolean disableBaseEntityFields;
    private boolean useRenamedFields;

    private boolean allowUseParentUniqueIndex;

    private ImportSpecification importSpecification = new ImportSpecification();

    public static PluginParameters emptyParameters() {
        return new PluginParameters();
    }

    private PluginParameters() {
        this.aggregateValidations = true;
        this.enableDynamicUpdate = true;
        this.twiceBuilding = true;
        this.localDeploy = false;
        this.changelogChecks = true;
        this.maxDBObjectNameLength = DEFAULT_MAX_DB_OBJECT_NAME_LENGTH;
        this.enableHistoryGenerators = false;
        this.historyUpdatedIndexStrategy = HistoryUpdatedIndexStrategy.KEEP_OLD;
        this.singleTableInHistoricalClasses = false;
        this.maxCciKeyLength = Integer.parseInt(JpaConstants.MAX_CCI_KEY_LENGTH);
        this.enableCustomDML = false;
        this.enableDictionaryDataCheck = true;
        this.enableModelNameChangeCheck = true;
        this.maxDictionaryFileSize = Long.parseLong(JpaConstants.DEFAULT_MAX_DICTIONARY_FILE_SIZE_BYTES);
    }

    public File getModel() {
        return model;
    }

    public boolean getAggregateValidations() {
        return aggregateValidations;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isEnableDynamicUpdate() {
        return enableDynamicUpdate;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public boolean isTwiceBuilding() {
        return twiceBuilding;
    }

    public boolean isChangelogChecks() {
        return changelogChecks;
    }

    public boolean isLocalDeploy() {
        return localDeploy;
    }

    public int getMaxDBObjectNameLength() {
        return maxDBObjectNameLength;
    }

    public int getMaxClassNameLength() {
        // 23 - T_maxUserPrefix_
        return maxClassNameLength != null ? maxClassNameLength : getMaxDBObjectNameLength() - 23;
    }

    public int getMaxPropertyNameLength() {
        // in analogy with getMaxClassNameLength. 23 is reserve for various prefixes and postfixes
        return maxPropertyNameLength != null ? maxPropertyNameLength : getMaxDBObjectNameLength() - 23;
    }

    public int getMinCroppedClassNameLength() {
        return minCroppedClassNameLength != null ? minCroppedClassNameLength : DEFAULT_MIN_CROPPED_CLASS_NAME_LENGTH;
    }

    public int getMinCroppedPropertyNameLength() {
        return minCroppedPropertyNameLength != null ? minCroppedPropertyNameLength : DEFAULT_MIN_CROPPED_PROPERTY_NAME_LENGTH;
    }

    public Long getMaxDictionaryFileSize() {
        return maxDictionaryFileSize;
    }

    public boolean isSingleTableInHistoricalClasses() {
        return singleTableInHistoricalClasses;
    }

    public String getImportModelName() {
        return importModelName;
    }

    public boolean isEnableHistoryGenerators() {
        return enableHistoryGenerators;
    }

    public HistoryUpdatedIndexStrategy getHistoryUpdatedIndexStragegy() {
        return historyUpdatedIndexStrategy;
    }

    public int getMaxCciKeyLength() {
        return maxCciKeyLength;
    }

    public boolean isEnableCustomDML() {
        return enableCustomDML;
    }

    public boolean isDropRemovedItems() {
        return dropRemovedItems;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public boolean isIntermediaryBuild() {
        return intermediaryBuild;
    }

    public boolean isDisableCompatibilityCheck() {
        return disableCompatibilityCheck;
    }

    public boolean isLoadDictionaryByTable() {
        return loadDictionaryByTable;
    }

    public boolean isGenerateAllIndices() {
        return generateAllIndices;
    }

    private void setModel(File model) {
        this.model = model;
    }

    private void setModelName(String modelName) {
        this.modelName = modelName;
    }

    private void setImportModelName(String importModelName) {
        this.importModelName = importModelName;
    }

    private void setEnableCustomDML(boolean enableCustomDML) {
        this.enableCustomDML = enableCustomDML;
    }

    private void setDropRemovedItems(boolean dropRemovedItems) {
        this.dropRemovedItems = dropRemovedItems;
    }

    private void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    private void setIntermediaryBuild(boolean intermediaryBuild) {
        this.intermediaryBuild = intermediaryBuild;
    }

    private void setDisableCompatibilityCheck(boolean disableCompatibilityCheck) {
        this.disableCompatibilityCheck = disableCompatibilityCheck;
    }

    private void setLoadDictionaryByTable(boolean loadDictionaryByTable) {
        this.loadDictionaryByTable = loadDictionaryByTable;
    }

    public void setGenerateAllIndices(boolean generateAllIndices) {
        this.generateAllIndices = generateAllIndices;
    }

    private void setAggregateValidations(boolean aggregateValidations) {
        this.aggregateValidations = aggregateValidations;
    }

    private void setEnableDynamicUpdate(boolean enableDynamicUpdate) {
        this.enableDynamicUpdate = enableDynamicUpdate;
    }

    private void setTwiceBuilding(boolean twiceBuilding) {
        this.twiceBuilding = twiceBuilding;
    }

    private void setLocalDeploy(boolean localDeploy) {
        this.localDeploy = localDeploy;
    }

    private void setChangelogChecks(boolean changelogChecks) {
        this.changelogChecks = changelogChecks;
    }

    private void setEnableHistoryGenerators(boolean enableHistoryGenerators) {
        this.enableHistoryGenerators = enableHistoryGenerators;
    }

    public void setHistoryUpdatedIndexStrategy(HistoryUpdatedIndexStrategy historyUpdatedIndexStrategy) {
        this.historyUpdatedIndexStrategy = historyUpdatedIndexStrategy;
    }

    private void setSingleTableInHistoricalClasses(boolean singleTableInHistoricalClasses) {
        this.singleTableInHistoricalClasses = singleTableInHistoricalClasses;
    }

    private void setMaxCciKeyLength(int maxCciKeyLength) {
        this.maxCciKeyLength = maxCciKeyLength;
    }

    public SysVersionFillParams getSysVersionFillParams() {
        return sysVersionFillParams;
    }

    public void setSysVersionFillParams(SysVersionFillParams sysVersionFillParams) {
        this.sysVersionFillParams = sysVersionFillParams;
    }

    public boolean isExceptionOnDictionaryReference() {
        return exceptionOnDictionaryReference;
    }

    public void setExceptionOnDictionaryReference(boolean exceptionOnDictionaryReference) {
        this.exceptionOnDictionaryReference = exceptionOnDictionaryReference;
    }

    public boolean isAllowLastChangeDateCompare() {
        return allowLastChangeDateCompare;
    }

    public void setAllowLastChangeDateCompare(boolean allowLastChangeDateCompare) {
        this.allowLastChangeDateCompare = allowLastChangeDateCompare;
    }

    public boolean isAllowUseParentUniqueIndex() {
        return allowUseParentUniqueIndex;
    }

    public void setAllowUseParentUniqueIndex(boolean allowUseParentUniqueIndex) {
        this.allowUseParentUniqueIndex = allowUseParentUniqueIndex;
    }

    public boolean isAllowNullWithCreateParam() {
        return allowNullWithCreateParam;
    }

    public void setAllowNullWithCreateParam(boolean allowNullWithCreateParam) {
        this.allowNullWithCreateParam = allowNullWithCreateParam;
    }

    public boolean isEnableDictionaryDataCheck() {
        return enableDictionaryDataCheck;
    }

    public void setEnableDictionaryDataCheck(boolean enableDictionaryDataCheck) {
        this.enableDictionaryDataCheck = enableDictionaryDataCheck;
    }

    public void setDisableAggregateRootReferenceCheck(boolean disableAggregateRootReferenceCheck) {
        this.disableAggregateRootReferenceCheck = disableAggregateRootReferenceCheck;
    }

    public boolean isDisableAggregateRootReferenceCheck() {
        return disableAggregateRootReferenceCheck;
    }

    public boolean isDisablePhysicNamesCheck() {
        return disablePhysicNamesCheck;
    }

    public boolean isDisableIncreaseVersionCheck() {
        return disableIncreaseVersionCheck;
    }

    public void setDisableIncreaseVersionCheck(boolean disableIncreaseVersionCheck) {
        this.disableIncreaseVersionCheck = disableIncreaseVersionCheck;
    }

    public boolean isDisableEmptyTypeReferenceCheck() {
        return disableEmptyTypeReferenceCheck;
    }

    public void setDisableEmptyTypeReferenceCheck(boolean disableEmptyTypeReferenceCheck) {
        this.disableEmptyTypeReferenceCheck = disableEmptyTypeReferenceCheck;
    }

    private void setDeprecateDeletedItems(boolean deprecateDeletedItems) {
        this.deprecateDeletedItems = deprecateDeletedItems;
    }

    private void setDropUnusedSchemaItems(boolean dropUnusedSchemaItems) {
        this.dropUnusedSchemaItems = dropUnusedSchemaItems;
    }

    private void setDropDeletedItemsImmediately(boolean dropDeletedItemsImmediately) {
        this.dropDeletedItemsImmediately = dropDeletedItemsImmediately;
    }

    private void setAllowDeleteNonDeprecatedItems(boolean allowDeleteNonDeprecatedItems) {
        this.allowDeleteNonDeprecatedItems = allowDeleteNonDeprecatedItems;
    }

    public void setOptimizeChangelog(boolean optimizeChangelog) {
        this.optimizeChangelog = optimizeChangelog;
    }

    private void setDisableGenerateOracleLiquibase(boolean disableGenerateOracleLiquibase) {
        this.disableGenerateOracleLiquibase = disableGenerateOracleLiquibase;
    }

    public boolean isDeprecateDeletedItems() {
        return deprecateDeletedItems;
    }

    public boolean isDropUnusedSchemaItems() {
        return dropUnusedSchemaItems;
    }

    public boolean isDropDeletedItemsImmediately() {
        return dropDeletedItemsImmediately;
    }

    public boolean isAllowDeleteNonDeprecatedItems() {
        return allowDeleteNonDeprecatedItems;
    }

    public boolean isEnableVersionedEntities() {
        return enableVersionedEntities;
    }

    public boolean isOptimizeChangelog() {
        return optimizeChangelog;
    }

    public boolean isSkipCustomChangelogCheck() {
        return skipCustomChangelogCheck;
    }

    public boolean isDisableGenerateOracleLiquibase() {
        return disableGenerateOracleLiquibase;
    }

    public boolean isGenerateAggRefValidationInfo() {
        return generateAggRefValidationInfo;
    }

    public void setGenerateAggRefValidationInfo(boolean generateAggRefValidationInfo) {
        this.generateAggRefValidationInfo = generateAggRefValidationInfo;
    }

    public boolean isEnableModelNameChangeCheck() {
        return enableModelNameChangeCheck;
    }

    public void setEnableModelNameChangeCheck(boolean enableModelNameChangeCheck) {
        this.enableModelNameChangeCheck = enableModelNameChangeCheck;
    }

    public boolean isAllowDictionaryPacket() {
        return allowDictionaryPacket;
    }

    public void setAllowDictionaryPacket(boolean allowDictionaryPacket) {
        this.allowDictionaryPacket = allowDictionaryPacket;
    }

    public boolean isAllowCalculatedWithGetParam() {
        return allowCalculatedWithGetParam;
    }

    public void setAllowCalculatedWithGetParam(boolean allowCalculatedWithGetParam) {
        this.allowCalculatedWithGetParam = allowCalculatedWithGetParam;
    }

    public String getForeignKeys() {
        return foreignKeys;
    }

    private void setForeignKeys(String foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public boolean isEnableDeleteCascade() {
        return enableDeleteCascade;
    }

    private void setEnableDeleteCascade(boolean enableDeleteCascade) {
        this.enableDeleteCascade = enableDeleteCascade;
    }

    public boolean isCustomChangelogCDATAValidation() {
        return customChangelogCDATAValidation;
    }

    private void setCustomChangelogCDATAValidation(boolean customChangelogCDATAValidation) {
        this.customChangelogCDATAValidation = customChangelogCDATAValidation;
    }

    public String getVersionCompatibility() {
        return versionCompatibility;
    }

    public void setVersionCompatibility(String versionCompatibility) {
        this.versionCompatibility = versionCompatibility;
    }

    public ImportSpecification getImportSpecification() {
        return importSpecification;
    }

    public void setImportSpecification(ImportSpecification importSpecification) {
        this.importSpecification = importSpecification;
    }

    public boolean isDisableBaseEntityFields() {
        return disableBaseEntityFields;
    }

    public void setDisableBaseEntityFields(boolean disableBaseEntityFields) {
        this.disableBaseEntityFields = disableBaseEntityFields;
    }

    public boolean isUseRenamedFields() {
        return useRenamedFields;
    }

    public void setUseRenamedFields(boolean useRenamedFields) {
        this.useRenamedFields = useRenamedFields;
    }

    public static class Builder {

        private final PluginParameters pluginParameters;

        private Builder() {
            pluginParameters = new PluginParameters();
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setModel(File model) {
            pluginParameters.setModel(model);
            return this;
        }

        public Builder setAggregateValidations(boolean aggregateValidations) {
            pluginParameters.setAggregateValidations(aggregateValidations);
            return this;
        }

        public Builder setModelName(String modelName) {
            pluginParameters.setModelName(modelName);
            return this;
        }

        public Builder setEnableDynamicUpdate(boolean enableDynamicUpdate) {
            pluginParameters.setEnableDynamicUpdate(enableDynamicUpdate);
            return this;
        }

        public Builder setTargetFile(File targetFile) {
            pluginParameters.setTargetFile(targetFile);
            return this;
        }

        public Builder setTwiceBuilding(boolean twiceBuilding) {
            pluginParameters.setTwiceBuilding(twiceBuilding);
            return this;
        }

        public Builder setLocalDeploy(boolean localDeploy) {
            pluginParameters.setLocalDeploy(localDeploy);
            return this;
        }

        public Builder setChangelogChecks(boolean changelogChecks) {
            pluginParameters.setChangelogChecks(changelogChecks);
            return this;
        }

        public Builder setMaxDBObjectNameLength(Integer maxDBObjectNameLength) {
            if (maxDBObjectNameLength != null) {
                ModelHelper.validateMaxDBObjectNameLength(maxDBObjectNameLength);
                pluginParameters.maxDBObjectNameLength = maxDBObjectNameLength;
            }
            return this;
        }

        public Builder setMaxClassNameLength(Integer maxClassNameLength) {
            if (maxClassNameLength != null) {
                ModelHelper.validateMaxClassNameLength(maxClassNameLength);
                pluginParameters.maxClassNameLength = maxClassNameLength;
            }
            return this;
        }

        public Builder setMaxPropertyNameLength(Integer maxPropertyNameLength) {
            if (maxPropertyNameLength != null) {
                ModelHelper.validateMaxPropertyNameLength(maxPropertyNameLength);
                pluginParameters.maxPropertyNameLength = maxPropertyNameLength;
            }
            return this;
        }

        public Builder setMinCroppedClassNameLength(Integer minCroppedClassNameLength) {
            if (minCroppedClassNameLength != null) {
                ModelHelper.validateMinCroppedClassNameLength(minCroppedClassNameLength);
                pluginParameters.minCroppedClassNameLength = minCroppedClassNameLength;
            }
            return this;
        }

        public Builder setMinCroppedPropertyNameLength(Integer minCroppedPropertyNameLength) {
            if (minCroppedPropertyNameLength != null) {
                ModelHelper.validateMinCroppedPropertyNameLength(minCroppedPropertyNameLength);
                pluginParameters.minCroppedPropertyNameLength = minCroppedPropertyNameLength;
            }
            return this;
        }

        public Builder setEnableHistoryGenerators(boolean enableHistoryGenerators) {
            pluginParameters.setEnableHistoryGenerators(enableHistoryGenerators);
            return this;
        }

        public Builder setSingleTableInHistoricalClasses(boolean singleTableInHistoricalClasses) {
            pluginParameters.setSingleTableInHistoricalClasses(singleTableInHistoricalClasses);
            return this;
        }

        public Builder setImportModelName(String importModelName) {
            pluginParameters.setImportModelName(importModelName);
            return this;
        }

        public Builder setMaxCciKeyLength(int maxCciKeyLength) {
            pluginParameters.setMaxCciKeyLength(maxCciKeyLength);
            return this;
        }

        public Builder setEnableCustomDML(boolean enableCustomDML) {
            pluginParameters.setEnableCustomDML(enableCustomDML);
            return this;
        }

        public Builder setDropRemovedItems(boolean dropRemovedItems) {
            pluginParameters.setDropRemovedItems(dropRemovedItems);
            return this;
        }

        public Builder setModelVersion(String modelVersion) {
            pluginParameters.setModelVersion(modelVersion);
            return this;
        }

        public Builder setIntermediaryBuild(boolean intermediaryBuild) {
            if (intermediaryBuild && pluginParameters.isDisableCompatibilityCheck()) {
                throw new PluginPropertiesException(ERROR_TEXT);
            }
            pluginParameters.setIntermediaryBuild(intermediaryBuild);
            return this;
        }

        public Builder setDisableCompatibilityCheck(boolean disableCompatibilityCheck) {
            if (pluginParameters.isIntermediaryBuild() && disableCompatibilityCheck) {
                throw new PluginPropertiesException(ERROR_TEXT);
            }
            pluginParameters.setDisableCompatibilityCheck(disableCompatibilityCheck);
            return this;
        }

        public Builder setLoadDictionaryByTable(boolean loadDictionaryByTable) {
            pluginParameters.setLoadDictionaryByTable(loadDictionaryByTable);
            return this;
        }

        public Builder setGenerateAllIndices(boolean generateAllIndices) {
            pluginParameters.setGenerateAllIndices(generateAllIndices);
            return this;
        }

        public Builder setHistoryUpdatedIndexStrategy(HistoryUpdatedIndexStrategy historyUpdatedIndexStrategy) {
            pluginParameters.setHistoryUpdatedIndexStrategy(historyUpdatedIndexStrategy);
            return this;
        }

        public Builder setExceptionOnDictionaryReference(boolean exceptionOnDictionaryReference) {
            pluginParameters.setExceptionOnDictionaryReference(exceptionOnDictionaryReference);
            return this;
        }

        public Builder setAllowLastChangeDateCompare(boolean allowLastChangeDateCompare) {
            pluginParameters.setAllowLastChangeDateCompare(allowLastChangeDateCompare);
            return this;
        }

        public Builder setAllowUseParentUniqueIndex(boolean allowUseParentUniqueIndex) {
            pluginParameters.setAllowUseParentUniqueIndex(allowUseParentUniqueIndex);
            return this;
        }

        public Builder setAllowNullWithCreateParam(boolean allowNullWithCreateParam) {
            pluginParameters.setAllowNullWithCreateParam(allowNullWithCreateParam);
            return this;
        }

        public Builder setAllowDictionaryPacket(boolean allowDictionaryPacket) {
            pluginParameters.setAllowDictionaryPacket(allowDictionaryPacket);
            return this;
        }

        public Builder setAllowCalculatedWithGetParam(boolean allowCalculatedWithGetParam) {
            pluginParameters.setAllowCalculatedWithGetParam(allowCalculatedWithGetParam);
            return this;
        }

        public Builder setEnableDictionaryDataCheck(boolean enableDictionaryDataCheck) {
            pluginParameters.setEnableDictionaryDataCheck(enableDictionaryDataCheck);
            return this;
        }

        public Builder setDisableAggregateRootReferenceCheck(boolean disableAggregateRootReferenceCheck) {
            pluginParameters.setDisableAggregateRootReferenceCheck(disableAggregateRootReferenceCheck);
            return this;
        }

        public Builder setDisableIncreaseVersionCheck(boolean disableIncreaseVersionCheck) {
            pluginParameters.setDisableIncreaseVersionCheck(disableIncreaseVersionCheck);
            return this;
        }

        public Builder setDeprecateDeletedItems(boolean deprecateDeletedItems) {
            pluginParameters.deprecateDeletedItems = deprecateDeletedItems;
            return this;
        }

        public Builder setDropUnusedSchemaItems(boolean dropUnusedSchemaItems) {
            pluginParameters.dropUnusedSchemaItems = dropUnusedSchemaItems;
            return this;
        }

        public Builder setDropDeletedItemsImmediately(boolean dropDeletedItemsImmediately) {
            if (!pluginParameters.isDeprecateDeletedItems() &&
                (pluginParameters.isIntermediaryBuild() || pluginParameters.isDisableCompatibilityCheck())) {
                pluginParameters.dropDeletedItemsImmediately = true;
            } else {
                pluginParameters.dropDeletedItemsImmediately = dropDeletedItemsImmediately;
            }
            return this;
        }

        public Builder setAllowDeleteNonDeprecatedItems(boolean allowDeleteNonDeprecatedItems) {
            if (!pluginParameters.isDeprecateDeletedItems() &&
                (pluginParameters.isIntermediaryBuild() || pluginParameters.isDisableCompatibilityCheck())) {
                pluginParameters.allowDeleteNonDeprecatedItems = true;
            } else {
                pluginParameters.allowDeleteNonDeprecatedItems = allowDeleteNonDeprecatedItems;
            }
            return this;
        }

        public Builder setEnableVersionedEntities(boolean enableVersionedEntities) {
            pluginParameters.enableVersionedEntities = enableVersionedEntities;
            return this;
        }

        public Builder setDisablePhysicNamesCheck(boolean disablePhysicNamesCheck) {
            pluginParameters.disablePhysicNamesCheck = disablePhysicNamesCheck;
            return this;
        }

        public Builder setOptimizeChangelog(boolean optimizeChangelog) {
            pluginParameters.optimizeChangelog = optimizeChangelog;
            return this;
        }

        public Builder setSkipCustomChangelogCheck(boolean skipCustomChangelogCheck) {
            pluginParameters.skipCustomChangelogCheck = skipCustomChangelogCheck;
            return this;
        }

        public Builder setDisableEmptyTypeReferenceCheck(boolean disableEmptyTypeReferenceCheck) {
            pluginParameters.disableEmptyTypeReferenceCheck = disableEmptyTypeReferenceCheck;
            return this;
        }

        public Builder setDisableGenerateOracleLiquibase(boolean disableGenerateOracleLiquibase) {
            pluginParameters.disableGenerateOracleLiquibase = disableGenerateOracleLiquibase;
            return this;
        }

        public Builder setGenerateAggRefValidationInfo(boolean generateAggRefValidationInfo) {
            pluginParameters.setGenerateAggRefValidationInfo(generateAggRefValidationInfo);
            return this;
        }

        public Builder setEnableModelNameChangeCheck(boolean enableModelNameChangeCheck) {
            pluginParameters.enableModelNameChangeCheck = enableModelNameChangeCheck;
            return this;
        }

        public Builder setMaxDictionaryFileSize(String maxDictionaryFileSize) {
            pluginParameters.maxDictionaryFileSize = DataSize.parse(maxDictionaryFileSize).toBytes();
            return this;
        }

        public Builder setForeignKeys(String foreignKeys) {
            pluginParameters.setForeignKeys(foreignKeys);
            return this;
        }

        public Builder setEnableDeleteCascade(boolean enableDeleteCascade) {
            pluginParameters.setEnableDeleteCascade(enableDeleteCascade);
            return this;
        }

        public Builder setCustomChangelogCDATAValidation(boolean customChangelogCDATAValidation) {
            pluginParameters.setCustomChangelogCDATAValidation(customChangelogCDATAValidation);
            return this;
        }

        public Builder setVersionCompatibility(String version) {
            pluginParameters.setVersionCompatibility(version);
            return this;
        }

        public Builder setImportSpecification(ImportSpecification importSpecification) {
            pluginParameters.setImportSpecification(importSpecification);
            return this;
        }

        public Builder setUseRenamedFields(boolean useRenamedFields) {
            pluginParameters.setUseRenamedFields(useRenamedFields);
            return this;
        }

        public Builder setDisableBaseEntityFields(boolean disableBaseEntityFields) {
            pluginParameters.setDisableBaseEntityFields(disableBaseEntityFields);
            return this;
        }

        public PluginParameters build() {
            if (pluginParameters.isOptimizeChangelog() && !pluginParameters.isLoadDictionaryByTable()) {
                throw new PluginParametersException("Disabled loading reference data through service table " +
                    "(parameter loadDictionaryByTable = false), optimization of the changelog cannot be performed!",
                    "It is necessary to disable changelog optimization (set the parameter optimizeChangelog = false," +
                        "or delete it from the build parameters), or enable downloading of reference data" +
                        "through the service table (set the parameter loadDictionaryByTable to true," +
                        "or delete it from the build parameters)");
            }
            return this.pluginParameters;
        }
    }
}
