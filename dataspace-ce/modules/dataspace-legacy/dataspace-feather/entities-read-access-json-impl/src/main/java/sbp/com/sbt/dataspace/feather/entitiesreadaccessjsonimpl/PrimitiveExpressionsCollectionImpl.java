package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getConditionStringNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getExpressionWithConditionNode;

/**
 * Implementation of a collection of primitive expressions
 */
class PrimitiveExpressionsCollectionImpl extends PreparableExpression implements PrimitiveExpressionsCollection {

    DataType type;
    Pointer<UnaryOperator<Node<String>>> getTableNodeFunctionPointer;
    Node<String> ownerColumnNode;
    Node<String> elementColumnNode;
    ColumnData ownerColumnData;
    ConditionImpl condition;
    Pointer<Supplier<Node<String>>> getAdditionalConditionNodeFunctionPointer;

    /**
     * @param getPrepareFunctionFunction The function for obtaining the preparation function
     */
    PrimitiveExpressionsCollectionImpl(Function<PrimitiveExpressionsCollectionImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    /**
     * Get signature
     */
    String getSignature() {
        return "${" + type + "}";
    }

    /**
     * Get request node
     *
     * @param function Function
     */
    Node<String> getQueryNode(UnaryOperator<Node<String>> function) {
        List<Node<String>> nodes = new ArrayList<>(11);
        nodes.add(Helper.BRACKET_L_SELECT_NODE);
        nodes.add(function.apply(elementColumnNode));
        nodes.add(Helper.FROM_NODE);
        nodes.add(getTableNodeFunctionPointer.object.apply(null));
        if (ownerColumnData != null) {
            nodes.add(Helper.WHERE_NODE);
            nodes.add(ownerColumnData.columnNode);
            nodes.add(Helper.EQ_NODE);
            nodes.add(ownerColumnNode);
        }
        if (condition != null) {
            nodes.add(ownerColumnData == null ? Helper.WHERE_NODE : Helper.AND_NODE);
            nodes.add(condition.get());
        }
        if (getAdditionalConditionNodeFunctionPointer != null && getAdditionalConditionNodeFunctionPointer.object != null) {
            nodes.add(Helper.AND_NODE);
            nodes.add(getAdditionalConditionNodeFunctionPointer.object.get());
        }
        nodes.add(Helper.BRACKET_R_NODE);
        return node(nodes);
    }

    /**
     * Get primitive expression
     *
     * @param code Код
     */
    PrimitiveExpressionImpl getPrimitiveExpression(Consumer<PrimitiveExpressionImpl> code) {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression.getConditionStringNodeFunction = getConditionStringNodeFunction;
            code.accept(primitiveExpression);
        });
    }

    @Override
    public PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            PrimitiveExpressionImpl primitiveExpressionImpl = (PrimitiveExpressionImpl) primitiveExpression;
            ExpressionContext expressionContext2 = new ExpressionContext(elementColumnNode, type);
            expressionContext2.aliasedEntitiesData = expressionContext.aliasedEntitiesData;
            primitiveExpressionImpl.prepare(sqlQueryProcessor, expressionContext2);
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = primitiveExpressionImpl.type;
            primitiveExpressionsCollection.getTableNodeFunctionPointer = getTableNodeFunctionPointer;
            primitiveExpressionsCollection.ownerColumnNode = ownerColumnNode;
            primitiveExpressionsCollection.elementColumnNode = getExpressionWithConditionNode(sqlQueryProcessor.requestData.sqlDialect, primitiveExpressionImpl.get(), primitiveExpressionImpl.getConditionStringNodeFunction.get());
            primitiveExpressionsCollection.ownerColumnData = ownerColumnData;
            primitiveExpressionsCollection.condition = condition;
            primitiveExpressionsCollection.getAdditionalConditionNodeFunctionPointer = getAdditionalConditionNodeFunctionPointer;
        });
    }

    @Override
    public PrimitiveExpression min() {
        return getPrimitiveExpression(primitiveExpression -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.type = type;
            } else if (type == DataType.TIME) {
                primitiveExpression.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$min");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> getQueryNode(elementColumn2 -> node(Helper.MIN_BRACKET_L_NODE, elementColumn2, Helper.BRACKET_R_NODE));
        });
    }

    @Override
    public PrimitiveExpression max() {
        return getPrimitiveExpression(primitiveExpression -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.type = type;
            } else if (type == DataType.TIME) {
                primitiveExpression.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$max");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> getQueryNode(elementColumn2 -> node(Helper.MAX_BRACKET_L_NODE, elementColumn2, Helper.BRACKET_R_NODE));
        });
    }

    @Override
    public PrimitiveExpression sum() {
        return getPrimitiveExpression(primitiveExpression -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$sum");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> getQueryNode(elementColumn2 -> node(Helper.SUM_BRACKET_L_NODE, elementColumn2, Helper.BRACKET_R_NODE));
        });
    }

    @Override
    public PrimitiveExpression avg() {
        return getPrimitiveExpression(primitiveExpression -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$avg");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> getQueryNode(elementColumn2 -> node(Helper.AVG_BRACKET_L_NODE, elementColumn2, Helper.BRACKET_R_NODE));
        });
    }

    @Override
    public PrimitiveExpression count() {
        return getPrimitiveExpression(primitiveExpression -> {
            primitiveExpression.type = DataType.BIG_DECIMAL;
            primitiveExpression.getExpressionStringNodeFunction = () -> getQueryNode(elementColumn2 -> Helper.COUNT_ASTERISK_NODE);
        });
    }

    @Override
    public Condition exists() {
        return new ConditionImpl(condition2 -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            condition2.getExpressionStringNodeFunction = () -> getConditionStringNode(node(Helper.EXISTS_NODE, getQueryNode(UnaryOperator.identity())), this);
        });
    }
}
