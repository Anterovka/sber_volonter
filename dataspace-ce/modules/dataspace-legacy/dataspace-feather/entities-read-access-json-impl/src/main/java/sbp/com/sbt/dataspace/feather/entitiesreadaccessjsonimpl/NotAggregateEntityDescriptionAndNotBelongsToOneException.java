package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The entity description is not an aggregate and does not belong to one
 */
public class NotAggregateEntityDescriptionAndNotBelongsToOneException extends FeatherException {

    /**
     * @param entityType Entity type
     */
    NotAggregateEntityDescriptionAndNotBelongsToOneException(String entityType) {
        super("Description of the entity is not an aggregate and does not belong to it", param("Entity type", entityType));
    }
}
