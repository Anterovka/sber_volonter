package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.parameters.enums.IdCategory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.sbt.mg.data.model.usermodel.UserXmlEvent.EVENT_TAG;
import static com.sbt.mg.data.model.usermodel.UserXmlModelClass.CLASS_TAG;

@JacksonXmlRootElement(localName = UserXmlModel.MODEL_TAG)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@XmlTagName(UserXmlModel.MODEL_TAG)
public class UserXmlModel<
        Q extends UserXmlModelExternalType,
        R extends UserXmlTypeDefs,
        S extends UserXmlEvent,
        T extends UserXmlEnum,
        U extends UserXmlImport,
        V extends UserXmlModelClass,
        W extends UserXmlModelInterface,
        X extends UserXmlQuery> {

    public static final String MODEL_TAG = "model";

    public static final String MODEL_NAME_TAG = "model-name";
    public static final String VERSION_TAG = "version";
    public static final String COMPONENT_CODE_TAG = "component-code";
    public static final String TABLE_PREFIX_TAG = "table-prefix";
    public static final String DESCRIPTION_TAG = "description";
    public static final String VERSIONED_ENTITIES_TAG = "versioned-entities";
    public static final String AUTO_ID_METHOD_TAG = "autoIdMethod";

    public static final String IMPORT_TAG = "import";
    public static final String ALIASES_TAG = "type-defs";
    public static final String STATUSES_TAG = "statuses";
    public static final String STATUS_CLASSES_TAG = "status-classes";
    public static final String EXTERNAL_TYPES_TAG = "external-types";
    public static final String INTERFACE_TAG = "interface";

    private String modelName;
    protected String componentCode;
    protected Boolean versionedEntities;
    private String version;
    protected String description;
    private String tablePrefix;
    private IdCategory autoIdMethod;

    protected final Map<String, X> queries = new HashMap<>();
    @JsonIgnore
    protected Map<String, W> interfaces = new LinkedHashMap<>();
/** Correspondence of class name in lowcase to the class itself */
    @JsonIgnore
    protected final Map<String, V> classes = new LinkedHashMap<>();
    protected final Set<U> imports = new HashSet<>();
    protected final List<T> enums = new ArrayList<>();
    protected final List<S> events = new ArrayList<>();
    @JsonIgnore
    protected List<Q> externalTypes = new ArrayList<>();

    /**
     * Aliases for basic types introduced on the model, such as type + precision specification
     */
    @JsonIgnore
    protected R xmlAlias;

    public UserXmlModel(@Nonnull @JacksonXmlProperty(isAttribute = true, localName = MODEL_NAME_TAG) String modelName,
                        @JacksonXmlProperty(isAttribute = true, localName = COMPONENT_CODE_TAG) String componentCode,
                        @JacksonXmlProperty(isAttribute = true, localName = VERSION_TAG) String version,
                        @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                        @JacksonXmlProperty(isAttribute = true, localName = TABLE_PREFIX_TAG) String tablePrefix,
                        @JacksonXmlProperty(isAttribute = true, localName = VERSIONED_ENTITIES_TAG) Boolean versionedEntities,
                        @JacksonXmlProperty(isAttribute = true, localName = AUTO_ID_METHOD_TAG) IdCategory autoIdMethod
    ) {
        this.modelName = modelName;

        this.componentCode = componentCode;
        this.version = version;
        this.description = description;
        this.tablePrefix = tablePrefix;
        this.versionedEntities = versionedEntities;
        this.autoIdMethod = validateAutoIdOrDefault(autoIdMethod);
    }

    @JacksonXmlProperty(isAttribute = true, localName = MODEL_NAME_TAG)
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @JacksonXmlProperty(isAttribute = true, localName = COMPONENT_CODE_TAG)
    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSIONED_ENTITIES_TAG)
    public Boolean isVersionedEntities() {
        return versionedEntities;
    }

    public void setVersionedEntities(Boolean versionedEntities) {
        this.versionedEntities = versionedEntities;
    }

    @JacksonXmlProperty(isAttribute = true, localName = AUTO_ID_METHOD_TAG)
    public IdCategory getAutoIdMethod() {
        return autoIdMethod;
    }

    public void setAutoIdMethod(IdCategory autoIdMethod) {
        this.autoIdMethod = validateAutoIdOrDefault(autoIdMethod);
    }

    private IdCategory validateAutoIdOrDefault(IdCategory value) {

        final IdCategory result = Objects.isNull(value) ? IdCategory.SNOWFLAKE : value;

        if (result.isGeneratedAlways()) {
            return result;
        }

        throw new IllegalArgumentException("Model teg '" + AUTO_ID_METHOD_TAG + "' has incorrect auto generate id value '" + value + "'");

    }

    @JacksonXmlProperty(isAttribute = true, localName = VERSION_TAG)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = TABLE_PREFIX_TAG)
    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    @JsonSetter(value = UserXmlQuery.QUERY_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addQueries(Collection<X> queries) {
        queries.forEach(this::addQuery);
    }

    public void addQuery(X query) {
        queries.put(query.getName(), query);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = UserXmlQuery.QUERY_TAG)
    public List<X> getQueriesAsList() {
        return new ArrayList<>(queries.values());
    }

    @JsonSetter(value = INTERFACE_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addInterfaces(Collection<W> interfaces) {
        interfaces.forEach(this::addInterface);
    }

    public void addInterface(W iface) {
        interfaces.put(iface.getName(), iface);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = INTERFACE_TAG)
    public List<W> getInterfacesAsList() {
        return new ArrayList<>(interfaces.values());
    }

    @JsonSetter(value = CLASS_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addClasses(List<V> classes) {
        classes.forEach(this::addClass);
    }

    public void addClass(V modelClass) {
        String className = modelClass.getName();
        classes.put(className.toLowerCase(Locale.ROOT), modelClass);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = CLASS_TAG)
    public List<V> getClassesAsList() {
        return new ArrayList<>(classes.values());
    }

    /**
     * Adding the passed imports to the model
     */
    @JsonSetter(value = IMPORT_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addImports(List<U> imports) {
        this.imports.addAll(imports);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = IMPORT_TAG)
/** Information about imported additional files into the model file */
    public Set<U> getImports() {
        return imports;
    }

    /**
     * Adds the passed enums to the class
     */
    @JsonSetter(value = UserXmlEnum.ENUM_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addEnums(List<T> enums) {
        enums.forEach(this::addEnum);
    }

    public void addEnum(T modelEnum) {
        this.enums.add(modelEnum);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = UserXmlEnum.ENUM_TAG)
    public List<T> getEnums() {
        return Collections.unmodifiableList(enums);
    }

    @JsonSetter(value = EVENT_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addEvents(List<S> events) {
        this.events.addAll(events);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = EVENT_TAG)
    public List<S> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @JsonIgnore
    public S getEvent(String name) {
        return events.stream().filter(it -> Objects.equals(it.getName(), name)).findFirst().orElse(null);
    }

    @JacksonXmlProperty(isAttribute = true, localName = ALIASES_TAG)
    public void setXmlAlias(R xmlAlias) {
        this.xmlAlias = xmlAlias;
    }

    @JacksonXmlProperty(isAttribute = true, localName = ALIASES_TAG)
    public R getXmlAlias() {
        return xmlAlias;
    }

    @JacksonXmlElementWrapper(localName = EXTERNAL_TYPES_TAG)
    public List<Q> getExternalTypes() {
        return externalTypes;
    }

    public void setExternalTypes(List<Q> externalTypes) {
        this.externalTypes.addAll(externalTypes);
    }

    @Override
    public String toString() {
        return " ===== The factory model \"" + modelName + "\" =====";
    }
}
