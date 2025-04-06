package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.AliasedEntitySpecification;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesSpecification;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.EntityElementSpecification;
import sbp.com.sbt.dataspace.feather.expressions.ExpressionsProcessor;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressions.RootSpecification;
import sbp.com.sbt.dataspace.feather.expressionscommon.AliasedEntitySpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntityElementSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.RootSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.BlackHoleList.blackHoleList;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.checkType;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getConditionStringNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getExpressionWithConditionNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processEntitiesCollectionSpecification;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.processEntity;
import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;

/**
 * Implementation of the expression handler
 */
class ExpressionsProcessorImpl implements ExpressionsProcessor {

    /**
     * Get data type
     *
     * @param value The value
     */
    DataType getDataType(Object value) {
        DataType type;
        if (value instanceof String) {
            type = DataType.STRING;
        } else if (value instanceof Long) {
            type = DataType.LONG;
        } else if (value instanceof BigDecimal) {
            type = DataType.BIG_DECIMAL;
        } else if (value instanceof LocalDate) {
            type = DataType.DATE;
        } else if (value instanceof LocalDateTime) {
            type = DataType.DATETIME;
        } else if (value instanceof OffsetDateTime) {
            type = DataType.OFFSET_DATETIME;
        } else if (value instanceof LocalTime) {
            type = DataType.TIME;
        } else {
            type = DataType.BOOLEAN;
        }
        return type;
    }

    /**
     * Get conditional group node
     *
     * @param sqlQueryProcessor The SQL query processor
     * @param type              Тип
     * @param conditionNode     The condition node
     * @param array             Array
     */
    Node<String> getConditionalGroupNode(SqlQueryProcessor sqlQueryProcessor, DataType type, Node<String> conditionNode, Object[] array) {
        if (sqlQueryProcessor.requestData.sqlDialect == SqlDialect.POSTGRESQL && type != DataType.TIME) {
            Node<String> parameterNode = sqlQueryProcessor.addParameter(array);
            return node(conditionNode, Helper.BRACKET_L_NODE, parameterNode, Helper.BRACKET_R_NODE);
        } else {
            List<Node<String>> nodes = new ArrayList<>(2 + 2 * array.length);
            nodes.add(conditionNode);
            nodes.add(Helper.BRACKET_L_NODE);
            addNodeListToNodes(nodes, Helper.UNION_ALL_NODE, Arrays.stream(array).map(elem -> {
                List<Node<String>> nodes2 = new ArrayList<>(4);
                nodes2.add(Helper.SELECT_NODE);
                nodes2.add(sqlQueryProcessor.addParameter(elem));
                if (sqlQueryProcessor.requestData.sqlDialect == SqlDialect.ORACLE) {
                    nodes2.add(Helper.FROM_NODE);
                    nodes2.add(Helper.DUAL);
                }
                return node(nodes2);
            }));
            nodes.add(Helper.BRACKET_R_NODE);
            return node(nodes);
        }
    }

    /**
     * Process array
     *
     * @param array Array
     * @return The data type of array elements
     */
    DataType processArray(Object[] array) {
        DataType type = getDataType(array[0]);
        for (int i = 1; i < array.length; ++i) {
            Object elem = array[i];
            DataType elemDataType = getDataType(elem);
            if (elemDataType == DataType.BIG_DECIMAL && type == DataType.LONG) {
                type = DataType.BIG_DECIMAL;
                for (int j = 0; j < i; ++j) {
                    array[j] = new BigDecimal((Long) array[j]);
                }
            } else if (elemDataType == DataType.LONG && type == DataType.BIG_DECIMAL) {
                array[i] = new BigDecimal((Long) elem);
            } else if (elemDataType != type) {
                throw new ArrayElemTypesShouldBeOfOneTypeException(Arrays.stream(array).map(this::getDataType).toArray(DataType[]::new));
            }
        }
        return type;
    }

    @Override
    public Entity root(Consumer<RootSpecification> specificationCode) {
        RootSpecification rootSpecification = getSpecification(RootSpecificationImpl::new, specificationCode);
        return new EntityImpl(entity -> (sqlQueryProcessor, expressionContext) -> processEntity(entity, sqlQueryProcessor, sqlQueryProcessor.entityDescription, rootSpecification));
    }

    @Override
    public PrimitiveExpression elemPE() {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            primitiveExpression.getExpressionStringNodeFunction = () -> expressionContext.collectionElementColumnNode;
            primitiveExpression.type = expressionContext.collectionElementType;
        });
    }

    @Override
    public Entity elemE(Consumer<EntityElementSpecification> specificationCode) {
        EntityElementSpecification entityElementSpecification = getSpecification(EntityElementSpecificationImpl::new, specificationCode);
        return new EntityImpl(entity -> (sqlQueryProcessor, expressionContext) -> processEntity(entity, expressionContext.collectionElementSqlQueryProcessor, expressionContext.collectionElementSqlQueryProcessor.entityDescription, entityElementSpecification));
    }

    @Override
    public Entity aliasedEntity(String alias, Consumer<AliasedEntitySpecification> specificationCode) {
        AliasedEntitySpecification aliasedEntitySpecification = getSpecification(AliasedEntitySpecificationImpl::new, specificationCode);
        return new EntityImpl(entity -> (sqlQueryProcessor, expressionContext) -> {
            AliasedEntityData aliasedEntityData = expressionContext.aliasedEntitiesData.get(alias);
            processEntity(entity, aliasedEntityData.sqlQueryProcessor, aliasedEntityData.entityDescription, aliasedEntitySpecification);
        });
    }

    @Override
    public PrimitiveExpression prim(Object primitiveValue) {
        DataType type = getDataType(primitiveValue);
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            Node<String> parameterNode = sqlQueryProcessor.addParameter(primitiveValue);
            primitiveExpression.getExpressionStringNodeFunction = () -> parameterNode;
            primitiveExpression.type = type;
        });
    }

    @Override
    public PrimitiveExpression now() {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            primitiveExpression.getExpressionStringNodeFunction = () -> Helper.CURRENT_TIMESTAMP_NODE;
            primitiveExpression.type = DataType.OFFSET_DATETIME;
        });
    }

    @Override
    public PrimitiveExpression coalesce(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        List<PrimitiveExpressionImpl> primitiveExpressionImpls = Stream.concat(
                Stream.of(primitiveExpression1),
                Arrays.stream(primitiveExpressions))
            .map(PrimitiveExpressionImpl.class::cast)
            .collect(Collectors.toList());
        Function<PrimitiveExpressionImpl, UnsupportedOperationException> exceptionInitializer = primitiveExpression -> new UnsupportedOperationException("coalesce(" + primitiveExpressionImpls.stream().map(PrimitiveExpressionImpl::getSignature).collect(Collectors.joining(", ")) + ")");
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            primitiveExpressionImpls.forEach(primitiveExpression2 -> primitiveExpression2.prepareFunction.accept(sqlQueryProcessor, expressionContext));
            DataType type = primitiveExpressionImpls.get(0).type;
            if (Helper.STRING_TYPES.contains(type)) {
                checkType(Helper.STRING_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                checkType(Helper.NUMBER_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type)) {
                checkType(Helper.DATE_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
                primitiveExpression.type = DataType.DATETIME;
            } else if (primitiveExpressionImpls.stream().allMatch(primitiveExpression2 -> primitiveExpression2.type == type)) {
                primitiveExpression.type = type;
            } else {
                throw exceptionInitializer.apply(null);
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> {
                List<Node<String>> nodes = new ArrayList<>(1 + primitiveExpressionImpls.size() * 2);
                nodes.add(Helper.COALESCE_NODE);
                addNodeListToNodes(nodes, Helper.COMMA_NODE, primitiveExpressionImpls.stream().map(primitiveExpression2 -> getExpressionWithConditionNode(sqlQueryProcessor.requestData.sqlDialect, primitiveExpression2.get(), primitiveExpression2.getConditionStringNodeFunction.get())));
                nodes.add(Helper.BRACKET_R_NODE);
                return node(nodes);
            };
        });
    }

    @Override
    public EntitiesCollection entities(Consumer<EntitiesSpecification> specificationCode) {
        EntitiesSpecification entitiesSpecification = getSpecification(EntitiesSpecificationImpl::new, specificationCode);
        return new EntitiesCollectionImpl(entitiesCollection -> (sqlQueryProcessor, expressionContext) -> {
            entitiesCollection.elementSqlQueryProcessor = new SqlQueryProcessor(sqlQueryProcessor.requestData);
            entitiesCollection.elementSqlQueryProcessor.entityDescription = sqlQueryProcessor.entityDescription.getModelDescription().getEntityDescription(entitiesSpecification.getType());
            entitiesCollection.elementSqlQueryProcessor.parameters = entitiesSpecification.getParameters();
            entitiesCollection.elementSqlQueryProcessor.tablesData = new HashMap<>();
            TableData startTableData = entitiesCollection.elementSqlQueryProcessor.getTableData(entitiesCollection.elementSqlQueryProcessor.entityDescription);
            startTableData.added = true;
            entitiesCollection.elementSqlQueryProcessor.workColumnsData = blackHoleList();
            entitiesCollection.elementSqlQueryProcessor.idColumnData = new ColumnData(entitiesCollection.elementSqlQueryProcessor, DataType.STRING, startTableData.idColumnNode);
            entitiesCollection.elementSqlQueryProcessor.mandatory = true;
            entitiesCollection.elementSqlQueryProcessor.mandatoryEntityDescription = entitiesCollection.elementSqlQueryProcessor.entityDescription;
            entitiesCollection.elementSqlQueryProcessor.getTableNodeFunctionPointer = new Pointer<>(table -> startTableData.tableNode);
            entitiesCollection.elementSqlQueryProcessor.localIdColumnsData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.primitiveColumnsData = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.referenceSqlQueryProcessors = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.backReferenceReferenceSqlQueryProcessors = new HashMap<>();
            entitiesCollection.elementSqlQueryProcessor.getAdditionalConditionNodeFunctionPointer = new Pointer<>();
            entitiesCollection.elementSqlQueryProcessor.processSingleTableInheritanceStrategy();
            processEntitiesCollectionSpecification(sqlQueryProcessor, expressionContext, entitiesCollection, entitiesSpecification);
        });
    }

    @Override
    public PrimitiveExpression rawPE(Object raw) {
        throw new UnexpectedRawException(raw);
    }

    @Override
    public ConditionalGroup any(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        PrimitiveExpressionsCollectionImpl primitiveExpressionsCollectionImpl = (PrimitiveExpressionsCollectionImpl) primitiveExpressionsCollection;
        return new ConditionalGroupImpl(conditionalGroup -> (sqlQueryProcessor, expressionContext) -> {
            primitiveExpressionsCollectionImpl.prepareFunction.accept(sqlQueryProcessor, expressionContext);
            conditionalGroup.type = primitiveExpressionsCollectionImpl.type;
            conditionalGroup.getExpressionStringNodeFunction = () -> getConditionStringNode(node(Helper.ANY_NODE, primitiveExpressionsCollectionImpl.getQueryNode(UnaryOperator.identity())), primitiveExpressionsCollectionImpl);
        });
    }

    @Override
    public ConditionalGroup any(Object[] array) {
        DataType type = processArray(array);
        return new ConditionalGroupImpl(conditionalGroup -> (sqlQueryProcessor, expressionContext) -> {
            conditionalGroup.type = type;
            conditionalGroup.getExpressionStringNodeFunction = () -> getConditionalGroupNode(sqlQueryProcessor, type, Helper.ANY_NODE, array);
        });
    }

    @Override
    public ConditionalGroup all(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        PrimitiveExpressionsCollectionImpl primitiveExpressionsCollectionImpl = (PrimitiveExpressionsCollectionImpl) primitiveExpressionsCollection;
        return new ConditionalGroupImpl(conditionalGroup -> (sqlQueryProcessor, expressionContext) -> {
            primitiveExpressionsCollectionImpl.prepareFunction.accept(sqlQueryProcessor, expressionContext);
            conditionalGroup.type = primitiveExpressionsCollectionImpl.type;
            conditionalGroup.getExpressionStringNodeFunction = () -> getConditionStringNode(node(Helper.ALL_NODE, primitiveExpressionsCollectionImpl.getQueryNode(UnaryOperator.identity())), primitiveExpressionsCollectionImpl);
        });
    }

    @Override
    public ConditionalGroup all(Object[] array) {
        DataType type = processArray(array);
        return new ConditionalGroupImpl(conditionalGroup -> (sqlQueryProcessor, expressionContext) -> {
            conditionalGroup.type = type;
            conditionalGroup.getExpressionStringNodeFunction = () -> getConditionalGroupNode(sqlQueryProcessor, type, Helper.ALL_NODE, array);
        });
    }
}
