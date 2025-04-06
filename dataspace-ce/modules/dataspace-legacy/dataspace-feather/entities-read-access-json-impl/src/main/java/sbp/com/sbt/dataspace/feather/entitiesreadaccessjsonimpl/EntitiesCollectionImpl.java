package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionBackReferenceReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesCollectionBackReferenceReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesCollectionReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getExpressionWithConditionNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processAdditionalCondition;
import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;

/**
 * Implementation of the collection of entities
 */
class EntitiesCollectionImpl extends PreparableExpression implements EntitiesCollection {

    SqlQueryProcessor elementSqlQueryProcessor;
    Node<String> ownerColumnNode;
    ColumnData ownerColumnData;
    ConditionImpl condition;

    /**
     * @param getPrepareFunctionFunction The function for obtaining the preparation function
     */
    EntitiesCollectionImpl(Function<EntitiesCollectionImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    @Override
    public PrimitiveExpressionsCollection type() {
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ColumnData typeColumnData = elementSqlQueryProcessor.getTypeColumnData();
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = DataType.STRING;
            primitiveExpressionsCollection.getTableNodeFunctionPointer = elementSqlQueryProcessor.getTableNodeFunctionPointer;
            primitiveExpressionsCollection.ownerColumnNode = ownerColumnNode;
            primitiveExpressionsCollection.elementColumnNode = typeColumnData.columnNode;
            primitiveExpressionsCollection.ownerColumnData = ownerColumnData;
            primitiveExpressionsCollection.condition = condition;
            primitiveExpressionsCollection.getAdditionalConditionNodeFunctionPointer = elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer;
        });
    }

    @Override
    public PrimitiveExpressionsCollection id() {
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = DataType.STRING;
            primitiveExpressionsCollection.getTableNodeFunctionPointer = elementSqlQueryProcessor.getTableNodeFunctionPointer;
            primitiveExpressionsCollection.ownerColumnNode = ownerColumnNode;
            primitiveExpressionsCollection.elementColumnNode = elementSqlQueryProcessor.idColumnData.columnNode;
            primitiveExpressionsCollection.ownerColumnData = ownerColumnData;
            primitiveExpressionsCollection.condition = condition;
            primitiveExpressionsCollection.getAdditionalConditionNodeFunctionPointer = elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer;
        });
    }

    @Override
    public PrimitiveExpressionsCollection prim(String propertyName) {
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            PrimitiveDescription primitiveDescription = elementSqlQueryProcessor.entityDescription.getPrimitiveDescription(propertyName);
            ColumnData primitiveColumnData = elementSqlQueryProcessor.getPrimitiveColumnData(primitiveDescription);
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = primitiveDescription.getType();
            primitiveExpressionsCollection.getTableNodeFunctionPointer = elementSqlQueryProcessor.getTableNodeFunctionPointer;
            primitiveExpressionsCollection.ownerColumnNode = ownerColumnNode;
            primitiveExpressionsCollection.elementColumnNode = primitiveColumnData.columnNode;
            primitiveExpressionsCollection.ownerColumnData = ownerColumnData;
            primitiveExpressionsCollection.condition = condition;
            primitiveExpressionsCollection.getAdditionalConditionNodeFunctionPointer = elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer;
        });
    }

    @Override
    public EntitiesCollection ref(String propertyName, Consumer<EntitiesCollectionReferenceSpecification> specificationCode) {
        EntitiesCollectionReferenceSpecification referenceSpecification = getSpecification(EntitiesCollectionReferenceSpecificationImpl::new, specificationCode);
        return new EntitiesCollectionImpl(entitiesCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ReferenceDescription referenceDescription = elementSqlQueryProcessor.entityDescription.getReferenceDescription(propertyName);
            entitiesCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            entitiesCollection.elementSqlQueryProcessor = elementSqlQueryProcessor.getReferenceSqlQueryProcessor(referenceDescription);
            entitiesCollection.elementSqlQueryProcessor.entityDescription = referenceDescription.getEntityDescription().cast(referenceSpecification == null ? null : referenceSpecification.getType());
            entitiesCollection.elementSqlQueryProcessor.reference = false;
            entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
            entitiesCollection.elementSqlQueryProcessor.addReferencesCollectionFilter(referenceDescription.getEntityDescription());
            processAdditionalCondition(elementSqlQueryProcessor, entitiesCollection);
            entitiesCollection.ownerColumnNode = ownerColumnNode;
            entitiesCollection.ownerColumnData = ownerColumnData;
            entitiesCollection.condition = condition;
        });
    }

    @Override
    public EntitiesCollection refB(String propertyName, Consumer<EntitiesCollectionBackReferenceReferenceSpecification> specificationCode) {
        EntitiesCollectionBackReferenceReferenceSpecification backReferenceReferenceSpecification = getSpecification(EntitiesCollectionBackReferenceReferenceSpecificationImpl::new, specificationCode);
        return new EntitiesCollectionImpl(entitiesCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ReferenceDescription backReferenceDescription = elementSqlQueryProcessor.entityDescription.getReferenceBackReferenceDescription(propertyName);
            entitiesCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            entitiesCollection.elementSqlQueryProcessor = elementSqlQueryProcessor.getBackReferenceReferenceSqlQueryProcessor(backReferenceDescription);
            entitiesCollection.elementSqlQueryProcessor.entityDescription = backReferenceDescription.getOwnerEntityDescription().cast(backReferenceReferenceSpecification == null ? null : backReferenceReferenceSpecification.getType());
            entitiesCollection.elementSqlQueryProcessor.reference = false;
            entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
            entitiesCollection.elementSqlQueryProcessor.addReferencesCollectionFilter(backReferenceDescription.getOwnerEntityDescription());
            processAdditionalCondition(elementSqlQueryProcessor, entitiesCollection);
            entitiesCollection.ownerColumnNode = ownerColumnNode;
            entitiesCollection.ownerColumnData = ownerColumnData;
            entitiesCollection.condition = condition;
        });
    }

    @Override
    public GroupsCollection group(String propertyName) {
        return new GroupsCollectionImpl(groupsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            groupsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            groupsCollection.elementSqlQueryProcessor = elementSqlQueryProcessor;
            groupsCollection.groupDescription = elementSqlQueryProcessor.entityDescription.getGroupDescription(propertyName);
            groupsCollection.ownerColumnNode = ownerColumnNode;
            groupsCollection.ownerColumnData = ownerColumnData;
            groupsCollection.condition = condition;
            groupsCollection.getAdditionalConditionNodeFunctionPointer = elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer;
        });
    }

    @Override
    public PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            PrimitiveExpressionImpl primitiveExpressionImpl = (PrimitiveExpressionImpl) primitiveExpression;
            ExpressionContext expressionContext2 = new ExpressionContext(elementSqlQueryProcessor);
            expressionContext2.aliasedEntitiesData = expressionContext.aliasedEntitiesData;
            primitiveExpressionImpl.prepare(sqlQueryProcessor, expressionContext2);
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = primitiveExpressionImpl.type;
            primitiveExpressionsCollection.getTableNodeFunctionPointer = elementSqlQueryProcessor.getTableNodeFunctionPointer;
            primitiveExpressionsCollection.ownerColumnNode = ownerColumnNode;
            primitiveExpressionsCollection.elementColumnNode = getExpressionWithConditionNode(sqlQueryProcessor.requestData.sqlDialect, primitiveExpressionImpl.get(), primitiveExpressionImpl.getConditionStringNodeFunction.get());
            primitiveExpressionsCollection.ownerColumnData = ownerColumnData;
            primitiveExpressionsCollection.condition = condition;
            primitiveExpressionsCollection.getAdditionalConditionNodeFunctionPointer = elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer;
        });
    }
}
