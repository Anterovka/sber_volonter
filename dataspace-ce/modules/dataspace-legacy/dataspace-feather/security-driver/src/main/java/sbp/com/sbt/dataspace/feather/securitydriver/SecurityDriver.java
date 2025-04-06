package sbp.com.sbt.dataspace.feather.securitydriver;

import java.util.Map;
import java.util.Set;

/**
 * Security driver
 */
// SpringBean
public interface SecurityDriver {

    /**
     * Get restrictions
     * <p>
     * In case of restrictions on the entity type, the result returned is a condition in terms of string expressions.
     * In case of absence of restrictions - the corresponding entry in the result will be absent
     *
     * @param entityTypes Types of entities
     */
    // NotNull
    Map<String, String> getRestrictions(Set<String> entityTypes);
}
