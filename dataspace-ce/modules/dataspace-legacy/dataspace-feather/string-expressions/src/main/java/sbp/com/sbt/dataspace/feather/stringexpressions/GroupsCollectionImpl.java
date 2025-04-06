package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressionscommon.GroupsCollectionReferenceSpecificationImpl;

import java.util.function.Consumer;

import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression;

/**
 * Implementation of the grouping collection
 */
class GroupsCollectionImpl extends StringBasedObject implements GroupsCollection {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    GroupsCollectionImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public PrimitiveExpressionsCollection prim(String propertyName) {
        return new PrimitiveExpressionsCollectionImpl(getPropertyStringNode(propertyName), Priority.VALUE);
    }

    @Override
    public EntitiesCollection ref(String propertyName, Consumer<GroupsCollectionReferenceSpecification> specificationCode) {
        return getExpression(EntitiesCollectionImpl::new, getPropertyStringNode(propertyName), GroupsCollectionReferenceSpecificationImpl::new, specificationCode);
    }
}
