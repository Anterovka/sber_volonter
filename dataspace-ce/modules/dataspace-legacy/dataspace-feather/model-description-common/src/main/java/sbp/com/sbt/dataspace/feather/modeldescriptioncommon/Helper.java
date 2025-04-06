package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescriptionWithType;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;

/**
 * Assistant
 */
final class Helper {

    private Helper() {
    }

    /**
     * Put settings description property
     *
     * @param propertyName                The name of the property
     * @param propertyDescriptionSettings The property description settings
     */
    static <S> void putPropertyDescriptionSettings(Map<String, S> propertyDescriptionsSettings, String propertyName, S propertyDescriptionSettings) {
checkNotNull(propertyName, "Property name");
checkNotNull(propertyDescriptionSettings, "Property description settings");
        if (propertyDescriptionsSettings.containsKey(propertyName)) {
            throw new DuplicatePropertyNamesFoundException(propertyName);
        }
        propertyDescriptionsSettings.put(propertyName, propertyDescriptionSettings);
    }

    /**
     * Process property descriptions
     *
     * @param ownerEntityDescription         Description of the entity-owner
     * @param propertyDescriptionsSettings   Settings for property descriptions
     * @param propertyDescriptions           Property descriptions
     * @param propertyDescriptionInitializer Initializer of the property description
     * @param <S>                            Type of settings
     * @param <P>                            Type of property description
     * @param <I>                            Type of property description implementation
     */
    static <S extends PropertyDescriptionSettings, P, I extends AbstractPropertyDescription> void processPropertyDescriptions(EntityDescription ownerEntityDescription, Map<String, S> propertyDescriptionsSettings, Map<String, P> propertyDescriptions, Supplier<I> propertyDescriptionInitializer) {
        propertyDescriptionsSettings.forEach((propertyName, propertyDescriptionSettings) -> {
            I propertyDescription = propertyDescriptionInitializer.get();
            propertyDescription.ownerEntityDescription = ownerEntityDescription;
            propertyDescription.name = propertyName;
            propertyDescriptions.put(propertyName, (P) propertyDescription);
        });
        forEach(propertyDescriptionsSettings.entrySet().stream(), entry -> ((I) propertyDescriptions.get(entry.getKey())).process(entry.getValue()), (exception, entry) -> new ProcessPropertyDescriptionException(exception, entry.getKey()));
    }

    /**
     * Get property description
     *
     * @param propertyDescriptions Property descriptions
     * @param propertyName         Property name
     * @param exceptionInitializer Exception initializer
     * @param <D>                  Type of property description
     */
    static <D extends PropertyDescription> D getPropertyDescription(Map<String, D> propertyDescriptions, String propertyName, Function<String, FeatherException> exceptionInitializer) {
        D propertyDescription = propertyDescriptions.get(propertyName);
        if (propertyDescription == null) {
            throw exceptionInitializer.apply(propertyName);
        }
        return propertyDescription;
    }

    /**
     * Check the type of enumeration value
     *
     * @param propertyDescriptionWithType Description of the property with type
     */
    static void checkEnumValueType(PropertyDescriptionWithType propertyDescriptionWithType) {
        if (propertyDescriptionWithType.getType() != DataType.STRING) {
            throw new UnsupportedEnumValueTypeException(propertyDescriptionWithType.getType());
        }
    }
}
