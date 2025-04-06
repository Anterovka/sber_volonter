package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressionscommon.GroupsCollectionReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processAdditionalCondition;
import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;

/**
 * Implementation of the grouping collection
 */
class GroupsCollectionImpl extends PreparableExpression implements GroupsCollection {

    SqlQueryProcessor elementSqlQueryProcessor;
    GroupDescription groupDescription;
    Node<String> ownerColumnNode;
    ColumnData ownerColumnData;
    ConditionImpl condition;
    Pointer<Supplier<Node<String>>> getAdditionalConditionNodeFunctionPointer;

    /**
     * @param getPrepareFunctionFunction The function of obtaining the preparation function
     */
    GroupsCollectionImpl(Function<GroupsCollectionImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    @Override
    public PrimitiveExpressionsCollection prim(String propertyName) {
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            PrimitiveDescription primitiveDescription = groupDescription.getPrimitiveDescription(propertyName);
            ColumnData primitiveColumnData = elementSqlQueryProcessor.getPrimitiveColumnData(primitiveDescription);
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = primitiveDescription.getType();
            primitiveExpressionsCollection.getTableNodeFunctionPointer = elementSqlQueryProcessor.getTableNodeFunctionPointer;
            primitiveExpressionsCollection.ownerColumnNode = ownerColumnNode;
            primitiveExpressionsCollection.elementColumnNode = primitiveColumnData.columnNode;
            primitiveExpressionsCollection.ownerColumnData = ownerColumnData;
            primitiveExpressionsCollection.condition = condition;
            primitiveExpressionsCollection.getAdditionalConditionNodeFunctionPointer = getAdditionalConditionNodeFunctionPointer;
        });
    }

    @Override
    public EntitiesCollection ref(String propertyName, Consumer<GroupsCollectionReferenceSpecification> specificationCode) {
        GroupsCollectionReferenceSpecification referenceSpecification = getSpecification(GroupsCollectionReferenceSpecificationImpl::new, specificationCode);
        return new EntitiesCollectionImpl(entitiesCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ReferenceDescription referenceDescription = groupDescription.getReferenceDescription(propertyName);
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
}
