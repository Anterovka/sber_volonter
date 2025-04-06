package sbp.com.sbt.dataspace.extension.status;

import com.sbt.converter.InterfaceConverterToXml;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.status.xml.user.UserXmlGroup;
import com.sbt.status.xml.user.UserXmlStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import sbp.com.sbt.dataspace.extension.status.exceptions.StatusException;
import sbp.com.sbt.dataspace.extension.status.interfaces.StatusFields;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

public class StatusGenerator implements ModelGenerate {

    private PdmModel pdmModel;

    @Override
    public String getProjectName() {
        return "status";
    }

    @Override
    public void addServiceClassesToModel(XmlModel model, PluginParameters pluginParameters) {
        if (StatusUtils.isModelHaveStatuses(model)) {
            addStatusClassesToModel(model);
            addStatusFieldsForClasses(model);
        }
    }

    private void addStatusClassesToModel(XmlModel model) {
        InterfaceConverterToXml.convertToXml(StatusFields.class, model);
        Integer reasonLength = findReasonLength(model);
        if (reasonLength > 0) {
            model.getClass("SysStatusFields").getProperty("reason").setLength(reasonLength);
        }
    }

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        model.getImports().add(new XmlImport(getProjectName(), ""));
    }

    @Override
    public void initModel(XmlModel model, File file, ModelParameters modelParameters) {

        applyChecks(model);

        this.pdmModel = modelParameters.getPdmModel();
        initBackReferences(model);
        if (pdmModel != null && pdmModel.getModel() != null) {
            initBackReferences(this.pdmModel.getModel());
            fillPdmByNewStatuses(model);
        }
    }

    private void applyChecks(XmlModel model) {
        Map<String, List<String>> classToGroupMap = new HashMap<>();
        model.getClassesAsList().stream()
            .filter(XmlModelClass::haveStatuses)
            .forEach(
                modelClass ->
                    modelClass.getStatuses().getGroups()
                        .forEach(group -> {
                                long count = group.getStatuses().stream()
                                    .filter(UserXmlStatus::isInitial)
                                    .count();
                                if (count != 1) {
                                    if (!classToGroupMap.containsKey(modelClass.getName())) {
                                        classToGroupMap.put(modelClass.getName(), new ArrayList<>());
                                    }
                                    classToGroupMap.get(modelClass.getName()).add(group.getCode());
                                }
                            }
                        )
            );
        if (!classToGroupMap.isEmpty()) {
            throw new StatusException(classToGroupMap);
        }
    }

    private void initBackReferences(XmlModel model) {
        model.getClassesAsList()
            .stream().filter(XmlModelClass::haveStatuses)
            .forEach(modelClass -> {
                modelClass.getStatuses().getGroups()
                    .forEach(group -> {
                        group.setModelClass(modelClass);
                        group.getStatuses()
                            .forEach(status -> {
                                status.setGroup(group);
                                status.getStatusTos()
                                    .forEach(to -> to.setStatus(status));
                            });
                    });
            });
    }

    private void addStatusFieldsForClasses(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(ModelHelper::isFirstUserClass)
            .forEach(firstClass -> {
                Map<XmlModelClass, Set<String>> baseClassGroups = new HashMap<>();

                createStatusFieldsForUserClasses(
                    firstClass,
                    Collections.singleton(firstClass),
                    baseClassGroups);
            });
    }

    private void createStatusFieldsForUserClasses(
        XmlModelClass baseClass,
        Collection<XmlModelClass> currentClasses,
        Map<XmlModelClass, Set<String>> baseClassGroups
    ) {
        currentClasses.forEach(currentClass -> {
            if (currentClass.haveStatuses()) {
                Set<String> currentClassGroups = currentClass.getStatuses().getGroups().stream()
                    .map(UserXmlGroup::getCode)
                    .collect(Collectors.toSet());
                currentClassGroups.forEach(groupName -> {

                    if (!baseClassGroups.containsKey(baseClass) || !baseClassGroups.get(baseClass).contains(groupName)) {
                        addStatusFieldsProperty(groupName, currentClass);

                        baseClassGroups.putIfAbsent(baseClass, new HashSet<>());
                        baseClassGroups.get(baseClass).add(groupName);
                    }
                });
            }
            createStatusFieldsForUserClasses(
                baseClass,
                ModelHelper.getAllChildClasses(currentClass),
                baseClassGroups);
        });
    }

    private void addStatusFieldsProperty(String group, XmlModelClass modelClass) {
        String statusFieldName = "statusFor" + StringUtils.capitalize(group);
        XmlModelClassProperty statusLink = XmlModelClassProperty.Builder.create()
            .setName(statusFieldName)
            .setType("SysStatusFields")
            .build();

        modelClass.addProperty(statusLink);
    }

    private Integer findReasonLength(XmlModel model) {

        final MutableInt maxLength = new MutableInt(0);

        model.getClassesAsList().stream()
            .filter(XmlModelClass::haveStatuses)
            .forEach(modelClass ->
                {
                    OptionalInt maxLengthOptional =
                        modelClass.getStatuses().getGroups().stream()
                            .map(group -> {
                                try {
                                    return Integer.parseInt(group.getReasonLength());
                                } catch (NumberFormatException ex) {
                                    return 0;
                                }
                            })
                            .mapToInt(Integer::intValue)
                            .max();
                    if (maxLengthOptional.isPresent() && maxLengthOptional.getAsInt() > maxLength.getValue()) {
                        maxLength.setValue(maxLengthOptional.getAsInt());
                    }
                }
            );
        return maxLength.getValue();
    }

    private void fillPdmByNewStatuses(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(XmlModelClass::haveStatuses)
            .forEach(modelClass -> {
                modelClass.getStatuses().getGroups()
                    .forEach(group -> {
                        if (StatusUtils.isModelDoesNotHave(this.pdmModel.getModel(), group)) {
                            StatusUtils.addTo(this.pdmModel.getModel(), group);
                        }
                        group.getStatuses()
                            .forEach(status -> {
                                if (StatusUtils.isModelDoesNotHave(this.pdmModel.getModel(), status)) {
                                    StatusUtils.addTo(this.pdmModel.getModel(), status);
                                }
                                status.getStatusTos()
                                    .forEach(to -> {
                                        if (StatusUtils.isModelDoesNotHave(this.pdmModel.getModel(), to)) {
                                            StatusUtils.addTo(this.pdmModel.getModel(), to);
                                        }
                                    });
                            });
                    });
            });
    }
}
