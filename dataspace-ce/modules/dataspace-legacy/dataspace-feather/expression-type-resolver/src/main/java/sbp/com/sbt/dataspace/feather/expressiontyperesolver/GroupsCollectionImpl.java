package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressionscommon.GroupsCollectionReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;

import java.util.function.Consumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.cast;

/**
 * Implementation of the grouping collection
 */
class GroupsCollectionImpl implements GroupsCollection {

    Function<Context, GroupDescription> getGroupDescriptionFunction;

    /**
     * @param getGroupDescriptionFunction Function for obtaining grouping description depending on context
     */
    GroupsCollectionImpl(Function<Context, GroupDescription> getGroupDescriptionFunction) {
        this.getGroupDescriptionFunction = getGroupDescriptionFunction;
    }

    @Override
    public PrimitiveExpressionsCollection prim(String propertyName) {
        return new PrimitiveExpressionsCollectionImpl(context -> getGroupDescriptionFunction.apply(context).getPrimitiveDescription(propertyName).getType());
    }

    @Override
    public EntitiesCollection ref(String propertyName, Consumer<GroupsCollectionReferenceSpecification> specificationCode) {
        return new EntitiesCollectionImpl(context -> cast(getGroupDescriptionFunction.apply(context).getReferenceDescription(propertyName).getEntityDescription(), getSpecification(GroupsCollectionReferenceSpecificationImpl::new, specificationCode)));
    }
}
