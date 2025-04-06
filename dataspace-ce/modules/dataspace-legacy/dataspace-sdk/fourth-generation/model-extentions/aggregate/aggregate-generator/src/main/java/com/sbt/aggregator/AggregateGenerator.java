package com.sbt.aggregator;

import com.sbt.aggregator.exception.AbstractClassInControlException;
import com.sbt.aggregator.exception.AddedParentPropertyToExistedClassException;
import com.sbt.aggregator.exception.AggregateCircleException;
import com.sbt.aggregator.exception.LinkBetweenAggregatesEmbeddableException;
import com.sbt.aggregator.exception.LinkBetweenAggregatesException;
import com.sbt.aggregator.exception.ManualAffinityInitialisingException;
import com.sbt.aggregator.exception.MarkedAsMappedByWithoutParentPropertyException;
import com.sbt.aggregator.exception.NoMappedByPropertyToParentException;
import com.sbt.aggregator.exception.ParentDiffPropertiesException;
import com.sbt.aggregator.exception.ParentIsCollectionException;
import com.sbt.aggregator.exception.ParentLinkToInheritedClassException;
import com.sbt.aggregator.exception.ParentNotOnBaseClassException;
import com.sbt.aggregator.exception.ParentPropertyOutOfModelException;
import com.sbt.aggregator.exception.ParentWithoutMappedByException;
import com.sbt.aggregator.exception.ReferenceCollectionInAbstractClassException;
import com.sbt.aggregator.exception.RemovedParentClassException;
import com.sbt.aggregator.exception.RootParentClassCircleException;
import com.sbt.aggregator.exception.TooMuchParentPropertiesException;
import com.sbt.aggregator.exception.TwoParentOnDifferentTypeException;
import com.sbt.aggregator.exception.TwoParentOnOwnerClassException;
import com.sbt.dataspace.pdm.ModelGenerate;
import com.sbt.dataspace.pdm.ModelParameters;
import com.sbt.dataspace.pdm.PdmModel;
import com.sbt.dataspace.pdm.PluginParameters;
import com.sbt.jpa.annotation.Aggregate;
import com.sbt.jpa.annotation.BaseAggregateEntity;
import com.sbt.mg.ElementState;
import com.sbt.mg.ModelHelper;
import com.sbt.mg.data.model.XmlImport;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.data.model.XmlModelClassProperty;
import com.sbt.mg.data.model.XmlModelClassReference;
import com.sbt.mg.data.model.usermodel.UserXmlModelClass;
import com.sbt.mg.exception.checkmodel.UseIdPrefixPositionException;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.sbt.mg.Helper.getTemplate;
import static com.sbt.mg.ModelHelper.isRootDictionary;
import static com.sbt.mg.jpa.JpaConstants.ROOT_DICTIONARY_CLASS_NAME;

public class AggregateGenerator implements ModelGenerate {

    private XmlModel prevModel;

    private static final Logger LOGGER = Logger.getLogger(AggregateGenerator.class.getName());
    private static final String AGGREGATE_ANNOTATION_TEMPLATE = getTemplate("/templates/aggregateAnnotation.template");

    private static final String VALUE = "${value}";
    private static final String MANAGED_CLASS = "${managedClass}";
    private static final String FROM = "${from}";
    private static final String AFFINITY = "${affinity}";
    private static final String AGGREGATE = "${aggregate}";

    public static final String PROJECT_NAME = "AggregateModelGenerator";

    private AggregateController aggregateController = new AggregateController();

    private boolean validateAggregateLinks;
    //    private boolean isIntermediateBuild;
    private boolean disableCompatibilityCheck;
    private boolean disableAggregateRootReferenceCheck;


    @Override
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @Override
    public int getPriority() {
        return 110;
    }

    @Override
    public void preInit(XmlModel model, PluginParameters pluginParameters) {
        model.getImports().add(new XmlImport(PROJECT_NAME, ""));

        setValidateAggregateLink(pluginParameters.getAggregateValidations());

//        this.isIntermediateBuild = pluginParameters.isIntermediaryBuild();
        this.disableCompatibilityCheck = pluginParameters.isIntermediaryBuild() || pluginParameters.isDisableCompatibilityCheck();
        this.disableAggregateRootReferenceCheck = pluginParameters.isDisableAggregateRootReferenceCheck();
    }

    private void setValidateAggregateLink(boolean validateAggregateLinks) {
        this.validateAggregateLinks = validateAggregateLinks;
    }

    @Override
    public void initModel(XmlModel model, File file, ModelParameters modelParameters) {
        model.getClassesAsList().stream()
            .filter(UserXmlModelClass::isEmbeddable)
            .forEach(modelClass -> modelClass.setUseAffinity(false));

        model.getClassesAsList().forEach(this::checkParentProperty);

        this.prevModel = modelParameters.getPdmModel().getModel();

        aggregateInitializer(model);

        aggregateController.getAggregatesRoots().stream()
            .filter(this::isFirstParent)
            .forEach(aggregateRoot ->
                aggregateRoot.addProperty(
                    XmlModelClassProperty.Builder.create()
                        .setName("sys_ver")
                        .setType("long")
                        .setLabel("Technical Change Counter")
                        .setChangeable(Changeable.READ_ONLY)
                        .build()
                )
            );
    }

    private boolean isFirstParent(XmlModelClass modelClass) {
        XmlModelClass extendedClass = modelClass.getExtendedClass();
        while (extendedClass != null) {
            if (!extendedClass.isAbstract()) {
                return false;
            }
            extendedClass = extendedClass.getExtendedClass();
        }
        return true;
    }

    private void aggregateInitializer(XmlModel model) {
        model.getClassesAsList().forEach(this::checkNoParentClassToItself);
        model.getClassesAsList().stream().filter(XmlModelClass::isAbstract).forEach(this::checkCollectReferenceInAbstract);

        createAggregateTree(model);
        squeezeAggregate(model);
        markAllParentPropertiesAsMandatory(model);
        model.getClassesAsList().forEach(this::checkAndCreateCorrectParentLinks);
        model.getClassesAsList().forEach(this::checkNoLinkToChildClass);

        if (validateAggregateLinks) {
            checkLinksBetweenAggregates(model);
        }

        model.getClassesAsList()
            .stream()
            .filter(XmlModelClass::isUseIdPrefix)
            .forEach(cls -> {
                XmlModelClass rootAggregate = aggregateController.getRootAggregate(cls);

                if (Objects.isNull(rootAggregate)
                    || !cls.getName().equals(rootAggregate.getName())
                    || cls.isAbstract()
                    || cls.isDictionary()) {
                    throw new UseIdPrefixPositionException(cls);
                }
            });
    }

    private void checkCollectReferenceInAbstract(XmlModelClass xmlModelClass) {
        Optional<XmlModelClassReference> first = xmlModelClass.getReferencesAsList().stream()
            .filter(xmlModelClassReference -> xmlModelClassReference.getCollectionType() != null)
            .findFirst();
        if (first.isPresent()) {
            throw new ReferenceCollectionInAbstractClassException(first.get());
        }
    }

    private void checkNoLinkToChildClass(XmlModelClass modelClass) {
        if (!modelClass.isBaseClassMark()) {
            return;
        }
        XmlModelClass rootAggregate = aggregateController.getRootAggregate(modelClass);

        if (!Objects.equals(rootAggregate, modelClass)) {
            return;
        }

        Set<XmlModelClass> allInheritedClasses;
        if (disableAggregateRootReferenceCheck) {
            allInheritedClasses = ModelHelper.getAllChildClasses(modelClass);

            List<XmlModelClassProperty> propertyWithTypeToItself = modelClass.getPropertiesAsList()
                .stream()
                .filter(property -> allInheritedClasses.stream()
                    .anyMatch(extendedClass -> extendedClass.getName().equals(property.getType())))
                .collect(Collectors.toList());

            if (!propertyWithTypeToItself.isEmpty() && !isLegacyAggregateReference(propertyWithTypeToItself)) {
                throw new ParentLinkToInheritedClassException(modelClass.getName(), propertyWithTypeToItself);
            }
        } else {
            allInheritedClasses = ModelHelper.getAllChildClasses(modelClass, true);
        }

        Set<String> extendedClasses = allInheritedClasses.stream().map(XmlModelClass::getName).collect(Collectors.toSet());

        allInheritedClasses.forEach(extendedClass -> {
            List<XmlModelClassProperty> circleLinks = extendedClass.getPropertiesAsList()
                .stream().filter(property -> extendedClasses.contains(property.getType()))
                .collect(Collectors.toList());

            if (!circleLinks.isEmpty() && !isLegacyAggregateReference(circleLinks) && !extendedClass.isDictionary()) {
                throw new ParentLinkToInheritedClassException(extendedClass.getName(), circleLinks);
            }
        });
    }

    /**
     * Analyze if there was an error situation in the previous model where the aggregate root could refer to itself.
     *
     * @param propertyWithTypeToItself properties of the current model that have a reference (not soft) to another aggregate.
     * @return whether all properties were already in the pdm.
     */
    private boolean isLegacyAggregateReference(List<XmlModelClassProperty> propertyWithTypeToItself) {
        if (this.disableAggregateRootReferenceCheck) {
            return true;
        }
        if (this.prevModel == null) {
            return false;
        }
        for (XmlModelClassProperty newProperty : propertyWithTypeToItself) {
            final String newClassName = newProperty.getModelClass().getName();
            final XmlModelClass prevClass = prevModel.getClassNullable(newClassName);
            if (prevClass == null) {
                return false;
            }
            final XmlModelClassProperty prevProperty = prevClass.getPropertyNullable(newProperty.getName());
            if (prevProperty == null) {
                return false;
            }
        }
        return true;
    }

    private void checkMappedByWithParent(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(modelClass -> !modelClass.isEvent())
            .forEach(modelClass -> {
                List<XmlModelClassProperty> properties = parentClassProperties(modelClass);

                Optional<XmlModelClassProperty> parentWithoutMappedBy = properties
                    .stream()
                    .filter(property -> modelClass.getModel().getClass(property.getType())
                        .getPropertiesAsList()
                        .stream()
                        .noneMatch(mappedProperty -> mappedProperty.getMappedBy() != null
                            && mappedProperty.getMappedBy().equals(property.getName())
                            && mappedProperty.getType().equals(modelClass.getName()))
                    )
                    .findFirst();

                if (parentWithoutMappedBy.isPresent()) {
                    XmlModelClassProperty property = parentWithoutMappedBy.get();
                    throw new ParentWithoutMappedByException(property);
                }
            });
    }

    @Override
    public void postInitModel(XmlModel model, File file, ModelParameters modelParameters) {
        LOGGER.info("\n╔═══╗╔═══╗╔═══╗╔═══╗╔═══╗╔═══╗╔═══╗╔════╗╔═══╗\n" +
            "║╔═╗║║╔═╗║║╔═╗║║╔═╗║║╔══╝║╔═╗║║╔═╗║║╔╗╔╗║║╔══╝\n" +
            "║║─║║║║─╚╝║║─╚╝║╚═╝║║╚══╗║║─╚╝║║─║║╚╝║║╚╝║╚══╗\n" +
            "║╚═╝║║║╔═╗║║╔═╗║╔╗╔╝║╔══╝║║╔═╗║╚═╝║──║║──║╔══╝\n" +
            "║╔═╗║║╚╩═║║╚╩═║║║║╚╗║╚══╗║╚╩═║║╔═╗║──║║──║╚══╗\n" +
            "╚╝─╚╝╚═══╝╚═══╝╚╝╚═╝╚═══╝╚═══╝╚╝─╚╝──╚╝──╚═══╝");

        aggregateController = new AggregateController();

        model.getClassesAsList().stream()
            .filter(XmlModelClass::isEmbeddable)
            .forEach(modelClass -> modelClass.setUseAffinity(false));


        model.getClassesAsList().forEach(this::checkParentProperty);
        model.getClassesAsList().forEach(this::checkAndCreateCorrectParentLinks);
        model.getClassesAsList().forEach(modelClass -> {
            if (modelClass.getAffinity() != null) {
                throw new ManualAffinityInitialisingException(modelClass.getName());
            }
        });

        model.getClassesAsList().forEach(this::initAffinityFields);

        checkMappedByWithParent(model);

        aggregateInitializer(model);
        addAggregateRootPropertyIntoClasses(model);


        aggregateController.printAggregates(model);
    }

    private void checkNoParentClassToItself(XmlModelClass modelClass) {
        if (modelClass.isDictionary()) {
            return;
        }
        List<XmlModelClassProperty> parentProperties = modelClass.getPropertiesAsList().stream()
            .filter(XmlModelClassProperty::isParent).collect(Collectors.toList());

        if (parentProperties.size() != 1) {
            return;
        }

        XmlModelClassProperty parentProperty = parentProperties.get(0);
        if (modelClass.getName().equals(parentProperty.getType())) {
            throw new RootParentClassCircleException(modelClass.getName(), parentProperty.getName());
        }
    }

    private void squeezeAggregate(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getExtendedClassName() == null && !modelClass.isEmbeddable())
            .forEach(modelClass -> squeezeAggregate(model, modelClass));
    }

    private void squeezeAggregate(XmlModel model, XmlModelClass modelClass) {
        model.getClassesAsList().stream()
            .filter(aggregateClass -> modelClass.getName().equals(aggregateClass.getExtendedClassName()))
            .forEach(aggregateClass -> {
                AggregateTree aggregateTree = aggregateController.classToAggregateTree(aggregateClass);
                if (aggregateTree != null) {
                    AggregateTree controlAggregateCLass = aggregateController.classToAggregateTree(modelClass);

                    if (controlAggregateCLass != null && !controlAggregateCLass.getXmlModelClassValue().isAbstract()) {
                        if (aggregateTree.getSubAggregates().isEmpty()) {
                            aggregateController.setAggregateTreeForClass(aggregateClass, controlAggregateCLass);
                        } else {
                            // transfer all properties from parent control
                            aggregateTree.getSubAggregates().addAll(controlAggregateCLass.getSubAggregates());
// наследуем класс без указания родителя, чтобы он мог существовать самостоятельно
                            AggregateTree controlAggregateCLassParent = controlAggregateCLass.getParent();

                            if (controlAggregateCLassParent != null) {
                                controlAggregateCLassParent.addSubAggregate(aggregateTree);
                            }
                            //Transferring the base property
                            String affinity = controlAggregateCLass.getXmlModelClassValue().getAffinity();
                            if (affinity != null) {
                                aggregateTree.getXmlModelClassValue().setAffinity(affinity);
                            }
                        }
                    }

                    squeezeAggregate(model, aggregateClass);
                }
            });
    }

    private void markAllParentPropertiesAsMandatory(XmlModel model) {
        model.getClassesAsList().forEach(modelClass ->
            modelClass.getPropertiesAsList().forEach(property -> {
                    if (property.isParent() && !modelClass.getName().equals(property.getType())) {
                        property.setMandatory(true);
                    }
                }
            ));
    }

    private void checkParentProperty(XmlModelClass modelClass) {
        if (!modelClass.isBaseClassMark()) {
            return;
        }

        List<XmlModelClassProperty> collectionWithTypeToItself = modelClass.getPropertiesAsList().stream().filter(property ->
                property.getCollectionType() != null
                    && property.getMappedBy() != null
                    && property.getType().equals(modelClass.getName()))
            .collect(Collectors.toList());

// no self-references or self-references with parent
        if (collectionWithTypeToItself.isEmpty() || collectionWithTypeToItself.stream().anyMatch(XmlModelClassProperty::isParent)) {
            return;
        }

        List<XmlModelClassProperty> xmlPropertiesFromMappedBy = collectionWithTypeToItself.stream()
            .map(property -> modelClass.getProperty(property.getMappedBy()))
            .collect(Collectors.toList());

        if (collectionWithTypeToItself.size() > 1) {

            List<XmlModelClassProperty> parentMarkedProperties = xmlPropertiesFromMappedBy.stream()
                .filter(XmlModelClassProperty::isParent).collect(Collectors.toList());

            if (parentMarkedProperties.isEmpty()) {
                throw new MarkedAsMappedByWithoutParentPropertyException(modelClass,
                    collectionWithTypeToItself, xmlPropertiesFromMappedBy);
            }
        }
    }

    private void createAggregateTree(XmlModel model) {
        model.getClassesAsList().stream()
            .filter(XmlModelClass::isUseAffinity)
            .forEach(modelClass -> {

                if (aggregateController.aggregateTreeContains(modelClass)) {
                    return;
                }

                AggregateTree aggregateTree = aggregateController.addAggregator(modelClass);
                checkCorrectFromRoot(aggregateTree, new HashSet<>());
            });
    }

    private void checkLinksBetweenAggregates(XmlModel model) {
        aggregateController.forEach(entry -> {

            XmlModelClass modelClass = entry.getKey();

            if (modelClass.isAbstract()) {
                collectEntitiesUsesAbstractClass(model, modelClass).forEach(entityClass -> {
                    List<XmlModelClass> superClasses = ModelHelper.getAllSuperClasses(entityClass);
                    Optional<XmlModelClass> firstNotAbstract = superClasses.stream()
                        .filter(modelClass1 -> !modelClass1.isAbstract())
                        .findFirst();

                    List<XmlModelClass> onlyFirstAbstract;
                    onlyFirstAbstract = firstNotAbstract
                        .map(xmlModelClass -> superClasses.subList(0, superClasses.indexOf(xmlModelClass)))
                        .orElse(superClasses);

                    List<XmlModelClassProperty> abstractProperties = onlyFirstAbstract.stream()
                        .flatMap(modelClass1 -> modelClass1.getPropertiesAsList().stream())
                        .collect(Collectors.toList());

                    XmlModelClass rootAggregate = aggregateController.getRootAggregate(entityClass);

                    validateAggregates(model, abstractProperties, rootAggregate);
                });
                return;
            }

            XmlModelClass rootAggregate = aggregateController.getRootAggregate(modelClass);

            validateAggregates(model, modelClass.getPropertiesAsList(), rootAggregate);
        });
    }

    private void validateAggregates(XmlModel model,
                                    Collection<XmlModelClassProperty> properties,
                                    XmlModelClass rootAggregate) {
        properties.forEach(property -> {
            XmlModelClass xmlModelClass = model.getClassNullable(property.getType());
            if (xmlModelClass == null) {
                return;

            }
            if (xmlModelClass.isEmbeddable()) {
                xmlModelClass.getPropertiesAsList().forEach(embeddedProperty -> {
                    XmlModelClass xmlModelClassInEmbedded = model.getClassNullable(embeddedProperty.getType());

                    if (xmlModelClassInEmbedded != null && xmlModelClassInEmbedded.isUseAffinity()
                        && !Objects.equals(rootAggregate, aggregateController.getRootAggregate(xmlModelClassInEmbedded))) {
                        throw new LinkBetweenAggregatesEmbeddableException(property, embeddedProperty, xmlModelClassInEmbedded);
                    }

                });
            } else if (xmlModelClass.isUseAffinity() && !xmlModelClass.isDictionary()
                && !Objects.equals(rootAggregate, aggregateController.getRootAggregate(xmlModelClass))) {
                throw new LinkBetweenAggregatesException(property);
            }
        });
    }

    private List<XmlModelClass> collectEntitiesUsesAbstractClass(XmlModel model,
                                                                 XmlModelClass abstractClass) {
        List<XmlModelClass> result = new ArrayList<>();

        model.getClassesAsList().stream().filter(modelClass ->
                abstractClass.getName().equals(modelClass.getExtendedClassName()))
            .forEach(modelClass -> {

                if (modelClass.isAbstract()) {
                    result.addAll(collectEntitiesUsesAbstractClass(model, modelClass));
                } else {
                    result.add(modelClass);
                }
            });

        return result;
    }

    private void addAggregateRootPropertyIntoClasses(XmlModel model) {
        model.getClassesAsList().stream().filter(XmlModelClass::isBaseClassMark).forEach(modelClass -> {
            if ((!modelClass.isDictionary() && isRootAggregate(modelClass)) || isRootDictionary(modelClass.getName())) {
                return;
            }

            String type = modelClass.isDictionary() ? ROOT_DICTIONARY_CLASS_NAME : aggregateController.getRootAggregate(modelClass).getName();
            //refactoring
            modelClass.addProperty(XmlModelClassProperty.Builder.create()
                .setName(JpaConstants.AGGREGATE_ROOT)
                .setType(type)
                .setChangeable(Changeable.READ_ONLY)
                .setIndex(Boolean.TRUE)
                .setLabel("link to the root aggregate")
                .build());
        });
    }

    private boolean isRootAggregate(XmlModelClass modelClass) {
        XmlModelClass aggregateAffinity = aggregateController.getRootAggregate(modelClass);

        // object does not support aggregation or it is necessary to ensure that the class is root without cycles
        return aggregateAffinity == null
            || (JpaConstants.OBJECT_ID.equals(aggregateAffinity.getAffinity()) && modelClass.equals(aggregateAffinity));
    }

    @Override
    public List<String> addInterfacesToJpaModel(XmlModelClass modelClass) {
        if (!isRootDictionary(modelClass.getName()) && modelClass.isDictionary()) {
            return Collections.singletonList(BaseAggregateEntity.class.getSimpleName()
                + String.join("", "<", ROOT_DICTIONARY_CLASS_NAME, ">"));
        }
        if (isRootAggregate(modelClass) || !modelClass.isBaseClassMark() || isRootDictionary(modelClass.getName())) {
            return Collections.emptyList();
        }

        XmlModelClass aggregateAffinity = aggregateController.getRootAggregate(modelClass);

        return Collections.singletonList(BaseAggregateEntity.class.getSimpleName()
            + "<" + aggregateAffinity.getName() + ">");
    }

    @Override
    public void checkDiffs(ModelParameters modelParameters, PdmModel pdmModel) {
        checkDropParent(modelParameters);
        checkParentDiff(modelParameters);

        List<XmlModelClassProperty> newProperties = modelParameters
            .getObjectByType(ElementState.NEW, XmlModelClassProperty.class);

        List<XmlModelClassProperty> newParentProperties = newProperties.stream()
            .filter(XmlModelClassProperty::isParent).collect(Collectors.toList());

        if (!newParentProperties.isEmpty()) {
            if (newParentProperties.stream().allMatch(property -> property.getType().equals(property.getModelClass().getName()))) {
                return;
            }

            List<XmlModelClass> newClasses = modelParameters.getObjectByType(ElementState.NEW, XmlModelClass.class);

            List<XmlModelClassProperty> prevExistedClass = newParentProperties.stream()
                .filter(property ->
                    !property.getType().equals(property.getModelClass().getName())
                        && newClasses.stream().noneMatch(modelClass ->
                        modelClass.getName().equals(property.getModelClass().getName())))
                .collect(Collectors.toList());

            if (!prevExistedClass.isEmpty() && !disableCompatibilityCheck) {
                throw new AddedParentPropertyToExistedClassException(prevExistedClass);
            }
        }
    }

    private void checkDropParent(ModelParameters modelParameters) {
        List<XmlModelClassProperty> objectByType = modelParameters.getObjectByType(ElementState.DEPRECATED, XmlModelClassProperty.class);

        List<XmlModelClassProperty> changedParent = objectByType.stream().filter(property ->
                property.propertyChanged(XmlModelClassProperty.PARENT_TAG)
                    && Boolean.TRUE == property.getOldValueChangedProperty(XmlModelClassProperty.PARENT_TAG))
            .collect(Collectors.toList());

        if (!changedParent.isEmpty()) {
            throw new RemovedParentClassException(changedParent);
        }
    }

    private void checkParentDiff(ModelParameters modelParameters) {
        List<XmlModelClassProperty> objectByType = modelParameters.getObjectByType(ElementState.UPDATED, XmlModelClassProperty.class);

        List<XmlModelClassProperty> changedParent = objectByType.stream().filter(property ->
                property.propertyChanged(XmlModelClassProperty.PARENT_TAG))
            .collect(Collectors.toList());

        List<XmlModelClassProperty> addedParent = new ArrayList<>();
        List<XmlModelClassProperty> droppedParent = new ArrayList<>();
        changedParent.forEach(property -> {
            if (property.isParent()) {
                addedParent.add(property);
            } else {
                droppedParent.add(property);
            }
        });

        if (!changedParent.isEmpty()) {
            throw new ParentDiffPropertiesException(addedParent, droppedParent);
        }
    }

    @Override
    public List<String> addClassAnnotations(XmlModelClass modelClass) {
        String s = generateAggregate(modelClass);
        if (s == null) {
            return Collections.emptyList();
        }

        return Collections.singletonList(s);
    }

    private String generateAggregate(XmlModelClass modelClass) {

        XmlModelClass aggregateAffinity = aggregateController.getAggregateParent(modelClass);

        if (modelClass.isDictionary()) {
            return AGGREGATE_ANNOTATION_TEMPLATE
                .replace(VALUE, ROOT_DICTIONARY_CLASS_NAME)
                .replace(MANAGED_CLASS, ROOT_DICTIONARY_CLASS_NAME)
                .replace(FROM, isRootDictionary(modelClass.getName()) ? JpaConstants.OBJECT_ID : JpaConstants.AGGREGATE_ROOT)
                .replace(AFFINITY, (JpaConstants.OBJECT_ID.equals(modelClass.getAffinity()) ? ", affinity = \"\"" : ""))
                .replace(AGGREGATE, (JpaConstants.OBJECT_ID.equals(modelClass.getAffinity()) ? ", aggregate = \"\"" : ""));
        }

        if (aggregateAffinity == null || !modelClass.isBaseClassMark()) {
            return null;
        }
        return AGGREGATE_ANNOTATION_TEMPLATE
            .replace(VALUE, aggregateAffinity.getName())
            .replace(MANAGED_CLASS, aggregateAffinity.getName())
            .replace(FROM, modelClass.getAffinity())
            .replace(AFFINITY, (JpaConstants.OBJECT_ID.equals(modelClass.getAffinity()) ? ", affinity = \"\"" : ""))
            .replace(AGGREGATE, (JpaConstants.OBJECT_ID.equals(modelClass.getAffinity()) ? ", aggregate = \"\"" : ""));
    }

    @Override
    public List<String> addImports(XmlModelClass modelClass) {
        return Arrays.asList(Aggregate.class.getName(),
            BaseAggregateEntity.class.getName());
    }

    private void checkAndCreateCorrectParentLinks(XmlModelClass modelClass) {
        List<XmlModelClassProperty> parentProperties = parentClassProperties(modelClass);

        String modelClassName = modelClass.getName();
        if (!parentProperties.isEmpty() && !modelClass.isBaseClassMark()) {
            throw new ParentNotOnBaseClassException(modelClassName, parentProperties);
        }

        if (parentProperties.size() > 2) {
            throw new TooMuchParentPropertiesException(modelClassName, parentProperties);
        }

        if (!modelClass.isBaseClassMark()) {
            return;
        }

        parentProperties.forEach(property -> {
            XmlModelClass xmlModelClass = modelClass.getModel().getClassNullable(property.getType());
            if (xmlModelClass == null) {
                throw new ParentPropertyOutOfModelException(property);
            }
        });

        if (parentProperties.size() == 2) {
            XmlModelClassProperty firstParentProperty = parentProperties.get(0);
            XmlModelClassProperty secondParentProperty = parentProperties.get(1);

            if (!(modelClassName.equals(firstParentProperty.getType())
                || modelClassName.equals(secondParentProperty.getType()))) {
                throw new TwoParentOnDifferentTypeException(modelClassName, firstParentProperty, secondParentProperty);
            }

            if (firstParentProperty.getType().equals(secondParentProperty.getType())) {
                throw new TwoParentOnOwnerClassException(modelClassName, firstParentProperty, secondParentProperty);
            }
        }

        List<XmlModelClassProperty> modelClassProperties = parentProperties.stream()
            .filter(property -> property.getCollectionType() != null).collect(Collectors.toList());

        if (!modelClassProperties.isEmpty()) {
            throw new ParentIsCollectionException(modelClassName, modelClassProperties);
        }
    }

    private List<XmlModelClassProperty> parentClassProperties(XmlModelClass modelClass) {
        return modelClass.getPropertiesAsList().stream()
            .filter(XmlModelClassProperty::isParent)
            .collect(Collectors.toList());
    }

    private void initAffinityFields(XmlModelClass modelClass) {
        if (modelClass.isEmbeddable()) {
            return;
        }

        String affinityEntityName;

        List<XmlModelClassProperty> parentProperties = parentClassProperties(modelClass);

        //each aggregate can be created once and then only read
        parentProperties.forEach(property -> property.setChangeable(Changeable.CREATE));

        if (isRootDictionary(modelClass.getName())) {
            affinityEntityName = JpaConstants.OBJECT_ID;
        } else if (modelClass.isDictionary()) {
            affinityEntityName = ROOT_DICTIONARY_CLASS_NAME;
        } else if (parentProperties.isEmpty()) {
            affinityEntityName = modelClass.isUseAffinity() ? JpaConstants.OBJECT_ID : null;
        } else if (parentProperties.size() == 1) {
            affinityEntityName = parentProperties.get(0).getName();
        } else {
            affinityEntityName = parentProperties.get(0).getType().equals(modelClass.getName())
                ? parentProperties.get(1).getName()
                : parentProperties.get(0).getName();
        }

        modelClass.setAffinity(affinityEntityName);
    }

    private void checkCorrectFromRoot(AggregateTree aggregateTree, Set<String> registeredClasses) {
        XmlModelClass modelClass = aggregateTree.getXmlModelClassValue();
        String className = modelClass.getName();

        if (!registeredClasses.add(className)) {
            throw new AggregateCircleException(registeredClasses);
        }

        modelClass.getModel().getClassesAsList().stream()
            .filter(modelClass1 -> !className.equals(modelClass1.getName()))
            .forEach(modelClass1 -> {
                Optional<XmlModelClassProperty> any = modelClass1.getPropertiesAsList().stream()
                    .filter(property ->
                        property.isParent() && property.getType().equals(className)).findAny();

                if (!any.isPresent()) {
                    return;
                }

                XmlModelClassProperty parentProperty = any.get();
                if (!modelClass1.isEvent() && modelClass.getPropertiesAsList().stream().noneMatch(property ->
                    parentProperty.getName().equals(property.getMappedBy()))) {
                    throw new NoMappedByPropertyToParentException(modelClass1.getName(), parentProperty);
                }

                if (modelClass.isAbstract()) {
                    throw new AbstractClassInControlException(parentProperty);
                }

                AggregateTree aggregateTree1 = aggregateController.addAggregator(modelClass1);

                aggregateTree.addSubAggregate(aggregateTree1);
                checkCorrectFromRoot(aggregateTree1, registeredClasses);
            });
    }

    public AggregateController getAggregateController() {
        return aggregateController;
    }
}

