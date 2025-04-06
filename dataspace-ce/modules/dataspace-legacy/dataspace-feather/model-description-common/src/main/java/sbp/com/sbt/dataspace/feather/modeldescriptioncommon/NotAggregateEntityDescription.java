package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The entity description is not an aggregate
 */
public class NotAggregateEntityDescription extends FeatherException {

    /**
     * @param entityType Entity type
     */
    NotAggregateEntityDescription(String entityType) {
        super("Description of the entity is not an aggregate", param("Entity type", entityType));
    }
}
