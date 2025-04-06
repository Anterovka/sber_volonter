package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking the description of the backlink for the link
 */
public class ReferenceBackReferenceDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    ReferenceDescription backReferenceDescription;
    String name;
    String backReferenceOwnerEntityType;
    String backReferencePropertyName;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    ReferenceBackReferenceDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        backReferenceDescription = entityDescription.getDeclaredReferenceBackReferenceDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set the type of the owning entity for the backlink
     *
     * @return Current check
     */
    public ReferenceBackReferenceDescriptionCheck setBackReferenceOwnerEntityType(String backReferenceOwnerEntityType) {
        this.backReferenceOwnerEntityType = backReferenceOwnerEntityType;
        return this;
    }

    /**
     * Specify the backlink name
     *
     * @return Current check
     */
    public ReferenceBackReferenceDescriptionCheck setBackReferencePropertyName(String backReferencePropertyName) {
        this.backReferencePropertyName = backReferencePropertyName;
        return this;
    }

    @Override
    void check() {
        assertNotNull(backReferenceDescription);

        assertEquals(entityDescription.getReferenceBackReferenceDescription(name), backReferenceDescription);
        assertEquals(entityDescription.getReferenceBackReferenceDescriptions().get(name), backReferenceDescription);

        assertEquals(entityDescription.getModelDescription().getEntityDescription(backReferenceOwnerEntityType).getDeclaredReferenceDescriptions().get(backReferencePropertyName), backReferenceDescription);
    }
}
