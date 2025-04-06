package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.InheritanceStrategy;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.EntityDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.GroupDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionImpl;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ParamDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.PrimitivesCollectionDescriptionSettings;

import java.util.Collections;

import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing of incorrect models")
public class IncorrectModelsTest {

    @DisplayName("Test for exception 'Mandatory setting not set'")
    @Test
    public void requiredSettingNotSetExceptionTest() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (2)")
    @Test
    public void requiredSettingNotSetExceptionTest2() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (3)")
    @Test
    public void requiredSettingNotSetExceptionTest3() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setTypeColumnName("TYPE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (4)")
    @Test
    public void requiredSettingNotSetExceptionTest4() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (5)")
    @Test
    public void requiredSettingNotSetExceptionTest5() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setIdColumnName("ID")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (6)")
    @Test
    public void requiredSettingNotSetExceptionTest6() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (7)")
    @Test
    public void requiredSettingNotSetExceptionTest7() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitivesCollectionDescriptionSettings("property", new PrimitivesCollectionDescriptionSettings()
                                .setTableName("ENTITY_PROPERTY")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.STRING))),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (8)")
    @Test
    public void requiredSettingNotSetExceptionTest8() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitivesCollectionDescriptionSettings("property", new PrimitivesCollectionDescriptionSettings()
                                .setColumnName("ELEMENT")
                                .setOwnerColumnName("OWNER")
                                .setType(DataType.STRING))),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (9)")
    @Test
    public void requiredSettingNotSetExceptionTest9() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setPrimitivesCollectionDescriptionSettings("property", new PrimitivesCollectionDescriptionSettings()
                                .setColumnName("ELEMENT")
                                .setTableName("ENTITY_PROPERTY")
                                .setType(DataType.STRING))),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (10)")
    @Test
    public void requiredSettingNotSetExceptionTest10() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setAggregateEntityType("Entity")
                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksTableName("ENTITY_SYSTEM_LOCKS")
                        .setSystemLocksAggregateColumnName("AGGREGATE")
                        .setSystemLocksVersionColumnName("VERSION")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (11)")
    @Test
    public void requiredSettingNotSetExceptionTest11() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksAggregateColumnName("AGGREGATE")
                        .setSystemLocksVersionColumnName("VERSION")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (12)")
    @Test
    public void requiredSettingNotSetExceptionTest12() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksTableName("ENTITY_SYSTEM_LOCKS")
                        .setSystemLocksVersionColumnName("VERSION")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'The required setting is not set' (13)")
    @Test
    public void requiredSettingNotSetExceptionTest13() {
        assertThrowsCausedBy(RequiredSettingNotSetException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setAggregate()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksTableName("ENTITY_SYSTEM_LOCKS")
                        .setSystemLocksAggregateColumnName("AGGREGATE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found'")
    @Test
    public void extraSettingFoundExceptionTest() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setFinal()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (2)")
    @Test
    public void extraSettingFoundExceptionTest2() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY2")
                        .setIdColumnName("ID")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (3)")
    @Test
    public void extraSettingFoundExceptionTest3() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (4)")
    @Test
    public void extraSettingFoundExceptionTest4() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (5)")
    @Test
    public void extraSettingFoundExceptionTest5() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.SINGLE_TABLE)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setIdColumnName("ID")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (6)")
    @Test
    public void extraSettingFoundExceptionTest6() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setAggregateColumnName("AGGREGATE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (7)")
    @Test
    public void extraSettingFoundExceptionTest7() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksTableName("ENTITY_SYSTEM_LOCKS")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (8)")
    @Test
    public void extraSettingFoundExceptionTest8() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksAggregateColumnName("ENTITY_SYSTEM_LOCKS")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (9)")
    @Test
    public void extraSettingFoundExceptionTest9() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setSystemLocksVersionColumnName("ENTITY_SYSTEM_LOCKS")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (10)")
    @Test
    public void extraSettingFoundExceptionTest10() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")
                        .setIdColumnName("ID")
                        .setAggregateColumnName("AGGREGATE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (11)")
    @Test
    public void extraSettingFoundExceptionTest11() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")
                        .setIdColumnName("ID")
                        .setSystemLocksTableName("ENTITY2_SYSTEM_LOCKS")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (12)")
    @Test
    public void extraSettingFoundExceptionTest12() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")
                        .setIdColumnName("ID")
                        .setSystemLocksAggregateColumnName("AGGREGATE")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (13)")
    @Test
    public void extraSettingFoundExceptionTest13() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE"))
                .setEntityDescriptionSettings("Entity2", new EntityDescriptionSettings()
                        .setParentEntityType("Entity")
                        .setTableName("ENTITY2")
                        .setIdColumnName("ID")
                        .setSystemLocksVersionColumnName("VERSION")),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Extra setting found' (14)")
    @Test
    public void extraSettingFoundExceptionTest14() {
        assertThrowsCausedBy(ExtraSettingFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setFinal()
                        .setTableType(TableType.QUERY)
                        .setTableName("ENTITY")
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for the exception 'Grouping Properties Not Found'")
    @Test
    public void groupPropertiesNotFoundExceptionTest() {
        assertThrowsCausedBy(GroupPropertiesNotFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setTableName("ENTITY")
                        .setIdColumnName("ID")
                        .setTypeColumnName("TYPE")
                        .setGroupDescriptionSettings("property", new GroupDescriptionSettings()
                                .setGroupName("Группа"))),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for exception 'Invalid parameter type'")
    @Test
    public void invalidParamTypeException() {
        assertThrowsCausedBy(InvalidParamTypeException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setFinal()
                        .setTableType(TableType.QUERY)
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setParamDescriptionSettings("p1", new ParamDescriptionSettings()
                                .setType(DataType.TEXT))),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }

    @DisplayName("Test for the exception 'Invalid parameter type' (2)")
    @Test
    public void invalidParamTypeException2() {
        assertThrowsCausedBy(InvalidParamTypeException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setFinal()
                        .setTableType(TableType.QUERY)
                        .setInheritanceStrategy(InheritanceStrategy.JOINED)
                        .setParamDescriptionSettings("p1", new ParamDescriptionSettings()
                                .setType(DataType.BYTE_ARRAY))),
                Collections.singletonList(new EntitiesReadAccessJsonModelDescriptionCheck())));
    }
}
