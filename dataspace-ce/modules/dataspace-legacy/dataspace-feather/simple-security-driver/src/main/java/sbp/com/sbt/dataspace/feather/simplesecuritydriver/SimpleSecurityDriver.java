package sbp.com.sbt.dataspace.feather.simplesecuritydriver;

import sbp.com.sbt.dataspace.feather.securitydriver.SecurityDriver;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple security driver
 */
class SimpleSecurityDriver implements SecurityDriver {

    Map<String, String> restrictions;

    /**
     * @param restrictions Ограничения
     */
    SimpleSecurityDriver(Map<String, String> restrictions) {
        this.restrictions = restrictions;
    }

    @Override
    public Map<String, String> getRestrictions(Set<String> entityTypes) {
        Map<String, String> result = new LinkedHashMap<>();
        entityTypes.forEach(entityType -> {
            String restriction = restrictions.get(entityType);
            if (restriction != null) {
                result.put(entityType, restriction);
            }
        });
        return result;
    }
}
