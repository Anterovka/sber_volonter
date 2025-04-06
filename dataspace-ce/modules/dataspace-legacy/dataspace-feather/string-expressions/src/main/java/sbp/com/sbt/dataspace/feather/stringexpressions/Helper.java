package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.Node;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithCondition;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithElementAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithEntityType;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithParameters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.addNodeListToNodes;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.Node.node;

/**
 * Assistant
 */
final class Helper {

    static final Node<String> DOT_NODE = node(".");
    static final Node<String> BRACKET_L_NODE = node("(");
    static final Node<String> BRACKET_R_NODE = node(")");
    static final Node<String> BRACKET2_L_NODE = node("{");
    static final Node<String> BRACKET2_R_NODE = node("}");
    static final Node<String> BRACKET3_L_NODE = node("[");
    static final Node<String> BRACKET3_R_NODE = node("]");
    static final Node<String> COMMA_NODE = node(",");
    static final Node<String> TYPE_SPECIFICATION_PART_NODE = node("type=");
    static final Node<String> ALIAS_SPECIFICATION_PART_NODE = node("alias=");
    static final Node<String> ELEMENT_ALIAS_SPECIFICATION_PART_NODE = node("elemAlias=");
    static final Node<String> COND_SPECIFICATION_PART_NODE = node("cond=");
    static final Node<String> PARAMS_SPECIFICATION_PART_NODE = node("params=");
    static final Node<String> NOT_NODE = node("!");
    static final Node<String> AND_NODE = node("&&");
    static final Node<String> OR_NODE = node("||");
    static final Node<String> MINUS_NODE = node("-");
    static final Node<String> UPPER_NODE = node(".$upper");
    static final Node<String> LOWER_NODE = node(".$lower");
    static final Node<String> LENGTH_NODE = node(".$length");
    static final Node<String> TRIM_NODE = node(".$trim");
    static final Node<String> LTRIM_NODE = node(".$ltrim");
    static final Node<String> RTRIM_NODE = node(".$rtrim");
    static final Node<String> ROUND_NODE = node(".$round");
    static final Node<String> CEIL_NODE = node(".$ceil");
    static final Node<String> FLOOR_NODE = node(".$floor");
    static final Node<String> HASH_NODE = node(".$hash");
    static final Node<String> AS_STRING_NODE = node(".$asString");
    static final Node<String> AS_BIG_DECIMAL_NODE = node(".$asBigDecimal");
    static final Node<String> AS_DATE_NODE = node(".$asDate");
    static final Node<String> AS_DATETIME_NODE = node(".$asDateTime");
    static final Node<String> AS_OFFSET_DATETIME_NODE = node(".$asOffsetDateTime");
    static final Node<String> AS_TIME_NODE = node(".$asTime");
    static final Node<String> YEAR_NODE = node(".$year");
    static final Node<String> MONTH_NODE = node(".$month");
    static final Node<String> DAY_NODE = node(".$day");
    static final Node<String> HOUR_NODE = node(".$hour");
    static final Node<String> MINUTE_NODE = node(".$minute");
    static final Node<String> SECOND_NODE = node(".$second");
    static final Node<String> OFFSET_HOUR_NODE = node(".$offsetHour");
    static final Node<String> OFFSET_MINUTE_NODE = node(".$offsetMinute");
    static final Node<String> DATE_NODE = node(".$date");
    static final Node<String> TIME_NODE = node(".$time");
    static final Node<String> DATETIME_NODE = node(".$dateTime");
    static final Node<String> OFFSET_NODE = node(".$offset");
    static final Node<String> ABS_NODE = node(".$abs");
    static final Node<String> BIT_NOT_NODE = node("~");
    static final Node<String> PLUS_NODE = node("+");
    static final Node<String> MUL_NODE = node("*");
    static final Node<String> DIV_NODE = node("/");
    static final Node<String> BIT_AND_NODE = node("&");
    static final Node<String> BIT_OR_NODE = node("|");
    static final Node<String> BIT_XOR_NODE = node("^");
    static final Node<String> SHIFT_LEFT_NODE = node("<<");
    static final Node<String> SHIFT_RIGHT_NODE = node(">>");
    static final Node<String> SUBSTR_NODE = node(".$substr(");
    static final Node<String> REPLACE_NODE = node(".$replace(");
    static final Node<String> ADD_MILLISECONDS_NODE = node(".$addMilliseconds(");
    static final Node<String> ADD_SECONDS_NODE = node(".$addSeconds(");
    static final Node<String> ADD_MINUTES_NODE = node(".$addMinutes(");
    static final Node<String> ADD_HOURS_NODE = node(".$addHours(");
    static final Node<String> ADD_DAYS_NODE = node(".$addDays(");
    static final Node<String> ADD_MONTHS_NODE = node(".$addMonths(");
    static final Node<String> ADD_YEARS_NODE = node(".$addYears(");
    static final Node<String> SUB_MILLISECONDS_NODE = node(".$subMilliseconds(");
    static final Node<String> SUB_SECONDS_NODE = node(".$subSeconds(");
    static final Node<String> SUB_MINUTES_NODE = node(".$subMinutes(");
    static final Node<String> SUB_HOURS_NODE = node(".$subHours(");
    static final Node<String> SUB_DAYS_NODE = node(".$subDays(");
    static final Node<String> SUB_MONTHS_NODE = node(".$subMonths(");
    static final Node<String> SUB_YEARS_NODE = node(".$subYears(");
    static final Node<String> LPAD_NODE = node(".$lpad(");
    static final Node<String> RPAD_NODE = node(".$rpad(");
    static final Node<String> IS_NULL_NODE = node("==null");
    static final Node<String> IS_NOT_NULL_NODE = node("!=null");
    static final Node<String> EQ_NODE = node("==");
    static final Node<String> NOT_EQ_NODE = node("!=");
    static final Node<String> GT_NODE = node(">");
    static final Node<String> LT_OR_EQ_NODE = node("<=");
    static final Node<String> LT_NODE = node("<");
    static final Node<String> GT_OR_EQ_NODE = node(">=");
    static final Node<String> LIKE_NODE = node("$like");
    static final Node<String> BETWEEN_NODE = node("$between");
    static final Node<String> IN_NODE = node("$in");
    static final Node<String> TYPE_NODE = node(".$type");
    static final Node<String> ID_NODE = node(".$id");
    static final Node<String> MIN_NODE = node(".$min");
    static final Node<String> MAX_NODE = node(".$max");
    static final Node<String> SUM_NODE = node(".$sum");
    static final Node<String> AVG_NODE = node(".$avg");
    static final Node<String> COUNT_NODE = node(".$count");
    static final Node<String> EXISTS_NODE = node(".$exists");
    static final Node<String> ROOT_NODE = node("root");
    static final Node<String> NOW = node("now");
    static final Node<String> IT_NODE = node("it");
    static final Node<String> COALESCE_NODE = node("coalesce(");
    static final Node<String> ENTITIES_NODE = node("entities");
    static final Node<String> AT_NODE = node("@");
    static final Node<String> QUOTE_NODE = node("'");
    static final Node<String> QUOTE4_NODE = node("''''");
    static final Node<String> DATE_PREFIX_NODE = node("D");
    static final Node<String> TIME_PREFIX_NODE = node("T");
    static final Node<String> MOD_NODE = node("%");
    static final Node<String> AS_BOOLEAN_NODE = node(".$asBoolean");
    static final Node<String> MAP_NODE = node(".$map(");
    static final Node<String> ASSIGN_NODE = node("=");
    static final Node<String> NULL_NODE = node("null");
    static final Node<String> ANY_NODE = node("any(");
    static final Node<String> ALL_NODE = node("all(");
    static final Node<String> POWER_NODE = node(".$power(");
    static final Node<String> LOG_NODE = node(".$log(");

    private Helper() {
    }

    /**
     * Check implementation
     *
     * @param primitiveExpression Primitive expression
     * @return The primitive expression provided
     */
    static PrimitiveExpressionImpl checkImpl(PrimitiveExpression primitiveExpression) {
        if (!(primitiveExpression instanceof PrimitiveExpressionImpl)) {
            throw new UsageOfNotTargetImplementationException("Primitive expression");
        }
        return (PrimitiveExpressionImpl) primitiveExpression;
    }

    /**
     * Check implementation
     *
     * @param conditionalGroup Conditional group
     * @return The приведенная условная группа
     */
    static ConditionalGroupImpl checkImpl(ConditionalGroup conditionalGroup) {
        if (!(conditionalGroup instanceof ConditionalGroupImpl)) {
            throw new UsageOfNotTargetImplementationException("Conditional group");
        }
        return (ConditionalGroupImpl) conditionalGroup;
    }

    /**
     * Check implementation
     *
     * @param primitiveExpressionsCollection of primitive expressions
     * @return The resulting collection of primitive expressions
     */
    static PrimitiveExpressionsCollectionImpl checkImpl(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        if (!(primitiveExpressionsCollection instanceof PrimitiveExpressionsCollectionImpl)) {
            throw new UsageOfNotTargetImplementationException("Collection of primitive expressions");
        }
        return (PrimitiveExpressionsCollectionImpl) primitiveExpressionsCollection;
    }

    /**
     * Check implementation
     *
     * @param entity Entity
     * @return The provided entity
     */
    static EntityImpl checkImpl(Entity entity) {
        if (!(entity instanceof EntityImpl)) {
            throw new UsageOfNotTargetImplementationException("Entity");
        }
        return (EntityImpl) entity;
    }

    /**
     * Check implementation
     *
     * @param entitiesCollection The collection of entities
     * @return The provided collection of entities
     */
    static EntitiesCollectionImpl checkImpl(EntitiesCollection entitiesCollection) {
        if (!(entitiesCollection instanceof EntitiesCollectionImpl)) {
            throw new UsageOfNotTargetImplementationException("Entity collection");
        }
        return (EntitiesCollectionImpl) entitiesCollection;
    }

    /**
     * Check implementation
     *
     * @param condition The condition
     * @return The given condition
     */
    static ConditionImpl checkImpl(Condition condition) {
        if (!(condition instanceof ConditionImpl)) {
            throw new UsageOfNotTargetImplementationException("Condition");
        }
        return (ConditionImpl) condition;
    }

    /**
     * Get primitive value node
     *
     * @param primitiveValue Primitive value
     * @param isNullAllowed  Is null allowed
     * @param isListAllowed  Is list allowed
     */
    static Node<String> getPrimitiveValueNode(Object primitiveValue, boolean isNullAllowed, boolean isListAllowed) {
        if (isNullAllowed && primitiveValue == null) {
            return NULL_NODE;
        }
        checkNotNull(primitiveValue, "Primitive value");
        if (primitiveValue instanceof Character) {
            char character = (Character) primitiveValue;
            return character == '\'' ? QUOTE4_NODE : node(QUOTE_NODE, node(String.valueOf(character)), QUOTE_NODE);
        } else if (primitiveValue instanceof String) {
            return node(QUOTE_NODE, node(((String) primitiveValue).replace("'", "''")), QUOTE_NODE);
        } else if (primitiveValue instanceof Byte
                || primitiveValue instanceof Short
                || primitiveValue instanceof Integer
                || primitiveValue instanceof Long
                || primitiveValue instanceof BigDecimal
                || primitiveValue instanceof Boolean) {
            return node(primitiveValue.toString());
        } else if (primitiveValue instanceof Float) {
            return node(String.valueOf((float) primitiveValue));
        } else if (primitiveValue instanceof Double) {
            return node(String.valueOf((double) primitiveValue));
        } else if (primitiveValue instanceof LocalDate) {
            return node(Helper.DATE_PREFIX_NODE, node(DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate) primitiveValue)));
        } else if (primitiveValue instanceof LocalDateTime) {
            return node(Helper.DATE_PREFIX_NODE, node(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((LocalDateTime) primitiveValue)));
        } else if (primitiveValue instanceof OffsetDateTime) {
            return node(Helper.DATE_PREFIX_NODE, node(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format((OffsetDateTime) primitiveValue)));
        } else if (primitiveValue instanceof LocalTime) {
            return node(Helper.TIME_PREFIX_NODE, node(DateTimeFormatter.ISO_LOCAL_TIME.format((LocalTime) primitiveValue)));
        } else if (isListAllowed && primitiveValue instanceof List) {
            List<Node<String>> nodes = new ArrayList<>();
            nodes.add(BRACKET3_L_NODE);
            addNodeListToNodes(nodes, COMMA_NODE, ((List<Object>) primitiveValue).stream().map(primitiveValue2 -> getPrimitiveValueNode(primitiveValue2, true, false)));
            nodes.add(BRACKET3_R_NODE);
            return node(nodes);
        } else {
            throw new UnexpectedPrimitiveValueClassException(primitiveValue.getClass());
        }
    }

    /**
     * Get string node with specification
     *
     * @param prefixNode     Prefix node
     * @param parameterNodes Nodes of parameters
     */
    static Node<String> getStringNodeWithSpecification(Node<String> prefixNode, List<Node<String>> parameterNodes) {
        List<Node<String>> nodes = new ArrayList<>(2 + parameterNodes.size() * 2);
        nodes.add(prefixNode);
        nodes.add(BRACKET2_L_NODE);
        addNodeListToNodes(nodes, COMMA_NODE, parameterNodes.stream());
        nodes.add(BRACKET2_R_NODE);
        return node(nodes);
    }

    /**
     * Get condition
     *
     * @param checkImplFunction Function for checking implementation
     * @param operatorNode      Operator node
     * @param expression1       Expression 1
     * @param expression2       Expression 2
     * @param expressions       Expressions
     */
    static <E, I extends StringBasedObject> Condition getCondition(Function<E, I> checkImplFunction, Node<String> operatorNode, I expression1, E expression2, E[] expressions) {
        List<Node<String>> nodes = new ArrayList<>(7 + expressions.length * 2);
        expression1.addStringNode2(nodes, Priority.EQUALITY_AND_RELATION);
        nodes.add(operatorNode);
        nodes.add(BRACKET3_L_NODE);
        nodes.add(checkImplFunction.apply(expression2).stringNode);
        Arrays.stream(expressions)
                .map(checkImplFunction)
                .forEach(expression -> {
                    nodes.add(COMMA_NODE);
                    nodes.add(expression.stringNode);
                });
        nodes.add(BRACKET3_R_NODE);
        return new ConditionImpl(node(nodes), Priority.EQUALITY_AND_RELATION);
    }

    /**
     * Get expression
     *
     * @param expressionInitializer The expression initializer
     * @param operatorNode          Operator node
     * @param priority              Priority
     * @param expression             Expression
     * @param <I>                   Type of expression implementation
     */
    static <I extends StringBasedObject> I getExpression(BiFunction<Node<String>, Priority, I> expressionInitializer, Node<String> operatorNode, Priority priority, StringBasedObject expression) {
        List<Node<String>> nodes = new ArrayList<>(4);
        nodes.add(operatorNode);
        expression.addStringNode2(nodes, priority);
        return expressionInitializer.apply(node(nodes), priority);
    }

    /**
     * Get expression
     *
     * @param expressionInitializer The expression initializer
     * @param operatorNode          Operator node
     * @param priority              Priority
     * @param expression             Expression
     * @param <I>                   Type of expression implementation
     */
    static <I extends StringBasedObject> I getExpression2(BiFunction<Node<String>, Priority, I> expressionInitializer, Node<String> operatorNode, Priority priority, StringBasedObject expression) {
        List<Node<String>> nodes = new ArrayList<>(4);
        expression.addStringNode2(nodes, priority);
        nodes.add(operatorNode);
        return expressionInitializer.apply(node(nodes), priority);
    }

    /**
     * Get expression
     *
     * @param expressionInitializer The expression initializer
     * @param checkImplFunction     The implementation verification function
     * @param operatorNode          Operator node
     * @param priority              Priority
     * @param associative           Is the operation associative
     * @param expression1           Expression 1
     * @param expression2           Expression 2
     * @param expressions             Expressions
     * @param <E>                   The type of expression
     * @param <I>                   Type of expression implementation
     */
    static <E, I extends StringBasedObject> I getExpression(BiFunction<Node<String>, Priority, I> expressionInitializer, Function<E, I> checkImplFunction, Node<String> operatorNode, Priority priority, boolean associative, I expression1, E expression2, E[] expressions) {
        List<Node<String>> nodes = new ArrayList<>(7 + expressions.length * 4);
        expression1.addStringNode2(nodes, priority);
        Stream.concat(
                        Stream.of(expression2),
                        Arrays.stream(expressions))
                .map(checkImplFunction)
                .forEach(expression -> {
                    nodes.add(operatorNode);
                    if (associative) {
                        expression.addStringNode2(nodes, priority);
                    } else {
                        expression.addStringNode(nodes, priority);
                    }
                });
        return expressionInitializer.apply(node(nodes), priority);
    }

    /**
     * Get expression
     *
     * @param expressionInitializer The expression initializer
     * @param checkImplFunction     The implementation verification function
     * @param operatorNode          The operator node
     * @param priority              Priority
     * @param associative           Is the operation associative
     * @param expression1           Expression 1
     * @param expression2           Expression 2
     * @param <E>                   The type of expression
     * @param <I>                   Type of expression implementation
     */
    static <E, I extends StringBasedObject, J extends StringBasedObject> J getExpression(BiFunction<Node<String>, Priority, J> expressionInitializer, Function<E, I> checkImplFunction, Node<String> operatorNode, Priority priority, boolean associative, I expression1, E expression2) {
        List<Node<String>> nodes = new ArrayList<>(7);
        expression1.addStringNode2(nodes, priority);
        nodes.add(operatorNode);
        I expression2Impl = checkImplFunction.apply(expression2);
        if (associative) {
            expression2Impl.addStringNode2(nodes, priority);
        } else {
            expression2Impl.addStringNode(nodes, priority);
        }
        return expressionInitializer.apply(node(nodes), priority);
    }

    /**
     * Add parameter specification type node
     *
     * @param parameterNodes              Parameter nodes
     * @param specificationWithEntityType The specification with the entity type
     */
    static void addTypeSpecificationParameterNode(List<Node<String>> parameterNodes, SpecificationWithEntityType<?> specificationWithEntityType) {
        if (specificationWithEntityType.getType() != null) {
            parameterNodes.add(node(TYPE_SPECIFICATION_PART_NODE, node(specificationWithEntityType.getType())));
        }
    }

    /**
     * Add parameter specification alias node
     *
     * @param parameterNodes         Parameter nodes
     * @param specificationWithAlias Specification with alias
     */
    static void addAliasSpecificationParameterNode(List<Node<String>> parameterNodes, SpecificationWithAlias<?> specificationWithAlias) {
        if (specificationWithAlias.getAlias() != null) {
            parameterNodes.add(node(ALIAS_SPECIFICATION_PART_NODE, node(specificationWithAlias.getAlias())));
        }
    }

    /**
     * Add parameter node of element alias specification
     *
     * @param parameterNodes                Parameter nodes
     * @param specificationWithElementAlias The specification with the element alias
     */
    static void addElementAliasSpecificationParameterNode(List<Node<String>> parameterNodes, SpecificationWithElementAlias<?> specificationWithElementAlias) {
        if (specificationWithElementAlias.getElementAlias() != null) {
            parameterNodes.add(node(ELEMENT_ALIAS_SPECIFICATION_PART_NODE, node(specificationWithElementAlias.getElementAlias())));
        }
    }

    /**
     * Add parameter specification condition node
     *
     * @param parameterNodes             Parameter nodes
     * @param specificationWithCondition Specification with condition
     */
    static void addConditionSpecificationParameterNode(List<Node<String>> parameterNodes, SpecificationWithCondition<?> specificationWithCondition) {
        if (specificationWithCondition.getCondition() != null) {
            parameterNodes.add(node(COND_SPECIFICATION_PART_NODE, checkImpl(specificationWithCondition.getCondition()).stringNode));
        }
    }

    /**
     * Add parameter specification node
     *
     * @param parameterNodes              Parameter nodes
     * @param specificationWithParameters The specification with parameters
     */
    static void addParametersSpecificationParameterNode(List<Node<String>> parameterNodes, SpecificationWithParameters<?> specificationWithParameters) {
        if (!specificationWithParameters.getParameters().isEmpty()) {
            List<Node<String>> nodes = new ArrayList<>();
            nodes.add(PARAMS_SPECIFICATION_PART_NODE);
            nodes.add(BRACKET2_L_NODE);
            addNodeListToNodes(nodes, COMMA_NODE, specificationWithParameters.getParameters().entrySet().stream().map(entry -> node(node(entry.getKey()), ASSIGN_NODE, getPrimitiveValueNode(entry.getValue(), true, true))));
            nodes.add(BRACKET2_R_NODE);
            parameterNodes.add(node(nodes));
        }
    }

    /**
     * Get expression
     *
     * @param prefixNode               The prefix node
     * @param expressionInitializer    The expression initializer
     * @param specificationInitializer Initializer of the specification
     * @param specificationCode        Specification code
     * @param <E>                      The type of expression
     * @param <S>                      The type of specification
     */
    static <E extends StringBasedObject, S extends SpecificationWithEntityType<?>> E getExpression(BiFunction<Node<String>, Priority, E> expressionInitializer, Node<String> prefixNode, Supplier<S> specificationInitializer, Consumer<? super S> specificationCode) {
        if (specificationCode != null) {
            S specification = specificationInitializer.get();
            specificationCode.accept(specification);
            List<Node<String>> parameterNodes = new ArrayList<>(1);
            addTypeSpecificationParameterNode(parameterNodes, specification);
            prefixNode = getStringNodeWithSpecification(prefixNode, parameterNodes);
        }
        return expressionInitializer.apply(prefixNode, Priority.VALUE);
    }

    /**
     * Get expression
     *
     * @param prefixNode               The prefix node
     * @param expressionInitializer    The expression initializer
     * @param specificationInitializer Initializer of the specification
     * @param specificationCode        Specification code
     * @param <E>                      The type of expression
     * @param <S>                      The type of specification
     */
    static <E extends StringBasedObject, S extends SpecificationWithEntityType<?> & SpecificationWithAlias<?>> E getExpression2(BiFunction<Node<String>, Priority, E> expressionInitializer, Node<String> prefixNode, Supplier<S> specificationInitializer, Consumer<? super S> specificationCode) {
        if (specificationCode != null) {
            S specification = specificationInitializer.get();
            specificationCode.accept(specification);
            List<Node<String>> parameterNodes = new ArrayList<>(2);
            addTypeSpecificationParameterNode(parameterNodes, specification);
            addAliasSpecificationParameterNode(parameterNodes, specification);
            prefixNode = getStringNodeWithSpecification(prefixNode, parameterNodes);
        }
        return expressionInitializer.apply(prefixNode, Priority.VALUE);
    }

    /**
     * Get entity collection
     *
     * @param prefixNode               The prefix node
     * @param expressionInitializer    The expression initializer
     * @param specificationInitializer Initializer of the specification
     * @param specificationCode        Specification code
     * @param <E>                      The type of expression
     * @param <S>                      The type of specification
     */
    static <E extends StringBasedObject, S extends SpecificationWithEntityType<?> & SpecificationWithElementAlias<?> & SpecificationWithCondition<?>> E getExpression3(BiFunction<Node<String>, Priority, E> expressionInitializer, Node<String> prefixNode, Supplier<S> specificationInitializer, Consumer<? super S> specificationCode) {
        if (specificationCode != null) {
            S specification = specificationInitializer.get();
            specificationCode.accept(specification);
            List<Node<String>> parameterNodes = new ArrayList<>(3);
            addTypeSpecificationParameterNode(parameterNodes, specification);
            addElementAliasSpecificationParameterNode(parameterNodes, specification);
            addConditionSpecificationParameterNode(parameterNodes, specification);
            prefixNode = getStringNodeWithSpecification(prefixNode, parameterNodes);
        }
        return expressionInitializer.apply(prefixNode, Priority.VALUE);
    }

    /**
     * Get entity collection
     *
     * @param prefixNode               The prefix node
     * @param expressionInitializer    The expression initializer
     * @param specificationInitializer Initializer of the specification
     * @param specificationCode        Specification code
     * @param <E>                      The type of expression
     * @param <S>                      The type of specification
     */
    static <E extends StringBasedObject, S extends SpecificationWithEntityType<?> & SpecificationWithElementAlias<?> & SpecificationWithCondition<?> & SpecificationWithParameters<?>> E getExpression4(BiFunction<Node<String>, Priority, E> expressionInitializer, Node<String> prefixNode, Supplier<S> specificationInitializer, Consumer<? super S> specificationCode) {
        if (specificationCode != null) {
            S specification = specificationInitializer.get();
            specificationCode.accept(specification);
            List<Node<String>> parameterNodes = new ArrayList<>(3);
            addTypeSpecificationParameterNode(parameterNodes, specification);
            addParametersSpecificationParameterNode(parameterNodes, specification);
            addElementAliasSpecificationParameterNode(parameterNodes, specification);
            addConditionSpecificationParameterNode(parameterNodes, specification);
            prefixNode = getStringNodeWithSpecification(prefixNode, parameterNodes);
        }
        return expressionInitializer.apply(prefixNode, Priority.VALUE);
    }

    static Node<String> getArrayNode(Object[] array) {
        List<Node<String>> nodes = new ArrayList<>(array.length * 2 + 1);
        nodes.add(Helper.BRACKET3_L_NODE);
        addNodeListToNodes(nodes, Helper.COMMA_NODE, Arrays.stream(array).map((value) -> getPrimitiveValueNode(value, false, false)));
        nodes.add(Helper.BRACKET3_R_NODE);
        return node(nodes);
    }
}
