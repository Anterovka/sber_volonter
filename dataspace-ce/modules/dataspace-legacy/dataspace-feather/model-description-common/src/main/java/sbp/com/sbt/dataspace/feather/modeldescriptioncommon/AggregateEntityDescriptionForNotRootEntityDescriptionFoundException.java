package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Entity aggregation description is found for non-root entity description
 */
public class AggregateEntityDescriptionForNotRootEntityDescriptionFoundException extends FeatherException {

    /**
     * @param aggregateEntityType The type of the aggregate entity
     */
    AggregateEntityDescriptionForNotRootEntityDescriptionFoundException(String aggregateEntityType) {
        super("Description of the entity's aggregate for describing non-root entity was found", "Only root entity can have an entity description of aggregate", param("Entity Description of Aggregate", aggregateEntityType));
    }
}
