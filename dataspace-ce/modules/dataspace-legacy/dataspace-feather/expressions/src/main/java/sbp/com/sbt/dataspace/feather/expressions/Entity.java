package sbp.com.sbt.dataspace.feather.expressions;

import java.util.Arrays;
import java.util.function.Consumer;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Entity
 */
public interface Entity {

    /**
     * Get type
     */
    // NotNull
    PrimitiveExpression type();

    /**
     * Get id
     */
    // NotNull
    PrimitiveExpression id();

    /**
     * Get primitive
     *
     * @param propertyName Property name
     */
    // NotNull
    PrimitiveExpression prim(String propertyName);

    /**
     * Get collection of primitives
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    PrimitiveExpressionsCollection prims(String propertyName, Consumer<PrimitivesCollectionSpecification> specificationCode);

    /**
     * Get collection of primitives
     *
     * @param propertyName Property name
     */
    // NotNull
    default PrimitiveExpressionsCollection prims(String propertyName) {
        return prims(propertyName, null);
    }

    /**
     * Get link
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    Entity ref(String propertyName, Consumer<ReferenceSpecification> specificationCode);

    /**
     * Get link
     *
     * @param propertyName Property name
     */
    // NotNull
    default Entity ref(String propertyName) {
        return ref(propertyName, null);
    }

    /**
     * Get link with backlink
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    Entity refB(String propertyName, Consumer<BackReferenceReferenceSpecification> specificationCode);

    /**
     * Get link with backlink
     *
     * @param propertyName Property name
     */
    // NotNull
    default Entity refB(String propertyName) {
        return refB(propertyName, null);
    }

    /**
     * Get link collection
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    EntitiesCollection refs(String propertyName, Consumer<ReferencesCollectionSpecification> specificationCode);

    /**
     * Get link collection
     *
     * @param propertyName Property name
     */
    // NotNull
    default EntitiesCollection refs(String propertyName) {
        return refs(propertyName, null);
    }

    /**
     * Get a collection of links with backlinks
     *
     * @param propertyName      Property name
     * @param specificationCode The specification code
     */
    // NotNull
    EntitiesCollection refsB(String propertyName, Consumer<BackReferenceReferencesCollectionSpecification> specificationCode);

    /**
     * Get a collection of links with backlinks
     *
     * @param propertyName Property name
     */
    // NotNull
    default EntitiesCollection refsB(String propertyName) {
        return refsB(propertyName, null);
    }

    /**
     * Get grouping
     *
     * @param propertyName Property name
     */
    // NotNull
    Group group(String propertyName);

    /**
     * Get condition "Equals null"
     */
    // NotNull
    default Condition isNull() {
        return id().isNull();
    }

    /**
     * Get condition "Not equal to null"
     */
    // NotNull
    default Condition isNotNull() {
        return id().isNotNull();
    }

    /**
     * Get condition "Exists"
     */
    // NotNull
    Condition exists();

    /**
     * Get condition "Equals"
     *
     * @param entity Entity
     */
    // NotNull
    default Condition eq(Entity entity) {
        return id().eq(checkNotNull(entity, "Entity").id());
    }

    /**
     * Get condition "Not equal to"
     *
     * @param entity Entity
     */
    // NotNull
    default Condition notEq(Entity entity) {
        return id().notEq(checkNotNull(entity, "Entity").id());
    }

    /**
     * Get condition "B"
     *
     * @param entity1  Entity 1
     * @param entities Entities
     */
    // NotNull
    default Condition in(Entity entity1, Entity... entities) {
        return id().in(checkNotNull(entity1, "Entity 1").id(), Arrays.stream(entities)
            .map(entity2 -> checkNotNull(entity2, "Entity from entities"))
            .map(Entity::id)
            .toArray(PrimitiveExpression[]::new));
    }

    /**
     * Get condition "B"
     *
     * @param entitiesCollection The collection of entities
     */
    // NotNull
    default Condition in(EntitiesCollection entitiesCollection) {
        return id().in(checkNotNull(entitiesCollection, "Entity collection").id());
    }
}
