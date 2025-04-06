package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during entity description verification
 */
public final class CheckEntityDescriptionException extends FeatherException {

    /**
     * @param throwable         Exception
     * @param entityDescription Entity description
     */
    public CheckEntityDescriptionException(Throwable throwable, EntityDescription entityDescription) {
        super(throwable, "Error during validation of entity description", param("Entity Type", entityDescription.getName()));
    }
}
