package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.feather.modeldescription.CheckModelDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.TableType;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing of incorrect models")
public class IncorrectModelsTest {

    @DisplayName("Test for exception 'Duplicate parameter names found'")
    @Test
    public void duplicateParamNamesFoundExceptionTest() {
        assertThrowsCausedBy(DuplicateParamNamesFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setParamDescriptionSettings(TestHelper.PROPERTY_NAME, new ParamDescriptionSettings()
                                .setType(DataType.STRING))
                        .setParamDescriptionSettings(TestHelper.PROPERTY_NAME, new ParamDescriptionSettings()
                                .setType(DataType.STRING))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Duplicate property names found'")
    @Test
    public void duplicatePropertyNamesFoundExceptionTest() {
        assertThrowsCausedBy(DuplicatePropertyNamesFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME, new PrimitiveDescriptionSettings()
                                .setType(DataType.STRING))
                        .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME, new ReferenceDescriptionSettings()
                                .setEntityType(TestHelper.ENTITY_TYPE))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Duplicate property names found' (2)")
    @Test
    public void duplicatePropertyNamesFoundExceptionTest2() {
        assertThrowsCausedBy(DuplicatePropertyNamesFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME, new ReferenceDescriptionSettings()
                                .setEntityType(TestHelper.ENTITY_TYPE)
                                .setEntityReferencePropertyName(TestHelper.PROPERTY_NAME2))
                        .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME3, new ReferenceDescriptionSettings()
                                .setEntityType(TestHelper.ENTITY_TYPE)
                                .setEntityReferencePropertyName(TestHelper.PROPERTY_NAME2))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Duplicate property names found' (3)")
    @Test
    public void duplicatePropertyNamesFoundExceptionTest3() {
        assertThrowsCausedBy(DuplicatePropertyNamesFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME, new ReferenceDescriptionSettings()
                                .setEntityType(TestHelper.ENTITY_TYPE)
                                .setEntityReferencesCollectionPropertyName(TestHelper.PROPERTY_NAME2))
                        .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME3, new ReferenceDescriptionSettings()
                                .setEntityType(TestHelper.ENTITY_TYPE)
                                .setEntityReferencesCollectionPropertyName(TestHelper.PROPERTY_NAME2))),
                Collections.emptyList()));
    }

    @DisplayName("Test for the exception 'Parent entity description is final'")
    @Test
    public void finalParentEntityDescriptionExceptionTest() {
        assertThrowsCausedBy(FinalParentEntityDescriptionException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setFinal())
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE2, new EntityDescriptionSettings()
                        .setParentEntityType(TestHelper.ENTITY_TYPE)),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Entity description is not an aggregate'")
    @Test
    public void notAggregateEntityDescriptionTest() {
        assertThrowsCausedBy(NotAggregateEntityDescription.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings())
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE2, new EntityDescriptionSettings()
                        .setAggregateEntityType(TestHelper.ENTITY_TYPE)),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Aggregate root characteristic detected for non-root entity description'")
    @Test
    public void aggregateForNotRootEntityDescriptionFoundExceptionTest() {
        assertThrowsCausedBy(AggregateForNotRootEntityDescriptionFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings())
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE2, new EntityDescriptionSettings()
                        .setParentEntityType(TestHelper.ENTITY_TYPE)
                        .setAggregate()),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Entity aggregate description found for non-root entity'")
    @Test
    public void aggregateEntityDescriptionForNotRootEntityDescriptionFoundExceptionTest() {
        assertThrowsCausedBy(AggregateEntityDescriptionForNotRootEntityDescriptionFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings())
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE2, new EntityDescriptionSettings()
                        .setParentEntityType(TestHelper.ENTITY_TYPE)
                        .setAggregateEntityType(TestHelper.ENTITY_TYPE3))
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE3, new EntityDescriptionSettings()
                        .setAggregate()),
                Collections.emptyList()));
    }

    @DisplayName("Test for the exception 'Reference Abuse'")
    @Test
    public void backReferenceOveruseExceptionTest() {
        assertThrowsCausedBy(BackReferenceOveruseException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME, new ReferenceDescriptionSettings()
                                .setEntityType(TestHelper.ENTITY_TYPE)
                                .setEntityReferencePropertyName(TestHelper.PROPERTY_NAME2)
                                .setEntityReferencesCollectionPropertyName(TestHelper.PROPERTY_NAME3))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Error during model description check'")
    @Test
    public void checkModelDescriptionExceptionTest() {
        assertThrows(CheckModelDescriptionException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()),
                Collections.singletonList(new TestModelDescriptionCheck2())));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found'")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (2)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest2() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.INTEGER)))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (3)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest3() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (4)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest4() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)
                                        .setMandatory()))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (5)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest5() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEnumDescriptionSettings(TestHelper.ENUM_TYPE, new EnumDescriptionSettings())
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME2, new PrimitiveDescriptionSettings()
                                        .setType(DataType.STRING)
                                        .setEnumType(TestHelper.ENUM_TYPE)))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (6)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest6() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (7)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest7() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE2))))
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE2, new EntityDescriptionSettings()),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (8)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest8() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)
                                        .setMandatory()))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name were found' (9)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest9() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)
                                        .setEntityReferencePropertyName(TestHelper.PROPERTY_NAME2)))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (10)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest10() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)
                                        .setEntityReferencesCollectionPropertyName(TestHelper.PROPERTY_NAME2)))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Different grouping structures with the same name have been found' (11)")
    @Test
    public void differentGroupStructuresWithSameNameFoundExceptionTest11() {
        assertThrowsCausedBy(DifferentGroupStructuresWithSameNameFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME2, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))
                        .setGroupDescriptionSettings(TestHelper.PROPERTY_NAME3, new GroupDescriptionSettings()
                                .setGroupName(TestHelper.GROUP_NAME)
                                .setReferenceDescriptionSettings(TestHelper.PROPERTY_NAME, new ReferenceDescriptionSettings()
                                        .setEntityType(TestHelper.ENTITY_TYPE)))),
                Collections.emptyList()));
    }

    @DisplayName("Test for the exception 'Unsupported enum value type'")
    @Test
    public void unsupportedEnumValueTypeExceptionTest() {
        assertThrowsCausedBy(UnsupportedEnumValueTypeException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEnumDescriptionSettings(TestHelper.ENUM_TYPE, new EnumDescriptionSettings())
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setPrimitiveDescriptionSettings(TestHelper.PROPERTY_NAME, new PrimitiveDescriptionSettings()
                                .setType(DataType.INTEGER)
                                .setEnumType(TestHelper.ENUM_TYPE))),
                Collections.emptyList()));
    }

    @DisplayName("Test for exception 'Default value is not supported for collection parameters'")
    @Test
    public void collectionParamDefaultValueNotSupportedExceptionTest() {
        assertThrowsCausedBy(CollectionParamDefaultValueNotSupportedException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setTableType(TableType.QUERY)
                        .setParamDescriptionSettings(TestHelper.PROPERTY_NAME, new ParamDescriptionSettings()
                                .setType(DataType.STRING)
                                .setCollection()
                                .setDefaultValue("Test"))),
                Collections.emptyList()));
    }

    @DisplayName("Test for the exception 'Default parameter value is incorrect'")
    @Test
    public void paramDefaultValueIsIncorrectExceptionTest() {
        assertThrowsCausedBy(ParamDefaultValueIsIncorrectException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings(TestHelper.ENTITY_TYPE, new EntityDescriptionSettings()
                        .setTableType(TableType.QUERY)
                        .setParamDescriptionSettings(TestHelper.PROPERTY_NAME, new ParamDescriptionSettings()
                                .setType(DataType.STRING)
                                .setDefaultValue(1))),
                Collections.emptyList()));
    }
}
