package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;

/**
 * Test verification of model description
 */
class TestModelDescriptionCheck implements ModelDescriptionCheck {

    @Override
    public String getDescription() {
        return "Test verification of model description";
    }

    @Override
    public void check(ModelDescription modelDescription) {
        // No action is required
    }
}
