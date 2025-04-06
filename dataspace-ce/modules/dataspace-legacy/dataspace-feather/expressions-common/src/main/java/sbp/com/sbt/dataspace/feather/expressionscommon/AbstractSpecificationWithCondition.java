package sbp.com.sbt.dataspace.feather.expressionscommon;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithCondition;

/**
 * Abstract specification with condition
 *
 * @param <S> Type of specification
 */
class AbstractSpecificationWithCondition<S> implements SpecificationWithCondition<S> {

    Condition condition;

    @Override
    public Condition getCondition() {
        return condition;
    }

    @Override
    public S setCondition(Condition condition) {
        this.condition = condition;
        return (S) this;
    }
}
