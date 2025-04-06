package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during entity description processing
 */
public class ProcessEntityDescriptionException extends FeatherException {

    /**
     * @param throwable  Exception
     * @param entityType Entity type
     */
    ProcessEntityDescriptionException(Throwable throwable, String entityType) {
        super(throwable, "Error processing entity description", param("Entity Type", entityType));
    }
}
