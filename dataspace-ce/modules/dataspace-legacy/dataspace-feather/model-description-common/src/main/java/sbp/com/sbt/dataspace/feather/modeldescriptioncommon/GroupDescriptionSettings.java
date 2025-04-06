package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import java.util.LinkedHashMap;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.putPropertyDescriptionSettings;

/**
 * Settings for grouping description
 */
public final class GroupDescriptionSettings implements PropertyDescriptionSettings {

    String groupName;
    Map<String, PrimitiveDescriptionSettings> primitiveDescriptionsSettings = new LinkedHashMap<>();
    Map<String, ReferenceDescriptionSettings> referenceDescriptionsSettings = new LinkedHashMap<>();

    /**
     * Get the name of the grouping
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Set the name of the grouping
     *
     * @return Current settings
     */
    public GroupDescriptionSettings setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    /**
     * Get settings for primitive description
     */
    public PrimitiveDescriptionSettings getPrimitiveDescriptionSettings(String propertyName) {
        return primitiveDescriptionsSettings.get(propertyName);
    }

    /**
     * Set primitive description settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public GroupDescriptionSettings setPrimitiveDescriptionSettings(String propertyName, PrimitiveDescriptionSettings primitiveDescriptionSettings) {
        putPropertyDescriptionSettings(primitiveDescriptionsSettings, propertyName, primitiveDescriptionSettings);
        return this;
    }

    /**
     * Get link description settings
     */
    public ReferenceDescriptionSettings getReferenceDescriptionSettings(String propertyName) {
        return referenceDescriptionsSettings.get(propertyName);
    }

    /**
     * Set link description settings
     *
     * @param propertyName Property name
     * @return Current settings
     */
    public GroupDescriptionSettings setReferenceDescriptionSettings(String propertyName, ReferenceDescriptionSettings referenceDescriptionSettings) {
        putPropertyDescriptionSettings(referenceDescriptionsSettings, propertyName, referenceDescriptionSettings);
        return this;
    }
}
