package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

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
import sbp.com.sbt.dataspace.feather.expressionscommon.ReferenceSpecificationImpl;
import sbp.com.sbt.dataspace.feather.expressionscommon.ReferencesCollectionSpecificationImpl;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.util.function.Consumer;
import java.util.function.Function;

import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.cast;

/**
 * Implementation of the entity
 */
class EntityImpl implements Entity {

    Function<Context, EntityDescription> getEntityDescriptionFunction;

    /**
     * @param getEntityDescriptionFunction The function of obtaining entity description depending on the context
     */
    EntityImpl(Function<Context, EntityDescription> getEntityDescriptionFunction) {
        this.getEntityDescriptionFunction = getEntityDescriptionFunction;
    }

    @Override
    public PrimitiveExpression type() {
        return new PrimitiveExpressionImpl(context -> DataType.STRING);
    }

    @Override
    public PrimitiveExpression id() {
        return new PrimitiveExpressionImpl(context -> DataType.STRING);
    }

    @Override
    public PrimitiveExpression prim(String propertyName) {
        return new PrimitiveExpressionImpl(context -> getEntityDescriptionFunction.apply(context).getPrimitiveDescription(propertyName).getType());
    }

    @Override
    public PrimitiveExpressionsCollection prims(String propertyName, Consumer<PrimitivesCollectionSpecification> specificationCode) {
        return new PrimitiveExpressionsCollectionImpl(context -> getEntityDescriptionFunction.apply(context).getPrimitivesCollectionDescription(propertyName).getType());
    }

    @Override
    public Entity ref(String propertyName, Consumer<ReferenceSpecification> specificationCode) {
        return new EntityImpl(context -> {
            ReferenceSpecification specification = getSpecification(ReferenceSpecificationImpl::new, specificationCode);
            EntityDescription result = cast(getEntityDescriptionFunction.apply(context).getReferenceDescription(propertyName).getEntityDescription(), specification);
            if (specification != null && specification.getAlias() != null) {
                context.aliasedEntityDescriptions.put(specification.getAlias(), result);
            }
            return result;
        });
    }

    @Override
    public Entity refB(String propertyName, Consumer<BackReferenceReferenceSpecification> specificationCode) {
        return new EntityImpl(context -> {
            BackReferenceReferenceSpecification specification = getSpecification(BackReferenceReferenceSpecificationImpl::new, specificationCode);
            EntityDescription result = cast(getEntityDescriptionFunction.apply(context).getReferenceBackReferenceDescription(propertyName).getOwnerEntityDescription(), specification);
            if (specification != null && specification.getAlias() != null) {
                context.aliasedEntityDescriptions.put(specification.getAlias(), result);
            }
            return result;
        });
    }

    @Override
    public EntitiesCollection refs(String propertyName, Consumer<ReferencesCollectionSpecification> specificationCode) {
        return new EntitiesCollectionImpl(context -> cast(getEntityDescriptionFunction.apply(context).getReferencesCollectionDescription(propertyName).getEntityDescription(), getSpecification(ReferencesCollectionSpecificationImpl::new, specificationCode)));
    }

    @Override
    public EntitiesCollection refsB(String propertyName, Consumer<BackReferenceReferencesCollectionSpecification> specificationCode) {
        return new EntitiesCollectionImpl(context -> cast(getEntityDescriptionFunction.apply(context).getReferencesCollectionBackReferenceDescription(propertyName).getOwnerEntityDescription(), getSpecification(BackReferenceReferencesCollectionSpecificationImpl::new, specificationCode)));
    }

    @Override
    public Group group(String propertyName) {
        return new GroupImpl(context -> getEntityDescriptionFunction.apply(context).getGroupDescription(propertyName));
    }

    @Override
    public Condition exists() {
        return Helper.CONDITION;
    }
}
