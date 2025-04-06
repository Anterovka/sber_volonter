package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Testing settings")
public class SettingsTest {

    @DisplayName("Model Description Settings Testing")
    @Test
    public void modelDescriptionSettingsTest() {
        ModelDescriptionSettings modelDescriptionSettings = new ModelDescriptionSettings();
        assertNull(modelDescriptionSettings.getEnumDescriptionSettings(TestHelper.ENUM_TYPE));
        assertNull(modelDescriptionSettings.getEntityDescriptionSettings(TestHelper.ENTITY_TYPE));
        modelDescriptionSettings.setEnumDescriptionSettings(TestHelper.ENUM_TYPE, new EnumDescriptionSettings());
        assertThrows(DuplicateEnumTypeFoundException.class, () -> modelDescriptionSettings.setEnumDescriptionSettings(TestHelper.ENUM_TYPE, new EnumDescriptionSettings()));
        modelDescriptionSettings.setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings());
        assertThrows(DuplicateEntityTypeFoundException.class, () -> modelDescriptionSettings.setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()));
    }

    @DisplayName("Testing the description settings enumeration")
    @Test
    public void enumDescriptionSettingsTest() {
        EnumDescriptionSettings enumDescriptionSettings = new EnumDescriptionSettings();
        assertTrue(enumDescriptionSettings.getValues().isEmpty());
    }

    @DisplayName("Testing entity description settings")
    @Test
    public void entityDescriptionSettingsTest() {
        EntityDescriptionSettings entityDescriptionSettings = new EntityDescriptionSettings();
        assertNull(entityDescriptionSettings.getParentEntityType());
        assertNull(entityDescriptionSettings.getAggregateEntityType());
        assertFalse(entityDescriptionSettings.isFinal());
        assertFalse(entityDescriptionSettings.isAggregate());
        assertNull(entityDescriptionSettings.getInheritanceStrategy());
        assertEquals(TableType.SIMPLE, entityDescriptionSettings.getTableType());
        assertNull(entityDescriptionSettings.getTableName());
        assertNull(entityDescriptionSettings.getIdColumnName());
        assertNull(entityDescriptionSettings.getTypeColumnName());
        assertNull(entityDescriptionSettings.getAggregateColumnName());
        assertNull(entityDescriptionSettings.getSystemLocksTableName());
        assertNull(entityDescriptionSettings.getSystemLocksAggregateColumnName());
        assertNull(entityDescriptionSettings.getSystemLocksVersionColumnName());
        assertNull(entityDescriptionSettings.getParamDescriptionSettings(TestHelper.PROPERTY_NAME));
        assertNull(entityDescriptionSettings.getPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME));
        assertNull(entityDescriptionSettings.getPrimitivesCollectionDescriptionSettings(TestHelper.PROPERTY_NAME));
        assertNull(entityDescriptionSettings.getReferenceDescriptionSettings(TestHelper.PROPERTY_NAME));
        assertNull(entityDescriptionSettings.getReferencesCollectionDescriptionSettings(TestHelper.PROPERTY_NAME));
        assertNull(entityDescriptionSettings.getGroupDescriptionSettings(TestHelper.PROPERTY_NAME));
        entityDescriptionSettings.setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME, new PrimitiveDescriptionSettings());
        assertThrows(DuplicatePropertyNamesFoundException.class, () -> entityDescriptionSettings.setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME, new PrimitiveDescriptionSettings()));
    }

    @DisplayName("Testing parameter description settings")
    @Test
    public void paramDescriptionSettingsTest() {
        ParamDescriptionSettings paramDescriptionSettings = new ParamDescriptionSettings();
        assertNull(paramDescriptionSettings.getType());
        assertFalse(paramDescriptionSettings.isCollection());
        assertNull(paramDescriptionSettings.getDefaultValue());
    }

    @DisplayName("Testing the primitive description settings")
    @Test
    public void primitiveDescriptionSettingsTest() {
        PrimitiveDescriptionSettings primitiveDescriptionSettings = new PrimitiveDescriptionSettings();
        assertNull(primitiveDescriptionSettings.getColumnName());
        assertNull(primitiveDescriptionSettings.getType());
        assertFalse(primitiveDescriptionSettings.isMandatory());
        assertNull(primitiveDescriptionSettings.getEnumType());
    }

    @DisplayName("Testing the settings of the description of the primitive collection")
    @Test
    public void primitivesCollectionDescriptionSettingsTest() {
        PrimitivesCollectionDescriptionSettings primitivesCollectionDescriptionSettings = new PrimitivesCollectionDescriptionSettings();
        assertNull(primitivesCollectionDescriptionSettings.getColumnName());
        assertNull(primitivesCollectionDescriptionSettings.getTableName());
        assertNull(primitivesCollectionDescriptionSettings.getOwnerColumnName());
        assertNull(primitivesCollectionDescriptionSettings.getType());
        assertNull(primitivesCollectionDescriptionSettings.getEnumType());
    }

    @DisplayName("Testing the link description settings")
    @Test
    public void referenceDescriptionSettingsTest() {
        ReferenceDescriptionSettings referenceDescriptionSettings = new ReferenceDescriptionSettings();
        assertNull(referenceDescriptionSettings.getColumnName());
        assertNull(referenceDescriptionSettings.getEntityType());
        assertFalse(referenceDescriptionSettings.isMandatory());
        assertNull(referenceDescriptionSettings.getEntityReferencePropertyName());
        assertNull(referenceDescriptionSettings.getEntityReferencesCollectionPropertyName());
    }

    @DisplayName("Testing the description collection settings")
    @Test
    public void referencesCollectionDescriptionSettingsTest() {
        ReferencesCollectionDescriptionSettings referencesCollectionDescriptionSettings = new ReferencesCollectionDescriptionSettings();
        assertNull(referencesCollectionDescriptionSettings.getColumnName());
        assertNull(referencesCollectionDescriptionSettings.getTableName());
        assertNull(referencesCollectionDescriptionSettings.getOwnerColumnName());
        assertNull(referencesCollectionDescriptionSettings.getEntityType());
    }

    @DisplayName("Testing the grouping description settings")
    @Test
    public void groupDescriptionSettingsTest() {
        GroupDescriptionSettings groupDescriptionSettings = new GroupDescriptionSettings();
        assertNull(groupDescriptionSettings.getGroupName());
        assertNull(groupDescriptionSettings.getPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME));
        assertNull(groupDescriptionSettings.getReferenceDescriptionSettings(TestHelper.PROPERTY_NAME));
    }
}
