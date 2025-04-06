package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;

import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression;

/**
 * Implementation of the condition
 */
class ConditionImpl extends StringBasedObject implements Condition {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    ConditionImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public Condition not() {
        return getExpression(ConditionImpl::new, Helper.NOT_NODE, Priority.LOGICAL_NOT, this);
    }

    @Override
    public Condition and(Condition condition1, Condition... conditions) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.AND_NODE, Priority.LOGICAL_AND, true, this, condition1, conditions);
    }

    @Override
    public Condition or(Condition condition1, Condition... conditions) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.OR_NODE, Priority.LOGICAL_OR, true, this, condition1, conditions);
    }

    @Override
    public PrimitiveExpression asBoolean() {
        return getSystemPrimitiveExpression(Helper.AS_BOOLEAN_NODE);
    }
}
