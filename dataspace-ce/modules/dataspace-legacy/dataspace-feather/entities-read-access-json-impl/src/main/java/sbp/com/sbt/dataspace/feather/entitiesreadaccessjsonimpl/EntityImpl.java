package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.BackReferenceReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.BackReferenceReferencesCollectionSpecification;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.Group;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressions.PrimitivesCollectionSpecification;
import sbp.com.sbt.dataspace.feather.expressions.ReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.ReferencesCollectionSpecification;
import sbp.com.sbt.dataspace.feather.expressionscommon.BackReferenceReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.BackReferenceReferencesCollectionSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.PrimitivesCollectionSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.ReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.ReferencesCollectionSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitivesCollectionDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferencesCollectionDescription;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static sbp.com.sbt.dataspace.feather.common.BlackHoleList.blackHoleList;
import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getColumnNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getConditionStringNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetExpressionStringNodeFunction;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processAlias;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processEntitiesCollectionSpecification;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processEntity;
import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;

/**
 * Implementation of the entity
 */
class EntityImpl extends PreparableExpression implements Entity {

    SqlQueryProcessor entitySqlQueryProcessor;
    EntityDescription entityDescription;
    Supplier<Node<String>> getIdConditionStringNodeFunction;

    /**
     * @param getPrepareFunctionFunction The function for obtaining the preparation function
     */
    EntityImpl(Function<EntityImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    @Override
    public PrimitiveExpression type() {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ColumnData typeColumnData = entitySqlQueryProcessor.getTypeColumnData();
            typeColumnData.inherit = true;
            primitiveExpression.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpression.getExpressionStringNodeFunction = () -> typeColumnData.columnNode;
            primitiveExpression.type = DataType.STRING;
        });
    }

    @Override
    public PrimitiveExpression id() {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            entitySqlQueryProcessor.idColumnData.inherit = true;
            primitiveExpression.getConditionStringNodeFunction = getIdConditionStringNodeFunction;
            primitiveExpression.getExpressionStringNodeFunction = () -> entitySqlQueryProcessor.idColumnData.columnNode;
            primitiveExpression.type = DataType.STRING;
        });
    }

    @Override
    public PrimitiveExpression prim(String propertyName) {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            PrimitiveDescription primitiveDescription = entityDescription.getPrimitiveDescription(propertyName);
            primitiveExpression.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpression.getExpressionStringNodeFunction = getGetExpressionStringNodeFunction(entitySqlQueryProcessor, primitiveDescription);
            primitiveExpression.type = primitiveDescription.getType();
        });
    }

    @Override
    public PrimitiveExpressionsCollection prims(String propertyName, Consumer<PrimitivesCollectionSpecification> specificationCode) {
        PrimitivesCollectionSpecification primitivesCollectionSpecification = getSpecification(PrimitivesCollectionSpecificationImpl::new, specificationCode);
        return new PrimitiveExpressionsCollectionImpl(primitiveExpressionsCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            entitySqlQueryProcessor.setIdOnlyFalse();
            PrimitivesCollectionDescription primitivesCollectionDescription = entityDescription.getPrimitivesCollectionDescription(propertyName);
            Node<String> tableAliasNode = sqlQueryProcessor.getTableAliasNode();
            primitiveExpressionsCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            primitiveExpressionsCollection.type = primitivesCollectionDescription.getType();
            Node<String> tableNode = sqlQueryProcessor.getTableNode(tableAliasNode, primitivesCollectionDescription.getTableName());
            primitiveExpressionsCollection.getTableNodeFunctionPointer = new Pointer<>(tableNode2 -> tableNode);
            primitiveExpressionsCollection.ownerColumnNode = getColumnNode(tableAliasNode, node(primitivesCollectionDescription.getOwnerColumnName()));
            primitiveExpressionsCollection.elementColumnNode = getColumnNode(tableAliasNode, node(primitivesCollectionDescription.getColumnName()));
            primitiveExpressionsCollection.ownerColumnData = entitySqlQueryProcessor.idColumnData;
            if (primitivesCollectionSpecification != null) {
                primitiveExpressionsCollection.condition = (ConditionImpl) primitivesCollectionSpecification.getCondition();
                ExpressionContext expressionContext2 = new ExpressionContext(primitiveExpressionsCollection.elementColumnNode, primitiveExpressionsCollection.type);
                expressionContext2.aliasedEntitiesData = expressionContext.aliasedEntitiesData;
                primitiveExpressionsCollection.condition.prepare(sqlQueryProcessor, expressionContext2);
            }
        });
    }

    @Override
    public Entity ref(String propertyName, Consumer<ReferenceSpecification> specificationCode) {
        ReferenceSpecification referenceSpecification = getSpecification(ReferenceSpecificationImpl::new, specificationCode);
        return new EntityImpl(entity -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ReferenceDescription referenceDescription = entityDescription.getReferenceDescription(propertyName);
            processEntity(entity, entitySqlQueryProcessor.getReferenceSqlQueryProcessor(referenceDescription), referenceDescription.getEntityDescription(), referenceSpecification);
            processAlias(expressionContext, entity, referenceSpecification);
            entity.getConditionStringNodeFunction = Helper.getGetConditionStringNodeFunction(this, entity);
        });
    }

    @Override
    public Entity refB(String propertyName, Consumer<BackReferenceReferenceSpecification> specificationCode) {
        BackReferenceReferenceSpecification backReferenceReferenceSpecification = getSpecification(BackReferenceReferenceSpecificationImpl::new, specificationCode);
        return new EntityImpl(entity -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ReferenceDescription backReferenceDescription = entityDescription.getReferenceBackReferenceDescription(propertyName);
            processEntity(entity, entitySqlQueryProcessor.getBackReferenceReferenceSqlQueryProcessor(backReferenceDescription), backReferenceDescription.getOwnerEntityDescription(), backReferenceReferenceSpecification);
            processAlias(expressionContext, entity, backReferenceReferenceSpecification);
            entity.getConditionStringNodeFunction = Helper.getGetConditionStringNodeFunction(this, entity);
        });
    }

    @Override
    public EntitiesCollection refs(String propertyName, Consumer<ReferencesCollectionSpecification> specificationCode) {
        ReferencesCollectionSpecification referencesCollectionSpecification = getSpecification(ReferencesCollectionSpecificationImpl::new, specificationCode);
        return new EntitiesCollectionImpl(entitiesCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            entitySqlQueryProcessor.setIdOnlyFalse();
            ReferencesCollectionDescription referencesCollectionDescription = entityDescription.getReferencesCollectionDescription(propertyName);
            Node<String> tableAliasNode = sqlQueryProcessor.getTableAliasNode();
            entitiesCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            entitiesCollection.elementSqlQueryProcessor = new SqlQueryProcessor(sqlQueryProcessor.requestData);
            entitiesCollection.elementSqlQueryProcessor.tablesData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.workColumnsData = blackHoleList();
            entitiesCollection.elementSqlQueryProcessor.idColumnData = new ColumnData(entitiesCollection.elementSqlQueryProcessor, DataType.STRING, getColumnNode(tableAliasNode, node(referencesCollectionDescription.getColumnName())));
            entitiesCollection.elementSqlQueryProcessor.mandatory = true;
            entitiesCollection.elementSqlQueryProcessor.getTableNodeFunctionPointer = new Pointer<>(table -> sqlQueryProcessor.getTableNode(tableAliasNode, referencesCollectionDescription.getTableName()));
            entitiesCollection.elementSqlQueryProcessor.localIdColumnsData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.primitiveColumnsData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.referenceSqlQueryProcessors = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
            entitiesCollection.elementSqlQueryProcessor.entityDescription = referencesCollectionDescription.getEntityDescription().cast(referencesCollectionSpecification == null ? null : referencesCollectionSpecification.getType());
            entitiesCollection.elementSqlQueryProcessor.mandatoryEntityDescription = entitiesCollection.elementSqlQueryProcessor.entityDescription;
            entitiesCollection.elementSqlQueryProcessor.addReferencesCollectionFilter(referencesCollectionDescription.getEntityDescription());
            entitiesCollection.ownerColumnNode = getColumnNode(tableAliasNode, node(referencesCollectionDescription.getOwnerColumnName()));
            entitiesCollection.ownerColumnData = entitySqlQueryProcessor.idColumnData;
            processEntitiesCollectionSpecification(sqlQueryProcessor, expressionContext, entitiesCollection, referencesCollectionSpecification);
        });
    }

    @Override
    public EntitiesCollection refsB(String propertyName, Consumer<BackReferenceReferencesCollectionSpecification> specificationCode) {
        BackReferenceReferencesCollectionSpecification backReferenceReferencesCollectionSpecification = getSpecification(BackReferenceReferencesCollectionSpecificationImpl::new, specificationCode);
        return new EntitiesCollectionImpl(entitiesCollection -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            entitySqlQueryProcessor.setIdOnlyFalse();
            ReferenceDescription backReferenceDescription = entityDescription.getReferencesCollectionBackReferenceDescription(propertyName);
            EntityDescription ownerEntityDescription = backReferenceDescription.getOwnerEntityDescription();
            entitiesCollection.getConditionStringNodeFunction = getConditionStringNodeFunction;
            entitiesCollection.elementSqlQueryProcessor = new SqlQueryProcessor(sqlQueryProcessor.requestData);
            entitiesCollection.elementSqlQueryProcessor.tablesData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.workColumnsData = blackHoleList();
            TableData ownerEntityTableData = entitiesCollection.elementSqlQueryProcessor.getTableData(ownerEntityDescription);
            ownerEntityTableData.added = true;
            entitiesCollection.elementSqlQueryProcessor.idColumnData = new ColumnData(entitiesCollection.elementSqlQueryProcessor, DataType.STRING, ownerEntityTableData.idColumnNode);
            entitiesCollection.elementSqlQueryProcessor.mandatory = true;
            entitiesCollection.elementSqlQueryProcessor.getTableNodeFunctionPointer = new Pointer<>(table -> ownerEntityTableData.tableNode);
            entitiesCollection.elementSqlQueryProcessor.localIdColumnsData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.localIdColumnsData.put(ownerEntityDescription, entitiesCollection.elementSqlQueryProcessor.idColumnData);
            entitiesCollection.elementSqlQueryProcessor.primitiveColumnsData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.referenceSqlQueryProcessors = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
            entitiesCollection.elementSqlQueryProcessor.entityDescription = ownerEntityDescription.cast(backReferenceReferencesCollectionSpecification == null ? null : backReferenceReferencesCollectionSpecification.getType());
            entitiesCollection.elementSqlQueryProcessor.mandatoryEntityDescription = entityDescription;
            entitiesCollection.elementSqlQueryProcessor.addReferencesCollectionFilter(ownerEntityDescription);
            entitiesCollection.ownerColumnNode = getColumnNode(ownerEntityTableData.aliasNode, node(backReferenceDescription.getColumnName()));
            entitiesCollection.ownerColumnData = entitySqlQueryProcessor.idColumnData;
            processEntitiesCollectionSpecification(sqlQueryProcessor, expressionContext, entitiesCollection, backReferenceReferencesCollectionSpecification);
        });
    }

    @Override
    public Group group(String propertyName) {
        return new GroupImpl(group -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            entitySqlQueryProcessor.setIdOnlyFalse();
            group.groupSqlQueryProcessor = entitySqlQueryProcessor;
            group.groupDescription = entityDescription.getGroupDescription(propertyName);
            group.getConditionStringNodeFunction = getConditionStringNodeFunction;
        });
    }

    @Override
    public Condition exists() {
        return new ConditionImpl(condition -> ((sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            ColumnData localIdColumnData = entitySqlQueryProcessor.getLocalIdColumnData(entityDescription);
            condition.getExpressionStringNodeFunction = () -> getConditionStringNode(node(localIdColumnData.columnNode, Helper.IS_NOT_NULL_NODE), this);
        }));
    }
}
