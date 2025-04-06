package sbp.com.sbt.dataspace.feather.expressionscommon;

import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithElementAlias;
import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithEntityType;

/**
 * Abstract specification with type, element alias, and condition
 *
 * @param <S> Type of specification
 */
class AbstractSpecificationWithTypeAndElementAliasAndCondition<S> extends AbstractSpecificationWithCondition<S> implements SpecificationWithEntityType<S>, SpecificationWithElementAlias<S> {

    String type;
    String elementAlias;

    @Override
    public String getType() {
        return type;
    }

    @Override
    public S setType(String type) {
        this.type = type;
        return (S) this;
    }

    @Override
    public String getElementAlias() {
        return elementAlias;
    }

    @Override
    public S setElementAlias(String elementAlias) {
        this.elementAlias = elementAlias;
        return (S) this;
    }
}
