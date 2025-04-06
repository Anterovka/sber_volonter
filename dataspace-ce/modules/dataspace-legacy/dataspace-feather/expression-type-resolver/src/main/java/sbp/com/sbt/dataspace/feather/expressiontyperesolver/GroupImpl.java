package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.Group;
import sbp.com.sbt.dataspace.feather.expressions.GroupReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressionscommon.GroupReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;

import java.util.function.Consumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.cast;

/**
 * Implementation of grouping
 */
class GroupImpl implements Group {

    Function<Context, GroupDescription> getGroupDescriptionFunction;

    /**
     * @param getGroupDescriptionFunction Function for obtaining grouping description depending on context
     */
    GroupImpl(Function<Context, GroupDescription> getGroupDescriptionFunction) {
        this.getGroupDescriptionFunction = getGroupDescriptionFunction;
    }

    @Override
    public PrimitiveExpression prim(String propertyName) {
        return new PrimitiveExpressionImpl(context -> getGroupDescriptionFunction.apply(context).getPrimitiveDescription(propertyName).getType());
    }

    @Override
    public Entity ref(String propertyName, Consumer<GroupReferenceSpecification> specificationCode) {
        return new EntityImpl(context -> cast(getGroupDescriptionFunction.apply(context).getReferenceDescription(propertyName).getEntityDescription(), getSpecification(GroupReferenceSpecificationImpl::new, specificationCode)));
    }

    @Override
    public Condition isNull() {
        return Helper.CONDITION;
    }

    @Override
    public Condition isNotNull() {
        return Helper.CONDITION;
    }
}
