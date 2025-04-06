package sbp.com.sbt.dataspace.feather.expressiontyperesolver;

import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.function.Function;
import java.util.function.Supplier;

import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getAvgType;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getMaxType;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getMinType;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getSignature;
import static sbp.com.sbt.dataspace.feather.expressiontyperesolver.Helper.getSumType;

/**
 * Implementation of a primitive expression
 */
class PrimitiveExpressionImpl implements PrimitiveExpression {

    Function<Context, DataType> getTypeFunction;

    /**
     * @param getTypeFunction The function for obtaining the type depending on the context
     */
    PrimitiveExpressionImpl(Function<Context, DataType> getTypeFunction) {
        this.getTypeFunction = getTypeFunction;
    }

    /**
     * Get date type of method
     *
     * @param type Тип
     */
    DataType getMethodDateType(DataType type) {
        return type == DataType.OFFSET_DATETIME ? DataType.OFFSET_DATETIME : DataType.DATETIME;
    }

    @Override
    public PrimitiveExpression neg() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException("-" + getSignature(type));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression upper() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$upper");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression lower() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$lower");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression length() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$length");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression trim() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$trim");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression ltrim() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$ltrim");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression rtrim() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$rtrim");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression round() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$round");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression ceil() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$ceil");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression floor() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$floor");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression hash() {
        return new PrimitiveExpressionImpl(context -> DataType.BIG_DECIMAL);
    }

    @Override
    public PrimitiveExpression asString() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.STRING;
            } else if (Helper.DATE_TYPES.contains(type)) {
                result = DataType.STRING;
            } else if (type == DataType.TIME) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$asString");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression asBigDecimal() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$asBigDecimal");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression asDate() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.DATE;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$asDate");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression asDateTime() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.DATETIME;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$asDateTime");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression asOffsetDateTime() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.OFFSET_DATETIME;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$asOffsetDateTime");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression asTime() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type)) {
                result = DataType.TIME;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$asTime");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression year() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$year");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression month() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$month");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression day() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$day");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression hour() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$hour");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression minute() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$minute");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression second() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$second");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression offsetHour() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.OFFSET_DATETIME) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$offsetHour");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression offsetMinute() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.OFFSET_DATETIME) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$offsetMinute");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression date() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                result = DataType.DATE;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$date");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression time() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                result = DataType.TIME;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$time");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression dateTime() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.OFFSET_DATETIME) {
                result = DataType.DATETIME;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$dateTime");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression offset() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (type == DataType.OFFSET_DATETIME) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$offset");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression abs() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$abs");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression bitNot() {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException("~" + getSignature(type));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression bitAnd(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " & " + getSignature(type2));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression bitOr(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " | " + getSignature(type2));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression bitXor(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " ^ " + getSignature(type2));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression shiftLeft(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " << " + getSignature(type2));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression shiftRight(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " >> " + getSignature(type2));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression plus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            Supplier<UnsupportedOperationException> exceptionInitializer = () -> new UnsupportedOperationException(getSignature(type) + " + " + getSignature(type1));
            if (Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(type1)) {
                result = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                if (Helper.NUMBER_TYPES.contains(type1)) {
                    result = DataType.BIG_DECIMAL;
                } else if (Helper.DATE_TYPES.contains(type1)) {
                    result = type1;
                } else {
                    throw exceptionInitializer.get();
                }
            } else if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1)) {
                result = type;
            } else {
                throw exceptionInitializer.get();
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression minus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1)) {
                result = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " - " + getSignature(type1));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression mul(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " * " + getSignature(type1));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression div(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " / " + getSignature(type1));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression substr(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression2).getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$substr(" + getSignature(type1) + ", " + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression substr(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$substr(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression replace(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression2).getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(type1) && Helper.STRING_TYPES.contains(type2)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$replace(" + getSignature(type1) + ", " + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression lpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression2).getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1) && Helper.STRING_TYPES.contains(type2)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$lpad(" + getSignature(type1) + ", " + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression rpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression2).getTypeFunction.apply(context);
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1) && Helper.STRING_TYPES.contains(type2)) {
                result = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$rpad(" + getSignature(type1) + ", " + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addMilliseconds(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$addMilliseconds(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addSeconds(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".addSeconds(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addMinutes(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$addMinutes(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addHours(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$addHours(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addDays(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$addDays(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addMonths(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$addMonths(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression addYears(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$addYears(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subMilliseconds(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subMilliseconds(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subSeconds(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subSeconds(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subMinutes(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subMinutes(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subHours(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subHours(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subDays(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subDays(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subMonths(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subMonths(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression subYears(PrimitiveExpression primitiveExpression) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type2 = ((PrimitiveExpressionImpl) primitiveExpression).getTypeFunction.apply(context);
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type2)) {
                result = type;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$subYears(" + getSignature(type2) + ")");
            }
            return result;
        });
    }

    @Override
    public Condition isNull() {
        return Helper.CONDITION;
    }

    @Override
    public Condition isNotNull() {
        return Helper.CONDITION;
    }

    @Override
    public Condition eq(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition eq(ConditionalGroup conditionalGroup) {
        return Helper.CONDITION;
    }

    @Override
    public Condition notEq(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition notEq(ConditionalGroup conditionalGroup) {
        return Helper.CONDITION;
    }

    @Override
    public Condition gt(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition gt(ConditionalGroup conditionalGroup) {
        return Helper.CONDITION;
    }

    @Override
    public Condition ltOrEq(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition ltOrEq(ConditionalGroup conditionalGroup) {
        return Helper.CONDITION;
    }

    @Override
    public Condition lt(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition lt(ConditionalGroup conditionalGroup) {
        return Helper.CONDITION;
    }

    @Override
    public Condition gtOrEq(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition gtOrEq(ConditionalGroup conditionalGroup) {
        return Helper.CONDITION;
    }

    @Override
    public Condition like(PrimitiveExpression primitiveExpression) {
        return Helper.CONDITION;
    }

    @Override
    public Condition between(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return Helper.CONDITION;
    }

    @Override
    public Condition in(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return Helper.CONDITION;
    }

    @Override
    public Condition in(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return Helper.CONDITION;
    }

    @Override
    public PrimitiveExpression mod(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType type1 = ((PrimitiveExpressionImpl) primitiveExpression1).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(type1)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + " % " + getSignature(type1));
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression power(PrimitiveExpression power) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType powerType = ((PrimitiveExpressionImpl) power).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(powerType)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$power(" + getSignature(powerType) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression log(PrimitiveExpression base) {
        return new PrimitiveExpressionImpl(context -> {
            DataType result;
            DataType type = getTypeFunction.apply(context);
            DataType baseType = ((PrimitiveExpressionImpl) base).getTypeFunction.apply(context);
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(baseType)) {
                result = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature(type) + ".$log(" + getSignature(baseType) + ")");
            }
            return result;
        });
    }

    @Override
    public PrimitiveExpression min() {
        return new PrimitiveExpressionImpl(context -> getMinType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression max() {
        return new PrimitiveExpressionImpl(context -> getMaxType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression sum() {
        return new PrimitiveExpressionImpl(context -> getSumType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression avg() {
        return new PrimitiveExpressionImpl(context -> getAvgType(getTypeFunction.apply(context)));
    }

    @Override
    public PrimitiveExpression count() {
        return new PrimitiveExpressionImpl(context -> DataType.BIG_DECIMAL);
    }
}
