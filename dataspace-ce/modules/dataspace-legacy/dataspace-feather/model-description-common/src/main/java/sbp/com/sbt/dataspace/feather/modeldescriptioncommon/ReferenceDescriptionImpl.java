package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import java.util.Collections;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;

/**
 * Implementation of link description
 */
class ReferenceDescriptionImpl extends AbstractPropertyDescriptionWithColumnName implements ReferenceDescription {

    EntityDescription entityDescription;
    String entityReferencePropertyName;
    String entityReferencesCollectionPropertyName;
    boolean mandatory;

    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        super.process(propertyDescriptionSettings);
        ReferenceDescriptionSettings referenceDescriptionSettings = (ReferenceDescriptionSettings) propertyDescriptionSettings;
        entityDescription = ownerEntityDescription.getModelDescription().getEntityDescription(checkNotNull(referenceDescriptionSettings.entityType, "Entity type"));
        mandatory = referenceDescriptionSettings.mandatory;
        entityReferencePropertyName = referenceDescriptionSettings.entityReferencePropertyName;
        entityReferencesCollectionPropertyName = referenceDescriptionSettings.entityReferencesCollectionPropertyName;
        if (entityReferencePropertyName != null && entityReferencesCollectionPropertyName != null) {
            throw new BackReferenceOveruseException();
        } else if (entityReferencePropertyName != null) {
            EntityDescriptionImpl entityDescriptionImpl = (EntityDescriptionImpl) entityDescription;
            if (entityDescriptionImpl.declaredReferenceBackReferenceDescriptions.containsKey(entityReferencePropertyName)) {
                throw new DuplicatePropertyNamesFoundException(entityDescriptionImpl.name, Collections.singleton(entityReferencePropertyName));
            }
            entityDescriptionImpl.declaredReferenceBackReferenceDescriptions.put(entityReferencePropertyName, this);
        } else if (entityReferencesCollectionPropertyName != null) {
            EntityDescriptionImpl entityDescriptionImpl = (EntityDescriptionImpl) entityDescription;
            if (entityDescriptionImpl.declaredReferencesCollectionBackReferenceDescriptions.containsKey(entityReferencesCollectionPropertyName)) {
                throw new DuplicatePropertyNamesFoundException(entityDescriptionImpl.name, Collections.singleton(entityReferencesCollectionPropertyName));
            }
            entityDescriptionImpl.declaredReferencesCollectionBackReferenceDescriptions.put(entityReferencesCollectionPropertyName, this);
        }
    }

    @Override
    public EntityDescription getEntityDescription() {
        return entityDescription;
    }

    @Override
    public String getEntityReferencePropertyName() {
        return entityReferencePropertyName;
    }

    @Override
    public String getEntityReferencesCollectionPropertyName() {
        return entityReferencesCollectionPropertyName;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }
}
