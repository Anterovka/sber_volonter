package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;

import java.util.LinkedHashMap;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.putPropertyDescriptionSettings;

/**
 * Entity Description Settings
 */
public final class EntityDescriptionSettings {

    String parentEntityType;
    String aggregateEntityType;
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
    Map<String, ParamDescriptionSettings> paramDescriptionsSettings = new LinkedHashMap<>();
    Map<String, PrimitiveDescriptionSettings> primitiveDescriptionsSettings = new LinkedHashMap<>();
    Map<String, PrimitivesCollectionDescriptionSettings> primitivesCollectionDescriptionsSettings = new LinkedHashMap<>();
    Map<String, ReferenceDescriptionSettings> referenceDescriptionsSettings = new LinkedHashMap<>();
    Map<String, ReferencesCollectionDescriptionSettings> referencesCollectionDescriptionsSettings = new LinkedHashMap<>();
    Map<String, GroupDescriptionSettings> groupDescriptionsSettings = new LinkedHashMap<>();

    /**
     * Get the type of parent entity
     */
    public String getParentEntityType() {
        return parentEntityType;
    }

    /**
     * Set the type of parent entity
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setParentEntityType(String parentEntityType) {
        this.parentEntityType = parentEntityType;
        return this;
    }

    /**
     * Get aggregate entity type
     */
    public String getAggregateEntityType() {
        return aggregateEntityType;
    }

    /**
     * Set the aggregate entity type
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setAggregateEntityType(String aggregateEntityType) {
        this.aggregateEntityType = aggregateEntityType;
        return this;
    }

    /**
     * Is it final
     */
    public boolean isFinal() {
        return final0;
    }

    /**
     * Set the flag for the final entity description
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setFinal() {
        this.final0 = true;
        return this;
    }

    /**
     * Is it an aggregate?
     */
    public boolean isAggregate() {
        return aggregate;
    }

    /**
     * Set the aggregate flag
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setAggregate() {
        this.aggregate = true;
        return this;
    }

    /**
     * Get inheritance strategy
     */
    public InheritanceStrategy getInheritanceStrategy() {
        return inheritanceStrategy;
    }

    /**
     * Set inheritance strategy
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setInheritanceStrategy(InheritanceStrategy inheritanceStrategy) {
        this.inheritanceStrategy = inheritanceStrategy;
        return this;
    }

    /**
     * Get table type
     */
    public TableType getTableType() {
        return tableType;
    }

    /**
     * Set table type
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setTableType(TableType tableType) {
        this.tableType = tableType;
        return this;
    }

    /**
     * Get table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Set table name
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Get the column name with id
     */
    public String getIdColumnName() {
        return idColumnName;
    }

    /**
     * Set the column name with id
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
        return this;
    }

    /**
     * Get the column name with the type
     */
    public String getTypeColumnName() {
        return typeColumnName;
    }

    /**
     * Set the column name with type
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setTypeColumnName(String typeColumnName) {
        this.typeColumnName = typeColumnName;
        return this;
    }

    /**
     * Get the column name with an aggregate
     */
    public String getAggregateColumnName() {
        return aggregateColumnName;
    }

    /**
     * Set the column name with an aggregate
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setAggregateColumnName(String aggregateColumnName) {
        this.aggregateColumnName = aggregateColumnName;
        return this;
    }

    /**
     * Get the name of the system lock table
     */
    public String getSystemLocksTableName() {
        return systemLocksTableName;
    }

    /**
     * Specify the name of the system lock table
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setSystemLocksTableName(String systemLocksTableName) {
        this.systemLocksTableName = systemLocksTableName;
        return this;
    }

    /**
     * Get the column name of the system lock table with the aggregate
     */
    public String getSystemLocksAggregateColumnName() {
        return systemLocksAggregateColumnName;
    }

    /**
     * Specify the name of the column in the system lock table with an aggregate
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setSystemLocksAggregateColumnName(String systemLocksAggregateColumnName) {
        this.systemLocksAggregateColumnName = systemLocksAggregateColumnName;
        return this;
    }

    /**
     * Get the name of the column in the system lock table with the version
     */
    public String getSystemLocksVersionColumnName() {
        return systemLocksVersionColumnName;
    }

    /**
     * Set the name of the column in the system lock table with the version
     *
     * @return Current settings
     */
    public EntityDescriptionSettings setSystemLocksVersionColumnName(String systemLocksVersionColumnName) {
        this.systemLocksVersionColumnName = systemLocksVersionColumnName;
        return this;
    }

    /**
     * Get parameter description settings
     */
    public ParamDescriptionSettings getParamDescriptionSettings(String paramName) {
        return paramDescriptionsSettings.get(paramName);
    }

    /**
     * Set description parameter settings
     *
     * @param paramName Parameter name=
     * @return Current settings
     */
    public EntityDescriptionSettings setParamDescriptionSettings(String paramName, ParamDescriptionSettings paramDescriptionSettings) {
        checkNotNull(paramName, "Parameter name");
        checkNotNull(paramDescriptionSettings, "Parameter description settings");
        if (paramDescriptionsSettings.containsKey(paramName)) {
            throw new DuplicateParamNamesFoundException(paramName);
        }
        paramDescriptionsSettings.put(paramName, paramDescriptionSettings);
        return this;
    }

    /**
     * Get settings for primitive description
     */
    public PrimitiveDescriptionSettings getPrimitiveDescriptionSettings(String propertyName) {
        return primitiveDescriptionsSettings.get(propertyName);
    }

    /**
     * Set primitive description settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public EntityDescriptionSettings setPrimitiveDescriptionSettings(String propertyName, PrimitiveDescriptionSettings primitiveDescriptionSettings) {
        putPropertyDescriptionSettings(primitiveDescriptionsSettings, propertyName, primitiveDescriptionSettings);
        return this;
    }

    /**
     * Get collection primitives description settings
     */
    public PrimitivesCollectionDescriptionSettings getPrimitivesCollectionDescriptionSettings(String propertyName) {
        return primitivesCollectionDescriptionsSettings.get(propertyName);
    }

    /**
     * Set primitive collection description settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public EntityDescriptionSettings setPrimitivesCollectionDescriptionSettings(String propertyName, PrimitivesCollectionDescriptionSettings primitivesCollectionDescriptionSettings) {
        putPropertyDescriptionSettings(primitivesCollectionDescriptionsSettings, propertyName, primitivesCollectionDescriptionSettings);
        return this;
    }

    /**
     * Get link description settings
     */
    public ReferenceDescriptionSettings getReferenceDescriptionSettings(String propertyName) {
        return referenceDescriptionsSettings.get(propertyName);
    }

    /**
     * Set link description settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public EntityDescriptionSettings setReferenceDescriptionSettings(String propertyName, ReferenceDescriptionSettings referenceDescriptionSettings) {
        putPropertyDescriptionSettings(referenceDescriptionsSettings, propertyName, referenceDescriptionSettings);
        return this;
    }

    /**
     * Get collection link description settings
     */
    public ReferencesCollectionDescriptionSettings getReferencesCollectionDescriptionSettings(String propertyName) {
        return referencesCollectionDescriptionsSettings.get(propertyName);
    }

    /**
     * Set description collection settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public EntityDescriptionSettings setReferencesCollectionDescriptionSettings(String propertyName, ReferencesCollectionDescriptionSettings referencesCollectionDescriptionSettings) {
        putPropertyDescriptionSettings(referencesCollectionDescriptionsSettings, propertyName, referencesCollectionDescriptionSettings);
        return this;
    }

    /**
     * Get grouping description settings
     */
    public GroupDescriptionSettings getGroupDescriptionSettings(String propertyName) {
        return groupDescriptionsSettings.get(propertyName);
    }

    /**
     * Set the description collection settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public EntityDescriptionSettings setGroupDescriptionSettings(String propertyName, GroupDescriptionSettings groupDescriptionSettings) {
        putPropertyDescriptionSettings(groupDescriptionsSettings, propertyName, groupDescriptionSettings);
        return this;
    }
}
