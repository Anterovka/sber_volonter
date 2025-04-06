package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.interfaces.PropertyFromXml;
import com.sbt.mg.data.model.interfaces.XmlProperty;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlModelClassProperty;
import com.sbt.mg.exception.checkmodel.ScalePrecisionBothInitException;
import com.sbt.mg.exception.checkmodel.TableNameAlreadyDefinedException;
import com.sbt.mg.exception.common.EmbeddedListNotFoundException;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.ObjectLinks;
import com.sbt.parameters.enums.PropertyAccess;
import com.sbt.parameters.enums.TemporalType;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.sbt.mg.ModelHelper.TYPES_INFO;
import static com.sbt.mg.ModelHelper.isModelClassType;

/**
 * Class model property
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(XmlModelClass.PROPERTY_TAG)
public class XmlModelClassProperty extends UserXmlModelClassProperty implements PropertyFromXml, XmlProperty<XmlModelClass> {

    public static final String ID_NAME_TAG = "id";
    public static final String COLUMN_NAME_TAG = "column-name";
    public static final String COLLECTION_TABLE_TAG = "collection-table-name";
    public static final String COLLECTION_PK_INDEX_NAME_TAG = "collection-pk-index-name";
    public static final String KEY_COLUMN_NAME_TAG = "key-column-name";
    public static final String REMOVED_TAG = "isRemoved";
    public static final String ENUM_TAG = "enum";
    public static final String ORIGINAL_TYPE_TAG = "original-type";
    public static final String EXTERNAL_SOFT_REFERENCE_TAG = "external-soft-reference";
    public static final String COLUMN_TYPE_NAME_TAG = "column-type";
    public static final String SOURCEID_TAG = "sourceId";
    public static final String EMBEDDED_TAG = "embedded";
    public static final String CDM_NAME_TAG = "cdm-name";
    public static final String ORDER_COLUMN_TAG = "order-column";
    public static final String EXTERNAL_LINK_TAG = "external-link";
    public static final String IS_SYSTEM_TAG = "system-field";
    public static final String VERSION_TAG = "version-field";
    public static final String CHANGEABLE_TAG = "changeable";
    public static final String OBJECT_LINK_TAG = "object-link";
    public static final String ACCESS_TAG = "access";
    public static final String UNICODE_TAG = "unicode";
    public static final String STATUS_REASON_TAG = "status-reason";
    public static final String VERSION_DEPRECATED_TAG = "ver-deprecated";
    public static final String VERSION_REMOVED_TAG = "ver-removed";
    public static final String FK_GENERATED_TAG = "fk-generated";
    public static final String FK_NAME_TAG = "fk-name";
    public static final String FK_DELETE_CASCADE_TAG = "fk-delete-cascade";

    public static final String REF_GENERATE_STRATEGY_TAG = "ref-generate-strategy";

    @JsonIgnore
    private XmlModelClass modelClass;
    private String columnName;
    /**
     * The name of the table in which the collection is located, if the collection property
     */
    private String collectionTableName;
    private Boolean isRemoved;
    private String sourceId;
    private String orderColumnName;
    private Boolean id;
    /**
     * The field is a reference
     */
    private Boolean externalLink;

    private Boolean version;
    private boolean isEnum;

    private ObjectLinks objectLinks;
    private PropertyAccess access;

    /**
     * Specifies that the property type is an embeddable class
     */
    private Boolean embedded;
    /**
     * Mapping field to corporate model. Lives thanks to indulgence of Roman Borisovich.
     */
    private String cdmName;
    private Changeable changeable;

    /**
     * Information about the physical type of property
     */
    @JsonIgnore
    private TypeInfo typeInfo;
    @JsonIgnore
    private String referenceType;
    /**
     * The original type of the field. For a soft reference, it is replaced. You need to know the original.
     * Necessary for the SDK generator.
     */
    private String originalType;
    @JsonIgnore
    private PropertyType category;
    /**
     * The name of the key column in the collection, if the property is collectible
     */
    @JsonIgnore
    private String keyColumnName;
    /* Name of the PK index for the table of the primitive collection field */
// раньше данное значение не сохранялось в pdm.
    private String collectionPkIndexName;
    @JsonIgnore
    private String columnType;

    /**
     * Reference to an external object/class. Not in the current model
     */
    private Boolean isExternalSoftReference;

    /** A sign that the field is the reason for the status change */
// This sign is analyzed, for example, when checking the duplication of names on the SingleTable table
    private boolean isStatusReason;

    private String versionDeprecated;

    private String versionRemoved;

    @JsonIgnore
    private TemporalType temporalType;

    /**
     * Backward compatibility. If we had multiple reference collections before translation to generation
     * "in a new way" (now the type name includes the field name), then you need to continue generating the property and class
     * "in the old way" (the name included only the owner class and the reference type class).
     */
    private ReferenceGenerateStrategy referenceGenerateStrategy;

    // The name of the property for which the updated historicality property is intended
    private String historyUpdatedFor;

    private boolean fkGenerated;
    private String fkName;
    private boolean fkDeleteCascade;

    // Fill in with default values. Should match the JsonCreator constructor.
    public XmlModelClassProperty() {
        this.access = PropertyAccess.PRIVATE;
        this.unicode = Boolean.FALSE;
    }


    @JsonCreator
    public XmlModelClassProperty(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                                 @JacksonXmlProperty(isAttribute = true, localName = TYPE_TAG) String type,
                                 @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_SOFT_REFERENCE_TAG) Boolean isExternalSoftReference,
                                 @JacksonXmlProperty(isAttribute = true, localName = ORIGINAL_TYPE_TAG) String originalType,
                                 @JacksonXmlProperty(isAttribute = true, localName = PRECISION_TAG) Integer precision,
                                 @JacksonXmlProperty(isAttribute = true, localName = SCALE_TAG) Integer scale,
                                 @JacksonXmlProperty(isAttribute = true, localName = LENGTH_TAG) Integer length,
                                 @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TAG) String collection,
                                 @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                                 @JacksonXmlProperty(isAttribute = true, localName = MANDATORY_TAG) Boolean mandatory,
                                 @JacksonXmlProperty(isAttribute = true, localName = UNIQUE_TAG) Boolean unique,
                                 @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG) Boolean index,
                                 @JacksonXmlProperty(isAttribute = true, localName = COLUMN_NAME_TAG) String columnName,
                                 @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TABLE_TAG) String collectionTableName,
                                 @JacksonXmlProperty(isAttribute = true, localName = KEY_COLUMN_NAME_TAG) String keyColumnName,
                                 @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean isDeprecated,
                                 @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG) Boolean isRemoved,
                                 @JacksonXmlProperty(isAttribute = true, localName = EMBEDDED_TAG) Boolean embedded,
                                 @JacksonXmlProperty(isAttribute = true, localName = DEFAULT_VALUE_TAG) String defaultValue,
                                 @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                                 @JacksonXmlProperty(isAttribute = true, localName = COLUMN_TYPE_NAME_TAG) String columnType, // ignore
                                 @JacksonXmlProperty(isAttribute = true, localName = CDM_NAME_TAG) String cdmName,
                                 @JacksonXmlProperty(isAttribute = true, localName = SOURCEID_TAG) String sourceId,
                                 @JacksonXmlProperty(isAttribute = true, localName = ID_NAME_TAG) Boolean id,
                                 @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_LINK_TAG) Boolean externalLink,
                                 @JacksonXmlProperty(isAttribute = true, localName = IS_SYSTEM_TAG) Boolean isSystemField,
                                 @JacksonXmlProperty(isAttribute = true, localName = VERSION_TAG) Boolean version,
                                 @JacksonXmlProperty(isAttribute = true, localName = CHANGEABLE_TAG) Changeable changeable,
                                 @JacksonXmlProperty(isAttribute = true, localName = PARENT_TAG) Boolean isParent,
                                 @JacksonXmlProperty(isAttribute = true, localName = MAPPED_BY_TAG) String mappedBy,
                                 @JacksonXmlProperty(isAttribute = true, localName = OBJECT_LINK_TAG) ObjectLinks objectLinks,
                                 @JacksonXmlProperty(isAttribute = true, localName = ENUM_TAG) Boolean isEnum,
                                 @JacksonXmlProperty(isAttribute = true, localName = ACCESS_TAG) PropertyAccess access,
                                 @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX) Boolean cciIndex,
                                 @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX_NAME) String cciIndexName,
                                 @JacksonXmlProperty(isAttribute = true, localName = CCI_NAME) String cciName,
                                 @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG) Boolean historical,
                                 @JacksonXmlProperty(isAttribute = true, localName = UNICODE_TAG) Boolean unicode,
                                 @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_CHECK_EXISTS_TAG) Boolean referenceCheckExists,
                                 @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG) Boolean integrityCheck,
                                 @JacksonXmlProperty(isAttribute = true, localName = MASK_TAG) String mask,
                                 @JacksonXmlProperty(isAttribute = true, localName = STATUS_REASON_TAG) Boolean isStatusReason,
                                 @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG) String versionDeprecated,
                                 @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG) String versionRemoved,
                                 @JacksonXmlProperty(isAttribute = true, localName = REF_GENERATE_STRATEGY_TAG) ReferenceGenerateStrategy referenceGenerateStrategy,
                                 @JacksonXmlProperty(isAttribute = true, localName = FK_GENERATED_TAG) Boolean fkGenerated,
                                 @JacksonXmlProperty(isAttribute = true, localName = FK_NAME_TAG) String fkName,
                                 @JacksonXmlProperty(isAttribute = true, localName = FK_DELETE_CASCADE_TAG) Boolean fkDeleteCascade

    ) {
        super(
                name,
                type,
                precision,
                scale,
                length,
                collection,
                label,
                mandatory,
                unique,
                index,
                isDeprecated,
                defaultValue,
                description,
                isParent,
                mappedBy,
                cciIndex,
                cciIndexName,
                cciName,
                historical,
                referenceCheckExists,
                integrityCheck,
                mask
        );
        this.isExternalSoftReference = isExternalSoftReference;
        this.originalType = originalType;

        if (collection != null) {
            this.collectionType = CollectionType.valueOf(collection.toUpperCase());
        }

        if (precision != null && scale != null) {
            throw new ScalePrecisionBothInitException(name);
        }
        this.columnName = columnName;
        this.collectionTableName = collectionTableName;
        this.keyColumnName = keyColumnName;
        this.isRemoved = isRemoved;
        this.sourceId = sourceId;
        this.columnType = columnType;
        this.embedded = embedded;
        this.cdmName = cdmName;
        this.id = id;
        this.externalLink = externalLink;
        this.version = version;
        this.changeable = changeable;
        this.objectLinks = objectLinks;
        this.isEnum = Boolean.TRUE.equals(isEnum);
        this.access = access == null ? PropertyAccess.PRIVATE : access;
        this.unicode = Boolean.TRUE.equals(unicode) || "unicodestring".equalsIgnoreCase(type);

        this.isStatusReason = Boolean.TRUE.equals(isStatusReason);
        this.versionDeprecated = versionDeprecated;
        this.versionRemoved = versionRemoved;
        this.referenceGenerateStrategy = referenceGenerateStrategy;
        this.label = Helper.replaceNullToEmpty(label);
        this.cciIndex = Boolean.TRUE.equals(cciIndex);
        this.description = Helper.replaceNullToEmpty(description);
        this.scale = precision == null ? scale : precision;
        this.integrityCheck = Optional.ofNullable(integrityCheck).orElse(Optional.ofNullable(referenceCheckExists).orElse(false));
        this.fkGenerated = Boolean.TRUE.equals(fkGenerated);
        this.fkName = fkName;
        this.fkDeleteCascade = Boolean.TRUE.equals(fkDeleteCascade);
    }

    /**
     * Creating a new "clean" class property
     */
    public static XmlModelClassProperty create() {
        return new XmlModelClassProperty();
    }

    /**
     * Get model class
     */
    public XmlModelClassProperty setModelClass(XmlModelClass modelClass) {
        this.modelClass = modelClass;
        return this;
    }

    /**
     * Get model class
     */
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

    @Override
    public Boolean isParent() {
        return isParent == null ? Boolean.FALSE : isParent;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    @Override
    public Boolean isDeprecated() {
        return Boolean.TRUE.equals(isDeprecated);
    }

    @JsonIgnore
    public Boolean getMandatory() {
        return mandatory;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG)
    public Boolean isIndexed() {
        return Boolean.TRUE.equals(index);
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = CCI_INDEX)
    public Boolean isCciIndex() {
        return Boolean.TRUE.equals(cciIndex);
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = HISTORICAL_TAG)
    public Boolean isHistorical() {
        return historical == null ? Boolean.FALSE : historical;
    }

    @JacksonXmlProperty(isAttribute = true, localName = INTEGRITY_CHECK_TAG)
    public Boolean isIntegrityCheck() {
        return Optional.ofNullable(this.integrityCheck).orElse(Optional.ofNullable(this.referenceCheckExists).orElse(false));
    }

    @JacksonXmlProperty(isAttribute = true, localName = ACCESS_TAG)
    public PropertyAccess getAccess() {
        return access;
    }

    public XmlModelClassProperty setAccess(PropertyAccess access) {
        this.access = access;
        return this;
    }

    @Override
    public XmlModelClassProperty setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public XmlModelClassProperty setType(String type) {
        this.type = type;
        if (ModelHelper.UNICODE_STRING_TYPE.equalsIgnoreCase(type)) {
            unicode  = true;
        }
        return this;
    }

    @Override
    public XmlModelClassProperty setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
        return this;
    }

    @JsonIgnore
    public String getReferenceType() {
        return referenceType;
    }


    public XmlModelClassProperty setReferenceType(String referenceType) {
        this.referenceType = referenceType;
        return this;
    }

    @JsonIgnore
    public XmlModelClassProperty setParent() {
        this.isParent = Boolean.TRUE;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = OBJECT_LINK_TAG)
    public ObjectLinks getObjectLinks() {
        return objectLinks;
    }

    public XmlModelClassProperty setObjectLinks(ObjectLinks objectLinks) {
        this.objectLinks = objectLinks;
        return this;
    }

    @JsonIgnore
    public Boolean getIndexed() {
        return this.index;
    }

    /**
     * Get information about type
     */
    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_SOFT_REFERENCE_TAG)
    public Boolean isExternalSoftReference() {
        return Boolean.TRUE.equals(isExternalSoftReference);
    }

    public XmlModelClassProperty setExternalSoftReference(Boolean externalSoftReference) {
        isExternalSoftReference = externalSoftReference;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ORIGINAL_TYPE_TAG)
    public String getOriginalType() {
        return originalType;
    }

    public XmlModelClassProperty setOriginalType(String originalType) {
        this.originalType = originalType;
        return this;
    }

    /**
     * Set information about type
     */
    public XmlModelClassProperty setTypeInfo(TypeInfo typeInfo) {
        this.typeInfo = new TypeInfo(typeInfo);
        if (this.length == null) {
            this.length = typeInfo.getFirstNumber();
        }
        if (this.scale == null) {
            this.scale = typeInfo.getSecondNumber();
        }

        if (!"clob".equals(typeInfo.getHbmName())) {
            this.typeInfo.setFirstNumber(length);
            this.typeInfo.setSecondNumber(scale);
        }
        return this;
    }

    /**
     * Get category (link or primitive) of property
     */
    public PropertyType getCategory() {
        return category;
    }

    public XmlModelClassProperty setCategory(PropertyType category) {
        this.category = category;
        return this;
    }

    /**
     * Get column name
     */
    @JacksonXmlProperty(isAttribute = true, localName = COLUMN_NAME_TAG)
    public String getColumnName() {
        return columnName;
    }

    /**
     * Set the column name
     */
    public XmlModelClassProperty setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Get the name of the table in which the collection is located
     */
    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_TABLE_TAG)
    public String getCollectionTableName() {
        return collectionTableName;
    }

    /**
     * Set table name for collection
     */
    public XmlModelClassProperty setCollectionTableName(String collectionTableName) {
        XmlModel model = getModelClass().getModel();
        if (this.collectionTableName != null) {
            model.removeTableName(this.collectionTableName);
        }
// Control of absence of duplication of table names on the model
        if (collectionTableName != null) {
            if (model.containsTableName(collectionTableName)) {
                throw new TableNameAlreadyDefinedException(collectionTableName);
            }
            model.addTableName(collectionTableName);
        }
        this.collectionTableName = collectionTableName;
        return this;
    }

    /**
     * Get the name of the key column in the collection
     */
    @JacksonXmlProperty(isAttribute = true, localName = KEY_COLUMN_NAME_TAG)
    public String getKeyColumnName() {
        return keyColumnName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CDM_NAME_TAG)
    public String getCdmName() {
        return cdmName;
    }

    public XmlModelClassProperty setCdmName(String cdmName) {
        this.cdmName = cdmName;
        return this;
    }

    /**
     * Set the name of the key column in the collection
     */
    public XmlModelClassProperty setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
        return this;
    }

    /**
     * Get the name of the order column in the collection
     */
    @JacksonXmlProperty(isAttribute = true, localName = ORDER_COLUMN_TAG)
    public String getOrderColumnName() {
        return orderColumnName;
    }

    /**
     * Set the column name for collection order
     */
    public XmlModelClassProperty setOrderColumnName(String orderColumnName) {
        this.orderColumnName = orderColumnName;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CHANGEABLE_TAG)
    public Changeable getChangeable() {
        return changeable;
    }

    public XmlModelClassProperty setChangeable(Changeable changeable) {
        this.changeable = changeable;
        return this;
    }

    /**
     * Get the name of the index for the primary key of the collection
     */
    @JacksonXmlProperty(isAttribute = true, localName = COLLECTION_PK_INDEX_NAME_TAG)
    public String getCollectionPkIndexName() {
        return collectionPkIndexName;
    }

    /**
     * Set the index name for the primary key of the collection
     */
    public XmlModelClassProperty setCollectionPkIndexName(String collectionPkIndexName) {
        this.collectionPkIndexName = collectionPkIndexName;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = IS_SYSTEM_TAG)
    @Deprecated
    public boolean isSystemField() {
        return changeable == Changeable.SYSTEM;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EMBEDDED_TAG)
    public boolean isEmbedded() {
        return embedded == null ? Boolean.FALSE : embedded;
    }

    public XmlModelClassProperty setEmbedded(Boolean embedded) {
        this.embedded = embedded;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_TAG)
    public boolean isVersion() {
        return version == null ? Boolean.FALSE : version;
    }

    public XmlModelClassProperty setVersion(Boolean version) {
        this.version = version;
        return this;
    }

    /**
     * Returns an effective value for the parameter unique. If not true, then means false
     *
     * @return
     */
    @JsonIgnore
    public boolean isUnique() {
        return Boolean.TRUE.equals(unique);
    }

    public XmlModelClassProperty setDeprecated() {
        setDeprecated(true);
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG)
    public Boolean isRemoved() {
        return isRemoved == null ? Boolean.FALSE : isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    @JacksonXmlProperty(isAttribute = true, localName = SOURCEID_TAG)
    public String getSourceId() {
        return sourceId == null ? "" : sourceId;
    }

    public XmlModelClassProperty setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ID_NAME_TAG)
    public Boolean isId() {
        return id == null ? Boolean.FALSE : id;
    }

    public XmlModelClassProperty setId(Boolean id) {
        this.id = id;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_LINK_TAG)
    public Boolean isExternalLink() {
        return Boolean.TRUE.equals(externalLink);
    }

    public XmlModelClassProperty setExternalLink(Boolean externalLink) {
        this.externalLink = externalLink;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = COLUMN_TYPE_NAME_TAG)
    public String getColumnType() {
        return typeInfo == null ? columnType : ModelHelper.getPropertyDbType(this);
    }

    /**
     * Returns the mappedBy property or null
     */
    @JsonIgnore
    public XmlModelClassProperty getMappedByProperty() {
        return mappedBy == null ? null : getModelClass().getModel().getClass(type).getProperty(mappedBy);
    }

    @JacksonXmlProperty(isAttribute = true, localName = ENUM_TAG)
    public boolean isEnum() {
        return isEnum;
    }

    public XmlModelClassProperty setEnum(Boolean isEnum) {
        this.isEnum = Boolean.TRUE == isEnum;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = UNICODE_TAG)
    public Boolean isUnicode() {
        return Boolean.TRUE.equals(unicode);
    }

    @JsonIgnore
    public Boolean getUnicode() {
        return unicode;
    }

    public XmlModelClassProperty setUnicode(Boolean unicode) {
        this.unicode = unicode;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = STATUS_REASON_TAG)
    public boolean isStatusReason() {
        return isStatusReason;
    }

    public XmlModelClassProperty setStatusReason(boolean statusReason) {
        isStatusReason = statusReason;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG)
    public String getVersionDeprecated() {
        return versionDeprecated;
    }

    public XmlModelClassProperty setVersionDeprecated(String versionDeprecated) {
        this.versionDeprecated = versionDeprecated;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG)
    public String getVersionRemoved() {
        return versionRemoved;
    }

    public void setVersionRemoved(String versionRemoved) {
        this.versionRemoved = versionRemoved;
    }

    public void setTemporalType(TemporalType temporalType) {
        this.temporalType = temporalType;
    }

    @JsonIgnore
    public TemporalType getTemporalType() {
        return temporalType;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REF_GENERATE_STRATEGY_TAG)
    public ReferenceGenerateStrategy getReferenceGenerateStrategy() {
        return referenceGenerateStrategy;
    }

    public XmlModelClassProperty setReferenceGenerateStrategy(ReferenceGenerateStrategy referenceGenerateStrategy) {
        this.referenceGenerateStrategy = referenceGenerateStrategy;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = FK_GENERATED_TAG)
    public boolean isFkGenerated() {
        return fkGenerated;
    }

    public XmlModelClassProperty setFkGenerated(boolean fkGenerated) {
        this.fkGenerated = fkGenerated;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = FK_NAME_TAG)
    public String getFkName() {
        return fkName;
    }

    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = FK_DELETE_CASCADE_TAG)
    public boolean isFkDeleteCascade() {
        return fkDeleteCascade;
    }

    public void setFkDeleteCascade(boolean fkDeleteCascade) {
        this.fkDeleteCascade = fkDeleteCascade;
    }

    @Override
    public String toString() {
        return "Property{"
                + "name='" + getName() + '\''
                + " in class = '" + (getModelClass() == null ? "not yet defined " : getModelClass().getName()) + '\''
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
        XmlModelClassProperty property = (XmlModelClassProperty) o;
        return name.equals(property.name) &&
            modelClass.equals(property.modelClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getModelClass().getName());
    }

    @JsonIgnore
    public PropertyExtractResult extractFromPdmProperty() {

        if (isPdmReference()) {
            XmlModelClassReference userReference = new XmlModelClassReference();
            userReference.setName(this.name);
            userReference.setType(ModelHelper.transformReferenceTypeToOriginClassName(this));
            userReference.setLabel(this.label);
            userReference.setCollectionType(this.collectionType);
            userReference.setMandatory(this.mandatory);
            userReference.setDescription(this.description);
            userReference.setCciIndex(this.isCciIndex());
            userReference.setReferenceGenerateStrategy(this.getReferenceGenerateStrategy());

            return new PropertyExtractResult(userReference);
        } else if (isUserProperty()) {
            XmlModelClassProperty userProperty = new XmlModelClassProperty();
            userProperty.setName(this.name);
            userProperty.setType(Objects.equals(this.columnType, "CLOB") ? "text" : this.type);
            userProperty.setLabel(this.label);
            userProperty.setChangeable(this.changeable);
            userProperty.setUnicode(this.unicode);
            userProperty.setCollectionType(this.collectionType);
            userProperty.setLength(isNeedLength() ? this.length : null);

            // scale we set only properties with the type BigDecimal, as there is a check checkScaleOnlyOnBigDecimal()
            TypeInfo PropertyTypeInfo = TYPES_INFO.get(userProperty.getType().toLowerCase(Locale.ENGLISH));
            if (BigDecimal.class.getSimpleName().equals(PropertyTypeInfo == null ? null : PropertyTypeInfo.getJavaName())) {
                userProperty.setScale(this.scale);
            }

            userProperty.setMandatory(this.mandatory);
            userProperty.setDescription(this.description);
            userProperty.setId(this.id);
            userProperty.setHistorical(this.historical);
            userProperty.setMappedBy(this.mappedBy);
            userProperty.setParent(this.isParent);
            userProperty.setDefaultValue(this.defaultValue);
            userProperty.setComputedExpression(this.computedExpression);
            userProperty.setColumnName(this.columnName);
            userProperty.setKeyColumnName(this.keyColumnName);
            userProperty.setOrderColumnName(this.orderColumnName);
            userProperty.setDeprecated(this.isDeprecated);
            userProperty.setVersionDeprecated(this.versionDeprecated);
            userProperty.setFkGenerated(this.fkGenerated);
            userProperty.setFkDeleteCascade(this.fkDeleteCascade);

            return new PropertyExtractResult(userProperty);
        } else {
            return PropertyExtractResult.systemProperty();
        }
    }

    private boolean isNeedLength() {
        if (Objects.equals(this.columnType, "CLOB")) {
            return false;
        }
        final TypeInfo fieldTypeInfo = TYPES_INFO.get(this.type.toLowerCase(Locale.ENGLISH));
        if (fieldTypeInfo != null) {
            return fieldTypeInfo.isLength();
        }
        return !isModelClassType(this.getModelClass().getModel(), this.getType());
    }

    @JsonIgnore
    public boolean isUserProperty() {
        return (this.changeable == null ||
                this.changeable == Changeable.CREATE ||
                this.changeable == Changeable.UPDATE) && !Objects.equals("statusHistory", this.getName());
    }

    @JsonIgnore
    public boolean isPdmReference() {
        if (!this.isEmbedded()) {
            // If the field is collectible, then we check the flag on the type of this property
            XmlModelClass typeClass = this.getModelClass().getModel().getClassNullable(getType());
            return typeClass != null && typeClass.isExternalReference();
        }
        XmlEmbeddedList embeddedInfo = this.getModelClass().getEmbeddedPropertyList().stream()
                .filter(it -> Objects.equals(it.getName(), this.getName()))
                .findFirst().orElseThrow(() -> new EmbeddedListNotFoundException(
                        this.getName(),
                        this.getModelClass().getName()));
        return embeddedInfo.isReference();
    }

    @JsonIgnore
    public boolean isLobProperty() {
        return this.getTypeInfo().getHbmName().endsWith("lob");
    }

    @JsonIgnore
    private String extractReferenceType(String referenceType) {
        return referenceType.substring(0, referenceType.indexOf(ModelHelper.REFERENCE));
    }

    /**
     * Is the field marked as indexable?
     */
    @JsonIgnore
    public boolean isAnyIndex() {
        return this.isIndexed() || this.isUnique();
    }

    @JsonIgnore
    public String getHistoryUpdatedFor() {
        return historyUpdatedFor;
    }

    @JsonIgnore
    public XmlModelClassProperty setHistoryUpdatedFor(String historyUpdatedFor) {
        this.historyUpdatedFor = historyUpdatedFor;
        return this;
    }

    public static class Builder {
        private XmlModelClassProperty property;

        private Builder() {
            this.property = new XmlModelClassProperty();
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setType(String type) {
            this.property.setType(type);
            return this;
        }

        public Builder setName(String name) {
            this.property.setName(name);
            return this;
        }

        public Builder setLabel(String label) {
            this.property.setLabel(label);
            return this;
        }

        public Builder setDescription(String description) {
            this.property.setDescription(description);
            return this;
        }

        public Builder setChangeable(Changeable changeable) {
            this.property.setChangeable(changeable);
            return this;
        }

        public Builder setHistoryUpdatedFor(String historyUpdatedFor) {
            this.property.setHistoryUpdatedFor(historyUpdatedFor);
            return this;
        }

        public Builder setIndex(Boolean isIndex) {
            this.property.setIndex(isIndex);
            return this;
        }

        public Builder setLength(Integer length) {
            this.property.setLength(length);
            return this;
        }

        public Builder setScale(Integer scale) {
            this.property.setScale(scale);
            return this;
        }

        public Builder setCollectionType(CollectionType collectionType) {
            this.property.setCollectionType(collectionType);
            return this;
        }

        public Builder setMappedBy(String mappedBy) {
            this.property.setMappedBy(mappedBy);
            return this;
        }

        public Builder setDeprecated(Boolean deprecated) {
            this.property.setDeprecated(deprecated);
            return this;
        }

        public Builder setVersionDeprecated(String versionDeprecated) {
            this.property.setVersionDeprecated(versionDeprecated);
            return this;
        }

        public Builder setParent() {
            this.property.setParent(Boolean.TRUE);
            return this;
        }

        public Builder setExternalLink(Boolean externalLink) {
            this.property.setExternalLink(externalLink);
            return this;
        }

        public Builder setUnique(Boolean unique) {
            this.property.setUnique(unique);
            return this;
        }

        public Builder setCciIndex(boolean cciIndex) {
            this.property.setCciIndex(cciIndex);
            return this;
        }

        public Builder setCciName(String cciName) {
            this.property.setCciName(cciName);
            return this;
        }

        public Builder setHistorical(boolean historical) {
            this.property.setHistorical(historical);
            return this;
        }

        public Builder setIntegrityCheck(Boolean integrityCheck) {
            this.property.setIntegrityCheck(integrityCheck);
            return this;
        }

        public Builder setExternalSoftReference(boolean externalSoftReference) {
            this.property.setExternalSoftReference(externalSoftReference);
            return this;
        }

        public Builder setOriginalType(String originalType) {
            this.property.setOriginalType(originalType);
            return this;
        }

        public Builder setStatusReason(boolean statusReason) {
            this.property.setStatusReason(statusReason);
            return this;
        }

        public Builder setReferenceGenerateStrategy(ReferenceGenerateStrategy referenceGenerateStrategy) {
            this.property.setReferenceGenerateStrategy(referenceGenerateStrategy);
            return this;
        }

        public Builder setMandatory(boolean mandatory) {
            this.property.setMandatory(mandatory);
            return this;
        }

        public Builder setId(boolean id) {
            this.property.setId(id);
            return this;
        }

        public Builder setDefaultValue(String defaultValue) {
            this.property.setDefaultValue(defaultValue);
            return this;
        }

        public Builder setColumnName(String columnName) {
            this.property.setColumnName(columnName);
            return this;
        }

        public Builder setFkGenerated(boolean fkGenerated) {
            this.property.setFkGenerated(fkGenerated);
            return this;
        }

        public Builder setFkName(String fkName) {
            this.property.setFkName(fkName);
            return this;
        }

        public Builder setFkDeleteCascade(boolean fkDeleteCascade) {
            this.property.setFkDeleteCascade(fkDeleteCascade);
            return this;
        }

        public XmlModelClassProperty build() {
            return this.property;
        }
    }
}
