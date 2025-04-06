package sbp.com.sbt.dataspace.feather.stringexpressions;

/**
* Type of expression
 */
enum ExpressionType {

    /**
     * Primitive expression
     */
PRIMITIVE_EXPRESSION("Primitive expression"),
    /**
     * Collection of primitive expressions
     */
PRIMITIVE_EXPRESSIONS_COLLECTION("Collection of primitive expressions"),
    /**
     * Entity
     */
ENTITY("Entity"),
    /**
     * Collection of entities
     */
ENTITIES_COLLECTION("Entity Collection"),

    /**
     * Condition
     */
CONDITION("Condition"),

    /**
     * Grouping
     */
GROUP("Grouping"),

    /**
     * Collection of groupings
     */
GROUPS_COLLECTION("Grouping Collection"),

    /**
     * Conditional group
     */
CONDITIONAL_GROUP("Conditional group");

    String description;

    /**
     * @param description Description
     */
    ExpressionType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "${" + description + "}";
    }
}
