package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Condition builder
 */
public abstract class ConditionBuilder extends AbstractBuilder<Condition> {

    /**
     * Get condition
     */
    protected abstract Condition condition();

    @Override
    Condition getExpression() {
        return condition();
    }
}
