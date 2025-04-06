package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;

import java.util.function.Supplier;

/**
 * Computed expression
 */
class CalculatedExpression extends PreparableExpression {

    Supplier<Node<String>> getExpressionStringNodeFunction;

    /**
     * Get
     */
    Node<String> get() {
        return getExpressionStringNodeFunction.get();
    }
}
