package sbp.com.sbt.dataspace.graphqlschema;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.EntityDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.GroupDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionImpl;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.ModelDescriptionSettings;
import sbp.com.sbt.dataspace.feather.modeldescriptioncommon.PrimitiveDescriptionSettings;

import java.util.Collections;

import static sbp.com.sbt.dataspace.feather.testcommon.TestHelper.assertThrowsCausedBy;

@DisplayName("Testing of incorrect models")
public class IncorrectModelsTest {

    @DisplayName("Test for exception 'The name does not match the regular expression'")
    @Test
    public void nameDoesNotMatchRegExpExceptionTest() {
        assertThrowsCausedBy(NameDoesNotMatchRegExpException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("_Entity", new EntityDescriptionSettings()),
                Collections.singletonList(new GraphQLSchemaModelDescriptionCheck(false, false))));
    }

    @DisplayName("Test for the exception 'Grouping Properties not found'")
    @Test
    public void groupPropertiesNotFoundExceptionTest() {
        assertThrowsCausedBy(GroupPropertiesNotFoundException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setGroupDescriptionSettings("group", new GroupDescriptionSettings()
                        .setGroupName("Group"))),
                Collections.singletonList(new GraphQLSchemaModelDescriptionCheck(false, false))));
    }

    @DisplayName("Test for the exception 'The field name is reserved'")
    @Test
    public void fieldNameReservedExceptionTest() {
        assertThrowsCausedBy(FieldNameReservedException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                        .setPrimitiveDescriptionSettings("id", new PrimitiveDescriptionSettings()
                        .setType(DataType.STRING))),
                Collections.singletonList(new GraphQLSchemaModelDescriptionCheck(false, false))));
    }

    @DisplayName("Test for the exception 'The field name is reserved' (2)")
    @Test
    public void fieldNameReservedExceptionTest2() {
        assertThrowsCausedBy(FieldNameReservedException.class, () -> new ModelDescriptionImpl(new ModelDescriptionSettings()
                .setEntityDescriptionSettings("Entity", new EntityDescriptionSettings()
                .setPrimitiveDescriptionSettings("aggVersion", new PrimitiveDescriptionSettings()
                .setType(DataType.STRING))),
                Collections.singletonList(new GraphQLSchemaModelDescriptionCheck(false, false))));
    }
}
