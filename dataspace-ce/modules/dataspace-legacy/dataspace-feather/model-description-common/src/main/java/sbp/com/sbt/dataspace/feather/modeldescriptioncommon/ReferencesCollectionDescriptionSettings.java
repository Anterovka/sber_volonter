package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

/**
 * Settings for the description of the link collection
 */
public final class ReferencesCollectionDescriptionSettings extends AbstractCollectionDescriptionSettings<ReferencesCollectionDescriptionSettings> {

    String entityType;

    /**
     * Get entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Set entity type
     *
     * @return Current settings
     */
    public ReferencesCollectionDescriptionSettings setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }
}
