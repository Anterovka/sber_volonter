package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Entity type is not set
 */
public class EntityTypeNotSetException extends FeatherException {

    EntityTypeNotSetException() {
        super("Entity type is not set", "Field 'type'");
    }
}
