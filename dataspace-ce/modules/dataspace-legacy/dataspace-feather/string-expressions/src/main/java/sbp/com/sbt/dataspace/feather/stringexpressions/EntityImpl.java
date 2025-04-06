package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.addConditionSpecificationParameterNode;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getCondition;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression2;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression3;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getStringNodeWithSpecification;

/**
 * Implementation of the entity
 */
class EntityImpl extends StringBasedObject implements Entity {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    EntityImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public PrimitiveExpression type() {
        return getSystemPrimitiveExpression(Helper.TYPE_NODE);
    }

    @Override
    public PrimitiveExpression id() {
        return getSystemPrimitiveExpression(Helper.ID_NODE);
    }

    @Override
    public PrimitiveExpression prim(String propertyName) {
        return new PrimitiveExpressionImpl(getPropertyStringNode(propertyName), Priority.VALUE);
    }

    @Override
    public PrimitiveExpressionsCollection prims(String propertyName, Consumer<PrimitivesCollectionSpecification> specificationCode) {
        Node<String> prefixNode = getPropertyStringNode(propertyName);
        if (specificationCode != null) {
            PrimitivesCollectionSpecificationImpl specification = new PrimitivesCollectionSpecificationImpl();
            specificationCode.accept(specification);
            List<Node<String>> parameterNodes = new ArrayList<>(1);
            addConditionSpecificationParameterNode(parameterNodes, specification);
            prefixNode = getStringNodeWithSpecification(prefixNode, parameterNodes);
        }
        return new PrimitiveExpressionsCollectionImpl(prefixNode, Priority.VALUE);
    }

    @Override
    public Entity ref(String propertyName, Consumer<ReferenceSpecification> specificationCode) {
        return getExpression2(EntityImpl::new, getPropertyStringNode(propertyName), ReferenceSpecificationImpl::new, specificationCode);
    }

    @Override
    public Entity refB(String propertyName, Consumer<BackReferenceReferenceSpecification> specificationCode) {
        return getExpression2(EntityImpl::new, getPropertyStringNode(propertyName), BackReferenceReferenceSpecificationImpl::new, specificationCode);
    }

    @Override
    public EntitiesCollection refs(String propertyName, Consumer<ReferencesCollectionSpecification> specificationCode) {
        return getExpression3(EntitiesCollectionImpl::new, getPropertyStringNode(propertyName), ReferencesCollectionSpecificationImpl::new, specificationCode);
    }

    @Override
    public EntitiesCollection refsB(String propertyName, Consumer<BackReferenceReferencesCollectionSpecification> specificationCode) {
        return getExpression3(EntitiesCollectionImpl::new, getPropertyStringNode(propertyName), BackReferenceReferencesCollectionSpecificationImpl::new, specificationCode);
    }

    @Override
    public Group group(String propertyName) {
        return new GroupImpl(getPropertyStringNode(propertyName), Priority.VALUE);
    }

    @Override
    public Condition isNull() {
        return getExpression2(ConditionImpl::new, Helper.IS_NULL_NODE, Priority.EQUALITY_AND_RELATION, this);
    }

    @Override
    public Condition isNotNull() {
        return getExpression2(ConditionImpl::new, Helper.IS_NOT_NULL_NODE, Priority.EQUALITY_AND_RELATION, this);
    }

    @Override
    public Condition exists() {
        return getExpression2(ConditionImpl::new, Helper.EXISTS_NODE, Priority.VALUE, this);
    }

    @Override
    public Condition eq(Entity entity) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, entity);
    }

    @Override
    public Condition notEq(Entity entity) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.NOT_EQ_NODE, Priority.EQUALITY_AND_RELATION, true, this, entity);
    }

    @Override
    public Condition in(Entity entity1, Entity... entities) {
        return getCondition(Helper::checkImpl, Helper.IN_NODE, this, entity1, entities);
    }

    @Override
    public Condition in(EntitiesCollection entitiesCollection) {
        return getExpression(ConditionImpl::new, Helper::checkImpl, Helper.IN_NODE, Priority.EQUALITY_AND_RELATION, true, this, entitiesCollection);
    }
}
