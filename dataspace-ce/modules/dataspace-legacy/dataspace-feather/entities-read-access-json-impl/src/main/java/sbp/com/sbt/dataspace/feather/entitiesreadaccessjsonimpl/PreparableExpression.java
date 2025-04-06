package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Expression requiring preparation
 */
class PreparableExpression {

    BiConsumer<SqlQueryProcessor, ExpressionContext> prepareFunction;
    Supplier<Node<String>> getConditionStringNodeFunction = () -> null;

    /**
     * Prepare
     *
     * @param sqlQueryProcessor The SQL query handler
     * @param expressionContext The context of the expression
     */
    void prepare(SqlQueryProcessor sqlQueryProcessor, ExpressionContext expressionContext) {
        prepareFunction.accept(sqlQueryProcessor, expressionContext);
    }
}
