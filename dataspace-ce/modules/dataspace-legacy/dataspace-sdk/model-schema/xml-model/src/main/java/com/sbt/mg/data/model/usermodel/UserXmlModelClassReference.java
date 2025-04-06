package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.interfaces.ReferenceFromXml;
import com.sbt.mg.data.model.interfaces.XmlTagName;

import javax.annotation.Nonnull;
import java.util.Optional;

@XmlTagName(XmlModelClass.REFERENCE_TAG)
public class UserXmlModelClassReference extends EntityDiff implements ReferenceFromXml {

    public static final String DEPRECATED_TAG = "isDeprecated";
    public static final String MANDATORY_TAG = "mandatory";
    public static final String TYPE_TAG = "type";
    public static final String NAME_TAG = "name";
    public static final String LABEL_TAG = "label";
    public static final String COLLECTION_TAG = "collection";
    public static final String DESCRIPTION_TAG = "description";
    public static final String HISTORICAL_TAG = "historical";
    public static final String CCI_INDEX_TAG = "cci-index";
    public static final String CCI_INDEX_NAME_TAG = "cci-index-name";
    public static final String ALIAS_TAG = "alias";
    public static final String REFERENCE_CHECK_EXISTS_TAG = "check-exists";
    public static final String INTEGRITY_CHECK_TAG = "integrity-check";
    public static final String UNIQUE_TAG = "unique";
    public static final String INDEX_TAG = "index";

    protected String name;
    private String type;
    protected String label;
    private CollectionType collectionType;
    protected Boolean mandatory;
    protected Boolean unique;
    private Boolean index;
    protected Boolean isDeprecated;
    protected String description;
    private String alias;
    /**Is the external link historicirable? */
    protected Boolean historical;

    protected Boolean cciIndex;
    private String cciIndexName;

    protected Boolean referenceCheckExists;
    protected Boolean integrityCheck;

    @JsonCreator
    public UserXmlModelClassReference(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                  @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                  @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                                  @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                                  @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG) Boolean mandatory,
                                  @JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG) Boolean unique,
                                  @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG) Boolean index,
                                  @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean isDeprecated,
                                  @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                                  @JacksonXmlProperty(isAttribute = true, localName = ALIAS_TAG) String alias,
                                  @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG) Boolean historical,
                                  @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_TAG) Boolean cciIndex,
                                  @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_NAME_TAG) String cciIndexName,
                                  @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_CHECK_EXISTS_TAG) Boolean referenceCheckExists,
                                  @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG) Boolean integrityCheck
    ) {
        this.name = name;
        this.type = type;
        this.label = label;

        if (collection != null) {
            this.collectionType = CollectionType.valueOf(collection.toUpperCase());
        }

        this.mandatory = mandatory;
        this.unique = unique;
        this.index = index;
        this.alias = alias;
        this.historical = historical;
        this.isDeprecated = isDeprecated;
        this.description = description;

        this.cciIndex = cciIndex;
        this.cciIndexName = cciIndexName;

        this.referenceCheckExists = referenceCheckExists;
        this.integrityCheck = integrityCheck;
    }

    public UserXmlModelClassReference() {
    }

    public String getName() {
        return name;
    }

    public ReferenceFromXml setName(String name) {
        this.name = name;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG)
    public String getType() {
        return type;
    }


    public ReferenceFromXml setType(String type) {
        this.type = type;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public ReferenceFromXml setLabel(String label) {
        this.label = Helper.replaceNullToEmpty(label);
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG)
    public CollectionType getCollectionType() {
        return collectionType;
    }

    public ReferenceFromXml setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG)
    public Boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ALIAS_TAG)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG)
    public Boolean isHistorical() {
        return historical;
    }

    public void setHistorical(Boolean historical) {
        this.historical = historical;
    }

    public Boolean isUnique() {
        return unique;
    }

    @JsonIgnore
    public Boolean getUnique() {
        return this.unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG)
    public Boolean getIndex() {
        return this.index;
    }

    public void setIndex(Boolean index) {
        this.index = index;
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

    @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_TAG)
    public Boolean isCciIndex() {
        return cciIndex;
    }

    public void setCciIndex(boolean cciIndex) {
        this.cciIndex = cciIndex;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_NAME_TAG)
    public String getCciIndexName() {
        return cciIndexName;
    }

    public void setCciIndexName(String cciIndexName) {
        this.cciIndexName = cciIndexName;
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
}
