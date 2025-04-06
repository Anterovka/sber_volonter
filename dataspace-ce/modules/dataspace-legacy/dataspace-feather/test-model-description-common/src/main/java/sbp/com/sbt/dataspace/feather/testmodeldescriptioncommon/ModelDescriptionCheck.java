package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checking model description
 */
public final class ModelDescriptionCheck extends AbstractCheck {

    ModelDescription modelDescription;
    Map<String, EnumDescriptionCheck> enumDescriptionChecks = new LinkedHashMap<>();
    Map<String, EntityDescriptionCheck> entityDescriptionChecks = new LinkedHashMap<>();

    /**
     * @param modelDescription Model description
     */
    public ModelDescriptionCheck(ModelDescription modelDescription) {
        this.modelDescription = modelDescription;
    }

    /**
     * Set description checking for enumeration
     *
     * @param enumType         Enumeration type
     * @param checkInitializer The check initializer
     * @return Current check
     */
    public ModelDescriptionCheck setEnumDescriptionCheck(String enumType, Consumer<EnumDescriptionCheck> checkInitializer) {
        checkInitializer.accept(enumDescriptionChecks.computeIfAbsent(enumType, key -> new EnumDescriptionCheck(modelDescription, key)));
        return this;
    }

    /**
     * Set entity description check
     *
     * @param entityType  Entity type
     * @param checkInitializer Инициализатор проверки
     * @return Current check
     */
    public ModelDescriptionCheck setEntityDescriptionCheck(String entityType, Consumer<EntityDescriptionCheck> checkInitializer) {
        checkInitializer.accept(entityDescriptionChecks.computeIfAbsent(entityType, key -> new EntityDescriptionCheck(modelDescription, key)));
        return this;
    }

    @Override
    public void check() {
        Helper.check(modelDescription.getEnumDescriptions(), enumDescriptionChecks);
        Helper.check(modelDescription.getEntityDescriptions(), entityDescriptionChecks);
        modelDescription.getGroupDescriptions().forEach((groupName, groupDescriptions) ->
            groupDescriptions.forEach(groupDescription -> {
                assertEquals(groupName, groupDescription.getGroupName());
                assertEquals(groupDescription, modelDescription.getEntityDescription(groupDescription.getOwnerEntityDescription().getName()).getGroupDescription(groupDescription.getName()));
            }));
    }
}
