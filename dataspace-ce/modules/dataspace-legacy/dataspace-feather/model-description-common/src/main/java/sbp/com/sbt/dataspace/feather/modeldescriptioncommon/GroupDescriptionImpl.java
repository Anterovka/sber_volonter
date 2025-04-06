package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.PrimitiveDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ReferenceDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.checkNotNull;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.getPropertyDescription;
import static sbp.com.sbt.dataspace.feather.modeldescriptioncommon.Helper.processPropertyDescriptions;

/**
 * Implementation of grouping
 */
class GroupDescriptionImpl extends AbstractPropertyDescription implements GroupDescription {

    String groupName;
    Map<String, PrimitiveDescription> primitiveDescriptions;
    Map<String, ReferenceDescription> referenceDescriptions;

    @Override
    void process(PropertyDescriptionSettings propertyDescriptionSettings) {
        GroupDescriptionSettings groupDescriptionSettings = (GroupDescriptionSettings) propertyDescriptionSettings;
        groupName = checkNotNull(groupDescriptionSettings.groupName, "Grouping Name");
        primitiveDescriptions = new LinkedHashMap<>(groupDescriptionSettings.primitiveDescriptionsSettings.size());
        processPropertyDescriptions(ownerEntityDescription, groupDescriptionSettings.primitiveDescriptionsSettings, primitiveDescriptions, PrimitiveDescriptionImpl::new);
        primitiveDescriptions = Collections.unmodifiableMap(primitiveDescriptions);
        referenceDescriptions = new LinkedHashMap<>(groupDescriptionSettings.referenceDescriptionsSettings.size());
        processPropertyDescriptions(ownerEntityDescription, groupDescriptionSettings.referenceDescriptionsSettings, referenceDescriptions, ReferenceDescriptionImpl::new);
        referenceDescriptions = Collections.unmodifiableMap(referenceDescriptions);
        List<GroupDescription> groupDescriptions = ((ModelDescriptionImpl) ownerEntityDescription.getModelDescription()).groupDescriptions.computeIfAbsent(groupName, key -> new ArrayList<>());
        if (!groupDescriptions.isEmpty()) {
            GroupDescription groupDescription = groupDescriptions.get(0);
            if (!(groupDescription.getPrimitiveDescriptions().size() == primitiveDescriptions.size()
                    && groupDescription.getReferenceDescriptions().size() == referenceDescriptions.size()
                    && groupDescription.getPrimitiveDescriptions().values().stream()
                    .allMatch(primitiveDescription -> {
                        PrimitiveDescription primitiveDescription2 = primitiveDescriptions.get(primitiveDescription.getName());
                        return primitiveDescription2 != null && primitiveDescription2.getType() == primitiveDescription.getType() && primitiveDescription2.isMandatory() == primitiveDescription.isMandatory() && primitiveDescription2.getEnumDescription() == primitiveDescription.getEnumDescription();
                    })
                    && groupDescription.getReferenceDescriptions().values().stream()
                    .allMatch(referenceDescription -> {
                        ReferenceDescription referenceDescription2 = referenceDescriptions.get(referenceDescription.getName());
                        return referenceDescription2 != null && referenceDescription2.getEntityDescription() == referenceDescription.getEntityDescription() && referenceDescription2.isMandatory() == referenceDescription.isMandatory() && Objects.equals(referenceDescription2.getEntityReferencePropertyName(), referenceDescription.getEntityReferencePropertyName()) && Objects.equals(referenceDescription2.getEntityReferencesCollectionPropertyName(), referenceDescription.getEntityReferencesCollectionPropertyName());
                    }))) {
                throw new DifferentGroupStructuresWithSameNameFoundException(groupName);
            }
        }
        groupDescriptions.add(this);
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public Map<String, PrimitiveDescription> getPrimitiveDescriptions() {
        return primitiveDescriptions;
    }

    @Override
    public PrimitiveDescription getPrimitiveDescription(String propertyName) {
        return getPropertyDescription(primitiveDescriptions, propertyName, PrimitiveDescriptionNotFoundException::new);
    }

    @Override
    public Map<String, ReferenceDescription> getReferenceDescriptions() {
        return referenceDescriptions;
    }

    @Override
    public ReferenceDescription getReferenceDescription(String propertyName) {
        return getPropertyDescription(referenceDescriptions, propertyName, ReferenceDescriptionNotFoundException::new);
    }
}
