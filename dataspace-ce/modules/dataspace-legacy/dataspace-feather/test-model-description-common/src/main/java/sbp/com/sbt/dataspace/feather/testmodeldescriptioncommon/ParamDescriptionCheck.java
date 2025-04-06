package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ParamDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking parameter description
 */
public class ParamDescriptionCheck extends AbstractCheck {

    ParamDescription paramDescription;
    String name;
    DataType type;
    boolean collection;
    Object defaultValue;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    ParamDescriptionCheck(EntityDescription entityDescription, String name) {
        paramDescription = entityDescription.getParamDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set type
     *
     * @param type Тип
     * @return Current check
     */
    public ParamDescriptionCheck setType(DataType type) {
        this.type = type;
        return this;
    }

    /**
     * Set collection flag
     *
     * @return Current check
     */
    public ParamDescriptionCheck setCollection() {
        collection = true;
        return this;
    }

    /**
     * Set default value
     *
     * @return Current check
     */
    public ParamDescriptionCheck setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    void check() {
        assertNotNull(paramDescription);
        assertEquals(name, paramDescription.getName());
        assertEquals(type, paramDescription.getType());
        assertEquals(collection, paramDescription.isCollection());
        assertEquals(defaultValue, paramDescription.getDefaultValue());
    }
}
