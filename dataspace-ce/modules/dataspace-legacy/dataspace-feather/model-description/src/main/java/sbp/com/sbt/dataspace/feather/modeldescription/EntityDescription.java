package sbp.com.sbt.dataspace.feather.modeldescription;

import java.util.Collection;
import java.util.Map;

/**
 * Entity description
 */
public interface EntityDescription extends ObjectWithMetaDataManager {

    /**
     * Get model description
     */
    // NotNull
    ModelDescription getModelDescription();

    /**
     * Get parent entity description
     */
    EntityDescription getParentEntityDescription();

    /**
     * Get child entities descriptions
     */
    Collection<EntityDescription> getChildEntityDescriptions();

    /**
     * Get root entity description
     */
    // NotNull
    EntityDescription getRootEntityDescription();

    /**
     * Get aggregate entity description
     */
    EntityDescription getAggregateEntityDescription();

    /**
     * Get the name
     */
    // NotNull
    String getName();

    /**
     * Is it final
     */
    boolean isFinal();

    /**
     * Is it an aggregate?
     */
    boolean isAggregate();

    /**
     * Get descriptions of primitives (declared in this entity description)
     */
    // NotNull
    Map<String, PrimitiveDescription> getDeclaredPrimitiveDescriptions();

    /**
     * Get descriptions of primitive collections (declared in this entity description)
     */
    // NotNull
    Map<String, PrimitivesCollectionDescription> getDeclaredPrimitivesCollectionDescriptions();

    /**
     * Get link descriptions (declared in this entity description)
     */
    // NotNull
    Map<String, ReferenceDescription> getDeclaredReferenceDescriptions();

    /**
     * Get backlink descriptions for links (declared in this entity description)
     */
    // NotNull
    Map<String, ReferenceDescription> getDeclaredReferenceBackReferenceDescriptions();

    /**
     * Obtain descriptions of link collections (declared in this entity description)
     */
    // NotNull
    Map<String, ReferencesCollectionDescription> getDeclaredReferencesCollectionDescriptions();

    /**
     * Get backlink descriptions for link collections (declared in this entity description)
     */
    // NotNull
    Map<String, ReferenceDescription> getDeclaredReferencesCollectionBackReferenceDescriptions();

    /**
     * Get descriptions of groupings (declared in this entity description)
     */
    // NotNull
    Map<String, GroupDescription> getDeclaredGroupDescriptions();

    /**
     * Get descriptions of primitives
     */
    // NotNull
    Map<String, PrimitiveDescription> getPrimitiveDescriptions();

    /**
     * Get descriptions of primitive collections
     */
    // NotNull
    Map<String, PrimitivesCollectionDescription> getPrimitivesCollectionDescriptions();

    /**
     * Get link descriptions
     */
    // NotNull
    Map<String, ReferenceDescription> getReferenceDescriptions();

    /**
     * Get descriptions of backlinks for links
     */
    // NotNull
    Map<String, ReferenceDescription> getReferenceBackReferenceDescriptions();

    /**
     * Get descriptions of link collections
     */
    // NotNull
    Map<String, ReferencesCollectionDescription> getReferencesCollectionDescriptions();

    /**
     * Get descriptions of backlinks for link collections
     */
    // NotNull
    Map<String, ReferenceDescription> getReferencesCollectionBackReferenceDescriptions();

    /**
     * Get descriptions of groupings
     */
    // NotNull
    Map<String, GroupDescription> getGroupDescriptions();

    /**
     * Get inheritance strategy
     */
    InheritanceStrategy getInheritanceStrategy();

    /**
     * Get table type
     */
    // NotNull
    TableType getTableType();

    /**
     * Get table name
     */
    String getTableName();

    /**
     * Get the column name with id
     */
    String getIdColumnName();

    /**
     * Get the column name with the type
     */
    String getTypeColumnName();

    /**
     * Get the column name with an aggregate
     */
    String getAggregateColumnName();

    /**
     * Get the name of the system lock table
     */
    String getSystemLocksTableName();

    /**
     * Get the column name of the system lock table with the aggregate
     */
    String getSystemLocksAggregateColumnName();

    /**
     * Get the column name of the system lock table with version
     */
    String getSystemLocksVersionColumnName();

    /**
     * Get parameter descriptions
     */
    // NotNull
    Map<String, ParamDescription> getParamDescriptions();

    /**
     * Is it an entity extension?
     *
     * @param entityType Entity type
     */
    boolean isExtensionOf(String entityType);

    /**
     * Convert to type
     *
     * @param entityType Entity type
     * @return Description of the entity after normalization
     */
    // NotNull
    default EntityDescription cast(String entityType) {
        if (entityType == null) {
            return this;
        }
        EntityDescription result = getModelDescription().getEntityDescription(entityType);
        if (!result.isExtensionOf(getName())) {
            throw new CastEntityTypeException(getName(), entityType);
        }
        return result;
    }

    /**
     * Get primitive description
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitiveDescription getPrimitiveDescription(String propertyName);

    /**
     * Get description of primitive collection
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitivesCollectionDescription getPrimitivesCollectionDescription(String propertyName);

    /**
     * Get link description
     *
     * @param propertyName Property name
     */
    // NotNull
    ReferenceDescription getReferenceDescription(String propertyName);

    /**
     * Get description of backlink for link
     *
     * @param propertyName Property name
     */
    // NotNull
    ReferenceDescription getReferenceBackReferenceDescription(String propertyName);

    /**
     * Get description of link collection
     *
     * @param propertyName Property name
     */
    // NotNull
    ReferencesCollectionDescription getReferencesCollectionDescription(String propertyName);

    /**
     * Get description of backlink for link collection
     */
    // NotNull
    ReferenceDescription getReferencesCollectionBackReferenceDescription(String propertyName);

    /**
     * Get description of grouping
     */
    // NotNull
    GroupDescription getGroupDescription(String propertyName);
}
