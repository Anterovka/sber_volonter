package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitivesCollectionDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Checking the description of the primitive collection
 */
public class PrimitivesCollectionDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    PrimitivesCollectionDescription primitivesCollectionDescription;
    String name;
    String columnName;
    String tableName;
    String ownerColumnName;
    DataType type;
    String enumType;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    PrimitivesCollectionDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        primitivesCollectionDescription = entityDescription.getDeclaredPrimitivesCollectionDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set the column name
     *
     * @return Current check
     */
    public PrimitivesCollectionDescriptionCheck setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Set table name
     *
     * @return Current check
     */
    public PrimitivesCollectionDescriptionCheck setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Set the column name with the owner
     *
     * @return Current check
     */
    public PrimitivesCollectionDescriptionCheck setOwnerColumnName(String ownerColumnName) {
        this.ownerColumnName = ownerColumnName;
        return this;
    }

    /**
     * Set type
     *
     * @return Current check
     */
    public PrimitivesCollectionDescriptionCheck setType(DataType type) {
        this.type = type;
        return this;
    }

    /**
     * Set enumeration type
     *
     * @return Current check
     */
    public PrimitivesCollectionDescriptionCheck setEnumType(String enumType) {
        this.enumType = enumType;
        return this;
    }

    @Override
    void check() {
        assertNotNull(primitivesCollectionDescription);

        assertEquals(entityDescription.getPrimitivesCollectionDescription(name), primitivesCollectionDescription);
        assertEquals(entityDescription.getPrimitivesCollectionDescriptions().get(name), primitivesCollectionDescription);

        assertEquals(entityDescription, primitivesCollectionDescription.getOwnerEntityDescription());
        assertEquals(name, primitivesCollectionDescription.getName());
        assertEquals(columnName, primitivesCollectionDescription.getColumnName());
        assertEquals(tableName, primitivesCollectionDescription.getTableName());
        assertEquals(ownerColumnName, primitivesCollectionDescription.getOwnerColumnName());
        assertEquals(type, primitivesCollectionDescription.getType());
        if (enumType == null) {
            assertNull(primitivesCollectionDescription.getEnumDescription());
        } else {
            assertEquals(primitivesCollectionDescription.getOwnerEntityDescription().getModelDescription().getEnumDescription(enumType), primitivesCollectionDescription.getEnumDescription());
            assertEquals(enumType, primitivesCollectionDescription.getEnumDescription().getName());
        }
    }
}
