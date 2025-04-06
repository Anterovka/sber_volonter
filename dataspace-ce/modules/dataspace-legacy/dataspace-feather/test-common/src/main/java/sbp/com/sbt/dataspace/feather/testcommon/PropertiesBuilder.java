package sbp.com.sbt.dataspace.feather.testcommon;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Property Builder
 */
public final class PropertiesBuilder {

    Map<String, Object> properties = new LinkedHashMap<>();

    /**
     * Add property
     *
     * @param propertyName Property name
     * @param value        Value
     * @return Current property builder
     */
    public PropertiesBuilder add(String propertyName, Object value) {
        properties.put(propertyName, value);
        return this;
    }

    /**
     * Create property builder
     */
    public static PropertiesBuilder propBuilder() {
        return new PropertiesBuilder();
    }
}
