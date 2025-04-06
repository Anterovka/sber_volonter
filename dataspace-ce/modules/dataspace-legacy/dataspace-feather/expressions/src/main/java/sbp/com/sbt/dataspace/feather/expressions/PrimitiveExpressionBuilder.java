package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Primitive expression builder
 */
public abstract class PrimitiveExpressionBuilder extends AbstractBuilder<PrimitiveExpression> {

    /**
     * Get primitive expression
     */
    protected abstract PrimitiveExpression primitiveExpression();

    @Override
    PrimitiveExpression getExpression() {
        return primitiveExpression();
    }
}
