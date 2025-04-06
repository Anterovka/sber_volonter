package sbp.com.sbt.dataspace.feather.expressionscommon;

import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithEntityType;

/**
 * Abstract specification with entity type
 *
 * @param <S> Type of specification
 */
class AbstractSpecificationWithEntityType<S> implements SpecificationWithEntityType<S> {

    String type;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public S setType(String type) {
        this.type = type;
        return (S) this;
    }
}
