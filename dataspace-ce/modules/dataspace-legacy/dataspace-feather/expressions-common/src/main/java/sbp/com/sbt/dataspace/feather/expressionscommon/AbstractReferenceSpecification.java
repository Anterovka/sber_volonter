package sbp.com.sbt.dataspace.feather.expressionscommon;

import sbp.com.sbt.dataspace.feather.expressions.SpecificationWithAlias;

/**
 * Abstract reference specification
 *
 * @param <S> Type of specification
 */
class AbstractReferenceSpecification<S> extends AbstractSpecificationWithEntityType<S> implements SpecificationWithAlias<S> {

    String alias;

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public S setAlias(String alias) {
        this.alias = alias;
        return (S) this;
    }
}
