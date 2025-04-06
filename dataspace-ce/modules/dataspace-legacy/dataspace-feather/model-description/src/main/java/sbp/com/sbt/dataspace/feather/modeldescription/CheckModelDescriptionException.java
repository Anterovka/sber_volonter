package sbp.com.sbt.dataspace.feather.modeldescription;

import sbp.com.sbt.dataspace.feather.common.FeatherException;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Error during model description verification
 */
public final class CheckModelDescriptionException extends FeatherException {

    /**
     * @param throwable             Exception
     * @param modelDescriptionCheck Model description check
     */
    public CheckModelDescriptionException(Throwable throwable, ModelDescriptionCheck modelDescriptionCheck) {
        super(throwable, "Error during validation of model description", param("Model Description Check", modelDescriptionCheck.getDescription()));
    }
}
