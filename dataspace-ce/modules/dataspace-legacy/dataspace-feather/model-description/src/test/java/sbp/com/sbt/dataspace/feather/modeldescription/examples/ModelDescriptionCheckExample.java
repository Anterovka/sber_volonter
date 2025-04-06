package sbp.com.sbt.dataspace.feather.modeldescription.examples;

import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Example of checking the model description
 */
public class ModelDescriptionCheckExample implements ModelDescriptionCheck {

    @Override
    public String getDescription() {
        return "Example of model description verification: All entity descriptions have table names assigned.";
    }

    @Override
    public void check(ModelDescription modelDescription) {
        modelDescription.getEntityDescriptions().values()
            .forEach(entityDescription -> checkNotNull(entityDescription.getTableName(), "Table name"));
    }
}
