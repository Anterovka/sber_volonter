package com.sbt.mg.data.model.usermodel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.sbt.mg.Helper;
import com.sbt.mg.data.model.ClassStrategy;
import com.sbt.mg.data.model.EntityDiff;
import com.sbt.mg.data.model.interfaces.XmlTagName;
import com.sbt.status.xml.user.UserXmlStatuses;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.sbt.mg.data.model.usermodel.UserXmlId.ID_DESCRIPTION_TAG;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@XmlTagName(UserXmlModelClass.CLASS_TAG)
public class UserXmlModelClass<
    S extends UserXmlStatuses,
    T extends UserXmlIdempotenceExclude,
    V extends UserXmlIndex,
    U extends UserXmlCciIndex,
    W extends UserXmlId,
    Y extends UserXmlModelClassProperty,
    Z extends UserXmlModelClassReference>
    extends EntityDiff {

    public static final String CLASS_TAG = "class";

    public static final String NAME_TAG = "name";
    public static final String EXTENDS_TAG = "extends";
    public static final String EMBEDDED_TAG = "embeddable";
    public static final String LABEL_TAG = "label";
    public static final String DESCRIPTION_TAG = "description";
    public static final String ABSTRACT_TAG = "is-abstract";
    public static final String DICTIONARY_TAG = "is-dictionary";
    public static final String FINAL_CLASS_TAG = "final-class";
    public static final String STRATEGY_TAG = "strategy";
    public static final String LOCKABLE_TAG = "lockable";
    public static final String INDEX_TAG = "index";
    public static final String CCI_INDEX_TAG = "cci-index";
    public static final String PROPERTY_TAG = "property";
    public static final String REFERENCE_TAG = "reference";
    public static final String IMPLEMENTS_TAG = "implements";
    public static final String IDEMPOTENCE_EXCLUDE_TAG = "idempotence-exclude";
    public static final String USE_ID_PREFIX_TAG = "id-prefixed";
    public static final String PARTITION_KEY_REGEX_TAG = "partition-key-regex";
    public static final String CLONEABLE_TAG = "cloneable";
    public static final String STATUSES_TAG = "statuses";

    @Setter
    protected String name;
    protected String extendedClass;
    /**
     * Specifies whether the class is embeddable
     */
    protected Boolean embeddable;
    @Getter
    protected String label = "";
    /**
     * Note to table, defined on the model
     */
    protected String description = "";
    /**
     * Sign of abstract class (without direct mapping to physical table)
     */
    protected Boolean isAbstract;
    protected Boolean isDictionary;
    protected Boolean finalClass;

    protected boolean isUseIdPrefix;

    protected String partitionKeyRegex;

    protected boolean cloneable;

    /**
     * The strategy used for the class (SingleTable, JOINED).
     * Mixing strategies is not allowed
     * May be absent for abstract classes
     */
    protected ClassStrategy strategy;
    protected W id;
    protected boolean lockable;
    /**
     * List of properties whose values are not taken into account when calculating the verification hash
     * during the execution of an idempotent call
     */
    private T idempotenceExclude;
    // String representation of the interfaces declared for implementation by the class
    // Conversion to the collection is not done immediately, because at the time of processing, the interfaces may not have been loaded into the model yet
    protected String implementedInterfacesAsString;

    /**
     * List of indices declared on the class
     */
    protected final List<V> indices = new ArrayList<>();
    protected final List<U> cciIndices = new ArrayList<>();

    @JsonIgnore
    protected final List<Y> properties = new ArrayList<>();
    @JsonIgnore
    protected final List<Z> references = new ArrayList<>();

    @Setter
    protected S statuses;

    public UserXmlModelClass() {
    }

    @JsonCreator
    public UserXmlModelClass(@JacksonXmlProperty(isAttribute = true, localName = NAME_TAG) String name,
                             @JacksonXmlProperty(isAttribute = true, localName = EXTENDS_TAG) String extendedClass,
                             @JacksonXmlProperty(isAttribute = true, localName = IMPLEMENTS_TAG) String implementedInterfacesAsString,
                             @JacksonXmlProperty(isAttribute = true, localName = EMBEDDED_TAG) Boolean embeddable,
                             @JacksonXmlProperty(isAttribute = true, localName = LABEL_TAG) String label,
                             @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG) String description,
                             @JacksonXmlProperty(isAttribute = true, localName = ABSTRACT_TAG) Boolean isAbstract,
                             @JacksonXmlProperty(isAttribute = true, localName = DICTIONARY_TAG) Boolean isDictionary,
                             @JacksonXmlProperty(isAttribute = true, localName = FINAL_CLASS_TAG) Boolean finalClass,
                             @JacksonXmlProperty(isAttribute = true, localName = STRATEGY_TAG) ClassStrategy strategy,
                             @JacksonXmlProperty(isAttribute = true, localName = LOCKABLE_TAG) Boolean lockable,
                             @JacksonXmlProperty(isAttribute = true, localName = ID_DESCRIPTION_TAG) W id,
                             @JacksonXmlProperty(isAttribute = true, localName = IDEMPOTENCE_EXCLUDE_TAG) T idempotenceExclude,
                             @JacksonXmlProperty(isAttribute = true, localName = USE_ID_PREFIX_TAG) Boolean isUseIdPrefix,
                             @JacksonXmlProperty(isAttribute = true, localName = PARTITION_KEY_REGEX_TAG) String partitionKeyRegex,
                             @JacksonXmlProperty(isAttribute = true, localName = CLONEABLE_TAG) Boolean cloneable,
                             @JacksonXmlProperty(localName = STATUSES_TAG) S statuses
    ) {
        this.name = name;

        this.extendedClass = extendedClass;
        this.implementedInterfacesAsString = implementedInterfacesAsString;

        this.label = Helper.replaceNullToEmpty(label);

        this.embeddable = embeddable;

        this.isUseIdPrefix = Optional.ofNullable(isUseIdPrefix).orElse(false);

        this.partitionKeyRegex =partitionKeyRegex;

        this.description = Helper.replaceNullToEmpty(description);
        this.isAbstract = isAbstract;
        this.isDictionary = isDictionary;
        this.finalClass = finalClass;
        this.strategy = strategy;
        this.lockable = Optional.ofNullable(lockable).orElse(Boolean.FALSE);
        this.id = id;
        this.idempotenceExclude = idempotenceExclude;
        this.cloneable = Optional.ofNullable(cloneable).orElse(false);

        this.statuses = statuses;
    }

    @JacksonXmlProperty(isAttribute = true, localName = NAME_TAG)
    public String getName() {
        return name;
    }

    @JsonSetter(value = EXTENDS_TAG)
    public void setExtendedClassName(String extendedClass) {
        this.extendedClass = extendedClass;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EXTENDS_TAG)
    public String getExtendedClassName() {
        return extendedClass;
    }

    public void setLabel(@Nonnull String label) {
        this.label = Helper.replaceNullToEmpty(label);
    }

    public void setPartitionKeyRegex(String partitionKeyRegex) {
        this.partitionKeyRegex = partitionKeyRegex;
    }

    @JacksonXmlProperty(isAttribute = true, localName = PARTITION_KEY_REGEX_TAG)
    public String getPartitionKeyRegex() {
        return partitionKeyRegex;
    }

    @JsonIgnore
    public String getPartitionKeyRegexNotNullAsEmpty() {
        return Optional.ofNullable(partitionKeyRegex).orElse("");
    }

    public void setDictionary(Boolean aDictionary) {
        isDictionary = aDictionary;
    }

    @JacksonXmlProperty(isAttribute = true, localName = DICTIONARY_TAG)
    public Boolean isDictionary() {
        return isDictionary == null ? Boolean.FALSE : isDictionary;
    }

    public void setEmbeddable(Boolean embeddable) {
        this.embeddable = embeddable;
    }

    @JacksonXmlProperty(isAttribute = true, localName = EMBEDDED_TAG)
    public boolean isEmbeddable() {
        return embeddable == null ? Boolean.FALSE : embeddable;
    }

    public void setDescription(String description) {
        this.description = Helper.replaceNullToEmpty(description);
    }

    @JacksonXmlProperty(isAttribute = true, localName = DESCRIPTION_TAG)
    public String getDescription() {
        return description;
    }

    @JacksonXmlProperty(isAttribute = true, localName = FINAL_CLASS_TAG)
    public Boolean isFinalClass() {
        return finalClass;
    }

    public void setFinalClass(Boolean finalClass) {
        this.finalClass = finalClass;
    }

    public void setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
    }

    @JacksonXmlProperty(isAttribute = true, localName = USE_ID_PREFIX_TAG)
    public boolean isUseIdPrefix() {
        return this.isUseIdPrefix;
    }

    public void setUseIdPrefix(Boolean useIdPrefix) {
        this.isUseIdPrefix = Optional.ofNullable(useIdPrefix).orElse(false);
    }

    @JacksonXmlProperty(isAttribute = true, localName = CLONEABLE_TAG)
    public boolean isCloneable() {
        return this.cloneable;
    }

    public void setCloneable(Boolean cloneable) {
        this.cloneable = Optional.ofNullable(cloneable).orElse(false);
    }

    @JacksonXmlProperty(isAttribute = true, localName = ABSTRACT_TAG)
    public Boolean isAbstract() {
        return isAbstract;
    }

    @JacksonXmlProperty(isAttribute = true, localName = STRATEGY_TAG)
    public ClassStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(ClassStrategy strategy) {
        this.strategy = strategy;
    }

    @JacksonXmlProperty(localName = ID_DESCRIPTION_TAG)
    public W getId() {
        return id;
    }

    public void setId(W id) {
        this.id = id;
    }

    @JacksonXmlProperty(isAttribute = true, localName = LOCKABLE_TAG)
    public boolean getLockable() {
        return lockable;
    }

    public void setLockable(boolean lockable) {
        this.lockable = lockable;
    }

    public void setIdempotenceExclude(T idempotenceExclude) {
        this.idempotenceExclude = idempotenceExclude;
    }

    @JacksonXmlProperty(isAttribute = true, localName = IDEMPOTENCE_EXCLUDE_TAG)
    public T getIdempotenceExclude() {
        return this.idempotenceExclude;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = INDEX_TAG)
    public List<V> getIndices() {
        return Collections.unmodifiableList(indices);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonSetter(value = INDEX_TAG)
    public void addIndices(List<V> indexes) {
        indexes.forEach(this::addIndex);
    }

    public void addIndex(V index) {
        indices.add(index);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonSetter(value = CCI_INDEX_TAG)
    public void addCciIndices(List<U> cciIndices) {
        cciIndices.forEach(this::addCciIndex);
    }

    private void addCciIndex(U cciIndex) {
        cciIndices.add(cciIndex);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = CCI_INDEX_TAG)
    public List<U> getCciIndices() {
        return cciIndices;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = PROPERTY_TAG)
    public List<Y> getPropertiesAsList() {
        return new ArrayList<>(properties);
    }

    @JsonSetter(value = PROPERTY_TAG)
    public void addProperties(Collection<Y> properties) {
        properties.forEach(this::addProperty);
    }

    public void addProperty(Y property) {
        properties.add(property);
    }

    @JsonSetter(value = REFERENCE_TAG)
    @JacksonXmlElementWrapper(useWrapping = false)
    public void addReferences(List<Z> references) {
        references.forEach(this::addReference);
    }

    public void addReference(Z reference) {
        references.add(reference);
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(isAttribute = true, localName = REFERENCE_TAG)
    public List<Z> getReferenceAsList() {
        return new ArrayList<>(references);
    }

    @JacksonXmlProperty(localName = STATUSES_TAG)
    public S getStatuses() {
        return this.statuses;
    }
}
