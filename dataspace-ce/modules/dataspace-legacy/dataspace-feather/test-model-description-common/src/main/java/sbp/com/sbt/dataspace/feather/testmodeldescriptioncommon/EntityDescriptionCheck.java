package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checking entity description
 */
public class EntityDescriptionCheck extends AbstractCheck {

    ModelDescription modelDescription;
    EntityDescription entityDescription;
    List<String> parentEntityTypes = Collections.emptyList();
    String aggregateEntityType;
    String name;
    boolean final0;
    boolean aggregate;
    InheritanceStrategy inheritanceStrategy;
    TableType tableType = TableType.SIMPLE;
    String tableName;
    String idColumnName;
    String typeColumnName;
    String aggregateColumnName;
    String systemLocksTableName;
    String systemLocksAggregateColumnName;
    String systemLocksVersionColumnName;
    Map<String, ParamDescriptionCheck> paramDescriptionChecks = new LinkedHashMap<>();
    Map<String, PrimitiveDescriptionCheck> primitiveDescriptionChecks = new LinkedHashMap<>();
    Map<String, PrimitivesCollectionDescriptionCheck> primitivesCollectionDescriptionChecks = new LinkedHashMap<>();
    Map<String, ReferenceDescriptionCheck> referenceDescriptionChecks = new LinkedHashMap<>();
    Map<String, ReferenceBackReferenceDescriptionCheck> referenceBackReferenceDescriptionChecks = new LinkedHashMap<>();
    Map<String, ReferencesCollectionDescriptionCheck> referencesCollectionDescriptionChecks = new LinkedHashMap<>();
    Map<String, ReferencesCollectionBackReferenceDescriptionCheck> referencesCollectionBackReferenceDescriptionChecks = new LinkedHashMap<>();
    Map<String, GroupDescriptionCheck> groupDescriptionChecks = new LinkedHashMap<>();

    /**
     * @param modelDescription Model description
     * @param name             Name
     */
    EntityDescriptionCheck(ModelDescription modelDescription, String name) {
        this.modelDescription = modelDescription;
        entityDescription = modelDescription.getEntityDescription(name);
        this.name = name;
    }

    /**
     * Check inheritance
     */
    void checkInheritance() {
        List<EntityDescription> expectedParentEntityDescriptions = parentEntityTypes.stream()
            .map(modelDescription::getEntityDescription)
            .collect(Collectors.toList());
        Map<String, EntityDescription> entityDescriptions = new LinkedHashMap<>(modelDescription.getEntityDescriptions());
        assertTrue(entityDescription.isExtensionOf(entityDescription.getName()));
        entityDescriptions.remove(entityDescription.getName());
        expectedParentEntityDescriptions.forEach(parentEntityDescription -> {
            assertTrue(entityDescription.isExtensionOf(parentEntityDescription.getName()));
            parentEntityDescription.getDeclaredPrimitiveDescriptions().forEach((propertyName, primitiveDescription) -> assertEquals(primitiveDescription, entityDescription.getPrimitiveDescription(propertyName)));
            parentEntityDescription.getDeclaredPrimitivesCollectionDescriptions().forEach((propertyName, primitivesCollectionDescription) -> assertEquals(primitivesCollectionDescription, entityDescription.getPrimitivesCollectionDescription(propertyName)));
            parentEntityDescription.getDeclaredReferenceDescriptions().forEach((propertyName, referenceDescription) -> assertEquals(referenceDescription, entityDescription.getReferenceDescription(propertyName)));
            parentEntityDescription.getDeclaredReferenceBackReferenceDescriptions().forEach((propertyName, backReferenceDescription) -> assertEquals(backReferenceDescription, entityDescription.getReferenceBackReferenceDescription(propertyName)));
            parentEntityDescription.getDeclaredReferencesCollectionDescriptions().forEach((propertyName, referencesCollectionDescription) -> assertEquals(referencesCollectionDescription, entityDescription.getReferencesCollectionDescription(propertyName)));
            parentEntityDescription.getDeclaredReferencesCollectionBackReferenceDescriptions().forEach((propertyName, backReferenceDescription) -> assertEquals(backReferenceDescription, entityDescription.getReferencesCollectionBackReferenceDescription(propertyName)));
            entityDescriptions.remove(parentEntityDescription.getName());
        });
        entityDescriptions.keySet().forEach(entityType -> assertFalse(entityDescription.isExtensionOf(entityType)));
    }

    /**
     * Set parent entity types
     *
     * @return Current check
     */
    public EntityDescriptionCheck setParentEntityTypes(String... parentEntityTypes) {
        this.parentEntityTypes = Arrays.asList(parentEntityTypes);
        return this;
    }

    /**
     * Set the aggregate entity type
     *
     * @return Current check
     */
    public EntityDescriptionCheck setAggregateEntityType(String aggregateEntityType) {
        this.aggregateEntityType = aggregateEntityType;
        return this;
    }

    /**
     * Set the flag for the final entity description
     *
     * @return Current check
     */
    public EntityDescriptionCheck setFinal() {
        this.final0 = true;
        return this;
    }

    /**
     * Set the aggregate flag
     *
     * @return Current check
     */
    public EntityDescriptionCheck setAggregate() {
        this.aggregate = true;
        return this;
    }

    /**
     * Set inheritance strategy
     *
     * @return Current check
     */
    public EntityDescriptionCheck setInheritanceStrategy(InheritanceStrategy inheritanceStrategy) {
        this.inheritanceStrategy = inheritanceStrategy;
        return this;
    }

    /**
     * Set table type
     *
     * @return Current check
     */
    public EntityDescriptionCheck setTableType(TableType tableType) {
        this.tableType = tableType;
        return this;
    }

    /**
     * Set table name
     *
     * @return Current check
     */
    public EntityDescriptionCheck setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Set the column name with id
     *
     * @return Current check
     */
    public EntityDescriptionCheck setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
        return this;
    }

    /**
     * Set the column name with type
     *
     * @return Current check
     */
    public EntityDescriptionCheck setTypeColumnName(String typeColumnName) {
        this.typeColumnName = typeColumnName;
        return this;
    }

    /**
     * Set the column name with an aggregate
     *
     * @return Current check
     */
    public EntityDescriptionCheck setAggregateColumnName(String aggregateColumnName) {
        this.aggregateColumnName = aggregateColumnName;
        return this;
    }

    /**
     * Specify the name of the system lock table
     *
     * @return Current check
     */
    public EntityDescriptionCheck setSystemLocksTableName(String systemLocksTableName) {
        this.systemLocksTableName = systemLocksTableName;
        return this;
    }

    /**
     * Set the name of the column in the system lock table with the aggregate
     *
     * @return Current check
     */
    public EntityDescriptionCheck setSystemLocksAggregateColumnName(String systemLocksAggregateColumnName) {
        this.systemLocksAggregateColumnName = systemLocksAggregateColumnName;
        return this;
    }

    /**
     * Set the name of the column in the system lock table with the version
     *
     * @return Current check
     */
    public EntityDescriptionCheck setSystemLocksVersionColumnName(String systemLocksVersionColumnName) {
        this.systemLocksVersionColumnName = systemLocksVersionColumnName;
        return this;
    }

    /**
     * Set parameter description check
     *
     * @param paramName        Parameter name
     * @param checkInitializer The check initializer
     * @return Current check
     */
    public EntityDescriptionCheck setParamDescriptionCheck(String paramName, Consumer<ParamDescriptionCheck> checkInitializer) {
        checkInitializer.accept(paramDescriptionChecks.computeIfAbsent(paramName, key -> new ParamDescriptionCheck(entityDescription, paramName)));
        return this;
    }

    /**
     * Set check of primitive description
     *
     * @param propertyName     Property name
     * @param checkInitializer Инициализатор проверки
     * @return Current check
     */
    public EntityDescriptionCheck setPrimitiveDescriptionCheck(String propertyName, Consumer<PrimitiveDescriptionCheck> checkInitializer) {
        checkInitializer.accept(primitiveDescriptionChecks.computeIfAbsent(propertyName, key -> new PrimitiveDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    /**
     * Set primitive collection description check
     *
     * @param propertyName     Property name
     * @param checkInitializer Инициализатор проверки
     * @return Current check
     */
    public EntityDescriptionCheck setPrimitivesCollectionDescriptionCheck(String propertyName, Consumer<PrimitivesCollectionDescriptionCheck> checkInitializer) {
        checkInitializer.accept(primitivesCollectionDescriptionChecks.computeIfAbsent(propertyName, key -> new PrimitivesCollectionDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    /**
     * Set check for link description
     *
     * @param propertyName     Property name
     * @param checkInitializer Инициализатор проверки
     * @return Current check
     */
    public EntityDescriptionCheck setReferenceDescriptionCheck(String propertyName, Consumer<ReferenceDescriptionCheck> checkInitializer) {
        checkInitializer.accept(referenceDescriptionChecks.computeIfAbsent(propertyName, key -> new ReferenceDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    /**
     * Set check description of backlink link
     *
     * @param propertyName     Property name
     * @param checkInitializer Инициализатор проверки
     * @return Current check
     */
    public EntityDescriptionCheck setReferenceBackReferenceDescriptionCheck(String propertyName, Consumer<ReferenceBackReferenceDescriptionCheck> checkInitializer) {
        checkInitializer.accept(referenceBackReferenceDescriptionChecks.computeIfAbsent(propertyName, key -> new ReferenceBackReferenceDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    /**
     * Set description check for link collection
     *
     * @param propertyName     Property name
     * @param checkInitializer The initializer of the check
     * @return Current check
     */
    public EntityDescriptionCheck setReferencesCollectionDescriptionCheck(String propertyName, Consumer<ReferencesCollectionDescriptionCheck> checkInitializer) {
        checkInitializer.accept(referencesCollectionDescriptionChecks.computeIfAbsent(propertyName, key -> new ReferencesCollectionDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    /**
     * Set check description of backlink collection link
     *
     * @param propertyName     Property name
     * @param checkInitializer The initializer of the check
     * @return Current check
     */
    public EntityDescriptionCheck setReferencesCollectionBackReferenceDescriptionCheck(String propertyName, Consumer<ReferencesCollectionBackReferenceDescriptionCheck> checkInitializer) {
        checkInitializer.accept(referencesCollectionBackReferenceDescriptionChecks.computeIfAbsent(propertyName, key -> new ReferencesCollectionBackReferenceDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    /**
     * Set up verification of grouping description
     *
     * @param propertyName     Property name
     * @param checkInitializer The initializer of the check
     * @return Current check
     */
    public EntityDescriptionCheck setGroupDescriptionCheck(String propertyName, Consumer<GroupDescriptionCheck> checkInitializer) {
        checkInitializer.accept(groupDescriptionChecks.computeIfAbsent(propertyName, key -> new GroupDescriptionCheck(entityDescription, propertyName)));
        return this;
    }

    @Override
    void check() {
        assertNotNull(entityDescription);
        assertEquals(entityDescription, modelDescription.getEntityDescriptions().get(name));

        assertEquals(modelDescription, entityDescription.getModelDescription());
        assertEquals(parentEntityTypes.isEmpty() ? null : modelDescription.getEntityDescription(parentEntityTypes.get(0)), entityDescription.getParentEntityDescription());
        parentEntityTypes.forEach(parentEntityType -> assertTrue(modelDescription.getEntityDescription(parentEntityType).getChildEntityDescriptions().contains(entityDescription)));
        assertEquals(parentEntityTypes.isEmpty() ? entityDescription : modelDescription.getEntityDescription(parentEntityTypes.get(parentEntityTypes.size() - 1)), entityDescription.getRootEntityDescription());
        assertEquals(aggregateEntityType == null ? null : modelDescription.getEntityDescription(aggregateEntityType), entityDescription.getAggregateEntityDescription());
        assertEquals(name, entityDescription.getName());
        assertEquals(final0, entityDescription.isFinal());
        assertEquals(aggregate, entityDescription.isAggregate());
        assertEquals(inheritanceStrategy, entityDescription.getInheritanceStrategy());
        assertEquals(tableType, entityDescription.getTableType());
        assertEquals(tableName, entityDescription.getTableName());
        assertEquals(idColumnName, entityDescription.getIdColumnName());
        assertEquals(typeColumnName, entityDescription.getTypeColumnName());
        assertEquals(aggregateColumnName, entityDescription.getAggregateColumnName());
        assertEquals(systemLocksTableName, entityDescription.getSystemLocksTableName());
        assertEquals(systemLocksAggregateColumnName, entityDescription.getSystemLocksAggregateColumnName());
        assertEquals(systemLocksVersionColumnName, entityDescription.getSystemLocksVersionColumnName());
        Helper.check(entityDescription.getParamDescriptions(), paramDescriptionChecks);
        Helper.check(entityDescription.getDeclaredPrimitiveDescriptions(), primitiveDescriptionChecks);
        Helper.check(entityDescription.getDeclaredPrimitivesCollectionDescriptions(), primitivesCollectionDescriptionChecks);
        Helper.check(entityDescription.getDeclaredReferenceDescriptions(), referenceDescriptionChecks);
        Helper.check(entityDescription.getDeclaredReferenceBackReferenceDescriptions(), referenceBackReferenceDescriptionChecks);
        Helper.check(entityDescription.getDeclaredReferencesCollectionDescriptions(), referencesCollectionDescriptionChecks);
        Helper.check(entityDescription.getDeclaredReferencesCollectionBackReferenceDescriptions(), referencesCollectionBackReferenceDescriptionChecks);
        Helper.check(entityDescription.getDeclaredGroupDescriptions(), groupDescriptionChecks);
        checkInheritance();
    }
}
