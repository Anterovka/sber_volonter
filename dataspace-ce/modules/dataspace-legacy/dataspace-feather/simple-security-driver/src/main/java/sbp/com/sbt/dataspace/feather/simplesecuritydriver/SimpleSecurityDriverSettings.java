package sbp.com.sbt.dataspace.feather.simplesecuritydriver;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.getFullDescription;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.param;

/**
 * Settings of simple security driver
 */
public final class SimpleSecurityDriverSettings {

    Map<String, String> entityRestrictions = new LinkedHashMap<>();

    /**
     * Get entity restrictions
     */
    public Map<String, String> getEntityRestrictions() {
        return Collections.unmodifiableMap(entityRestrictions);
    }

    /**
     * Set entity restriction
     *
     * @param entityType  Entity type
     * @param restriction Restriction
     */
    public SimpleSecurityDriverSettings setEntityRestriction(String entityType, String restriction) {
        entityRestrictions.put(checkNotNull(entityType, "Entity type"), checkNotNull(restriction, "Restriction"));
        return this;
    }

    @Override
    public String toString() {
        return getFullDescription("The security driver settings",
            param("Entity restrictions", entityRestrictions));
    }
}
