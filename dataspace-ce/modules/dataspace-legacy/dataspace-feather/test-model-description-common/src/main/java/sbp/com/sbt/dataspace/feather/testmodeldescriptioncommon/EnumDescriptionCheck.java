package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EnumDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking the description of the enumeration
 */
public class EnumDescriptionCheck extends AbstractCheck {

    ModelDescription modelDescription;
    EnumDescription enumDescription;
    String name;
    Set<String> values;

    /**
     * @param modelDescription Model description
     * @param name             Name
     */
    EnumDescriptionCheck(ModelDescription modelDescription, String name) {
        this.modelDescription = modelDescription;
        enumDescription = modelDescription.getEnumDescription(name);
        this.name = name;
    }

    /**
     * Set values
     *
     * @return Current check
     */
    public EnumDescriptionCheck setValues(Set<String> values) {
        this.values = values;
        return this;
    }

    @Override
    void check() {
        assertNotNull(enumDescription);

        assertEquals(enumDescription, modelDescription.getEnumDescriptions().get(name));

        assertEquals(modelDescription, enumDescription.getModelDescription());
        assertEquals(values, enumDescription.getValues());
    }
}
