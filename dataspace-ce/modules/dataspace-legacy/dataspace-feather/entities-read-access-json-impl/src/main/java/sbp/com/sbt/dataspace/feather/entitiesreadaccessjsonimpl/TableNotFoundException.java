package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * The table for entity description was not found.
 */
public class TableNotFoundException extends FeatherException {

    /**
     * @param entityType Entity type
     */
    TableNotFoundException(String entityType) {
        super("The table for describing the entity was not found", param("Entity type", entityType));
    }
}
