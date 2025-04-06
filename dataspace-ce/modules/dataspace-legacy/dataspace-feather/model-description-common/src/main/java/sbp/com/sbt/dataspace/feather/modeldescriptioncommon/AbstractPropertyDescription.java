package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PropertyDescription;

/**
 * Abstract description of the property
 */
abstract class AbstractPropertyDescription extends AbstractObjectWithMetaDataManager implements PropertyDescription {

    EntityDescription ownerEntityDescription;
    String name;

    /**
     * Process
     *
     * @param propertyDescriptionSettings The property description settings
     */
    abstract void process(PropertyDescriptionSettings propertyDescriptionSettings);

    @Override
    public EntityDescription getOwnerEntityDescription() {
        return ownerEntityDescription;
    }

    @Override
    public String getName() {
        return name;
    }
}
