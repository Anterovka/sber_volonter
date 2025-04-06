package com.sbt.mg.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.sbt.mg.data.model.event.XmlEvent;
import com.sbt.mg.data.model.interfaces.XmlObject;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.mg.data.model.layout.Layout;
import com.sbt.mg.data.model.typedef.XmlTypeDefs;
import com.sbt.mg.data.model.unusedschemaItems.XmlUnusedSchemaItems;
import com.sbt.mg.data.model.usermodel.UserXmlModel;
import com.sbt.mg.exception.checkmodel.ClassDuplicationException;
import com.sbt.mg.exception.checkmodel.ClassNotFoundException;
import com.sbt.mg.exception.checkmodel.CustomQueryDuplicationException;
import com.sbt.mg.exception.checkmodel.CustomQueryNotFoundException;
import com.sbt.mg.exception.checkmodel.ExternalTypeDuplicationException;
import com.sbt.mg.exception.checkmodel.InterfaceDuplicationException;
import com.sbt.mg.exception.checkmodel.InterfaceNotFoundException;
import com.sbt.mg.exception.checkmodel.SameEnumClassNameException;
import com.sbt.mg.exception.checkmodel.TypeDefXmlDuplicationException;
import com.sbt.mg.exception.checkmodel.XmlDuplicateExternalTypeException;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Model
 */
@JacksonXmlRootElement(localName = UserXmlModel.MODEL_TAG)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlTagName(UserXmlModel.MODEL_TAG)
public class XmlModel extends UserXmlModel<
        XmlModelExternalType,
        XmlTypeDefs,
        XmlEvent,
        XmlModelClassEnum,
        XmlImport,
        XmlModelClass,
        XmlModelInterface,
        XmlQuery> {

    public static final String ARCH_REPLICA_CODE = "arch-replica-code";
    public static final String MIN_COMPATIBLE_VERSION_TAG = "minCompatibleVersion";

    public static final String UNUSED_SCHEMA_ITEMS = "unusedSchemaItems";

    private String archReplicaCode;

    private String minCompatibleVersion;

    @JsonIgnore
    private final Set<String> tableNames = new HashSet<>();

    private XmlUnusedSchemaItems unusedSchemaItems;

    @Nonnull
    @JsonIgnore
    private final Set<String> indexNames = new HashSet<>();

    @JsonIgnore
    private final Set<String> fkNames = new HashSet<>();

    @JsonIgnore
    private Set<String> externalReferenceClasses;

    /** The field is left for backward compatibility with old models and is no longer used.
     * The text cannot be deleted, otherwise the deserialization of old models will fail */
    @JsonIgnore
    @Deprecated
    private String modelPackage;
    /** The field is left for backward compatibility with old models and is no longer used.
    * The text cannot be deleted, otherwise the deserialization of old models will fail */
    @JsonIgnore
    @Deprecated
    private String domainPackage;
    /** The field is left for backward compatibility with old models and is no longer used.
    * The text cannot be deleted, otherwise the deserialization of old models will fail */
    @JsonIgnore
    @Deprecated
    private File modelDirectory;
    /** The field is left for backward compatibility with old models and is no longer used.
    * The text cannot be deleted, otherwise the deserialization of old models will fail */
    @JsonIgnore
    @Deprecated
    private String domainModelArtifactId;

    private Layout layout;

    public XmlModel(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = MODEL_NAME_TAG) String modelName,
                    @JacksonXmlProperty(isAttribute = true, localName = COMPONENT_CODE_TAG) String componentCode,
                    @JacksonXmlProperty(isAttribute = true, localName = VERSION_TAG) String version,
                    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                    @JacksonXmlProperty(isAttribute = true, localName = TABLE_PREFIX_TAG) String tablePrefix,
                    @JacksonXmlProperty(isAttribute = true, localName = VERSIONED_ENTITIES_TAG) Boolean versionedEntities,
                    @JacksonXmlProperty(isAttribute = true, localName = AUTO_ID_METHOD_TAG) IdCategory autoIdMethod,
                    @JacksonXmlProperty(localName = Layout.LAYOUT_TAG) Layout layout,
                    @JacksonXmlProperty(localName = UNUSED_SCHEMA_ITEMS) XmlUnusedSchemaItems unusedSchemaItems
    ) {
        super(modelName, componentCode, version, description, tablePrefix, versionedEntities, autoIdMethod);
        this.componentCode = StringUtils.isEmpty(componentCode) ? "НЕ ЗАДАНО" : componentCode;
        this.description = description == null ? "" : description;
        this.layout = layout;
        this.unusedSchemaItems = unusedSchemaItems;
    }

    @JsonIgnore
    public void setQueries(Collection<XmlQuery> queries) {
        this.queries.clear();
        addQueries(queries);
    }

    @Override
    public void addQuery(XmlQuery query) {
        if (queries.containsKey(query.getName())) {
            throw new CustomQueryDuplicationException(query.getName());
        }
        queries.put(query.getName(), query);
        query.setModel(this);
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSIONED_ENTITIES_TAG)
    @Override
    public Boolean isVersionedEntities() {
        return Boolean.TRUE.equals(versionedEntities);
    }

    public void addExternalTypes(Collection<XmlModelExternalType> externalTypes) {
        externalTypes.forEach(this::addExternalType);
    }

    public void addExternalType(XmlModelExternalType externalType) {
        if (externalTypes.stream().anyMatch(it -> Objects.equals(it.getType(), externalType.getType()))) {
            throw new ExternalTypeDuplicationException(externalType.getType());
        }
        externalTypes.add(externalType);
        externalType.setModel(this);
    }

    /** Adds a query to the model without checking if the query is in the model */
    public void addQueryWithoutCheck(XmlQuery xmlQuery) {
        queries.put(xmlQuery.getName(), xmlQuery);
        xmlQuery.setModel(this);
    }

    @JsonIgnore
    public List<XmlQuery> getQueries() {
        return getQueriesAsList();
    }

    public XmlQuery getQuery(String name) {
        if (!queries.containsKey(name)) {
            throw new CustomQueryNotFoundException(name);
        }
        return queries.get(name);
    }

/** Does the model contain the user's query with the given name? **/
    public boolean containsQuery(String name) {
        return queries.containsKey(name);
    }

    public XmlQuery removeQuery(String name) {
        return removeQuery(name, false);
    }

    public XmlQuery removeQuery(String name, boolean suppressNotFound) {
        XmlQuery xmlQuery = getQuery(name);
        if (xmlQuery == null && !suppressNotFound) {
            throw new CustomQueryNotFoundException(name);
        }
        queries.remove(name);
        return xmlQuery;
    }

    /**
     * Overrides interfaces in the model
     * If such interface already exists in the current model, then an exception InterfaceDuplicationException is thrown
     * @throws InterfaceDuplicationException
     */
    @JsonIgnore
    public void setInterfaces(Collection<XmlModelInterface> interfaces) {
        this.interfaces.clear();
        addInterfaces(interfaces);
    }

    /** Adding an interface to the model with binding to the model */
    @Override
    public void addInterface(XmlModelInterface iface) {
        if (interfaces.containsKey(iface.getName())) {
            throw new InterfaceDuplicationException(iface.getName());
        }
        interfaces.put(iface.getName(), iface);
        iface.setModel(this);
    }

    /** Returns an interface by name. If the interface is not found, then InterfaceNotFoundException */
    public XmlModelInterface getInterface(String name) {
        if (!interfaces.containsKey(name)) {
            throw new InterfaceNotFoundException(name);
        }
        return interfaces.get(name);
    }

    /** Returns an interface by name. If the interface is not found, returns null */
    public XmlModelInterface getInterfaceNullable(String name) {
        return interfaces.get(name);
    }

    /** Does the model contain an interface with the given name? */
    public boolean containsInterface(String name) {
        return interfaces.containsKey(name);
    }

    /** Overrides model classes with the given list.
     * Classes are bound to the current model
     * @throws ClassDuplicationException */
    @JsonIgnore
    public void setClasses(List<XmlModelClass> classes) {
        this.classes.clear();
        addClasses(classes);
    }


    /** Adds a class to the model, binding it to it.
     * If the class has already been bound to the model, then a ClassDuplicationException will be thrown.
     * @throws ClassDuplicationException */
    @Override
    public void addClass(XmlModelClass modelClass) {
        if (classes.containsKey(modelClass.getName().toLowerCase(Locale.ROOT))) {
            throw new ClassDuplicationException(modelClass.getName());
        }
        addClassWithoutCheck(modelClass);
    }

    /** Adds a class to the model without checking if the class is already in the model */
    public void addClassWithoutCheck(XmlModelClass modelClass) {
        classes.put(modelClass.getName().toLowerCase(Locale.ROOT), modelClass);
        modelClass.setModel(this);
        if (modelClass.getImportModelName() == null) {
            setImportModelNameToModelClass(modelClass);
        }
    }

    /**
     * Deletion of class from model
     */
    public XmlModelClass removeClass(String name) {
        return removeClass(name, false);
    }

    /**
     * Deletion of class from model. Case-sensitive function
     *
     * @param name            class name
     * @param suppressNotFound to suppress the error if the class is not found
     */
    public XmlModelClass removeClass(String name, boolean suppressNotFound) {
        XmlModelClass xmlModelClass = getClassNullable(name);
        if (xmlModelClass == null && !suppressNotFound) {
            throw new ClassNotFoundException(name);
        }
        classes.remove(name.toLowerCase(Locale.ROOT));
        return xmlModelClass;
    }

    public void removeEvent(String name) {
        final List<XmlEvent> deletedEvents = this.events.stream()
                .filter(it -> Objects.equals(it.getName(), name))
                .collect(Collectors.toList());
        this.events.removeAll(deletedEvents);
    }

    private void setImportModelNameToModelClass(XmlModelClass modelClass){
        XmlModel model = modelClass.getModel();
        if(model != null) {
            String importModelName = modelClass.getModel().getModelName() == null ?
                    null : modelClass.getModel().getModelName().toLowerCase(Locale.ENGLISH);
            modelClass.setImportModelName(importModelName);
        }
    }

    @JsonIgnore
    public List<XmlObject> getClassesAndQueriesAsList() {
        List<XmlObject> result = new ArrayList<>(classes.size() + queries.size());
        result.addAll(classes.values());
        result.addAll(queries.values());
        return result;
    }

    /** Returns a class by name. If the class is not found, then ClassNotFoundException. */
    public XmlModelClass getClass(String name) {
        XmlModelClass modelClass = getClassNullable(name);
        if (modelClass == null) {
            throw new ClassNotFoundException(name);
        }
        return modelClass;
    }

    /** Returns a class by name. If the class is not found, then null. */
    public XmlModelClass getClassNullable(String name) {
        return name == null ? null : classes.get(name.toLowerCase(Locale.ROOT));
    }

    /**
     * Get class with given name
     */
    @JsonIgnore
    public Optional<XmlModelClass> findClass(String name) {
        return Optional.ofNullable(classes.get(name.toLowerCase(Locale.ROOT)));
    }

    /** Returns a flag indicating whether the model contains a class with the specified name */
    public boolean containsClass(String name) {
        return classes.containsKey(name.toLowerCase(Locale.ROOT));
    }

    /** Returns a flag indicating whether the model contains a class with the specified name */
    public boolean containsEvent(String name) {
        return getEvents().stream().anyMatch(it -> Objects.equals(it.getName(), name));
    }

    /** Redefinition of imports on the model */
    @JsonIgnore
    public void setImports(List<XmlImport> imports) {
        this.imports.clear();
        addImports(imports);
    }

    /** Redefines all enums on the class */
    @JsonIgnore
    public void setEnums(List<XmlModelClassEnum> enums) {
        this.enums.clear();
        addEnums(enums);
    }

    /** Adds the passed enum to the class */
    @Override
    public void addEnum(XmlModelClassEnum modelEnum) {
        if (this.enums.contains(modelEnum)) {
            throw new SameEnumClassNameException(modelEnum.getName());
        }
        this.enums.add(modelEnum);
    }

    @JsonIgnore
    public List<XmlModelClassEnum> getUserEnums() {
        return Collections.unmodifiableList(enums.stream().filter(it -> it.getChangeable() != Changeable.SYSTEM).collect(Collectors.toList()));
    }

    /** Checking the existence of an enum by name */
    public boolean containsEnum(String name) {
    // assuming that the collection won't be too large to translate it into a Map
        return enums.stream().anyMatch(it -> it.getName().equals(name));
    }

    @JacksonXmlProperty(localName = Layout.LAYOUT_TAG)
    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ARCH_REPLICA_CODE)
    public String getArchReplicaCode() {
        return archReplicaCode;
    }

    public void setArchReplicaCode(String archReplicaCode) {
        this.archReplicaCode = archReplicaCode;
    }

    @JacksonXmlProperty(isAttribute = true, localName = MIN_COMPATIBLE_VERSION_TAG)
    public String getMinCompatibleVersion() {
        return minCompatibleVersion;
    }

    public void setMinCompatibleVersion(String minCompatibleVersion) {
        this.minCompatibleVersion = minCompatibleVersion;
    }

    /**
    * Get table names
     */
    @Nonnull
    public Set<String> getTableNames() {
        return Collections.unmodifiableSet(tableNames);
    }

    public XmlModel addTableName(String tableName) {
        tableNames.add(tableName);
        return this;
    }

    public XmlModel removeTableName(String tableName) {
        tableNames.remove(tableName);
        return this;
    }

    public XmlModel clearTableNames() {
        tableNames.clear();
        return this;
    }

    /**
     * Get index names
     */
    @Nonnull
    public Set<String> getIndexNames() {
        return indexNames;
    }

    public Set<String> getFkNames() {
        return fkNames;
    }

    /**
     * Remove section from PDM. Don't know why.
     * @return
     */
    @Override
    @JsonIgnore
    public XmlTypeDefs getXmlAlias() {
        return xmlAlias;
    }

    @Override
    @JacksonXmlProperty(isAttribute = true, localName = ALIASES_TAG)
    public void setXmlAlias(XmlTypeDefs xmlAlias){
        if (this.xmlAlias == null) {
            this.xmlAlias = xmlAlias;
        } else {
            throw new TypeDefXmlDuplicationException();
        }
    }

    /** Does the model contain an alias of the given type name? */
    public boolean containsAlias(String aliasName) {
        if (xmlAlias == null) {
            return false;
        }
        return xmlAlias.getTypeDefs().stream().anyMatch(item -> item.getName().equals(aliasName));
    }

    @Override
    public void setExternalTypes(List<XmlModelExternalType> externalTypes){
        externalTypes.forEach(it -> {
                    if (this.externalTypes.stream()
                    .anyMatch(externalType -> Objects.equals(externalType.getType(), it.getType()))) {
                        throw new XmlDuplicateExternalTypeException("type", it.getType());
                    }
                    if (this.externalTypes.stream()
                            .anyMatch(externalType -> Objects.equals(externalType.getMergeKind(), it.getMergeKind()))
                    ) {
                        throw new XmlDuplicateExternalTypeException("merge-kind", it.getMergeKind());
                    }
                    this.externalTypes.add(it);
                }
        );
    }

    @JsonIgnore
    public void setWithClearExternalTypes(List<XmlModelExternalType> externalTypes){
        this.externalTypes = externalTypes;
    }

    public boolean containsExternalTypes(String externalTypeName) {
        if (externalTypeName == null) {
            return false;
        }
        return externalTypes.stream().anyMatch(it -> Objects.equals(it.getType(), externalTypeName));
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = UNUSED_SCHEMA_ITEMS)
    public XmlUnusedSchemaItems getUnusedSchemaItems() {
        return unusedSchemaItems;
    }

    public XmlModel setUnusedSchemaItems(XmlUnusedSchemaItems unusedSchemaItems) {
        this.unusedSchemaItems = unusedSchemaItems;
        return this;
    }

    /**
     * Moves all elements from the passed model:
     * classes, enums, interfaces, statuses and status classes
     * @param otherModel
     */
    @JsonIgnore
    public void importXmlModel(XmlModel otherModel) {
        //TODO understand why elements are transferred but not deleted from the previous class.
        // possibly, it is necessary to recreate elements;
        addClasses(otherModel.getClassesAsList());
        addEvents(otherModel.getEvents());
        addInterfaces(otherModel.getInterfacesAsList());
        addQueries(otherModel.getQueriesAsList());
        addExternalTypes(otherModel.getExternalTypes());
        this.enums.addAll(otherModel.enums);
        if (otherModel.xmlAlias != null) {
            if (this.xmlAlias == null) {
                xmlAlias = new XmlTypeDefs();
            }
            this.xmlAlias.getTypeDefs().addAll(otherModel.xmlAlias.getTypeDefs());
        }
    }

    /**
     * Returns a list of class names that are used in external references
     */
    @JsonIgnore
    public Set<String> getExternalReferenceClasses() {
        if (externalReferenceClasses != null) {
            return externalReferenceClasses;
        }

        externalReferenceClasses = new HashSet<>();
        getClassesAsList().forEach(xmlModelClass -> xmlModelClass.getPropertiesAsList().forEach(prop -> {
            if (prop.isExternalLink() && Objects.isNull(prop.getCollectionType())) {
                externalReferenceClasses.add(prop.getType());
            }
        }));
        return externalReferenceClasses;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getDomainPackage() {
        return domainPackage;
    }

    public void setDomainPackage(String domainPackage) {
        this.domainPackage = domainPackage;
    }

    public File getModelDirectory() {
        return modelDirectory;
    }

    public void setModelDirectory(File modelDirectory) {
        this.modelDirectory = modelDirectory;
    }

    public String getDomainModelArtifactId() {
        return domainModelArtifactId;
    }

    public void setDomainModelArtifactId(String domainModelArtifactId) {
        this.domainModelArtifactId = domainModelArtifactId;
    }

    public boolean containsTableName(String tableName) {
        return tableNames.contains(tableName)
                || unusedSchemaItems != null
                && unusedSchemaItems.containsTable(tableName);
    }

    public XmlModel addIndexName(String indexName) {
        indexNames.add(indexName);
        return this;
    }

    public XmlModel clearIndexNames() {
        indexNames.clear();
        return this;
    }

    public XmlModel addFkName(String fkName) {
        fkNames.add(fkName);
        return this;
    }

}
