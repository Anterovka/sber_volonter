package sbp.com.sbt.dataspace.feather.testmodeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checking the grouping description
 */
public class GroupDescriptionCheck extends AbstractCheck {

    EntityDescription entityDescription;
    GroupDescription groupDescription;
    String name;
    String groupName;
    Map<String, GroupPrimitiveDescriptionCheck> primitiveDescriptionChecks = new LinkedHashMap<>();
    Map<String, GroupReferenceDescriptionCheck> referenceDescriptionChecks = new LinkedHashMap<>();

    /**
     * @param entityDescription Entity description
     * @param name              Name
     */
    GroupDescriptionCheck(EntityDescription entityDescription, String name) {
        this.entityDescription = entityDescription;
        groupDescription = entityDescription.getDeclaredGroupDescriptions().get(name);
        this.name = name;
    }

    public GroupDescriptionCheck setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    /**
     * Set check of primitive description
     *
     * @param propertyName Property name
     * @param initializer  Инициализатор проверки
     * @return Current check
     */
    public GroupDescriptionCheck setPrimitiveDescriptionCheck(String propertyName, Consumer<GroupPrimitiveDescriptionCheck> checkInitializer) {
        checkInitializer.accept(primitiveDescriptionChecks.computeIfAbsent(propertyName, key -> new GroupPrimitiveDescriptionCheck(groupDescription, propertyName)));
        return this;
    }

    /**
     * Set check for link description
     *
     * @param propertyName Property name
     * @param checkInitializer  Инициализатор проверки
     * @return Current check
     */
    public GroupDescriptionCheck setReferenceDescriptionCheck(String propertyName, Consumer<GroupReferenceDescriptionCheck> checkInitializer) {
        checkInitializer.accept(referenceDescriptionChecks.computeIfAbsent(propertyName, key -> new GroupReferenceDescriptionCheck(groupDescription, propertyName)));
        return this;
    }

    @Override
    void check() {
        assertNotNull(groupDescription);

        assertEquals(entityDescription.getGroupDescription(name), groupDescription);
        assertEquals(entityDescription.getGroupDescriptions().get(name), groupDescription);

        assertEquals(entityDescription, groupDescription.getOwnerEntityDescription());
        assertEquals(name, groupDescription.getName());
        assertEquals(groupName, groupDescription.getGroupName());
        Helper.check(groupDescription.getPrimitiveDescriptions(), primitiveDescriptionChecks);
        Helper.check(groupDescription.getReferenceDescriptions(), referenceDescriptionChecks);
        assertTrue(groupDescription.getOwnerEntityDescription().getModelDescription().getGroupDescriptions().get(groupName).contains(groupDescription));
    }
}
