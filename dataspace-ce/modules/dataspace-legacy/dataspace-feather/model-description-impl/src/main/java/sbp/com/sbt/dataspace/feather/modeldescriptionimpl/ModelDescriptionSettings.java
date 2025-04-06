package sbp.com.sbt.dataspace.feather.modeldescriptionimpl;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Model description settings
 */
public final class ModelDescriptionSettings {

    String modelResourceName;

    /**
     * Get the resource name with the model
     */
    public String getModelResourceName() {
        return modelResourceName;
    }

    /**
     * Set the name of the resource with the model
     *
     * @return Current settings
     */
    public ModelDescriptionSettings setModelResourceName(String modelResourceName) {
        this.modelResourceName = modelResourceName;
        return this;
    }

    @Override
    public String toString() {
        return getFullDescription("Model Description Settings",
            param("Resource name", modelResourceName));
    }
}
