package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.Group;
import sbp.com.sbt.dataspace.feather.expressions.GroupReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressionscommon.GroupReferenceSpecificationImpl;

import java.util.function.Consumer;

import static sbp.com.sbt.dataspace.feather.stringexpressions.Helper.getExpression2;

/**
 * Implementation of grouping
 */
class GroupImpl extends StringBasedObject implements Group {

    /**
     * @param stringNode String node
     * @param priority   Priority
     */
    GroupImpl(Node<String> stringNode, Priority priority) {
        super(stringNode, priority);
    }

    @Override
    public PrimitiveExpression prim(String propertyName) {
        return new PrimitiveExpressionImpl(getPropertyStringNode(propertyName), Priority.VALUE);
    }

    @Override
    public Entity ref(String propertyName, Consumer<GroupReferenceSpecification> specificationCode) {
        return getExpression2(EntityImpl::new, getPropertyStringNode(propertyName), GroupReferenceSpecificationImpl::new, specificationCode);
    }

    @Override
    public Condition isNull() {
        return getExpression2(ConditionImpl::new, Helper.IS_NULL_NODE, Priority.EQUALITY_AND_RELATION, this);
    }

    @Override
    public Condition isNotNull() {
        return getExpression2(ConditionImpl::new, Helper.IS_NOT_NULL_NODE, Priority.EQUALITY_AND_RELATION, this);
    }
}
