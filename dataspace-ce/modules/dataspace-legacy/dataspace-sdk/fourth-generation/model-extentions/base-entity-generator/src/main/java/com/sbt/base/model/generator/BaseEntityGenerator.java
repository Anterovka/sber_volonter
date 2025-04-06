package com.sbt.base.model.generator;

import com.sbt.base.model.generator.exception.AmbiguousIdException;
import com.sbt.base.model.generator.exception.ExtendedClassChangeIdInInheritedClassException;
import com.sbt.base.model.generator.exception.NoIdNotSupportedForBaseEntityException;
import com.sbt.base.model.generator.exception.NotSupportedIdTypeException;
import com.sbt.base.model.generator.exception.SnowflakeOnEmbeddableKeyNotSupportedException;
import com.sbt.converter.InterfaceConverterToXml;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.checkmodel.PropertyNameAlreadyDefinedException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.model.base.Entity;
import com.sbt.model.base.IdEntity;
import com.sbt.model.support.GeneratorConstants;
import com.sbt.model.support.GeneratorIdExternalValueInitializer;
import com.sbt.model.support.GeneratorIdPrefix;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.useIdGenerator;
import static com.sbt.model.support.GeneratorConstants.GENERATOR_KIND_PARAMETER_NAME;
import static com.sbt.model.support.GeneratorConstants.GENERATOR_REQUIRED_VALUE_PARAMETER_NAME;

public class BaseEntityGenerator implements ModelGenerate {

    private static final String ENTITY_PROPERTIES_TEMPLATE = getTemplate("/generate/hibernate/entityProperties.template");
    private static final String BASE_ENTITY_PROPERTIES_TEMPLATE = getTemplate("/generate/hibernate/baseEntityProperties.template");
    private static final String DATE_FORMAT_TEMPLATE = getTemplate("/generate/hibernate/dateFormat.template");
    private static final String ADDITIONAL_PROPERTIES_TEMPLATE = getTemplate("/generate/hibernate/additionalProperties.template");
    private static final String GENERATOR_ID_EXTERNAL_VALUE_INITIALIZER_PROPERTIES_TEMPLATE = getTemplate("/generate/hibernate/generatorIdExternalValueInitializer.template");

    public static final String PROJECT_NAME = "BASE_ENTITY";

    private static final int PRIORITY = 100;

    private static final String CHG_CNT_ID = "chgCnt";
    private static final String LAST_CHANGE_DATE = "lastChangeDate";

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        model.getImports().add(new XmlImport(PROJECT_NAME, ""));
    }

    @Override
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }

    @Override
    public void initModel(XmlModel model, File file, ModelParameters modelParameters) {
        ModelHelper.initBaseMarkClass(model);
    }

    @Override
    public void postInitModel(XmlModel model, File file, ModelParameters modelParameters) {
        model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getExtendedClassName() == null)
            .filter(modelClass -> !modelClass.isEmbeddable())
            .filter(modelClass -> !modelClass.isDictionary())
            .filter(modelClass -> !modelClass.isNoBaseEntity())
            .forEach(modelClass -> modelClass.setExtendedClassName(JpaConstants.ENTITY_CLASS_NAME));

        InterfaceConverterToXml.convertToXml(Entity.class, model);

// Sets the base class flag on model classes where necessary
        ModelHelper.initBaseMarkClass(model);

// Sets XmlId on all model classes
        initCorrectIdTypes(model);
// Adds objectId and sysIdPrefix properties (if necessary) to base classes + creates primary index
        addObjectIdToBaseClasses(model);
// Adds to root classes sysCloneOrigin
        addCloneableProperty(model);
// why-so transfer id to income property of all descendants
        addIncomeObjectIdPropertiesForInheritors(model);
    }

    /**
     * Adds an objectId property to the income collection of all descendants of the base classes of the model
     */
// Why is this done - it is not yet clear
    private void addIncomeObjectIdPropertiesForInheritors(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(XmlModelClass::isBaseClassMark)
            .forEach(modelClass -> {
                Set<XmlModelClass> allInheritedClasses = ModelHelper.getAllChildClasses(modelClass);
                allInheritedClasses.forEach(modelClass1 ->
                    modelClass1.addIncomePropertyWithoutCheck(
                        modelClass.getPropertyWithHierarchyInSingleTable(JpaConstants.OBJECT_ID)
                    ));
            });
    }

    /**
     * Adds on root classes sysCloneOrigin
     */
    private void addCloneableProperty(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(XmlModelClass::isBaseClassMark)
            .filter(xmlModelClass -> !xmlModelClass.containsProperty(JpaConstants.SYS_CLONE_ORIGIN))
            .filter(xmlModelClass -> ModelHelper
                .getAllChildClasses(xmlModelClass, true)
                .stream()
                .anyMatch(UserXmlModelClass::isCloneable)
            )
            .forEach(modelClass -> modelClass
                .addProperty(
                    XmlModelClassProperty
                        .Builder
                        .create()
                        .setName(JpaConstants.SYS_CLONE_ORIGIN)
                        .setType("String")
                        .setLabel("Entity identifier that served as a template during creation")
                        .setColumnName("SYS_CLONEORIGIN")
                        .setChangeable(Changeable.READ_ONLY)
                        .build()
                )
            );
    }

    /**
     * Adds properties objectId and sysIdPrefix (if necessary) to base classes + creates primary index
     */
    private void addObjectIdToBaseClasses(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(it -> it.getId() != null && !Objects.equals(it.getId().getIdCategory(), IdCategory.NO_ID))
            .forEach(modelClass -> {
                if (!modelClass.isBaseClassMark() || modelClass.isEvent()) {
                    return;
                }

                //if the property objectId already exists and the class is custom,then an error occurs
                if (modelClass.getPropertyNullable(JpaConstants.OBJECT_ID) != null &&
                    Objects.equals(modelClass.getClassAccess(), Changeable.UPDATE)) {
                    throw new PropertyNameAlreadyDefinedException(
                        modelClass.getName(),
                        Collections.singletonList(JpaConstants.OBJECT_ID)
                    );
                }

                final XmlModelClassProperty idField = findIdField(modelClass);
                if (idField != null) {
                    ModelHelper.addComplexIndexToClass(
                        modelClass,
                        false,
                        true,
                        Collections.singletonList(idField),
                        false
                    );
                } else {
                    modelClass.addProperty(XmlModelClassProperty.Builder.create()
                        .setName(JpaConstants.OBJECT_ID)
                        .setType(modelClass.getId().getType())
                        .setLabel("Application Object ID")
                        .setMandatory(true)
                        .setChangeable(Changeable.READ_ONLY)
                        .setId(true)
                        .setLength(modelClass.getId().getLength())
                        .build()
                    );

                    ModelHelper.addComplexIndexToClass(
                        modelClass,
                        false,
                        true,
                        Collections.singletonList(modelClass.getPropertyWithHierarchyInSingleTable(JpaConstants.OBJECT_ID)),
                        false
                    );
                }

                if (modelClass.isUseIdPrefix()) {
                    modelClass.addProperty(XmlModelClassProperty.Builder.create()
                        .setName(JpaConstants.OBJECT_ID_PREFIX)
                        .setType("String")
                        .setLabel("Object ID prefix")
                        .setColumnName("SYS_ID_PREFIX")
                        .setChangeable(Changeable.CREATE)
                        .build());
                }

            });
    }

    private static XmlModelClassProperty findIdField(XmlModelClass modelClass) {
        final List<XmlModelClassProperty> idFields = modelClass.getPropertiesAsList().stream()
            .filter(XmlModelClassProperty::isId)
            .collect(Collectors.toList());
        if (idFields.size() > 1) {
            throw new AmbiguousIdException(
                modelClass.getName(),
                idFields.stream().map(XmlModelClassProperty::getName).collect(Collectors.toList())
            );
        }
        if (idFields.isEmpty()) {
            return null;
        }
        return idFields.get(0);
    }

    private void initCorrectIdTypes(XmlModel model) {
// On BaseEntity, we set the IdCategory.NO_ID
//refactoring
        model.getClass(JpaConstants.ENTITY_CLASS_NAME)
            .setId(new XmlId(String.class.getSimpleName(), IdCategory.NO_ID));

// For embeddable and root abstract classes, we set IdCategory.NO_ID.
        model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.isEmbeddable()
                || (modelClass.isAbstract() && ModelHelper.getAllSuperClasses(modelClass)
                .stream().allMatch(XmlModelClass::isAbstract)))
            .forEach(modelClass -> modelClass.setId(new XmlId(String.class.getSimpleName(), IdCategory.NO_ID)));

        List<XmlModelClass> baseModelClasses =
            model.getClassesAsList().stream().filter(XmlModelClass::isBaseClassMark).collect(Collectors.toList());

        baseModelClasses.forEach(modelClass -> {
            XmlId modelClassId = modelClass.getId();
            if (modelClassId == null) {
                modelClass.setId(XmlId.defaultId(modelClass, model.getAutoIdMethod()));
            } else {
                String type = modelClassId.getType();

                XmlModelClass xmlModelClass = model.getClassNullable(type);
                if (!String.class.getSimpleName().equals(type) && (xmlModelClass == null || !xmlModelClass.isEmbeddable())) {
                    throw new NotSupportedIdTypeException(modelClass.getName(), type);
                }

// for embeddable classes manual installation
                if (xmlModelClass != null) {
                    modelClassId.setIdCategory(IdCategory.MANUAL);
                }
            }
        });

        baseModelClasses.forEach(modelClass -> {
            List<XmlModelClass> allInheritedClasses = new ArrayList<>(ModelHelper.getAllChildClasses(modelClass));

            // copy type of identifier of base class to descendants
            allInheritedClasses.forEach(inheritedClass -> {
                //If the type of the child identifier is specified and does not match the type of the base class identifier -an error
                if (inheritedClass.getId() != null &&
                    !Objects.equals(inheritedClass.getId(), modelClass.getId())) {
                    throw new ExtendedClassChangeIdInInheritedClassException(inheritedClass.getName());
                }

                inheritedClass.setId(modelClass.getId());
            });
        });

        baseModelClasses
            .stream()
            .filter(xmlModelClass -> xmlModelClass.getClassAccess() != Changeable.UPDATE)
            .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getId()))
            .peek(modelClass -> {
                if (modelClass.getName().endsWith("ApiCall")) {
                    modelClass.getId().setIdCategory(IdCategory.MANUAL);
                }
            })
            .filter(xmlModelClass -> Objects.nonNull(xmlModelClass.getId().getIdCategory()))
            .filter(xmlModelClass -> xmlModelClass.getId().getIdCategory().isGeneratedAlways())
            .forEach(xmlModelClass -> {

                final IdCategory idCategory =
                    ModelHelper
                        .getAllPropertiesWithInherited(xmlModelClass)
                        .stream()
                        .filter(xmlModelClassProperty -> Boolean.TRUE.equals(xmlModelClassProperty.isParent()))
                        .findFirst()
                        .map(parentProperty -> model.getClassNullable(parentProperty.getType()))
                        .map(parentClass -> parentClass.getId().getIdCategory())
                        .map(parentCategory ->
                            switch (parentCategory) {
                                case UUIDV4, UUIDV4_ON_EMPTY -> IdCategory.UUIDV4;
                                case SNOWFLAKE, AUTO_ON_EMPTY -> IdCategory.SNOWFLAKE;
                                default -> model.getAutoIdMethod();
                            }
                        )
                        .orElse(model.getAutoIdMethod());

                xmlModelClass.getId().setIdCategory(idCategory);

            });

    }

    @Override
    public List<String> addInterfacesToJpaModel(XmlModelClass modelClass) {
        List<String> interfaces = new ArrayList<>();

        if (modelClass.isBaseClassMark()) {
            interfaces.add(IdEntity.class.getSimpleName() + "<" + modelClass.getId().getType() + ">");

            if (useIdGenerator(modelClass)) {
                interfaces.add(GeneratorIdExternalValueInitializer.class.getSimpleName());
            }

            if (modelClass.isUseIdPrefix()) {
                interfaces.add(GeneratorIdPrefix.class.getSimpleName());
            }

        } else if (Objects.equals(modelClass.getName(), JpaConstants.ENTITY_CLASS_NAME)) {
            interfaces.add(Serializable.class.getSimpleName());
            interfaces.add(Entity.class.getName());
        }

        return interfaces;
    }

    @Override
    public List<String> addImports(XmlModelClass modelClass) {
        List<String> imports = new ArrayList<>();

        if (modelClass.isBaseClassMark()) {
            imports.add(IdEntity.class.getName());
            imports.add(Objects.class.getName());

            if (useIdGenerator(modelClass)) {
                imports.add(GeneratorIdExternalValueInitializer.class.getName());
            }

            boolean isEmbeddedId = modelClass.getPropertyWithHierarchyInSingleTable(JpaConstants.OBJECT_ID).isEmbedded();

            if (useIdGenerator(modelClass) && isEmbeddedId) {
                throw new SnowflakeOnEmbeddableKeyNotSupportedException(modelClass.getName());
            }

            if (modelClass.isUseIdPrefix()) {
                imports.add(GeneratorIdPrefix.class.getName());
            }

        } else if (Objects.equals(modelClass.getName(), JpaConstants.ENTITY_CLASS_NAME)) {
            imports.addAll(Arrays.asList(
                "java.io.Serializable",
                "java.util.List",
                "java.util.ArrayList",
                "javax.persistence.TemporalType",
                "javax.persistence.Temporal",
                "java.time.ZoneOffset",
                "javax.persistence.Transient",
                "java.text.DateFormat",
                "java.text.SimpleDateFormat",
                "java.time.LocalDateTime",
                "java.time.LocalDate",
                "java.time.OffsetDateTime",
                "java.time.format.DateTimeFormatter",
                "java.time.format.DateTimeParseException",
                "java.text.ParseException",
                "javax.persistence.Version"));
        }
        imports.add("java.util.Date");

        return imports;
    }

    @Override
    public List<String> addPropertyAnnotations(XmlModelClassProperty property) {
        XmlModelClass modelClass = property.getModelClass();
        List<String> annotations = new ArrayList<>();

        if (modelClass.isBaseClassMark()) {
            if (JpaConstants.OBJECT_ID.equals(property.getName())) {
                if (property.isEmbedded()) {
                    annotations.add("@javax.persistence.EmbeddedId");
                } else {
                    annotations.add("@javax.persistence.Id");
                }

                if (useIdGenerator(modelClass)) {
                    annotations.add("@javax.persistence.GeneratedValue(generator = \"snowflake-generator\")");

                    StringBuilder builder = new StringBuilder()
                        .append("@org.hibernate.annotations.GenericGenerator(")
                        .append("name = \"snowflake-generator\"")
                        .append(", ")
                        .append("strategy = \"sbp.sbt.model.config.snowflake.SnowflakeIdGenerator\"");

                    final Map<String, String> parameters = generatorParameters(modelClass);

                    if (parameters.size() > 0) {

                        final String parametersString = parameters
                            .entrySet()
                            .stream()
                            .map(entry -> "@org.hibernate.annotations.Parameter(name = \"" +
                                entry.getKey() +
                                "\", value = \"" +
                                entry.getValue() +
                                "\")")
                            .collect(Collectors.joining(", "));

                        builder
                            .append(", ")
                            .append("parameters = ")
                            .append(parameters.size() == 1 ? "" : "{")
                            .append(parametersString)
                            .append(parameters.size() == 1 ? "" : "}");

                    }

                    builder.append(")");

                    annotations.add(builder.toString());
                }

                if (Objects.equals(Changeable.UPDATE, modelClass.getClassAccess()) &&
                    modelClass.getId().getIdCategory() == IdCategory.NO_ID) {
                    throw new NoIdNotSupportedForBaseEntityException(modelClass.getName());
                }
            }
        } else if (Objects.equals(property.getModelClass().getName(), JpaConstants.ENTITY_CLASS_NAME)) {
            if (LAST_CHANGE_DATE.equals(property.getName())) {
                annotations.add("@Temporal(TemporalType.TIMESTAMP)");
            } else if (CHG_CNT_ID.equals(property.getName())
                && property.getModelClass().getModel().isVersionedEntities()) {
                annotations.add("@Version");
            }
        }

        return annotations;
    }

    @Override
    public String addProperty(XmlModelClass modelClass) {
        if (Objects.equals(modelClass.getName(), JpaConstants.ENTITY_CLASS_NAME)) {
            return DATE_FORMAT_TEMPLATE;
        }

        return "";
    }

    @Override
    public String addMethod(XmlModelClass modelClass) {
        if (Objects.equals(modelClass.getName(), JpaConstants.ENTITY_CLASS_NAME)) {
            return ENTITY_PROPERTIES_TEMPLATE;
        }

        if (!modelClass.isBaseClassMark()) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(
            ADDITIONAL_PROPERTIES_TEMPLATE
                .replace("${class}", modelClass.getName()));

        if (useIdGenerator(modelClass)) {
            stringBuilder.append(GENERATOR_ID_EXTERNAL_VALUE_INITIALIZER_PROPERTIES_TEMPLATE);
        }

        return stringBuilder.toString();
    }

    private Map<String, String> generatorParameters(XmlModelClass modelClass) {

        final Map<String, String> parameters = new LinkedHashMap<>();

        switch (modelClass.getId().getIdCategory()) {
            case MANUAL:
                parameters.put(
                    GENERATOR_REQUIRED_VALUE_PARAMETER_NAME,
                    "true"
                );
                break;
            case UUIDV4:
            case UUIDV4_ON_EMPTY:
                parameters.put(
                    GENERATOR_KIND_PARAMETER_NAME,
                    GeneratorConstants.Kind.UUID_V4.getParameterValue()
                );
                break;
            default:
                break;
        }

        return parameters;

    }

}
