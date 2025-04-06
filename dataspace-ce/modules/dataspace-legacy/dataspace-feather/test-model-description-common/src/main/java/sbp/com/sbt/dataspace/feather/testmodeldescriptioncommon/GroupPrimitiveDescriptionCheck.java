package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Checking the description of the grouping primitive
 */
public class GroupPrimitiveDescriptionCheck extends AbstractCheck {

    GroupDescription groupDescription;
    PrimitiveDescription primitiveDescription;
    String name;
    String columnName;
    DataType type;
    boolean mandatory;
    String enumType;

    /**
     * @param groupDescription Description of the grouping
     * @param name                Name
     */
    GroupPrimitiveDescriptionCheck(GroupDescription groupDescription, String name) {
        this.groupDescription = groupDescription;
        primitiveDescription = groupDescription.getPrimitiveDescription(name);
        this.name = name;
    }

    /**
     * Set the column name
     *
     * @return Current check
     */
    public GroupPrimitiveDescriptionCheck setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Set type
     *
     * @return Current check
     */
    public GroupPrimitiveDescriptionCheck setType(DataType type) {
        this.type = type;
        return this;
    }

    /**
     * Set the obligation mark
     *
     * @return Current check
     */
    public GroupPrimitiveDescriptionCheck setMandatory() {
        this.mandatory = true;
        return this;
    }

    /**
     * Set enumeration type
     *
     * @return Current check
     */
    public GroupPrimitiveDescriptionCheck setEnumType(String enumType) {
        this.enumType = enumType;
        return this;
    }

    @Override
    void check() {
        assertNotNull(primitiveDescription);

        assertEquals(groupDescription.getPrimitiveDescriptions().get(name), primitiveDescription);

        assertEquals(groupDescription.getOwnerEntityDescription(), primitiveDescription.getOwnerEntityDescription());
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
