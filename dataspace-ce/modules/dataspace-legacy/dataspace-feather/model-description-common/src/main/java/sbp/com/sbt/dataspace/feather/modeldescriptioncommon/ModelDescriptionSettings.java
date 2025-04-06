package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import java.util.LinkedHashMap;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Model description settings
 */
public final class ModelDescriptionSettings {

    Map<String, EnumDescriptionSettings> enumDescriptionsSettings = new LinkedHashMap<>();
    Map<String, EntityDescriptionSettings> entityDescriptionsSettings = new LinkedHashMap<>();

    /**
     * Get enumeration description settings
     *
     * @param enumType Enumeration type
     */
    public EnumDescriptionSettings getEnumDescriptionSettings(String enumType) {
        return enumDescriptionsSettings.get(enumType);
    }

    /**
     * Set enumeration description settings
     *
     * @param enumType Enumeration type
     * @return Current settings
     */
    public ModelDescriptionSettings setEnumDescriptionSettings(String enumType, EnumDescriptionSettings enumDescriptionSettings) {
        checkNotNull(enumType, "Enumeration type");
        checkNotNull(enumDescriptionSettings, "Enum description settings");
        if (enumDescriptionsSettings.containsKey(enumType)) {
            throw new DuplicateEnumTypeFoundException(enumType);
        }
        enumDescriptionsSettings.put(enumType, enumDescriptionSettings);
        return this;
    }

    /**
     * Get entity description settings
     *
     * @param entityType Entity type
     */
    public EntityDescriptionSettings getEntityDescriptionSettings(String entityType) {
        return entityDescriptionsSettings.get(entityType);
    }

    /**
     * Set entity description settings
     *
     * @param entityType Entity type
     * @return Current settings
     */
    public ModelDescriptionSettings setEntityDescriptionSettings(String entityType, EntityDescriptionSettings entityDescriptionSettings) {
        checkNotNull(entityType, "Entity type");
        checkNotNull(entityDescriptionSettings, "Entity description settings");
        if (entityDescriptionsSettings.containsKey(entityType)) {
            throw new DuplicateEntityTypeFoundException(entityType);
        }
        entityDescriptionsSettings.put(entityType, entityDescriptionSettings);
        return this;
    }
}
