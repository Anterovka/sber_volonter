package sbp.com.sbt.dataspace.feather.expressions;

import java.util.function.Consumer;

/**
 * Expression handler
 */
public interface ExpressionsProcessor {

    /**
     * Get root entity
     *
     * @param specificationCode The specification code
     */
    // NotNull
    Entity root(Consumer<RootSpecification> specificationCode);

    /**
     * Get root entity
     */
    // NotNull
    default Entity root() {
        return root(null);
    }

    /**
     * Get collection element (primitive expression)
     */
    // NotNull
    PrimitiveExpression elemPE();

    /**
     * Get collection element (entity)
     *
     * @param specificationCode The specification code
     */
    // NotNull
    Entity elemE(Consumer<EntityElementSpecification> specificationCode);

    /**
     * Get collection element (entity)
     */
    // NotNull
    default Entity elemE() {
        return elemE(null);
    }

    /**
     * Get entity with alias
     *
     * @param alias             Alias
     * @param specificationCode The specification code
     */
    // NotNull
    Entity aliasedEntity(String alias, Consumer<AliasedEntitySpecification> specificationCode);

    /**
     * Get entity with alias
     *
     * @param alias Alias
     */
    // NotNull
    default Entity aliasedEntity(String alias) {
        return aliasedEntity(alias, null);
    }

    /**
     * Get primitive value
     *
     * @param primitiveValue Primitive value
     */
    // NotNull
    PrimitiveExpression prim(Object primitiveValue);

    /**
     * Get the current date and time (with offset)
     */
    // NotNull
    PrimitiveExpression now();

    /**
     * Get the first non-zero element
     *
     * @param primitiveExpression1 Primitive expression 1
     * @param primitiveExpressions Primitive expressions
     */
    // NotNull
    PrimitiveExpression coalesce(PrimitiveExpression primitiveExpression1, PrimitiveExpression... primitiveExpressions);

    /**
     * Get entities
     *
     * @param specificationCode The specification code
     */
    // NotNull
    EntitiesCollection entities(Consumer<EntitiesSpecification> specificationCode);

    /**
     * Get RAW object (primitive expression)
     *
     * @param raw RAW-объект
     */
    // NotNull
    PrimitiveExpression rawPE(Object raw);

    /**
     * Get conditional group "any"
     *
     * @param primitiveExpressionsCollection of primitive expressions
     */
    // NotNull
    ConditionalGroup any(PrimitiveExpressionsCollection primitiveExpressionsCollection);

    /**
     * Get conditional group "any"
     *
     * @param array Array
     */
    // NotNull
    ConditionalGroup any(Object[] array);

    /**
     * Get conditional group "all"
     *
     * @param primitiveExpressionsCollection of primitive expressions
     */
    // NotNull
    ConditionalGroup all(PrimitiveExpressionsCollection primitiveExpressionsCollection);

    /**
     * Get conditional group "all"
     *
     * @param array Array
     */
    // NotNull
    ConditionalGroup all(Object[] array);
}
