package sbp.com.sbt.dataspace.feather.stringexpressions;

/**
 * Priority
 */
enum Priority {
    /**
     * Value
     */
    VALUE,
    /**
     * Unary operations (minus, bitwise negation)
     */
    UNARY_OPERATIONS,
    /**
     * Multiplication
     */
    MULTIPLICATIVE,
    /**
     * Addition
     */
    ADDITIVE,
    /**
     * Shift
     */
    SHIFT,
    /**
     * Equality and ratio
     */
    EQUALITY_AND_RELATION,
    /**
     * Bitwise "AND"
     */
    BITWISE_AND,
    /**
     * Bitwise "Exclusive or"
     */
    BITWISE_XOR,
    /**
     * Bitwise "Or"
     */
    BITWISE_OR,
    /**
     * Logical negation
     */
    LOGICAL_NOT,
    /**
     * Logical "AND"
     */
    LOGICAL_AND,
    /**
     * Logical "Or"
     */
    LOGICAL_OR
}
