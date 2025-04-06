package sbp.com.sbt.dataspace.feather.expressions;

/**
 * Specification with an element alias
 *
 * @param <S> Type of specification
 */
public interface SpecificationWithElementAlias<S> {

    /**
     * Get alias of element
     */
    String getElementAlias();

    /**
     * Set alias for element
     *
     * @return Current specification
     */
    // NotNull
    S setElementAlias(String elementAlias);
}
