package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Specification with alias
 *
 * @param <S> Type of specification
 */
public interface SpecificationWithAlias<S> {

    /**
     * Get alias
     */
    String getAlias();

    /**
     * Alias
     *
     * @return Current specification
     */
    // NotNull
    S setAlias(String elementAlias);
}
