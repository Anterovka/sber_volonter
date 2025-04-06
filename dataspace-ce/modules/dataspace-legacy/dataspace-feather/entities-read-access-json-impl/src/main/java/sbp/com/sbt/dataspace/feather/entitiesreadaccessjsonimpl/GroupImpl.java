package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.Group;
import sbp.com.sbt.dataspace.feather.expressions.GroupReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressionscommon.GroupReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetConditionStringNodeFunction;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetExpressionStringNodeFunction;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processAlias;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processEntity;
import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;

/**
 * Implementation of grouping
 */
class GroupImpl extends PreparableExpression implements Group {

    SqlQueryProcessor groupSqlQueryProcessor;
    GroupDescription groupDescription;

    /**
     * @param getPrepareFunctionFunction The function of obtaining the preparation function
     */
    GroupImpl(Function<GroupImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    @Override
    public PrimitiveExpression prim(String propertyName) {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            PrimitiveDescription primitiveDescription = groupDescription.getPrimitiveDescription(propertyName);
            primitiveExpression.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpression.getExpressionStringNodeFunction = getGetExpressionStringNodeFunction(groupSqlQueryProcessor, primitiveDescription);
            primitiveExpression.type = primitiveDescription.getType();
        });
    }

    @Override
    public Entity ref(String propertyName, Consumer<GroupReferenceSpecification> specificationCode) {
        GroupReferenceSpecification referenceSpecification = getSpecification(GroupReferenceSpecificationImpl::new, specificationCode);
        return new EntityImpl(entity -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ReferenceDescription referenceDescription = groupDescription.getReferenceDescription(propertyName);
            processEntity(entity, groupSqlQueryProcessor.getReferenceSqlQueryProcessor(referenceDescription), referenceDescription.getEntityDescription(), referenceSpecification);
            processAlias(expressionContext, entity, referenceSpecification);
            entity.getConditionStringNodeFunction = getGetConditionStringNodeFunction(this, entity);
        });
    }

    @Override
    public Condition isNull() {
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ConditionImpl condition2 = (ConditionImpl) Stream.concat(
                groupDescription.getPrimitiveDescriptions().keySet().stream().map(propertyName -> prim(propertyName).isNull()),
                groupDescription.getReferenceDescriptions().keySet().stream().map(propertyName -> ref(propertyName).isNull())
            ).reduce(Condition::and).get();
            condition2.prepare(sqlQueryProcessor, expressionContext);
            condition.getExpressionStringNodeFunction = () -> condition2.getExpressionStringNodeFunction.get();
        });
    }

    @Override
    public Condition isNotNull() {
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ConditionImpl condition2 = (ConditionImpl) Stream.concat(
                groupDescription.getPrimitiveDescriptions().keySet().stream().map(propertyName -> prim(propertyName).isNotNull()),
                groupDescription.getReferenceDescriptions().keySet().stream().map(propertyName -> ref(propertyName).isNotNull())
            ).reduce(Condition::or).get();
            condition2.prepare(sqlQueryProcessor, expressionContext);
            condition.getExpressionStringNodeFunction = () -> condition2.getExpressionStringNodeFunction.get();
        });
    }
}
