package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking the description of the grouping link
 */
public class GroupReferenceDescriptionCheck extends AbstractCheck {

    GroupDescription groupDescription;
    ReferenceDescription referenceDescription;
    String name;
    String columnName;
    String entityType;
    boolean mandatory;
    String entityReferencePropertyName;
    String entityReferencesCollectionPropertyName;

    /**
     * @param groupDescription Description of the grouping
     * @param name                Name
     */
    GroupReferenceDescriptionCheck(GroupDescription groupDescription, String name) {
        this.groupDescription = groupDescription;
        referenceDescription = groupDescription.getReferenceDescription(name);
        this.name = name;
    }

    /**
     * Set the column name
     *
     * @return Current check
     */
    public GroupReferenceDescriptionCheck setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Set entity type
     *
     * @return Current check
     */
    public GroupReferenceDescriptionCheck setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    /**
     * Set the obligation mark
     *
     * @return Current check
     */
    public GroupReferenceDescriptionCheck setMandatory() {
        this.mandatory = true;
        return this;
    }

    /**
     * Set the name of the entity-reference property
     *
     * @return Current check
     */
    public GroupReferenceDescriptionCheck setEntityReferencePropertyName(String entityReferencePropertyName) {
        this.entityReferencePropertyName = entityReferencePropertyName;
        return this;
    }

    /**
     * Set the name of the collection property referring to entities
     *
     * @return Current check
     */
    public GroupReferenceDescriptionCheck setEntityReferencesCollectionPropertyName(String entityReferencesCollectionPropertyName) {
        this.entityReferencesCollectionPropertyName = entityReferencesCollectionPropertyName;
        return this;
    }

    @Override
    void check() {
        assertNotNull(referenceDescription);

        assertEquals(groupDescription.getReferenceDescriptions().get(name), referenceDescription);

        assertEquals(groupDescription.getOwnerEntityDescription(), referenceDescription.getOwnerEntityDescription());
        assertEquals(name, referenceDescription.getName());
        assertEquals(columnName, referenceDescription.getColumnName());
        assertEquals(groupDescription.getOwnerEntityDescription().getModelDescription().getEntityDescription(entityType), referenceDescription.getEntityDescription());
        assertEquals(mandatory, referenceDescription.isMandatory());
        assertEquals(entityReferencePropertyName, referenceDescription.getEntityReferencePropertyName());
        assertEquals(entityReferencesCollectionPropertyName, referenceDescription.getEntityReferencesCollectionPropertyName());
    }
}
