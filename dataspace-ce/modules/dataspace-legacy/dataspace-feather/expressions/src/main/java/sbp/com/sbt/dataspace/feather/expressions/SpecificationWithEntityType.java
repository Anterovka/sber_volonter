package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Specification with entity type
 *
 * @param <S> Type of specification
 */
public interface SpecificationWithEntityType<S> {

    /**
     * Get entity type
     */
    String getType();

    /**
     * Set entity type
     *
     * @return Current specification
     */
    // NotNull
    S setType(String type);
}
