package sbp.com.sbt.dataspace.feather.expressions;

import java.util.function.Consumer;

/**
 * Grouping
 */
public interface Group {

    /**
     * Get primitive
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitiveExpression prim(String propertyName);

    /**
     * Get link
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    Entity ref(String propertyName, Consumer<GroupReferenceSpecification> specificationCode);

    /**
     * Get link
     *
     * @param propertyName Property name
     */
    // NotNull
    default Entity ref(String propertyName) {
        return ref(propertyName, null);
    }

    /**
     * Get condition "Equals null"
     */
    // NotNull
    Condition isNull();

    /**
     * Get condition "Not equal to null"
     */
    // NotNull
    Condition isNotNull();
}
