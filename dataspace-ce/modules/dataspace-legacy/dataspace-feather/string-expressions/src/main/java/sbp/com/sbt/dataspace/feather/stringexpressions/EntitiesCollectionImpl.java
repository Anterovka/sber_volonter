package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionBackReferenceReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesCollectionBackReferenceReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesCollectionReferenceSpecificationImpl;

import java.util.function.Consumer;

import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.checkImpl;
import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression;

/**
 * Implementation of the collection of entities
 */
class EntitiesCollectionImpl extends StringBasedObject implements EntitiesCollection {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    EntitiesCollectionImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public PrimitiveExpressionsCollection type() {
        return getSystemPrimitiveExpressionsCollection(Helper.TYPE_NODE);
    }

    @Override
    public PrimitiveExpressionsCollection id() {
        return getSystemPrimitiveExpressionsCollection(Helper.ID_NODE);
    }

    @Override
    public PrimitiveExpressionsCollection prim(String propertyName) {
        return new PrimitiveExpressionsCollectionImpl(getPropertyStringNode(propertyName), Priority.VALUE);
    }

    @Override
    public EntitiesCollection ref(String propertyName, Consumer<EntitiesCollectionReferenceSpecification> specificationCode) {
        return getExpression(EntitiesCollectionImpl::new, getPropertyStringNode(propertyName), EntitiesCollectionReferenceSpecificationImpl::new, specificationCode);
    }

    @Override
    public EntitiesCollection refB(String propertyName, Consumer<EntitiesCollectionBackReferenceReferenceSpecification> specificationCode) {
        return getExpression(EntitiesCollectionImpl::new, getPropertyStringNode(propertyName), EntitiesCollectionBackReferenceReferenceSpecificationImpl::new, specificationCode);
    }

    @Override
    public GroupsCollection group(String propertyName) {
        return new GroupsCollectionImpl(getPropertyStringNode(propertyName), Priority.VALUE);
    }

    @Override
    public PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionsCollectionImpl(node(stringNode, Helper.MAP_NODE, checkImpl(primitiveExpression).stringNode, Helper.BRACKET_R_NODE), Priority.VALUE);
    }

    @Override
    public PrimitiveExpression count() {
        return getSystemPrimitiveExpression(Helper.COUNT_NODE);
    }

    @Override
    public Condition exists() {
        return getSystemCondition(Helper.EXISTS_NODE);
    }
}
