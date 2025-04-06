package com.sbt.dataspace.pdm.xml;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class XmlMetaInformation {
    public static final String PLUGIN_VERSION_TAG = "plugin-version";
    public static final String HAVE_INSERT_ROOT_DICTIONARY_TAG = "have-insert-root-dictionary";
    public static final String HAVE_INSERT_ROOT_SECURITY_TAG = "have-insert-root-security";
    public static final String HAVE_H2_DECODE_BASE64_FUNCTION_TAG = "have-h2-decode-base64-function";
    public static final String ALLOW_LASTCHANGEDATE_COMPARE_TAG = "allow-lastchangedate-compare";
    public static final String UPDATED_CNGCNT_SYSVERSION_TAG = "updatedCngCntSysVersion";
    public static final String ADDED_PRECONDITION = "addedPrecondition";
    public static final String DISABLED_GENERATE_ORACLE_LIQUIBASE = "disabledGenerateOracleLiquibase";
    public static final String USE_RENAMED_FIELDS = "useRenamedFields";
    public static final String DISABLED_BASE_ENTITY_FIELDS = "disableBaseEntityFields";
    public static final String ALLOW_USE_PARENT_UNIQUE_INDEX = "allowUseParentUniqueIndex";

    private String pluginVersion;
    private String haveInsertRootDictionary;
    private String haveInsertRootSecurity;
    private String haveH2DecodeBase64Function;
    private String allowLastChangeDateCompare;
    private String useRenamedFields;
    private String disableBaseEntityFields;
    /**
     * When versioned-entities is enabled and the value of chgCht is null, Hibernate crashes with an NPE during increment.
     * Update entity SysVersions chgCnt and set flag to true to analyze further and not update.
     */
    private String updatedCngCntSysVersion;

    /**
     * Flag indicating that the previous changelog had a preConditions section added to the changeSets on
     * - setColumnRemarks
     * - setTableRemarks
     * - modifyDataType
     */
    private String addedPrecondition;

    private String disabledGenerateOracleLiquibase;

    /**
     * When set to true, enables the use of unique indexes from parent classes in the updateOrCreate command
     * regardless of the inheritance strategy. If the value is absent or false, usage is permitted with the strategy
     * inheritance SINGLE_TABLE
     */
    private String allowUseParentUniqueIndex;

    public XmlMetaInformation() {
    }

    @JsonCreator
    public XmlMetaInformation(@JacksonXmlProperty(localName = PLUGIN_VERSION_TAG) String pluginVersion,
                              @JacksonXmlProperty(localName = HAVE_INSERT_ROOT_DICTIONARY_TAG) String haveInsertRootDictionary,
                              @JacksonXmlProperty(localName = HAVE_INSERT_ROOT_SECURITY_TAG) String haveInsertRootSecurity,
                              @JacksonXmlProperty(localName = HAVE_H2_DECODE_BASE64_FUNCTION_TAG) String haveH2DecodeBase64Function,
                              @JacksonXmlProperty(localName = ALLOW_LASTCHANGEDATE_COMPARE_TAG) String allowLastChangeDateCompare,
                              @JacksonXmlProperty(localName = UPDATED_CNGCNT_SYSVERSION_TAG) String updatedCngCntSysVersion,
                              @JacksonXmlProperty(localName = ADDED_PRECONDITION) String addedPrecondition,
                              @JacksonXmlProperty(localName = DISABLED_GENERATE_ORACLE_LIQUIBASE) String disabledGenerateOracleLiquibase,
                              @JacksonXmlProperty(localName = USE_RENAMED_FIELDS) String useRenamedFields,
                              @JacksonXmlProperty(localName = DISABLED_BASE_ENTITY_FIELDS) String disableBaseEntityFields,
                              @JacksonXmlProperty(localName = ALLOW_USE_PARENT_UNIQUE_INDEX) String allowUseParentUniqueIndex
    ) {
        this.pluginVersion = pluginVersion;
        this.haveInsertRootDictionary = haveInsertRootDictionary;
        this.haveInsertRootSecurity = haveInsertRootSecurity;
        this.haveH2DecodeBase64Function = haveH2DecodeBase64Function;
        this.allowLastChangeDateCompare = allowLastChangeDateCompare;
        this.updatedCngCntSysVersion = updatedCngCntSysVersion;
        this.addedPrecondition = addedPrecondition;
        this.disabledGenerateOracleLiquibase = disabledGenerateOracleLiquibase;
        this.useRenamedFields = useRenamedFields;
        this.disableBaseEntityFields = disableBaseEntityFields;
        this.allowUseParentUniqueIndex = allowUseParentUniqueIndex;
    }

    @JacksonXmlProperty(localName = PLUGIN_VERSION_TAG)
    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    @JacksonXmlProperty(localName = HAVE_INSERT_ROOT_DICTIONARY_TAG)
    public String getHaveInsertRootDictionary() {
        return haveInsertRootDictionary;
    }

    public void setHaveInsertRootDictionary(String haveInsertRootDictionary) {
        this.haveInsertRootDictionary = haveInsertRootDictionary;
    }

    @JacksonXmlProperty(localName = HAVE_INSERT_ROOT_SECURITY_TAG)
    public String getHaveInsertRootSecurity() {
        return haveInsertRootSecurity;
    }

    public void setHaveInsertRootSecurity(String haveInsertRootSecurity) {
        this.haveInsertRootSecurity = haveInsertRootSecurity;
    }

    @JacksonXmlProperty(localName = HAVE_H2_DECODE_BASE64_FUNCTION_TAG)
    public String getHaveH2DecodeBase64Function() {
        return haveH2DecodeBase64Function;
    }

    public void setHaveH2DecodeBase64Function(String haveH2DecodeBase64Function) {
        this.haveH2DecodeBase64Function = haveH2DecodeBase64Function;
    }

    @JacksonXmlProperty(localName = ALLOW_LASTCHANGEDATE_COMPARE_TAG)
    public String getAllowLastChangeDateCompare() {
        return allowLastChangeDateCompare;
    }

    public void setAllowLastChangeDateCompare(String allowLastChangeDateCompare) {
        this.allowLastChangeDateCompare = allowLastChangeDateCompare;
    }

    @JacksonXmlProperty(localName = UPDATED_CNGCNT_SYSVERSION_TAG)
    public String getUpdatedCngCntSysVersion() {
        return updatedCngCntSysVersion;
    }

    public void setUpdatedCngCntSysVersion(String updatedCngCntSysVersion) {
        this.updatedCngCntSysVersion = updatedCngCntSysVersion;
    }

    @JacksonXmlProperty(localName = ADDED_PRECONDITION)
    public String getAddedPrecondition() {
        return addedPrecondition;
    }

    public void setAddedPrecondition(String addedPrecondition) {
        this.addedPrecondition = addedPrecondition;
    }

    @JacksonXmlProperty(localName = DISABLED_GENERATE_ORACLE_LIQUIBASE)
    public String getDisabledGenerateOracleLiquibase() {
        return disabledGenerateOracleLiquibase;
    }

    public void setDisabledGenerateOracleLiquibase(String disabledGenerateOracleLiquibase) {
        this.disabledGenerateOracleLiquibase = disabledGenerateOracleLiquibase;
    }

    @JacksonXmlProperty(localName = USE_RENAMED_FIELDS)
    public String getUseRenamedFields() {
        return useRenamedFields;
    }

    public void setUseRenamedFields(String useRenamedFields) {
        this.useRenamedFields = useRenamedFields;
    }

    @JacksonXmlProperty(localName = DISABLED_BASE_ENTITY_FIELDS)
    public String getDisableBaseEntityFields() {
        return disableBaseEntityFields;
    }

    public void setDisableBaseEntityFields(String disableBaseEntityFields) {
        this.disableBaseEntityFields = disableBaseEntityFields;
    }

    @JacksonXmlProperty(localName = ALLOW_USE_PARENT_UNIQUE_INDEX)
    public String getAllowUseParentUniqueIndex() {
        return allowUseParentUniqueIndex;
    }

    public void setAllowUseParentUniqueIndex(String allowUseParentUniqueIndex) {
        this.allowUseParentUniqueIndex = allowUseParentUniqueIndex;
    }
}
