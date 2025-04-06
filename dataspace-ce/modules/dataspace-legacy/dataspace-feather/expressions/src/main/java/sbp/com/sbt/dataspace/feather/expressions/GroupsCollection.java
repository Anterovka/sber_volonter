package sbp.com.sbt.dataspace.feather.expressions;

import java.util.function.Consumer;

/**
 * Collection of groupings
 */
public interface GroupsCollection {

    /**
     * Get a collection of primitive expressions based on the primitive
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitiveExpressionsCollection prim(String propertyName);

    /**
     * Obtain a collection of entities based on the link
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    EntitiesCollection ref(String propertyName, Consumer<GroupsCollectionReferenceSpecification> specificationCode);

    /**
     * Get a collection of entities based on the link
     *
     * @param propertyName Property name
     */
    // NotNull
    default EntitiesCollection ref(String propertyName) {
        return ref(propertyName, null);
    }
}
