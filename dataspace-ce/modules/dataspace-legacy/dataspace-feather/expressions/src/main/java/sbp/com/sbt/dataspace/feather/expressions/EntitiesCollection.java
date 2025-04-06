package sbp.com.sbt.dataspace.feather.expressions;

import java.util.function.Consumer;

/**
 * Collection of entities
 */
public interface EntitiesCollection {

    /**
     * Obtain a collection of primitive expressions based on entity type
     */
    // NotNull
    PrimitiveExpressionsCollection type();

    /**
     * Get a collection of primitive expressions based on entity id
     */
    // NotNull
    PrimitiveExpressionsCollection id();

    /**
     * Obtain a collection of primitive expressions based on the primitive
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitiveExpressionsCollection prim(String propertyName);

    /**
     * Get a collection of entities based on the link
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    EntitiesCollection ref(String propertyName, Consumer<EntitiesCollectionReferenceSpecification> specificationCode);

    /**
     * Get a collection of links based on the link
     *
     * @param propertyName Property name
     */
    // NotNull
    default EntitiesCollection ref(String propertyName) {
        return ref(propertyName, null);
    }

    /**
     * Obtain a collection of entities based on a link with a backlink
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    EntitiesCollection refB(String propertyName, Consumer<EntitiesCollectionBackReferenceReferenceSpecification> specificationCode);

    /**
     * Obtain a collection of entities based on a link with a backlink
     *
     * @param propertyName Property name
     */
    // NotNull
    default EntitiesCollection refB(String propertyName) {
        return refB(propertyName, null);
    }

    /**
     * Get collection of groupings based on grouping
     *
     * @param propertyName Property name
     */
    // NotNull
    GroupsCollection group(String propertyName);

    /**
     * Get collection of primitives based on transformation
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression);

    /**
     * Get number of elements
     */
    // NotNull
    default PrimitiveExpression count() {
        return id().count();
    }

    /**
     * Get condition "Exists"
     */
    // NotNull
    default Condition exists() {
        return id().exists();
    }
}
