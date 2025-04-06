package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

/**
 * Settings for link description
 */
public final class ReferenceDescriptionSettings extends AbstractPropertyDescriptionWithColumnNameSettings<ReferenceDescriptionSettings> {

    String entityType;
    boolean mandatory;
    String entityReferencePropertyName;
    String entityReferencesCollectionPropertyName;

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
    public ReferenceDescriptionSettings setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    /**
     * Get the name of the entity-reference property
     */
    public String getEntityReferencePropertyName() {
        return entityReferencePropertyName;
    }

    /**
     * Set the name of the entity-reference property
     *
     * @return Current settings
     */
    public ReferenceDescriptionSettings setEntityReferencePropertyName(String entityReferencePropertyName) {
        this.entityReferencePropertyName = entityReferencePropertyName;
        return this;
    }

    /**
     * Is it mandatory
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Set the mandatory flag
     *
     * @return Current settings
     */
    public ReferenceDescriptionSettings setMandatory() {
        this.mandatory = true;
        return this;
    }

    /**
     * Get the name of the collection property reference to entities
     */
    public String getEntityReferencesCollectionPropertyName() {
        return entityReferencesCollectionPropertyName;
    }

    /**
     * Set the name of the property collection on the entity
     *
     * @return Current settings
     */
    public ReferenceDescriptionSettings setEntityReferencesCollectionPropertyName(String entityReferencesCollectionPropertyName) {
        this.entityReferencesCollectionPropertyName = entityReferencesCollectionPropertyName;
        return this;
    }
}
