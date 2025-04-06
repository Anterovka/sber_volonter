package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.common.Procedure3;
import sbp.com.sbt.dataspace.feather.common.Procedure4;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.modeldescription.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.Node.node;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.checkType;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getConditionStringNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getExpressionWithConditionNode;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetConditionStringNodeFunction;
import static sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl.Helper.getGetExpressionStringNodeFunction;

/**
 * Implementation of a primitive expression
 */
class PrimitiveExpressionImpl extends CalculatedExpression implements PrimitiveExpression {

    DataType type;

    /**
     * @param getPrepareFunctionFunction The function for obtaining the preparation function
     */
    PrimitiveExpressionImpl(Function<PrimitiveExpressionImpl, BiConsumer<SqlQueryProcessor, ExpressionContext>> getPrepareFunctionFunction) {
        prepareFunction = getPrepareFunctionFunction.apply(this);
    }

    /**
     * Get signature
     */
    String getSignature() {
        return "${" + type + "}";
    }

    /**
     * Get date type of method
     *
     * @param type Тип
     */
    DataType getMethodDateType(DataType type) {
        return type == DataType.OFFSET_DATETIME ? DataType.OFFSET_DATETIME : DataType.DATETIME;
    }

    /**
     * Get primitive expression
     *
     * @param code Код
     */
    PrimitiveExpressionImpl getPrimitiveExpression(BiConsumer<SqlDialect, PrimitiveExpressionImpl> code) {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            code.accept(sqlQueryProcessor.requestData.sqlDialect, primitiveExpression);
            primitiveExpression.getConditionStringNodeFunction = getConditionStringNodeFunction;
        });
    }

    /**
     * Get primitive expression
     *
     * @param code Код
     */
    PrimitiveExpressionImpl getPrimitiveExpression2(BiConsumer<SqlDialect, PrimitiveExpressionImpl> code) {
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            code.accept(sqlQueryProcessor.requestData.sqlDialect, primitiveExpression);
        });
    }

    /**
     * Get primitive expression
     *
     * @param code                 Код
     * @param primitiveExpression1 Primitive expression 1
     */
    PrimitiveExpressionImpl getPrimitiveExpression(Procedure3<SqlDialect, PrimitiveExpressionImpl, PrimitiveExpressionImpl> code, PrimitiveExpression primitiveExpression1) {
        PrimitiveExpressionImpl primitiveExpression1Impl = (PrimitiveExpressionImpl) primitiveExpression1;
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression1Impl.prepare(sqlQueryProcessor, expressionContext);
            code.call(sqlQueryProcessor.requestData.sqlDialect, primitiveExpression, primitiveExpression1Impl);
            primitiveExpression.getConditionStringNodeFunction = getGetConditionStringNodeFunction(this, primitiveExpression1Impl);
        });
    }

    /**
     * Get primitive expression
     *
     * @param code                 Код
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpression2 Primitive expression 2
     */
    PrimitiveExpressionImpl getPrimitiveExpression(Procedure4<SqlDialect, PrimitiveExpressionImpl, PrimitiveExpressionImpl, PrimitiveExpressionImpl> code, PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        PrimitiveExpressionImpl primitiveExpression1Impl = (PrimitiveExpressionImpl) primitiveExpression1;
        PrimitiveExpressionImpl primitiveExpression2Impl = (PrimitiveExpressionImpl) primitiveExpression2;
        return new PrimitiveExpressionImpl(primitiveExpression -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression1Impl.prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression2Impl.prepare(sqlQueryProcessor, expressionContext);
            code.call(sqlQueryProcessor.requestData.sqlDialect, primitiveExpression, primitiveExpression1Impl, primitiveExpression2Impl);
            primitiveExpression.getConditionStringNodeFunction = getGetConditionStringNodeFunction(this, primitiveExpression1Impl);
            primitiveExpression.getConditionStringNodeFunction = getGetConditionStringNodeFunction(this, primitiveExpression2Impl);
        });
    }

    /**
     * Get condition
     *
     * @param code Код
     */
    ConditionImpl getCondition(Consumer<ConditionImpl> code) {
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            code.accept(condition);
            Supplier<Node<String>> currentGetExpressionStringNodeFunction = condition.getExpressionStringNodeFunction;
            condition.getExpressionStringNodeFunction = () -> getConditionStringNode(currentGetExpressionStringNodeFunction.get(), this);
        });
    }

    /**
     * Get condition
     *
     * @param code                Код
     * @param primitiveExpression Primitive expression
     */
    ConditionImpl getCondition(BiConsumer<ConditionImpl, PrimitiveExpressionImpl> code, PrimitiveExpression primitiveExpression) {
        PrimitiveExpressionImpl primitiveExpressionImpl = (PrimitiveExpressionImpl) primitiveExpression;
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpressionImpl.prepare(sqlQueryProcessor, expressionContext);
            code.accept(condition, primitiveExpressionImpl);
            Supplier<Node<String>> currentGetExpressionStringNodeFunction = condition.getExpressionStringNodeFunction;
            condition.getExpressionStringNodeFunction = () -> getConditionStringNode(getConditionStringNode(currentGetExpressionStringNodeFunction.get(), this), primitiveExpressionImpl);
        });
    }

    /**
     * Get condition
     *
     * @param code             Код
     * @param conditionalGroup Conditional group
     */
    ConditionImpl getCondition(BiConsumer<ConditionImpl, ConditionalGroupImpl> code, ConditionalGroup conditionalGroup) {
        ConditionalGroupImpl conditionalGroupImpl = (ConditionalGroupImpl) conditionalGroup;
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            conditionalGroupImpl.prepare(sqlQueryProcessor, expressionContext);
            code.accept(condition, conditionalGroupImpl);
            Supplier<Node<String>> currentGetExpressionStringNodeFunction = condition.getExpressionStringNodeFunction;
            condition.getExpressionStringNodeFunction = () -> getConditionStringNode(getConditionStringNode(currentGetExpressionStringNodeFunction.get(), this), conditionalGroupImpl);
        });
    }

    @Override
    public PrimitiveExpression neg() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.MINUS2_NODE, get());
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException("-" + getSignature());
            }
        });
    }

    @Override
    public PrimitiveExpression upper() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.UPPER_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$upper");
            }
        });
    }

    @Override
    public PrimitiveExpression lower() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.LOWER_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$lower");
            }
        });
    }

    @Override
    public PrimitiveExpression length() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.LENGTH_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$length");
            }
        });
    }

    @Override
    public PrimitiveExpression trim() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.TRIM_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$trim");
            }
        });
    }

    @Override
    public PrimitiveExpression ltrim() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.LTRIM_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$ltrim");
            }
        });
    }

    @Override
    public PrimitiveExpression rtrim() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.RTRIM_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$rtrim");
            }
        });
    }

    @Override
    public PrimitiveExpression round() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.ROUND_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$round");
            }
        });
    }

    @Override
    public PrimitiveExpression ceil() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.CEIL_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$ceil");
            }
        });
    }

    @Override
    public PrimitiveExpression floor() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.FLOOR_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$floor");
            }
        });
    }

    @Override
    public PrimitiveExpression hash() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.hash(get());
            primitiveExpression.type = DataType.BIG_DECIMAL;
        });
    }

    @Override
    public PrimitiveExpression asString() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = this::get;
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.castAsString(get());
                primitiveExpression.type = DataType.STRING;
            } else if (type == DataType.DATE) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.TO_CHAR_NODE, get(), Helper.DATE_FORMAT_BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else if (type == DataType.DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.TO_CHAR_NODE, get(), Helper.DATETIME_FORMAT_BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else if (type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.castOffsetDateTimeAsString(get());
                primitiveExpression.type = DataType.STRING;
            } else if (type == DataType.TIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.castTimeAsString(get());
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$asString");
            }
        });
    }

    @Override
    public PrimitiveExpression asBigDecimal() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.castAsBigDecimal(get());
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = this::get;
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$asBigDecimal");
            }
        });
    }

    @Override
    public PrimitiveExpression asDate() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.TO_DATE_NODE, get(), Helper.DATE_FORMAT_BRACKET_R_NODE);
                primitiveExpression.type = DataType.DATE;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$asDate");
            }
        });
    }

    @Override
    public PrimitiveExpression asDateTime() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.TO_TIMESTAMP_NODE, get(), Helper.DATETIME_FORMAT_BRACKET_R_NODE);
                primitiveExpression.type = DataType.DATETIME;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$asDateTime");
            }
        });
    }

    @Override
    public PrimitiveExpression asOffsetDateTime() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.castStringAsOffsetDateTime(get());
                primitiveExpression.type = DataType.OFFSET_DATETIME;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$asOffsetDateTime");
            }
        });
    }

    @Override
    public PrimitiveExpression asTime() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.castStringAsTime(get());
                primitiveExpression.type = DataType.TIME;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$asTime");
            }
        });
    }

    @Override
    public PrimitiveExpression year() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_YEAR_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$year");
            }
        });
    }

    @Override
    public PrimitiveExpression month() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_MONTH_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$month");
            }
        });
    }

    @Override
    public PrimitiveExpression day() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_DAY_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$day");
            }
        });
    }

    @Override
    public PrimitiveExpression hour() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_HOUR_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$hour");
            }
        });
    }

    @Override
    public PrimitiveExpression minute() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_MINUTE_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$minute");
            }
        });
    }

    @Override
    public PrimitiveExpression second() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.getSecond(get());
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$second");
            }
        });
    }

    @Override
    public PrimitiveExpression offsetHour() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_TIMEZONE_HOUR_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$offsetHour");
            }
        });
    }

    @Override
    public PrimitiveExpression offsetMinute() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.EXTRACT_TIMEZONE_MINUTE_FROM_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$offsetMinute");
            }
        });
    }

    @Override
    public PrimitiveExpression date() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.getDate(get());
                primitiveExpression.type = DataType.DATE;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$date");
            }
        });
    }

    @Override
    public PrimitiveExpression time() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.DATETIME || type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.getTime(get());
                primitiveExpression.type = DataType.TIME;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$time");
            }
        });
    }

    @Override
    public PrimitiveExpression dateTime() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.CAST_NODE, get(), Helper.AS_DATETIME_NODE);
                primitiveExpression.type = DataType.DATETIME;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$dateTime");
            }
        });
    }

    @Override
    public PrimitiveExpression offset() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (type == DataType.OFFSET_DATETIME) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.getOffset(get());
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$offset");
            }
        });
    }

    @Override
    public PrimitiveExpression abs() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.ABS_BRACKET_L_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$abs");
            }
        });
    }

    @Override
    public PrimitiveExpression bitNot() {
        return getPrimitiveExpression((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.bitNot(get());
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException("~" + getSignature());
            }
        });
    }

    @Override
    public PrimitiveExpression bitAnd(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression0, primitiveExpressionImpl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression0.getExpressionStringNodeFunction = () -> sqlDialect.bitAnd(get(), primitiveExpressionImpl.get());
                primitiveExpression0.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " & " + primitiveExpressionImpl.getSignature());
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression bitOr(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression0, primitiveExpressionImpl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression0.getExpressionStringNodeFunction = () -> sqlDialect.bitOr(get(), primitiveExpressionImpl.get());
                primitiveExpression0.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " | " + primitiveExpressionImpl.getSignature());
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression bitXor(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression0, primitiveExpressionImpl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression0.getExpressionStringNodeFunction = () -> sqlDialect.bitXor(get(), primitiveExpressionImpl.get());
                primitiveExpression0.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " ^ " + primitiveExpressionImpl.getSignature());
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression shiftLeft(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression0, primitiveExpressionImpl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression0.getExpressionStringNodeFunction = () -> sqlDialect.shiftLeft(get(), primitiveExpressionImpl.get());
                primitiveExpression0.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " << " + primitiveExpressionImpl.getSignature());
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression shiftRight(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression0, primitiveExpressionImpl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression0.getExpressionStringNodeFunction = () -> sqlDialect.shiftRight(get(), primitiveExpressionImpl.get());
                primitiveExpression0.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " >> " + primitiveExpressionImpl.getSignature());
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression plus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        Function<PrimitiveExpressionImpl, UnsupportedOperationException> exceptionInitializer = primitiveExpression -> new UnsupportedOperationException(getSignature() + " + " + primitiveExpression.getSignature());
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.CONCAT_BRACKET_L_NODE, get(), Helper.COMMA_NODE, primitiveExpression1Impl.get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                if (Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                    primitiveExpression.getExpressionStringNodeFunction = getGetExpressionStringNodeFunction(Helper.PLUS_NODE, this, primitiveExpression1Impl);
                    primitiveExpression.type = DataType.BIG_DECIMAL;
                } else if (Helper.DATE_TYPES.contains(primitiveExpression1Impl.type)) {
                    primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.addDaysToDateTime(primitiveExpression1Impl.get(), get());
                    primitiveExpression.type = primitiveExpression1Impl.type;
                } else {
                    throw exceptionInitializer.apply(primitiveExpression1Impl);
                }
            } else if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.addDaysToDateTime(get(), primitiveExpression1Impl.get());
                primitiveExpression.type = type;
            } else {
                throw exceptionInitializer.apply(primitiveExpression1Impl);
            }
        }, primitiveExpression1);
    }

    @Override
    public PrimitiveExpression minus(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = getGetExpressionStringNodeFunction(Helper.MINUS_NODE, this, primitiveExpression1Impl);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.subDaysFromDateTime(get(), primitiveExpression1Impl.get());
                primitiveExpression.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + " - " + primitiveExpression1Impl.getSignature());
            }
        }, primitiveExpression1);
    }

    @Override
    public PrimitiveExpression mul(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = getGetExpressionStringNodeFunction(Helper.MUL_NODE, this, primitiveExpression1Impl);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " * " + primitiveExpression1Impl.getSignature());
            }
        }, primitiveExpression1);
    }

    @Override
    public PrimitiveExpression div(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.div(get(), primitiveExpression1Impl.get());
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " / " + primitiveExpression1Impl.getSignature());
            }
        }, primitiveExpression1);
    }

    @Override
    public PrimitiveExpression substr(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl, primitiveExpression2Impl) -> {
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type) && Helper.NUMBER_TYPES.contains(primitiveExpression2Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.substr(get(), primitiveExpression1Impl.get(), primitiveExpression2Impl.get());
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$substr(" + primitiveExpression1Impl.getSignature() + ", " + primitiveExpression2Impl.getSignature() + ")");
            }
        }, primitiveExpression1, primitiveExpression2);
    }

    @Override
    public PrimitiveExpression substr(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.substr(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$substr(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression replace(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl, primitiveExpression2Impl) -> {
            if (Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpression1Impl.type) && Helper.STRING_TYPES.contains(primitiveExpression2Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.REPLACE_BRACKET_L_NODE, get(), Helper.COMMA_NODE, primitiveExpression1Impl.get(), Helper.COMMA_NODE, primitiveExpression2Impl.get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$replace(" + primitiveExpression1Impl.getSignature() + ", " + primitiveExpression2Impl.getSignature() + ")");
            }
        }, primitiveExpression1, primitiveExpression2);
    }

    @Override
    public PrimitiveExpression lpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl, primitiveExpression2Impl) -> {
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type) && Helper.STRING_TYPES.contains(primitiveExpression2Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.lpad(get(), primitiveExpression1Impl.get(), primitiveExpression2Impl.get());
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$lpad(" + primitiveExpression1Impl.getSignature() + ", " + primitiveExpression2Impl.getSignature() + ")");
            }
        }, primitiveExpression1, primitiveExpression2);
    }

    @Override
    public PrimitiveExpression rpad(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl, primitiveExpression2Impl) -> {
            if (Helper.STRING_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type) && Helper.STRING_TYPES.contains(primitiveExpression2Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> sqlDialect.rpad(get(), primitiveExpression1Impl.get(), primitiveExpression2Impl.get());
                primitiveExpression.type = DataType.STRING;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$rpad(" + primitiveExpression1Impl.getSignature() + ", " + primitiveExpression2Impl.getSignature() + ")");
            }
        }, primitiveExpression1, primitiveExpression2);
    }

    @Override
    public PrimitiveExpression addMilliseconds(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addMillisecondsToDateTime(sqlDialect.processDate(type, get()), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addMilliseconds(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression addSeconds(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addSecondsToDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addSeconds(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression addMinutes(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addMinutesToDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addMinutes(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression addHours(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addHoursToDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addHours(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression addDays(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addDaysToDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addDays(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression addMonths(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addMonthsToDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addMonths(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression addYears(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.addYearsToDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$addYears(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subMilliseconds(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subMillisecondsFromDateTime(sqlDialect.processDate(type, get()), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subMilliseconds(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subSeconds(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subSecondsFromDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subSeconds(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subMinutes(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subMinutesFromDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subMinutes(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subHours(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subHoursFromDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = getMethodDateType(type);
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subHours(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subDays(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subDaysFromDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subDays(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subMonths(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subMonthsFromDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subMonths(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public PrimitiveExpression subYears(PrimitiveExpression primitiveExpression) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression2, primitiveExpressionImpl) -> {
            if (Helper.DATE_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type)) {
                primitiveExpression2.getExpressionStringNodeFunction = () -> sqlDialect.subYearsFromDateTime(get(), primitiveExpressionImpl.get());
                primitiveExpression2.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$subYears(" + primitiveExpressionImpl.getSignature() + ")");
            }
        }, primitiveExpression);
    }

    @Override
    public Condition isNull() {
        return getCondition(condition -> condition.getExpressionStringNodeFunction = () -> node(get(), Helper.IS_NULL_NODE));
    }

    @Override
    public Condition isNotNull() {
        return getCondition(condition -> condition.getExpressionStringNodeFunction = () -> node(get(), Helper.IS_NOT_NULL_NODE));
    }

    @Override
    public Condition eq(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionImpl.type))
                || (type == DataType.TIME && primitiveExpressionImpl.type == DataType.TIME)
                || (type == DataType.BOOLEAN && primitiveExpressionImpl.type == DataType.BOOLEAN))) {
                throw new UnsupportedOperationException(getSignature() + " == " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.EQ_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition eq(ConditionalGroup conditionalGroup) {
        return getCondition((condition, conditionalGroupImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(conditionalGroupImpl.type))
                || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(conditionalGroupImpl.type))
                || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(conditionalGroupImpl.type))
                || (type == DataType.TIME && conditionalGroupImpl.type == DataType.TIME)
                || (type == DataType.BOOLEAN && conditionalGroupImpl.type == DataType.BOOLEAN))) {
                throw new UnsupportedOperationException(getSignature() + " == " + conditionalGroupImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.EQ_NODE, conditionalGroupImpl.get());
        }, conditionalGroup);
    }

    @Override
    public Condition notEq(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionImpl.type))
                || (type == DataType.TIME && primitiveExpressionImpl.type == DataType.TIME)
                || (type == DataType.BOOLEAN && primitiveExpressionImpl.type == DataType.BOOLEAN))) {
                throw new UnsupportedOperationException(getSignature() + " != " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.NOT_EQ_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition notEq(ConditionalGroup conditionalGroup) {
        return getCondition((condition, conditionalGroupImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(conditionalGroupImpl.type))
                || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(conditionalGroupImpl.type))
                || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(conditionalGroupImpl.type))
                || (type == DataType.TIME && conditionalGroupImpl.type == DataType.TIME)
                || (type == DataType.BOOLEAN && conditionalGroupImpl.type == DataType.BOOLEAN))) {
                throw new UnsupportedOperationException(getSignature() + " != " + conditionalGroupImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.NOT_EQ_NODE, conditionalGroupImpl.get());
        }, conditionalGroup);
    }

    @Override
    public Condition gt(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionImpl.type))
                || (type == DataType.TIME && primitiveExpressionImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " > " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.GT_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition gt(ConditionalGroup conditionalGroup) {
        return getCondition((condition, conditionalGroupImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(conditionalGroupImpl.type))
                    || (type == DataType.TIME && conditionalGroupImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " > " + conditionalGroupImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.GT_NODE, conditionalGroupImpl.get());
        }, conditionalGroup);
    }

    @Override
    public Condition ltOrEq(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionImpl.type))
                    || (type == DataType.TIME && primitiveExpressionImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " <= " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.LT_OR_EQ_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition ltOrEq(ConditionalGroup conditionalGroup) {
        return getCondition((condition, conditionalGroupImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(conditionalGroupImpl.type))
                    || (type == DataType.TIME && conditionalGroupImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " <= " + conditionalGroupImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.LT_OR_EQ_NODE, conditionalGroupImpl.get());
        }, conditionalGroup);
    }

    @Override
    public Condition lt(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type))
                || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionImpl.type))
                || (type == DataType.TIME && primitiveExpressionImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " < " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.LT_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition lt(ConditionalGroup conditionalGroup) {
        return getCondition((condition, conditionalGroupImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(conditionalGroupImpl.type))
                    || (type == DataType.TIME && conditionalGroupImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " < " + conditionalGroupImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.LT_NODE, conditionalGroupImpl.get());
        }, conditionalGroup);
    }

    @Override
    public Condition gtOrEq(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionImpl.type))
                    || (type == DataType.TIME && primitiveExpressionImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " >= " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.GT_OR_EQ_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition gtOrEq(ConditionalGroup conditionalGroup) {
        return getCondition((condition, conditionalGroupImpl) -> {
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(conditionalGroupImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(conditionalGroupImpl.type))
                    || (type == DataType.TIME && conditionalGroupImpl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " >= " + conditionalGroupImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.GT_OR_EQ_NODE, conditionalGroupImpl.get());
        }, conditionalGroup);
    }

    @Override
    public Condition like(PrimitiveExpression primitiveExpression) {
        return getCondition((condition, primitiveExpressionImpl) -> {
            if (!(Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionImpl.type))) {
                throw new UnsupportedOperationException(getSignature() + " $like " + primitiveExpressionImpl.getSignature());
            }
            condition.getExpressionStringNodeFunction = () -> node(get(), Helper.LIKE_NODE, primitiveExpressionImpl.get());
        }, primitiveExpression);
    }

    @Override
    public Condition between(PrimitiveExpression primitiveExpression1, PrimitiveExpression primitiveExpression2) {
        PrimitiveExpressionImpl primitiveExpression1Impl = (PrimitiveExpressionImpl) primitiveExpression1;
        PrimitiveExpressionImpl primitiveExpression2Impl = (PrimitiveExpressionImpl) primitiveExpression2;
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression1Impl.prepare(sqlQueryProcessor, expressionContext);
            primitiveExpression2Impl.prepare(sqlQueryProcessor, expressionContext);
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpression1Impl.type) && Helper.STRING_TYPES.contains(primitiveExpression2Impl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type) && Helper.NUMBER_TYPES.contains(primitiveExpression2Impl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpression1Impl.type) && Helper.DATE_TYPES.contains(primitiveExpression2Impl.type))
                    || (type == DataType.TIME && primitiveExpression1Impl.type == DataType.TIME && primitiveExpression2Impl.type == DataType.TIME))) {
                throw new UnsupportedOperationException(getSignature() + " $between (" + primitiveExpression1Impl.getSignature() + ", " + primitiveExpression2Impl.getSignature() + ')');
            }
            Supplier<Node<String>> currentGetExpressionStringNodeFunction = () -> node(get(), Helper.BETWEEN_NODE, primitiveExpression1Impl.get(), Helper.AND_NODE, primitiveExpression2Impl.get());
            condition.getExpressionStringNodeFunction = () -> getConditionStringNode(getConditionStringNode(getConditionStringNode(currentGetExpressionStringNodeFunction.get(), this), primitiveExpression1Impl), primitiveExpression2Impl);
        });
    }

    @Override
    public Condition in(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        List<PrimitiveExpressionImpl> primitiveExpressionImpls = Stream.concat(
                        Stream.of(primitiveExpression1),
                        Arrays.stream(primitiveExpressions))
                .map(PrimitiveExpressionImpl.class::cast)
                .collect(Collectors.toList());
        Function<PrimitiveExpressionImpl, UnsupportedOperationException> exceptionInitializer = primitiveExpression2 -> new UnsupportedOperationException(getSignature() + " $in [" + primitiveExpressionImpls.stream().map(PrimitiveExpressionImpl::getSignature).collect(Collectors.joining(", ")) + "]");
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpressionImpls.forEach(primitiveExpression -> primitiveExpression.prepare(sqlQueryProcessor, expressionContext));
            if (Helper.STRING_TYPES.contains(type)) {
                checkType(Helper.STRING_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                checkType(Helper.NUMBER_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
            } else if (Helper.DATE_TYPES.contains(type)) {
                checkType(Helper.DATE_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
            } else if (type == DataType.TIME) {
                checkType(Helper.TIME_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
            } else if (type == DataType.BOOLEAN) {
                checkType(Helper.BOOLEAN_TYPES, exceptionInitializer, primitiveExpressionImpls.stream());
            } else {
                throw exceptionInitializer.apply(null);
            }
            Supplier<Node<String>> currentGetExpressionStringNodeFunction = () -> {
                List<Node<String>> nodes = new ArrayList<>(2 + primitiveExpressionImpls.size() * 2);
                nodes.add(get());
                nodes.add(Helper.IN_NODE);
                addNodeListToNodes(nodes, Helper.COMMA_NODE, primitiveExpressionImpls.stream()
                        .map(PrimitiveExpressionImpl::get));
                nodes.add(Helper.BRACKET_R_NODE);
                return node(nodes);
            };
            condition.getExpressionStringNodeFunction = () -> {
                Pointer<Node<String>> resultPointer = new Pointer<>(getConditionStringNode(currentGetExpressionStringNodeFunction.get(), this));
                primitiveExpressionImpls.forEach(primitiveExpression -> resultPointer.object = getConditionStringNode(resultPointer.object, primitiveExpression));
                return resultPointer.object;
            };
        });
    }

    @Override
    public Condition in(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        PrimitiveExpressionsCollectionImpl primitiveExpressionsCollectionImpl = (PrimitiveExpressionsCollectionImpl) primitiveExpressionsCollection;
        return new ConditionImpl(condition -> (sqlQueryProcessor, expressionContext) -> {
            prepare(sqlQueryProcessor, expressionContext);
            primitiveExpressionsCollectionImpl.prepare(sqlQueryProcessor, expressionContext);
            if (!((Helper.STRING_TYPES.contains(type) && Helper.STRING_TYPES.contains(primitiveExpressionsCollectionImpl.type))
                    || (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpressionsCollectionImpl.type))
                    || (Helper.DATE_TYPES.contains(type) && Helper.DATE_TYPES.contains(primitiveExpressionsCollectionImpl.type))
                    || (type == DataType.TIME && primitiveExpressionsCollectionImpl.type == DataType.TIME)
                    || (type == DataType.BOOLEAN && primitiveExpressionsCollectionImpl.type == DataType.BOOLEAN))) {
                throw new UnsupportedOperationException(getSignature() + " $in " + primitiveExpressionsCollectionImpl.getSignature());
            }
            Supplier<Node<String>> currentGetExpressionStringNodeFunction = () -> node(get(), Helper.IN2_NODE, primitiveExpressionsCollectionImpl.getQueryNode(UnaryOperator.identity()));
            condition.getExpressionStringNodeFunction = () -> getConditionStringNode(getConditionStringNode(currentGetExpressionStringNodeFunction.get(), this), primitiveExpressionsCollectionImpl);
        });
    }

    @Override
    public PrimitiveExpression mod(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.MOD_BRACKET_L_NODE, get(), Helper.COMMA_NODE, primitiveExpression1Impl.get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + " % " + primitiveExpression1Impl.getSignature());
            }
        }, primitiveExpression1);
    }

    @Override
    public PrimitiveExpression power(PrimitiveExpression power) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.POWER_NODE, get(), Helper.COMMA_NODE, primitiveExpression1Impl.get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$power(" + primitiveExpression1Impl.getSignature() + ")");
            }
        }, power);
    }

    @Override
    public PrimitiveExpression log(PrimitiveExpression base) {
        return getPrimitiveExpression((sqlDialect, primitiveExpression, primitiveExpression1Impl) -> {
            if (Helper.NUMBER_TYPES.contains(type) && Helper.NUMBER_TYPES.contains(primitiveExpression1Impl.type)) {
                primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.LOG_NODE, primitiveExpression1Impl.get(), Helper.COMMA_NODE, get(), Helper.BRACKET_R_NODE);
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$log(" + primitiveExpression1Impl.getSignature() + ")");
            }
        }, base);
    }

    @Override
    public PrimitiveExpression min() {
        return getPrimitiveExpression2((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.type = type;
            } else if (type == DataType.TIME) {
                primitiveExpression.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$min");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.MIN_BRACKET_L_NODE, getExpressionWithConditionNode(sqlDialect, get(), getConditionStringNodeFunction.get()), Helper.BRACKET_R_NODE);
        });
    }

    @Override
    public PrimitiveExpression max() {
        return getPrimitiveExpression2((sqlDialect, primitiveExpression) -> {
            if (Helper.STRING_TYPES.contains(type)) {
                primitiveExpression.type = DataType.STRING;
            } else if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else if (Helper.DATE_TYPES.contains(type)) {
                primitiveExpression.type = type;
            } else if (type == DataType.TIME) {
                primitiveExpression.type = type;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$max");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.MAX_BRACKET_L_NODE, getExpressionWithConditionNode(sqlDialect, get(), getConditionStringNodeFunction.get()), Helper.BRACKET_R_NODE);
        });
    }

    @Override
    public PrimitiveExpression sum() {
        return getPrimitiveExpression2((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$sum");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.SUM_BRACKET_L_NODE, getExpressionWithConditionNode(sqlDialect, get(), getConditionStringNodeFunction.get()), Helper.BRACKET_R_NODE);
        });
    }

    @Override
    public PrimitiveExpression avg() {
        return getPrimitiveExpression2((sqlDialect, primitiveExpression) -> {
            if (Helper.NUMBER_TYPES.contains(type)) {
                primitiveExpression.type = DataType.BIG_DECIMAL;
            } else {
                throw new UnsupportedOperationException(getSignature() + ".$avg");
            }
            primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.AVG_BRACKET_L_NODE, getExpressionWithConditionNode(sqlDialect, sqlDialect.processIntegerNumber(get()), getConditionStringNodeFunction.get()), Helper.BRACKET_R_NODE);
        });
    }

    @Override
    public PrimitiveExpression count() {
        return getPrimitiveExpression2((sqlDialect, primitiveExpression) -> {
            primitiveExpression.type = DataType.BIG_DECIMAL;
            primitiveExpression.getExpressionStringNodeFunction = () -> node(Helper.COUNT_BRACKET_L_NODE, getExpressionWithConditionNode(sqlDialect, get(), getConditionStringNodeFunction.get()), Helper.BRACKET_R_NODE);
        });
    }
}
