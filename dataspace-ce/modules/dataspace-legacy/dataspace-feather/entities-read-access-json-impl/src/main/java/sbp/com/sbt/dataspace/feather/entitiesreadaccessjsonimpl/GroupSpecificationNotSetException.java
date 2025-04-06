package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * The grouping specification is not set
 */
public class GroupSpecificationNotSetException extends FeatherException {

    GroupSpecificationNotSetException() {
        super("The grouping specification is not set");
    }
}
