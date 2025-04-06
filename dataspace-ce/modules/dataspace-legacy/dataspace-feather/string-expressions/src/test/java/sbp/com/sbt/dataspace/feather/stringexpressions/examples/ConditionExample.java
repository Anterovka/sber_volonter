package sbp.com.sbt.dataspace.feather.stringexpressions.examples;

/**
 * Search condition
 */
class ConditionExample {

    /**
     * Execute
     *
     * @return Условие поиска
     */
    String run() {
        // Get condition: (the number of all executions (executionCount) of services (services), whose code matches the pattern 'service%', is greater than 0)
        // and (the owner of the product that the root entity points to is either the owner of the root entity or Ivanov Ivan)
        return "it.services{cond = it.code $like 'service%'}.executionCount.$sum > 0"
            + " && it.product.owner $in [it.owner, 'Ivanov Ivan']";
    }
}
