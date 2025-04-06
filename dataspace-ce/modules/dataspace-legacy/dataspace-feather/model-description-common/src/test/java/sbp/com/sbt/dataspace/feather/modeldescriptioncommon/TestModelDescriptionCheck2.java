package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.CheckEntityDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;

/**
 * Test verification of model description (2)
 */
class TestModelDescriptionCheck2 implements ModelDescriptionCheck {

    @Override
    public String getDescription() {
        return "Test verification of model description (2)";
    }

    @Override
    public void check(ModelDescription modelDescription) {
        forEach(modelDescription.getEntityDescriptions().values().stream(), entityDescription -> checkNotNull(entityDescription.getTableName(), "Table name"), CheckEntityDescriptionException::new);
    }
}
