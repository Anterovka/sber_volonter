package sbp.com.sbt.dataspace.feather.expressions;

import java.util.Map;

/**
 * Specification with parameters
 *
 * @param <S> Type of specification
 */
public interface SpecificationWithParameters<S> {

    /**
     * Get parameters
     */
    // NotNull
    Map<String, Object> getParameters();

    /**
     * Set parameter
     *
     * @param name  Name
     * @param value The value
     * @return Current specification
     */
    // NotNull
    S setParameter(String name, Object value);
}
