package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Condition
 */
public interface Condition {

    /**
     * Get condition "Negation"
     */
    // NotNull
    Condition not();

    /**
     * Get condition "AND"
     *
     * @param condition1 Condition 2
     * @param conditions Conditions
     */
    // NotNull
    Condition and(Condition condition1, Condition... conditions);

    /**
     * Get condition "Or"
     *
     * @param condition1 Condition 2
     * @param conditions The terms
     */
    // NotNull
    Condition or(Condition condition1, Condition... conditions);

    /**
     * Get as a Boolean value
     */
    PrimitiveExpression asBoolean();
}
