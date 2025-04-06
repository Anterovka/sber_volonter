package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking the link description
 */
public class ReferenceDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    ReferenceDescription referenceDescription;
    String name;
    String columnName;
    String entityType;
    boolean mandatory;
    String entityReferencePropertyName;
    String entityReferencesCollectionPropertyName;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    ReferenceDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        referenceDescription = entityDescription.getDeclaredReferenceDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set the column name
     *
     * @return Current check
     */
    public ReferenceDescriptionCheck setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Set entity type
     *
     * @return Current check
     */
    public ReferenceDescriptionCheck setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    /**
     * Set the obligation mark
     *
     * @return Current check
     */
    public ReferenceDescriptionCheck setMandatory() {
        this.mandatory = true;
        return this;
    }

    /**
     * Set the name of the entity-reference property
     *
     * @return Current check
     */
    public ReferenceDescriptionCheck setEntityReferencePropertyName(String entityReferencePropertyName) {
        this.entityReferencePropertyName = entityReferencePropertyName;
        return this;
    }

    /**
     * Set the name of the property collection referring to entities
     *
     * @return Current check
     */
    public ReferenceDescriptionCheck setEntityReferencesCollectionPropertyName(String entityReferencesCollectionPropertyName) {
        this.entityReferencesCollectionPropertyName = entityReferencesCollectionPropertyName;
        return this;
    }

    @Override
    void check() {
        assertNotNull(referenceDescription);

        assertEquals(entityDescription.getReferenceDescription(name), referenceDescription);
        assertEquals(entityDescription.getReferenceDescriptions().get(name), referenceDescription);

        assertEquals(entityDescription, referenceDescription.getOwnerEntityDescription());
        assertEquals(name, referenceDescription.getName());
        assertEquals(columnName, referenceDescription.getColumnName());
        assertEquals(entityDescription.getModelDescription().getEntityDescription(entityType), referenceDescription.getEntityDescription());
        assertEquals(mandatory, referenceDescription.isMandatory());
        assertEquals(entityReferencePropertyName, referenceDescription.getEntityReferencePropertyName());
        assertEquals(entityReferencesCollectionPropertyName, referenceDescription.getEntityReferencesCollectionPropertyName());
    }
}
