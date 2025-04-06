package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

/**
 * Implementation of the condition
 */
class ConditionImpl implements Condition {

    ConditionImpl() {
    }

    @Override
    public Condition not() {
        return Helper.CONDITION;
    }

    @Override
    public Condition and(Condition condition1, Condition... conditions) {
        return Helper.CONDITION;
    }

    @Override
    public Condition or(Condition condition1, Condition... conditions) {
        return Helper.CONDITION;
    }

    @Override
    public PrimitiveExpression asBoolean() {
        return new PrimitiveExpressionImpl(context -> DataType.BOOLEAN);
    }
}
