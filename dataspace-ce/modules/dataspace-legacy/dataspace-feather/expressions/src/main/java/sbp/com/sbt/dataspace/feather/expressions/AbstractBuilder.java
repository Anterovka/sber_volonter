package sbp.com.sbt.dataspace.feather.expressions;

import java.util.function.Consumer;

/**
 * Abstract builder
 *
 * @param <E> Type of expression
 */
abstract class AbstractBuilder<E> {

    static final ThreadLocal<ExpressionsProcessor> EXPRESSIONS_PROCESSOR = new ThreadLocal<>();

    /**
     * Get root entity
     *
     * @param specificationCode The specification code
     */
    // NotNull
    protected final Entity root(Consumer<RootSpecification> specificationCode) {
        return EXPRESSIONS_PROCESSOR.get().root(specificationCode);
    }

    /**
     * Get root entity
     */
    // NotNull
    protected final Entity root() {
        return EXPRESSIONS_PROCESSOR.get().root();
    }

    /**
     * Get collection element (primitive expression)
     */
    // NotNull
    protected final PrimitiveExpression elemPE() {
        return EXPRESSIONS_PROCESSOR.get().elemPE();
    }

    /**
     * Get collection element (entity)
     *
     * @param specificationCode The specification code
     */
    // NotNull
    protected final Entity elemE(Consumer<EntityElementSpecification> specificationCode) {
        return EXPRESSIONS_PROCESSOR.get().elemE(specificationCode);
    }

    /**
     * Get collection element (entity)
     */
    // NotNull
    protected final Entity elemE() {
        return EXPRESSIONS_PROCESSOR.get().elemE();
    }

    /**
     * Get entity with alias
     *
     * @param alias             Alias
     * @param specificationCode The specification code
     */
    // NotNull
    protected final Entity aliasedEntity(String alias, Consumer<AliasedEntitySpecification> specificationCode) {
        return EXPRESSIONS_PROCESSOR.get().aliasedEntity(alias, specificationCode);
    }

    /**
     * Get entity with alias
     *
     * @param alias Alias
     */
    // NotNull
    protected final Entity aliasedEntity(String alias) {
        return EXPRESSIONS_PROCESSOR.get().aliasedEntity(alias);
    }

    /**
     * Get primitive value
     *
     * @param primitiveValue Primitive value
     */
    // NotNull
    protected final PrimitiveExpression prim(Object primitiveValue) {
        return EXPRESSIONS_PROCESSOR.get().prim(primitiveValue);
    }

    /**
     * Get the first non-zero element
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    protected final PrimitiveExpression coalesce(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions) {
        return EXPRESSIONS_PROCESSOR.get().coalesce(primitiveExpression1, primitiveExpressions);
    }

    /**
     * Get the current date and time (with offset)
     */
    // NotNull
    protected final PrimitiveExpression now() {
        return EXPRESSIONS_PROCESSOR.get().now();
    }

    /**
     * Get entities
     *
     * @param specificationCode The specification code
     */
    // NotNull
    protected final EntitiesCollection entities(Consumer<EntitiesSpecification> specificationCode) {
        return EXPRESSIONS_PROCESSOR.get().entities(specificationCode);
    }

    /**
     * Get RAW object (primitive expression)
     *
     * @param raw RAW-объект
     */
    // NotNull
    protected final PrimitiveExpression rawPE(Object raw) {
        return EXPRESSIONS_PROCESSOR.get().rawPE(raw);
    }

    /**
     * Get conditional group "any"
     *
     * @param collection of primitive expressions
     */
    // NotNull
    protected final ConditionalGroup any(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return EXPRESSIONS_PROCESSOR.get().any(primitiveExpressionsCollection);
    }

    /**
     * Get conditional group "any"
     *
     * @param array Array
     */
    // NotNull
    protected final ConditionalGroup any(Object[] array) {
        return EXPRESSIONS_PROCESSOR.get().any(array);
    }

    /**
     * Get conditional group "all"
     *
     * @param primitiveExpressionsCollection collection of primitive expressions
     */
    // NotNull
    protected final ConditionalGroup all(PrimitiveExpressionsCollection primitiveExpressionsCollection) {
        return EXPRESSIONS_PROCESSOR.get().all(primitiveExpressionsCollection);
    }

    /**
     * Get conditional group "all"
     *
     * @param array Array
     */
    // NotNull
    protected final ConditionalGroup all(Object[] array) {
        return EXPRESSIONS_PROCESSOR.get().all(array);
    }

    /**
     * Get expression
     */
    abstract E getExpression();

    /**
     * Build
     *
     * @param expressionsProcessor The expression processor
     */
    public final E build(ExpressionsProcessor expressionsProcessor) {
        EXPRESSIONS_PROCESSOR.set(expressionsProcessor);
        E result = getExpression();
        EXPRESSIONS_PROCESSOR.remove();
        return result;
    }
}
