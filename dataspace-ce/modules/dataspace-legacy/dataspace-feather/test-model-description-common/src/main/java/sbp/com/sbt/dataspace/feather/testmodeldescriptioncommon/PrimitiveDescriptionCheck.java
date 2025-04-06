package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Checking the description of the primitive
 */
public class PrimitiveDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    PrimitiveDescription primitiveDescription;
    String name;
    String columnName;
    DataType type;
    boolean mandatory;
    String enumType;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    PrimitiveDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        primitiveDescription = entityDescription.getDeclaredPrimitiveDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set the column name
     *
     * @return Current check
     */
    public PrimitiveDescriptionCheck setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Set type
     *
     * @return Current check
     */
    public PrimitiveDescriptionCheck setType(DataType type) {
        this.type = type;
        return this;
    }

    /**
     * Set the mandatory flag
     *
     * @return Current check
     */
    public PrimitiveDescriptionCheck setMandatory() {
        this.mandatory = true;
        return this;
    }

    /**
     * Set enumeration type
     *
     * @return Current check
     */
    public PrimitiveDescriptionCheck setEnumType(String enumType) {
        this.enumType = enumType;
        return this;
    }

    @Override
    void check() {
        assertNotNull(primitiveDescription);

        assertEquals(entityDescription.getPrimitiveDescription(name), primitiveDescription);
        assertEquals(entityDescription.getPrimitiveDescriptions().get(name), primitiveDescription);

        assertEquals(entityDescription, primitiveDescription.getOwnerEntityDescription());
        assertEquals(name, primitiveDescription.getName());
        assertEquals(columnName, primitiveDescription.getColumnName());
        assertEquals(type, primitiveDescription.getType());
        assertEquals(mandatory, primitiveDescription.isMandatory());
        if (enumType == null) {
            assertNull(primitiveDescription.getEnumDescription());
        } else {
            assertEquals(primitiveDescription.getOwnerEntityDescription().getModelDescription().getEnumDescription(enumType), primitiveDescription.getEnumDescription());
            assertEquals(enumType, primitiveDescription.getEnumDescription().getName());
        }
    }
}
