package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Primitive expression
 */
public interface PrimitiveExpression {

    /**
     * Get negation
     */
    // NotNull
    PrimitiveExpression neg();

    /**
     * Get in uppercase
     */
    // NotNull
    PrimitiveExpression upper();

    /**
     * Get in lowercase
     */
    // NotNull
    PrimitiveExpression lower();

    /**
     * Get length
     */
    // NotNull
    PrimitiveExpression length();

    /**
     * Get without whitespace characters at the beginning and end
     */
    // NotNull
    PrimitiveExpression trim();

    /**
     * Get rid of leading whitespace characters
     */
    // NotNull
    PrimitiveExpression ltrim();

    /**
     * Get without whitespace characters at the end
     */
    // NotNull
    PrimitiveExpression rtrim();

    /**
     * Get rounding
     */
    // NotNull
    PrimitiveExpression round();

    /**
     * Get ceiling rounding
     */
    // NotNull
    PrimitiveExpression ceil();

    /**
     * Get the down rounding
     */
    // NotNull
    PrimitiveExpression floor();

    /**
     * Get hash
     */
    // NotNull
    PrimitiveExpression hash();

    /**
     * Get as string
     */
    // NotNull
    PrimitiveExpression asString();

    /**
     * Get as a large decimal number
     */
    // NotNull
    PrimitiveExpression asBigDecimal();

    /**
     * Get as date
     */
    // NotNull
    PrimitiveExpression asDate();

    /**
     * Get as date and time
     */
    // NotNull
    PrimitiveExpression asDateTime();

    /**
     * Get as date and time with offset
     */
    // NotNull
    PrimitiveExpression asOffsetDateTime();

    /**
     * Get as time
     */
    // NotNull
    PrimitiveExpression asTime();

    /**
     * Year
     */
    // NotNull
    PrimitiveExpression year();

    /**
     * Month
     */
    // NotNull
    PrimitiveExpression month();

    /**
     * Day
     */
    // NotNull
    PrimitiveExpression day();

    /**
     * Hour
     */
    // NotNull
    PrimitiveExpression hour();

    /**
     * Minute
     */
    // NotNull
    PrimitiveExpression minute();

    /**
     * Second
     */
    // NotNull
    PrimitiveExpression second();

    /**
     * Offset hour
     */
    // NotNull
    PrimitiveExpression offsetHour();

    /**
     * Offset minute
     */
    // NotNull
    PrimitiveExpression offsetMinute();

    /**
     * Date
     */
    // NotNull
    PrimitiveExpression date();

    /**
     * Time
     */
    // NotNull
    PrimitiveExpression time();

    /**
     * Date and time
     */
    // NotNull
    PrimitiveExpression dateTime();

    /**
     * Offset
     */
    // NotNull
    PrimitiveExpression offset();

    /**
     * Get the absolute value of the number
     */
    // NotNull
    PrimitiveExpression abs();

    /**
     * Get bitwise negation
     */
    // NotNull
    PrimitiveExpression bitNot();

    /**
     * Get bitwise "AND"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression bitAnd(PrimitiveExpression primitiveExpression);

    /**
     * Get bitwise "Or"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression bitOr(PrimitiveExpression primitiveExpression);

    /**
     * Get bitwise "Exclusive or"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression bitXor(PrimitiveExpression primitiveExpression);

    /**
     * Get shift left
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression shiftLeft(PrimitiveExpression primitiveExpression);

    /**
     * Get shift right
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression shiftRight(PrimitiveExpression primitiveExpression);

    /**
     * Get addition
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    PrimitiveExpression plus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get difference
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    PrimitiveExpression minus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get the product
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    PrimitiveExpression mul(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get division
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    PrimitiveExpression div(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get substring
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpression2 Primitive expression 2
     */
    // NotNull
    PrimitiveExpression substr(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2);

    /**
     * Get substring
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression substr(PrimitiveExpression primitiveExpression);

    /**
     * Get replacement
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpression2 Primitive expression 2
     */
    // NotNull
    PrimitiveExpression replace(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2);

    /**
     * Get string with left padding
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpression2 Primitive expression 2
     */
    // NotNull
    PrimitiveExpression lpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2);

    /**
     * Get string with right padding
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpression2 Primitive expression 2
     */
    // NotNull
    PrimitiveExpression rpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2);

    /**
     * Get millisecond addition
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addMilliseconds(PrimitiveExpression primitiveExpression);

    /**
     * Get the increase in seconds
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addSeconds(PrimitiveExpression primitiveExpression);

    /**
     * Get minutes addition
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addMinutes(PrimitiveExpression primitiveExpression);

    /**
     * Get an increase in hours
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addHours(PrimitiveExpression primitiveExpression);

    /**
     * Get days increase
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addDays(PrimitiveExpression primitiveExpression);

    /**
     * Get an increase in months
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addMonths(PrimitiveExpression primitiveExpression);

    /**
     * Obtain an increase in years
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression addYears(PrimitiveExpression primitiveExpression);

    /**
     * Get millisecond subtraction
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subMilliseconds(PrimitiveExpression primitiveExpression);

    /**
     * Get the subtraction of seconds
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subSeconds(PrimitiveExpression primitiveExpression);

    /**
     * Get minutes subtraction
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subMinutes(PrimitiveExpression primitiveExpression);

    /**
     * Get hours subtraction
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subHours(PrimitiveExpression primitiveExpression);

    /**
     * Get days subtraction
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subDays(PrimitiveExpression primitiveExpression);

    /**
     * Get subtraction of months
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subMonths(PrimitiveExpression primitiveExpression);

    /**
     * Get subtraction of years
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    PrimitiveExpression subYears(PrimitiveExpression primitiveExpression);

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

    /**
     * Get condition "Equals"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition eq(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "Equals"
     *
     * @param conditionalGroup Conditional group
     */
    // NotNull
    Condition eq(ConditionalGroup conditionalGroup);

    /**
     * Get condition "Not equal to"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition notEq(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "Not equal to"
     *
     * @param conditionalGroup Conditional group
     */
    // NotNull
    Condition notEq(ConditionalGroup conditionalGroup);

    /**
     * Get condition "More"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition gt(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "More"
     *
     * @param conditional group
     */
    // NotNull
    Condition gt(ConditionalGroup conditionalGroup);

    /**
     * Get condition "Less than or equal to"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition ltOrEq(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "Less than or equal to"
     *
     * @param conditionalGroup Conditional group
     */
    // NotNull
    Condition ltOrEq(ConditionalGroup conditionalGroup);

    /**
     * Get condition "Less"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition lt(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "Less"
     *
     * @param conditionalGroup Conditional group
     */
    // NotNull
    Condition lt(ConditionalGroup conditionalGroup);

    /**
     * Get condition "Greater than or equal to"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition gtOrEq(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "Greater than or equal to"
     *
     * @param conditionalGroup Conditional group
     */
    // NotNull
    Condition gtOrEq(ConditionalGroup conditionalGroup);

    /**
     * Get condition "Similar"
     *
     * @param primitiveExpression Primitive expression
     */
    // NotNull
    Condition like(PrimitiveExpression primitiveExpression);

    /**
     * Get condition "Between"
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpression2 Primitive expression 2
     */
    // NotNull
    Condition between(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2);

    /**
     * Get condition "B"
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    Condition in(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get condition "B"
     *
     * @param collection of primitive expressions
     */
    // NotNull
    Condition in(PrimitiveExpressionsCollection primitiveExpressionsCollection);

    /**
     * Get the remainder from division
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    PrimitiveExpression mod(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get raising to the power
     *
     * @param степень Power
     */
    PrimitiveExpression power(PrimitiveExpression power);

    /**
     * Get logarithm
     *
     * @param base Base
     */
    PrimitiveExpression log(PrimitiveExpression base);

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
}
