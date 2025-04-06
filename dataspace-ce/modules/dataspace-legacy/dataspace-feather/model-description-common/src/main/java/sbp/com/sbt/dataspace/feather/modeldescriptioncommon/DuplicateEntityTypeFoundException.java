package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Duplicate entity type detected
 */
public class DuplicateEntityTypeFoundException extends FeatherException {

    /**
     * @param entityType Entity type
     */
    DuplicateEntityTypeFoundException(String entityType) {
        super("Entity type duplicate detected", param("Entity type", entityType));
    }
}
