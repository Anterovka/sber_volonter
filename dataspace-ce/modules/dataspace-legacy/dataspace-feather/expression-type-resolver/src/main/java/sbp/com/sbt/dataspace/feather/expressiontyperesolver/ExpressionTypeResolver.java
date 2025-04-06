package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.stringexpressions.StringPrimitiveExpressionBuilder;

/**
 * Expression type resolver
 */
public class ExpressionTypeResolver {

    private ModelDescription modelDescription;

    /**
     * @param modelDescription Model description
     */
    ExpressionTypeResolver(ModelDescription modelDescription) {
        this.modelDescription = modelDescription;
    }

    /**
     * Allow primitive expression type
     *
     * @param rootEntityType   The type of the root entity
     * @param expressionString The expression string
     * @return Data type
     */
    public DataType resolvePrimitiveExpressionType(String rootEntityType, String expressionString) {
        return ((PrimitiveExpressionImpl) new StringPrimitiveExpressionBuilder(expressionString, modelDescription, rootEntityType).build(new ExpressionsProcessorImpl(modelDescription.getEntityDescription(rootEntityType)))).getTypeFunction.apply(new Context());
    }
}
