package sbp.com.sbt.dataspace.feather.modeldescriptioncommon;

import sbp.com.sbt.dataspace.feather.modeldescription.CheckModelDescriptionException;
import sbp.com.sbt.dataspace.feather.modeldescription.EntityDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.EnumDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.GroupDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescription;
import sbp.com.sbt.dataspace.feather.modeldescription.ModelDescriptionCheck;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.forEach;

/**
 * Implementation of model description
 */
public final class ModelDescriptionImpl extends AbstractObjectWithMetaDataManager implements ModelDescription {

    Map<String, EntityDescription> entityDescriptions;
    Map<String, List<GroupDescription>> groupDescriptions;
    Map<String, EnumDescription> enumDescriptions;

    /**
     * @param modelDescriptionSettings Model description settings
     * @param modelDescriptionChecks   Checks of the description of the model
     */
    public ModelDescriptionImpl(ModelDescriptionSettings modelDescriptionSettings, List<ModelDescriptionCheck> modelDescriptionChecks) {
        enumDescriptions = new LinkedHashMap<>();
        modelDescriptionSettings.enumDescriptionsSettings.forEach((enumType, enumDescriptionSettings) -> {
            EnumDescriptionImpl enumDescription = new EnumDescriptionImpl();
            enumDescription.modelDescription = this;
            enumDescription.name = enumType;
            enumDescription.process(enumDescriptionSettings);
            enumDescriptions.put(enumType, enumDescription);
        });
        enumDescriptions = Collections.unmodifiableMap(enumDescriptions);
        groupDescriptions = new LinkedHashMap<>();
        entityDescriptions = new LinkedHashMap<>(modelDescriptionSettings.entityDescriptionsSettings.size());
        modelDescriptionSettings.entityDescriptionsSettings.forEach((entityType, entityDescriptionSettings) -> {
            EntityDescriptionImpl entityDescription = new EntityDescriptionImpl();
            entityDescription.modelDescription = this;
            entityDescription.name = entityType;
            entityDescriptions.put(entityType, entityDescription);
        });
        Map<String, EntityDescriptionImpl> entityDescriptionImpls = (Map<String, EntityDescriptionImpl>) (Object) entityDescriptions;
        forEach(modelDescriptionSettings.entityDescriptionsSettings.entrySet().stream(), entry -> entityDescriptionImpls.get(entry.getKey()).process(entry.getValue()), (exception, entry) -> new ProcessEntityDescriptionException(exception, entry.getKey()));
        forEach(modelDescriptionSettings.entityDescriptionsSettings.entrySet().stream(), entry -> entityDescriptionImpls.get(entry.getKey()).process2(entry.getValue()), (exception, entry) -> new ProcessEntityDescriptionException(exception, entry.getKey()));
        forEach(modelDescriptionSettings.entityDescriptionsSettings.entrySet().stream(), entry -> entityDescriptionImpls.get(entry.getKey()).process3(), (exception, entry) -> new ProcessEntityDescriptionException(exception, entry.getKey()));
        entityDescriptions = Collections.unmodifiableMap(entityDescriptions);
        forEach(modelDescriptionChecks.stream(), modelDescriptionCheck -> modelDescriptionCheck.check(this), CheckModelDescriptionException::new);
        groupDescriptions.keySet().forEach(groupName -> groupDescriptions.put(groupName, Collections.unmodifiableList(groupDescriptions.get(groupName))));
        groupDescriptions = Collections.unmodifiableMap(groupDescriptions);
    }

    @Override
    public Map<String, EntityDescription> getEntityDescriptions() {
        return entityDescriptions;
    }

    @Override
    public EntityDescription getEntityDescription(String entityType) {
        EntityDescription entityDescription = entityDescriptions.get(entityType);
        if (entityDescription == null) {
            throw new EntityDescriptionNotFoundException(entityType);
        }
        return entityDescription;
    }

    @Override
    public Map<String, List<GroupDescription>> getGroupDescriptions() {
        return groupDescriptions;
    }

    @Override
    public Map<String, EnumDescription> getEnumDescriptions() {
        return enumDescriptions;
    }

    @Override
    public EnumDescription getEnumDescription(String enumType) {
        EnumDescription enumDescription = enumDescriptions.get(enumType);
        if (enumDescription == null) {
            throw new EnumDescriptionNotFoundException(enumType);
        }
        return enumDescription;
    }
}
