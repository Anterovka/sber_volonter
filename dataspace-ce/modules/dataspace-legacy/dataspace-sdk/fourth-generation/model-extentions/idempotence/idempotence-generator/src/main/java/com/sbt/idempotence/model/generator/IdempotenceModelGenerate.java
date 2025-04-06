package com.sbt.idempotence.model.generator;

import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.sysversion.utils.semver.SemVerUtils;
import sbp.sbt.idempotence.model.interfaces.AggregateIdempotenceEntityApiCall;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sbt.mg.Helper.isSnapshotVersion;

public class IdempotenceModelGenerate implements ModelGenerate {

    public static final String PROJECT_NAME = "IdempotenceModelGenerator";
    private static final String API_CALL = "ApiCall";
    private static final String PARENT_OBJECT = "parentObject";

    private PluginParameters pluginParameters;

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        model.getImports().add(new XmlImport(PROJECT_NAME, ""));
        this.pluginParameters = pluginParameters;
    }

    @Override
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @Override
    public void initModel(XmlModel xmlModel, File file, ModelParameters modelParameters) {

        addIdempotenceElements(xmlModel);

        if (Objects.nonNull(modelParameters.getPdmModel()) && Objects.nonNull(modelParameters.getPdmModel().getModel())) {
            processRemoveIdempotenceElements(modelParameters.getPdmModel().getModel(), xmlModel);
        }
    }

    private void addIdempotenceElements(XmlModel xmlModel) {
        xmlModel.getClassesAsList().stream()
            .filter(XmlModelClass::isBaseClassMark)
            .filter(modelClass -> !modelClass.isDictionary() && !modelClass.isNoIdempotence())
            .filter(xmlModelClass -> xmlModelClass.getPropertiesAsList().stream()
                .noneMatch(property -> property.isParent() && !property.getType().equals(xmlModelClass.getName())))
            .forEach(xmlModelClass -> {
                addIdempotenceClass(xmlModel, xmlModelClass, null);
                addIdempotenceProperties(xmlModelClass, null);
            });
    }

    private void addIdempotenceClass(XmlModel xmlModel, XmlModelClass xmlModelClass, Boolean deprecated) {
        xmlModel.addClassWithoutCheck(XmlModelClass.Builder.create()
            .setName(xmlModelClass.getName() + API_CALL)
            .setFinalClass(Boolean.TRUE)
            .setLabel("Result of processing the package Unit of Work for " + xmlModelClass.getName())
            .setClassAccess(Changeable.SYSTEM)
            .setUseAffinity(false)
            .setDeprecated(deprecated == null ? xmlModelClass.isDeprecated() : deprecated)
            .setVersionDeprecated(Objects.equals(deprecated, true) ? xmlModel.getVersion() : xmlModelClass.getVersionDeprecated())
            .setId(new XmlId(String.class.getSimpleName(), IdCategory.MANUAL))
            .addProperties(
                XmlModelClassProperty.Builder.create()
                    .setName("apiCallId")
                    .setType("String")
                    .setLabel("Package Identifier")
                    .setUnique(true)
                    .setChangeable(Changeable.SYSTEM)
                    .build(),
                XmlModelClassProperty.Builder.create()
                    .setName("firstCallDate")
                    .setType("Date")
                    .setLabel("Date of the first successful package call")
                    .setChangeable(Changeable.SYSTEM)
                    .build(),
                XmlModelClassProperty.Builder.create()
                    .setName("data")
                    .setType("String")
                    .setLabel("Processing package result")
                    .setLength(JpaConstants.MAX_STRING_LENGTH)
                    .setChangeable(Changeable.SYSTEM)
                    .build(),
                XmlModelClassProperty.Builder.create()
                    .setName("bigData")
                    .setType("Text")
                    .setLabel("The result of package processing, used if the result does not fit in the data field")
                    .setChangeable(Changeable.SYSTEM)
                    .build(),
                XmlModelClassProperty.Builder.create()
                    .setName(PARENT_OBJECT)
                    .setLabel("Reference to parent object")
                    .setType(xmlModelClass.getName())
                    .setParent()
                    .setChangeable(Changeable.SYSTEM)
                    .build()
            )
            .build());
    }

    private void addIdempotenceProperties(XmlModelClass xmlModelClass, Boolean deprecated) {
        xmlModelClass
            .addProperty(
                XmlModelClassProperty.Builder.create()
                    .setName("apiCalls")
                    .setLabel("Collection with results of idempotent calls")
                    .setType(xmlModelClass.getName() + API_CALL)
                    .setCollectionType(CollectionType.SET)
                    .setMappedBy("parentObject")
                    .setChangeable(Changeable.SYSTEM)
                    .setDeprecated(deprecated)
                    .setVersionDeprecated(Objects.equals(deprecated, true) ? xmlModelClass.getModel().getVersion() : null)
                    .build());
    }

    /**
     * We take those ApiCall classes that are in the old version and not in the new one. If they are not outdated in the old version, then we are at step 1 and
     * adding outdated elements to the new one.
     * If in the old one outdated, then we add such outdated elements only if the major version was not raised.
     * If a major version has been released, it means that elements should be deleted and no new ones should be added.
     *
     * @param prevModel
     * @param newModel
     */
    private void processRemoveIdempotenceElements(XmlModel prevModel, XmlModel newModel) {
        final List<XmlModelClass> checkingClasses = prevModel.getClassesAsList().stream()
            .filter(it -> it.getName().endsWith(API_CALL) && it.getClassAccess() == Changeable.SYSTEM)
            .filter(it -> it.containsProperty(PARENT_OBJECT)) // The old logic implied 1 table for all calls with the name APICALL
            .filter(it -> Objects.equals(prevModel.getClass(it.getProperty(PARENT_OBJECT).getType()).getClassAccess(), System.class))
            .filter(it -> !newModel.containsClass(it.getName()))
            .collect(Collectors.toList());

        checkingClasses.forEach(modelClass -> {
            final String classNameForAdd = modelClass.getProperty(PARENT_OBJECT).getType();
            final XmlModelClass classForAdd = newModel.getClassNullable(classNameForAdd);
            if (Objects.nonNull(classForAdd)) {
                if (this.pluginParameters.isDeprecateDeletedItems()) {
                    addIdempotenceClass(newModel, classForAdd, true);
                    addIdempotenceProperties(classForAdd, true);
                } else {
                    if (!modelClass.isDeprecated()) {
                        addIdempotenceClass(newModel, classForAdd, true);
                        addIdempotenceProperties(classForAdd, true);
                    } else {
                        if (!SemVerUtils.isMajorIncrease(modelClass.getVersionDeprecated(), newModel.getVersion()) ||
                            isSnapshotVersion(newModel.getVersion())) {
                            addIdempotenceClass(newModel, classForAdd, true);
                            addIdempotenceProperties(classForAdd, true);
                        }
                    }
                }
            }

        });
    }

    @Override
    public List<String> addInterfacesToJpaModel(XmlModelClass xmlModelClass) {
        String modelClassName = xmlModelClass.getName();
        if (!API_CALL.equals(modelClassName) && modelClassName.endsWith(API_CALL)) {
            return Collections.singletonList(
                AggregateIdempotenceEntityApiCall.class.getSimpleName()
                    + "<" + (modelClassName.substring(0, modelClassName.length() - API_CALL.length()) + ">"));
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> addImports(XmlModelClass modelClass) {
        if (!API_CALL.equals(modelClass.getName()) && modelClass.getName().endsWith(API_CALL)) {
            return Collections.singletonList(AggregateIdempotenceEntityApiCall.class.getName());
        }

        return Collections.emptyList();
    }
}
