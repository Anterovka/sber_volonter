package sbp.com.sbt.dataspace.feather.stringexpressions;

import sbp.com.sbt.dataspace.feather.common.FeatherException;
import sbp.com.sbt.dataspace.feather.common.Pointer;
import sbp.com.sbt.dataspace.feather.expressions.AliasedEntitySpecification;
import sbp.com.sbt.dataspace.feather.expressions.Condition;
import sbp.com.sbt.dataspace.feather.expressions.ConditionalGroup;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesCollection;
import sbp.com.sbt.dataspace.feather.expressions.EntitiesSpecification;
import sbp.com.sbt.dataspace.feather.expressions.Entity;
import sbp.com.sbt.dataspace.feather.expressions.EntityElementSpecification;
import sbp.com.sbt.dataspace.feather.expressions.Group;
import sbp.com.sbt.dataspace.feather.expressions.GroupsCollection;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpression;
import sbp.com.sbt.dataspace.feather.expressions.PrimitiveExpressionsCollection;
import sbp.com.sbt.dataspace.feather.expressions.RootSpecification;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithCondition;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithElementAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithEntityType;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithParameters;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Expression Parser
 */
class ExpressionsParser {

    static final Set<TokenKind> ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS = EnumSet.of(TokenKind.TYPE);
    static final Set<TokenKind> ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS2 = EnumSet.of(TokenKind.TYPE, TokenKind.ALIAS);
    static final Set<TokenKind> PRIMITIVES_COLLECTION_SPECIFICATION_SETTING_TOKEN_KINDS = EnumSet.of(TokenKind.COND);
    static final Set<TokenKind> ENTITIES_COLLECTION_SPECIFICATION_SETTING_TOKEN_KINDS = EnumSet.of(TokenKind.TYPE, TokenKind.PARAMS, TokenKind.ELEM_ALIAS, TokenKind.COND);
    static final PrimitiveExpression[] EMPTY_PRIMITIVE_EXPRESSIONS_ARRAY = new PrimitiveExpression[0];
    static final TokenKind[] EMPTY_TOKEN_KINDS_ARRAY = new TokenKind[0];

    TokenParser tokenParser;
    ModelDescription modelDescription;
    EntityDescription rootEntityDescription;
    EntityDescription elementEntityDescription;
    boolean primitiveExpressionsCollection;
    Map<String, EntityDescription> aliasedEntityDescriptions = new HashMap<>();
    Function<Consumer<RootSpecification>, Entity> rootFunction;
    Supplier<PrimitiveExpression> elemPEFunction;
    Function<Consumer<EntityElementSpecification>, Entity> elemEFunction;
    BiFunction<String, Consumer<AliasedEntitySpecification>, Entity> aliasedEntityFunction;
    Function<Object, PrimitiveExpression> primFunction;
    BiFunction<PrimitiveExpression, PrimitiveExpression[], PrimitiveExpression> coalesceFunction;
    Supplier<PrimitiveExpression> now;
    Function<Consumer<EntitiesSpecification>, EntitiesCollection> entitiesFunction;
    Function<PrimitiveExpressionsCollection, ConditionalGroup> anyFunction;
    Function<Object[], ConditionalGroup> anyFunction2;
    Function<PrimitiveExpressionsCollection, ConditionalGroup> allFunction;
    Function<Object[], ConditionalGroup> allFunction2;

    /**
     * @param expressionString                 The expression string
     * @param modelDescription                 Description of the model
     * @param rootEntityType                   The type of the root entity
     * @param entityElementType                The type of entity element
     * @param primitiveExpressionsCollection   Is it inside the collection of primitive expressions
     * @param rootFunction                     The function for obtaining the root entity
     * @param elemPEFunction                   Function for obtaining collection element (primitive expression)
     * @param elemEFunction                    Function for obtaining a collection element (entity)
     * @param aliasedEntityFunction            The function for retrieving an entity with an alias
     * @param primFunction                     Function for obtaining a primitive value
     * @param coalesceFunction                 The function for obtaining the first non-zero element
     * @param now                              Function for obtaining the current date and time (with offset)
     * @param entitiesFunction                 The function for retrieving entities
     */
    ExpressionsParser(
            String expressionString,
            ModelDescription modelDescription,
            String rootEntityType,
            String entityElementType,
            boolean primitiveExpressionsCollection,
            Function<Consumer<RootSpecification>, Entity> rootFunction,
            Supplier<PrimitiveExpression> elemPEFunction,
            Function<Consumer<EntityElementSpecification>, Entity> elemEFunction,
            BiFunction<String, Consumer<AliasedEntitySpecification>, Entity> aliasedEntityFunction,
            Function<Object, PrimitiveExpression> primFunction,
            BiFunction<PrimitiveExpression, PrimitiveExpression[], PrimitiveExpression> coalesceFunction,
            Supplier<PrimitiveExpression> now,
            Function<Consumer<EntitiesSpecification>, EntitiesCollection> entitiesFunction,
            Function<PrimitiveExpressionsCollection, ConditionalGroup> anyFunction,
            Function<Object[], ConditionalGroup> anyFunction2,
            Function<PrimitiveExpressionsCollection, ConditionalGroup> allFunction,
            Function<Object[], ConditionalGroup> allFunction2) {
        this.tokenParser = new TokenParser(expressionString);
        this.modelDescription = checkNotNull(modelDescription, "Model description");
        rootEntityDescription = modelDescription.getEntityDescription(checkNotNull(rootEntityType, "The type of the root entity"));
        if (entityElementType != null) {
            elementEntityDescription = modelDescription.getEntityDescription(entityElementType);
        }
        this.primitiveExpressionsCollection = primitiveExpressionsCollection;
        this.rootFunction = rootFunction;
        this.elemPEFunction = elemPEFunction;
        this.elemEFunction = elemEFunction;
        this.aliasedEntityFunction = aliasedEntityFunction;
        this.primFunction = primFunction;
        this.coalesceFunction = coalesceFunction;
        this.now = now;
        this.entitiesFunction = entitiesFunction;
        this.anyFunction = anyFunction;
        this.anyFunction2 = anyFunction2;
        this.allFunction = allFunction;
        this.allFunction2 = allFunction2;
    }

    /**
     * Set entity description with alias
     *
     * @param alias      Alias
     * @param entityType Entity type
     * @return Current builder
     */
    void setAliasedEntityDescription(String alias, String entityType) {
        aliasedEntityDescriptions.put(checkNotNull(alias, "Alias"), modelDescription.getEntityDescription(checkNotNull(entityType, "Entity type")));
    }

    /**
     * Are all elements of a specific type
     *
     * @param expressionsData Data of expressions
     * @param expressionType  Expression type
     */
    boolean areAllOfType(List<ExpressionData> expressionsData, ExpressionType expressionType) {
        return expressionsData.stream().allMatch(expressionData -> expressionData.type == expressionType);
    }

    /**
     * Get an array of primitive expressions
     *
     * @param expressionsData Data of expressions
     */
    <T> T[] getArray(List<ExpressionData> expressionsData, IntFunction<T[]> arrayInitializer) {
        return expressionsData.stream()
            .map(expressionData -> expressionData.data)
            .toArray(arrayInitializer);
    }

    /**
     * Get trigger for specification for type
     *
     * @param entityDescriptionPointer Pointer to the entity description
     */
    Consumer<String> getTypeSpecificationTrigger(Pointer<EntityDescription> entityDescriptionPointer) {
        return entityType -> entityDescriptionPointer.object = modelDescription.getEntityDescription(entityType);
    }

    /**
     * Get trigger for specification for type
     *
     * @param entityDescriptionPointer Pointer to the entity description
     */
    Consumer<String> getTypeSpecificationTrigger2(Pointer<EntityDescription> entityDescriptionPointer) {
        return entityType -> {
            elementEntityDescription = modelDescription.getEntityDescription(entityType);
            entityDescriptionPointer.object = elementEntityDescription;
        };
    }

    /**
     * Get trigger for specification for alias
     *
     * @param position                 Позиция
     * @param entityDescriptionPointer Pointer to the entity description
     * @param aliases                  Aliases
     */
    Consumer<String> getAliasSpecificationTrigger(int position, Pointer<EntityDescription> entityDescriptionPointer, Set<String> aliases) {
        return alias -> {
            if (aliasedEntityDescriptions.containsKey(alias)) {
                throw new DuplicateAliasFoundException(alias, tokenParser.getContext(position));
            }
            aliasedEntityDescriptions.put(alias, entityDescriptionPointer.object);
            aliases.add(alias);
        };
    }

    /**
     * Get specification code
     *
     * @param specificationSettingCodes The codes of the specification settings
     * @param <S>                       The type of specification
     */
    <S> Consumer<S> getSpecificationCode(List<Consumer<Object>> specificationSettingCodes) {
        return specificationSettingCodes.isEmpty() ? null : (specification -> specificationSettingCodes.forEach(specificationSettingCode -> specificationSettingCode.accept(specification)));
    }

    /**
     * Get the specification code for the type
     *
     * @param entityType Entity type
     * @param <S>        The type of specification
     */
    <S extends SpecificationWithEntityType<S>> Consumer<S> getTypeSpecificationCode(String entityType) {
        return specification -> specification.setType(entityType);
    }

    /**
     * Get the specification code for the parameters
     *
     * @param parameters Parameters
     * @param <S>        Type of specification
     */
    <S extends SpecificationWithParameters<S>> Consumer<S> getParametersSpecificationCode(Map<String, Object> parameters) {
        return specification -> specification.getParameters().putAll(parameters);
    }

    /**
     * Get the specification code for the alias
     *
     * @param alias Alias
     * @param <S>   The type of specification
     */
    <S extends SpecificationWithAlias<S>> Consumer<S> getAliasSpecificationCode(String alias) {
        return specification -> specification.setAlias(alias);
    }

    /**
     * Get the specification code for the element alias
     *
     * @param elementAlias Alias of the element
     * @param <S>          The type of specification
     */
    <S extends SpecificationWithElementAlias<S>> Consumer<S> getElementAliasSpecificationCode(String elementAlias) {
        return specification -> specification.setElementAlias(elementAlias);
    }

    /**
     * Get the specification code for the condition
     *
     * @param condition The condition
     * @param <S>       Type of specification
     */
    <S extends SpecificationWithCondition<S>> Consumer<S> getConditionSpecificationCode(Condition condition) {
        return specification -> specification.setCondition(condition);
    }

    /**
     * Get number
     */
    Number getNumber() {
        String image = tokenParser.getTokenImage();
        if (!image.contains(".")) {
            try {
                return Long.valueOf(image);
            } catch (NumberFormatException e) {
                //None action
            }
        }
        return new BigDecimal(image);
    }

    /**
     * Parse the root condition
     */
    Condition parseRootCondition() {
        Condition result = parseCondition();
        tokenParser.checkEnd();
        return result;
    }

    /**
     * Parse the condition
     */
    Condition parseCondition() {
        int position = tokenParser.position;
        // The string expression from the protocol is transformed into the logic of obtaining the SQL string in this method.
        ExpressionData expressionData = parseLogicalOr();
        if (expressionData.type == ExpressionType.CONDITION) {
            return (Condition) expressionData.data;
        }
        throw new NotConditionException(tokenParser.getContext(position));
    }

    /**
     * Parse logical "Or"
     *
     * @return The data of this expression
     */
    ExpressionData parseLogicalOr() {
        ExpressionData result = parseLogicalAnd();
        while (true) {
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.OR);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseLogicalAnd();
            if (result.type == ExpressionType.CONDITION && expressionData.type == ExpressionType.CONDITION) {
                result.data = ((Condition) result.data).or((Condition) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " || " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Parse logical "AND"
     *
     * @return The data of this expression
     */
    ExpressionData parseLogicalAnd() {
        ExpressionData result = parseLogicalNot();
        while (true) {
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.AND);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseLogicalNot();
            if (result.type == ExpressionType.CONDITION && expressionData.type == ExpressionType.CONDITION) {
                result.data = ((Condition) result.data).and((Condition) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " && " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Logical Negation Parsing
     *
     * @return The data of this expression
     */
    ExpressionData parseLogicalNot() {
        Queue<Consumer<ExpressionData>> logicalNotAppliers = new LinkedList<>();
        while (true) {
            tokenParser.skipSpaces();
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.NOT);
            if (tokenKind == null) {
                break;
            } else {
                logicalNotAppliers.add(expressionData -> {
                    if (expressionData.type == ExpressionType.CONDITION) {
                        expressionData.data = ((Condition) expressionData.data).not();
                    } else {
                        throw new MethodNotFoundException("!" + expressionData, tokenParser.getContext(position));
                    }
                });
            }
        }
        ExpressionData result = parseBitwiseOr();
        logicalNotAppliers.forEach(applier -> applier.accept(result));
        return result;
    }

    /**
     * Bitwise-or parsing
     *
     * @return The data of this expression
     */
    ExpressionData parseBitwiseOr() {
        ExpressionData result = parseBitwiseXor();
        while (true) {
            tokenParser.skipSpaces();
            if (tokenParser.position + 1 < tokenParser.string.length() && tokenParser.string.substring(tokenParser.position, tokenParser.position + 2).equals(TokenKind.OR.image)) {
                break;
            }
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.BIT_OR);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseBitwiseXor();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).bitOr((PrimitiveExpression) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " | " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * XOR Parsing
     *
     * @return The data of this expression
     */
    ExpressionData parseBitwiseXor() {
        ExpressionData result = parseBitwiseAnd();
        while (true) {
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.BIT_XOR);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseBitwiseAnd();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).bitXor((PrimitiveExpression) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " ^ " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Bitwise 'AND' parse
     *
     * @return The data of this expression
     */
    ExpressionData parseBitwiseAnd() {
        ExpressionData result = parseEqualityAndRelation();
        while (true) {
            tokenParser.skipSpaces();
            if (tokenParser.position + 1 < tokenParser.string.length() && tokenParser.string.substring(tokenParser.position, tokenParser.position + 2).equals(TokenKind.AND.image)) {
                break;
            }
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.BIT_AND);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseEqualityAndRelation();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).bitAnd((PrimitiveExpression) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " & " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Парсинг равенства и соотношений
     *
     * @return The data of this expression
     */
    ExpressionData parseEqualityAndRelation() {
        ExpressionData result = parseShift();
        int position = tokenParser.position;
        TokenKind tokenKind = tokenParser.parseToken(TokenKind.EQ, TokenKind.NOT_EQ, TokenKind.GT_OR_EQ, TokenKind.GT, TokenKind.LT_OR_EQ, TokenKind.LT, TokenKind.LIKE, TokenKind.BETWEEN, TokenKind.IN);
        if (tokenKind != null) {
            if (tokenKind == TokenKind.BETWEEN) {
                parseBetween(result, position);
            } else if (tokenKind == TokenKind.IN) {
                parseIn(result, position);
            } else {
                TokenKind tokenKind2 = tokenParser.parseToken(TokenKind.NULL);
                if (tokenKind2 == null) {
                    parseComparison(result, position, tokenKind);
                } else {
                    parseNullComparison(result, position, tokenKind);
                }
            }
            result.type = ExpressionType.CONDITION;
        }
        return result;
    }

    /**
     * Shift parsing
     *
     * @return The data of this expression
     */
    ExpressionData parseShift() {
        ExpressionData result = parseAdditive();
        while (true) {
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.SHIFT_LEFT, TokenKind.SHIFT_RIGHT);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseAdditive();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = tokenKind == TokenKind.SHIFT_LEFT ? ((PrimitiveExpression) result.data).shiftLeft((PrimitiveExpression) expressionData.data) : ((PrimitiveExpression) result.data).shiftRight((PrimitiveExpression) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " " + tokenKind.image + " " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Парсинг сложения
     *
     * @return The data of this expression
     */
    ExpressionData parseAdditive() {
        ExpressionData result = parseMultiplicative();
        while (true) {
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.PLUS, TokenKind.MINUS);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseMultiplicative();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = tokenKind == TokenKind.PLUS ? ((PrimitiveExpression) result.data).plus((PrimitiveExpression) expressionData.data) : ((PrimitiveExpression) result.data).minus((PrimitiveExpression) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " " + tokenKind.image + " " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Парсинг умножения
     *
     * @return The data of this expression
     */
    ExpressionData parseMultiplicative() {
        ExpressionData result = parseUnaryOperations();
        while (true) {
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.MUL, TokenKind.DIV, TokenKind.MOD2, TokenKind.MOD);
            if (tokenKind == null) {
                break;
            }
            ExpressionData expressionData = parseUnaryOperations();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                if (tokenKind == TokenKind.MUL) {
                    result.data = ((PrimitiveExpression) result.data).mul((PrimitiveExpression) expressionData.data);
                } else if (tokenKind == TokenKind.DIV) {
                    result.data = ((PrimitiveExpression) result.data).div((PrimitiveExpression) expressionData.data);
                } else {
                    result.data = ((PrimitiveExpression) result.data).mod((PrimitiveExpression) expressionData.data);
                }
            } else {
                throw new MethodNotFoundException(result + " " + tokenKind.image + " " + expressionData, tokenParser.getContext(position));
            }
        }
        return result;
    }

    /**
     * Parse unary operations
     *
     * @return The data of this expression
     */
    ExpressionData parseUnaryOperations() {
        Queue<Consumer<ExpressionData>> unaryOperationAppliers = new LinkedList<>();
        while (true) {
            tokenParser.skipSpaces();
            if (tokenParser.position + 1 < tokenParser.string.length() && tokenParser.string.charAt(tokenParser.position) == '-' && tokenParser.isDigit(tokenParser.string.charAt(tokenParser.position + 1))) {
                break;
            }
            int position = tokenParser.position;
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.MINUS, TokenKind.BIT_NOT);
            if (tokenKind == null) {
                break;
            } else if (tokenKind == TokenKind.MINUS) {
                unaryOperationAppliers.add(expressionData -> {
                    if (expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                        expressionData.data = ((PrimitiveExpression) expressionData.data).neg();
                    } else {
                        throw new MethodNotFoundException("-" + expressionData, tokenParser.getContext(position));
                    }
                });
            } else {
                unaryOperationAppliers.add(expressionData -> {
                    if (expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                        expressionData.data = ((PrimitiveExpression) expressionData.data).bitNot();
                    } else {
                        throw new MethodNotFoundException("~" + expressionData, tokenParser.getContext(position));
                    }
                });
            }
        }
        ExpressionData result = parseValue();
        unaryOperationAppliers.forEach(applier -> applier.accept(result));
        return result;
    }

    /**
     * Parse the value
     *
     * @return The data of this expression
     */
    ExpressionData parseValue() {
        //In this method, parsing of the object to which all other functions will be attached begins.
        int position = tokenParser.position;
        ExpressionData result;
        Set<String> aliases = new LinkedHashSet<>();
        TokenKind tokenKind = tokenParser.parseRequiredToken(TokenKind.BRACKET_L, TokenKind.STRING, TokenKind.NUMBER, TokenKind.OFFSET_DATETIME, TokenKind.DATETIME, TokenKind.DATE, TokenKind.TIME, TokenKind.TRUE, TokenKind.FALSE, TokenKind.IT, TokenKind.ROOT, TokenKind.AT, TokenKind.ENTITIES, TokenKind.COALESCE, TokenKind.NOW, TokenKind.ANY, TokenKind.ALL, TokenKind.ELEM);
        if (tokenKind == TokenKind.BRACKET_L) {
            // If there is a '(' in the line, then, in fact, it is necessary to parse everything again and then check the ')'
            result = parseLogicalOr();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
        } else if (tokenKind == TokenKind.IT || tokenKind == TokenKind.ROOT || tokenKind == TokenKind.ELEM || tokenKind == TokenKind.AT) {
            result = parseStartEntity(position, tokenKind);
        } else if (tokenKind == TokenKind.ENTITIES) {
            result = parseEntities(position, aliases);
        } else if (tokenKind == TokenKind.COALESCE) {
            result = parseCoalesce();
        } else if (tokenKind == TokenKind.NOW) {
            result = parseNow();
        } else if (tokenKind == TokenKind.ANY || tokenKind == TokenKind.ALL) {
            result = parseConditionalGroup(tokenKind);
        } else {
            result = parsePrimitiveValue(tokenKind);
        }
        while (true) {
            tokenKind = tokenParser.parseToken(TokenKind.DOT);
            if (tokenKind == null) {
                break;
            }
            tokenKind = tokenParser.parseToken(TokenKind.DOLLAR);
            if (tokenKind == null) {
                parseProperty(result, position, aliases);
            } else {
                parseSystemProperty(result, position);
            }
        }
        aliases.forEach(aliasedEntityDescriptions::remove);
        return result;
    }

    /**
     * Parse the initial entity
     *
     * @param position  Позиция
     * @param tokenKind Type of token
     */
    ExpressionData parseStartEntity(int position, TokenKind tokenKind) {
        Pointer<EntityDescription> entityDescriptionPointer = new Pointer<>();
        String entityAlias = null;
        // Here we will catch either it, or root, or elem, or alias
        if (tokenKind == TokenKind.IT) {
            if (primitiveExpressionsCollection || elementEntityDescription != null) {
                tokenKind = TokenKind.ELEM;
            } else {
                tokenKind = TokenKind.ROOT;
            }
        }
        if (tokenKind == TokenKind.ROOT) {
            entityDescriptionPointer.object = rootEntityDescription;
        } else if (tokenKind == TokenKind.ELEM) {
            if (!primitiveExpressionsCollection && elementEntityDescription == null) {
                throw new GetElementOutsideOfCollectionException(tokenParser.getContext(position));
            }
            entityDescriptionPointer.object = elementEntityDescription;
        } else {
            tokenParser.parseRequiredToken(TokenKind.IDENTIFIER);
            entityAlias = tokenParser.getTokenImage();
            if (!aliasedEntityDescriptions.containsKey(entityAlias)) {
                throw new UnknownAliasException(entityAlias, tokenParser.getContext(position));
            }
            entityDescriptionPointer.object = aliasedEntityDescriptions.get(entityAlias);
        }
        // Check if there is a specification in curly braces. If so, the specification should only contain type; otherwise, it is not applicable.
        List<Consumer<Object>> specificationSettingCodes = tokenParser.parseToken(TokenKind.BRACKET2_L) == null ? Collections.emptyList() : parseSpecification(ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS, Collections.singletonMap(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer)));
        if (tokenKind == TokenKind.ROOT) {
            return new ExpressionData(rootFunction.apply(getSpecificationCode(specificationSettingCodes)), entityDescriptionPointer.object);
        } else if (tokenKind == TokenKind.ELEM) {
            if (primitiveExpressionsCollection) {
                if (!specificationSettingCodes.isEmpty()) {
                    throw new UnexpectedPrimitiveExpressionsCollectionSpecificationException(tokenParser.getContext(position));
                }
                return new ExpressionData(ExpressionType.PRIMITIVE_EXPRESSION, elemPEFunction.get());
            } else {
                return new ExpressionData(elemEFunction.apply(getSpecificationCode(specificationSettingCodes)), entityDescriptionPointer.object);
            }
        } else {
            return new ExpressionData(aliasedEntityFunction.apply(entityAlias, getSpecificationCode(specificationSettingCodes)), entityDescriptionPointer.object);
        }
    }

    /**
     * Parse "entities"
     *
     * @param position Позиция
     * @param aliases  Aliases
     */
    ExpressionData parseEntities(int position, Set<String> aliases) {
        EntityDescription elementEntityDescriptionBackUp = elementEntityDescription;
        boolean primitiveExpressionsCollectionBackUp = primitiveExpressionsCollection;
        primitiveExpressionsCollection = false;
        Pointer<EntityDescription> entityDescriptionPointer = new Pointer<>();
        Supplier<FeatherException> exceptionInitializer = () -> new TypeSpecificationSettingShouldBeFirstException(tokenParser.getContext(position));
        Map<TokenKind, Object> triggers = new EnumMap<>(TokenKind.class);
        triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger2(entityDescriptionPointer));
        triggers.put(TokenKind.ELEM_ALIAS, getAliasSpecificationTrigger(position, entityDescriptionPointer, aliases));
        triggers.put(TokenKind.COND, (Runnable) () -> {
            if (entityDescriptionPointer.object == null) {
                throw exceptionInitializer.get();
            }
        });
        tokenParser.parseRequiredToken(TokenKind.BRACKET2_L);
        List<Consumer<Object>> specificationSettingCodes = parseSpecification(ENTITIES_COLLECTION_SPECIFICATION_SETTING_TOKEN_KINDS, triggers);
        if (entityDescriptionPointer.object == null) {
            throw exceptionInitializer.get();
        }
        elementEntityDescription = elementEntityDescriptionBackUp;
        primitiveExpressionsCollection = primitiveExpressionsCollectionBackUp;
        ExpressionData result = new ExpressionData(ExpressionType.ENTITIES_COLLECTION, entitiesFunction.apply(getSpecificationCode(specificationSettingCodes)));
        result.entityDescription = entityDescriptionPointer.object;
        return result;
    }

    /**
     * Parse "coalesce"
     */
    ExpressionData parseCoalesce() {
// First, we read the opening bracket and the first parameter
        tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
        PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
        List<PrimitiveExpression> primitiveExpressions = new ArrayList<>();
// Next, for each comma (if any), we read the subsequent parameters
        while (true) {
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.COMMA);
            if (tokenKind == null) {
                break;
            }
            primitiveExpressions.add(parsePrimitiveExpression());
        }
        //Reading the closing parenthesis
        tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
        return new ExpressionData(ExpressionType.PRIMITIVE_EXPRESSION, coalesceFunction.apply(primitiveExpression, primitiveExpressions.toArray(EMPTY_PRIMITIVE_EXPRESSIONS_ARRAY)));
    }

    /**
     * Parse the specification
     *
     * @param specificationSettingTokenKinds Types of specification setting tokens
     * @param triggers                       Triggers
     */
    List<Consumer<Object>> parseSpecification(Set<TokenKind> specificationSettingTokenKinds, Map<TokenKind, Object> triggers) {
        int position = tokenParser.position;
        List<Consumer<Object>> result;
        TokenKind[] tokenKinds = specificationSettingTokenKinds.toArray(EMPTY_TOKEN_KINDS_ARRAY);
        TokenKind tokenKind = tokenParser.parseToken(tokenKinds);
        if (tokenKind == null) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<>(2);
            Set<TokenKind> processedSpecificationSettingTokenKinds = EnumSet.noneOf(TokenKind.class);
            // We read the clarification for the object up to the closing curly bracket, for each clarification we call the corresponding trigger from triggers
            result.add(parseSpecificationSettingCode(triggers, position, tokenKind, processedSpecificationSettingTokenKinds));
            while (true) {
                tokenKind = tokenParser.parseToken(TokenKind.COMMA);
                if (tokenKind == null) {
                    break;
                }
                position = tokenParser.position;
                tokenKind = tokenParser.parseRequiredToken(tokenKinds);
                result.add(parseSpecificationSettingCode(triggers, position, tokenKind, processedSpecificationSettingTokenKinds));
            }
        }
        tokenParser.parseRequiredToken(TokenKind.BRACKET2_R);
        return result;
    }

    /**
     * Parse the parameter value
     */
    Object parseParameterValue() {
        TokenKind tokenKind = tokenParser.parseRequiredToken(TokenKind.BRACKET3_L, TokenKind.STRING, TokenKind.NUMBER, TokenKind.OFFSET_DATETIME, TokenKind.DATETIME, TokenKind.DATE, TokenKind.TIME, TokenKind.TRUE, TokenKind.FALSE, TokenKind.NULL);
        if (tokenKind == TokenKind.BRACKET3_L) {
            List<Object> list = new ArrayList<>();
            while (true) {
                TokenKind tokenKind2 = tokenParser.parseRequiredToken(TokenKind.STRING, TokenKind.NUMBER, TokenKind.OFFSET_DATETIME, TokenKind.DATETIME, TokenKind.DATE, TokenKind.TIME, TokenKind.TRUE, TokenKind.FALSE, TokenKind.NULL);
                if (tokenKind2 == TokenKind.NULL) {
                    list.add(null);
                } else {
                    list.add(getPrimitiveValue(tokenKind2));
                }
                tokenKind = tokenParser.parseToken(TokenKind.COMMA);
                if (tokenKind == null) {
                    break;
                }
            }
            tokenParser.parseRequiredToken(TokenKind.BRACKET3_R);
            return list;
        } else if (tokenKind == TokenKind.NULL) {
            return null;
        } else {
            return getPrimitiveValue(tokenKind);
        }
    }

    /**
     * Parse parameters
     */
    Map<String, Object> parseParameters() {
        Map<String, Object> result = new LinkedHashMap<>();
        tokenParser.parseRequiredToken(TokenKind.BRACKET2_L);
        while (true) {
            tokenParser.parseRequiredToken(TokenKind.IDENTIFIER);
            String name = tokenParser.getTokenImage();
            tokenParser.parseRequiredToken(TokenKind.ASSIGN);
            result.put(name, parseParameterValue());
            TokenKind tokenKind = tokenParser.parseToken(TokenKind.COMMA);
            if (tokenKind == null) {
                break;
            }
        }
        tokenParser.parseRequiredToken(TokenKind.BRACKET2_R);
        return result;
    }

    /**
     * Parse the specification configuration code
     *
     * @param triggers                                Triggers
     * @param position                                Position
     * @param tokenKind                               Type of token
     * @param processedSpecificationSettingTokenKinds The types of specification setting tokens that have been processed
     */
    Consumer<Object> parseSpecificationSettingCode(Map<TokenKind, Object> triggers, int position, TokenKind tokenKind, Set<TokenKind> processedSpecificationSettingTokenKinds) {
        Consumer<Object> result;
        //What is actually in the object specification ?
            tokenParser.parseRequiredToken(TokenKind.ASSIGN);
        if (tokenKind == TokenKind.TYPE) {
            tokenParser.parseRequiredToken(TokenKind.IDENTIFIER);
            String entityType = tokenParser.getTokenImage();
            ((Consumer<String>) triggers.get(TokenKind.TYPE)).accept(entityType);
            result = (Consumer<Object>) (Object) getTypeSpecificationCode(entityType);
        } else if (tokenKind == TokenKind.PARAMS) {
            result = (Consumer<Object>) (Object) getParametersSpecificationCode(parseParameters());
        } else if (tokenKind == TokenKind.ALIAS) {
            tokenParser.parseRequiredToken(TokenKind.IDENTIFIER);
            String alias = tokenParser.getTokenImage();
            ((Consumer<String>) triggers.get(TokenKind.ALIAS)).accept(alias);
            result = (Consumer<Object>) (Object) getAliasSpecificationCode(alias);
        } else if (tokenKind == TokenKind.ELEM_ALIAS) {
            tokenParser.parseRequiredToken(TokenKind.IDENTIFIER);
            String elementAlias = tokenParser.getTokenImage();
            ((Consumer<String>) triggers.get(TokenKind.ELEM_ALIAS)).accept(elementAlias);
            result = (Consumer<Object>) (Object) getElementAliasSpecificationCode(elementAlias);
        } else {
            Runnable trigger = ((Runnable) triggers.get(TokenKind.COND));
            if (trigger != null) {
                trigger.run();
            }
            result = (Consumer<Object>) (Object) getConditionSpecificationCode(parseCondition());
        }
        // Check for duplicates and their correct order
        if (processedSpecificationSettingTokenKinds.contains(tokenKind)) {
            throw new DuplicateSpecificationSettingException(tokenKind, tokenParser.getContext(position));
        }
        if (tokenKind == TokenKind.TYPE && !processedSpecificationSettingTokenKinds.isEmpty()) {
            throw new TypeSpecificationSettingShouldBeFirstException(tokenParser.getContext(position));
        }
        if (processedSpecificationSettingTokenKinds.contains(TokenKind.COND)) {
            throw new CondSpecificationSettingShouldBeLastException(tokenParser.getContext(position));
        }
        // Remember the read property
        processedSpecificationSettingTokenKinds.add(tokenKind);
        return result;
    }

    /**
     * Parse the property
     *
     * @param result   Result
     * @param position Позиция
     * @param aliases  Aliases
     */
    void parseProperty(ExpressionData result, int position, Set<String> aliases) {
        tokenParser.parseRequiredToken(TokenKind.IDENTIFIER);
        String propertyName = tokenParser.getTokenImage();
        if (propertyName.equals(TokenKind.ID.image)) {
        // By Victor Biryukov's decision
            if (result.type == ExpressionType.ENTITY) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((Entity) result.data).id();
            } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = ((EntitiesCollection) result.data).id();
            } else {
                throw new MethodNotFoundException(result + ".id", tokenParser.getContext(position));
            }
            return;
        }
        EntityDescription elementEntityDescriptionBackUp = elementEntityDescription;
        boolean primitiveExpressionsCollectionBackUp = primitiveExpressionsCollection;
        Set<TokenKind> specificationSettingTokenKinds = EnumSet.noneOf(TokenKind.class);
        Map<TokenKind, Object> triggers = new EnumMap<>(TokenKind.class);
        Pointer<EntityDescription> entityDescriptionPointer = new Pointer<>();
        if (result.type == ExpressionType.PRIMITIVE_EXPRESSION || result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
            throw new DisallowedPropertyAccessException(result.type, propertyName, tokenParser.getContext(position));
        } else if (result.type == ExpressionType.ENTITY) {
            if (result.entityDescription.getPrimitivesCollectionDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = PRIMITIVES_COLLECTION_SPECIFICATION_SETTING_TOKEN_KINDS;
                elementEntityDescription = null;
                primitiveExpressionsCollection = true;
            } else if (result.entityDescription.getReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS2;
                entityDescriptionPointer.object = result.entityDescription.getReferenceDescription(propertyName).getEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer));
                triggers.put(TokenKind.ALIAS, getAliasSpecificationTrigger(position, entityDescriptionPointer, aliases));
            } else if (result.entityDescription.getReferenceBackReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS2;
                entityDescriptionPointer.object = result.entityDescription.getReferenceBackReferenceDescription(propertyName).getOwnerEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer));
                triggers.put(TokenKind.ALIAS, getAliasSpecificationTrigger(position, entityDescriptionPointer, aliases));
            } else if (result.entityDescription.getReferencesCollectionDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITIES_COLLECTION_SPECIFICATION_SETTING_TOKEN_KINDS;
                elementEntityDescription = result.entityDescription.getReferencesCollectionDescription(propertyName).getEntityDescription();
                primitiveExpressionsCollection = false;
                entityDescriptionPointer.object = result.entityDescription.getReferencesCollectionDescription(propertyName).getEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger2(entityDescriptionPointer));
                triggers.put(TokenKind.ELEM_ALIAS, getAliasSpecificationTrigger(position, entityDescriptionPointer, aliases));
            } else if (result.entityDescription.getReferencesCollectionBackReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITIES_COLLECTION_SPECIFICATION_SETTING_TOKEN_KINDS;
                elementEntityDescription = result.entityDescription.getReferencesCollectionBackReferenceDescription(propertyName).getOwnerEntityDescription();
                primitiveExpressionsCollection = false;
                entityDescriptionPointer.object = result.entityDescription.getReferencesCollectionBackReferenceDescription(propertyName).getOwnerEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger2(entityDescriptionPointer));
                triggers.put(TokenKind.ELEM_ALIAS, getAliasSpecificationTrigger(position, entityDescriptionPointer, aliases));
            } else if (!(result.entityDescription.getPrimitiveDescriptions().containsKey(propertyName)
                || result.entityDescription.getGroupDescriptions().containsKey(propertyName))) {
                throw new PropertyNotFoundException(propertyName, tokenParser.getContext(position));
            }
        } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
            if (result.entityDescription.getReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS;
                entityDescriptionPointer.object = result.entityDescription.getReferenceDescription(propertyName).getEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer));
            } else if (result.entityDescription.getReferenceBackReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS;
                entityDescriptionPointer.object = result.entityDescription.getReferenceBackReferenceDescription(propertyName).getOwnerEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer));
            } else if (result.entityDescription.getPrimitivesCollectionDescriptions().containsKey(propertyName)
                || result.entityDescription.getReferencesCollectionDescriptions().containsKey(propertyName)
                || result.entityDescription.getReferencesCollectionBackReferenceDescriptions().containsKey(propertyName)) {
                throw new DisallowedPropertyAccessException(result.type, propertyName, tokenParser.getContext(position));
            } else if (!(result.entityDescription.getPrimitiveDescriptions().containsKey(propertyName)
                || result.entityDescription.getGroupDescriptions().containsKey(propertyName))) {
                throw new PropertyNotFoundException(propertyName, tokenParser.getContext(position));
            }
        } else if (result.type == ExpressionType.GROUP) {
            if (result.groupDescription.getReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS2;
                entityDescriptionPointer.object = result.groupDescription.getReferenceDescription(propertyName).getEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer));
                triggers.put(TokenKind.ALIAS, getAliasSpecificationTrigger(position, entityDescriptionPointer, aliases));
            } else if (!result.groupDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
                throw new PropertyNotFoundException(propertyName, tokenParser.getContext(position));
            }
        } else {
            if (result.groupDescription.getReferenceDescriptions().containsKey(propertyName)) {
                specificationSettingTokenKinds = ENTITY_SPECIFICATION_SETTING_TOKEN_KINDS2;
                entityDescriptionPointer.object = result.groupDescription.getReferenceDescription(propertyName).getEntityDescription();
                triggers.put(TokenKind.TYPE, getTypeSpecificationTrigger(entityDescriptionPointer));
            } else if (!result.groupDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
                throw new PropertyNotFoundException(propertyName, tokenParser.getContext(position));
            }
        }
        List<Consumer<Object>> specificationSettingCodes = tokenParser.parseToken(TokenKind.BRACKET2_L) == null ? Collections.emptyList() : parseSpecification(specificationSettingTokenKinds, triggers);
        if (result.type == ExpressionType.ENTITY) {
            Entity entity = (Entity) result.data;
            if (result.entityDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = entity.prim(propertyName);
            } else if (result.entityDescription.getPrimitivesCollectionDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = entity.prims(propertyName, getSpecificationCode(specificationSettingCodes));
            } else if (result.entityDescription.getReferenceDescriptions().containsKey(propertyName)) {
                result.data = entity.ref(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            } else if (result.entityDescription.getReferenceBackReferenceDescriptions().containsKey(propertyName)) {
                result.data = entity.refB(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            } else if (result.entityDescription.getReferencesCollectionDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.ENTITIES_COLLECTION;
                result.data = entity.refs(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            } else if (result.entityDescription.getReferencesCollectionBackReferenceDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.ENTITIES_COLLECTION;
                result.data = entity.refsB(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            } else {
                result.type = ExpressionType.GROUP;
                result.data = entity.group(propertyName);
                result.groupDescription = result.entityDescription.getGroupDescription(propertyName);
            }
        } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
            EntitiesCollection entitiesCollection = (EntitiesCollection) result.data;
            if (result.entityDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = entitiesCollection.prim(propertyName);
            } else if (result.entityDescription.getReferenceDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.ENTITIES_COLLECTION;
                result.data = entitiesCollection.ref(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            } else if (result.entityDescription.getReferenceBackReferenceDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.ENTITIES_COLLECTION;
                result.data = entitiesCollection.refB(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            } else {
                result.type = ExpressionType.GROUPS_COLLECTION;
                result.data = entitiesCollection.group(propertyName);
                result.groupDescription = result.entityDescription.getGroupDescription(propertyName);
            }
        } else if (result.type == ExpressionType.GROUP) {
            Group group = (Group) result.data;
            if (result.groupDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = group.prim(propertyName);
            } else {
                result.type = ExpressionType.ENTITY;
                result.data = group.ref(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            }
        } else {
            GroupsCollection groupsCollection = (GroupsCollection) result.data;
            if (result.groupDescription.getPrimitiveDescriptions().containsKey(propertyName)) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = groupsCollection.prim(propertyName);
            } else {
                result.type = ExpressionType.ENTITIES_COLLECTION;
                result.data = groupsCollection.ref(propertyName, getSpecificationCode(specificationSettingCodes));
                result.entityDescription = entityDescriptionPointer.object;
            }
        }
        elementEntityDescription = elementEntityDescriptionBackUp;
        primitiveExpressionsCollection = primitiveExpressionsCollectionBackUp;
    }

    /**
     * Parse system property
     *
     * @param result   Result
     * @param position Position
     */
    void parseSystemProperty(ExpressionData result, int position) {
        TokenKind tokenKind = tokenParser.parseRequiredToken(TokenKind.UPPER, TokenKind.LOWER, TokenKind.LENGTH, TokenKind.TRIM, TokenKind.LTRIM, TokenKind.RTRIM, TokenKind.ROUND, TokenKind.CEIL, TokenKind.FLOOR, TokenKind.HASH, TokenKind.AS_STRING, TokenKind.AS_BIG_DECIMAL, TokenKind.AS_OFFSET_DATE_TIME, TokenKind.AS_DATE_TIME, TokenKind.AS_DATE, TokenKind.AS_TIME, TokenKind.YEAR, TokenKind.MONTH, TokenKind.DAY, TokenKind.HOUR, TokenKind.MINUTE, TokenKind.SECOND, TokenKind.OFFSET_HOUR, TokenKind.OFFSET_MINUTE, TokenKind.DATETIME2, TokenKind.DATE2, TokenKind.TIME2, TokenKind.OFFSET, TokenKind.ABS, TokenKind.SUBSTR, TokenKind.REPLACE, TokenKind.ADD_MILLISECONDS, TokenKind.ADD_SECONDS, TokenKind.ADD_MINUTES, TokenKind.ADD_HOURS, TokenKind.ADD_DAYS, TokenKind.ADD_MONTHS, TokenKind.ADD_YEARS, TokenKind.SUB_MILLISECONDS, TokenKind.SUB_SECONDS, TokenKind.SUB_MINUTES, TokenKind.SUB_HOURS, TokenKind.SUB_DAYS, TokenKind.SUB_MONTHS, TokenKind.SUB_YEARS, TokenKind.TYPE, TokenKind.ID, TokenKind.MIN, TokenKind.MAX, TokenKind.SUM, TokenKind.AVG, TokenKind.COUNT, TokenKind.EXISTS, TokenKind.AS_BOOLEAN, TokenKind.MAP, TokenKind.LPAD, TokenKind.RPAD, TokenKind.POWER, TokenKind.LOG);
        if (tokenKind == TokenKind.UPPER) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).upper();
            } else {
                throw new MethodNotFoundException(result + ".$upper", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.LOWER) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).lower();
            } else {
                throw new MethodNotFoundException(result + ".$lower", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.LENGTH) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).length();
            } else {
                throw new MethodNotFoundException(result + ".$length", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.TRIM) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).trim();
            } else {
                throw new MethodNotFoundException(result + ".$trim", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.LTRIM) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).ltrim();
            } else {
                throw new MethodNotFoundException(result + ".$ltrim", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.RTRIM) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).rtrim();
            } else {
                throw new MethodNotFoundException(result + ".$rtrim", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ROUND) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).round();
            } else {
                throw new MethodNotFoundException(result + ".$round", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.CEIL) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).ceil();
            } else {
                throw new MethodNotFoundException(result + ".$ceil", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.FLOOR) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).floor();
            } else {
                throw new MethodNotFoundException(result + ".$floor", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.HASH) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).hash();
            } else {
                throw new MethodNotFoundException(result + ".$hash", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_STRING) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).asString();
            } else {
                throw new MethodNotFoundException(result + ".$asString", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_BIG_DECIMAL) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).asBigDecimal();
            } else {
                throw new MethodNotFoundException(result + ".$asBigDecimal", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_DATE) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).asDate();
            } else {
                throw new MethodNotFoundException(result + ".$asDate", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_DATE_TIME) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).asDateTime();
            } else {
                throw new MethodNotFoundException(result + ".$asDateTime", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_OFFSET_DATE_TIME) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).asOffsetDateTime();
            } else {
                throw new MethodNotFoundException(result + ".$asOffsetDateTime", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_TIME) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).asTime();
            } else {
                throw new MethodNotFoundException(result + ".$asTime", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.YEAR) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).year();
            } else {
                throw new MethodNotFoundException(result + ".$year", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.MONTH) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).month();
            } else {
                throw new MethodNotFoundException(result + ".$month", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.DAY) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).day();
            } else {
                throw new MethodNotFoundException(result + ".$day", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.HOUR) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).hour();
            } else {
                throw new MethodNotFoundException(result + ".$hour", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.MINUTE) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).minute();
            } else {
                throw new MethodNotFoundException(result + ".$minute", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SECOND) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).second();
            } else {
                throw new MethodNotFoundException(result + ".$second", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.OFFSET_HOUR) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).offsetHour();
            } else {
                throw new MethodNotFoundException(result + ".$offsetHour", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.OFFSET_MINUTE) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).offsetMinute();
            } else {
                throw new MethodNotFoundException(result + ".$offsetMinute", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.DATETIME2) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).dateTime();
            } else {
                throw new MethodNotFoundException(result + ".$dateTime", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.DATE2) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).date();
            } else {
                throw new MethodNotFoundException(result + ".$date", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.TIME2) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).time();
            } else {
                throw new MethodNotFoundException(result + ".$time", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.OFFSET) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).offset();
            } else {
                throw new MethodNotFoundException(result + ".$offset", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ABS) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).abs();
            } else {
                throw new MethodNotFoundException(result + ".$abs", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUBSTR) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            PrimitiveExpression primitiveExpression2 = tokenParser.parseToken(TokenKind.COMMA) == null ? null : parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (primitiveExpression2 == null) {
                if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                    result.data = ((PrimitiveExpression) result.data).substr(primitiveExpression);
                } else {
                    throw new MethodNotFoundException(result + ".$substr(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
                }
            } else {
                if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                    result.data = ((PrimitiveExpression) result.data).substr(primitiveExpression, primitiveExpression2);
                } else {
                    throw new MethodNotFoundException(result + ".$substr(" + ExpressionType.PRIMITIVE_EXPRESSION + ", " + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
                }
            }
        } else if (tokenKind == TokenKind.REPLACE) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.COMMA);
            PrimitiveExpression primitiveExpression2 = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).replace(primitiveExpression, primitiveExpression2);
            } else {
                throw new MethodNotFoundException(result + ".$replace(" + ExpressionType.PRIMITIVE_EXPRESSION + ", " + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_MILLISECONDS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addMilliseconds(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addMilliseconds(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_SECONDS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addSeconds(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addSeconds(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_MINUTES) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addMinutes(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addMinutes(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_HOURS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addHours(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addHours(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_DAYS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addDays(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addDays(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_MONTHS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addMonths(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addMonths(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ADD_YEARS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).addYears(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$addYears(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_MILLISECONDS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subMilliseconds(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subMilliseconds(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_SECONDS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subSeconds(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subSeconds(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_MINUTES) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subMinutes(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subMinutes(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_HOURS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subHours(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subHours(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_DAYS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subDays(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subDays(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_MONTHS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subMonths(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subMonths(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUB_YEARS) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).subYears(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$subYears(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.TYPE) {
            if (result.type == ExpressionType.ENTITY) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((Entity) result.data).type();
            } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = ((EntitiesCollection) result.data).type();
            } else {
                throw new MethodNotFoundException(result + ".$type", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.ID) {
            if (result.type == ExpressionType.ENTITY) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((Entity) result.data).id();
            } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = ((EntitiesCollection) result.data).id();
            } else {
                throw new MethodNotFoundException(result + ".$id", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.MIN) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).min();
            } else if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((PrimitiveExpressionsCollection) result.data).min();
            } else {
                throw new MethodNotFoundException(result + ".$min", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.MAX) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).max();
            } else if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((PrimitiveExpressionsCollection) result.data).max();
            } else {
                throw new MethodNotFoundException(result + ".$max", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.SUM) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).sum();
            } else if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((PrimitiveExpressionsCollection) result.data).sum();
            } else {
                throw new MethodNotFoundException(result + ".$sum", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AVG) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).avg();
            } else if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((PrimitiveExpressionsCollection) result.data).avg();
            } else {
                throw new MethodNotFoundException(result + ".$avg", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.COUNT) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).count();
            } else if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((PrimitiveExpressionsCollection) result.data).count();
            } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((EntitiesCollection) result.data).count();
            } else {
                throw new MethodNotFoundException(result + ".$count", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.EXISTS) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.type = ExpressionType.CONDITION;
                result.data = ((PrimitiveExpressionsCollection) result.data).exists();
            } else if (result.type == ExpressionType.ENTITY) {
                result.type = ExpressionType.CONDITION;
                result.data = ((Entity) result.data).exists();
            } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
                result.type = ExpressionType.CONDITION;
                result.data = ((EntitiesCollection) result.data).exists();
            } else {
                throw new MethodNotFoundException(result + ".$exists", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.AS_BOOLEAN) {
            if (result.type == ExpressionType.CONDITION) {
                result.type = ExpressionType.PRIMITIVE_EXPRESSION;
                result.data = ((Condition) result.data).asBoolean();
            } else {
                throw new MethodNotFoundException(result + ".$asBoolean", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.MAP) {
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                EntityDescription elementEntityDescriptionBackUp = elementEntityDescription;
                boolean primitiveExpressionsCollectionBackUp = primitiveExpressionsCollection;
                elementEntityDescription = null;
                primitiveExpressionsCollection = true;
                tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
                PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
                tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
                elementEntityDescription = elementEntityDescriptionBackUp;
                primitiveExpressionsCollection = primitiveExpressionsCollectionBackUp;
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = ((PrimitiveExpressionsCollection) result.data).map(primitiveExpression);
            } else if (result.type == ExpressionType.ENTITIES_COLLECTION) {
                EntityDescription elementEntityDescriptionBackUp = elementEntityDescription;
                boolean primitiveExpressionsCollectionBackUp = primitiveExpressionsCollection;
                elementEntityDescription = result.entityDescription;
                primitiveExpressionsCollection = false;
                tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
                PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
                tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
                elementEntityDescription = elementEntityDescriptionBackUp;
                primitiveExpressionsCollection = primitiveExpressionsCollectionBackUp;
                result.type = ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION;
                result.data = ((EntitiesCollection) result.data).map(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$map(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.LPAD) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.COMMA);
            PrimitiveExpression primitiveExpression2 = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).lpad(primitiveExpression, primitiveExpression2);
            } else {
                throw new MethodNotFoundException(result + ".$lpad(" + ExpressionType.PRIMITIVE_EXPRESSION + ", " + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.RPAD) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.COMMA);
            PrimitiveExpression primitiveExpression2 = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).rpad(primitiveExpression, primitiveExpression2);
            } else {
                throw new MethodNotFoundException(result + ".$rpad(" + ExpressionType.PRIMITIVE_EXPRESSION + ", " + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else if (tokenKind == TokenKind.POWER) {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).power(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$power(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        } else {
            tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
            PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
                result.data = ((PrimitiveExpression) result.data).log(primitiveExpression);
            } else {
                throw new MethodNotFoundException(result + ".$log(" + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
            }
        }
    }

    /**
     * Get primitive value
     *
     * @param tokenKind Type of token
     */
    Object getPrimitiveValue(TokenKind tokenKind) {
        String image = tokenParser.getTokenImage();
        if (tokenKind == TokenKind.STRING) {
            return image.substring(1, image.length() - 1).replace("''", "'");
        } else if (tokenKind == TokenKind.NUMBER) {
            return getNumber();
        } else if (tokenKind == TokenKind.DATE) {
            return LocalDate.parse(image.substring(1), DateTimeFormatter.ISO_LOCAL_DATE);
        } else if (tokenKind == TokenKind.DATETIME) {
            return LocalDateTime.parse(image.substring(1), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } else if (tokenKind == TokenKind.OFFSET_DATETIME) {
            return OffsetDateTime.parse(image.substring(1), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else if (tokenKind == TokenKind.TIME) {
            return LocalTime.parse(image.substring(1), DateTimeFormatter.ISO_LOCAL_TIME);
        } else {
            return tokenKind == TokenKind.TRUE;
        }
    }

    /**
     * Parse a primitive value
     *
     * @param tokenKind Type of token
     */
    ExpressionData parsePrimitiveValue(TokenKind tokenKind) {
        return new ExpressionData(ExpressionType.PRIMITIVE_EXPRESSION, primFunction.apply(getPrimitiveValue(tokenKind)));
    }

    /**
     * Parse the current date and time (with offset)
     */
    ExpressionData parseNow() {
        return new ExpressionData(ExpressionType.PRIMITIVE_EXPRESSION, now.get());
    }

    /**
     * Conditional group parsing
     *
     * @param tokenKind Type of token
     */
    ExpressionData parseConditionalGroup(TokenKind tokenKind) {
        tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
        if (tokenParser.parseToken(TokenKind.BRACKET3_L) == null) {
            PrimitiveExpressionsCollection primitiveExpressionsCollection = parsePrimitiveExpressionsCollection();
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            return new ExpressionData(ExpressionType.CONDITIONAL_GROUP, tokenKind == TokenKind.ANY ? anyFunction.apply(primitiveExpressionsCollection) : allFunction.apply(primitiveExpressionsCollection));
        } else {
            List<Object> values = new ArrayList<>();
            values.add(getPrimitiveValue(tokenParser.parseRequiredToken(TokenKind.STRING, TokenKind.NUMBER, TokenKind.OFFSET_DATETIME, TokenKind.DATETIME, TokenKind.DATE, TokenKind.TIME, TokenKind.TRUE, TokenKind.FALSE)));
            while (tokenParser.parseToken(TokenKind.BRACKET3_R) == null) {
                tokenParser.parseRequiredToken(TokenKind.COMMA);
                values.add(getPrimitiveValue(tokenParser.parseRequiredToken(TokenKind.STRING, TokenKind.NUMBER, TokenKind.OFFSET_DATETIME, TokenKind.DATETIME, TokenKind.DATE, TokenKind.TIME, TokenKind.TRUE, TokenKind.FALSE)));
            }
            tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
            return new ExpressionData(ExpressionType.CONDITIONAL_GROUP, tokenKind == TokenKind.ANY ? anyFunction2.apply(values.toArray()) : allFunction2.apply(values.toArray()));
        }
    }

    /**
     * Parse "between"
     *
     * @param result   Result
     * @param position Position
     */
    void parseBetween(ExpressionData result, int position) {
        tokenParser.parseRequiredToken(TokenKind.BRACKET_L);
        PrimitiveExpression primitiveExpression = parsePrimitiveExpression();
        tokenParser.parseRequiredToken(TokenKind.COMMA);
        PrimitiveExpression primitiveExpression2 = parsePrimitiveExpression();
        tokenParser.parseRequiredToken(TokenKind.BRACKET_R);
        if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
            result.data = ((PrimitiveExpression) result.data).between(primitiveExpression, primitiveExpression2);
        } else {
            throw new MethodNotFoundException(result + " $between (" + ExpressionType.PRIMITIVE_EXPRESSION + ", " + ExpressionType.PRIMITIVE_EXPRESSION + ")", tokenParser.getContext(position));
        }
    }

    /**
     * Parse "in"
     *
     * @param result   Result
     * @param position Position
     */
    void parseIn(ExpressionData result, int position) {
        TokenKind tokenKind = tokenParser.parseToken(TokenKind.BRACKET3_L);
        if (tokenKind == null) {
            ExpressionData expressionData = parseShift();
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
                result.data = ((PrimitiveExpression) result.data).in((PrimitiveExpressionsCollection) expressionData.data);
            } else if (result.type == ExpressionType.ENTITY && expressionData.type == ExpressionType.ENTITIES_COLLECTION) {
                result.data = ((Entity) result.data).in((EntitiesCollection) expressionData.data);
            } else {
                throw new MethodNotFoundException(result + " $in " + expressionData, tokenParser.getContext(position));
            }
        } else {
            ExpressionData expressionData = parseLogicalOr();
            List<ExpressionData> expressionsData = new ArrayList<>();
            while (true) {
                tokenKind = tokenParser.parseToken(TokenKind.COMMA);
                if (tokenKind == null) {
                    break;
                }
                expressionsData.add(parseLogicalOr());
            }
            tokenParser.parseRequiredToken(TokenKind.BRACKET3_R);
            if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION && areAllOfType(expressionsData, ExpressionType.PRIMITIVE_EXPRESSION)) {
                result.data = ((PrimitiveExpression) result.data).in((PrimitiveExpression) expressionData.data, getArray(expressionsData, PrimitiveExpression[]::new));
            } else if (result.type == ExpressionType.ENTITY && expressionData.type == ExpressionType.ENTITY && areAllOfType(expressionsData, ExpressionType.ENTITY)) {
                result.data = ((Entity) result.data).in((Entity) expressionData.data, getArray(expressionsData, Entity[]::new));
            } else {
                expressionsData.add(0, expressionData);
                throw new MethodNotFoundException(expressionData + " $in " + expressionsData, tokenParser.getContext(position));
            }
        }
    }

    /**
     * Parse comparing with null
     *
     * @param result    Result
     * @param position  Position
     * @param tokenKind Type of token
     */
    void parseNullComparison(ExpressionData result, int position, TokenKind tokenKind) {
        Supplier<FeatherException> exceptionInitializer = () -> new MethodNotFoundException(result + " " + tokenKind.image + " null", tokenParser.getContext(position));
        if (result.type == ExpressionType.PRIMITIVE_EXPRESSION) {
            if (tokenKind == TokenKind.EQ) {
                result.data = ((PrimitiveExpression) result.data).isNull();
            } else if (tokenKind == TokenKind.NOT_EQ) {
                result.data = ((PrimitiveExpression) result.data).isNotNull();
            } else {
                throw exceptionInitializer.get();
            }
        } else if (result.type == ExpressionType.ENTITY) {
            if (tokenKind == TokenKind.EQ) {
                result.data = ((Entity) result.data).isNull();
            } else if (tokenKind == TokenKind.NOT_EQ) {
                result.data = ((Entity) result.data).isNotNull();
            } else {
                throw exceptionInitializer.get();
            }
        } else if (result.type == ExpressionType.GROUP) {
            if (tokenKind == TokenKind.EQ) {
                result.data = ((Group) result.data).isNull();
            } else if (tokenKind == TokenKind.NOT_EQ) {
                result.data = ((Group) result.data).isNotNull();
            } else {
                throw exceptionInitializer.get();
            }
        } else {
            throw exceptionInitializer.get();
        }
    }

    /**
     * Parse comparing
     *
     * @param result    Result
     * @param position  Position
     * @param tokenKind Type of token
     */
    void parseComparison(ExpressionData result, int position, TokenKind tokenKind) {
        ExpressionData expressionData = parseShift();
        Supplier<FeatherException> exceptionInitializer = () -> new MethodNotFoundException(result + " " + tokenKind.image + " " + expressionData, tokenParser.getContext(position));
        if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
            if (tokenKind == TokenKind.EQ) {
                result.data = ((PrimitiveExpression) result.data).eq((PrimitiveExpression) expressionData.data);
            } else if (tokenKind == TokenKind.NOT_EQ) {
                result.data = ((PrimitiveExpression) result.data).notEq((PrimitiveExpression) expressionData.data);
            } else if (tokenKind == TokenKind.GT) {
                result.data = ((PrimitiveExpression) result.data).gt((PrimitiveExpression) expressionData.data);
            } else if (tokenKind == TokenKind.LT_OR_EQ) {
                result.data = ((PrimitiveExpression) result.data).ltOrEq((PrimitiveExpression) expressionData.data);
            } else if (tokenKind == TokenKind.LT) {
                result.data = ((PrimitiveExpression) result.data).lt((PrimitiveExpression) expressionData.data);
            } else if (tokenKind == TokenKind.GT_OR_EQ) {
                result.data = ((PrimitiveExpression) result.data).gtOrEq((PrimitiveExpression) expressionData.data);
            } else {
                result.data = ((PrimitiveExpression) result.data).like((PrimitiveExpression) expressionData.data);
            }
        } else if (result.type == ExpressionType.PRIMITIVE_EXPRESSION && expressionData.type == ExpressionType.CONDITIONAL_GROUP) {
            if (tokenKind == TokenKind.EQ) {
                result.data = ((PrimitiveExpression) result.data).eq((ConditionalGroup) expressionData.data);
            } else if (tokenKind == TokenKind.NOT_EQ) {
                result.data = ((PrimitiveExpression) result.data).notEq((ConditionalGroup) expressionData.data);
            } else if (tokenKind == TokenKind.GT) {
                result.data = ((PrimitiveExpression) result.data).gt((ConditionalGroup) expressionData.data);
            } else if (tokenKind == TokenKind.LT_OR_EQ) {
                result.data = ((PrimitiveExpression) result.data).ltOrEq((ConditionalGroup) expressionData.data);
            } else if (tokenKind == TokenKind.LT) {
                result.data = ((PrimitiveExpression) result.data).lt((ConditionalGroup) expressionData.data);
            } else if (tokenKind == TokenKind.GT_OR_EQ) {
                result.data = ((PrimitiveExpression) result.data).gtOrEq((ConditionalGroup) expressionData.data);
            } else {
                throw exceptionInitializer.get();
            }
        } else if (result.type == ExpressionType.ENTITY && expressionData.type == ExpressionType.ENTITY) {
            if (tokenKind == TokenKind.EQ) {
                result.data = ((Entity) result.data).eq((Entity) expressionData.data);
            } else if (tokenKind == TokenKind.NOT_EQ) {
                result.data = ((Entity) result.data).notEq((Entity) expressionData.data);
            } else {
                throw exceptionInitializer.get();
            }
        } else {
            throw exceptionInitializer.get();
        }
    }

    /**
     * Parse the root primitive expression
     */
    PrimitiveExpression parseRootPrimitiveExpression() {
        PrimitiveExpression result = parsePrimitiveExpression();
        tokenParser.checkEnd();
        return result;
    }

    /**
     * Parse a primitive expression
     */
    PrimitiveExpression parsePrimitiveExpression() {
        int position = tokenParser.position;
        ExpressionData expressionData = parseLogicalOr();
        if (expressionData.type == ExpressionType.PRIMITIVE_EXPRESSION) {
            return (PrimitiveExpression) expressionData.data;
        }
        throw new NotPrimitiveExpressionException(tokenParser.getContext(position));
    }

    /**
     * Parse a collection of primitive expressions
     */
    PrimitiveExpressionsCollection parsePrimitiveExpressionsCollection() {
        int position = tokenParser.position;
        ExpressionData expressionData = parseLogicalOr();
        if (expressionData.type == ExpressionType.PRIMITIVE_EXPRESSIONS_COLLECTION) {
            return (PrimitiveExpressionsCollection) expressionData.data;
        }
        throw new NotPrimitiveExpressionsCollectionException(tokenParser.getContext(position));
    }
}
