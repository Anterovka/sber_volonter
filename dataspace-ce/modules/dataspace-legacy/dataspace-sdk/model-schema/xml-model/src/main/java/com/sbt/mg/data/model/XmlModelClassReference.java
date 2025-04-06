package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.interfaces.XmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlModelClassReference;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.PropertyAccess;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

/**
 * Class model property
 */
@XmlTagName(XmlModelClass.REFERENCE_TAG)
public class XmlModelClassReference extends UserXmlModelClassReference implements XmlProperty<XmlModelClass> {


    public static final String REMOVED_TAG = "isRemoved";
    public static final String CHANGEABLE_TAG = "changeable";
    public static final String ACCESS_TAG = "access";
    public static final String VERSION_DEPRECATED_TAG = "ver-deprecated";
    public static final String VERSION_REMOVED_TAG = "ver-removed";

    public static final String REF_GENERATE_STRATEGY_TAG = "ref-generate-strategy";

    @JsonIgnore
    private XmlModelClass modelClass;

    private Boolean isRemoved;

    private PropertyAccess access;

    private Changeable changeable;

    private String versionDeprecated;
    private String versionRemoved;

    /**
     * Backward compatibility. If we had multiple reference collections before translation to generation
     * "in a new way" (now the type name includes the field name), then you need to continue generating the property and class
     * "in the old way" (the name included only the owner class and the reference type class).
     */
    private ReferenceGenerateStrategy referenceGenerateStrategy;
    // This is set only for collection references
    private boolean existsInPdm;

    public XmlModelClassReference() {
        access = PropertyAccess.PRIVATE;
    }


    @JsonCreator
    public XmlModelClassReference(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                  @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                  @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                                  @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                                  @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG) Boolean mandatory,
                                  @JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG) Boolean unique,
                                  @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG) Boolean index,
                                  @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean isDeprecated,
                                  @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG) Boolean isRemoved,
                                  @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                                  @JacksonXmlProperty(isAttribute = true, localName = CHANGEABLE_TAG) Changeable changeable,
                                  @JacksonXmlProperty(isAttribute = true, localName = ALIAS_TAG) String alias,
                                  @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG) Boolean historical,
                                  @JacksonXmlProperty(isAttribute = true, localName = ACCESS_TAG) PropertyAccess access,
                                  @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_TAG) Boolean cciIndex,
                                  @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_NAME_TAG) String cciIndexName,
                                  @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG) String versionDeprecated,
                                  @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG) String versionRemoved,
                                  @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_CHECK_EXISTS_TAG) Boolean referenceCheckExists,
                                  @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG) Boolean integrityCheck,
                                  @JacksonXmlProperty(isAttribute = true, localName = REF_GENERATE_STRATEGY_TAG) ReferenceGenerateStrategy referenceGenerateStrategy
    ) {
        super(
                name,
                type,
                collection,
                label,
                mandatory,
                unique,
                index,
                isDeprecated,
                description,
                alias,
                historical,
                cciIndex,
                cciIndexName,
                referenceCheckExists,
                integrityCheck
        );

        this.isRemoved = isRemoved;
        this.changeable = changeable;
        this.access = access == null ? PropertyAccess.PRIVATE : access;
        this.versionDeprecated = versionDeprecated;
        this.versionRemoved = versionRemoved;
        this.description = Helper.replaceNullToEmpty(description);
        this.label = Helper.replaceNullToEmpty(label);
        this.cciIndex = Boolean.TRUE.equals(cciIndex);
        this.integrityCheck = Optional.ofNullable(integrityCheck).orElse(Optional.ofNullable(referenceCheckExists).orElse(false));

        this.referenceGenerateStrategy = referenceGenerateStrategy;
    }

    /**
* Get model class
     */

    public XmlModelClassReference setModelClass(XmlModelClass modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    /**
* Get model class
     */
    @Nonnull
    @JsonIgnore
    public XmlModelClass getModelClass() {
        return modelClass;
    }

    /**
     * Method duplicates getModelClass().
     * Introduced to support a common interface
     */
    @JsonIgnore
    public XmlModelClass getParent() {
        return modelClass;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ACCESS_TAG)
    public PropertyAccess getAccess() {
        return access;
    }

    public XmlModelClassReference setAccess(PropertyAccess access) {
        this.access = access;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CHANGEABLE_TAG)
    public Changeable getChangeable() {
        return changeable;
    }

    public XmlModelClassReference setChangeable(Changeable changeable) {
        this.changeable = changeable;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG)
    public Boolean isRemoved() {
        return isRemoved == null ? Boolean.FALSE : isRemoved;
    }

    public XmlModelClassReference setRemoved(Boolean removed) {
        isRemoved = removed;
        return this;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG)
    public Boolean isMandatory() {
        return mandatory == null ? Boolean.FALSE : mandatory;
    }

    @Override
    public Boolean isUnique() {
        return unique == null ? Boolean.FALSE : unique;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public Boolean isDeprecated() {
        return isDeprecated == null ? Boolean.FALSE : isDeprecated;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG)
    public Boolean isIntegrityCheck() {
        return Optional.ofNullable(this.integrityCheck).orElse(Optional.ofNullable(this.referenceCheckExists).orElse(false));
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG)
    public Boolean isHistorical() {
        return historical == null ? Boolean.FALSE : historical;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG)
    public String getVersionDeprecated() {
        return versionDeprecated;
    }

    public XmlModelClassReference setVersionDeprecated(String versionDeprecated) {
        this.versionDeprecated = versionDeprecated;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG)
    public String getVersionRemoved() {
        return versionRemoved;
    }

    public XmlModelClassReference setVersionRemoved(String versionRemoved) {
        this.versionRemoved = versionRemoved;
        return this;
    }

    public ReferenceGenerateStrategy getReferenceGenerateStrategy() {
        return referenceGenerateStrategy;
    }

    public XmlModelClassReference setReferenceGenerateStrategy(ReferenceGenerateStrategy referenceGenerateStrategy) {
        this.referenceGenerateStrategy = referenceGenerateStrategy;
        return this;
    }

    public boolean isExistsInPdm() {
        return existsInPdm;
    }

    public XmlModelClassReference setExistsInPdm(boolean existsInPdm) {
        this.existsInPdm = existsInPdm;
        return this;
    }

    @Override
    public String toString() {
        return "Reference{"
                + "name='" + name + '\''
                + " in class = '" + (modelClass == null ? "not yet defined " : modelClass.getName()) + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlModelClassReference property = (XmlModelClassReference) o;
        return name.equals(property.name) &&
                modelClass.equals(property.modelClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, modelClass.getName());
    }
}
