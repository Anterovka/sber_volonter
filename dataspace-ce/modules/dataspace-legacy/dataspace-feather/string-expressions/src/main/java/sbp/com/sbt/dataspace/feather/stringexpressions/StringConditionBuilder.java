package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionBuilder;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

/**
 * Condition builder based on a string
 */
public final class StringConditionBuilder extends ConditionBuilder {

    ExpressionsParser expressionsParser;

    /**
     * @param expressionString               The expression string
     * @param modelDescription               Description of the model
     * @param rootEntityType                 The type of the root entity
     * @param entityElementType              The type of the entity element
     * @param primitiveExpressionsCollection Is it inside the collection of primitive expressions
     */
    StringConditionBuilder(String expressionString, ModelDescription modelDescription, String rootEntityType, String entityElementType, boolean primitiveExpressionsCollection) {
        expressionsParser = new ExpressionsParser(
                expressionString,
                modelDescription,
                rootEntityType,
                entityElementType,
                primitiveExpressionsCollection,
                this::root,
                this::elemPE,
                this::elemE,
                this::aliasedEntity,
                this::prim,
                this::coalesce,
                this::now,
                this::entities,
                this::any,
                this::any,
                this::all,
                this::all);
    }

    /**
     * @param expressionString The expression string
     * @param modelDescription Model description
     * @param rootEntityType   The type of the root entity
     */
    public StringConditionBuilder(String expressionString, ModelDescription modelDescription, String rootEntityType) {
        this(expressionString, modelDescription, rootEntityType, null, false);
    }

    /**
     * @param expressionString The expression string
     * @param modelDescription Description of the model
     * @param rootEntityType   The type of the root entity
     * @param primitiveExpressionsCollection       of primitive expressions Contains primitives that are part of the expression collection
     */
    public StringConditionBuilder(String expressionString, ModelDescription modelDescription, String rootEntityType, boolean primitiveExpressionsCollection) {
        this(expressionString, modelDescription, rootEntityType, null, primitiveExpressionsCollection);
    }

    /**
     * @param expressionString  Expression string
     * @param modelDescription  Model description
     * @param rootEntityType    The type of the root entity
     * @param entityElementType The type of the entity element
     */
    public StringConditionBuilder(String expressionString, ModelDescription modelDescription, String rootEntityType, String entityElementType) {
        this(expressionString, modelDescription, rootEntityType, entityElementType, false);
    }

    /**
     * Set entity description with alias
     *
     * @param alias      Alias
     * @param entityType Entity type
     * @return Current builder
     */
    public StringConditionBuilder setAliasedEntityDescription(String alias, String entityType) {
        expressionsParser.setAliasedEntityDescription(alias, entityType);
        return this;
    }

    @Override
    protected Condition condition() {
        return expressionsParser.parseRootCondition();
    }
}
