package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionBackReferenceReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollectionReferenceSpecification;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesCollectionBackReferenceReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.EntitiesCollectionReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.util.function.Consumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.cast;

/**
 *
 */
class EntitiesCollectionImpl implements EntitiesCollection {

    Function<Context, EntityDescription> getEntityDescriptionFunction;

    /**
     * @param getEntityDescriptionFunction The function of obtaining entity description depending on the context
     */
    EntitiesCollectionImpl(Function<Context, EntityDescription> getEntityDescriptionFunction) {
        this.getEntityDescriptionFunction = getEntityDescriptionFunction;
    }

    @Override
    public PrimitiveExpressionsCollection type() {
        return new PrimitiveExpressionsCollectionImpl(context -> DataType.STRING);
    }

    @Override
    public PrimitiveExpressionsCollection id() {
        return new PrimitiveExpressionsCollectionImpl(context -> DataType.STRING);
    }

    @Override
    public PrimitiveExpressionsCollection prim(String propertyName) {
        return new PrimitiveExpressionsCollectionImpl(context -> getEntityDescriptionFunction.apply(context).getPrimitiveDescription(propertyName).getType());
    }

    @Override
    public EntitiesCollection ref(String propertyName, Consumer<EntitiesCollectionReferenceSpecification> specificationCode) {
        return new EntitiesCollectionImpl(context -> cast(getEntityDescriptionFunction.apply(context).getReferenceDescription(propertyName).getEntityDescription(), getSpecification(EntitiesCollectionReferenceSpecificationImpl::new, specificationCode)));
    }

    @Override
    public EntitiesCollection refB(String propertyName, Consumer<EntitiesCollectionBackReferenceReferenceSpecification> specificationCode) {
        return new EntitiesCollectionImpl(context -> cast(getEntityDescriptionFunction.apply(context).getReferenceBackReferenceDescription(propertyName).getOwnerEntityDescription(), getSpecification(EntitiesCollectionBackReferenceReferenceSpecificationImpl::new, specificationCode)));
    }

    @Override
    public GroupsCollection group(String propertyName) {
        return new GroupsCollectionImpl(context -> getEntityDescriptionFunction.apply(context).getGroupDescription(propertyName));
    }

    @Override
    public PrimitiveExpressionsCollection map(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionsCollectionImpl(context -> ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(new Context(getEntityDescriptionFunction.apply(context)).withAliasedEntityDescriptions(context.aliasedEntityDescriptions)));
    }
}
