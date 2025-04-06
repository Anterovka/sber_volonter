package com.sbt.reference;

import com.sbt.aggregator.AggregateController;
import com.sbt.aggregator.AggregateGenerator;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.CollectionType;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlIndex;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.id.XmlId;
import com.sbt.mg.data.model.interfaces.ReferenceFromXml;
import com.sbt.mg.exception.checkmodel.PropertyNameAlreadyDefinedException;
import com.sbt.mg.exception.checkmodel.PropertyNameAlreadyExistsException;
import com.sbt.mg.exception.common.AggregateGeneratorNotFoundException;
import com.sbt.parameters.enums.Changeable;
import com.sbt.parameters.enums.IdCategory;
import com.sbt.reference.exception.ReferenceToAbstractCLassException;
import com.sbt.reference.exception.ReferenceToEmbeddableCLassException;
import com.sbt.reference.exception.ReferenceToInterfaceException;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.ELEMENT_REFERENCE;

public class ExternalReferenceGenerator implements ModelGenerate {

    public static final String PROJECT_NAME = "REFERENCE";
    public static final String ENTITY_ID = "entityId";
    public static final String ROOT_ENTITY_ID = "rootEntityId";
    public static final String BACK_REFERENCE = "backReference";
    public static final String REFERENCE = "reference";

    private static final String TEMPORAL_METHODS = getTemplate("/generate/temporalMethods.template");
    private static final String EQUAL_HASH_CODE_METHODS = getTemplate("/generate/equalHashCode.template");
    private static final String TEMPORAL_ROOT_METHODS = getTemplate("/generate/temporalRootMethods.template");
    private static final String EQUAL_HASH_CODE_ROOT_METHODS = getTemplate("/generate/equalHashCodeRoot.template");

    private final Map<String, ReferenceDescription> referencesName = new HashMap<>();
    private final Map<String, XmlModelClass> referencesEntityName = new HashMap<>();

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
        return 115;
    }

    @Override
    public void initModel(XmlModel model, File file, ModelParameters modelParameters) {
        AggregateGenerator aggregateGenerator = (AggregateGenerator) modelParameters.getExecutingModelGenerate().stream()
                .filter(modelGenerate -> modelGenerate instanceof AggregateGenerator)
                .findAny()
.orElseThrow(() -> new AggregateGeneratorNotFoundException("functionality work " + PROJECT_NAME));

        model.getClassesAsList()
                .forEach(modelClass -> {
                    XmlModelClass aggregateParent = aggregateGenerator.getAggregateController().getRootAggregate(modelClass);
                    if (aggregateParent != null) {
                        modelClass.setRootType(aggregateParent.getName());
                    }

                    modelClass.getReferencesAsList().stream()
                            .filter(reference -> model.getInterfaceNullable(reference.getType()) != null)
                            .findFirst()
                            .ifPresent(reference -> {
                                throw new ReferenceToInterfaceException(reference.getModelClass(), reference);
                            });

                    modelClass.getReferencesAsList().stream()
                            //For all links (both collectible and non-collectible), an embeddable type should be created to represent them.
                            // this link
                            .peek(reference -> {
                                String referenceTypeName = getReferenceTypeName(reference);

                                ReferenceDescription referenceDescription = new ReferenceDescription();
                                referenceDescription.setClassOwner(modelClass);
                                referenceDescription.setOriginalType(reference.getType());
                                referenceDescription.setReferenceClassName(referenceTypeName);
                                XmlModelClass xmlModelClass = modelClass.getModel().getClassNullable(reference.getType());

                                if (xmlModelClass == null) {
                                    referenceDescription.setReferenceType(String.class.getSimpleName());
                                } else {
                                    if (xmlModelClass.isAbstract()) {
                                        throw new ReferenceToAbstractCLassException(xmlModelClass, reference);
                                    }
                                    if (xmlModelClass.isEmbeddable()) {
                                        throw new ReferenceToEmbeddableCLassException(xmlModelClass, reference);
                                    }
                                    referenceDescription.setEntityClass(xmlModelClass);
                                }

                                referencesName.put(referenceTypeName, referenceDescription);
                            })
                            //For non-collection properties, we create an XmlModelClassProperty and add it to the model.
                            // Collection properties are further processed because they will have a different type.
                            .filter(reference -> reference.getCollectionType() == null)
                            .map(reference -> {
                                String referenceTypeName = getReferenceTypeName(reference);
                                boolean isExternalSoftReference = !model.findClass(reference.getType()).isPresent();
                                XmlModelClassProperty property = XmlModelClassProperty.Builder.create()
                                        .setName(reference.getName())
                                        .setLabel(reference.getLabel())
                                        .setDescription(reference.getDescription())
                                        .setChangeable(Changeable.READ_ONLY)
                                        .setType(referenceTypeName)
                                        .setExternalLink(Boolean.TRUE)
                                        .setCollectionType(reference.getCollectionType())
                                        .setUnique(reference.getUnique())
                                        .setCciIndex(reference.isCciIndex())
                                        .setCciName(reference.getCciIndexName())
                                        .setDeprecated(reference.isDeprecated())
                                        .setHistorical(reference.isHistorical())
                                        .setVersionDeprecated(reference.getVersionDeprecated())
                                        .setIntegrityCheck(reference.isIntegrityCheck())
                                        .setExternalSoftReference(isExternalSoftReference)
                                        .setOriginalType(reference.getType())
                                        .build();

                                if (!isIndexAlreadyExist(aggregateGenerator, modelClass, reference, isExternalSoftReference)) {
                                    property.setIndex(Boolean.TRUE);
                                }

                                return property;
                            })
                            .forEach(xmlModelClassProperty -> {
                                //Currently, the created property has not been added to the class, we check that it is not occupied.
                                //Уже есть такое имя
                                if (modelClass.containsProperty(xmlModelClassProperty.getName())) {
                                    throw new PropertyNameAlreadyExistsException(xmlModelClassProperty, modelClass);
                                }

                                modelClass.addProperty(xmlModelClassProperty);
                            });
                });

        referencesName.forEach((type, desc) ->
                createEmbeddableReferenceClass(type, desc, model, aggregateGenerator));

        model.getClassesAsList().stream()
                .flatMap(modelClass -> modelClass.getReferencesAsList().stream())
                .filter(reference -> reference.getCollectionType() != null)
                .forEach(reference -> {
                    XmlModelClass modelClass = reference.getModelClass();
                    ReferenceGenerateInfo referenceGenerateInfo = generateCollectionTypeName(modelParameters, reference);
                    String referenceTypeName = getReferenceTypeName(reference);
                    XmlModelClass xmlModelClass = new XmlModelClass();
                    xmlModelClass.setName(referenceGenerateInfo.getCollectionTypeName());
                    xmlModelClass.setLabel("Class for external reference " + referenceTypeName);
                    xmlModelClass.setFinalClass(Boolean.TRUE);
                    xmlModelClass.setExternalReference(Boolean.TRUE);
                    xmlModelClass.setClassAccess(Changeable.READ_ONLY);
                    xmlModelClass.setDeprecated(reference.isDeprecated());
                    xmlModelClass.setVersionDeprecated(reference.getVersionDeprecated());
                    xmlModelClass.setId(new XmlId(String.class.getSimpleName(), IdCategory.SNOWFLAKE));
                    xmlModelClass.addProperties(
                                    XmlModelClassProperty.Builder.create()
                                            .setName(BACK_REFERENCE)
                                            .setParent()
                                            .setType(modelClass.getName())
                                            .setLabel("Backlink to the aggregate owner")
                                            .build(),
                                    XmlModelClassProperty.Builder.create()
                                            .setType(referenceTypeName)
                                            .setName(REFERENCE)
                                            .setExternalLink(Boolean.TRUE)
                                            .setLabel("attribute of external reference")
                                            .setOriginalType(reference.getType())
                                            .build()
                            )
                            .setOriginalType(reference.getType());
                    model.addClassWithoutCheck(xmlModelClass);

                    ModelHelper.addComplexIndexToClass(
                            xmlModelClass,
                            true,
                            false,
                            Arrays.asList(xmlModelClass.getPropertyWithHierarchyInSingleTable(BACK_REFERENCE),
                                    xmlModelClass.getPropertyWithHierarchyInSingleTable(REFERENCE)),
                            false
                    );

                    referencesEntityName.put(referenceGenerateInfo.getCollectionTypeName(), xmlModelClass);

                    XmlModelClassProperty property = XmlModelClassProperty.Builder.create()
                            .setName(reference.getName())
                            .setCollectionType(CollectionType.SET)
                            .setType(referenceGenerateInfo.getCollectionTypeName())
                            .setLabel(reference.getLabel())
                            .setMappedBy(BACK_REFERENCE)
                            .setIntegrityCheck(reference.isIntegrityCheck())
                            .setDeprecated(reference.isDeprecated())
                            .setVersionDeprecated(reference.getVersionDeprecated())
                            .setExternalLink(Boolean.TRUE)
                            .setExternalSoftReference(!model.findClass(reference.getType()).isPresent())
                            .setOriginalType(reference.getType())
                            .setReferenceGenerateStrategy(referenceGenerateInfo.getReferenceGenerateStrategy())
                            .build();

                    if (modelClass.containsProperty(reference.getName())) {
                        throw new PropertyNameAlreadyDefinedException(property, modelClass.getName());
                    }

                    modelClass.addProperty(property);
                });
    }

    private XmlModelClassProperty getReferenceFromPdm(ModelParameters modelParameters, XmlModelClassReference reference) {
        XmlModelClassProperty oldReference = null;
        PdmModel pdmModel = modelParameters.getPdmModel();
        if (pdmModel != null) {
            String referenceModelClassName = reference.getModelClass().getName();
            String referenceName = reference.getName();

            oldReference = pdmModel.getModel().getClass(referenceModelClassName).getProperty(referenceName);
        }
        return oldReference;
    }

    private ReferenceGenerateInfo generateCollectionTypeName(ModelParameters modelParameters, XmlModelClassReference reference) {
        if (reference.isExistsInPdm()) {
            XmlModelClassProperty oldReference = getReferenceFromPdm(modelParameters, reference);
            // TODO I would completely abandon the strategy
            return new ReferenceGenerateInfo(oldReference.getType(), oldReference.getReferenceGenerateStrategy());
        }
        return new ReferenceGenerateInfo(makeReferenceCollectionTypeName(reference), null);
    }

    private boolean isIndexAlreadyExist(AggregateGenerator aggregateGenerator, XmlModelClass modelClass, XmlModelClassReference reference, boolean isExternalSoftReference) {
        return modelClass.getIndices().stream()
                .anyMatch(index ->
                        isIndexContainReference(reference, isExternalSoftReference, index, aggregateGenerator)
                );
    }

    private static boolean isIndexContainReference(XmlModelClassReference reference,
                                                   boolean isExternalSoftReference,
                                                   XmlIndex index,
                                                   AggregateGenerator aggregateGenerator) {
        final boolean isRootAggregate = isRootAggregate(reference, isExternalSoftReference, aggregateGenerator);

        if (isExternalSoftReference || isRootAggregate) {
            String propertyName = index.getProperties().get(0).getName();
            return propertyName.equals(reference.getName()) ||
                    propertyName.equals(String.format("%s.%s", reference.getName(), ENTITY_ID));
        } else {
            return Objects.equals(index.getProperties().get(0).getName(), reference.getName()) ||
                    index.getProperties().get(0).getName().equals(String.format("%s.%s", reference.getName(), ENTITY_ID)) &&
                            index.getProperties().get(1).getName().equals(String.format("%s.%s", reference.getName(), ROOT_ENTITY_ID));
        }
    }

    private static boolean isRootAggregate(XmlModelClassReference reference, boolean isExternalSoftReference, AggregateGenerator aggregateGenerator) {
        final boolean isRootAggregate;
        if (!isExternalSoftReference) {
            XmlModelClass modelClass = reference.getModelClass().getModel().getClass(reference.getType());
            if (modelClass.isDictionary()) {
                return false;
            }
            XmlModelClass rootAggregate = aggregateGenerator
                    .getAggregateController()
                    .getRootAggregate(modelClass);
            isRootAggregate = Objects.equals(rootAggregate.getName(), reference.getType());
        } else {
            isRootAggregate = false;
        }
        return isRootAggregate;
    }

    private void createEmbeddableReferenceClass(String type, ReferenceDescription desc, XmlModel model, AggregateGenerator aggregateGenerator) {
        XmlModelClass modelClass = new XmlModelClass();
        modelClass.setName(type);
        modelClass.setEmbeddable(Boolean.TRUE);
        modelClass.setLabel("External link " + type);
        modelClass.setFinalClass(Boolean.TRUE);
        modelClass.setUseAffinity(false);
        modelClass.setClassAccess(Changeable.READ_ONLY);
        modelClass.setId(new XmlId(String.class.getSimpleName(), IdCategory.NO_ID));
        modelClass.setOriginalType(desc.getOriginalType());
        modelClass.setImportModelName(desc.getEntityClass() == null ? null : desc.getEntityClass().getImportModelName());

        model.addClassWithoutCheck(modelClass);

        if (Objects.nonNull(desc.getEntityClass())) {
            modelClass.setDeprecated(Boolean.TRUE.equals(desc.getEntityClass().isDeprecated()));
        }

        modelClass.addProperty(XmlModelClassProperty.Builder.create()
                .setName(ENTITY_ID)
                .setType(String.class.getSimpleName())
                .setLabel("External object Id")
                .build());

        AggregateController aggregateController = aggregateGenerator.getAggregateController();

        XmlModelClass rootAggregate = aggregateController.getRootAggregate(desc.getEntityClass());
        if (rootAggregate != null
                && ModelHelper.getAllSuperClasses(desc.getEntityClass(), true)
                .stream().noneMatch(rootAggregate::equals)) {
            desc.setRootEntityClass(rootAggregate);

            modelClass.addProperty(XmlModelClassProperty.Builder.create()
                    .setName(ROOT_ENTITY_ID)
                    .setType(String.class.getSimpleName())
                    .setLabel("Root aggregate external object Id")
                    .build());
        }
    }

    private String getReferenceTypeName(ReferenceFromXml reference) {
        return ModelHelper.transformClassToReferenceClass(reference.getType());
    }

    private String makeReferenceCollectionTypeName(XmlModelClassReference reference) {
        return "Rci" + reference.getModelClass().getName() + StringUtils.capitalize(reference.getName());
    }

    @Override
    public List<String> addInterfacesToJpaModel(XmlModelClass modelClass) {
        ReferenceDescription realType = referencesName.get(modelClass.getName());
        if (realType == null) {
            XmlModelClass collectionTable = referencesEntityName.get(modelClass.getName());

            // The second condition is self-sufficient, but I left the first condition for backup, since I am not sure that
            // the isExternalReference flag was always set
            if (modelClass.getName().endsWith(ELEMENT_REFERENCE) || modelClass.isExternalReference()) {
                return Collections.singletonList(SoftReferenceEntity.class.getSimpleName()
                        + "<" + collectionTable.getPropertyWithHierarchyInSingleTable(REFERENCE).getType()
                        + ", " + collectionTable.getPropertyWithHierarchyInSingleTable(BACK_REFERENCE).getType() + ">");
            }

            return Collections.emptyList();
        }

        if (realType.getEntityClass() != null) {
            if (realType.getRootEntityClass() != null) {
                return Collections.singletonList(ComplexReference.class.getSimpleName()
                        + "<" + realType.getRootEntityClass().getName()
                        + ", " + realType.getEntityClass().getName() + ">");
            }

            return Collections.singletonList(SoftReference.class.getSimpleName()
                    + "<" + realType.getEntityClass().getName() + ">");
        } else {
            return Collections.singletonList(SoftReference.class.getSimpleName()
                    + "<" + realType.getReferenceType() + ">");
        }
    }

    @Override
    public List<String> addImports(XmlModelClass modelClass) {
        XmlModelClass xmlModelClass = referencesEntityName.get(modelClass.getName());

        if (xmlModelClass == null && !referencesName.containsKey(modelClass.getName())) {
            return Collections.emptyList();
        }

        List<String> stringList = new ArrayList<>();
        stringList.add(ComplexReference.class.getName());
        stringList.add(SoftReference.class.getName());
        stringList.add(Objects.class.getName());

        if (xmlModelClass != null) {
            stringList.add(SoftReferenceEntity.class.getName());
        }

        return stringList;
    }

    @Override
    public String addMethod(XmlModelClass modelClass) {
        ReferenceDescription realType = referencesName.get(modelClass.getName());
        if (realType == null) {
            return "";
        }

        StringBuilder referenceAdditionalMethods = new StringBuilder();

        if (realType.getEntityClass() != null) {
            referenceAdditionalMethods.append(TEMPORAL_METHODS
                    .replace("${className}", realType.getEntityClass().getName()));

            if (realType.getRootEntityClass() != null) {
                referenceAdditionalMethods.append(TEMPORAL_ROOT_METHODS
                        .replace("${className}", realType.getRootEntityClass().getName()));
                referenceAdditionalMethods.append(EQUAL_HASH_CODE_ROOT_METHODS
                        .replace("${thisClass}", modelClass.getName()));
            } else {
                referenceAdditionalMethods.append(EQUAL_HASH_CODE_METHODS
                        .replace("${thisClass}", modelClass.getName()));
            }
        } else {
            referenceAdditionalMethods.append(TEMPORAL_METHODS
                    .replace("${className}", realType.getReferenceType()));

            referenceAdditionalMethods.append(EQUAL_HASH_CODE_METHODS
                    .replace("${thisClass}", modelClass.getName()));
        }

        return referenceAdditionalMethods.toString();
    }
}
