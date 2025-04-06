package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * No entity description found
 */
public class EntityDescriptionNotFoundException extends FeatherException {

    /**
     * @param entityType Entity type
     */
    EntityDescriptionNotFoundException(String entityType) {
        super("Description of the entity was not found", param("Entity type", entityType));
    }
}
