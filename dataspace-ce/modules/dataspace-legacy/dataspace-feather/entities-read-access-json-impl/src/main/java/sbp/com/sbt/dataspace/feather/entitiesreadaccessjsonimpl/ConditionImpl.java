package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetExpressionStringNodeFunction;

/**
 * Implementation of the condition
 */
class ConditionImpl extends CalculatedExpression implements Condition {

    /**
     * @param getPrepareFunctionFunction The function of obtaining the preparation function
     */
    ConditionImpl(Function<ConditionImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        this.prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    /**
     * Get condition
     *
     * @param operationNode Operation node
     * @param condition1    Condition 2
     */
    ConditionImpl getCondition(Node<String> operationNode, Condition condition1) {
        ConditionImpl condition1Impl = (ConditionImpl) condition1;
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            condition1Impl.prepare(sqlQueryProcessor, expressionContext);
            condition.getExpressionStringNodeFunction = getGetExpressionStringNodeFunction(operationNode, this, condition1Impl);
        });
    }

    @Override
    public Condition not() {
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            condition.getExpressionStringNodeFunction = () -> node(Helper.NOT_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
        });
    }

    @Override
    public Condition and(Condition condition1, Condition... conditions) {
        return getCondition(Helper.AND_NODE, condition1);
    }

    @Override
    public Condition or(Condition condition1, Condition... conditions) {
        return getCondition(Helper.OR_NODE, condition1);
    }

    @Override
    public PrimitiveExpression asBoolean() {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression.getExpressionStringNodeFunction = () -> sqlQueryProcessor.requestData.sqlDialect.castAsBoolean(get());
            primitiveExpression.type = DataType.BOOLEAN;
        });
    }
}
