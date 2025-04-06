package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking the description of the backlink for the link collection
 */
public class ReferencesCollectionBackReferenceDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    ReferenceDescription backReferenceDescription;
    String name;
    String backReferenceOwnerEntityType;
    String backReferencePropertyName;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    ReferencesCollectionBackReferenceDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        backReferenceDescription = entityDescription.getDeclaredReferencesCollectionBackReferenceDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set the type of the owning entity for the backlink
     *
     * @return Current check
     */
    public ReferencesCollectionBackReferenceDescriptionCheck setBackReferenceOwnerEntityType(String backReferenceOwnerEntityType) {
        this.backReferenceOwnerEntityType = backReferenceOwnerEntityType;
        return this;
    }

    /**
     * Specify the backlink name
     *
     * @return Current check
     */
    public ReferencesCollectionBackReferenceDescriptionCheck setBackReferencePropertyName(String backReferencePropertyName) {
        this.backReferencePropertyName = backReferencePropertyName;
        return this;
    }

    @Override
    void check() {
        assertNotNull(backReferenceDescription);

        assertEquals(entityDescription.getReferencesCollectionBackReferenceDescription(name), backReferenceDescription);
        assertEquals(entityDescription.getReferencesCollectionBackReferenceDescriptions().get(name), backReferenceDescription);

        assertEquals(entityDescription.getModelDescription().getEntityDescription(backReferenceOwnerEntityType).getDeclaredReferenceDescriptions().get(backReferencePropertyName), backReferenceDescription);
    }
}
