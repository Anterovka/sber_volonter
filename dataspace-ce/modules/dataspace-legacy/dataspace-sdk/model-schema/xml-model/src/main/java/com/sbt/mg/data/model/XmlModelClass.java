package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.data.model.interfaces.XmlObject;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.checkmodel.NoFoundEmbeddablePropertyException;
import com.sbt.mg.exception.checkmodel.NoFoundEmbeddedPropertyException;
import com.sbt.mg.exception.checkmodel.PropertyNotFoundInClassException;
import com.sbt.mg.exception.checkmodel.ReferenceNotFoundInClassException;
import com.sbt.mg.exception.checkmodel.TableNameAlreadyDefinedException;
import com.sbt.mg.exception.checkmodel.UndefinedImplementsClassException;
import com.sbt.mg.exception.checkmodel.UndefinedPropertyException;
import com.sbt.mg.exception.common.NotUserClassException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;
import com.sbt.status.xml.XmlStatuses;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sbt.mg.data.model.usermodel.UserXmlId.ID_DESCRIPTION_TAG;
import static com.sbt.mg.data.model.usermodel.UserXmlModelClass.CLASS_TAG;
import static com.sbt.mg.utils.ClassUtils.isBaseClass;

/**
 * Model class
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(CLASS_TAG)
public class XmlModelClass
    extends UserXmlModelClass<XmlStatuses, XmlIdempotenceExclude, XmlIndex, XmlCciIndex, XmlId, XmlModelClassProperty, XmlModelClassReference>
    implements XmlObject {

    public static final String EXTERNAL_REFERENCE_TAG = "external-reference";
    public static final String DEPRECATED_TAG = "isDeprecated";
    public static final String REMOVED_TAG = "isRemoved";
    public static final String PK_INDEX_NAME_TAG = "pk-index-name";
    public static final String SOURCE_ID_TAG = "sourceId";
    public static final String AFFINITY_TAG = "affinity";
    public static final String CLASS_ACCESS_TAG = "class-access";
    public static final String BASE_CLASS_MARK_TAG = "base-class-mark"; // automatically calculated property (not recorded in pdm)
    public static final String JPA_TAG = "generate-jpa";
    public static final String EMBEDDED_LISTS_TAG = "embedded-lists";
    public static final String EMBEDDED_LIST_TAG = "embedded-list";
    public static final String TABLE_NAME_TAG = "tableName";
    public static final String HISTORY_CLASS_TAG = "history-class";
    public static final String HISTORY_FOR_CLASS_TAG = "history-for";
    public static final String ROOT_TYPE_TAG = "root-type";
    public static final String IMPORT_MODEL_NAME_TAG = "import-model-name";
    public static final String UPDATED_ROOT_DICTIONARIES_TAG = "is-updated-root-dictionaries";
    public static final String VERSION_DEPRECATED_TAG = "ver-deprecated";
    public static final String VERSION_REMOVED_TAG = "ver-removed";
    public static final String ORIGINAL_TYPE_TAG = "original-type";
    public static final String NO_BASE_ENTITY_TAG = "no-base-entity"; // for utility classes that do not need to inherit from BaseEntity
    public static final String NO_IDEMPOTENCE_TAG = "no-idempotence"; // for service classes that do not need to add tables and fields of idempotent call
    public static final String IS_EVENT_TAG = "is-event";
    public static final String PARTITION_KEY_TAG = "partition-key";

    private XmlModel model;

    // Service interfaces
    @JsonIgnore
    private List<String> systemInterfaces = new ArrayList<>();

    // Collection representation of the parameter implementedInterfacesAsString. Translated on the first request
    private Set<XmlModelInterface> implementedInterfaces;

    /**
     * Attributes brought from parent abstract classes
     */
    @JsonIgnore
    private final List<XmlModelClassProperty> incomeProperties = new ArrayList<>();

    /**
     * Physical names of embeddable properties of embedded class fields
     */
    private final List<XmlEmbeddedList> embeddedPropertyList = new ArrayList<>();

    /**
     * Name of the physical table that is mapped to the class
     */
    private String tableName;
    /**
     * Is set if the class in the new model was deleted. The physical class is not removed
     */
    private Boolean isDeprecated;
    /**
     * If the class in the new model was deleted, this is set. The class is physically removed.
     */
    private Boolean isRemoved;

    /**
     * Here is indicated the original type in model.xml relevant to SoftReference
     */
    private String originalType;
    /**
     * Index name by primary key
     */
    private String pkIndexName;

    private String sourceId;
    private String affinity;
    private Changeable classAccess;

    /**
     * Indication that the class is the first non-abstract in the inheritance chain
     */
    private Boolean baseClassMark;

    private Boolean isExternalReference;
    private Boolean isJPA = Boolean.TRUE;
    /**
     * If not null, then the class is history.
     * And the property holds the name of its historically rooted class
     */
    private String historyForClass;
    /**
     If not null, the property holds the name of its class history (not serialized)
     */
    // Previously (before 1.13), the property contained a flag (Boolean) indicating that the class is a history class
    //Therefore,this feature is deducted from pdm for backward compatibility.Then it is converted to the    attribute historyForClass
    private String historyClass;
    /**
     * The name of the type is the root aggregate. It is necessary for the SDK generator.
     */
    private String rootType;
    private String importModelName;
    private Boolean isUpdatedRootDictionaries;

    private String versionDeprecated;

    private String versionRemoved;

    @JsonIgnore
    private boolean useAffinity = true;

    private boolean isEvent;

    @Nonnull
    @JsonIgnore
    private final Set<String> columnNames = new HashSet<>();

    private boolean noBaseEntity;
    private boolean noIdempotence;

    @JsonIgnore
    /** Service class. Used to exclude from various controls the rules applied to custom classes */
    private boolean serviceClass;

    private String partitionKey;

    public XmlModelClass() {
    }

    @JsonCreator
    public XmlModelClass(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                         @JacksonXmlProperty(isAttribute = true, localName = EXTENDS_TAG) String extendedClass,
                         @JacksonXmlProperty(isAttribute = true, localName = IMPLEMENTS_TAG) String implementedInterfacesAsString,
                         @JacksonXmlProperty(isAttribute = true, localName = EMBEDDED_TAG) Boolean embeddable,
                         @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                         @Nonnull @JacksonXmlProperty(isAttribute = true, localName = TABLE_NAME_TAG) String tableName,
                         @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG) Boolean isDeprecated,
                         @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG) Boolean isRemoved,
                         @Nonnull @JacksonXmlProperty(isAttribute = true, localName = PK_INDEX_NAME_TAG) String pkIndexName,
                         @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                         @JacksonXmlProperty(isAttribute = true, localName = SOURCE_ID_TAG) String sourceId,
                         @JacksonXmlProperty(isAttribute = true, localName = ABSTRACT_TAG) Boolean isAbstract,
                         @JacksonXmlProperty(isAttribute = true, localName = DICTIONARY_TAG) Boolean isDictionary,
                         @JacksonXmlProperty(isAttribute = true, localName = AFFINITY_TAG) String affinity,
                         @JacksonXmlProperty(isAttribute = true, localName = CLASS_ACCESS_TAG) Changeable classAccess,
                         @JacksonXmlProperty(isAttribute = true, localName = FINAL_CLASS_TAG) Boolean finalClass,
                         @JacksonXmlProperty(isAttribute = true, localName = BASE_CLASS_MARK_TAG) Boolean baseClassMark,
                         @JacksonXmlProperty(isAttribute = true, localName = STRATEGY_TAG) ClassStrategy strategy,
                         @JacksonXmlProperty(isAttribute = true, localName = LOCKABLE_TAG) Boolean lockable,
                         @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_REFERENCE_TAG) Boolean isExternalReference,
                         @JacksonXmlProperty(isAttribute = true, localName = JPA_TAG) Boolean isJPA,
                         @JacksonXmlProperty(isAttribute = true, localName = HISTORY_CLASS_TAG) String historyClass,
                         @JacksonXmlProperty(isAttribute = true, localName = HISTORY_FOR_CLASS_TAG) String historyForClass,
                         @JacksonXmlProperty(isAttribute = true, localName = IMPORT_MODEL_NAME_TAG) String importModelName,
                         @JacksonXmlProperty(isAttribute = true, localName = UPDATED_ROOT_DICTIONARIES_TAG) Boolean isUpdatedRootDictionaries,
                         @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG) String versionDeprecated,
                         @JacksonXmlProperty(isAttribute = true, localName = VERSION_REMOVED_TAG) String versionRemoved,
                         @JacksonXmlProperty(isAttribute = true, localName = ROOT_TYPE_TAG) String rootType,
                         @JacksonXmlProperty(isAttribute = true, localName = ID_DESCRIPTION_TAG) XmlId id,
                         @JacksonXmlProperty(isAttribute = true, localName = USE_ID_PREFIX_TAG) Boolean isUseIdPrefix,
                         @JacksonXmlProperty(isAttribute = true, localName = IDEMPOTENCE_EXCLUDE_TAG) XmlIdempotenceExclude idempotenceExclude,
                         @JacksonXmlProperty(isAttribute = true, localName = ORIGINAL_TYPE_TAG) String originalType,
                         @JacksonXmlProperty(isAttribute = true, localName = IS_EVENT_TAG) boolean isEvent,
                         @JacksonXmlProperty(isAttribute = true, localName = PARTITION_KEY_REGEX_TAG) String partitionKeyRegex,
                         @JacksonXmlProperty(isAttribute = true, localName = PARTITION_KEY_TAG) String partitionKey,
                         @JacksonXmlProperty(isAttribute = true, localName = CLONEABLE_TAG) Boolean cloneable,
                         @JacksonXmlProperty(localName = STATUSES_TAG) XmlStatuses statuses

    ) {
        super(
            name,
            extendedClass,
            implementedInterfacesAsString,
            embeddable,
            label,
            description,
            isAbstract,
            isDictionary,
            finalClass,
            strategy,
            lockable,
            id,
            idempotenceExclude,
            isUseIdPrefix,
            partitionKeyRegex,
            cloneable,
            statuses
        );
        this.tableName = tableName;
        this.isDeprecated = isDeprecated;
        this.isRemoved = isRemoved;
        this.pkIndexName = pkIndexName;
        this.sourceId = sourceId;
        this.affinity = affinity;
        this.classAccess = classAccess;
        this.baseClassMark = baseClassMark;
        this.isJPA = Optional.ofNullable(isJPA).orElse(Boolean.TRUE);
        this.isExternalReference = Optional.ofNullable(isExternalReference).orElse(Boolean.FALSE);
        this.historyClass = historyClass;
        this.historyForClass = historyForClass;
        this.importModelName = importModelName;
        this.isUpdatedRootDictionaries = Optional.ofNullable(isUpdatedRootDictionaries).orElse(Boolean.FALSE);
        this.versionDeprecated = versionDeprecated;
        this.versionRemoved = versionRemoved;

        this.rootType = rootType;

        this.originalType = originalType;

        this.isEvent = isEvent;

        this.partitionKey = partitionKey;
    }

    /**
     * Creating a new XmlModelClass
     */
    public static XmlModelClass create() {
        return new XmlModelClass();
    }

    /**
     * Returns a list of interfaces declared for implementation by the class
     */
    // during the first access, a transformation from string to a collection of objects (interfaces) occurs
    @JsonIgnore
    public Set<XmlModelInterface> getImplementedInterfacesAsSet() {
        if (implementedInterfaces != null) {
            return implementedInterfaces;
        }
        if (implementedInterfacesAsString == null || implementedInterfacesAsString.isEmpty()) {
            implementedInterfaces = Collections.emptySet();
        } else {
            String[] interfaceNames = implementedInterfacesAsString.split(",");
            implementedInterfaces = new HashSet<>(interfaceNames.length);
            for (String iName : interfaceNames) {
                iName = iName.trim();
                XmlModelInterface xmlModelInterface = model.getInterfaceNullable(iName);
                if (xmlModelInterface == null) {
                    throw new UndefinedImplementsClassException(getName(), iName);
                }
                implementedInterfaces.add(xmlModelInterface);
            }
        }
        return implementedInterfaces;
    }

    @JsonIgnore
    private XmlModelClassProperty getIncomePropertyByName(String name) {
        Optional<XmlModelClassProperty> optional = this.incomeProperties.stream()
            .filter(it -> it.getName().equals(name))
            .findFirst();
        return optional.orElse(null);
    }

    @Nonnull
    @JsonIgnore
    public List<XmlModelClassProperty> getIncomePropertiesAsList() {
        return new ArrayList<>(incomeProperties);
    }

    @JsonIgnore
    // TODO determine where and how the method is used and get rid of it if possible, or describe its necessity
    public List<XmlModelClassProperty> getPropertiesWithIncome() {
        List<XmlModelClassProperty> result = new ArrayList<>(properties);
        result.addAll(incomeProperties);

    // I don't understand why this is needed; it shouldn't be in the non-base class either, and there shouldn't be an object_id property anyway.
        if (!isBaseClassMark() && getStrategy() == ClassStrategy.SINGLE_TABLE) {
            result.removeIf(it -> JpaConstants.OBJECT_ID.equals(it.getName()));
        }

        return result;
    }

    //it is necessary to receive fields that are part of the primary key, even if the class with ClassStrategy.SINGLE_TABLE
    // (unlike the getPropertiesWithIncome() method), this is necessary to add fields to the rollback for dropTable
    //I did not find another way to pass the field  back in the rollback(well, except to hardcode it directly into the method HandlerCommonMethods.getCreateTableChange)
    @JsonIgnore
    public List<XmlModelClassProperty> getPropertiesWithAllIncome() {
        List<XmlModelClassProperty> result = new ArrayList<>(properties);
        result.addAll(incomeProperties);
        return result;
    }

    /**
     * Implementation of XmlObject
     *
     * @return
     */
    @Override
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public List<XmlModelClassProperty> getPropertiesAsList() {
        return new ArrayList<>(properties);
    }

    public void clearIncomeProperties() {
        incomeProperties.clear();
    }

    public void addIncomePropertyWithoutCheck(XmlModelClassProperty property) {
        // incomeProperties - this is properties transferred from nearest abstract ancestors, but they are not directly related to this class.
        //Therefore, the property modelClass is not set(not overwritten).
        if (getIncomePropertyByName(property.getName()) == null) {
            incomeProperties.add(property);
        }
    }

    /**
     * Getting non-system class property
     */
    @JsonIgnore
    public List<XmlModelClassProperty> getNonSystemProperties() {
        return properties.stream()
            .filter(property -> property.getChangeable() != Changeable.SYSTEM)
            .collect(Collectors.toList());
    }

    /**
     * Getting property declared exclusively in this class.
     * If the property is not found, then an exception of type PropertyNotFoundInClassException is thrown
     */
    public XmlModelClassProperty getProperty(String name) {
        return getProperty(name, false);
    }

    /**
     * Getting property declared exclusively in this class.
     * If the property is not found, then null is returned
     */
    public XmlModelClassProperty getPropertyNullable(String name) {
        return getProperty(name, true);
    }

    /**
     * Getting property declared exclusively in this class
     *
     * @param suppressNotFoundException whether to suppress an exception if the property is not found
     */
    private XmlModelClassProperty getProperty(String name, boolean suppressNotFoundException) {
        XmlModelClassProperty property = getPropertyByName(name);
        if (property == null && !suppressNotFoundException) {
            throw new PropertyNotFoundInClassException(this, name);
        }
        return property;
    }

    @JsonIgnore
    private XmlModelClassProperty getPropertyByName(String name) {
        Optional<XmlModelClassProperty> optional = this.properties.stream()
            .filter(it -> it.getName().equals(name))
            .findFirst();
        return optional.orElse(null);
    }

    /**
     * Getting property by name, including properties inherited from nearest abstract ancestors
     */
    public XmlModelClassProperty getPropertyWithIncomeNullable(String name) {
        XmlModelClassProperty property = getPropertyByName(name);
        if (property != null) {
            return property;
        }
        return getIncomePropertyByName(name);
    }

    /**
     * Getting property by name, considering ancestor properties
     */
    public XmlModelClassProperty getPropertyWithHierarchyNullable(String name) {
        XmlModelClass modelClass = this;
        while (modelClass != null) {
            XmlModelClassProperty property = modelClass.getPropertyNullable(name);

            if (property != null) {
                return property;
            }

            modelClass = modelClass.getExtendedClass();
        }

        return null;
    }

    public XmlModelClassProperty getPropertyWithHierarchy(String name) {
        XmlModelClass modelClass = this;
        while (modelClass != null) {
            XmlModelClassProperty property = modelClass.getPropertyNullable(name);

            if (property != null) {
                return property;
            }

            modelClass = modelClass.getExtendedClass();
        }

        throw new PropertyNotFoundInClassException(this, name);
    }

    /**
     * Getting property by name, including properties inherited from abstract ancestors
     */
    //// This method is used, for example, in the construction of indexes. An index on the fields of several objects is possible if the strategy is SingleTable.
    // TODO review the logic that uses this method, move the singleTable check to business logic, and get rid of this method
    public XmlModelClassProperty getPropertyWithHierarchyInSingleTableNullable(String name) {
        XmlModelClassProperty property = getPropertyByName(name);

        if (property != null) {
            return property;
        }

        XmlModelClassProperty incomeProperty = getIncomePropertyByName(name);

        if (incomeProperty != null) {
            return incomeProperty;
        }

        Optional<XmlModelClassProperty> superProperty = Optional.empty();

        if (this.getStrategy() == ClassStrategy.SINGLE_TABLE) {
            List<XmlModelClass> allSuperClasses = ModelHelper.getAllSuperClasses(this);
            superProperty = allSuperClasses.stream()
                    .flatMap(modelClass -> modelClass.getPropertiesWithIncome().stream())
                    .filter(prop -> {
                        String propName;
                        if (prop.getName().contains(".")) {
                            propName = prop.getName().split("\\.")[0];
                        } else {
                            propName = prop.getName();
                        }
                        return propName.equals(name);
                    })
                    .findFirst();
        }

        return superProperty.orElse(null);
    }

    public XmlModelClassProperty getPropertyWithHierarchyInSingleTable(String name) {
        XmlModelClassProperty property = getPropertyWithHierarchyInSingleTableNullable(name);
        return Optional.ofNullable(property).orElseThrow(() -> new UndefinedPropertyException(this, name));
    }

    /**
     * Obtaining properties of a class marked as historic
     */
    @JsonIgnore
    public List<XmlModelClassProperty> getHistoricalProperties() {
        return properties.stream().filter(XmlModelClassProperty::isHistorical)
            .collect(Collectors.toList());
    }

    /**
     * Obtaining properties of a class marked as historic
     */
    @JsonIgnore
    public List<XmlModelClassProperty> getHistoricalProperties(Predicate<XmlModelClassProperty> predicate) {
        return properties.stream().filter(XmlModelClassProperty::isHistorical)
            .filter(predicate)
            .collect(Collectors.toList());
    }

    /**
     * Getting external links of a class marked as historic
     */
    @JsonIgnore
    public List<XmlModelClassReference> getHistoricalReferences() {
        return references.stream().filter(XmlModelClassReference::isHistorical)
            .collect(Collectors.toList());
    }

    /**
     * Does this class contain a property with the given name
     */
    public boolean containsProperty(String name) {
        return properties.stream().anyMatch(it -> it.getName().equals(name));
    }

    public boolean containsLobProperty() {
        return properties.stream().anyMatch(XmlModelClassProperty::isLobProperty);
    }


    /**
     * Does the property contain this class or its ancestors in the inheritance hierarchy?
     */
    public boolean containsPropertyWithHierarchy(String name) {
        if (containsProperty(name)) {
            return Boolean.TRUE;
        }

        List<XmlModelClass> allSuperClasses = ModelHelper.getAllSuperClasses(this);
        for (XmlModelClass superClass : allSuperClasses) {
            if (superClass.containsProperty(name)) {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }

    /**
     * Overrides properties on the class, binding them to it
     */
    @JsonIgnore
    public void setProperties(List<XmlModelClassProperty> properties) {
        this.properties.clear();
        addProperties(properties);
    }

    /**
     * Adding properties to a class with binding to it.
     * If a property with this name already exists in the class, then PropertyNameAlreadyDefinedException
     */
    public XmlModelClass addProperties(XmlModelClassProperty... property) {
        for (XmlModelClassProperty p : property) {
            addProperty(p);
        }
        return this;
    }

    /**
     * Adding a property to a class with binding to it.
     * If a property with this name already exists in the class, then PropertyNameAlreadyDefinedException
     */
    @Override
    public void addProperty(XmlModelClassProperty property) {
        properties.add(property);
        property.setModelClass(this);
    }

    /**
     * Deleting property from class
     */
    public XmlModelClassProperty removeProperty(String name) {
        return removeProperty(name, false);
    }

    /**
     * Deleting property from class
     *
     * @param name             property name
     * @param suppressNotFound ignore the error if the property is not found
     */
    public XmlModelClassProperty removeProperty(String name, boolean suppressNotFound) {
        XmlModelClassProperty property = getPropertyByName(name);
        if (property == null && !suppressNotFound) {
            throw new PropertyNotFoundInClassException(this, name);
        }
        properties.remove(property);
        return property;
    }

    /**
     * Redefines external references in the class by binding them to it.
     */
    @JsonIgnore
    public void setReferences(List<XmlModelClassReference> references) {
        this.references.clear();
        addReferences(references);
    }

    /**
     * Adds an external link to the class and binds it to it.
     * If a link with such a name already exists, then the ReferenceNameAlreadyDefinedException
     */
    @Override
    public void addReference(XmlModelClassReference reference) {
        references.add(reference);
        reference.setModelClass(this);
    }

    @JsonIgnore
    private XmlModelClassReference getReferenceByName(String name) {
        Optional<XmlModelClassReference> optional = this.references.stream()
            .filter(it -> it.getName().equals(name))
            .findFirst();
        return optional.orElse(null);
    }

    /**
     * Returns a list of external links to the class
     */
    @JsonIgnore
    public List<XmlModelClassReference> getReferencesAsList() {
        return new ArrayList<>(references);
    }

    /**
     * In pdm there are no links. The method is overridden for @JsonIgnore
     *
     * @return
     */
    @Override
    @JsonIgnore
    public List<XmlModelClassReference> getReferenceAsList() {
        return new ArrayList<>(references);
    }

    /**
     * Returns a link by name. If the link is not found, then ReferenceNotFoundInClassException
     */
    public XmlModelClassReference getReference(String name) {
        return getReference(name, false);
    }

    /**
     * Returns a link by name. If the link is not found, then null
     */
    public XmlModelClassReference getReferenceNullable(String name) {
        return getReference(name, true);
    }

    private XmlModelClassReference getReference(String name, boolean suppressNotFoundException) {
        XmlModelClassReference reference = getReferenceByName(name);
        if (reference == null && !suppressNotFoundException) {
            throw new ReferenceNotFoundInClassException(this, name);
        }
        return reference;
    }

    /**
     * Overrides indices associated with the class
     */
    @JsonIgnore
    public void setIndices(List<XmlIndex> indexes) {
        this.indices.clear();
        addIndices(indexes);
    }

    /**
     * Adds an index to the class without checking for duplicates
     */
    // Check for duplication is absent, as the index name is generated later,
    // yes, and duplicate indices are removed during verification.
    @Override
    public void addIndex(XmlIndex index) {
        index.setModelClass(this);
        indices.add(index);
    }

    public boolean removeIndex(XmlIndex index) {
        return indices.remove(index);
    }

    @JsonIgnore
    public List<XmlIndex> getNotDeprecatedIndices() {
        return indices.stream().filter(index -> !index.isDeprecated()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<XmlIndex> getShouldDeletedIndices() {
        return indices.stream().filter(index -> index.isDeprecatedWithClass() || !index.isDeprecated()).collect(Collectors.toList());
    }


    /**
     * Returns indices, including ancestor indexes within inheritance
     */
    @JsonIgnore
    public List<XmlIndex> getAllIndices() {
        if (this.strategy == ClassStrategy.SINGLE_TABLE) {
            List<XmlIndex> result = new ArrayList<>(this.getExtendedClass().getAllIndices());
            result.addAll(this.indices);
            return result;
        }
        return this.indices;
    }

    /**
     * Get model
     */
    @JsonIgnore
    public XmlModel getModel() {
        return model;
    }

    /**
     * Set model
     */
    public XmlModelClass setModel(XmlModel model) {
        this.model = model;
        return this;
    }

    /**
     * Returns an expandable class from the model, otherwise null
     */
    @JsonIgnore
    public XmlModelClass getExtendedClass() {
        return extendedClass == null ? null : model.getClassNullable(extendedClass);
    }

    /**
     * Get interfaces
     */
    @JacksonXmlProperty(isAttribute = true, localName = IMPLEMENTS_TAG)
    public String getImplementedInterfacesAsString() {
        return implementedInterfacesAsString;
    }

    public XmlModelClass setImplementedInterfacesAsString(String implementedInterfacesAsString) {
        this.implementedInterfacesAsString = implementedInterfacesAsString;
        return this;
    }

    /**
     * Get table name
     */
    @JacksonXmlProperty(isAttribute = true, localName = TABLE_NAME_TAG)
    public String getTableName() {
        return tableName;
    }

    /**
     * Set table name
     */
    public XmlModelClass setTableName(String tableName) {
        // Control of absence of duplication of table names on the model
        if (strategy != ClassStrategy.SINGLE_TABLE || isBaseClassMark()) {
            if (this.getTableName() != null) {
                model.removeTableName(this.getTableName());
            }
            if (tableName != null) {
                if (model.containsTableName(tableName)) {
                    throw new TableNameAlreadyDefinedException(tableName);
                }
                model.addTableName(tableName);
            }
        }

        this.tableName = tableName;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = PARTITION_KEY_TAG)
    public String getPartitionKey() {
        return partitionKey;
    }

    public void setPartitionKey(String partitionKey) {
        this.partitionKey = partitionKey;
    }

    /**
     * Get column names
     */
    @Nonnull
    public Set<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Get column names
     */
    @JsonIgnore
    @Nonnull
    public Set<String> getColumnNamesReal() {
        return strategy != ClassStrategy.SINGLE_TABLE ? columnNames : ModelHelper.getBaseClass(this).columnNames;
    }

    public XmlModelClass clearColumnNamesReal() {
        XmlModelClass targetClass = strategy != ClassStrategy.SINGLE_TABLE ? this : ModelHelper.getBaseClass(this);
        targetClass.clearColumnNames();
        return this;
    }

    public XmlModelClass clearColumnNames() {
        columnNames.clear();
        return this;
    }

    /**
     * Get the name of the index for the primary key
     */
    @JacksonXmlProperty(isAttribute = true, localName = PK_INDEX_NAME_TAG)
    public String getPkIndexName() {
        return pkIndexName;
    }

    /**
     * Set the name of the index for the primary key
     */
    public XmlModelClass setPkIndexName(String pkIndexName) {
        this.pkIndexName = pkIndexName;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = AFFINITY_TAG)
    public String getAffinity() {
        return affinity;
    }

    public XmlModelClass setAffinity(String affinity) {
        this.affinity = affinity;
        return this;
    }

    public XmlModelClass setDeprecated(Boolean isDeprecated) {
        this.isDeprecated = isDeprecated;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DEPRECATED_TAG)
    public Boolean isDeprecated() {
        return isDeprecated == null ? Boolean.FALSE : isDeprecated;
    }

    @JacksonXmlProperty(isAttribute = true, localName = REMOVED_TAG)
    public boolean isRemoved() {
        return Boolean.TRUE.equals(isRemoved);
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    @JacksonXmlProperty(isAttribute = true, localName = SOURCE_ID_TAG)
    public String getSourceId() {
        return sourceId == null ? "" : sourceId;
    }

    public XmlModelClass setSourceId(String sourceId) {
        this.sourceId = sourceId;
        return this;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = ABSTRACT_TAG)
    public Boolean isAbstract() {
        return isAbstract == null ? Boolean.FALSE : isAbstract;
    }

    @JsonIgnore
    public XmlModelClass setAbstract() {
        isAbstract = Boolean.TRUE;
        return this;
    }

    @JsonIgnore
    public Boolean getIsDictionary() {
        return isDictionary;
    }

    @JacksonXmlProperty(isAttribute = true, localName = CLASS_ACCESS_TAG)
    public Changeable getClassAccess() {
        return classAccess;
    }

    public XmlModelClass setClassAccess(Changeable classAccess) {
        this.classAccess = classAccess;
        return this;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = FINAL_CLASS_TAG)
    public Boolean isFinalClass() {
        return finalClass == null ? Boolean.FALSE : finalClass;
    }

    @JsonIgnore
    public Boolean isBaseClassMark() {
        return Boolean.TRUE.equals(baseClassMark);
    }

    public XmlModelClass setBaseClassMark(Boolean baseClassMark) {
        this.baseClassMark = baseClassMark;
        return this;
    }

    @JsonIgnore
    public boolean isUseAffinity() {
        return useAffinity;
    }

    public XmlModelClass setUseAffinity(boolean useAffinity) {
        this.useAffinity = useAffinity;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EXTERNAL_REFERENCE_TAG)
    public boolean isExternalReference() {
        return isExternalReference == null ? Boolean.FALSE : isExternalReference;
    }

    public XmlModelClass setExternalReference(Boolean isExternalReference) {
        this.isExternalReference = isExternalReference;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ORIGINAL_TYPE_TAG)
    public String getOriginalType() {
        return originalType;
    }

    public XmlModelClass setOriginalType(String originalType) {
        this.originalType = originalType;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = JPA_TAG)
    public Boolean isJPA() {
        return isJPA;
    }

    public XmlModelClass setJPA(Boolean isJPA) {
        this.isJPA = isJPA;
        return this;
    }

    @JsonIgnore
    public String getHistoryClass() {
        return historyClass;
    }

    @JacksonXmlProperty(isAttribute = true, localName = HISTORY_CLASS_TAG)
    public XmlModelClass setHistoryClass(String historyClass) {
        this.historyClass = historyClass;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = HISTORY_FOR_CLASS_TAG)
    public String getHistoryForClassName() {
        return historyForClass;
    }

    @JsonIgnore
    public XmlModelClass getHistoryForClass() {
        return historyForClass != null
                ? model.getClassNullable(historyForClass)
                : null;
    }

    public XmlModelClass setHistoryForClass(String historicalClass) {
        this.historyForClass = historicalClass;
        return this;
    }

    @JsonIgnore
    public Boolean isHistoryClass() {
        return historyForClass != null;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ROOT_TYPE_TAG)
    public String getRootType() {
        return rootType;
    }

    public void setRootType(String rootType) {
        this.rootType = rootType;
    }

    @JacksonXmlProperty(isAttribute = true, localName = IMPORT_MODEL_NAME_TAG)
    public String getImportModelName() {
        return importModelName;
    }

    public XmlModelClass setImportModelName(String importModelName) {
        this.importModelName = importModelName;
        return this;
    }

    @JacksonXmlProperty(isAttribute = true, localName = UPDATED_ROOT_DICTIONARIES_TAG)
    public Boolean isUpdatedRootDictionaries() {
        return Boolean.TRUE == isUpdatedRootDictionaries;
    }

    public void setUpdatedRootDictionaries(Boolean isUpdatedRootDictionaries) {
        this.isUpdatedRootDictionaries = isUpdatedRootDictionaries;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NO_BASE_ENTITY_TAG)
    public boolean isNoBaseEntity() {
        return noBaseEntity;
    }

    public void setNoBaseEntity(boolean noBaseEntity) {
        this.noBaseEntity = noBaseEntity;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NO_IDEMPOTENCE_TAG)
    public boolean isNoIdempotence() {
        return noIdempotence;
    }

    public void setNoIdempotence(boolean noIdempotence) {
        this.noIdempotence = noIdempotence;
    }

    /**
     * Overrides the collection of built-in properties on the class
     */
    @JsonIgnore
    public XmlModelClass setEmbeddedPropertyList(List<XmlEmbeddedList> embeddedPropertyList) {
        this.embeddedPropertyList.clear();
        return addEmbeddedPropertyList(embeddedPropertyList);
    }

    /**
     * Adds to the class a collection of built-in properties
     */
    @JacksonXmlElementWrapper(localName = EMBEDDED_LISTS_TAG)
    @JsonSetter(value = EMBEDDED_LIST_TAG)
    public XmlModelClass addEmbeddedPropertyList(Collection<XmlEmbeddedList> embeddedPropertyList) {
        embeddedPropertyList.forEach(this::addEmbeddedProperty);
        return this;
    }

    /**
     * Adds a built-in property to the class
     */
    public XmlModelClass addEmbeddedProperty(XmlEmbeddedList embeddedProperty) {
        //TODO find out if there's any way to implement a check for duplicates
        embeddedProperty.setModelClass(this);
        embeddedPropertyList.add(embeddedProperty);
        return this;
    }

    /**
     * Returns meta information about embeddable fields placed on the class
     */
    @JacksonXmlElementWrapper(localName = EMBEDDED_LISTS_TAG)
    @JacksonXmlProperty(isAttribute = true, localName = EMBEDDED_LIST_TAG)
    public List<XmlEmbeddedList> getEmbeddedPropertyList() {
        return Collections.unmodifiableList(embeddedPropertyList);
    }

    @JsonIgnore
/** Obtaining information on the given embedded field of the class */
    public Optional<XmlEmbeddedList> getEmbeddedPropertyMeta(String propertyName) {
        return embeddedPropertyList.stream().filter(it -> it.getName().equals(propertyName)).findAny();
    }

    /**
     * Adding or replacing information based on the given embedded field
     */
    public void addOrReplaceEmbeddedPropertyMeta(XmlEmbeddedList xmlEmbeddedList) {
        Iterator<XmlEmbeddedList> iterator = embeddedPropertyList.iterator();
        while (iterator.hasNext()) {
            XmlEmbeddedList next = iterator.next();
            if (next.getName().equals(xmlEmbeddedList.getName())) {
                iterator.remove();
            }
        }

        addEmbeddedProperty(xmlEmbeddedList);
    }

    public static XmlEmbeddedProperty getEmbeddedProperty(XmlModelClassProperty classProperty, String embeddedProperty) {
        XmlEmbeddedList embeddedList = getEmbeddedList(classProperty);

        return embeddedList.getEmbeddedPropertyList().stream()
                .filter(xmlEmbeddedProperty -> embeddedProperty.equals(xmlEmbeddedProperty.getName()))
                .findFirst().orElseThrow(() -> new NoFoundEmbeddablePropertyException(embeddedProperty, classProperty));
    }

    public static XmlEmbeddedList getEmbeddedList(XmlModelClassProperty classProperty) {
        return classProperty.getModelClass().getEmbeddedPropertyList().stream()
                .filter(xmlEmbeddedList -> xmlEmbeddedList.getName().equals(classProperty.getName())).findAny()
                .orElseThrow(() -> new NoFoundEmbeddedPropertyException(classProperty.getName(), classProperty.getModelClass()));
    }

    public XmlModelClass removeEmbeddedList(XmlModelClassProperty classProperty) {
        XmlEmbeddedList embeddedList = getEmbeddedList(classProperty);
        embeddedPropertyList.remove(embeddedList);
        return this;
    }

    public XmlModelClass removeEmbeddedList(XmlEmbeddedList xmlEmbeddedList) {
        embeddedPropertyList.remove(xmlEmbeddedList);
        return this;
    }

    /**
     * Overrides CCI indices on the class
     */
    @JsonIgnore
    public void setCciIndices(List<XmlCciIndex> cciIndices) {
        this.cciIndices.clear();
        addCciIndices(cciIndices);
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_DEPRECATED_TAG)
    public String getVersionDeprecated() {
        return versionDeprecated;
    }

    public XmlModelClass setVersionDeprecated(String versionDeprecated) {
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

    @JsonIgnore
    public List<String> getSystemInterfaces() {
        return systemInterfaces;
    }

    public void setSystemInterfaces(List<String> systemInterfaces) {
        this.systemInterfaces = systemInterfaces;
    }

    @JacksonXmlProperty(isAttribute = true, localName = IS_EVENT_TAG)
    public boolean isEvent() {
        return isEvent;
    }

    public XmlModelClass setEvent(boolean event) {
        isEvent = event;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XmlModelClass that = (XmlModelClass) o;
        return name.equals(that.name);
    }

    @JsonIgnore
    public boolean isServiceClass() {
        return serviceClass;
    }

    @JsonIgnore
    public boolean isMergeEvent() {

        if (!this.isEvent) {
            return false;
        }

        return getModel()
                .getEvents()
                .stream()
                .anyMatch(xmlEvent -> xmlEvent.getMergeEvent() && xmlEvent.getName().equals(this.name));

    }

    public XmlModelClass setServiceClass(boolean serviceClass) {
        this.serviceClass = serviceClass;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Class{"
                + "name='" + name + '\''
                + '}';
    }

    /**
     * Method for extracting from a class in pdm, which is initialized with values, system properties, etc. of the class,
     * filled only with what the user should fill in
     *
     * @return generated custom class.
     */
    @JsonIgnore
    public XmlModelClass extractFromPdmUserClass() {
        if (this.getClassAccess() != Changeable.UPDATE) {
            throw new NotUserClassException(this);
        }
        XmlModelClass userClass = new XmlModelClass();
        userClass.setName(this.name);
        userClass.setLabel(this.label);
        userClass.setLockable(this.lockable);
        userClass.setAbstract(this.isAbstract);
        userClass.setEmbeddable(this.embeddable);
        userClass.setDictionary(this.isDictionary);
        userClass.setDescription(this.description);
        userClass.setFinalClass(this.finalClass);
        userClass.setExternalReference(this.isExternalReference);
        userClass.setExtendedClassName(isBaseClass(this.extendedClass) ? null : this.extendedClass);
        userClass.setStrategy((ModelHelper.isModelClassBaseMark(this) && this.strategy != ClassStrategy.JOINED) ?
                        this.strategy :
                        null);
        userClass.setImplementedInterfacesAsString(this.implementedInterfacesAsString);

        userClass.setUseIdPrefix(this.isUseIdPrefix);
        userClass.setPartitionKeyRegex(this.partitionKeyRegex);
        userClass.setCloneable(this.cloneable);

        if (userClass.getExtendedClassName() == null) {
            userClass.setId(this.id == null ?
                    null :
                    new XmlId(Objects.equals("string", this.id.getType().toLowerCase()) ?
                            null :
                            this.id.getType(),
                            this.id.getIdCategory()
                    )
            );
        }

        this.getPropertiesAsList().stream()
                .filter(property -> !Objects.equals("Status", property.getType()))
                .filter(property -> !property.isStatusReason())
                .forEach(property -> {
                    PropertyExtractResult propertyExtractResult = property.extractFromPdmProperty();
                    if (propertyExtractResult.isProperty()) {
                        XmlModelClassProperty userProperty = propertyExtractResult.getProperty();
                        userClass.addProperty(userProperty);
                    } else if (propertyExtractResult.isReference()) {
                        XmlModelClassReference userReference = propertyExtractResult.getReference();
                        userClass.addReference(userReference);
                    }
                });

        this.getIndices().forEach(pdmIndex -> {
                    boolean isSystemIndex = pdmIndex.getProperties().stream()
                            .allMatch(pdmIndexProperty -> {
                                String propertyName = pdmIndexProperty.getName().contains(".") ?
                                        pdmIndexProperty.getName().split("\\.")[0] :
                                        pdmIndexProperty.getName();

                            // For SINGLE_TABLE, this is possible.SINGLE_TABLE will trigger another check during verification.
                            XmlModelClassProperty classProperty = this.getPropertyWithHierarchyNullable(propertyName);

                                return !classProperty.isUserProperty() ||
                                        classProperty.isPdmReference() ||
                                        Objects.equals("Status", classProperty.getType());
                            });

                    if (!isSystemIndex || isUniqueOnReference(pdmIndex)) {
                        copyIndex(userClass, pdmIndex);
                    }
                }
        );

        this.getCciIndices().forEach(pdmCciIndex -> {
            XmlCciIndex userCciIndex = new XmlCciIndex();
            List<Property> userProperties = pdmCciIndex.getProperties().stream()
                    .map(property -> {
                        if (JpaConstants.OBJECT_ID.equals(property.getName())) {
                            return new Property("id");
                        }
                        return new Property(property.getName());
                    }).collect(Collectors.toList());
            userCciIndex.getProperties().addAll(userProperties);
            userClass.getCciIndices().add(userCciIndex);
        });

        return userClass;
    }

    private void copyIndex(XmlModelClass userClass, XmlIndex pdmIndex) {
        XmlIndex userIndex = new XmlIndex();
        userIndex.setUnique(pdmIndex.isUnique());
        pdmIndex.getProperties().forEach(pdmProperty -> {
            Property property = new Property(pdmProperty.getName());
            userIndex.addProperty(property);
        });

        userClass.addIndex(userIndex);
    }

    private boolean isUniqueOnReference(XmlIndex pdmIndex) {
        if (pdmIndex.isUnique() && pdmIndex.getProperties().size() == 1) {
            String name = pdmIndex.getProperties().get(0).getName();
            String mainPropName = name.split("\\.").length > 1 ? name.split("\\.")[0] : name;
            return this.getPropertyWithHierarchyNullable(mainPropName).isPdmReference();
        }
        return false;
    }

    public boolean haveStatuses() {
        return this.statuses != null && this.statuses.getGroups() != null;
    }

    public static class Builder {

        private XmlModelClass xmlModelClass;

        private Builder() {
            this.xmlModelClass = XmlModelClass.create();
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder setName(String name) {
            this.xmlModelClass.setName(name);
            return this;
        }

        public Builder setFinalClass(Boolean finalClass) {
            this.xmlModelClass.setFinalClass(finalClass);
            return this;
        }

        public Builder setLabel(String label) {
            this.xmlModelClass.setLabel(label);
            return this;
        }

        public Builder setDescription(String description) {
            this.xmlModelClass.setDescription(description);
            return this;
        }

        public Builder setClassAccess(Changeable changeable) {
            this.xmlModelClass.setClassAccess(changeable);
            return this;
        }

        public Builder setUseAffinity(boolean useAffinity) {
            this.xmlModelClass.setUseAffinity(useAffinity);
            return this;
        }

        public Builder setDeprecated(Boolean deprecated) {
            this.xmlModelClass.setDeprecated(deprecated);
            return this;
        }

        public Builder setVersionDeprecated(String versionDeprecated) {
            this.xmlModelClass.setVersionDeprecated(versionDeprecated);
            return this;
        }

        public Builder setEvent(boolean event) {
            this.xmlModelClass.setEvent(event);
            return this;
        }

        public Builder addProperties(XmlModelClassProperty... properties) {
            this.xmlModelClass.addProperties(properties);
            return this;
        }

        public Builder addProperties(Collection<XmlModelClassProperty> properties) {
            this.xmlModelClass.addProperties(properties);
            return this;
        }

        public Builder setIndices(List<XmlIndex> indices) {
            this.xmlModelClass.setIndices(indices);
            return this;
        }

        public Builder addIndices(List<XmlIndex> indices) {
            this.xmlModelClass.addIndices(indices);
            return this;
        }

        public Builder setBaseClassMark(Boolean baseClassMark) {
            this.xmlModelClass.setBaseClassMark(baseClassMark);
            return this;
        }

        public Builder setExtendedClassName(String extendedClassName) {
            this.xmlModelClass.setExtendedClassName(extendedClassName);
            return this;
        }

        public Builder setEmbeddable(Boolean embeddable) {
            this.xmlModelClass.setEmbeddable(embeddable);
            return this;
        }

        public Builder setId(XmlId id) {
            this.xmlModelClass.setId(id);
            return this;
        }

        public Builder setNoJpa() {
            this.xmlModelClass.setJPA(false);
            return this;
        }

        public Builder setNoIdempotence() {
            this.xmlModelClass.setNoIdempotence(false);
            return this;
        }

        public XmlModelClass build() {
            return this.xmlModelClass;
        }
    }
}
