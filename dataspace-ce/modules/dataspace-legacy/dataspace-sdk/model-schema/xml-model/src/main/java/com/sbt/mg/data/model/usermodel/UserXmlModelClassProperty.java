package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.sbt.mg.Helper;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.usermodel.interfaces.UserPropertyFromXml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserXmlModelClassProperty extends EntityDiff implements UserPropertyFromXml {

    public static final String DEFAULT_VALUE_TAG = "default-value";
    public static final String MANDATORY_TAG = "mandatory";
    public static final String LENGTH_TAG = "length";
    public static final String PRECISION_TAG = "precision";
    public static final String SCALE_TAG = "scale";
    public static final String TYPE_TAG = "type";
    public static final String COLLECTION_TAG = "collection";
    public static final String UNIQUE_TAG = "unique";
    public static final String CCI_INDEX = "cci-index";
    public static final String CCI_INDEX_NAME = "cci-index-name";
    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";
    public static final String HISTORICAL_TAG = "historical";
    public static final String INDEX_TAG = "index";
    public static final String PARENT_TAG = "parent";
    public static final String MAPPED_BY_TAG = "mappedBy";
    public static final String INTEGRITY_CHECK_TAG = "integrity-check";
    public static final String MASK_TAG = "mask";
    public static final String DEPRECATED_TAG = "isDeprecated";
    public static final String REFERENCE_CHECK_EXISTS_TAG = "check-exists";
    public static final String CCI_NAME = "cci-name";

    protected String name;
    protected String type;
    protected String label;
    protected CollectionType collectionType;
    protected Integer length;
    protected Integer scale;
    protected Boolean mandatory;
    protected Boolean index;
    protected Boolean unique;
    protected Boolean isDeprecated;
    protected String description;
    /** The flag that indicates that the field is historicized (historical="true" on the model) */
    protected Boolean historical;
    protected String mappedBy;
    protected Boolean isParent;
    protected String defaultValue;

    protected Boolean cciIndex;
    private String cciName;
    private String cciIndexName;
    protected List<UserXmlPropertySqlExpression> sqlExpressions = new ArrayList<>();

    @JsonIgnore
    protected Boolean unicode;

    /** Approved name was changed. 99.99% of people don't use it. But...
     * @see #integrityCheck
     */
    @Deprecated
    protected Boolean referenceCheckExists;
    protected Boolean integrityCheck;

    /** Sets an expression that is evaluated at the DB level. The consumer cannot set such a field directly. */
    protected String computedExpression;
    /**
     * regular expression that the value assigned to the property must match
     */
    private String mask;

    public UserXmlModelClassProperty() {
        this.label = "";
        this.description = "";
        this.cciIndex = Boolean.FALSE;
        this.integrityCheck = Boolean.FALSE;
    }

    @JsonCreator
    public UserXmlModelClassProperty(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                 @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                 @JacksonXmlProperty(isAttribute = true, localName = PRECISION_TAG) Integer precision,
                                 @JacksonXmlProperty(isAttribute = true, localName = SCALE_TAG) Integer scale,
                                 @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length,
                                 @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                                 @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                                 @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG) Boolean mandatory,
                                 @JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG) Boolean unique,
                                 @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG) Boolean index,
                                 @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean isDeprecated,
                                 @JacksonXmlProperty(isAttribute = true, localName = DEFAULT_VALUE_TAG) String defaultValue,
                                 @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                                 @JacksonXmlProperty(isAttribute = true, localName = PARENT_TAG) Boolean isParent,
                                 @JacksonXmlProperty(isAttribute = true, localName = MAPPED_BY_TAG) String mappedBy,
                                 @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX) Boolean cciIndex,
                                 @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_NAME) String cciIndexName,
                                 @JacksonXmlProperty(isAttribute = true, localName = CCI_NAME) String cciName,
                                 @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG) Boolean historical,
                                 @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_CHECK_EXISTS_TAG) Boolean referenceCheckExists,
                                 @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG) Boolean integrityCheck,
                                 @JacksonXmlProperty(isAttribute = true, localName = MASK_TAG) String mask

    ) {
        this.name = name;
        this.type = type;
        this.label = label;

        if (collection != null) {
            this.collectionType = CollectionType.valueOf(collection.toUpperCase());
        }

        this.length = length;

        this.scale = scale;

        this.mandatory = mandatory;
        this.index = index;
        this.unique = unique;
        this.isDeprecated = isDeprecated;
        this.description = description;
        this.defaultValue = defaultValue;
        this.isParent = isParent;
        this.mappedBy = mappedBy;
        this.cciIndex = cciIndex;
        this.cciName = cciName;
        this.cciIndexName = cciIndexName;
        this.historical = historical;

        this.referenceCheckExists = referenceCheckExists;
        this.integrityCheck = integrityCheck;
        this.mask = mask;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    public UserXmlModelClassProperty setName(String name) {
        this.name = name;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }


    public UserXmlModelClassProperty setType(String type) {
        this.type = type;
        if (ModelHelper.UNICODE_STRING_TYPE.equalsIgnoreCase(type)) {
            unicode  = true;
        }
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG)
    public CollectionType getCollectionType() {
        return collectionType;
    }

    public UserXmlModelClassProperty setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = Helper.replaceNullToEmpty(label);
    }

    public Boolean isParent() {
        return isParent;
    }

    @JacksonXmlProperty(isAttribute = true, localName = PARENT_TAG)
    public void setParent(Boolean parent) {
        isParent = parent;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG)
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @JacksonXmlProperty(isAttribute = true, localName = SCALE_TAG)
    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG)
    public boolean isMandatory() {
        return Boolean.TRUE.equals(mandatory);
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG)
    public Boolean isIndexed() {
        return index;
    }

    public void setIndex(Boolean isIndex) {
        this.index = isIndex;
    }

    /**
     * The need to check the external link
     * @see #isIntegrityCheck
     */
    @Deprecated
    @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_CHECK_EXISTS_TAG)
    public Boolean isReferenceCheckExists() {
        return referenceCheckExists;
    }

    /**
     * The need to check the external link
     * @see #setIntegrityCheck
     */
    @Deprecated
    public void setReferenceCheckExists(Boolean referenceCheckExists) {
        this.referenceCheckExists = Optional.ofNullable(referenceCheckExists).orElse(false);
    }

    @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG)
    public Boolean isIntegrityCheck() {
        return integrityCheck;
    }

    public void setIntegrityCheck(Boolean integrityCheck) {
        this.integrityCheck = Optional.ofNullable(integrityCheck).orElse(false);
    }

    /**
     * Returns the value of the parameter unique as is.
     *
     * @return
     */
    @JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG)
    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public void setDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public Boolean isDeprecated() {
        return isDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Helper.replaceNullToEmpty(description);
    }

    @JacksonXmlProperty(isAttribute = true, localName = MASK_TAG)
    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEFAULT_VALUE_TAG)
    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MAPPED_BY_TAG)
    public String getMappedBy() {
        return mappedBy;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX)
    public Boolean isCciIndex() {
        return cciIndex;
    }

    public void setCciIndex(Boolean cciIndex) {
        this.cciIndex = cciIndex;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CCI_NAME)
    public String getCciName() {
        return cciName;
    }

    public void setCciName(String cciName) {
        this.cciName = cciName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_NAME)
    public String getCciIndexName() {
        return cciIndexName;
    }

    public void setCciIndexName(String cciIndexName) {
        this.cciIndexName = cciIndexName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG)
    public Boolean isHistorical() {
        return historical;
    }

    public void setHistorical(Boolean historical) {
        this.historical = historical;
    }

    @JacksonXmlText
    public String getComputedExpression() {
        return computedExpression;
    }

    @JacksonXmlText
    public void setComputedExpression(String computedExpression) {
        this.computedExpression = Helper.beautifierExpression(computedExpression);
    }


    @JsonSetter(value = UserXmlPropertySqlExpression.SQL_EXPRESSION)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addSqlExpressions(List<UserXmlPropertySqlExpression> sqlExpressions) {
        sqlExpressions.forEach(this::addSqlExpressions);
    }

    public void addSqlExpressions(UserXmlPropertySqlExpression sqlExpression) {
        this.sqlExpressions.add(sqlExpression);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = UserXmlPropertySqlExpression.SQL_EXPRESSION)
    public List<UserXmlPropertySqlExpression> getSqlExpressions() {
        return Collections.unmodifiableList(sqlExpressions);
    }

}
