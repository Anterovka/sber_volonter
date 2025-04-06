package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Collection of primitive expressions
 */
public interface PrimitiveExpressionsCollection {

    /**
     * Get collection of primitives based on transformation
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression);

    /**
     * Get minimum
     */
    // NotNull
    PrimitiveExpression min();

    /**
     * Get maximum
     */
    // NotNull
    PrimitiveExpression max();

    /**
     * Get sum
     */
    // NotNull
    PrimitiveExpression sum();

    /**
     * Get average
     */
    // NotNull
    PrimitiveExpression avg();

    /**
     * Get number of elements
     */
    // NotNull
    PrimitiveExpression count();

    /**
     * Get condition "Exists"
     */
    // NotNull
    Condition exists();
}
