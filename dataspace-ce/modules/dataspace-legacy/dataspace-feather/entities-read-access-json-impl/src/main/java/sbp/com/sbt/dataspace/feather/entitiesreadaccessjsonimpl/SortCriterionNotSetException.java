package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

/**
 * No sorting criterion is set
 */
public class SortCriterionNotSetException extends FeatherException {

    SortCriterionNotSetException() {
        super("The sorting criterion is not set");
    }
}
