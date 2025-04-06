package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;

import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.checkImpl;

/**
 * Implementation of a collection of primitive expressions
 */
class PrimitiveExpressionsCollectionImpl extends StringBasedObject implements PrimitiveExpressionsCollection {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    PrimitiveExpressionsCollectionImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionsCollectionImpl(node(stringNode, Helper.MAP_NODE, checkImpl(primitiveExpression).stringNode, Helper.BRACKET_R_NODE), Priority.VALUE);
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

    @Override
    public Condition exists() {
        return getSystemCondition(Helper.EXISTS_NODE);
    }


}
