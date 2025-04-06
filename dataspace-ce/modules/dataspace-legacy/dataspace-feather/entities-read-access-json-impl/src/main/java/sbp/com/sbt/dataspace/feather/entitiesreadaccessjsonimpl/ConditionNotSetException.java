package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * Condition is not set
 */
public class ConditionNotSetException extends FeatherException {

    ConditionNotSetException() {
        super("Condition is not set");
    }
}
