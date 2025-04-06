package sbp.com.sbt.dataspace.graphqlschema

/**
 * Location of fields for computed expressions
 */
enum class CalcExprFieldsPlacement {
    /**
     The computable expressions are queried through a set of fields _getChar, _getString, ... on each type.
     */
    ON_EACH_TYPE,

    /**
     * Computable expressions are requested via the field *_calc* (of type *_Calculation*) on each type
     * with a set of fields *char, string, ...*
     */
    ON_SEPARATE_TYPE,
}
