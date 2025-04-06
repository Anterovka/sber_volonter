package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferencesCollectionDescription;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checking the description of the link collection
 */
public class ReferencesCollectionDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    ReferencesCollectionDescription referencesCollectionDescription;
    String name;
    String columnName;
    String tableName;
    String ownerColumnName;
    String entityType;

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    ReferencesCollectionDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        referencesCollectionDescription = entityDescription.getDeclaredReferencesCollectionDescriptions().get(name);
        this.name = name;
    }

    /**
     * Set the column name
     *
     * @return Current check
     */
    public ReferencesCollectionDescriptionCheck setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    /**
     * Set table name
     *
     * @return Current check
     */
    public ReferencesCollectionDescriptionCheck setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    /**
     * Set the column name with the owner
     *
     * @return Current check
     */
    public ReferencesCollectionDescriptionCheck setOwnerColumnName(String ownerColumnName) {
        this.ownerColumnName = ownerColumnName;
        return this;
    }

    /**
     * Set entity type
     *
     * @return Current check
     */
    public ReferencesCollectionDescriptionCheck setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    @Override
    void check() {
        assertNotNull(referencesCollectionDescription);

        assertEquals(entityDescription.getReferencesCollectionDescription(name), referencesCollectionDescription);
        assertEquals(entityDescription.getReferencesCollectionDescriptions().get(name), referencesCollectionDescription);

        assertEquals(entityDescription, referencesCollectionDescription.getOwnerEntityDescription());
        assertEquals(name, referencesCollectionDescription.getName());
        assertEquals(columnName, referencesCollectionDescription.getColumnName());
        assertEquals(tableName, referencesCollectionDescription.getTableName());
        assertEquals(ownerColumnName, referencesCollectionDescription.getOwnerColumnName());
        assertEquals(entityDescription.getModelDescription().getEntityDescription(entityType), referencesCollectionDescription.getEntityDescription());
    }
}
