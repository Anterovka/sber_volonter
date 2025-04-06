package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Specification with condition
 *
 * @param <S> Type of specification
 */
public interface SpecificationWithCondition<S> {

    /**
     * Get condition
     */
    Condition getCondition();

    /**
     * Set condition
     *
     * @return Current specification
     */
    // NotNull
    S setCondition(Condition condition);
}
