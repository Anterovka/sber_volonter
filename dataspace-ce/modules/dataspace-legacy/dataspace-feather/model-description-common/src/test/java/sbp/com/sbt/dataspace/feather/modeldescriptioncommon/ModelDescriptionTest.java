package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Model Description Testing")
@SpringJUnitConfig(ModelDescriptionTestConfiguration.class)
public class ModelDescriptionTest extends sbp.com.sbt.dataspace.feather.testmodeldescription.ModelDescriptionTest {

    static final Object WRITE_KEY = new Object();
    static final String VERSION = "DEV-SNAPSHOT";

    @DisplayName("Testing of work with metadata")
    @Test
    public void metaDataManagementTest() {
        assertNull(modelDescription.getMetaDataManager().get(String.class));
        modelDescription.getMetaDataManager().put(String.class, WRITE_KEY, VERSION);
        assertEquals(VERSION, modelDescription.getMetaDataManager().get(String.class));
        modelDescription.getMetaDataManager().remove(String.class, WRITE_KEY);
        assertNull(modelDescription.getMetaDataManager().get(String.class));
    }

    @DisplayName("Test for the exception 'Enum description not found'")
    @Test
    public void enumDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(EnumDescriptionNotFoundException.class, () -> modelDescription.getEnumDescription(TestHelper.ENUM_TYPE));
    }

    @DisplayName("Test for the exception 'Entity description not found'")
    @Test
    public void entityDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(EntityDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription(TestHelper.ENTITY_TYPE2));
    }

    @DisplayName("Test for the exception 'Primitive description not found'")
    @Test
    public void primitiveDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(PrimitiveDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getPrimitiveDescription(TestHelper.PROPERTY_NAME));
    }

    @DisplayName("Test for the exception 'Primitive collection description not found'")
    @Test
    public void primitivesCollectionDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(PrimitivesCollectionDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getPrimitivesCollectionDescription(TestHelper.PROPERTY_NAME));
    }

    @DisplayName("Test for the exception 'No link description found'")
    @Test
    public void referenceDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(ReferenceDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getReferenceDescription(TestHelper.PROPERTY_NAME));
    }

    @DisplayName("Test for exception 'No description of backlink was found for link'")
    @Test
    public void referenceBackReferenceDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(ReferenceBackReferenceDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getReferenceBackReferenceDescription(TestHelper.PROPERTY_NAME));
    }

    @DisplayName("Test for the exception 'No description of the link collection was found'")
    @Test
    public void referencesCollectionDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(ReferencesCollectionDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getReferencesCollectionDescription(TestHelper.PROPERTY_NAME));
    }

    @DisplayName("Test for exception 'No description of back reference found for link collection'")
    @Test
    public void referencesCollectionBackReferenceDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(ReferencesCollectionBackReferenceDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getReferencesCollectionBackReferenceDescription(TestHelper.PROPERTY_NAME));
    }

    @DisplayName("Test for the exception 'Grouping description not found'")
    @Test
    public void groupDescriptionNotFoundExceptionTest() {
        assertThrowsCausedBy(GroupDescriptionNotFoundException.class, () -> modelDescription.getEntityDescription("Entity").getGroupDescription(TestHelper.PROPERTY_NAME));
    }
}
