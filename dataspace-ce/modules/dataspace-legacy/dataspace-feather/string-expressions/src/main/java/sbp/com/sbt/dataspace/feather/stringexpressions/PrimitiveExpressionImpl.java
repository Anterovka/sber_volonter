package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;

import java.util.ArrayList;
import java.util.List;

import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.checkImpl;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getCondition;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression2;

/**
 * Implementation of a primitive expression
 */
class PrimitiveExpressionImpl extends StringBasedObject implements PrimitiveExpression {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    PrimitiveExpressionImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public PrimitiveExpression neg() {
        return getExpression(PrimitiveExpressionImpl::new, Helper.MINUS_NODE, Priority.UNARY_OPERATIONS, this);
    }

    @Override
    public PrimitiveExpression upper() {
        return getSystemPrimitiveExpression(Helper.UPPER_NODE);
    }

    @Override
    public PrimitiveExpression lower() {
        return getSystemPrimitiveExpression(Helper.LOWER_NODE);
    }

    @Override
    public PrimitiveExpression length() {
        return getSystemPrimitiveExpression(Helper.LENGTH_NODE);
    }

    @Override
    public PrimitiveExpression trim() {
        return getSystemPrimitiveExpression(Helper.TRIM_NODE);
    }

    @Override
    public PrimitiveExpression ltrim() {
        return getSystemPrimitiveExpression(Helper.LTRIM_NODE);
    }

    @Override
    public PrimitiveExpression rtrim() {
        return getSystemPrimitiveExpression(Helper.RTRIM_NODE);
    }

    @Override
    public PrimitiveExpression round() {
        return getSystemPrimitiveExpression(Helper.ROUND_NODE);
    }

    @Override
    public PrimitiveExpression ceil() {
        return getSystemPrimitiveExpression(Helper.CEIL_NODE);
    }

    @Override
    public PrimitiveExpression floor() {
        return getSystemPrimitiveExpression(Helper.FLOOR_NODE);
    }

    @Override
    public PrimitiveExpression hash() {
        return getSystemPrimitiveExpression(Helper.HASH_NODE);
    }

    @Override
    public PrimitiveExpression asString() {
        return getSystemPrimitiveExpression(Helper.AS_STRING_NODE);
    }

    @Override
    public PrimitiveExpression asBigDecimal() {
        return getSystemPrimitiveExpression(Helper.AS_BIG_DECIMAL_NODE);
    }

    @Override
    public PrimitiveExpression asDate() {
        return getSystemPrimitiveExpression(Helper.AS_DATE_NODE);
    }

    @Override
    public PrimitiveExpression asDateTime() {
        return getSystemPrimitiveExpression(Helper.AS_DATETIME_NODE);
    }

    @Override
    public PrimitiveExpression asOffsetDateTime() {
        return getSystemPrimitiveExpression(Helper.AS_OFFSET_DATETIME_NODE);
    }

    @Override
    public PrimitiveExpression asTime() {
        return getSystemPrimitiveExpression(Helper.AS_TIME_NODE);
    }

    @Override
    public PrimitiveExpression year() {
        return getSystemPrimitiveExpression(Helper.YEAR_NODE);
    }

    @Override
    public PrimitiveExpression month() {
        return getSystemPrimitiveExpression(Helper.MONTH_NODE);
    }

    @Override
    public PrimitiveExpression day() {
        return getSystemPrimitiveExpression(Helper.DAY_NODE);
    }

    @Override
    public PrimitiveExpression hour() {
        return getSystemPrimitiveExpression(Helper.HOUR_NODE);
    }

    @Override
    public PrimitiveExpression minute() {
        return getSystemPrimitiveExpression(Helper.MINUTE_NODE);
    }

    @Override
    public PrimitiveExpression second() {
        return getSystemPrimitiveExpression(Helper.SECOND_NODE);
    }

    @Override
    public PrimitiveExpression offsetHour() {
        return getSystemPrimitiveExpression(Helper.OFFSET_HOUR_NODE);
    }

    @Override
    public PrimitiveExpression offsetMinute() {
        return getSystemPrimitiveExpression(Helper.OFFSET_MINUTE_NODE);
    }

    @Override
    public PrimitiveExpression date() {
        return getSystemPrimitiveExpression(Helper.DATE_NODE);
    }

    @Override
    public PrimitiveExpression time() {
        return getSystemPrimitiveExpression(Helper.TIME_NODE);
    }

    @Override
    public PrimitiveExpression dateTime() {
        return getSystemPrimitiveExpression(Helper.DATETIME_NODE);
    }

    @Override
    public PrimitiveExpression offset() {
        return getSystemPrimitiveExpression(Helper.OFFSET_NODE);
    }

    @Override
    public PrimitiveExpression abs() {
        return getSystemPrimitiveExpression(Helper.ABS_NODE);
    }

    @Override
    public PrimitiveExpression bitNot() {
        return getExpression(PrimitiveExpressionImpl::new, Helper.BIT_NOT_NODE, Priority.UNARY_OPERATIONS, this);
    }

    @Override
    public PrimitiveExpression bitAnd(PrimitiveExpression primitiveExpression) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.BIT_AND_NODE, Priority.BITWISE_AND, true, this, primitiveExpression);
    }

    @Override
    public PrimitiveExpression bitOr(PrimitiveExpression primitiveExpression) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.BIT_OR_NODE, Priority.BITWISE_OR, true, this, primitiveExpression);
    }

    @Override
    public PrimitiveExpression bitXor(PrimitiveExpression primitiveExpression) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.BIT_XOR_NODE, Priority.BITWISE_XOR, true, this, primitiveExpression);
    }

    @Override
    public PrimitiveExpression shiftLeft(PrimitiveExpression primitiveExpression) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.SHIFT_LEFT_NODE, Priority.SHIFT, false, this, primitiveExpression);
    }

    @Override
    public PrimitiveExpression shiftRight(PrimitiveExpression primitiveExpression) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.SHIFT_RIGHT_NODE, Priority.SHIFT, false, this, primitiveExpression);
    }

    @Override
    public PrimitiveExpression plus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.PLUS_NODE, Priority.ADDITIVE, true, this, primitiveExpression1, primitiveExpressions);
    }

    @Override
    public PrimitiveExpression minus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.MINUS_NODE, Priority.ADDITIVE, false, this, primitiveExpression1, primitiveExpressions);
    }

    @Override
    public PrimitiveExpression mul(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.MUL_NODE, Priority.MULTIPLICATIVE, true, this, primitiveExpression1, primitiveExpressions);
    }

    @Override
    public PrimitiveExpression div(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.DIV_NODE, Priority.MULTIPLICATIVE, false, this, primitiveExpression1, primitiveExpressions);
    }

    @Override
    public PrimitiveExpression substr(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getSystemPrimitiveExpression(Helper.SUBSTR_NODE, checkImpl(primitiveExpression1).stringNode, checkImpl(primitiveExpression2).stringNode);
    }

    @Override
    public PrimitiveExpression substr(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUBSTR_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression replace(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getSystemPrimitiveExpression(Helper.REPLACE_NODE, checkImpl(primitiveExpression1).stringNode, checkImpl(primitiveExpression2).stringNode);
    }

    @Override
    public PrimitiveExpression lpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getSystemPrimitiveExpression(Helper.LPAD_NODE, checkImpl(primitiveExpression1).stringNode, checkImpl(primitiveExpression2).stringNode);
    }

    @Override
    public PrimitiveExpression rpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getSystemPrimitiveExpression(Helper.RPAD_NODE, checkImpl(primitiveExpression1).stringNode, checkImpl(primitiveExpression2).stringNode);
    }

    @Override
    public PrimitiveExpression addMilliseconds(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_MILLISECONDS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression addSeconds(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_SECONDS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression addMinutes(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_MINUTES_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression addHours(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_HOURS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression addDays(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_DAYS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression addMonths(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_MONTHS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression addYears(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.ADD_YEARS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subMilliseconds(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_MILLISECONDS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subSeconds(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_SECONDS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subMinutes(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_MINUTES_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subHours(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_HOURS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subDays(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_DAYS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subMonths(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_MONTHS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public PrimitiveExpression subYears(PrimitiveExpression primitiveExpression) {
        return getSystemPrimitiveExpression(Helper.SUB_YEARS_NODE, checkImpl(primitiveExpression).stringNode);
    }

    @Override
    public Condition isNull() {
        return getExpression2(ConditionImpl::new, Helper.IS_NULL_NODE, Priority.EQUALITY_AND_RELATION, this);
    }

    @Override
    public Condition isNotNull() {
        return getExpression2(ConditionImpl::new, Helper.IS_NOT_NULL_NODE, Priority.EQUALITY_AND_RELATION, this);
    }

    @Override
    public Condition eq(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition eq(ConditionalGroup conditionalGroup) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, conditionalGroup);
    }

    @Override
    public Condition notEq(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.NOT_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition notEq(ConditionalGroup conditionalGroup) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.NOT_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, conditionalGroup);
    }

    @Override
    public Condition gt(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.GT_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition gt(ConditionalGroup conditionalGroup) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.GT_NODE, Priority.EQUALITY_AND_RELATION, true, this, conditionalGroup);
    }

    @Override
    public Condition ltOrEq(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.LT_OR_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition ltOrEq(ConditionalGroup conditionalGroup) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.LT_OR_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, conditionalGroup);
    }

    @Override
    public Condition lt(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.LT_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition lt(ConditionalGroup conditionalGroup) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.LT_NODE, Priority.EQUALITY_AND_RELATION, true, this, conditionalGroup);
    }

    @Override
    public Condition gtOrEq(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.GT_OR_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition gtOrEq(ConditionalGroup conditionalGroup) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.GT_OR_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, conditionalGroup);
    }

    @Override
    public Condition like(PrimitiveExpression primitiveExpression) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.LIKE_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpression);
    }

    @Override
    public Condition between(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        List<Node<String>> nodes = new ArrayList<>(7);
        addStringNode2(nodes, Priority.EQUALITY_AND_RELATION);
        nodes.add(Helper.BETWEEN_NODE);
        nodes.add(Helper.BRACKET_L_NODE);
        nodes.add(checkImpl(primitiveExpression1).stringNode);
        nodes.add(Helper.COMMA_NODE);
        nodes.add(checkImpl(primitiveExpression2).stringNode);
        nodes.add(Helper.BRACKET_R_NODE);
        return new ConditionImpl(node(nodes), Priority.EQUALITY_AND_RELATION);
    }

    @Override
    public Condition in(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getCondition(Helper::checkImpl, Helper.IN_NODE, this, primitiveExpression1, primitiveExpressions);
    }

    @Override
    public Condition in(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.IN_NODE, Priority.EQUALITY_AND_RELATION, true, this, primitiveExpressionsCollection);
    }

    @Override
    public PrimitiveExpression mod(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getExpression(PrimitiveExpressionImpl::new, Helper::checkImpl, Helper.MOD_NODE, Priority.MULTIPLICATIVE, false, this, primitiveExpression1, primitiveExpressions);
    }

    @Override
    public PrimitiveExpression power(PrimitiveExpression power) {
        return getSystemPrimitiveExpression(Helper.POWER_NODE, checkImpl(power).stringNode);
    }

    @Override
    public PrimitiveExpression log(PrimitiveExpression base) {
        return getSystemPrimitiveExpression(Helper.LOG_NODE, checkImpl(base).stringNode);
    }

    @Override
    public PrimitiveExpression min() {
        return getSystemPrimitiveExpression(Helper.MIN_NODE);
    }

    @Override
    public PrimitiveExpression max() {
        return getSystemPrimitiveExpression(Helper.MAX_NODE);
    }

    @Override
    public PrimitiveExpression sum() {
        return getSystemPrimitiveExpression(Helper.SUM_NODE);
    }

    @Override
    public PrimitiveExpression avg() {
        return getSystemPrimitiveExpression(Helper.AVG_NODE);
    }

    @Override
    public PrimitiveExpression count() {
        return getSystemPrimitiveExpression(Helper.COUNT_NODE);
    }
}
