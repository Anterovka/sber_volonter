package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EnumDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.util.Collections;
import java.util.Set;

/**
 * Implementation of enumeration description
 */
class EnumDescriptionImpl implements EnumDescription {

    ModelDescription modelDescription;
    String name;
    Set<String> values;

    /**
     * Process
     *
     * @param enumDescriptionSettings The settings for enumeration description
     */
    void process(EnumDescriptionSettings enumDescriptionSettings) {
        values = Collections.unmodifiableSet(enumDescriptionSettings.values);
    }

    @Override
    public ModelDescription getModelDescription() {
        return modelDescription;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getValues() {
        return values;
    }
}
