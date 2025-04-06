package sbp.com.sbt.dataspace.feather.expressionscommon;

import sbp.com.sbt.dataspace.feather.expressions.EntitiesSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of entity specifications
 */
public final class EntitiesSpecificationImpl extends AbstractSpecificationWithTypeAndElementAliasAndCondition<EntitiesSpecification> implements EntitiesSpecification {

    Map<String, Object> parameters = new LinkedHashMap<>();

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public EntitiesSpecification setParameter(String name, Object value) {
        parameters.put(name, value);
        return this;
    }
}
