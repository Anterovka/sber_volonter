package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ParamDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitivesCollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferencesCollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.getPropertyDescription;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.processPropertyDescriptions;

/**
 * Implementation of entity description
 */
class EntityDescriptionImpl extends AbstractObjectWithMetaDataManager implements EntityDescription {

    ModelDescription modelDescription;
    EntityDescription parentEntityDescription;
    Collection<EntityDescription> childEntityDescriptions = new ArrayList<>();
    EntityDescription rootEntityDescription;
    EntityDescription aggregateEntityDescription;
    String name;
    boolean final0;
    boolean aggregate;
    InheritanceStrategy inheritanceStrategy;
    TableType tableType;
    String tableName;
    String idColumnName;
    String typeColumnName;
    String aggregateColumnName;
    String systemLocksTableName;
    String systemLocksAggregateColumnName;
    String systemLocksVersionColumnName;
    Map<String, ParamDescription> paramDescriptions;
    Map<String, PrimitiveDescription> declaredPrimitiveDescriptions;
    Map<String, PrimitivesCollectionDescription> declaredPrimitivesCollectionDescriptions;
    Map<String, ReferenceDescription> declaredReferenceDescriptions;
    Map<String, ReferenceDescription> declaredReferenceBackReferenceDescriptions;
    Map<String, ReferencesCollectionDescription> declaredReferencesCollectionDescriptions;
    Map<String, ReferenceDescription> declaredReferencesCollectionBackReferenceDescriptions;
    Map<String, GroupDescription> declaredGroupDescriptions;
    Map<String, PrimitiveDescription> primitiveDescriptions;
    Map<String, PrimitivesCollectionDescription> primitivesCollectionDescriptions;
    Map<String, ReferenceDescription> referenceDescriptions;
    Map<String, ReferenceDescription> referenceBackReferenceDescriptions;
    Map<String, ReferencesCollectionDescription> referencesCollectionDescriptions;
    Map<String, ReferenceDescription> referencesCollectionBackReferenceDescriptions;
    Map<String, GroupDescription> groupDescriptions;
    Set<String> allParentNames;

    /**
     * Check property names
     *
     * @param propertyNames        Names of properties
     * @param entityDescription    Entity description
     * @param propertyDescriptions Property descriptions
     * @param <D>                  Type of property description
     */
    <D> void checkPropertyNames(Set<String> propertyNames, EntityDescription entityDescription, Map<String, D> propertyDescriptions) {
        List<String> duplicatePropertyNames = propertyDescriptions.keySet().stream().filter(propertyNames::contains).collect(Collectors.toList());
        if (!duplicatePropertyNames.isEmpty()) {
            throw new DuplicatePropertyNamesFoundException(entityDescription.getName(), duplicatePropertyNames);
        }
        propertyNames.addAll(propertyDescriptions.keySet());
    }

    /**
     * Check property names
     *
     * @param propertyNames     Names of properties
     * @param entityDescription Entity description
     */
    void checkPropertyNames(Set<String> propertyNames, EntityDescription entityDescription) {
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredPrimitiveDescriptions());
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredPrimitivesCollectionDescriptions());
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredReferenceDescriptions());
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredReferenceBackReferenceDescriptions());
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredReferencesCollectionDescriptions());
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredReferencesCollectionBackReferenceDescriptions());
        checkPropertyNames(propertyNames, entityDescription, entityDescription.getDeclaredGroupDescriptions());
    }

    /**
     * Process
     *
     * @param entityDescriptionSettings The entity description settings
     */
    void process(EntityDescriptionSettings entityDescriptionSettings) {
        if (entityDescriptionSettings.parentEntityType != null) {
            parentEntityDescription = modelDescription.getEntityDescription(entityDescriptionSettings.parentEntityType);
            if (parentEntityDescription.isFinal()) {
                throw new FinalParentEntityDescriptionException(parentEntityDescription.getName());
            }
        }
        final0 = entityDescriptionSettings.final0;
        aggregate = entityDescriptionSettings.aggregate;
        inheritanceStrategy = entityDescriptionSettings.inheritanceStrategy;
        tableType = entityDescriptionSettings.tableType;
        tableName = entityDescriptionSettings.tableName;
        idColumnName = entityDescriptionSettings.idColumnName;
        typeColumnName = entityDescriptionSettings.typeColumnName;
        aggregateColumnName = entityDescriptionSettings.aggregateColumnName;
        systemLocksTableName = entityDescriptionSettings.systemLocksTableName;
        systemLocksAggregateColumnName = entityDescriptionSettings.systemLocksAggregateColumnName;
        systemLocksVersionColumnName = entityDescriptionSettings.systemLocksVersionColumnName;
        paramDescriptions = new LinkedHashMap<>(entityDescriptionSettings.paramDescriptionsSettings.size());
        entityDescriptionSettings.paramDescriptionsSettings.forEach((paramName, paramDescription) -> paramDescriptions.put(paramName, new ParamDescriptionImpl(paramName, paramDescription.type, paramDescription.collection, paramDescription.defaultValue)));
        declaredPrimitiveDescriptions = new LinkedHashMap<>(entityDescriptionSettings.primitiveDescriptionsSettings.size());
        declaredPrimitivesCollectionDescriptions = new LinkedHashMap<>(entityDescriptionSettings.primitivesCollectionDescriptionsSettings.size());
        declaredReferenceDescriptions = new LinkedHashMap<>(entityDescriptionSettings.referenceDescriptionsSettings.size());
        declaredReferenceBackReferenceDescriptions = new LinkedHashMap<>();
        declaredReferencesCollectionDescriptions = new LinkedHashMap<>(entityDescriptionSettings.referencesCollectionDescriptionsSettings.size());
        declaredReferencesCollectionBackReferenceDescriptions = new LinkedHashMap<>();
        declaredGroupDescriptions = new LinkedHashMap<>(entityDescriptionSettings.groupDescriptionsSettings.size());
    }

    /**
     * Processed (2nd iteration)
     *
     * @param entityDescriptionSettings The entity description settings
     */
    void process2(EntityDescriptionSettings entityDescriptionSettings) {
        if (parentEntityDescription == null) {
            if (entityDescriptionSettings.aggregateEntityType != null) {
                aggregateEntityDescription = modelDescription.getEntityDescription(entityDescriptionSettings.aggregateEntityType);
                if (!aggregateEntityDescription.isAggregate()) {
                    throw new NotAggregateEntityDescription(aggregateEntityDescription.getName());
                }
            }
        } else {
            if (entityDescriptionSettings.aggregate) {
                throw new AggregateForNotRootEntityDescriptionFoundException();
            }
            if (entityDescriptionSettings.aggregateEntityType != null) {
                throw new AggregateEntityDescriptionForNotRootEntityDescriptionFoundException(entityDescriptionSettings.aggregateEntityType);
            }
        }
        EntityDescription currentEntityDescription = parentEntityDescription;
        while (currentEntityDescription != null) {
            currentEntityDescription.getChildEntityDescriptions().add(this);
            currentEntityDescription = currentEntityDescription.getParentEntityDescription();
        }
        processPropertyDescriptions(this, entityDescriptionSettings.primitiveDescriptionsSettings, declaredPrimitiveDescriptions, PrimitiveDescriptionImpl::new);
        processPropertyDescriptions(this, entityDescriptionSettings.primitivesCollectionDescriptionsSettings, declaredPrimitivesCollectionDescriptions, PrimitivesCollectionDescriptionImpl::new);
        processPropertyDescriptions(this, entityDescriptionSettings.referenceDescriptionsSettings, declaredReferenceDescriptions, ReferenceDescriptionImpl::new);
        processPropertyDescriptions(this, entityDescriptionSettings.referencesCollectionDescriptionsSettings, declaredReferencesCollectionDescriptions, ReferencesCollectionDescriptionImpl::new);
        processPropertyDescriptions(this, entityDescriptionSettings.groupDescriptionsSettings, declaredGroupDescriptions, GroupDescriptionImpl::new);
    }

    /**
     * Processed (3rd iteration)
     */
    void process3() {
        childEntityDescriptions = Collections.unmodifiableCollection(childEntityDescriptions);
        Set<String> propertyNames = new HashSet<>();
        primitiveDescriptions = new LinkedHashMap<>(declaredPrimitiveDescriptions.size());
        primitivesCollectionDescriptions = new LinkedHashMap<>(declaredPrimitivesCollectionDescriptions.size());
        referenceDescriptions = new LinkedHashMap<>(declaredReferenceDescriptions.size());
        referenceBackReferenceDescriptions = new LinkedHashMap<>(declaredReferenceBackReferenceDescriptions.size());
        referencesCollectionDescriptions = new LinkedHashMap<>(declaredReferencesCollectionDescriptions.size());
        referencesCollectionBackReferenceDescriptions = new LinkedHashMap<>(declaredReferencesCollectionBackReferenceDescriptions.size());
        groupDescriptions = new LinkedHashMap<>(declaredGroupDescriptions.size());
        allParentNames = new HashSet<>();
        EntityDescription currentEntityDescription = this;
        while (currentEntityDescription != null) {
            checkPropertyNames(propertyNames, currentEntityDescription);
            allParentNames.add(currentEntityDescription.getName());
            rootEntityDescription = currentEntityDescription;
            primitiveDescriptions.putAll(currentEntityDescription.getDeclaredPrimitiveDescriptions());
            primitivesCollectionDescriptions.putAll(currentEntityDescription.getDeclaredPrimitivesCollectionDescriptions());
            referenceDescriptions.putAll(currentEntityDescription.getDeclaredReferenceDescriptions());
            referenceBackReferenceDescriptions.putAll(currentEntityDescription.getDeclaredReferenceBackReferenceDescriptions());
            referencesCollectionDescriptions.putAll(currentEntityDescription.getDeclaredReferencesCollectionDescriptions());
            referencesCollectionBackReferenceDescriptions.putAll(currentEntityDescription.getDeclaredReferencesCollectionBackReferenceDescriptions());
            groupDescriptions.putAll(currentEntityDescription.getDeclaredGroupDescriptions());
            currentEntityDescription = currentEntityDescription.getParentEntityDescription();
        }
        declaredPrimitiveDescriptions = Collections.unmodifiableMap(declaredPrimitiveDescriptions);
        declaredPrimitivesCollectionDescriptions = Collections.unmodifiableMap(declaredPrimitivesCollectionDescriptions);
        declaredReferenceDescriptions = Collections.unmodifiableMap(declaredReferenceDescriptions);
        declaredReferenceBackReferenceDescriptions = Collections.unmodifiableMap(declaredReferenceBackReferenceDescriptions);
        declaredReferencesCollectionDescriptions = Collections.unmodifiableMap(declaredReferencesCollectionDescriptions);
        declaredReferencesCollectionBackReferenceDescriptions = Collections.unmodifiableMap(declaredReferencesCollectionBackReferenceDescriptions);
        declaredGroupDescriptions = Collections.unmodifiableMap(declaredGroupDescriptions);
        primitiveDescriptions = Collections.unmodifiableMap(primitiveDescriptions);
        primitivesCollectionDescriptions = Collections.unmodifiableMap(primitivesCollectionDescriptions);
        referenceDescriptions = Collections.unmodifiableMap(referenceDescriptions);
        referenceBackReferenceDescriptions = Collections.unmodifiableMap(referenceBackReferenceDescriptions);
        referencesCollectionDescriptions = Collections.unmodifiableMap(referencesCollectionDescriptions);
        referencesCollectionBackReferenceDescriptions = Collections.unmodifiableMap(referencesCollectionBackReferenceDescriptions);
        groupDescriptions = Collections.unmodifiableMap(groupDescriptions);
    }

    @Override
    public ModelDescription getModelDescription() {
        return modelDescription;
    }

    @Override
    public EntityDescription getParentEntityDescription() {
        return parentEntityDescription;
    }

    @Override
    public Collection<EntityDescription> getChildEntityDescriptions() {
        return childEntityDescriptions;
    }

    @Override
    public EntityDescription getRootEntityDescription() {
        return rootEntityDescription;
    }

    @Override
    public EntityDescription getAggregateEntityDescription() {
        return aggregateEntityDescription;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFinal() {
        return final0;
    }

    @Override
    public boolean isAggregate() {
        return aggregate;
    }

    @Override
    public Map<String, PrimitiveDescription> getDeclaredPrimitiveDescriptions() {
        return declaredPrimitiveDescriptions;
    }

    @Override
    public Map<String, PrimitivesCollectionDescription> getDeclaredPrimitivesCollectionDescriptions() {
        return declaredPrimitivesCollectionDescriptions;
    }

    @Override
    public Map<String, ReferenceDescription> getDeclaredReferenceDescriptions() {
        return declaredReferenceDescriptions;
    }

    @Override
    public Map<String, ReferenceDescription> getDeclaredReferenceBackReferenceDescriptions() {
        return declaredReferenceBackReferenceDescriptions;
    }

    @Override
    public Map<String, ReferencesCollectionDescription> getDeclaredReferencesCollectionDescriptions() {
        return declaredReferencesCollectionDescriptions;
    }

    @Override
    public Map<String, ReferenceDescription> getDeclaredReferencesCollectionBackReferenceDescriptions() {
        return declaredReferencesCollectionBackReferenceDescriptions;
    }

    @Override
    public Map<String, GroupDescription> getDeclaredGroupDescriptions() {
        return declaredGroupDescriptions;
    }

    @Override
    public Map<String, PrimitiveDescription> getPrimitiveDescriptions() {
        return primitiveDescriptions;
    }

    @Override
    public Map<String, PrimitivesCollectionDescription> getPrimitivesCollectionDescriptions() {
        return primitivesCollectionDescriptions;
    }

    @Override
    public Map<String, ReferenceDescription> getReferenceDescriptions() {
        return referenceDescriptions;
    }

    @Override
    public Map<String, ReferenceDescription> getReferenceBackReferenceDescriptions() {
        return referenceBackReferenceDescriptions;
    }

    @Override
    public Map<String, ReferencesCollectionDescription> getReferencesCollectionDescriptions() {
        return referencesCollectionDescriptions;
    }

    @Override
    public Map<String, ReferenceDescription> getReferencesCollectionBackReferenceDescriptions() {
        return referencesCollectionBackReferenceDescriptions;
    }

    @Override
    public Map<String, GroupDescription> getGroupDescriptions() {
        return groupDescriptions;
    }

    @Override
    public InheritanceStrategy getInheritanceStrategy() {
        return inheritanceStrategy;
    }

    @Override
    public TableType getTableType() {
        return tableType;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public String getIdColumnName() {
        return idColumnName;
    }

    @Override
    public String getTypeColumnName() {
        return typeColumnName;
    }

    @Override
    public String getAggregateColumnName() {
        return aggregateColumnName;
    }

    @Override
    public String getSystemLocksTableName() {
        return systemLocksTableName;
    }

    @Override
    public String getSystemLocksAggregateColumnName() {
        return systemLocksAggregateColumnName;
    }

    @Override
    public String getSystemLocksVersionColumnName() {
        return systemLocksVersionColumnName;
    }

    @Override
    public Map<String, ParamDescription> getParamDescriptions() {
        return paramDescriptions;
    }

    @Override
    public boolean isExtensionOf(String entityType) {
        return allParentNames.contains(entityType);
    }

    @Override
    public PrimitiveDescription getPrimitiveDescription(String propertyName) {
        return getPropertyDescription(primitiveDescriptions, propertyName, PrimitiveDescriptionNotFoundException::new);
    }

    @Override
    public PrimitivesCollectionDescription getPrimitivesCollectionDescription(String propertyName) {
        return getPropertyDescription(primitivesCollectionDescriptions, propertyName, PrimitivesCollectionDescriptionNotFoundException::new);
    }

    @Override
    public ReferenceDescription getReferenceDescription(String propertyName) {
        return getPropertyDescription(referenceDescriptions, propertyName, ReferenceDescriptionNotFoundException::new);
    }

    @Override
    public ReferenceDescription getReferenceBackReferenceDescription(String propertyName) {
        return getPropertyDescription(referenceBackReferenceDescriptions, propertyName, ReferenceBackReferenceDescriptionNotFoundException::new);
    }

    @Override
    public ReferencesCollectionDescription getReferencesCollectionDescription(String propertyName) {
        return getPropertyDescription(referencesCollectionDescriptions, propertyName, ReferencesCollectionDescriptionNotFoundException::new);
    }

    @Override
    public ReferenceDescription getReferencesCollectionBackReferenceDescription(String propertyName) {
        return getPropertyDescription(referencesCollectionBackReferenceDescriptions, propertyName, ReferencesCollectionBackReferenceDescriptionNotFoundException::new);
    }

    @Override
    public GroupDescription getGroupDescription(String propertyName) {
        return getPropertyDescription(groupDescriptions, propertyName, GroupDescriptionNotFoundException::new);
    }
}
