package sbp.com.sbt.dataspace.feather.modeldescriptionimpl2;

import com.sbt.dataspace.pdm.PdmModel;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Model description settings
 */
public final class ModelDescriptionSettings {

    PdmModel pdmModel;

    /**
     * Get PDM file model
     */
    public PdmModel getPdmModel() {
        return pdmModel;
    }

    /**
     * Set pdm file model
     *
     * @return Current settings
     */
    public ModelDescriptionSettings setPdmModel(PdmModel pdmModel) {
        this.pdmModel = pdmModel;
        return this;
    }

    @Override
    public String toString() {
        return getFullDescription("Model description settings",
            param("PDM file model", pdmModel));
    }
}
