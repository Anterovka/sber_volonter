package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Implementation of conditional group
 */
class ConditionalGroupImpl extends CalculatedExpression implements ConditionalGroup {

    DataType type;

    /**
     * @param getPrepareFunctionFunction The function for obtaining the preparation function
     */
    ConditionalGroupImpl(Function<ConditionalGroupImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    /**
     * Get signature
     */
    String getSignature() {
        return "${" + type + "}";
    }
}
