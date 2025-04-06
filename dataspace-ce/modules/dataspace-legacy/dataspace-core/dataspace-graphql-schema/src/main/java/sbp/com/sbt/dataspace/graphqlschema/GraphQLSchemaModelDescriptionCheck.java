package sbp.com.sbt.dataspace.graphqlschema;

import sbp.com.sbt.dataspace.feather.modeldescription.CheckEntityDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.CheckEnumDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.CheckEnumValueDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.CheckPropertyDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescription;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;

/**
 * Checking the model description for the GraphQL schema
 */
public class GraphQLSchemaModelDescriptionCheck implements ModelDescriptionCheck {

    static final String NAME_REGEXP = "[A-Za-z0-9][A-Za-z0-9_]*";

    boolean idWithUnderscore;
    boolean aggregateVersionWithUnderscore;

    /**
     * @param idWithUnderscore                Use underscore for id
     * @param aggregateVersionWithUnderscore Use a lowercase underscore for the aggregate version
     */
    public GraphQLSchemaModelDescriptionCheck(boolean idWithUnderscore, boolean aggregateVersionWithUnderscore) {
        this.idWithUnderscore = idWithUnderscore;
        this.aggregateVersionWithUnderscore = aggregateVersionWithUnderscore;
    }

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
        if ((!idWithUnderscore && GraphQLSchemaHelper.ID_FIELD_NAME.equals(propertyDescription.getName()))
                || (!aggregateVersionWithUnderscore && GraphQLSchemaHelper.AGGREGATE_VERSION_FIELD_NAME.equals(propertyDescription.getName()))) {
            throw new FieldNameReservedException(propertyDescription.getName());
        }
    }

    @Override
    public String getDescription() {
        return "Checking the model description for the GraphQL schema";
    }

    @Override
    public void check(ModelDescription modelDescription) {
        forEach(modelDescription.getEnumDescriptions().values().stream(), enumDescription -> {
            checkName(enumDescription.getName());
            forEach(enumDescription.getValues().stream(), this::checkName, CheckEnumValueDescriptionException::new);
        }, CheckEnumDescriptionException::new);
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
                checkName(groupDescription.getGroupName());
                forEach(groupDescription.getPrimitiveDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
                forEach(groupDescription.getReferenceDescriptions().values().stream(), this::checkName, CheckPropertyDescriptionException::new);
            }, CheckPropertyDescriptionException::new);
        }, CheckEntityDescriptionException::new);

        modelDescription.getGroupDescriptions().values().stream()
                .map(groupDescriptions -> groupDescriptions.get(0))
                .forEach(groupDescription -> {
                    if (groupDescription.getPrimitiveDescriptions().isEmpty() && groupDescription.getReferenceDescriptions().isEmpty()) {
                        throw new GroupPropertiesNotFoundException(groupDescription.getGroupName());
                    }
                });
    }
}
