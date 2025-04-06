package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferencesCollectionDescription;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Implementation of the link collection description
 */
class ReferencesCollectionDescriptionImpl extends AbstractCollectionDescription implements ReferencesCollectionDescription {

    EntityDescription entityDescription;

    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        super.process(propertyDescriptionSettings);
        entityDescription = ownerEntityDescription.getModelDescription().getEntityDescription(checkNotNull(((ReferencesCollectionDescriptionSettings) propertyDescriptionSettings).entityType, "Entity type"));
    }

    @Override
    public EntityDescription getEntityDescription() {
        return entityDescription;
    }
}
