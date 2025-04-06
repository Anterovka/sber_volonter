package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

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
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.expressionscommon.CommonExpressionsHelper.getSpecification;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.cast;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.checkType;

/**
 * Implementation of the expression handler
 */
class ExpressionsProcessorImpl implements ExpressionsProcessor {

    EntityDescription rootEntityDescription;

    /**
     * @param rootEntityDescription Description of the root entity
     */
    ExpressionsProcessorImpl(EntityDescription rootEntityDescription) {
        this.rootEntityDescription = rootEntityDescription;
    }

    @Override
    public Entity root(Consumer<RootSpecification> specificationCode) {
        return new EntityImpl(context -> cast(rootEntityDescription, getSpecification(RootSpecificationImpl::new, specificationCode)));
    }

    @Override
    public PrimitiveExpression elemPE() {
        return new PrimitiveExpressionImpl(context -> context.elementDataType);
    }

    @Override
    public Entity elemE(Consumer<EntityElementSpecification> specificationCode) {
        return new EntityImpl(context -> cast(context.elementEntityDescription, getSpecification(EntityElementSpecificationImpl::new, specificationCode)));
    }

    @Override
    public Entity aliasedEntity(String alias, Consumer<AliasedEntitySpecification> specificationCode) {
        return new EntityImpl(context -> cast(context.aliasedEntityDescriptions.get(alias), getSpecification(AliasedEntitySpecificationImpl::new, specificationCode)));
    }

    @Override
    public PrimitiveExpression prim(Object primitiveValue) {
        DataType type;
        if (primitiveValue instanceof String) {
            type = DataType.STRING;
        } else if (primitiveValue instanceof BigDecimal
                || primitiveValue instanceof Long) {
            type = DataType.BIG_DECIMAL;
        } else if (primitiveValue instanceof LocalDate) {
            type = DataType.DATE;
        } else if (primitiveValue instanceof LocalDateTime) {
            type = DataType.DATETIME;
        } else if (primitiveValue instanceof OffsetDateTime) {
            type = DataType.OFFSET_DATETIME;
        } else if (primitiveValue instanceof LocalTime) {
            type = DataType.TIME;
        } else {
            type = DataType.BOOLEAN;
        }
        return new PrimitiveExpressionImpl(context -> type);
    }

    @Override
    public PrimitiveExpression now() {
        return new PrimitiveExpressionImpl(context -> DataType.OFFSET_DATETIME);
    }

    @Override
    public PrimitiveExpression coalesce(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return new PrimitiveExpressionImpl(context -> {
            List<DataType> typesList = Stream.concat(
                    Stream.of(primitiveExpression1),
                    Arrays.stream(primitiveExpressions))
                    .map(PrimitiveExpressionImpl.class::cast)
                    .map(primitiveExpression -> primitiveExpression.getTypeFunction.apply(context))
                    .collect(Collectors.toList());
            Function<DataType, UnsupportedOperationException> exceptionInitializer = type -> new UnsupportedOperationException("coalesce(" + typesList.stream().map(Helper::getSignature).collect(Collectors.joining(", ")) + ")");
            DataType type = typesList.get(0);
            DataType result;
            if (Helper.STRING_TYPES.contains(type)) {
                checkType(Helper.STRING_TYPES, exceptionInitializer, typesList.stream());
                result = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                checkType(Helper.NUMBER_TYPES, exceptionInitializer, typesList.stream());
                result = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type)) {
                checkType(Helper.DATE_TYPES, exceptionInitializer, typesList.stream());
                result = DataType.DATETIME;
            } else if (typesList.stream().allMatch(type2 -> type2 == type)) {
                result = type;
            } else {
                throw exceptionInitializer.apply(null);
            }
            return result;
        });
    }

    @Override
    public EntitiesCollection entities(Consumer<EntitiesSpecification> specificationCode) {
        return new EntitiesCollectionImpl(context -> rootEntityDescription.getModelDescription().getEntityDescription(getSpecification(EntitiesSpecificationImpl::new, specificationCode).getType()));
    }

    @Override
    public PrimitiveExpression rawPE(Object raw) {
        throw new UnexpectedRawException(raw);
    }

    @Override
    public ConditionalGroup any(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return Helper.CONDITIONAL_GROUP;
    }

    @Override
    public ConditionalGroup any(Object[] array) {
        return Helper.CONDITIONAL_GROUP;
    }

    @Override
    public ConditionalGroup all(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return Helper.CONDITIONAL_GROUP;
    }

    @Override
    public ConditionalGroup all(Object[] array) {
        return Helper.CONDITIONAL_GROUP;
    }
}
