package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getAvgType;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getMaxType;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getMinType;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getSumType;

/**
 * Implementation of a collection of primitive expressions
 */
class PrimitiveExpressionsCollectionImpl implements PrimitiveExpressionsCollection {

    Function<Context, DataType> getTypeFunction;

    /**
     * @param getTypeFunction The function for obtaining the type depending on the context
     */
    PrimitiveExpressionsCollectionImpl(Function<Context, DataType> getTypeFunction) {
        this.getTypeFunction = getTypeFunction;
    }

    @Override
    public PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionsCollectionImpl(context -> ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(new Context(getTypeFunction.apply(context)).withAliasedEntityDescriptions(context.aliasedEntityDescriptions)));
    }

    @Override
    public PrimitiveExpression min() {
        return new PrimitiveExpressionImpl(context -> getMinType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression max() {
        return new PrimitiveExpressionImpl(context -> getMaxType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression sum() {
        return new PrimitiveExpressionImpl(context -> getSumType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression avg() {
        return new PrimitiveExpressionImpl(context -> getAvgType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression count() {
        return new PrimitiveExpressionImpl(context -> DataType.BIG_DECIMAL);
    }

    @Override
    public Condition exists() {
        return Helper.CONDITION;
    }
}
