package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.modeldescription.CheckEntityDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.CheckPropertyDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescription;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;

/**
 * Checking model description for string expressions
 */
public class StringExpressionsModelDescriptionCheck implements ModelDescriptionCheck {

    static final String NAME_REGEXP = "[A-Za-z0-9_]+";

    /**
     * Check the name
     *
     * @param name Name
     */
    void checkName(String name) {
        if (!name.matches(NAME_REGEXP)) {
            throw new NameDoesNotMatchRegExpException(name, NAME_REGEXP);
        }
    }

    /**
     * Check the name
     *
     * @param propertyDescription Property description
     */
    void checkName(PropertyDescription propertyDescription) {
        checkName(propertyDescription.getName());
    }

    @Override
    public String getDescription() {
        return "Checking model description for string expressions";
    }

    @Override
    public void check(ModelDescription modelDescription) {
        forEach(modelDescription.getEntityDescriptions().values().stream(), entityDescription -> {
            checkName(entityDescription.getName());
            forEach(entityDescription.getDeclaredPrimitiveDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
            forEach(entityDescription.getDeclaredPrimitivesCollectionDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
            forEach(entityDescription.getDeclaredReferenceDescriptions().values().stream(), referenceDescription -> {
                checkName(referenceDescription);
                if (referenceDescription.getEntityReferencePropertyName() != null) {
                    checkName(referenceDescription.getEntityReferencePropertyName());
                }
                if (referenceDescription.getEntityReferencesCollectionPropertyName() != null) {
                    checkName(referenceDescription.getEntityReferencesCollectionPropertyName());
                }
            }, CheckPropertyDescriptionException::new);
            forEach(entityDescription.getDeclaredReferencesCollectionDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
            forEach(entityDescription.getDeclaredGroupDescriptions().values().stream(), groupDescription -> {
                checkName(groupDescription);
                forEach(groupDescription.getPrimitiveDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
                forEach(groupDescription.getReferenceDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
            }, CheckPropertyDescriptionException::new);
        }, CheckEntityDescriptionException::new);
    }
}
