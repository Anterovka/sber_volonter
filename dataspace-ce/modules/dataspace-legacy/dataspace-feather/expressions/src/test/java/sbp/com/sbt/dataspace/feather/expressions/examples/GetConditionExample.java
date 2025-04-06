package sbp.com.sbt.dataspace.feather.expressions.examples;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionBuilder;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;

/**
 * Getting condition
 */
class GetConditionExample {

    /**
     * Execute
     *
     * @param expressionsProcessor The expression processor
     * @return Condition
     */
    Condition run(ExpressionsProcessor expressionsProcessor) {
// Get condition: (number of executions (executionCount) of services (services), whose code matches the pattern 'service%', is greater than 0)
// and (the owner of the product that the root entity points to is either the owner of the root entity or Ivanov Ivan)
        return new ConditionBuilder() {
            @Override
            protected Condition condition() {
                return root().refs("services", specification -> specification.setCondition(
                        elemE().prim("code").like(prim("service%")))).prim("executionCount").sum().gt(prim(0))
                    .and(root().ref("product").prim("owner").in(root().prim("owner"), prim("Иванов Иван")));
            }
        }.build(expressionsProcessor);
    }
}
