package sbp.com.sbt.dataspace.feather.expressions.examples;

import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionBuilder;

/**
 * Primitive expression retrieval
 */
class GetPrimitiveExpressionExample {

    /**
     * Execute
     *
     * @param expressionsProcessor The expression processor
     * @return Primitive expression
     */
    PrimitiveExpression run(ExpressionsProcessor expressionsProcessor) {
        // Get the number of services (services) that have a code (code) matching the pattern 'service%'
        return new PrimitiveExpressionBuilder() {
            @Override
            protected PrimitiveExpression primitiveExpression() {
                return root().refs("services", specification -> specification.setCondition(
                    elemE().prim("code").like(prim("service%")))).count();
            }
        }.build(expressionsProcessor);
    }
}
